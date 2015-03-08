var Q = require('q');
var http = require("http");
var tz = require("timezone");

module.exports = function(){
	this.getHTML = function(host, path, headers){
		headers = headers || {};

		var deferred = Q.defer();

		http.request({
			hostname: host,
			method: "GET",
			path: path,
			headers: headers
		}, function(res){
			if(res.statusCode >= 400)
				deferred.reject(res.statusCode);
			else{
				res.setEncoding("utf8");
				var body = "";
				res.on("data", function(chunk){
					body += chunk;
				});
				res.on("end", function(){
					deferred.resolve(body);
				})
			}
		}).on("error", function(err){
			deferred.reject(err);
		}).end();

		return deferred.promise;
	}
	this.postHTML = function(host, path, headers, body){
		headers = headers || {};
		parameters = parameters || "";

		var deferred = Q.defer();

		var req = http.request({
			hostname: host,
			method: "POST",
			path: path,
			headers: headers
		}, function(res){
			deferred.resolve(res);
		});
		req.write(postData);
		req.end();

		return deferred.promise;
	}

	// month 1-12
	this.getDate = function(year, month, day, hour, min, timezone){
		function to2Digit(n){
			if(n < 10)
				return "0" + n;
			return n;
		}

		var txt = year + "-" + to2Digit(month) + "-" + to2Digit(day) + " " + to2Digit(hour) + ":" + to2Digit(min);
		if(timezone){
			var _tz = tz(require("timezone/" + timezone));
			return new Date(_tz(txt, timezone));
		}else{
			return new Date(tz(txt));
		}
	}


	this.kmhToKnots = function(kmh){
		return parseFloat(kmh) / 1.852;
	}
	this.mpsToKnots = function(mps){
		return parseFloat(mps) * 1.94384449;
	}
	this.mphToKnots = function(mh){
		return parseFloat(mph) * 0.8689;
	}
	this.inhgTombar = function(inHG){
		return parseFloat(inHG) * 33.8639;
	}
	this.inhTomm = function(inches){
		return parseFloat(inches) * 25.4;
	}
	this.FtoC = function(f){
		return (parseFloat(f) - 32) * 5 / 9;
	}

	this.txtToDir = function(txt, i){
		function charToDir(c){
			if(c == 'N') return 0;
			if(c == 'E') return 90;
			if(c == 'S') return 180;
			if(c == 'W') return 270;
		}
		txt = txt.trim().replace(/O/g, "W");

		var val1 = charToDir(txt.charAt(i));
		if(txt.length - i == 1) return val1;
		var val2 = this.txtToDir(txt, i+1);

		if(val1 == 0 && val2 > 180) val1 = 360;

		return (val1+val2)/2;
	}

	this.nomMesEspToNum = function(mes){
		mes = mes.trim().toLowerCase();
		if(mes == "enero") return 1;
		if(mes == "febrero") return 2;
		if(mes == "marzo") return 3;
		if(mes == "abril") return 4;
		if(mes == "mayo") return 5;
		if(mes == "junio") return 6;
		if(mes == "julio") return 7;
		if(mes == "agosto") return 8;
		if(mes == "septiembre") return 9;
		if(mes == "octubre") return 10;
		if(mes == "noviembre") return 11;
		if(mes == "diciembre") return 12;
	}
	this.nomMesEngToNum = function(mes){
		mes = mes.trim().toLowerCase();
		if(mes == "january") return 1;
		if(mes == "february") return 2;
		if(mes == "march") return 3;
		if(mes == "april") return 4;
		if(mes == "may") return 5;
		if(mes == "june") return 6;
		if(mes == "july") return 7;
		if(mes == "august") return 8;
		if(mes == "september") return 9;
		if(mes == "october") return 10;
		if(mes == "november") return 11;
		if(mes == "december") return 12;
	}

	this.isNumber = function(str){
		return /^[-+]?[0-9]*\.?[0-9]+$/.test(str);
	}

	this.splitter = function(str){
		this.getToStrEx = function(find){
			var i = str.indexOf(find);
			if(i >= 0)
				str = str.substring(0, i);
			else
				str = "";
			return this;
		}
		this.getToStr = function(find){
			var i = str.indexOf(find);
			if(i >= 0)
				str = str.substring(0, i + find.length);
			else
				str = "";
			return this;
		}
		this.cropToStr = function(find){
			var i = str.indexOf(find);
			if(i >= 0)
				str = str.substring(i);
			else
				str = "";
			return this;
		}
		this.cropToStrEx = function(find){
			this.cropToStr(find);
			if(str.length < find.length)
				str = "";
			else
				str = str.substring(find.length);

			return this;
		}

		this.getString = function(){
			return str.trim();
		}
	}
}