(function () {
    'use strict';

    function JsonLoader (assert, $http) {
        this._errMsg = 'Fortscale.shared.services.jsonLoader: ';

        this.$http = $http;
        this.assert = assert;
    }

    angular.merge(JsonLoader.prototype, {

        _validateUrl: function (url, errMsg) {
            this.assert(_.isString(url), errMsg + 'url must be a string.', TypeError);
            this.assert(url !== '', errMsg + 'url must not be an empty string.', RangeError);
            this.assert(/\.json$/.test(url) || /\.jsonx$/.test(url),
                errMsg + 'url must request a json or jsonx type file.', RangeError);
        },

        /**
         * Takes a json url.
         * Returns a promise that is resolved on an object that a json file represents.
         * If preventCache is true, the file will not be cached.
         *
         * @param {string} url
         * @param {boolean=} preventCache
         * @returns {Promise}
         */
        load: function (url, preventCache) {

            this._validateUrl(url, this._errMsg + 'load: ');

            var shouldCache = !preventCache;

            return this.$http
                .get(url, {
                    cache: shouldCache
                })
                .then(function (res) {
                    return res.data;
                });
        },

        /**
         * Takes a jsonx url.
         * Returns a promise that is resolved on a string that a jsonx file represents.
         * This type of file is used when a json needs to be interpolated, and before interpolation is not a valid
         * JSON file.
         * If preventCache is true, the file will not be cached.
         *
         * @param {string} url
         * @param {boolean=} preventCache
         * @returns {Promise}
         */
        loadJsonx: function (url, preventCache) {

            this._validateUrl(url, this._errMsg + 'load: ');

            var shouldCache = !preventCache;

            return this.$http({
                method: 'get',
                url: url,
                transformResponse: function (data) {
                    return data;
                },
                cache: shouldCache
            })
                .then(function (res) {
                    return res.data;
                });
        }
    });

    JsonLoader.$inject = ['assert', '$http'];

    angular.module('Fortscale.shared.services.jsonLoader', [
        'Fortscale.shared.services.assert'
    ])
        .service('jsonLoader', JsonLoader);
}());
