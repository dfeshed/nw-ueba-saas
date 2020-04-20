/**
 * This directive adds a validator to the attached ngModel. This validator is an ip validator.
 */

(function () {
    'use strict';
    function fsValidatorIp () {

        function linkFn ($scope, $element, attrs, ngModel) {
            ngModel.$validators.ip = function(modelValue, viewValue) {
                var value = modelValue || viewValue;
                /*jslint maxlen: 200 */
                return /^(\d|[1-9]\d|1\d\d|2([0-4]\d|5[0-5]))\.(\d|[1-9]\d|1\d\d|2([0-4]\d|5[0-5]))\.(\d|[1-9]\d|1\d\d|2([0-4]\d|5[0-5]))\.(\d|[1-9]\d|1\d\d|2([0-4]\d|5[0-5]))$/.test(value);
            };
        }

        return {
            restrict: 'A',
            link: linkFn,
            require: 'ngModel'
        };
    }

    fsValidatorIp.$inject = [];

    angular.module('Fortscale.layouts.reports')
    .directive('fsValidatorIp', fsValidatorIp);
}());
