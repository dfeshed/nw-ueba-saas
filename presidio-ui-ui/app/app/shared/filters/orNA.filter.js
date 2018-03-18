(function () {
    'use strict';

    function orNA () {

        function orNAFilter (val) {
            if (val === null || val === undefined || val ==="") {
                return "N/A";
            }

            return val;
        }

        return orNAFilter;

    }

    angular.module('Fortscale.shared.filters')
        .filter('orNA', orNA);
}());
