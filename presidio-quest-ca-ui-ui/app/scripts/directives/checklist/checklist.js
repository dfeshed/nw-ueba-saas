(function () {
    'use strict';

    angular.module("Fortscale").directive("checklist", [function () {
        return {
            restrict: 'E',
            templateUrl: "scripts/directives/checklist/checklist.template.html",
            replace: true,
            require: '?ngModel',
            scope: {
                items: "=",
                _onChange: "&onChange",
                defaultValue: "=",
                allowNone: "=",
                useItemParams: "="
            },
            link: function postLink (scope, element, attrs, ngModel) {
                ngModel.$render = function () {
                    if (!ngModel.$viewValue) {
                        return;
                    }

                    var selectedItemsValues = ngModel.$viewValue || [];
                    if (typeof(selectedItemsValues) === "string") {
                        selectedItemsValues = selectedItemsValues.split(",");
                    }

                    scope.items.forEach(function (item) {
                        item.checked = !!~selectedItemsValues.indexOf(scope.useItemParams ? item.param : item.value);
                    });
                };

                scope.$watch("items", function (items) {
                    items.forEach(function (item) {
                        if (item.checked === undefined) {
                            item.checked = !!scope.defaultValue;
                        }
                    });
                });

                scope.selectAll = function () {
                    scope.items.forEach(function (item) {
                        item.checked = true;
                    });
                    scope.onChange();
                };

                scope.selectNone = function () {
                    scope.items.forEach(function (item) {
                        item.checked = false;
                    });
                    scope.onChange();
                };

                scope.onChange = function () {
                    var selectedItemsValues = [],
                        allSelected = true;

                    scope.items.forEach(function (_item) {
                        if (_item.checked) {
                            selectedItemsValues.push(scope.useItemParams ? _item.param || _item.value : _item.value);
                        } else {
                            allSelected = false;
                        }
                    });

                    if (!selectedItemsValues.length) {
                        selectedItemsValues = null;
                    }

                    ngModel.$setViewValue(selectedItemsValues, scope);

                    if (scope._onChange) {
                        scope._onChange(selectedItemsValues);
                    }
                };
            }
        };
    }]);
}());
