(function () {
    'use strict';

    function pascalCase () {


        function pascalCaseFilter (val) {
            if (val !== undefined && _.isString(val) && val !== '') {
                var str = val.toLowerCase();
                str = str.charAt(0).toUpperCase() + str.slice(1);
                return str;
            }

            return val;
        }

        return pascalCaseFilter;

    }

    angular.module('Fortscale.shared.filters')
        .filter('pascalCase', pascalCase);
}());
