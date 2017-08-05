if (typeof String.prototype.trim !== 'function') {

	String.prototype.trim = function() {
		return this.replace(/^\s+|\s+$/g, '');
	};
}

var validFlag = true;

function validateElement(e) {
	validFlag = true;

	try {
		if($(this).hasClass("validate"))
		{	
		if (!(e.data.errorMessage == undefined))
			;
		{

			var bool = e.data.compareElement == undefined;
			if (bool == false && !(e.data.method == undefined)) {

				validateMe_Double($(this), e.data.method,
						$(e.data.compareElement), e.data.errorMessage);
			} else if (!(e.data.regExpr == undefined)) {

				validateMe_RegExpr($(this), e.data.regExpr, e.data.errorMessage);

			}

			else if (!(e.data.method == undefined)) {

				validateMe_Single($(this), e.data.method, e.data.errorMessage);
			}

		}

		classValidate($(this));
		// var element=$("#" + imageID);
		
		}

	} catch (e) {
		alert(e);
	}
}

function classValidate(me) {
	try {

		var errorMessage = "";
		

		if (validFlag && $(me).hasClass("required")) {
			// This errorMessageElement should be defined as an element in source page
			var errorMessageElement = $(me).attr("id") + "_required_errorMessage";
			errorMessage=" This field is required.";
			if (!($("#" + errorMessageElement).val() == undefined)) {
				errorMessage = $("#" + errorMessageElement).val();
			}
			validateMe_Single($(me), required, errorMessage);
		}
	} catch (e) {
		alert(e);
	}
}

function validateMe_Double(me, fn, element2, msg) {

	setErrorStatus(fn(me, element2) , $(me), msg);
}

function validateMe_RegExpr(me, regExpr, msg) {
	if ($(me).val() != "") {
		setErrorStatus(match($(me).val(), regExpr), $(me), msg);
 	}
}

function validateMe_Single(me, fn, msg) {
		setErrorStatus(fn(me), $(me), msg);
}
function equals(element1, element2) {
	var value1 = $(element1).val();
	var value2 = $(element2).val();
	if (value1 == value2)
		return true;
	else
		return false;
}

function required(element) {

	if ($(element).val().trim() == "") {
		return false;
	} else {
		return true;
	}
}

function match(value, param) {

	// return value.match("^\+?\d?\d?\d{10}$");

	try {
		var s = value.match(param);
		if (s.length > 0)
			return true;
		else
			return false;
	} catch (e) {

		return false;
	}

}

function setErrorStatus(valid, me, msg) {
	setErrorMessage(valid, me, msg);
	validFlag = valid;
	if (valid) {
		$(me).addClass("valid");
		$(me).removeClass("error");
	} else {
		$(me).addClass("error");
		$(me).removeClass("valid");
	}
}

function setErrorMessage(valid, me, msg) {
	try {
		var imageID = $(me).attr("id") + "_errorImage";

		if ($("#" + imageID).is($("img"))) {
			if (!valid) {

				$("#" + imageID).attr("title", msg);
				$("#" + imageID).show();

			} else {
				$("#" + imageID).hide();
				$("#" + imageID).attr("title", "");
			}
		} else if ($("#" + imageID).is($("td"))
				|| $("#" + imageID).is($("div"))) {
			if (!valid) {

				$("#" + imageID).html(msg);
				// $("#" + imageID).show();

			} else {
				// $("#" + imageID).hide();
				$("#" + imageID).html("");
			}

		}

	} catch (e) {
		alert(e);
	}

}
