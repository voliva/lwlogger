var csvReader = require('csvreader');
var csvWriter = require("csv-write-stream");
var fs = require("fs");

var FILE = "stationsMonitor.csv"
var N_FAIL = 3;

var table = {}; // Codi => result

try {
  csvReader.read(
    FILE,
    function(data){
			var id = data[0];
			var sdata = JSON.parse(data[1]);
			var repetitions = parseInt(data[2]);
			var fails = data[3] == "true";
			var missingLecture = data[4] == "true";

			table[id] = {
				data: sdata,
				repetitions: repetitions,
				fails: fails,
				missingLecture: missingLecture
			}
    },
    {
      parseOptions: {
        delimiter: "\t"
      }
    }
  );
}catch(ex){

}

module.exports = {
	check: function(id, data){
		// If there's no data, then the station is not working
		if(!data){
			if(table[id]){
				table[id].fails = true;
				table[id].missingLecture = true;
			}
			return false;
		}

		data = data.toObject();
		data.dateTime = data.dateTime.toISOString();

		// If we don't have it registered, then we just register it and assume it's working
		if(!table[id]){
			table[id] = {
				data: data,
				repetitions: 0,
				fails: false,
				missingLecture: false
			}
			return 1;
		}

		// If the time hasn't changed, then we are getting the same data: Do not write.
		if(data.dateTime == table[id].data.dateTime){
			table[id].missingLecture = true;
			return false;
		}else{
			table[id].missingLecture = false;
		}

		/* If wind/gust/dir haven't changed, and it's not calma, then we have a repetition. Do not write */
		if(Math.abs(data.wind - table[id].data.wind) < 0.01 && data.wind >= 1 &&
			Math.abs(data.gust - table[id].data.gust) < 0.01 &&
			Math.abs(data.dir - table[id].data.dir) < 0.01)
		{
			table[id].repetitions++;
			table[id].data = data;
			// If we have many repetitions, then it's a fail

			if(table[id].repetitions > N_FAIL){
				table[id].repetitions = N_FAIL;
				table[id].fails = true;
			}

			return false;
		}else{
			/*console.log(Math.abs(data.wind - table[id].data.wind), data.wind >= 1,
				Math.abs(data.gust - table[id].data.gust),
				Math.abs(data.dir - table[id].data.dir));*/

			var nRepeats = table[id].fails ? 0 : table[id].repetitions;
			table[id].repetitions = 0;
			table[id].fails = false;
			table[id].data = data;

			return nRepeats ? nRepeats + 1 : 1;
		}
	},
	save: function(){
		var writer = csvWriter({
			headers: ["id", "data", "repetitions", "fails", "missing"],
			sendHeaders: false,
			separator: "\t"
		});
		writer.pipe(fs.createWriteStream(FILE));
		for(var id in table){
			writer.write([
				id,
				JSON.stringify(table[id].data),
				table[id].repetitions,
				table[id].fails,
				table[id].missingLecture
			]);
		}
		writer.end();
	}
}
