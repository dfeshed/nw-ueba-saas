(function () {
    'use strict';

    var IS_NOT_OPTIONAL = false;
    var CAN_NOT_BE_EMPTY = false;

    function CountryCodesUtilService (assert, countryCodes) {
        this.assert = assert;
        this.countryCodes = countryCodes;
    }

    _.merge(CountryCodesUtilService.prototype, {
        _errMsg: 'countryCodesUtils.service: ',

        /**
         * Returns a country object by
         *
         * @param {String} searchBy The property name to search by
         * @param {String} searchValue the value to search by
         * @returns {*|null}
         * @private
         */
        _getCountryObj: function (searchBy, searchValue) {
            // Since the countryCodes object's properties names are country names, there is no need to query when
            // searchBy is 'name', you just need to return the country object.
            if (searchBy === 'name') {
                return this.countryCodes[searchValue] || null;
            } else {
                // Query the countryCodes object to get the required country object.
                var query = {};
                query[searchBy] = searchValue;
                return _.filter(this.countryCodes, query)[0] || null;
            }
        },

        /**
         * Validates countryName argument
         *
         * @param {String} methodName
         * @param {String} countryName
         * @private
         */
        _validateCountryName: function (methodName, countryName) {
            this.assert.isString(countryName, 'countryName', this._errMsg + methodName + ': ', IS_NOT_OPTIONAL,
                CAN_NOT_BE_EMPTY);
        },

        /**
         * Return Alpha2 value by country name.
         *
         * @param {String} countryName
         * @returns {String|Null}
         */
        getAlpha2ByCountryName: function (countryName) {
            this._validateCountryName('getAlpha2ByCountryName', countryName);
            var countryObj = this._getCountryObj('name', countryName.trim().toLowerCase());
            if (countryObj === null) {
                return null;
            }
            return countryObj['alpha-2'];
        },

        /**
         * Return Alpha2 value by country code.
         *
         * @param {String} countryCode
         * @returns {String|Null}
         */
        getAlpha2ByCountryCode: function (countryCode) {
            this.assert.isString(countryCode, 'countryCode', this._errMsg + 'getAlpha2ByCountryCode: ', IS_NOT_OPTIONAL,
                CAN_NOT_BE_EMPTY);
            var countryObj = this._getCountryObj('country-code', countryCode.time());
            if (countryObj === null) {
                return null;
            }
            return countryObj['alpha-2'];
        },

        /**
         * Return country-code value by country name.
         *
         * @param {String} countryName
         * @returns {String|Null}
         */
        getCountryCodeByCountryName: function (countryName) {
            this._validateCountryName('getAlpha2ByCountryName', countryName);
            var countryObj = this._getCountryObj('name', countryName.trim().toLowerCase());
            if (countryObj === null) {
                return null;
            }
            return countryObj['country-code'];
        },

        /**
         * Returns country name by alpha-2 code
         *
         * @param {String} alpha2
         * @returns {String|Null}
         */
        getCountryNameByAlpha2: function (alpha2) {
            this.assert.isString(alpha2, 'alpha2', this._errMsg + 'getCountryNameByAlpha2: ', IS_NOT_OPTIONAL,
                CAN_NOT_BE_EMPTY);
            var countryObj = this._getCountryObj('alpha-2', alpha2.trim().toUpperCase());
            if (countryObj === null) {
                return null;
            }
            return countryObj.name;
        },

        /**
         * Returns country name by country-code
         *
         * @param {String} countryCode
         * @returns {String|Null}
         */
        getCountryNameByCountryCode: function (countryCode) {
            this.assert.isString(countryCode, 'countryCode', this._errMsg + 'getCountryNameByAlpha2: ', IS_NOT_OPTIONAL,
                CAN_NOT_BE_EMPTY);
            var countryObj = this._getCountryObj('country-code', countryCode);
            if (countryObj === null) {
                return null;
            }
            return countryObj.name;
        }
    });

    CountryCodesUtilService.$inject = ['assert', 'countryCodes'];

    angular.module('Fortscale.shared.services.countryCodesUtil', [])
        .service('countryCodesUtil', CountryCodesUtilService);

}());
