var mongoose     = require('mongoose');
var ObjectId     = mongoose.Types.ObjectId;
var Schema       = mongoose.Schema;

// Look at http://mongoosejs.com/docs/schematypes.html

/* Recommended indexes:
	Compound: {"station": 1, "dateTime": 1} unique

	Because many times we need to get the data of one station, ordered by date.
	And unique because we won't have more than one data for one station at the same time.
*/
var DataSchema   = new Schema({
	station: {
		type: Schema.Types.ObjectId,
		ref: 'Station'
	},
	dateTime: Date,
	temp: Number,
	hidro: Number,
	pressure: Number,
	wind: Number,
	gust: Number,
	dir: Number,
	rain: Number
},{
	versionKey: false // See http://aaronheckmann.tumblr.com/post/48943525537/mongoose-v3-part-1-versioning
});

// Public stuff
DataSchema.set("toJSON", {
	transform: function(doc, ret, options){
		var timestamp = Math.floor(ret.dateTime.getTime() / 1000);
		ret.timeUtc = timestamp;

		delete ret._id;
		delete ret.station;
		delete ret.dateTime;
		return ret;
	}
});

// Custom methods
/* fields and options are optional. Added to support Mongoose API */
DataSchema.statics.findByStationAndDates = function(stationId, start, end, fields, options, callback){
	if(options == undefined)
		options = {};

	options.sort = {'dateTime': 1};

	this.find({
		station: new ObjectId(stationId),
		dateTime: {
			"$gte": start,
			"$lt": end
		}
	}, fields, options, callback);
}

DataSchema.statics.findLastByStationAndDates = function(stationId, start, end, fields, options, callback){
	if(options == undefined)
		options = {};

	// Descending, because we want the first row to be the last record.
	options.sort = {'dateTime': -1};

	this.findOne({
		station: new ObjectId(stationId),
		dateTime: {
			"$gte": start,
			"$lt": end
		}
	}, fields, options, callback);
}

/* Find all the last datas for each station in a date range */
DataSchema.statics.findLastsByDates = function(start, end, callback){
	var group = {
		key: { station: 1 }, // Group by station
		cond: { // Between these dates
			dateTime: {
				"$gte": start,
				"$lt": end
			}
		},
		initial: {}, // initial result
		reduce: function(cur, result){
			if(result.dateTime == undefined || result.dateTime < cur.dateTime){
				for (var name in cur) {
					if (cur.hasOwnProperty(name)) {
						result[name] = cur[name];
					}
				}
			}
		}
	}

	this.collection.group(group.key, group.cond, group.initial, group.reduce, null, true, callback);
}

DataSchema.statics.findByDates = function(start, end, fields, options, callback){
	if(options == undefined)
		options = {};

	options.sort = {'dateTime': 1};

	this.find({
		dateTime: {
			"$gte": start,
			"$lt": end
		}
	}, fields, options, callback);
}



module.exports = mongoose.model('Data', DataSchema);
