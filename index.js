const fs = require("fs");
const stationsMonitor = require("./stationsMonitor");
const Rx = require("rxjs");
const mysql2 = require('mysql2/promise');

const mySQLconnection = mysql2.createConnection({
	host: process.env.SQL_DB_HOST,
	user: process.env.SQL_DB_USER,
	password: process.env.SQL_DB_PSW,
	database: process.env.SQL_DB_NAME
});

const stationToRun = process.argv[2];

const fetchers = [];
fs.readdirSync("./station_modules").forEach(function(file, i){
	// if(i != 0) return;
	if(file.indexOf(".js") > 0){
		fetchers.push(require("./station_modules/" + file));
	}
});

/** Counter */
let finished = 0;
let total = 0;
const dataStream = Rx.Observable
	.from(fetchers)
	.mergeMap(fetcher => Rx.Observable
		.from(fetcher.stations)
		.map(station => ({fetcher, station}))
	)
	.filter(({station}) => !stationToRun || stationToRun == station.id)
	.do(_ => total++)
	.mergeMap(({fetcher, station}) => fetcher
		.fetch(station.arg)
		.map(data => (data && station.post) ? station.post(data) : data)
		.map(data => {
			if(stationToRun) {
				console.log(data);
				return null;
			}
			const checkResult = stationsMonitor.check(station.id, data);
			switch(checkResult) {
				case stationsMonitor.NO_CHANGE:
					console.log(`station ${station.id} hasn't updated yet`);
					return null;
				case stationsMonitor.NO_DATA:
					console.log(`station ${station.id} returned no data`);
					return null;
				case stationsMonitor.REPEAT:
					console.log(`station ${station.id} repeated the same wind`);
					return null;
				case stationsMonitor.OK:
					return {
						stationId: station.id,
						data
					};
				default:
					console.log(`station ${station.id} unkown check result: ${checkResult}`);
					return null;
			}
		})
		.catch(err => {
			console.log(`station ${station.id} raised an exception`, err);
			return Rx.Observable.of(null);
		})
		.do(_ => {
			finished++;
			// console.log(`${finished}/${total}`);
		})
		.filter(v => !!v)
		.filter(v => v.data.temp ||
			v.data.temp ||
			v.data.hidro ||
			v.data.pressure ||
			v.data.wind ||
			v.data.gust ||
			v.data.dir ||
			v.data.rain)
	)
	.publish();

/** MySQL **/
const maybe = v => typeof v === 'undefined' ? null : v;
const timestampLimit = Math.floor(new Date().getTime() / 1000 - 60*60); // Delete all records older than an hour
dataStream.mergeMap((res) => {
	const data = {
		stationId: res.stationId,
		timestamp: Math.floor(res.data.dateTime.getTime()/1000),
		temperature: res.data.temp,
		humidity: res.data.hidro,
		pressure: res.data.pressure,
		wind: res.data.wind,
		gust: res.data.gust,
		direction: res.data.dir,
		rain: res.data.rain
	};
	const insertQuery = (op, table) => `${op} INTO
		${table} (stationId, timestamp, temperature, humidity, pressure, wind, gust, direction, rain)
		VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)`;
	const queryData = [
		data.stationId,
		data.timestamp,
		maybe(data.temperature),
		maybe(data.humidity),
		maybe(data.pressure),
		maybe(data.wind),
		maybe(data.gust),
		maybe(data.direction),
		maybe(data.rain)
	];
	const runQueries = async () => {
		const connection = await mySQLconnection;
		await connection.execute(insertQuery('INSERT','weatherData'), queryData);
		if(data.timestamp >= timestampLimit) {
			await connection.execute(
				insertQuery('REPLACE', 'lastWeatherData'),
				queryData
			);
		}
	}

	return Rx.Observable.fromPromise(runQueries());	
}).subscribe(() => {}, () => {}, async () => {
	const connection = await mySQLconnection;
	await connection.execute('DELETE FROM lastWeatherData WHERE timestamp < ' + timestampLimit);
	connection.close();
});

dataStream.connect();

function formatNumericValue(n) {
	if(n == null) {
		return {
			NULL: true
		}
	}
	
	return {
		N: n.toString().replace(',', '.')
	}
}

function createPutRequest(stationId, data) {
	const tsday = Math.floor(data.dateTime.getTime() / (1000 * 60 * 60 * 24))
	const Item = {
		'stationId': {
			S: `${stationId}-${tsday}`
		},
		timestamp: {
			N: `${Math.floor(data.dateTime.getTime()/1000)}`
		}
	}

	Item.temperature = formatNumericValue(data.temp);
	Item.humidity = formatNumericValue(data.hidro);
	Item.pressure = formatNumericValue(data.pressure);
	Item.wind = formatNumericValue(data.wind);
	Item.gust = formatNumericValue(data.gust);
	Item.direction = formatNumericValue(data.dir);
	Item.rain = formatNumericValue(data.rain);

	return {
		Item
	}
}

// Crazy fast idea: Locate+track where people go sailing in order to discover new places.
