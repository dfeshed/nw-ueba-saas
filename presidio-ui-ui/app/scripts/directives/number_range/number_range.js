(function () {
    'use strict';

    angular.module("NumberRangeModule", ["NumbersOnlyModule"]).directive("numberRange", function () {
        return {
            restrict: "E",
            templateUrl: "scripts/directives/number_range/number_range_template.html",
            replace: true,
            require: "?ngModel",
            scope: {
                minVal: '=',
                maxVal: '='
            },
            link: function ($scope, element, attrs, ngModelCtrl) {

                function initScopeVars () {
                    $scope.minPlaceholderValue = $scope.minVal ? "> " + $scope.minVal : "From";
                    $scope.maxPlaceholderValue = $scope.maxVal ? "< " + $scope.maxVal : "To";
                }

                ngModelCtrl.$render = function () {
                    $scope.value = angular.copy(ngModelCtrl.$viewValue);
                };

                initScopeVars();

                $scope.validateInput = function () {
                    ngModelCtrl.$setViewValue(angular.copy($scope.value));

                };
            }
        };
    });

}());
