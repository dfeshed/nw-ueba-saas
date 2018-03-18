(function () {
    'use strict';

    angular.module("Conditions", ["Format", "Transforms", "DataEntities"]).factory("conditions",
        ["format", "utils", "dataEntities", function (format, utils, dataEntities) {
            var validations = {
                contains: function (val1, val2) {
                    if (validations.hasNoValue(val1) || validations.hasNoValue(val2)) {
                        return false;
                    }

                    if (angular.isArray(val1)) {
                        return !!~val1.indexOf(val2);
                    }

                    var regexp = new RegExp(val2, "i");
                    return regexp.test(val1);
                }, equals: function (val1, val2) {
                    if (angular.isObject(val1) && angular.isObject(val2)) {
                        return val1 === val2;
                    }

                    if ((val1 === null || val1 === undefined) && (val2 === null || val2 === undefined)) {
                        return true;
                    }

                    if (((val1 === null || val1 === undefined) && (val2 !== null && val2 !== undefined )) ||
                        ((val1 !== null && val1 !== undefined) && (val2 === undefined || val2 === undefined))) {
                        return false;
                    }

                    return val1.toString() === val2.toString();
                }, entityExists: function (val1, val2) {
                    return dataEntities.entityExists(val1 || val2);
                }, entityDoesntExists: function (val1, val2) {
                    return !dataEntities.entityExists(val1 || val2);
                }, isFalsy: function (val) {
                    return !val;
                }, isTruthy: function (val) {
                    return !!val;
                }, notEquals: function (val1, val2) {
                    return val1 !== val2;
                }, greaterThan: function (val1, val2) {
                    return val1 > val2;
                }, greaterThanOrEquals: function (val1, val2) {
                    return val1 >= val2;
                }, lesserThan: function (val1, val2) {
                    return val1 < val2;
                }, lesserThanOrEqual: function (val1, val2) {
                    return val1 <= val2;
                }, included: function (val1, arr) {
                    return !!~arr.indexOf(val1);
                }, hasValue: function (value) {
                    return value !== undefined && value !== null;
                }, hasNoValue: function (value) {
                    return !validations.hasValue(value);
                }
            };

            function getQueryParamOperator (paramValue) {
                var paramOperator = getParamOperator(paramValue), defaultOperator = "equals";

                if (!paramOperator) {
                    return defaultOperator;
                }

                for (var operatorName in operatorTypesParamPrefixes) {
                    if (operatorTypesParamPrefixes.hasOwnProperty(operatorName)) {
                        if (operatorTypesParamPrefixes[operatorName] === paramOperator) {
                            return operatorName;
                        }
                    }
                }

                return defaultOperator;
            }

            var paramParsers = {
                dateRange: function (param) {
                    if (angular.isObject(param)) {
                        return param;
                    }

                    var dates = param.split("::"), dateRange = {
                        timeStart: dates[0], timeEnd: dates[0]
                    };

                    if (dates.length > 1) {
                        dateRange.timeEnd = dates[1];
                    }

                    return dateRange;
                },
                range: function (param) {
                    if (angular.isObject(param)) {
                        return param;
                    }

                    var range = param.split("::");

                    return {
                        min: range[0], max: range[1]
                    };
                },
                "default": function (param) {
                    return param;
                }
            };

            function getParamOperator (param) {
                var operator;
                for (var i = 0, prefix; (!!(prefix = operatorCharacters[i])); i++) {
                    if (!param.indexOf(prefix)) {
                        if (!operator || prefix.length > operator.length) {
                            operator = prefix;
                        }
                    }
                }
                return operator;
            }

            var operatorTypesParamPrefixes = {
                contains: "~",
                notEquals: "!",
                greaterThan: ">",
                greaterThanOrEquals: ">=",
                lesserThan: "<",
                lesserThanOrEqual: "<=",
                startsWith: "^",
                endsWith: "$",
                "in": "[]",
                dateRange: ":",
                hasValue: "*",
                hasNoValue: "!*",
                range: "--"
            };

            var operatorCharacters = [];
            angular.forEach(operatorTypesParamPrefixes, function (prefix) {
                operatorCharacters.push(prefix);
            });

            var operators = {
                boolean: {
                    "default": "equals", operators: [{name: "equals", display: "=", requiresValue: true}]
                }, string: {
                    "default": "equals",
                    operators: [{name: "equals", display: "=", requiresValue: true, allowMultiple: true},
                        {name: "in", display: "IN", text: "=", requiresValue: true},
                        {name: "notEquals", display: "!=", requiresValue: true, allowMultiple: true},
                        {name: "contains", display: "Contains", requiresValue: true},
                        {name: "hasValue", display: "Has value", sql: "IS NOT NULL", requiresValue: false},
                        {name: "hasNoValue", display: "Has no value", sql: "IS NULL", requiresValue: false},
                        {name: "startsWith", display: "Starts With", requiresValue: true},
                        {name: "endsWith", display: "Ends With", requiresValue: true},
                        {name: "regexp", display: "RegExp", requiresValue: true}]
                }, number: {
                    "default": "equals",
                    operators: [{name: "equals", display: "=", requiresValue: true},
                        {name: "greaterThan", display: ">", requiresValue: true, types: ["number"]},
                        {name: "greaterThanOrEquals", display: "≥", requiresValue: true, types: ["number"]},
                        {name: "lesserThan", display: "<", requiresValue: true, types: ["number"]},
                        {name: "lesserThanOrEqual", display: "≤", requiresValue: true, types: ["number"]},
                        {name: "hasValue", display: "Has value", requiresValue: false},
                        {name: "hasNoValue", display: "Has no value", requiresValue: false}]
                }, date: {
                    "default": "dateRange",
                    operators: [{name: "dateRange", display: "Dates", text: "", requiresValue: true, types: ["date"]},
                        {name: "greaterThan", display: ">", requiresValue: true, types: ["date"]},
                        {name: "greaterThanOrEquals", display: "≥", requiresValue: true, types: ["date"]},
                        {name: "lesserThan", display: "<", requiresValue: true, types: ["date"]},
                        {name: "lesserThanOrEqual", display: "≤", requiresValue: true, types: ["date"]}]
                }, hours: {
                    "default": "greaterThan",
                    operators: [{name: "equals", display: "=", requiresValue: true},
                        {name: "greaterThan", display: ">", requiresValue: true, types: ["number"]},
                        {name: "greaterThanOrEquals", display: "≥", requiresValue: true, types: ["number"]},
                        {name: "lesserThan", display: "<", requiresValue: true, types: ["number"]},
                        {name: "lesserThanOrEqual", display: "≤", requiresValue: true, types: ["number"]}]
                }, range: {
                    "default": "range", operators: [{name: "range", display: "", requiresValue: true, types: ["range"]}]
                }
            };

            function getFormattedValue (value) {
                if (angular.isDate(value)) {
                    return utils.date.getMoment(value).format("YYYY-MM-DD");
                }

                return value;
            }

            var operatorsToParamValues = {
                dateRange: function (dates) {
                    var strDates = [dates.timeStart];
                    if (dates.timeEnd && dates.timeEnd !== dates.timeStart) {
                        strDates.push(dates.timeEnd);
                    }

                    return strDates.join("::");
                },
                range: function (rangeValue) {
                    return rangeValue.min + "::" + rangeValue.max;
                },
                greaterThan: getFormattedValue,
                greaterThanOrEquals: getFormattedValue,
                lesserThan: getFormattedValue,
                lesserThanOrEqual: getFormattedValue
            };

            var operatorsToString = {
                dateRange: function (value) {
                    var isRange = value.timeEnd && value.timeEnd !== value.timeStart, str = isRange ? "between " : "= ";

                    str += utils.date.getMoment(value.timeStart).format("MM/DD/YYYY");
                    if (isRange) {
                        str += " - ";
                        str += utils.date.getMoment(value.timeEnd).format("MM/DD/YYYY");
                    }

                    return str;
                }
            };

            var stringToOperators = {
                date: function (value) {
                    var time = utils.date.getMoment(value).format("YYYY-MM-DD");

                    return {
                        timeStart: time, timeEnd: time
                    };
                }
            };

            var operatorsSqlValue = {
                contains: function (value, fieldName, condition) {
                    return "lcase(" + fieldName + ") LIKE '%" + value.toLowerCase() + "%'";
                }, equals: function (value, fieldName, condition) {
                    if (condition.valueField) {
                        return [fieldName, condition.valueField].join(" = ");
                    }

                    if (condition.castAs) {
                        return fieldName + " = cast(" + value + " as " + condition.castAs + ")";
                    } else if (typeof(value) === "string") {
                        if (condition.type === "date") {
                            var dateStr;

                            if (/^\d+$/.test(value)) {
                                dateStr = utils.date.getMoment(parseInt(value)).format("YYYY-MM-DD");
                            } else {
                                dateStr = value;
                            }

                            return dateStr ? 'to_date(' + fieldName + ') = "' + dateStr + '"' : "";
                        } else {
                            return "lcase(" + fieldName + ") = \"" + value.toLowerCase() + "\"";
                        }
                    } else {
                        return [fieldName, value].join(" = ");
                    }
                }, range: function (value, fieldName, condition) {
                    return fieldName + " <= " + value.max + " AND " + fieldName + " >= " + value.min;
                }, dateRange: function (value, fieldName, condition) {
                    var dateStart = utils.date.getMoment(angular.isObject(value) ? value.timeStart :
                        value), dateEnd = utils.date.getMoment(angular.isObject(value) ? value.timeEnd :
                        value), fieldNameParts = fieldName.split("."), tableName = fieldNameParts.length > 1 ?
                    fieldNameParts[0] + "." : "";

                    var dateStartValue;
                    var dateEndValue;

                    // Shift to fill in the whole day
                    dateStart.startOf("day");
                    dateEnd.endOf("day");

                    // Set date start
                    if (condition.useValueOfDates) {
                        dateStartValue = getValueForType(dateStart.valueOf(), "date");
                    } else {
                        dateStartValue = "'" + dateStart.format("YYYY-MM-DD") + " 00:00:00'";
                    }

                    // Set date end
                    if (condition.useValueOfDates) {
                        dateEndValue = getValueForType(dateEnd.valueOf(), "date");
                    } else {
                        dateEndValue = "'" + dateEnd.format("YYYY-MM-DD") + " 23:59:59'";
                    }

                    var sql;

                    if (dateStartValue === dateEndValue) {
                        sql = fieldName + " = " + dateEndValue;
                    } else {
                        sql = [fieldName, "IS NOT NULL AND", fieldName, ">=", dateStartValue, "AND", fieldName, "<=",
                            dateEndValue].join(" ");
                    }

                    if (condition.usePartitions !== false) {
                        var partitions = utils.date.getDatePartitionSql(dateStart, dateEnd, tableName + "yearmonthday");
                        sql = partitions + " AND " + sql;
                    }

                    return sql;
                }, endsWith: function (value, fieldName) {
                    return "lcase(" + fieldName + ") LIKE '%" + value.toLowerCase() + "'";
                }, greaterThan: function (value, fieldName, condition) {
                    return getDeltaCondition(value, fieldName, condition);
                }, greaterThanOrEquals: function (value, fieldName, condition) {
                    return getDeltaCondition(value, fieldName, condition);
                }, hasValue: function (value, fieldName, condition) {
                    var sql = fieldName + " IS NOT NULL";
                    if (condition.type !== "number" && condition.type !== "boolean") {
                        sql += " AND " + fieldName + " != \"\"";
                    }

                    return sql;
                }, hasNoValue: function (value, fieldName, condition) {
                    var sql = fieldName + " IS NULL";
                    if (condition.type !== "number" && condition.type !== "boolean") {
                        sql += " OR " + fieldName + " = \"\"";
                    }

                    return sql;
                }, "in": function (value, fieldName) {
                    return fieldName + " IS NOT NULL AND " + fieldName + " IN (" + value + ")";
                }, lesserThan: function (value, fieldName, condition) {
                    return getDeltaCondition(value, fieldName, condition);
                }, lesserThanOrEqual: function (value, fieldName, condition) {
                    return getDeltaCondition(value, fieldName, condition);
                }, regexp: function (value, fieldName) {
                    return fieldName + " REGEXP '" + value + "'";
                }, startsWith: function (value, fieldName) {
                    return "lcase(" + fieldName + ") LIKE '" + value.toLowerCase() + "%'";
                }
            };

            function getPartitionStamp (value, operator) {
                var momentValue;

                if (isNaN(value)) {
                    momentValue = utils.date.getMoment(value);
                } else {
                    momentValue = utils.date.getMoment(Number(value));
                }

                return utils.date.getDatePartitionSql(momentValue, null, "yearmonthday", false, operator);
            }

            function getDeltaCondition (value, fieldName, condition) {
                var operator = operatorTypesParamPrefixes[condition.operator];
                var sql = "(" + fieldName + " IS NOT NULL AND ";
                if (condition.type === "date") {
                    if (condition.usePartitions !== false) {
                        sql += getPartitionStamp(value, operator) + " AND ";
                    }

                    sql += fieldName + " " + operator + " " +
                        (condition.valueField || getValueForType(value, condition.type, condition.operator));
                } else {
                    sql += fieldName + " " + operator + " " + (condition.valueField || value);
                }

                return sql + ")";
            }

            function getValueForType (value, type, conditionOperator) {
                if (type === "date") {
                    if (typeof value === "string") {
                        if (/^\d+$/.test(value)) {
                            var momentValue = utils.date.getMoment(Number(value));

                            if (conditionOperator) {
                                if (conditionOperator === "greaterThanOrEquals" ||
                                    conditionOperator === "greaterThan") {

                                    momentValue.startOf("day");
                                } else if (conditionOperator === "lesserThanOrEqual" ||
                                    conditionOperator === "greaterThan") {

                                    momentValue.endOf("day");
                                }
                            }

                            return "cast(" + Math.floor(momentValue.valueOf() / 1000) + " as timestamp)";
                        } else if (/^\d{4}-\d{2}-\d{2}$/.test(value)) {
                            return "'" + value + "'";
                        }
                    }
                }

                return value;
            }

            var operatorsIndex = {};
            function populateOperatorsIndex (operator) {
                operatorsIndex[operator.name] = {
                    sql: operator.sql || operator.display,
                    display: operator.display,
                    requiresValue: operator.requiresValue,
                    text: operator.text
                };
            }
            for (var operatorType in operators) {
                if (operators.hasOwnProperty(operatorType)) {
                    angular.forEach(operators[operatorType].operators, populateOperatorsIndex);
                }
            }

            function getOrConditionsIndex (conditions) {
                var orConditionsValues = {}, index = {};

                angular.forEach(conditions, function (condition) {
                    var orConditionValue = orConditionsValues[condition.field];
                    if (orConditionValue === undefined) {
                        orConditionsValues[condition.field] = 0;
                    } else {
                        orConditionsValues[condition.field]++;
                    }
                });

                for (var field in orConditionsValues) {
                    if (orConditionsValues.hasOwnProperty(field)) {
                        if (orConditionsValues[field]) {
                            index[field] = false;
                        }
                    }
                }

                return index;
            }

            function hasConditionsWithValue (filter) {
                if (filter.type === "search") {
                    return true;
                }

                if (!filter.conditions || !filter.conditions.length) {
                    return false;
                }

                var conditionConfig;
                for (var i = 0, condition; (!!(condition = filter.conditions[i])); i++) {
                    conditionConfig = methods.getOperator(condition.operator);
                    if (!conditionConfig.requiresValue) {
                        return true;
                    }

                    if (condition.valueField) {
                        return true;
                    }

                    if (condition.value || condition.value === 0 || condition.value === false ||
                        condition.values && condition.values.length) {
                        return true;
                    }
                }

                return false;
            }

            function filterValuesToConditions (operator, filterValues, fieldId, entities, innerAnd, availableEntities) {
                var paramParser = paramParsers[operator] ||
                        paramParsers.default, conditionsArr = [],
                    entitiesDictionary = utils.objects.arrayToObject(entities, "id"), entitiesFiltersIndex = {};

                function addCondition (conditionValue) {
                    var valueField = getValueField(getParamValueSqlField(conditionValue, availableEntities,
                            true)), value = valueField ? null :
                            paramParser(conditionValue), fieldIdParts = fieldId.split("."),
                        fieldIdWithoutEntity = fieldIdParts[fieldIdParts.length - 1],
                        entityId = fieldIdParts[0], filter = entitiesFiltersIndex[entityId] &&
                            entitiesFiltersIndex[entityId][fieldIdWithoutEntity];

                    if (!filter) {
                        entitiesFiltersIndex[entityId] =
                            utils.objects.arrayToObject(entitiesDictionary[entityId].filters, "id");
                        filter = entitiesFiltersIndex[entityId][fieldIdWithoutEntity];
                    }

                    conditionsArr.push({
                        field: filter.column || fieldIdWithoutEntity,
                        table: fieldIdParts.length > 1 && !filter.column ?
                        entitiesDictionary[entityId].tableAlias || entitiesDictionary[entityId].table : null,
                        operator: operator,
                        valueField: valueField,
                        value: value,
                        or: true,
                        type: filter.type,
                        usePartitions: entitiesDictionary[entityId].usePartitions,
                        useValueOfDates: entitiesDictionary[entityId].useValueOfDates,
                        innerAnd: innerAnd
                    });
                }


                if (operator === "in") {
                    var inValues = [];
                    filterValues.forEach(function (value) {
                        if (!getValueField(value)) {
                            inValues.push("\"" + value + "\"");
                        }
                    });
                    addCondition(inValues.join(","));
                } else {
                    filterValues.forEach(addCondition);
                }

                return conditionsArr;
            }

            function getValueField (value) {
                if (typeof(value) !== "string") {
                    return null;
                }

                var fieldMatch = value.match(/^\[(\w+\.\w+)\]$/);
                return fieldMatch ? fieldMatch[1] : null;
            }

            function getFilterParamName (filter) {
                if (filter.type === "search") {
                    return "search." + filter.id;
                }

                return (filter.entityId || filter.entity.id) + "." + (filter.id || filter.filterId);
            }

            function paramsToEntityFilters (entity, params, availableEntities) {
                var filtersValues = {};

                function getParamValues (filter, paramOperatorValues) {
                    var operator = getQueryParamOperator(paramOperatorValues), paramValues = operator === "equals" ?
                            paramOperatorValues :
                            paramOperatorValues.substr(operatorTypesParamPrefixes[operator].length),
                        paramValuesParser = paramParsers[operator] || paramParsers.default,
                        tempParamValues = paramValues.replace(/\,\,/g, "**;;**");

                    paramValues = tempParamValues.split(",");

                    angular.forEach(paramValues, function (value, i) {
                        value = value.replace(/\*\*\;\;\*\*/g, ",");
                        var paramValue = getParamValueSqlField(value, availableEntities);

                        if (!paramValue) {
                            if (filter.type === "string" || filter.type === "date" || filter.type === "range") {
                                paramValue = paramValuesParser(value.replace(/_;_/g, ",").replace(/_;;_/g, "|"));
                            } else if (filter.type === "number" || filter.type === "hours") {
                                paramValue = parseFloat(value);
                                if (isNaN(paramValue)) {
                                    paramValue = null;
                                }
                            } else if (filter.type === "boolean") {
                                paramValue = value.toLowerCase() === "true";
                            }

                        }

                        paramValues[i] = paramValue;
                    });

                    var paramName = getParamName(filter), filterValues = filtersValues[paramName];

                    if (!filterValues) {
                        filterValues = filtersValues[paramName] = {};
                    }

                    filterValues[operator] = paramValues;
                }

                function getBulkFilters () {
                    var values = ["true", "false"];

                    values.forEach(function (value) {
                        var bulkFields = params[entity.id + "._" + value];
                        if (bulkFields) {
                            bulkFields.split(",").forEach(function (field) {
                                params[entity.id + "." + field] = value;
                            });
                        }
                    });
                }

                function getParamName (filter) {
                    return entity.id + "." + filter.id;
                }
                getBulkFilters();

                angular.forEach(entity.filters, function (filter) {
                    var paramName = getParamName(filter), paramValue = params[paramName];

                    if (paramValue !== undefined && paramValue !== null) {
                        var paramOperators = angular.isArray(paramValue) ? paramValue :
                            typeof(paramValue) === "string" ? paramValue.split("|") : [String(paramValue)];
                        paramOperators.forEach(function (paramOperator) {
                            getParamValues(filter, paramOperator);
                        });

                        if (params[paramName + ".or"]) {
                            filtersValues[paramName].andOr = "or";
                        }

                        if (params[paramName + "_conjuction"]) {
                            filtersValues[paramName].innerAnd =
                                params[paramName + "_conjuction"].toLowerCase() === "and";
                        }
                    }
                });


                return filtersValues;
            }

            function getParamValueSqlField (value, availableEntities, useTable) {
                var fieldMatch = typeof(value) === "string" && value.match(/^\[(\w+)(?:\.(\w+))?\]$/);

                if (!fieldMatch) {
                    return null;
                }

                var entity;
                var fieldEntity = fieldMatch[2] ? getEntityTableName(fieldMatch[1], availableEntities) || entity :
                    entity;
                var sqlField = fieldEntity[useTable ? "table" : "id"] + ".", fieldName = fieldMatch[2] ||
                    fieldMatch[1], entitySqlField = getEntityFieldName(fieldEntity, fieldName);

                if (!entitySqlField) {
                    throw new Error("Invalid field, " + fieldName);
                }

                return "[" + sqlField + entitySqlField + "]";
            }

            function getEntityTableName (entityName, availableEntities) {
                for (var i = 0, entity; (!!(entity = availableEntities[i])); i++) {
                    if (entity.id === entityName) {
                        return entity;
                    }
                }

                return null;
            }

            function getEntityFieldName (entity, fieldId) {
                for (var i = 0, entityField; (!!(entityField = entity.fields[i])); i++) {
                    if (entityField.id === fieldId) {
                        return entityField.column || entityField.id;
                    }
                }

                return null;
            }

            var methods = {
                conditionsToSql: function (conditions) {
                    var sql = [], conditionFields = {}, fieldSql = {}, fieldSqlClauses, fieldConjuctions;

                    angular.forEach(conditions, function (condition) {
                        if (condition.sql) {
                            sql.push(condition.sql);
                        } else if (condition.field) {
                            var field = conditionFields[condition.field];
                            if (!field) {
                                field = conditionFields[condition.field] = [];
                                fieldSql[condition.field] = [];
                            }

                            field.push(condition);
                        }
                    });

                    function populateFieldConjuctions (condition) {
                        var conditionValue = condition.value;
                        var getValueFunction = operatorsSqlValue[condition.operator],
                            orCondition, conditionTable = condition.table ? condition.table + "." : "",
                            conditionField = conditionTable + condition.field;

                        fieldConjuctions.push(condition.innerAnd ? "AND" : "OR");

                        if (getValueFunction) {
                            orCondition = getValueFunction(conditionValue, conditionField, condition);
                        } else {
                            if (typeof(conditionValue) === "string") {
                                conditionValue = "\"" + conditionValue + "\"";
                            }

                            orCondition = [conditionField, operatorsIndex[condition.operator].sql,
                                condition.valueField || conditionValue].join(" ");
                        }

                        if (orCondition && !~fieldSql[fieldName].indexOf(orCondition)) {
                            fieldSql[fieldName].push(orCondition);
                        }
                    }

                    function populateFieldSqlClauses (fieldClause, i) {
                        if (i) {
                            fieldSqlClauses.push(" " + fieldConjuctions[i] + " ");
                        }
                        fieldSqlClauses.push(fieldClause);
                    }

                    for (var fieldName in conditionFields) {
                        if (conditionFields.hasOwnProperty(fieldName)) {
                            fieldConjuctions = [];
                            fieldSqlClauses = [];

                            angular.forEach(conditionFields[fieldName], populateFieldConjuctions);

                            fieldSqlClauses.push("(");
                            fieldSql[fieldName].forEach(populateFieldSqlClauses);
                            fieldSqlClauses.push(")");
                            sql.push(fieldSqlClauses.join(""));
                        }
                    }

                    return sql.join(" AND ");
                },
                conditionToString: function (condition, filter) {
                    var operator = methods.getOperator(condition.operator), operatorDisplay = operator.text !==
                        undefined ? operator.text :
                            operator.display, conditionText = [operatorDisplay.toLowerCase()],
                        valueWrapper = filter.type === "string" ? "'" : "";

                    if (operator.requiresValue) {
                        if (condition.values && condition.values.length) {
                            valueWrapper = typeof condition.values[0] === "string" ? "'" : "";
                            conditionText.push(utils.arrays.toSentence(condition.values, filter.conjuction,
                                valueWrapper));
                        } else {
                            var valueField = condition.valueField || getValueField(condition.value);
                            if (valueField) {
                                conditionText.push(valueField);
                            } else if (operatorsToString[condition.operator]) {
                                conditionText.push(operatorsToString[condition.operator](condition.value));
                            } else {
                                if (condition.filterType === "date" || condition.filterType === "dateSelect") {
                                    if (condition.timeStart && condition.timeEnd) {
                                        conditionText.push(operatorsToString.dateRange(condition.value));
                                    } else {
                                        var m = utils.date.getMoment(condition.value);
                                        if (m.isValid()) {
                                            conditionText.push(m.format("YYYY-MM-DD"));
                                        } else {
                                            conditionText.push("BAD DATE");
                                        }
                                    }
                                } else if (condition.filterType === "hoursSelect") {
                                    conditionText.push(condition.value / 3600 + ":00");
                                } else if (condition.filterType === "range") {
                                    conditionText.push(condition.value.min + filter.settings.labelSuffix + " ≤ " +
                                        filter.name + " ≤ " + condition.value.max + filter.settings.labelSuffix);
                                } else {
                                    if (angular.isDate(condition.value)) {
                                        conditionText.push(utils.date.getMoment(condition.value).format("YYYY-MM-DD"));
                                    } else {
                                        conditionText.push(condition.value !== null && condition.value !== undefined ?
                                        " " + valueWrapper + condition.value + valueWrapper : "(none)");
                                    }
                                }
                            }
                        }
                    }

                    return conditionText.join(" ");
                },
                filterHasConditionsWithValue: hasConditionsWithValue,
                filtersToSqlParams: function (filters, entities, entitiesJoin, savedSearches, sqlConditions,
                                              availableEntities) {


                    function getJoins () {
                        var joins = [];

                        if (entitiesJoin) {
                            entitiesJoin.forEach(function (entityJoin) {
                                if (joinRequired(entityJoin)) {
                                    var join = {caseSensitive: entityJoin._caseSensitive}, i = 1;

                                    for (var p in entityJoin) {
                                        if (entityJoin.hasOwnProperty(p)) {
                                            if (p[0] === "_") {
                                                continue;
                                            }

                                            join["table" + i] = entitiesDictionary[p].table;
                                            join["field" + i] = entityJoin[p];
                                            i++;
                                        }
                                    }
                                    joins.push(join);
                                }
                            });
                        }
                        return joins;
                    }

                    function joinRequired (entityJoin) {
                        var i, entity, entityFound;

                        for (var p in entityJoin) {
                            if (entityJoin.hasOwnProperty(p)) {
                                entityFound = false;
                                if (p[0] === "_") {
                                    continue;
                                }
                                for (i = 0; (!!(entity = entities[i])); i++) {
                                    if (entity.id === p) {
                                        entityFound = true;
                                        break;
                                    }
                                }

                                if (!entityFound) {
                                    return false;
                                }
                            }
                        }

                        return true;
                    }

                    var sqlParams = {
                        whereClauses: []
                    }, entitiesDictionary = utils.objects.arrayToObject(entities,
                        "id"), joins = getJoins(), filterConditions;


                    if (savedSearches && savedSearches.length) {
                        sqlParams.whereClauses =
                            sqlParams.whereClauses.concat(methods.savedSearchesToFilters(savedSearches, entities,
                                entitiesJoin));
                    }

                    if (sqlConditions && sqlConditions.length) {
                        var sqlConditionsData = [];
                        if (!angular.isArray(sqlConditions)) {
                            sqlConditions = [sqlConditions];
                        }

                        sqlConditions.forEach(function (sqlCondition) {
                            sqlConditionsData.push({
                                sql: sqlCondition
                            });
                        });

                        sqlParams.whereClauses.push({
                            conditions: sqlConditionsData
                        });
                    }

                    function setFiltersCondition (filterValues, index) {
                        if (index !== "andOr" && index !== "innerAnd") {
                            var operator = angular.isString(index) ? index :
                                getQueryParamOperator(filterValues);

                            if (!angular.isArray(filterValues)) {
                                filterValues = [filterValues];
                            }

                            filterConditions.conditions =
                                filterConditions.conditions.concat(filterValuesToConditions(operator,
                                    filterValues,
                                    fieldId, entities, !!filters[fieldId].innerAnd, availableEntities));
                        }
                    }

                    for (var fieldId in filters) {
                        if (filters.hasOwnProperty(fieldId)) {
                            filterConditions = {conditions: [], isOr: !!filters[fieldId].andOr};

                            angular.forEach(filters[fieldId], setFiltersCondition);

                            sqlParams.whereClauses.push(filterConditions);
                        }
                    }

                    if (joins.length) {
                        sqlParams.join = joins;
                    }

                    return sqlParams;
                },
                filtersToParams: function (filters, entities) {
                    var params = {};

                    if (!filters) {
                        return params;
                    }

                    if (!angular.isArray(filters)) {
                        filters = [filters];
                    }

                    entities.forEach(function (entity) {
                        params[entity.id + "_page"] = null;
                    });

                    angular.forEach(filters, function (filter) {
                        var filterParamName = getFilterParamName(filter);

                        if (filter.enabled && methods.filterHasConditionsWithValue(filter)) {
                            if (filter.type === "search") {
                                params[filterParamName] = 1;
                            } else {
                                var filterValues = [];

                                var conditionOperators = {};

                                filter.conditions.forEach(function (condition) {
                                    var conditionOperator = condition.valueField && condition.operator === "in" ?
                                            "equals" :
                                            condition.operator, operator = conditionOperators[conditionOperator],
                                        values = condition.values ? angular.copy(condition.values) : [];

                                    if (condition.valueField) {
                                        values.push("[" + condition.valueField + "]");
                                    }

                                    if (condition.value !== null && condition.value !== undefined) {
                                        values.push(condition.value);
                                    }

                                    if (!operator) {
                                        operator = conditionOperators[conditionOperator] = [];
                                    }

                                    if (values && values.length) {
                                        values.forEach(function (value) {
                                            if (value !== null && value !== undefined) {
                                                var strValue = operatorsToParamValues[conditionOperator] ?
                                                    operatorsToParamValues[conditionOperator](value) :
                                                    angular.isString(value) ? utils.url.formatStringForParam(value) :
                                                        value;

                                                if (!~operator.indexOf(strValue)) {
                                                    operator.push(strValue);
                                                }
                                            }
                                        });
                                    }
                                });

                                for (var operatorName in conditionOperators) {
                                    if (conditionOperators.hasOwnProperty(operatorName)) {
                                        filterValues.push((operatorTypesParamPrefixes[operatorName] || "") +
                                            conditionOperators[operatorName].join(","));
                                    }
                                }

                                if (filterValues.length) {
                                    params[filterParamName] = filterValues.join("|");
                                    if (filter.andOr === "or") {
                                        params[filterParamName + ".or"] = 1;
                                    }
                                } else {
                                    params[filterParamName] = null;
                                }
                            }
                        } else {
                            params[filterParamName] = null;
                        }
                    });

                    return params;
                },
                filterStringToValue: function (filterType, value) {
                    var convertor = stringToOperators[filterType];
                    return convertor ? convertor(value) : value;
                },
                getFilterParamName: getFilterParamName,
                getOperator: function (operatorName) {
                    return operatorsIndex[operatorName];
                },
                getParamOperator: function (operatorName) {
                    return operatorTypesParamPrefixes[operatorName];
                },
                getUsedEntities: function (entities, savedSearches, params) {
                    var filters = this.paramsToFilters(entities, params), usedEntities = [], usedEntityIds = {};

                    savedSearches.forEach(function (search) {
                        search.entities.forEach(function (entityId) {
                            usedEntityIds[entityId] = true;
                        });
                    });

                    var entityMatchRegExp = /^(.*)\./, operator, operatorValues;

                    function setUsedEntityId (value) {
                        var valueField = getValueField(value);
                        if (valueField) {
                            usedEntityIds[valueField.split(".")[0]] = true;
                        }
                    }

                    for (var filterName in filters) {
                        if (filters.hasOwnProperty(filterName)) {
                            usedEntityIds[filterName.match(entityMatchRegExp)[1]] = true;
                            for (operator in filters[filterName]) {
                                if (filters[filterName].hasOwnProperty(operator)) {
                                    operatorValues = filters[filterName][operator];
                                    if (angular.isArray(operatorValues)) {
                                        operatorValues.forEach(setUsedEntityId);
                                    }
                                }
                            }
                        }
                    }

                    entities.forEach(function (entity) {
                        if (usedEntityIds[entity.id]) {
                            var entityCopy = angular.copy(entity), minScore = entity.scoreField &&
                                params[entity.id + "." + entity.scoreField];

                            if (minScore) {
                                if (typeof(minScore) === "string") {
                                    minScore = minScore.match(/^\>\=?(\d+)/);
                                }

                                if (minScore) {
                                    if (!angular.isNumber(minScore)) {
                                        minScore = parseInt(minScore[1], 10);
                                    }

                                    if (!isNaN(minScore)) {
                                        entityCopy.table = utils.strings.getEventsTableName(entity.table, minScore);
                                        if (entityCopy.table !== entity.table) {
                                            entityCopy.tableAlias = entity.table;
                                        }
                                    }
                                }
                            }

                            usedEntities.push(entityCopy);
                        }
                    });

                    return usedEntities;
                },
                getValueField: getValueField,
                operators: operators,
                get operatorsIndex () {
                    return angular.copy(operatorsIndex);
                },
                paramsToSqlParams: function (usedEntities, entitiesJoin, savedSearches, params, sqlConditions,
                                             availableEntities) {
                    return methods.filtersToSqlParams(methods.paramsToFilters(usedEntities, params, availableEntities),
                        usedEntities, entitiesJoin, savedSearches, sqlConditions, availableEntities);
                },
                paramsToFilters: function (usedEntities, params, availableEntities) {
                    var filtersValues = {};
                    availableEntities = availableEntities || usedEntities;

                    usedEntities.forEach(function (entity) {
                        angular.extend(filtersValues, paramsToEntityFilters(entity, params, availableEntities));
                    });

                    return filtersValues;
                },
                savedSearchesToFilters: function (savedSearches, entities, entitiesJoin) {
                    var filters = [];

                    savedSearches.forEach(function (search) {
                        if (search.enabled === undefined) {
                            search.enabled = true;
                        }

                        search.type = "search";
                        search.filters.forEach(function (filter) {
                            filter.enabled = true;
                        });

                        var params = methods.filtersToParams(search.filters,
                            entities), filterObj = methods.paramsToSqlParams(entities, entitiesJoin,
                            search.savedSearches || [], params, [], entities);

                        filterObj.isOr = search.andOr === "or";
                        filters.push(filterObj);
                    });

                    return filters;
                },
                validateCondition: function (value1, operator, value2) {
                    var validation = validations[operator];
                    if (!validation) {
                        throw new Error("Invalid operator for validation: '" + operator + "'.");
                    }

                    return validation(value1, value2);
                },
                /**
                 * Gets an array of conditions and returns true/false, whether the conditions are met or not.
                 * @param conditions Array of conditions
                 * @param data Data object to use in condition checkers
                 * @param params State object
                 * @returns {boolean}
                 */
                validateConditions: function (conditions, data, params) {
                    if (!conditions) {
                        return true;
                    }

                    if (!angular.isArray(conditions)) {
                        conditions = [conditions];
                    }

                    var conditionValue, conditionField, dataValue, paramMatch,
                        orConditionsValues = getOrConditionsIndex(conditions);

                    for (var i = 0, condition; (!!(condition = conditions[i])); i++) {
                        conditionValue = condition.value;
                        if (conditionValue !== undefined) {
                            if (/^@/.test(condition.value)) {
                                conditionValue = data[condition.value];
                            } else if (params) {
                                if (!condition.value.match) {
                                    conditionValue = condition.value;
                                } else if (!!(paramMatch = condition.value.match(/^\{\{([^\}]+)\}\}$/))) {
                                    conditionValue = params[paramMatch[1]];
                                }
                            }
                        }

                        conditionField = condition.field;

                        if (conditionField) {
                            if (!!(paramMatch = conditionField.match(/^\{\{([^\}]+)\}\}$/))) {
                                dataValue = params[paramMatch[1]];
                            } else {
                                dataValue = utils.objects.getObjectByPath(data, conditionField);
                            }

                            if (condition.fieldType) {
                                dataValue = format[condition.fieldType](dataValue, {});
                            }
                        }

                        var conditionResult = methods.validateCondition(dataValue, condition.operator,
                            conditionValue), orConditionsValue = orConditionsValues[condition.field];

                        if (orConditionsValue !== undefined) {
                            orConditionsValues[condition.field] = orConditionsValue | conditionResult;
                        } else if (!conditionResult) {
                            return false;
                        }
                    }

                    for (var field in orConditionsValues) {
                        if (orConditionsValues.hasOwnProperty(field)) {
                            if (!orConditionsValues[field]) {
                                return false;
                            }
                        }
                    }

                    return true;
                }
            };

            return methods;
        }]);
}());
