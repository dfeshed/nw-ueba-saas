(function () {
    'use strict';

    angular.module("Fortscale")
        .directive("dropdownNoclose", [function dropdownNoCloseDirective() {
            return {
                restrict: 'AC',
                link: function postLink(scope, element, attrs) {
                    element.on("click", function (e) {
                        e.stopPropagation();
                    });
                }
            };
        }]);
}());
