(function () {
    'use strict';

    function round () {

        function roundFilter(value) {
            var parsedVal = parseInt(value, 10);
            if (isNaN(parsedVal)) {
                return value;
            }

            return Math.round(parsedVal);
        }

        return roundFilter;
    }

    round.$inject = [];

    angular.module('Fortscale.shared.filters')
        .filter('round', round);
}());
