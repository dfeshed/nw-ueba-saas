(function () {
    'use strict';

    angular.module("NumbersOnlyModule", []).directive("numbersOnly", function () {
        return {
            restrict: "A",
            require: 'ngModel',
            link: function ($scope, element, $attrs, ngModelCtrl) {
                ngModelCtrl.$parsers.push(function (inputValue) {
                    if (inputValue === undefined) {
                        return '';
                    }
                    var transformedInput = inputValue.replace(/[^\d.]/g, '');
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
