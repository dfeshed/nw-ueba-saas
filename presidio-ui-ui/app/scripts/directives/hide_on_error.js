(function () {
    'use strict';

    angular.module("Fortscale")
        .directive("hideOnError", [function () {
            return {
                restrict: 'A',
                link: function postLink(scope, element, attrs) {
                    scope.$on("$destroy", function (e, data) {
                        element.off();
                    });

                    element.on("error", function (event) {
                        element.css("display", "none");
                    });
                }
            };
        }]);
}());
