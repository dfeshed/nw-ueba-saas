angular.module("Fortscale").factory("conditions", ["format", function(format){

    var validations = {
        equals: function(val1, val2){
            return val1 === val2;
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
        { name: "equals", display: "=" },
        { name: "notEquals", display: "!=" },
        { name: "greaterThan", display: ">" },
        { name: "greaterThanOrEqual", display: ">=" },
        { name: "lesserThan", display: "<" },
        { name: "lesserThanOrEqual", display: "<=" },
        { name: "included", display: "IN" },
        { name: "hasValue", display: "Has value" },
        { name: "hasNoValue", display: "Has no value" }
    ];

    var methods = {
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
                paramMatch;

            for(var i= 0, condition; condition = conditions[i]; i++){
                conditionValue = condition.value;
                if (conditionValue !== undefined && /^@/.test(condition.value))
                    conditionValue = data[condition.value];

                conditionField = condition.field;

                if (paramMatch = conditionField.match(/^\{\{([^\}]+)\}\}$/))
                    dataValue = params[paramMatch[1]];
                else
                    dataValue = data[conditionField];

                if (condition.fieldType)
                    dataValue = format[condition.fieldType](dataValue);

                if (!methods.validateCondition(dataValue, condition.operator, condition.value))
                    return false;
            }

            return true;
        }
    };

    return methods;
}]);