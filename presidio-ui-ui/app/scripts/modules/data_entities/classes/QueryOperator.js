(function () {
    'use strict';

    angular.module("DataEntities").factory("QueryOperator", function () {
        function QueryOperator (config) {
            this.validate(config);

            this.id = config.id;
            this.name = config.name;
            this.requiresValue = !!config.requiresValue;
            this.supportsSearch = !!config.supportsSearch;
            this.inputTemplate = config.inputTemplate;
            this.defaultValue = config.defaultValue;
            this.paramOperator = config.paramOperator || "";
            this.valueToParamConfig = config.valueToParam;
            if (this.valueToParamConfig) {
                this.valueToParam = function valueToParam (value, valueType, valueFormat) {
                    if (this.valueToParamConfig.constructor === Function) {
                        return this.valueToParamConfig(value);
                    }
                    else if (this.valueToParamConfig[valueType]) {
                        return this.valueToParamConfig[valueType](value, valueFormat);
                    }
                    return value;
                };
            }
            this.paramToValueConfig = config.paramToValue;
            if (this.paramToValueConfig) {
                this.paramToValue = function paramToValue (param, valueType) {
                    if (this.paramToValueConfig.constructor === Function) {
                        return this.paramToValueConfig(param);
                    }
                    else if (this.paramToValueConfig[valueType]) {
                        return this.paramToValueConfig[valueType](param);
                    }
                    return param;
                };
            }
            //add support in display which is different from the value - relevant currently for duration fields
            this.paramToDisplayValueConfig = config.paramToDisplayValue;
            if (this.paramToDisplayValueConfig) {
                this.paramToDisplayValue = function paramToDisplayValue (param, valueType, valueFormat) {
                    if (this.paramToDisplayValueConfig.constructor === Function) {
                        return this.paramToDisplayValueConfig(param);
                    }
                    else if (this.paramToDisplayValueConfig[valueType]) {
                        return this.paramToDisplayValueConfig[valueType](param, valueFormat);
                    }
                    return undefined;
                };
            }
            this.dataQueryOperator = config.dataQueryOperator || config.id;
            this.validators = config.validators || [];
            this.displayValidators = config.displayValidators || [];
        }

        QueryOperator.prototype.validate = function (config) {
            if (!config.id) {
                throw new Error("Can't create QueryOperator, missing id.");
            }

            if (!config.name) {
                throw new Error("Can't create QueryOperator, missing name.");
            }

            if (config.requiresValue && !config.inputTemplate) {
                throw new Error("Can't create QueryOperator for operator '" + config.name +
                    "', missing inputTemplate.");
            }

            if (config.paramOperator && typeof(config.paramOperator) !== "string") {
                throw new TypeError("Can't create QueryOperator, expected string for paramOperator but got " +
                    typeof(config.paramOperator));
            }
        };

        return QueryOperator;
    });
}());
