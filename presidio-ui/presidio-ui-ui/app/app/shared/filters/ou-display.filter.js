(function () {
    'use strict';

    function ouDisplay () {

        function ouDisplayFilter (val) {
            var rgx = /=/;

            if (rgx.test(val)) {
                return val.split('=')[1];
            }

            return val;
        }

        return ouDisplayFilter;

    }

    angular.module('Fortscale.shared.filters')
        .filter('ouDisplay', ouDisplay);
}());
