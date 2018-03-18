module Fortscale.appConfigProvider.validators {
    'use strict';

    function requiredValidator(val) {
        return val !== null && val !== undefined && val !== '';
    }

    /**
     * Validates integer
     *
     * @param {string} val
     * @returns {boolean}
     */
    function integerValidator(val) {
        if (val === null) {
            return true;
        }

        return (!isNaN(val) && _.isNumber(val) && Math.floor(val) === val);
    }

    function numberValidator(val) {
        if (val === null) {
            return true;
        }

        return (!isNaN(val) && _.isNumber(val));
    }


    function portValidator(val:number) {
        if (val === null) {
            return true;
        }

        return (!isNaN(val) && _.isNumber(val) && val > 0 && val <= 65535);
    }

    function ipValidator(val:string) {
        if (val === null) {
            return true;
        }

        if (!_.isString(val)) {
            return false;
        }

        let ipRgx = /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/
        return ipRgx.test(val);
    }

    function stringValidator(val:any) {
        if (val === null) {
            return true;
        }

        return _.isString(val);
    }

    function booleanValidator (val:any) {
        if (val === null || val === true || val === false) {
            return true;
        }
    }

    angular.module('Fortscale.appConfig')
        .config(['appConfigProvider', function (appConfigProvider) {
            appConfigProvider
                .addValidator('integer', integerValidator)
                .addValidator('number', numberValidator)
                .addValidator('string', stringValidator)
                .addValidator('password', stringValidator)
                .addValidator('boolean', booleanValidator)
                .addValidator('required', requiredValidator)
                .addValidator('port', portValidator)
                .addValidator('ip', ipValidator);
        }]);
}
