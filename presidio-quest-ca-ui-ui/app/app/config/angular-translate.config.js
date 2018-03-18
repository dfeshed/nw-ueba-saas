(function () {
    'use strict';

    var defaultLang = 'en_US';
    angular.module('Fortscale')

        .config(['$translateProvider', function ($translateProvider) {
                    $translateProvider.useLoader('customAngularTranslateLoader');
                    $translateProvider.preferredLanguage('en');
        }]);

    /**
     * Factory for $translateProvider to load the messages from rest API
     */
    function CustomAngularTranslateLoaderFactory ($http, BASE_URL, $q) {

        /**
         * This method fetch the messages from the RestAPI and return manipulated messages object to translateProvider
         */
        function customAngularTranslateLoader (options) {
            return $http({
                method:'GET',
                url:BASE_URL + '/messages/'+defaultLang //Use for real messages from server (/api/messages/lang)
                //url:'/assets/messages/messages-copy.json' //Use for mock messages from messages-copy.properties
            }).then(function (response) {
                return response.data.data;
            },function () {
                return options.key;
            });
        }

        return customAngularTranslateLoader;
    }

    CustomAngularTranslateLoaderFactory.$inject = ['$http', 'BASE_URL', '$q'];

    angular.module('Fortscale')
        .factory('customAngularTranslateLoader', CustomAngularTranslateLoaderFactory);
}());
