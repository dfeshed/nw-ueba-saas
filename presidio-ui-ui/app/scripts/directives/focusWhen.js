(function () {
    'use strict';

    angular.module("FocusWhen", [])
        .directive("focusWhen", ["$parse", function ($parse) {
            return {
                restrict: 'A',
                link: function postLink(scope, element, attrs) {
                    scope.$watch(attrs.focusWhen, function (value) {
                        if (value) {
                            setTimeout(function () {
                                element[0].focus();
                                element[0].select();
                            }, 40);
                        }
                    });
                }
            };
        }]);
}());
