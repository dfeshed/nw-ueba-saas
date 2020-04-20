(function () {
    'use strict';

    /**
     * Returns a filter
     *
     * @returns {function(any): number}
     */
    function orZero ():(val)=>number {

        /**
         * if val is "falsy" return 0
         *
         * @param {any} number
         * @returns number
         */
        return function (val):number {
            if (!val) {
                return 0;
            }

            return val;
        }
    }


    angular.module('Fortscale.shared.filters')
        .filter('orZero', [orZero]);
}());
