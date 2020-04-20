(function () {
    'use strict';


    function encodeUrlComponent() {

        function encodeUrlComponentFilter(value) {
            if (!_.isString(value)) {
                return value;
            }


            return window.encodeURIComponent(value);
        }

        return encodeUrlComponentFilter;
    }

    angular.module('Fortscale.shared.filters')
        .filter('encodeUrlComponent', encodeUrlComponent);
}());
