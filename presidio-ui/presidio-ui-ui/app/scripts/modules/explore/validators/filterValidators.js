(function () {
    'use strict';

    angular.module("FilterValidatorsModule", []).factory("FilterValidators", ["utils", function (utils) {

        function NumberRangeValidator (validationObj) {
            this.validationObj = validationObj;

            this.validate = function () {

                new RequiredValidator(this.validationObj.fromValue).validate();
                new RequiredValidator(this.validationObj.toValue).validate();

                var toValueAsNumber = parseFloat(this.validationObj.toValue);
                var fromValueAsNumber = parseFloat(this.validationObj.fromValue);

                if (toValueAsNumber < fromValueAsNumber) {
                    throw new Error("Low value greater than the high value");
                }
                if (this.validationObj.min && fromValueAsNumber < this.validationObj.min) {
                    throw new Error("Minimum value allowed is " + this.validationObj.min);
                }
                if (this.validationObj.max && toValueAsNumber > this.validationObj.max) {
                    throw new Error("Minimum value allowed is " + this.validationObj.max);
                }
            };
        }

        function RequiredValidator (val) {
            this.val = val;
            this.validate = function () {
                if (this.isEmpty(this.val)) {
                    throw new Error("The value is required");
                }
            };

            this.isEmpty = function (val) {
                return val === undefined || val === null || val === "";
            };
        }

        function DateValidator (dateVal) {
            this.dateVal = dateVal;
            this.validate = function () {
                // momentjs returns a valid object if dateVal is just a literal object! Stupid!
                if (angular.isObject(this.dateVal) && this.dateVal.timeStart && this.dateVal.timeEnd) {
                    if (!utils.date.getMoment(this.dateVal.timeStart).isValid() ||
                        !utils.date.getMoment(this.dateVal.timeEnd).isValid() ||
                        utils.date.getDatesSpan(this.dateVal.timeStart, this.dateVal.timeEnd).length !== 0) {
                        throw new Error("Not a valid date");
                    }
                }
                if (!utils.date.getMoment(this.dateVal).isValid()) {
                    throw new Error("Not a valid date");
                }
            };
        }

        function DateRangeValidator (dateVal) {
            this.dateVal = dateVal;

            this.validate = function () {
                if (angular.isObject(this.dateVal)) {
                    if (!this.dateVal.timeStart || !this.dateVal.timeEnd) {
                        throw new ReferenceError("Time object must have timeStart and timeEnd");
                    }
                    if (!utils.date.getMoment(this.dateVal.timeStart).isValid() ||
                        !utils.date.getMoment(this.dateVal.timeEnd).isValid()) {
                        throw new RangeError("Not a valid date range");
                    }

                    return;
                }

                // dateVal is not an object
                if (!utils.date.getMoment(this.dateVal).isValid()) {
                    throw new RangeError("Not a valid date range");
                }
            };
        }

        function IsArrayValidator (val) {
            this.val = val;
            this.validate = function () {
                if (!angular.isArray(this.val)) {
                    throw new Error("The value must be array");
                }
            };
        }

        function IsDurationValidator (val) {
            this.val = val;
            this.validate = function () {
                //regexp pattern for duration in the format hh:mm or hh:mm:ss
                var pattern = /^(([01]\d)|(2[0-3]))(:[0-5]\d){1,2}$/;
                if (!pattern.test(val)) {
                    throw new Error("The value is not hh:mm:ss format");
                }
            };
        }

        var validators = {
            "numberRangeValidator": NumberRangeValidator,
            "dateRangeValidator": DateRangeValidator,
            "requiredValidator": RequiredValidator,
            "dateValidator": DateValidator,
            "isArrayValidator": IsArrayValidator,
            "isDurationValidator": IsDurationValidator
        };


        return {
            getValidator: function (validatorName, validatorValue) {
                var Validator = validators[validatorName];
                return new Validator(validatorValue);
            }
        };
    }]);
}());
