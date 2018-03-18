/**
 * Based on https://github.com/jdpedrie/angularjs-ordinal-filter
 * This filter converts val into an ordinal value.
 * Example: 1 => st
 * Example: 2 => nd
 */
(function () {
    'use strict';

    function fsOrdinal () {

        /**
         *
         * @param {string} val
         * @returns {string}
         */
        function fsOrdinalFilter (val) {
            val = parseInt(val, 10);
            return Math.floor(val / 10) === 1 ? 'th' :
                (val % 10 === 1 ? 'st' :
                    (val % 10 === 2 ? 'nd' :
                        (val % 10 === 3 ? 'rd' : 'th')
                    )
                );
        }

        return fsOrdinalFilter;

    }

    angular.module('Fortscale.shared.filters')
        .filter('fsOrdinal', fsOrdinal);
}());
