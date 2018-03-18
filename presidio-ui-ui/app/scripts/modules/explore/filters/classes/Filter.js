(function () {
    'use strict';

    function FilterClass (DataEntityField, QueryOperator, search, FilterValidators) {
        /**
         * Constructor for Filter objects, used in the Explore page
         * @param field
         * @constructor
         */
        function Filter (field) {
            if (!(field instanceof DataEntityField)) {
                throw new TypeError("Invalid field for filter, expected an instance of DataEntityFilter.");
            }

            this.field = field;
            this.valueField = null;
            this.operator = field.type.defaultOperator;
            this.isDirty = true;
            this._value = this.defaultValue;
            this.validObj = {};
            this.initValidState();
            this.valueList = field.valueList;
        }

        Filter.prototype.copy = function (filter) {
            if (!(filter instanceof Filter)) {
                throw new TypeError("Can't copy Filter, expected an instance of Filter.");
            }

            this.valueField = filter.valueField;
            this._operator = filter._operator;
            this._value = filter.value;
            this._displayValue = filter.displayValue;
            this.isDirty = filter.isDirty;
            this.validObj = filter.validObj;

            filter.validate(this._value);

            return this;
        };

        Filter.prototype.initValidState = function () {
            if (this.validObj) {
                this.validObj.errorMessage = "";
                this.validObj.showErrors = false;
                this.validObj.isValid = true;

                // Set isValidOnInit:
                // If it requires value, and '_value" and '_displayValue' are not set, set it to false
                // Else set it to true
                if (this.operator.requiresValue) {
                    this.validObj.isValidOnInit =
                        (angular.isDefined(this._value) || angular.isDefined(this._displayValue));
                }
                else {
                    this.validObj.isValidOnInit = true;
                }
            }
        };

        Filter.copy = function (filter) {
            var newFilter = new Filter(filter.field);
            newFilter.copy(filter);
            return newFilter;
        };

        /**
         * Compares this filter to another one. Returns true if they are equal or false if they're not.
         * @param anotherFilter
         */
        Filter.prototype.equals = function (anotherFilter) {
            if (!anotherFilter) {
                return false;
            }

            if (!(anotherFilter instanceof Filter)) {
                return false;
            }

            if (anotherFilter.operator.id !== this.operator.id) {
                return false;
            }

            if (typeof this.value !== typeof anotherFilter.value) {
                return false;
            }

            if (typeof this.displayValue !== typeof anotherFilter.displayValue) {
                return false;
            }

            if ((!!this.valueField).toString() !== (!!anotherFilter.valueField).toString()) {
                return false;
            }

            if (this.valueField && anotherFilter.valueField) {
                if (this.valueField.id !== anotherFilter.valueField.id ||
                    this.valueField.entity.id !== anotherFilter.valueField.entity.id) {
                    return false;
                }
            }

            if (this.field.id !== anotherFilter.field.id) {
                return false;
            }

            if (!angular.isObject(this.value)) {
                if (this.value !== anotherFilter.value) {
                    return false;
                }
            }
            else {
                if (JSON.stringify(this.value) !== JSON.stringify(anotherFilter.value)) {
                    return false;
                }
            }
            if (!angular.isObject(this.displayValue)) {
                if (this.displayValue !== anotherFilter.displayValue) {
                    return false;
                }
            }
            else {
                if (JSON.stringify(this.displayValue) !== JSON.stringify(anotherFilter.displayValue)) {
                    return false;
                }
            }

            return true;
        };

        Filter.prototype.__defineGetter__("operator", function () {
            return this._operator;
        });

        Filter.prototype.__defineSetter__("operator", function (operator) {
            if (!(operator instanceof QueryOperator)) {
                throw new TypeError("Invalid operator, expected an instance of QueryOperator.");
            }

            this._operator = operator;

            if (this._value === undefined) {
                this._value = this.defaultValue;
            } else {
                try {
                    this.validate(this._value);
                }
                catch (e) {
                    this._value = this.defaultValue;
                }
            }

            this.initValidState();
            this.isDirty = true;
        });

        Filter.prototype.validate = function (value, isDisplay) {

            var validatorsArr = getValidators.call(this, isDisplay) || [];
            if (this._operator.requiresValue) {
                validatorsArr.unshift("requiredValidator");
            }

            if (validatorsArr.length === 0) {
                return this;
            }

            for (var i = 0; i < validatorsArr.length; i++) {
                var validator = FilterValidators.getValidator(validatorsArr[i], value); //also sets "value" as a member
                                                                                        // of Validator.
                validator.validate();
            }
            return this;
        };

        //there two sets of validators - one for display values and one for actual values
        //isDisplay - defines which set to use
        function getValidators (isDisplay) {
            /* jshint validthis: true */
            var validators = this.operator.validators;
            if (isDisplay) {
                validators = this.operator.displayValidators;
            }
            if (angular.isArray(validators)) {
                return angular.copy(validators);
            }
            return angular.copy(validators[this.field.type.id.toLowerCase()]);
        }

        Filter.prototype.__defineGetter__("inputTemplate", function () {
            var templateName = "exploreFilter-";

            if (!this.operator.inputTemplate) {
                return null;
            }

            if (typeof(this.operator.inputTemplate) === "string") {
                templateName += this.operator.inputTemplate;
            } else {
                templateName += this.operator.inputTemplate[this.field.type.id.toLowerCase()];
            }

            if (this.field.isSearchable && this.operator.supportsSearch) {
                templateName += "-searchable";
            }

            return templateName;
        });

        Filter.prototype.__defineGetter__("searchSettings", function () {
            if (this._searchSettings === undefined) {
                if (this.field.isSearchable) {
                    this._searchSettings = search.getDataEntityFieldSearchSettings(this.field);
                } else {
                    this._searchSettings = null;
                }
            }

            return this._searchSettings;

        });

        Filter.prototype.__defineGetter__("defaultValue", function () {
            if (this.operator.defaultValue) {
                return this.operator.defaultValue[this.field.type.id.toLowerCase()];
            }
        });

        /*
         * The getter and setter mechanism is used for validation and defining the filter as dirty every time you
         * edit the filter content
         */
        Filter.prototype.__defineGetter__("value", function () {
            return this._value;
        });

        Filter.prototype.__defineSetter__("value", function (value) {
            if (value !== this._value) {

                try {
                    this.validate(value);
                    this.validObj.isValid = true;
                    this.validObj.showErrors = false;
                } catch (errorMessage) {
                    this.validObj.isValid = false;
                    this.validObj.errorMessage = errorMessage.message;
                    this.validObj.showErrors = true;
                }

                this._value = this.field.type.parseValue(value);
                this.isDirty = true;
                this.validObj.isValidOnInit = true;
            }
        });

        Filter.prototype.__defineGetter__("displayValue", function () {
            return this._displayValue;
        });

        Filter.prototype.__defineSetter__("displayValue", function (displayValue) {
            if (displayValue !== this._displayValue) {

                try {
                    this.validate(displayValue, true);
                    this.validObj.isValid = true;
                    this.validObj.showErrors = false;
                } catch (errorMessage) {
                    this.validObj.isValid = false;
                    this.validObj.errorMessage = errorMessage.message;
                    this.validObj.showErrors = true;
                }

                this._displayValue = this.field.type.parseValue(displayValue);
                this.isDirty = true;
                this.validObj.isValidOnInit = true;
            }
        });

        /**
         * Returns a string that can be used by state
         * @returns {string}
         */
        Filter.prototype.getParamValue = function () {
            var valueParam = this.valueField ? "[" + this.valueField.entity.id + "." + this.valueField.id + "]" :
            this.displayValue || this.value;
            if (this.operator.valueToParam) {
                valueParam =
                    this.operator.valueToParam(valueParam, this.field.type.id.toLowerCase(), this.field.format);
            }

            return this.operator.paramOperator + valueParam;
        };

        return Filter;
    }

    FilterClass.$inject = ["DataEntityField", "QueryOperator", "search", "FilterValidators"];

    angular.module("Explore.Filters").factory("Filter", FilterClass);
})();
