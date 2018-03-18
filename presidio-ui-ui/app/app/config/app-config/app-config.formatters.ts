module Fortscale.appConfigProvider.formatters {
    'use strict';


    function numberFormatter (val) {
        var parsedVal = parseFloat(val);
        if (!isNaN(parsedVal)) {
            return parsedVal;
        }

        if (val === '' || val === undefined) {
            return null;
        }

        return val;
    }

    function booleanFormatter (val) {
        if (typeof val === 'string') {
            return val.trim().toLowerCase() !== 'false';
        }

        return !!val;
    }

    function stringFormatter (val) {
        if (val === null || val === undefined) {
            return null;
        }
        return val.toString();
    }

    angular.module('Fortscale.appConfig')
    .config([
            'appConfigProvider',
            function (appConfigProvider) {
                appConfigProvider.addFormatter('integer', numberFormatter);
                appConfigProvider.addFormatter('number', numberFormatter);
                appConfigProvider.addFormatter('boolean', booleanFormatter);
                appConfigProvider.addFormatter('string', stringFormatter);
                appConfigProvider.addFormatter('password', stringFormatter);
                appConfigProvider.addFormatter('isEnabled', booleanFormatter);
            }
        ]);
}
