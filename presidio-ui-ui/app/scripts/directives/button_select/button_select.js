(function () {
    'use strict';

    angular.module("Fortscale").directive("buttonSelect", [function () {
        return {
            restrict: 'E',
            templateUrl: "scripts/directives/button_select/button_select.template.html",
            replace: true,
            require: '?ngModel',
            scope: {
                "buttons": "="
            },
            link: function postLink (scope, element, attrs, ngModel) {

                function setSelectedButton () {
                    if (!scope.buttons) {
                        return;
                    }

                    scope.buttons.forEach(function (button) {
                        button.selected = String(ngModel.$viewValue) === String(button.value);
                        if (button.selected) {
                            selectedButton = button;
                        }
                    });
                }

                var selectedButton;

                ngModel.$render = setSelectedButton;
                scope.$watch("buttons", setSelectedButton);


                scope.selectButton = function (button) {
                    if (button === selectedButton) {
                        return false;
                    }

                    if (selectedButton) {
                        selectedButton.selected = false;
                    }

                    selectedButton = button;
                    button.selected = true;

                    ngModel.$setViewValue(button.value, scope);
                };
            }
        };
    }]);
}());
