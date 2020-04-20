(function () {
    'use strict';

    angular.module("DataEntities").factory("DataEntityFieldType", ["queryOperators", function (queryOperators) {
        /**
         * Constructor that represents a type for dataEntity fields, to be used in DataQueries.
         * Contains the type and its available operators.
         * @param config
         * @constructor
         */
        function DataEntityFieldType (config) {
            this.validate(config);

            this.id = config.id;
            this.name = config.name;
            this.operators = config.operators.map(function (operatorId) {
                var operator = queryOperators.operators.get(operatorId);
                if (!operator) {
                    throw new Error("Invalid operator for DataEntityFieldType: " + operatorId);
                }

                return operator;
            });
            this.parseValue = config.parser;
        }

        DataEntityFieldType.prototype.validate = function (config) {
            if (!config.id || typeof(config.id) !== "string") {
                throw new TypeError("Can't crate DataEntityFieldType, expected a string ID.");
            }

            if (!config.name || typeof(config.name) !== "string") {
                throw new TypeError("Can't crate DataEntityFieldType, expected a string name.");
            }

            if (!config.operators) {
                throw new Error("Can't create DataEntityFieldType, missing the operators array.");
            }

            if (!config.parser || !(config.parser instanceof Function)) {
                throw new Error("Can't create DataEntityFieldType, expected a parser function.");
            }

            if (config.operators.constructor !== Array) {
                throw new TypeError("Invalid operators for DataEntityFieldType, expected an array.");
            }
        };

        DataEntityFieldType.prototype.__defineGetter__("defaultOperator", function () {
            return this.operators[0];
        });

        return DataEntityFieldType;
    }]);
})();
