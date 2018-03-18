(function () {
    'use strict';

    /**
     * Factory restangular.adapters.dataQueryAdapter
     * returns a function that is an adapter for data returned from Fortscale's dataQuery api
     *
     * @returns {Function}
     * @constructor
     */
    function DataAdapters () {
        var provider = this;

        provider._errorMsg = 'restangular.dataAdapters: ';
        /**
         * Takes a data object (returned by Fortscale's dataQuery api) and converts it to a
         * generalized object schema where the main object holds the data,
         * and it has a _meta property holding all meta data
         *
         * @param {object} data
         * @returns {object}
         */
        provider.processDataQuery = function (data) {

            // Validations (not using assert because its a provider)
            if (!(data && angular.isDefined(data.data))) {
                throw new ReferenceError(provider._errorMsg +
                    'provided data argument must have a "data" property.');
            }
            if (!angular.isObject(data.data)) {
                throw new TypeError(provider._errorMsg +
                    'provided data argument\'s "data" property must be an object.');
            }
            // Set the 'actual' data as the primary object
            var transformedData = data.data;

            // Create a _meta object for holding meta data
            transformedData._meta = {};

            // Iterate through all keys on the data and attach them to _meta object (expect for
            // 'data' key which is already the primary object
            _.each(data, function (value, key, obj) {
                if (key !== 'data') {
                    transformedData._meta[key] = value;
                }
            });

            // Return the transformed data object
            return transformedData;
        };

        /**
         * Takes a data object (returned by Fortscale's rest api) and converts it to a
         * generalized object schema where the main object holds the data,
         * and it has a _meta property holding all meta data
         *
         * @param {object} data
         * @returns {object}
         */
        provider.processRest = function (data) {

            // Validations (not using assert because its a provider)
            if (!(data && angular.isDefined(data.data))) {
                throw new ReferenceError(provider._errorMsg +
                    'provided data argument must have an "data" property.');
            }

            if (!angular.isObject(data.data)) {
                throw new TypeError(provider._errorMsg +
                    'provided data._embedded argument\'s "data" property must be an object.');
            }

            // Set the 'actual' data as the primary object
            var transformedData = data.data;

            // Create a _meta object for holding meta data
            transformedData._meta = {};

            // Iterate through all keys on the data and attach them to _meta object (expect for
            // 'data' key which is already the primary object
            _.each(data, function (value, key, obj) {
                if (key !== 'data') {
                    transformedData._meta[key] = value;
                }
            });

            // Return the transformed data object
            return transformedData;
        };
        /*
        The $get is an angular convention, and is !required! when creating a provider.
         */
        provider.$get = [function () {
            throw new Error(provider._errorMsg +
                'This provider is only supposed to be consumed in config phase.');
        }];

    }

    function restangularConfig(BASE_URL, RestangularProvider, dataAdaptersProvider) {

        // Set base url
        RestangularProvider.setBaseUrl(BASE_URL);

        // Add response interceptor
        // This is uses to 'iron-out' the response coming from different sources and having
        // different schemas
        RestangularProvider.addResponseInterceptor(function (data, operation, entity) {
            if (entity === 'dataQuery') {
                return dataAdaptersProvider.processDataQuery(data);
            } else {
                return dataAdaptersProvider.processRest(data);
            }
        });
    }

    restangularConfig.$inject = [
        'BASE_URL',
        'RestangularProvider',
        'restangular.dataAdaptersProvider'
    ];

    angular.module('Fortscale')

        // Adapter for the data returning from dataQuery api
        .provider('restangular.dataAdapters', DataAdapters)
        .config(restangularConfig);
}());
