// apply trim for string

if (typeof String.prototype.trim !== 'function') {

	String.prototype.trim = function() {
		return this.replace(/^\s+|\s+$/g, '');
	};
}

function check() {
	alert(' Hai ');

}

function CompareTwoDate_scriptdate_ddmmyyyy(str1, str2) {
	var data1 = str1.split("/");
	var data2 = str2.split("/");

	// alert( parseInt(data1[0]) + "-" + data1[1] + "-" + data1[2]);
	// alert( parseInt(data2[0]) + "-" + data2[1] + "-" + data2[2]);

	var dt1 = data1[0];
	var mon1 = data1[1];
	var yr1 = data1[2];
	var dt2 = data2[0];
	var mon2 = data2[1];
	var yr2 = data2[1];
	var date1 = new Date(yr1, mon1, dt1);
	var date2 = new Date(yr2, mon2, dt2);

	date1.setFullYear(yr1);
	date1.setMonth(mon1);
	date1.setDate(dt1);
	date2.setFullYear(yr2);
	date2.setMonth(mon2);
	date2.setDate(dt2);

	// alert(date1.getFullYear() + "-" + date1.getMonth() + "-" +
	// date1.getDate() );
	// alert(date2.getFullYear() + "-" + date2.getMonth() + "-" +
	// date2.getDate() );

	// var date1 = new Date( dt1+"/"+mon1+"/"+yr1);
	// var date2 = new Date( dt2+"/"+mon2+"/"+yr2);

	if (date1 < date2) {

		return false;
	} else {
		return true;
	}
}

// Used in employee subscription

function CompareTwoDatesddmmyyyy(str1, str2) {
	var data1 = str1.split("/");
	var data2 = str2.split("/");

	// alert( parseInt(data1[0]) + "-" + data1[1] + "-" + data1[2]);
	// alert( parseInt(data2[0]) + "-" + data2[1] + "-" + data2[2]);

	var dt1 = data1[0];
	var mon1 = data1[1] - 1;
	var yr1 = data1[2];
	var dt2 = data2[0];
	var mon2 = data2[1];
	var yr2 = data2[2];
	
	var date1 = new Date(yr1, mon1, dt1);
	var date2 = new Date(yr2, mon2, dt2);
	
	

	date1.setFullYear(yr1);
	date1.setMonth(mon1);
	date1.setDate(dt1);
	date2.setFullYear(yr2);
	date2.setMonth(mon2);
	date2.setDate(dt2);
	// alert(date1.getFullYear() + "-" + date1.getMonth() + "-" +
	// date1.getDate() );
	// alert(date2.getFullYear() + "-" + date2.getMonth() + "-" +
	// date2.getDate() );

	// var date1 = new Date( dt1+"/"+mon1+"/"+yr1);
	// var date2 = new Date( dt2+"/"+mon2+"/"+yr2);
alert(date1 + "  " + date2);
	if (date1 <= date2) {

		return true;
	} else {
		return false;

	}
	

}
function CheckDateEqualOrGreaterDatesddmmyyyy(str1, str2) {
	var data1 = str1.split("/");
	var data2 = str2.split("/");

	// alert( parseInt(data1[0]) + "-" + data1[1] + "-" + data1[2]);
	// alert( parseInt(data2[0]) + "-" + data2[1] + "-" + data2[2]);

	var dt1 = data1[0];
	var mon1 = data1[1];
	var yr1 = data1[2];
	var dt2 = data2[0];
	var mon2 = data2[1];
	var yr2 = data2[2];
	var date1 = new Date();
	var date2 = new Date();
	date1.setFullYear(yr1);
	date1.setMonth(mon1-1);
	date1.setDate(dt1);
	date2.setFullYear(yr2);
	date2.setMonth(mon2-1);
	date2.setDate(dt2);
	// alert(date1.getFullYear() + "-" + date1.getMonth() + "-" +
	// date1.getDate() );
	// alert(date2.getFullYear() + "-" + date2.getMonth() + "-" +
	// date2.getDate() );

	// var date1 = new Date( dt1+"/"+mon1+"/"+yr1);
	// var date2 = new Date( dt2+"/"+mon2+"/"+yr2);
	if (date1 < date2) {		
		return -1;
	} else if (date1 > date2) {
		return 1;
	} else {
		return 0;
	}
}


function CompareTwoDatesddmmyyyy(str1, str2) {
	var data1 = str1.split("/");
	var data2 = str2.split("/");

	// alert( parseInt(data1[0]) + "-" + data1[1] + "-" + data1[2]);
	// alert( parseInt(data2[0]) + "-" + data2[1] + "-" + data2[2]);

	var dt1 = data1[0];
	var mon1 = data1[1] - 1;
	var yr1 = data1[2];
	var dt2 = data2[0];
	var mon2 = data2[1];
	var yr2 = data2[2];
	
	var date1 = new Date(yr1, mon1, dt1);
	var date2 = new Date(yr2, mon2, dt2);
	
	

	date1.setFullYear(yr1);
	date1.setMonth(mon1);
	date1.setDate(dt1);
	date2.setFullYear(yr2);
	date2.setMonth(mon2);
	date2.setDate(dt2);
	// alert(date1.getFullYear() + "-" + date1.getMonth() + "-" +
	// date1.getDate() );
	// alert(date2.getFullYear() + "-" + date2.getMonth() + "-" +
	// date2.getDate() );

	// var date1 = new Date( dt1+"/"+mon1+"/"+yr1);
	// var date2 = new Date( dt2+"/"+mon2+"/"+yr2);
 
	if (date1 <= date2) {

		return true;
	} else {
		return false;

	}
}

function CompareTwoDatesddmmyyyy_1(str1, str2) {
	var data1 = str1.split("/");
	var data2 = str2.split("/");

	// alert( parseInt(data1[0]) + "-" + data1[1] + "-" + data1[2]);
	// alert( parseInt(data2[0]) + "-" + data2[1] + "-" + data2[2]);

	var dt1 = data1[0];
	var mon1 = data1[1] - 1;
	var yr1 = data1[2];
	var dt2 = data2[0];
	var mon2 = data2[1] - 1;
	var yr2 = data2[2];
	
	var date1 = new Date(yr1, mon1, dt1);
	var date2 = new Date(yr2, mon2, dt2);
	
	

	date1.setFullYear(yr1);
	date1.setMonth(mon1);
	date1.setDate(dt1);
	date2.setFullYear(yr2);
	date2.setMonth(mon2);
	date2.setDate(dt2);
	// alert(date1.getFullYear() + "-" + date1.getMonth() + "-" +
	// date1.getDate() );
	// alert(date2.getFullYear() + "-" + date2.getMonth() + "-" +
	// date2.getDate() );

	// var date1 = new Date( dt1+"/"+mon1+"/"+yr1);
	// var date2 = new Date( dt2+"/"+mon2+"/"+yr2);
 
	if (date1 <= date2) {

		return true;
	} else {
		return false;

	}
}
function CompareTwodiffDatesWithDayArg(str1, str2, days) {

	var data1 = str1.split("/");
	var data2 = str2.split("/");
	var dt1 = data1[0];
	var mon1 = data1[1] - 1;
	var yr1 = data1[2];
	var dt2 = data2[0];
	var mon2 = data2[1] - 1;
	var yr2 = data2[2];
	var date1 = new Date(yr1, mon1, dt1);
	var date2 = new Date(yr2, mon2, dt2);
	date2.setTime(date2.getTime() - (days * 24 * 60 * 60 * 1000));

	if (date1 < date2) {

		return true;
	} else {
		return false;

	}
}

function CompareTwodiffDates(str1, str2) {
	var data1 = str1.split("/");
	var data2 = str2.split("/");

	// alert(parseInt(data1[0]) + "-" + data1[1] + "-" + data1[2]);
	// alert(parseInt(data2[0]) + "-" + data2[1] + "-" + data2[2]);

	var dt1 = data1[0];
	var mon1 = data1[1] - 1;
	var yr1 = data1[2];
	var dt2 = data2[0];
	var mon2 = data2[1] - 1;
	var yr2 = data2[2];
	var date1 = new Date(yr1, mon1, dt1);
	var date2 = new Date(yr2, mon2, dt2);

	// date1.setFullYear(yr1);
	// date1.setMonth(mon1);
	// date1.setDate(dt1);
	// date2.setFullYear(yr2);
	// date2.setMonth(mon2);
	// date2.setDate(dt2);
	//
	// alert(date1.getFullYear() + "-" + date1.getMonth() + "-" +
	// date1.getDate());
	// alert(date2.getFullYear() + "-" + date2.getMonth() + "-" +
	// date2.getDate());

	// var date1 = new Date( dt1+"/"+mon1+"/"+yr1);
	// var date2 = new Date( dt2+"/"+mon2+"/"+yr2);
	//
	if (date1 < date2) {

		return true;
	} else {
		return false;

	}
}

function CompareTwoDatesyyyymmddddmmyyyy(str1, str2) {
	var data1 = str1.split("/");
	var data2 = str2.split("-");
	var dt1 = data1[0];
	var mon1 = data1[1] - 1;
	var yr1 = data1[2];
	var dt2 = data2[2];
	var mon2 = data2[1] - 1;
	var yr2 = data2[0];
	var date1 = new Date(yr1, mon1, dt1);
	var date2 = new Date(yr2, mon2, dt2);
	date1.setFullYear(yr1);
	date1.setMonth(mon1);
	date1.setDate(dt1);
	date2.setFullYear(yr2);
	date2.setMonth(mon2);
	date2.setDate(dt2);
	if (date1 < date2) {
		return false;
	} else {
		return true;
	}
}

function CompareFourDatesyyyymmddddmmyyyy(str1, str2, str3, str4) {
	var data1 = str1.split("/");
	var data2 = str2.split("/");
	var data3 = str3.split("-");
	var data4 = str4.split("-");
	var dt1 = data1[0];
	var mon1 = data1[1] - 1;
	var yr1 = data1[2];

	var dt2 = data2[0];
	var mon2 = data2[1] - 1;
	var yr2 = data2[2];

	var dt3 = data3[2];
	var mon3 = data3[1] - 1;
	var yr3 = data3[0];

	var dt4 = data4[2];
	var mon4 = data4[1] - 1;
	var yr4 = data4[0];
	var date1 = new Date(yr1, mon1, dt1);
	var date2 = new Date(yr2, mon2, dt2);
	var date3 = new Date(yr3, mon3, dt3);
	var date4 = new Date(yr4, mon4, dt4);
	date1.setFullYear(yr1);
	date1.setMonth(mon1);
	date1.setDate(dt1);
	date2.setFullYear(yr2);
	date2.setMonth(mon2);
	date2.setDate(dt2);
	date3.setFullYear(yr3);
	date3.setMonth(mon3);
	date3.setDate(dt3);
	date4.setFullYear(yr4);
	date4.setMonth(mon4);
	date4.setDate(dt4);
	if (date1 <= date4 && date2 >= date3) {
		return false;
	} else {
		return true;
	}
}

// compare to dates
function CompareTwoDatesLessThan(str1, str2) {
	var data1 = str1.split("-");
	var data2 = str2.split("-");

	// alert( parseInt(data1[0]) + "-" + data1[1] + "-" + data1[2]);
	// alert( parseInt(data2[0]) + "-" + data2[1] + "-" + data2[2]);

	var dt1 = data1[2];
	var mon1 = data1[1];
	var yr1 = data1[0];
	var dt2 = data2[2];
	var mon2 = data2[1];
	var yr2 = data2[0];
	var date1 = new Date(yr1, mon1, dt1);
	var date2 = new Date(yr2, mon2, dt2);

	date1.setFullYear(yr1);
	date1.setMonth(mon1);
	date1.setDate(dt1);
	date2.setFullYear(yr2);
	date2.setMonth(mon2);
	date2.setDate(dt2);

	// alert(date1.getFullYear() + "-" + date1.getMonth() + "-" +
	// date1.getDate() );
	// alert(date2.getFullYear() + "-" + date2.getMonth() + "-" +
	// date2.getDate() );

	// var date1 = new Date( dt1+"/"+mon1+"/"+yr1);
	// var date2 = new Date( dt2+"/"+mon2+"/"+yr2);

	if (date1 < date2) {

		return false;
	} else {
		return true;
	}
}

function CompareTwoDatesGreaterThanOrEqual(str1, str2) {
	// alert('ssss');
	var data1 = str1.split("-");
	var data2 = str2.split("-");
	var dt1 = data1[2];
	var mon1 = data1[1];
	var yr1 = data1[0];
	var dt2 = data2[2];
	var mon2 = data2[1];
	var yr2 = data2[0];
	var date1 = new Date(yr1, mon1, dt1);
	var date2 = new Date(yr2, mon2, dt2);

	date1.setFullYear(yr1);
	date1.setMonth(mon1);
	date1.setDate(dt1);
	date2.setFullYear(yr2);
	date2.setMonth(mon2);
	date2.setDate(dt2);

	if (date1 > date2 || date1 == date2) {

		return false;
	} else {
		return true;
	}
}
function dateCompareLessThanToday(value, errorMessage) {
	// alert('dd');

	jQuery.validator.addMethod("LessThanToday",

	function(value, element, params) {

		var toDay = new Date();
		var date2 = toDay.getFullYear() + "-" + (toDay.getMonth() + 1) + "-"
				+ toDay.getDate();
		return CompareTwoDatesLessThan(value, date2);

	}, errorMessage);
	// value="input[name=endDate]"
	$(value).rules('add', {
		LessThanToday : "today"
	});

}

function CompareDatesLessThan(value, param, errorMessage) {
	// alert(value);
	jQuery.validator.addMethod("greaterThan",

	function(value, element, params) {
		if ($(params).val() == "") {

			return true;
		} else {

			var date2 = $(params).val();
			return CompareTwoDatesLessThan(value, date2);

		}

	}, errorMessage);
	// value="input[name=endDate]"
	$(value).rules('add', {
		greaterThan : param
	});

}

function dateCompareGreaterThanToday(value, errorMessage) {

	jQuery.validator.addMethod("greaterThanToDay",

	function(value, element, params) {

		var toDay = new Date();
		var date2 = toDay.getFullYear() + "-" + (toDay.getMonth() + 1) + "-"
				+ toDay.getDate();
		return CompareTwoDatesGreaterThanOrEqual(value, date2);

	}, errorMessage);
	// value="input[name=endDate]"
	$(value).rules('add', {
		greaterThanToDay : "today"
	});

}

function validateDateMinus(value, param, errorMessage) {
	// alert(value);
	jQuery.validator.addMethod("dateMinus",

	function(value, element, params) {
		if ($(params).val() == "") {

			return true;
		} else {
			// alert("val " + value + " " + params);
			var date2 = params;
			return dateMinus(value, date2);

		}

	}, errorMessage);
	// value="input[name=endDate]"
	$(value).rules('add', {
		dateMinus : param
	});

}

function dateMinus(str1, str2) {
	// var str1=$(value1).val();
	// var str2=$(value2).val();
	// alert("Str1 " + str1 + ", str2 " + str2);
	var data1 = str1.split("-");
	var data2 = str2.split("-");
	var dt1 = parseInt(data1[2]);
	var mon1 = parseInt(data1[1]);
	var yr1 = parseInt(data1[0]);
	var dt2 = parseInt(data2[2]);
	var mon2 = parseInt(data2[1]);
	var yr2 = parseInt(data2[0]);
	var dt = dt1 - dt2;
	var mon = mon1 = mon2;
	var yr = yr1 - yr2;
	//

	if (yr < -15) {
		// alert(yr);
		return true;
	}

	else
		return false;
}

function CompareDatesLessThan1(value, param, errorMessage) {
	// alert(value);
	jQuery.validator.addMethod("greaterThan",

	function(value, element, params) {
		if ($(params).val() == "") {

			return true;
		} else {

			var date2 = $(params).val();
			return CompareTwoDatesLessThan(value, date2);

		}

	}, errorMessage);
	// value="input[name=endDate]"
	$(value).rules('add', {
		greaterThan : param
	});

}

function isNumber(value) {

	var str = value;
	var n = str.match(/^([-+]?[0-9]+(\.[0-9]+)?)?$/);

	if (n == null) {
		return false;
	} else {
		return true;
	}

}

/*
 * number is float and returns string
 * 
 * This function round a number to two decimal places
 * 
 * for example 3.099 = 3.10
 */
function toFixed(number, decimals) {

	if (typeof (number) != "number") {
		if (typeof (number) == "string") {
			number = number.trim();
		}
		number = parseFloat(number);

	}

	var value = "";

	try {
		value = "" + number.toFixed(2);
		var afterDecimal = value.substr(value.indexOf(".") + 1, 2);
		var beforeDecimal = value.substr(0, value.indexOf("."));
		value = beforeDecimal + "." + afterDecimal;
		// value=value.indexOf(".");

	} catch (err) {
		alert("Error occured while rounding number " + number + " Error:"
				+ err.message)
	}
	return value;

}

function less_than(str1, str2) {
	var data1 = str1.split("/");
	var data2 = str2.split("/");

	// alert(parseInt(data1[0]) + "-" + data1[1] + "-" + data1[2]);
	// alert(parseInt(data2[0]) + "-" + data2[1] + "-" + data2[2]);

	var dt1 = data1[0];
	var mon1 = data1[1] - 1;
	var yr1 = data1[2];
	var dt2 = data2[0];
	var mon2 = data2[1] - 1;
	var yr2 = data2[2];
	var date1 = new Date(yr1, mon1, dt1);
	var date2 = new Date(yr2, mon2, dt2);

	// date1.setFullYear(yr1);
	// date1.setMonth(mon1);
	// date1.setDate(dt1);
	// date2.setFullYear(yr2);
	// date2.setMonth(mon2);
	// date2.setDate(dt2);
	//
	// alert(date1.getFullYear() + "-" + date1.getMonth() + "-" +
	// date1.getDate());
	// alert(date2.getFullYear() + "-" + date2.getMonth() + "-" +
	// date2.getDate());

	// var date1 = new Date( dt1+"/"+mon1+"/"+yr1);
	// var date2 = new Date( dt2+"/"+mon2+"/"+yr2);
	//
	if (date1 < date2) {

		return true;
	} else {
		return false;

	}
}

function less_thanEqual(str1, str2) {
	var data1 = str1.split("/");
	var data2 = str2.split("/");

	// alert(parseInt(data1[0]) + "-" + data1[1] + "-" + data1[2]);
	// alert(parseInt(data2[0]) + "-" + data2[1] + "-" + data2[2]);

	var dt1 = data1[0];
	var mon1 = data1[1] - 1;
	var yr1 = data1[2];
	var dt2 = data2[0];
	var mon2 = data2[1] - 1;
	var yr2 = data2[2];
	var date1 = new Date(yr1, mon1, dt1);
	var date2 = new Date(yr2, mon2, dt2);

	// date1.setFullYear(yr1);
	// date1.setMonth(mon1);
	// date1.setDate(dt1);
	// date2.setFullYear(yr2);
	// date2.setMonth(mon2);
	// date2.setDate(dt2);
	//
	// alert(date1.getFullYear() + "-" + date1.getMonth() + "-" +
	// date1.getDate());
	// alert(date2.getFullYear() + "-" + date2.getMonth() + "-" +
	// date2.getDate());

	// var date1 = new Date( dt1+"/"+mon1+"/"+yr1);
	// var date2 = new Date( dt2+"/"+mon2+"/"+yr2);
	//
	if (date1 <= date2) {

		return true;
	} else {
		return false;

	}
}