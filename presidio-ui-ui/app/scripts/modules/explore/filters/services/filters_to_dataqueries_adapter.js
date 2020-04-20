(function () {

    'use strict';

    function filtersToDataQueriesAdapter (queryOperators, Filter) {

        /**
         * Converts a Filter object to its parallel DataQueryConditionTerm configuration
         * @param filterCollection
         */
        function filterCollectionToConditionTerm (filterCollection) {
            if (filterCollection.constructor.name !== "FilterCollection") {
                throw new TypeError("Invalid filterCollection, expected an instance of FilterCollection but got " +
                    filterCollection);
            }

            var conditionTerm = {
                operator: filterCollection.operator
            };

            conditionTerm.terms = filterCollection.filters.map(filterToConditionField);

            return conditionTerm;
        }

        var filterToConditionsMapping = {

            "DATE_TIME": {
                notEquals: function (filter) {
                    //filter. value has timeStart and timeEnd
                    //create 2 conditions: <timeStart and >timeEnd
                    //return conditionTerm
                    var startFilter = Filter.copy(filter);
                    startFilter.operator = queryOperators.operators.get("lesserThan");
                    startFilter.value = filter.value.timeStart;
                    var conditionFieldTimeStart = filterToConditionField(startFilter);

                    var endFilter = Filter.copy(filter);
                    endFilter.operator = queryOperators.operators.get("greaterThan");
                    endFilter.value = filter.value.timeEnd;
                    var conditionFieldTimeEnd = filterToConditionField(endFilter);

                    var conditionTerm = {
                        operator: "OR",
                        type: "term"
                    };

                    conditionTerm.terms = [conditionFieldTimeStart, conditionFieldTimeEnd];
                    return conditionTerm;
                },
                equals: function (filter) {
                    //filter. value has timeStart and timeEnd
                    //create range condition
                    var newFilter = Filter.copy(filter);
                    newFilter.operator = queryOperators.operators.get("dateRange");
                    return filterToConditionField(newFilter);
                }
            }

        };


        /**
         * Converts a Filter object to its parallel DataQueryConditionField configuration
         * @param filter
         * @returns {{type: string, id: *, operator: *, value: *}}
         */
        function filterToConditionField (filter) {
            if (filter.constructor.name !== "Filter") {
                throw new TypeError("Invalid filter, expected an instance of Filter but got " + filter);
            }

            if (filterToConditionsMapping[filter.field.type.id] &&
                filterToConditionsMapping[filter.field.type.id][filter.operator.id]) {
                //function that translate the filter to several conditions
                return filterToConditionsMapping[filter.field.type.id][filter.operator.id](filter);
            }

            var conditionField = {
                type: "field",
                id: filter.field.id,
                operator: filter.operator.id,
                entity: filter.field.entity.id
            };

            if (filter.valueField) {
                conditionField.valueField = {id: filter.valueField.id, entity: filter.valueField.entity.id};
            } else {
                conditionField.value = filter.value;
            }

            return conditionField;
        }

        return {
            filterCollectionToConditionTerm: filterCollectionToConditionTerm
        };

    }

    filtersToDataQueriesAdapter.$inject = ["queryOperators", "Filter"];

    angular.module("Explore.Filters").factory("filtersToDataQueriesAdapter", filtersToDataQueriesAdapter);
})();
