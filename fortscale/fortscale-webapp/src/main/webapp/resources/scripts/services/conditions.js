angular.module("Fortscale").factory("conditions", ["format", function(format){

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
        { name: "equals", display: "=", requiresValue: true, types: ["string", "number"] },
        { name: "notEquals", display: "!=", requiresValue: true, types: ["string", "number"] },
        { name: "greaterThan", display: ">", requiresValue: true, types: ["number"] },
        { name: "greaterThanOrEqual", display: ">=", requiresValue: true, types: ["number"] },
        { name: "lesserThan", display: "<", requiresValue: true, types: ["number"] },
        { name: "lesserThanOrEqual", display: "<=", requiresValue: true, types: ["number"] },
        { name: "included", display: "IN", requiresValue: true, types: ["number"] },
        { name: "hasValue", display: "Has value", requiresValue: false, types: ["string", "number"] },
        { name: "hasNoValue", display: "Has no value", requiresValue: false, types: ["string", "number"] }
    ];

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