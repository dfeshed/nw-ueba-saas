angular.module("Fortscale").factory("controls", ["utils", function(utils){
    var controlInitMethods = {
        link: function(control, params){
            if (!control._href)
                control._href = control.href;

            control.href = utils.strings.parseValue(control._href, {}, params);
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