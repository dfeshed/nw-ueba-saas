angular.module("Conditions", ["Format", "Transforms"]).factory("conditions", ["format", "utils", function(format, utils){

    var validations = {
        contains: function(val1, val2){
            if (validations.hasNoValue(val1) || validations.hasNoValue(val2))
                return false;

            var regexp = new RegExp(val2, "i");
            return regexp.test(val1);
        },
        equals: function(val1, val2){
            if (angular.isObject(val1) && angular.isObject(val2))
                return val1 === val2;

            if ((val1 === null || val1 === undefined) && (val2 === null || val2 === undefined))
                return true;

            if (((val1 === null || val1 === undefined) && (val2 !== null && val2 !== undefined )) || ((val1 !== null && val1 !== undefined) && (val2 === undefined || val2 === undefined)))
                return false;

            return val1.toString() === val2.toString();
        },
        notEquals: function(val1, val2){
            return val1 !== val2;
        },
        greaterThan: function(val1, val2){
            return val1 > val2;
        },
        greaterThanOrEqual: function(val1, val2){
            return val1 >= val2;
        },
        lesserThan: function(val1, val2){
            return val1 < val2;
        },
        lesserThanOrEqual: function(val1, val2){
            return val1 <= val2;
        },
        included: function(val1, arr){
            return !!~arr.indexOf(val1);
        },
        hasValue: function(value){
            return value !== undefined && value !== null;
        },
        hasNoValue: function(value){
            return !validations.hasValue(value);
        }
    };

    var operators = [
        { name: "contains", display: "Contains", requiresValue: true, types: ["string"] },
        { name: "equals", display: "=", requiresValue: true, types: ["string", "number"] },
        { name: "notEquals", display: "!=", requiresValue: true, types: ["string", "number"] },
        { name: "greaterThan", display: ">", requiresValue: true, types: ["number"] },
        { name: "greaterThanOrEqual", display: ">=", requiresValue: true, types: ["number"] },
        { name: "lesserThan", display: "<", requiresValue: true, types: ["number"] },
        { name: "lesserThanOrEqual", display: "<=", requiresValue: true, types: ["number"] },
        { name: "included", display: "IN", requiresValue: true, types: ["number"] },
        { name: "hasValue", display: "Has value", sql: "IS NOT NULL", requiresValue: false, types: ["string", "number"] },
        { name: "hasNoValue", display: "Has no value", sql: "IS NULL", requiresValue: false, types: ["string", "number"] },
        { name: "startsWith", display: "Starts With", requiresValue: true, types: ["string"] },
        { name: "endsWith", display: "Ends With", requiresValue: true, types: ["string"] },
        { name: "regexp", display: "RegExp", requiresValue: true, types: ["string"] },
        { name: "dateRange", display: "Dates", requiresValue: true, types: ["date"] }
    ];

    var operatorsSqlValue = {
        contains: function(value, fieldName){
            return "lcase(" + fieldName + ") LIKE '%" + value.toLowerCase() + "%'";
        },
        equals: function(value, fieldName){
            if (typeof(value) === "string"){
                return "lcase(" + fieldName + ") = \"" + value.toLowerCase() + "\"";
            }
            else
                return [fieldName, value].join(" = ");

        },
        dateRange: function(value, fieldName){
            var dateStart = utils.date.getMoment(angular.isObject(value) ? value.timeStart : value),
                dateEnd = utils.date.getMoment(angular.isObject(value) ? value.timeEnd : value);

            dateEnd.add("days", 1);

            return [fieldName, ">", "'" + dateStart.format("YYYY-MM-DD") + "'", "AND", fieldName, "<", "'" + dateEnd.format("YYYY-MM-DD") + "'"].join(" ");
        },
        endsWith: function(value, fieldName){
            return "lcase(" + fieldName + ") LIKE '%" + value.toLowerCase() + "'";
        },
        regexp: function(value, fieldName){
            return fieldName + " REGEXP '" + value + "'";
        },
        startsWith: function(value, fieldName){
            return "lcase(" + fieldName + ") LIKE '" + value.toLowerCase() + "%'";
        }
    };

    var operatorsSqlIndex = {};
    angular.forEach(operators, function(operator){
        operatorsSqlIndex[operator.name] = operator.sql || operator.display;
    });

    function getOrConditionsIndex(conditions){
        var orConditionsValues = {},
            index = {};


        angular.forEach(conditions, function(condition){
            var orConditionValue = orConditionsValues[condition.field];
            if (orConditionValue === undefined)
                orConditionsValues[condition.field] = 0;
            else
                orConditionsValues[condition.field]++;
        });

        for(var field in orConditionsValues){
            if (orConditionsValues[field])
                index[field] = false;
        }

        return index;
    }

    var methods = {
        conditionsToSql: function(conditions){
            var sql = [];
            angular.forEach(conditions, function(condition){
                var conditionValue = condition.value;
                var getValueFunction = operatorsSqlValue[condition.operator];
                if (getValueFunction)
                    sql.push(getValueFunction(conditionValue, condition.field));
                else{
                    if (typeof(conditionValue) === "string")
                        conditionValue = "\"" + conditionValue + "\"";

                    sql.push([condition.field, operatorsSqlIndex[condition.operator], conditionValue].join(" "));
                }
            });

            return sql.join(" AND ");
        },
        operators: operators,
        validateCondition: function(value1, operator, value2){
            var validation = validations[operator];
            if (!validation)
                throw new Error("Invalid operator for validation: '" + operator + "'.");

            return validation(value1, value2);
        },
        validateConditions: function(conditions, data, params){
            var conditionValue,
                conditionField,
                dataValue,
                paramMatch,
                orConditionsValues = getOrConditionsIndex(conditions);

            for(var i= 0, condition; condition = conditions[i]; i++){
                conditionValue = condition.value;
                if (conditionValue !== undefined){
                    if (/^@/.test(condition.value))
                        conditionValue = data[condition.value];
                    else if (params){
                        if (!condition.value.match)
                            conditionValue = condition.value;
                        else if(paramMatch = condition.value.match(/^\{\{([^\}]+)\}\}$/))
                            conditionValue = params[paramMatch[1]];
                    }
                }

                conditionField = condition.field;

                if (paramMatch = conditionField.match(/^\{\{([^\}]+)\}\}$/))
                    dataValue = params[paramMatch[1]];
                else
                    dataValue = data[conditionField];

                if (condition.fieldType)
                    dataValue = format[condition.fieldType](dataValue, {});

                var conditionResult = methods.validateCondition(dataValue, condition.operator, conditionValue),
                    orConditionsValue = orConditionsValues[condition.field];

                if (orConditionsValue !== undefined)
                    orConditionsValues[condition.field] = orConditionsValue | conditionResult;
                else if(!conditionResult)
                    return false;
            }

            for(var field in orConditionsValues){
                if (!orConditionsValues[field])
                    return false;
            }

            return true;
        }
    };

    return methods;
}]);