const fs = require("fs");
const stationsMonitor = require("./stationsMonitor");
const AWS = require('aws-sdk');
AWS.config.loadFromPath(process.env.AWS_CFG_PATH);
const dynamoDB = new AWS.DynamoDB();
var Rx = require("rxjs");

const stationToRun = process.argv[2];

const fetchers = [];
fs.readdirSync("./station_modules").forEach(function(file, i){
	if(file.indexOf(".js") > 0){
		fetchers.push(require("./station_modules/" + file));
	}
});


Rx.Observable
	.from(fetchers)
	.mergeMap(fetcher => Rx.Observable
		.from(fetcher.stations)
		.map(station => ({fetcher, station}))
	)
	.filter(({station}) => !stationToRun || stationToRun == station.id)
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
		.filter(v => !!v)
	)
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
