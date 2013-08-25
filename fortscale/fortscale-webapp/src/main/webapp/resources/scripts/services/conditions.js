angular.module("Fortscale").factory("conditions", [function(){

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
        }
    };

    var operators = [
        { name: "equals", display: "=" },
        { name: "notEquals", display: "!=" },
        { name: "greaterThan", display: ">" },
        { name: "greaterThanOrEqual", display: ">=" },
        { name: "lesserThan", display: "<" },
        { name: "lesserThanOrEqual", display: "<=" },
        { name: "included", display: "IN" }
    ];

    var methods = {
        validateCondition: function(value1, operator, value2){
            var validation = validations[operator];
            if (!validation)
                throw new Error("Invalid operator for validation: '" + operator + "'.");

            return validation(value1, value2);
        }
    };

    return methods;
}]);