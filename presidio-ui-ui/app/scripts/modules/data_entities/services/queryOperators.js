(function () {
    'use strict';

    angular.module("DataEntities").factory("queryOperators",
        ["QueryOperator", "utils",
            function (QueryOperator, utils) {

                /**
                 * Common handlers
                 */

                function durationPrettyTime (paramValue, valueFormat) {
                    return utils.duration.prettyTime(paramValue, valueFormat);
                }

                function durationToNumber (value, valueFormat) {
                    return utils.duration.durationToNumber(value, valueFormat);
                }

                /**
                 * Get date range string of a full day from a single date value
                 *
                 * @param  {*}      value Any valid value for `utils.date.getMoment`
                 * @return {string}       Day start to day end range
                 */
                function valueToDateRange (value) {
                    var start = utils.date.getMoment(value).startOf('day');
                    var end = utils.date.getMoment(value).endOf('day');

                    return start + "::" + end;
                }

                /**
                 * Get date range object from a date range string
                 *
                 * @param  {string} paramValue Date range string, as in `valueToDateRange` above
                 * @param  {string} operatorId The relevant operator context, used for the error
                 * @return {Object}            An object with `timeStart` and `timeEnd` properties
                 */
                function parseDateRangeStr (paramValue, operatorId) {
                    // Validate input
                    if (!(/[^:]+::[^:]+/.test(paramValue))) {
                        throw new Error("Date range value must be in format: 'date::date'");
                    }

                    var rangeParts = paramValue.split("::");

                    var timeStart = utils.date.getMoment(rangeParts[0]);
                    var timeEnd = utils.date.getMoment(rangeParts[1]);

                    // If range of strings that are not timestamps
                    // Shift to start of and end of days
                    if (!utils.date.isTimeStamp(rangeParts[0])) {
                        timeStart.startOf('day');
                    }
                    if (!utils.date.isTimeStamp(rangeParts[1])) {
                        timeEnd.endOf('day');
                    }

                    // Make sure we have valid dates
                    if (!timeStart.isValid() || !timeEnd.isValid()) {
                        throw new Error("Invalid value for " + operatorId +
                            " - one or both dates are invalid: '" + paramValue + "'.");
                    }

                    return {
                        timeStart: timeStart.toDate(),
                        timeEnd: timeEnd.toDate()
                    };
                }

                /**
                 * Operators settings
                 */

                var operatorsConfig = [
                    {
                        id: "equals",
                        name: "=",
                        requiresValue: true,
                        supportsSearch: true,
                        inputTemplate: {
                            string: "string",
                            number: "number",
                            date_time: "date",
                            timestamp: "date",
                            boolean: "boolean",
                            duration: "duration",
                            select: "select"
                        },
                        validators: {date_time: ["dateValidator"]},
                        displayValidators: {duration: ["isDurationValidator"]},
                        defaultValue: {
                            boolean: true,
                            date_time: "now"
                        },
                        valueToParam: {
                            date_time: valueToDateRange,
                            duration: durationToNumber
                        },
                        paramToValue: {
                            date_time: function (paramValue) {
                                return parseDateRangeStr(paramValue, 'EqualsRange');
                            }
                        },
                        paramToDisplayValue: {
                            duration: durationPrettyTime
                        }
                    },
                    {
                        //notEquals for date relates to a certain day, not timestamp, therefore we need range
                        // -startOfDay and EndofDay. not a single parameter.
                        id: "notEquals",
                        name: "≠",
                        paramOperator: "!",
                        requiresValue: true,
                        supportsSearch: true,
                        inputTemplate: {
                            string: "string",
                            number: "number",
                            date_time: "date",
                            timestamp: "date",
                            boolean: "boolean",
                            duration: "duration",
                            select: "select"
                        },
                        validators: {date_time: ["dateValidator"]},
                        displayValidators: {duration: ["isDurationValidator"]},
                        defaultValue: {
                            boolean: true,
                            date_time: "now"
                        },
                        valueToParam: {
                            date_time: valueToDateRange,
                            duration: durationToNumber
                        },
                        paramToValue: {
                            date_time: function (paramValue) {
                                return parseDateRangeStr(paramValue, 'notEqualsRange');
                            }
                        },
                        paramToDisplayValue: {
                            duration: durationPrettyTime
                        }
                    },
                    {
                        id: "in",
                        name: "IN",
                        paramOperator: "[]",
                        text: "=",
                        requiresValue: true,
                        inputTemplate: "stringIn",
                        defaultValue: {string: []},
                        validators: ["isArrayValidator"],
                        valueToParam: function (value) {
                            return value.map(function (val) {
                                return val.replace(/[|]/g, "~~");
                            }).join("|");

                        },
                        paramToValue: function (paramValue) {
                            if (paramValue === "") {
                                return [];
                            }
                            return paramValue.split("|").map(function (val) {
                                return val.replace(/~~/g, "|");
                            });
                        }
                    },
                    {
                        id: "contains",
                        name: "Contains",
                        paramOperator: "~",
                        requiresValue: true,
                        inputTemplate: "string"
                    },
                    {
                        id: "hasValue",
                        name: "Has value",
                        paramOperator: "*",
                        requiresValue: false
                    },
                    {
                        id: "hasNoValue",
                        name: "Has no value",
                        paramOperator: "!*",
                        requiresValue: false
                    },
                    {
                        id: "startsWith",
                        name: "Starts With",
                        paramOperator: "^",
                        requiresValue: true,
                        inputTemplate: "string"
                    },
                    {
                        id: "endsWith",
                        name: "Ends With",
                        paramOperator: "$",
                        requiresValue: true,
                        inputTemplate: "string"
                    },
                    {
                        id: "regexp",
                        name: "RegExp",
                        paramOperator: "/",
                        requiresValue: true,
                        inputTemplate: "regex"
                    },
                    {
                        id: "greaterThan",
                        name: ">",
                        paramOperator: ">",
                        requiresValue: true,
                        inputTemplate: {number: "number", date_time: "date", timestamp: "date", duration: "duration"},
                        validators: {date_time: ["dateValidator"]},
                        displayValidators: {duration: ["isDurationValidator"]},
                        defaultValue: {
                            date_time: "now",
                            timestamp: "now",
                            number: 0
                        },
                        valueToParam: {
                            date_time: function (paramValue) {
                                return utils.date.getMoment(paramValue).endOf('day');
                            },
                            duration: durationToNumber
                        },
                        paramToDisplayValue: {
                            duration: durationPrettyTime
                        }
                    },
                    {
                        id: "greaterThanOrEquals",
                        name: "≥",
                        paramOperator: ">=",
                        requiresValue: true,
                        inputTemplate: {number: "number", date_time: "date", timestamp: "date", duration: "duration"},
                        validators: {date_time: ["dateValidator"]},
                        displayValidators: {duration: ["isDurationValidator"]},
                        defaultValue: {
                            date_time: "now",
                            timestamp: "now",
                            number: 0
                        },
                        valueToParam: {
                            date_time: function (paramValue) {
                                return utils.date.getMoment(paramValue).startOf('day');
                            },
                            duration: durationToNumber
                        },
                        paramToDisplayValue: {
                            duration: durationPrettyTime
                        }
                    },
                    {
                        id: "lesserThan",
                        name: "<",
                        paramOperator: "<",
                        requiresValue: true,
                        inputTemplate: {number: "number", date_time: "date", timestamp: "date", duration: "duration"},
                        validators: {date_time: ["dateValidator"]},
                        displayValidators: {duration: ["isDurationValidator"]},
                        defaultValue: {
                            date_time: "now",
                            timestamp: "now",
                            number: 0
                        },
                        valueToParam: {
                            date_time: function (paramValue) {
                                return utils.date.getMoment(paramValue).startOf('day');
                            },
                            duration: durationToNumber
                        },
                        paramToDisplayValue: {
                            duration: durationPrettyTime
                        }
                    },
                    {
                        id: "lesserThanOrEquals",
                        name: "≤",
                        paramOperator: "<=",
                        requiresValue: true,
                        inputTemplate: {number: "number", date_time: "date", timestamp: "date", duration: "duration"},
                        validators: {date_time: ["dateValidator"]},
                        displayValidators: {duration: ["isDurationValidator"]},
                        defaultValue: {
                            date_time: "now",
                            timestamp: "now",
                            number: 0
                        },
                        valueToParam: {
                            date_time: function (paramValue) {
                                return utils.date.getMoment(paramValue).endOf('day');
                            },
                            duration: durationToNumber

                        },
                        paramToDisplayValue: {
                            duration: durationPrettyTime
                        }
                    },
                    {
                        id: "range",
                        dataQueryOperator: "between",
                        name: "Range",
                        paramOperator: "--",
                        requiresValue: true,
                        inputTemplate: "numberRange",
                        validators: ["numberRangeValidator"],
                        valueToParam: function (value) {
                            return value.fromValue + "--" + value.toValue;
                        },
                        paramToValue: function (paramValue) {
                            if (typeof(paramValue) !== "string") {
                                throw new TypeError("Invalid param value for numberRange, expected a string but got " +
                                    paramValue);
                            }

                            var rangeParts = paramValue.split("--");

                            if (rangeParts.length !== 2) {
                                throw new Error("Invalid value for numberRange, '" + paramValue + "'.");
                            }

                            return {
                                fromValue: rangeParts[0],
                                toValue: rangeParts[1]
                            };

                        }
                    },
                    {
                        id: "dateRange",
                        dataQueryOperator: "between",
                        validators: ["dateRangeValidator"],
                        name: "Between",
                        paramOperator: ":",
                        requiresValue: true,
                        inputTemplate: "dateRange",
                        valueToParam: function (value) {
                            var start;
                            var end;

                            // If an object with timeStart and timeEnd
                            if (angular.isObject(value) && value.timeStart && value.timeEnd) {
                                start = utils.date.getMoment(value.timeStart).startOf('day');
                                end = utils.date.getMoment(value.timeEnd).endOf('day');
                            }
                            // Assume singel value
                            else {
                                start = utils.date.getMoment(value).startOf('day');
                                end = utils.date.getMoment(value).endOf('day');
                            }

                            return start + '::' + end;
                        },
                        paramToValue: function (paramValue) {
                            return parseDateRangeStr(paramValue, 'dateRange');
                        },
                        defaultValue: {
                            date_time: {
                                timeStart: "-7d",
                                timeEnd: "now"
                            }
                        }
                    }
                ];

                var operators = new Map(),
                    paramOperators = {};

                operatorsConfig.forEach(function (operator) {
                    var paramOperatorId = operator.paramOperator || "equals";
                    // Checking that paramOperators are unique:
                    if (paramOperators[paramOperatorId]) {
                        throw new Error("Duplicate param operator: " + paramOperatorId);
                    }

                    var queryOperator = new QueryOperator(operator);
                    operators.set(operator.id, queryOperator);
                    paramOperators[paramOperatorId] = queryOperator;
                });

                var logicalOperators = {
                    AND: "AND",
                    OR: "OR"
                };

                return {
                    /**
                     * Given a param value (URL param), returns the used QueryOperator
                     * @param paramValue
                     */
                    getParamOperator: function (paramValue) {
                        var paramPrefixMatch = paramValue.match(/^[^\w\d]{1,2}/),
                            operator;

                        if (paramPrefixMatch) {
                            var prefix = paramPrefixMatch[0];
                            // The operator prefix is either one or two characters. First we try to find a
                            // two-character operator, then a single-character, if two isn't found:
                            operator = paramOperators[prefix] || paramOperators[prefix[0]];
                        }

                        return operator || paramOperators.equals;
                    },
                    get operators () {
                        return operators;
                    },
                    get logicalOperators () {
                        return logicalOperators;
                    }
                };
            }]);
}());
