angular.module("Fortscale").factory("controls", ["utils", function(utils){
    var controlInitMethods = {
        link: function(control, params){
            control.href = utils.strings.parseValue(control.href, {}, params);
        }
    };

    var methods = {
        initControl: function(control, params){
            var initMethod = controlInitMethods[control.type];
            if (initMethod)
                initMethod(control, params);
        }
    };

    return methods;
}]);