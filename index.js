const fs = require("fs");
const stationsMonitor = require("./stationsMonitor");
const AWS = require('aws-sdk');
AWS.config.loadFromPath(process.env.AWS_CFG_PATH || '/home/victor/development/lwlogger/credentials.json');
const dynamoDB = new AWS.DynamoDB();
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
		.map(p => station.post ? station.post(p) : p)
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
		data.temperature || null,
		data.humidity || null,
		data.pressure || null,
		data.wind || null,
		data.gust || null,
		data.direction || null,
		data.rain || null
	];
console.log(queryData);
	const runQueries = async () => {
		const connection = await mySQLconnection;
		await connection.execute(insertQuery('INSERT','weatherData'), queryData);
		await connection.execute(
			insertQuery('REPLACE', 'lastWeatherData'),
			queryData
		);
	}

	return Rx.Observable.fromPromise(runQueries());	
}).subscribe(() => {}, () => {}, async () => {
	const connection = await mySQLconnection;
	connection.close();
});

// AWS
dataStream
	.bufferCount(25)
	.mergeMap(resArr => {
		console.log(`sending a ${resArr.length}-batch to AWS`);

		const obj = {
			RequestItems: {
				'livewind-data': resArr.map(d => ({
					PutRequest: createPutRequest(d.stationId, d.data)
				}))
			}
		};

		return Rx.Observable.create(obs => {
			dynamoDB.batchWriteItem(obj, (err, result) => {
				if(err) {
					console.log(err, result);
				}
				if(result && Object.keys(result.UnprocessedItems).length) {
					console.log('UnprocessedItems', result.UnprocessedItems);
				}
				obs.next();
				obs.complete();
			});
		});
	})
	.subscribe(_ => {}, err => console.log(err), _ => stationsMonitor.save());

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
