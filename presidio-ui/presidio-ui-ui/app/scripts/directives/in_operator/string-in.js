(function () {
    'use strict';

    angular.module("StringInModule", []).directive("stringIn", function () {
        return {
            restrict: "E",
            templateUrl: "scripts/directives/in_operator/string_in_template.html",
            replace: true,
            require: "?ngModel",
            scope: {
                inValues: '=ngModel',
                isSearchable: '=',
                searchSettings: '='
            },
            link: function ($scope) {

                function initValues () {
                    for (var i = 0; i < $scope.inValues.length; i++) {
                        var val = $scope.inValues[i];
                        $scope.vals.push({enabled: true, val: val});
                    }
                }

                function isInValExist (val) {
                    for (var i = 0; i < $scope.vals.length; i++) {
                        if ($scope.vals[i].val === val) {
                            return true;
                        }
                    }
                    return false;
                }

                function getAllEnabledValues () {
                    var arr = [];
                    for (var i = 0; i < $scope.vals.length; i++) {
                        var stringInVal = $scope.vals[i];
                        if (stringInVal.enabled) {
                            arr.push(stringInVal.val);
                        }
                    }
                    return arr;
                }

                $scope.vals = [];
                if ($scope.inValues) {
                    initValues();
                }

                $scope.onStringInEnter = function (event) {
                    if (event.which === 13) {
                        var element = angular.element(event.target);
                        var val = element.val();
                        if (val && !isInValExist(val)) {
                            $scope.vals.push({enabled: true, val: val});
                            $scope.inValues = getAllEnabledValues();
                        }
                        element.val("");
                        event.preventDefault();
                    }
                };

                $scope.onStringInSearchSelect = function ($value) {
                    if ($value && !isInValExist($value)) {
                        $scope.vals.push({enabled: true, val: $value});
                        $scope.inValues = getAllEnabledValues();
                    }
                };

                $scope.removeStringInVal = function (val, index) {
                    $scope.vals.splice(index, 1);
                    $scope.inValues = getAllEnabledValues();
                };

                $scope.onCheckboxClick = function () {
                    $scope.inValues = getAllEnabledValues();
                };

            }
        };
    });

}());
