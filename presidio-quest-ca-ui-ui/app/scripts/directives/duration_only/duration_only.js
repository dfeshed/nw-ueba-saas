(function () {
    'use strict';

    angular.module("DurationOnlyModule", []).directive("durationOnly", function () {
        return {
            restrict: "A",
            require: 'ngModel',
            link: function ($scope, element, $attrs, ngModelCtrl) {
                //allows to enter the input field only digits and ':' - to support the format hh:mm:ss
                ngModelCtrl.$parsers.push(function (inputValue) {
                    if (inputValue === undefined) {
                        return '';
                    }
                    var transformedInput = inputValue.replace(/[^\d:]/g, '');
                    if (transformedInput !== inputValue) {
                        ngModelCtrl.$setViewValue(transformedInput);
                        ngModelCtrl.$render();
                    }
                    return transformedInput;
                });
            }
        };
    });


}());
