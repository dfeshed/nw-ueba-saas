(function () {
    'use strict';

    function filters (Filter, dataEntities, queryOperators) {

        /**
         * Given a param name and a param value, returns a new Filter that matches the param
         * @param paramName
         * @param paramValue
         * @returns {*}
         */
        function getFilterFromParam (paramName, paramValue) {
            if (paramValue === null || paramValue === undefined || paramValue === "_null_") {
                return null;
            }

            if (typeof(paramValue) !== "string") {
                throw new TypeError("Invalid param value, expected a string but got " + typeof(paramValue));
            }

            var entityField = paramName.split("."),
                dataEntityField = dataEntities.getField(entityField[0], entityField[1]);

            if (!dataEntityField) {
                return null;
            }

            var filter = new Filter(dataEntityField);

            var paramOperator = queryOperators.getParamOperator(paramValue),
                paramValueWithoutOperator = paramValue.replace(paramOperator.paramOperator, "");

            var valueFieldMatch = paramValueWithoutOperator.match(/\[((\w+).(\w+))\]$/);
            if (valueFieldMatch) {
                var valueField = dataEntities.getField(valueFieldMatch[2], valueFieldMatch[3]);
                if (!valueField) {
                    console.warn("WARNING: Unknown field, '" + valueFieldMatch[1] + ", treating '" +
                        paramValueWithoutOperator + "' as a string value.");
                }

                filter.valueField = valueField;
            }
            else {
                filter.value = paramOperator.paramToValue ?
                    paramOperator.paramToValue(paramValueWithoutOperator, filter.field.type.id.toLowerCase()) :
                    paramValueWithoutOperator;
                // Use display value only for fields that support it
                if (paramOperator.paramToDisplayValue) {
                    filter.displayValue =
                        paramOperator.paramToDisplayValue(paramValueWithoutOperator, filter.field.type.id.toLowerCase(),
                            filter.field.format);
                }

            }
            filter.operator = paramOperator;

            return filter;
        }

        return {
            getFilterFromParam: getFilterFromParam
        };

    }

    filters.$inject = ["Filter", "dataEntities", "queryOperators"];

    angular.module("Explore.Filters").factory("filters", filters);
})();
