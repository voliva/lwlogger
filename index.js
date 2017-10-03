const fs = require("fs");
const stationsMonitor = require("./stationsMonitor");
const mkdirp = require("mkdirp");
const esTz = require("timezone")(require("timezone/Europe/Madrid"));
const AWS = require('aws-sdk');
AWS.config.loadFromPath(process.env.AWS_CFG_PATH);
const dynamoDB = new AWS.DynamoDB();

const stationToRun = process.argv[2];

const fetchers = [];
fs.readdirSync("./station_modules").forEach(function(file, i){
	if(file.indexOf(".js") > 0){
		fetchers.push(require("./station_modules/" + file));
	}
});

const promises = [];
const dataToSave = [];

fetchers.forEach(function(fetcher){
	fetcher.stations.forEach(function(station){
		if(stationToRun && station.id != stationToRun) return;

		var p = fetcher
			.fetch(station.arg)
			.catch(function(err){
				console.log(station.id, err);
				console.log(err.stack);
				return null;
			});
		if(station.post){
			p = station.post(p);
		}
		p = p.then(function(data){
			if(stationToRun){
				console.log(data);
				return;
			}
			try {
				var res = stationsMonitor.check(station.id, data);
				if(!res) {
					return;
				}

				dataToSave.push({
					stationId: station.id,
					data
				})
			}catch(ex){
				console.log(station.id, err, data);
				console.log(err.stack);
			}
		});
		promises.push(p);
	});
});

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

function finish() {
	try {
		stationsMonitor.save();

		const dataToSaveBatches = [];
		while(dataToSave.length > 24) {
			dataToSaveBatches.push(dataToSave.splice(0, 24));
		}
		dataToSaveBatches.push(dataToSave);

		let awsPromise = Promise.resolve();
		dataToSaveBatches.forEach((batch) => {
			awsPromise = awsPromise.then(() => new Promise((resolve, reject) => {
				const obj = {
					RequestItems: {
						'livewind-data': batch.map(d => ({
							PutRequest: createPutRequest(d.stationId, d.data)
						}))
					}
				};
		
				const res = dynamoDB.batchWriteItem(obj, (err, result) => {
					if(err) {
						console.log(err, result);
					}
					if(result && Object.keys(result.UnprocessedItems).length) {
						console.log('UnprocessedItems', result.UnprocessedItems);
					}
					resolve();
				});
			}));
		});
	}catch (ex){ 
		console.log(ex);
	}
}

if(!stationToRun){
	Promise.all(
		promises
	).then(finish, finish);
}

// Crazy fast idea: Locate+track where people go sailing in order to discover new places.
