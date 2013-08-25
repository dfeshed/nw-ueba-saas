'use strict';

angular.module("Fortscale").directive("knob", function () {
    return {
        restrict: 'C',
        require: "?ngModel",
        link: function postLink(scope, element, attrs, ngModel) {
            element.knob();
        }
    }
});
