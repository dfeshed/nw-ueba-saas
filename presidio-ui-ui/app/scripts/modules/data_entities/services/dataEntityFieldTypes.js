(function () {
    'use strict';

    /**
     * The available types for Data Entity fields. Returns an object, of which each property is the ID of a type
     * ('string', 'boolean', ...). Each type has an ID, name and its possible operators.
     * @param DataEntityFieldType DI for the DataEntityFieldType constructor
     * @param utils DI for the DataEntityFieldType constructor
     * @returns {{boolean: {id: string, name: string, operators: string[]}, string: {id: string, name: string,
     *     operators: string[]}, number: {id: string, name: string, operators: string[]}, date_time: {id: string, name:
     *     string, operators: string[]}, timestamp: {id: string, name: string, operators: string[]}}}
     * @constructor
     */
    function dataEntityFieldTypes (DataEntityFieldType, utils) {

        function dateTimeParser (value) {
            if (value === undefined || value === null) {
                return value;
            }

            // It's a date range:
            if (Object(value) === value && !angular.isDate(value)) {
                if (!value.timeEnd || !value.timeStart) {
                    throw new Error("Invalid object for date, should contain both timeStart and timeEnd.");
                }

                var startMoment = utils.date.getMoment(value.timeStart).startOf("day"),
                    endMoment = utils.date.getMoment(value.timeEnd).endOf("day");

                if (!startMoment.isValid() || !endMoment.isValid()) {
                    throw new Error("Invalid timeStart or timeEnd for date.");
                }

                return {timeStart: startMoment.toDate(), timeEnd: endMoment.toDate()};
            }
            else {
                var momentValue = utils.date.getMoment(value);
                if (momentValue.isValid()) {
                    return momentValue.toDate();
                }
            }

            throw new TypeError("Can't parse value to date_time: " + value + ".");
        }

        function timestampParser (value) {
            if (value === undefined || value === null) {
                return value;
            }

            // It's a timestamp range:
            if (Object(value) === value && !angular.isDate(value)) {
                if (!value.timeEnd || !value.timeStart) {
                    throw new Error("Invalid object for date, should contain both timeStart and timeEnd.");
                }

                var startMoment = utils.date.getMoment(value.timeStart),
                    endMoment = utils.date.getMoment(value.timeEnd);

                if (!startMoment.isValid() || !endMoment.isValid()) {
                    throw new Error("Invalid timeStart or timeEnd for date.");
                }

                return {timeStart: startMoment.toDate(), timeEnd: endMoment.toDate()};
            }
            else {
                var momentValue = utils.date.getMoment(value);
                if (momentValue.isValid()) {
                    return momentValue.toDate();
                }
            }

            throw new TypeError("Can't parse value to date_time: " + value + ".");
        }

        var commonOperators = ["hasValue", "hasNoValue"],
            dateOperators = ["dateRange", "equals", "notEquals", "greaterThan", "greaterThanOrEquals", "lesserThan",
                "lesserThanOrEquals"];

        var types = {
            "boolean": {
                id: "BOOLEAN",
                name: "Boolean",
                operators: ["equals"],
                parser: function (value) {
                    if (value === undefined || value === null) {
                        return value;
                    }

                    if (value === "true") {
                        return true;
                    }

                    if (value === "false") {
                        return false;
                    }

                    return !!value;
                }
            },
            "string": {
                id: "STRING",
                name: "String",
                operators: ["equals", "notEquals", "contains", "in", "startsWith", "endsWith"],
                parser: function (value) {
                    if (value === undefined || value === null) {
                        return value;
                    }

                    if (typeof(value) === "string") {
                        return value;
                    }

                    if (Object(value) === value) {
                        if (angular.isArray(value)) {
                            return value;
                        }
                        return JSON.parse(value);
                    }

                    return value.toString();
                }
            },
            "capitalize": {
                id: "CAPITALIZE",
                name: "Capitalize",
                operators: ["equals", "notEquals", "contains", "in", "startsWith", "endsWith"],
                parser: function (value) {
                    if (value === undefined || value === null) {
                        return value;
                    }

                    if (typeof(value) === "string") {
                        return value;
                    }

                    if (Object(value) === value) {
                        if (angular.isArray(value)) {
                            return value;
                        }
                        return JSON.parse(value);
                    }

                    return value.toString();
                }
            },
            "number": {
                id: "NUMBER",
                name: "Number",
                operators: ["equals", "notEquals", "greaterThan", "greaterThanOrEquals", "lesserThan",
                    "lesserThanOrEquals", "range"],
                parser: function (value) {
                    //undefiend is whne the filter is not set and should set 0 for numeric default value
                    if (value === undefined || value === 'undefined') {
                        return 0;
                    }

                    //null is defined and handled by changing it to the default value of '0'
                    if (value === null || value === 'null') {
                        return null;
                    }

                    if (angular.isObject(value)) {
                        return value;
                    }
                    return utils.numbers.parse(value);

                }
            },

            //date_time: the time in day resolution.
            "date_time": {
                id: "DATE_TIME",
                name: "Date",
                operators: dateOperators,
                parser: dateTimeParser
            },
            //timestamp: includes also the hours, minutes, etc.
            "timestamp": {
                id: "TIMESTAMP",
                name: "Timestamp",
                operators: dateOperators,
                parser: timestampParser
            },
            //duration is in format hh:mm:ss
            "duration": {
                id: "duration",
                name: "duration",
                operators: ["equals", "notEquals", "greaterThan", "greaterThanOrEquals", "lesserThan",
                    "lesserThanOrEquals"],
                parser: function (value) {
                    return value;
                }
            },
            //select from constant list of values
            "select": {
                id: "select",
                name: "select",
                operators: ["equals", "notEquals"],
                parser: function (value) {
                    return value;
                }
            }
        };

        var type;

        for (var typeId in types) {
            if (types.hasOwnProperty(typeId)) {
                type = types[typeId];
                type.operators = type.operators.concat(commonOperators);
                types[typeId] = new DataEntityFieldType(type);
            }
        }

        return types;

    }

    dataEntityFieldTypes.$inject = ["DataEntityFieldType", "utils"];

    angular.module("DataEntities").factory("dataEntityFieldTypes", dataEntityFieldTypes);
})();
