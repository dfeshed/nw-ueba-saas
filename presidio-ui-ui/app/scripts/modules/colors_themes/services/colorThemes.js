(function () {
    'use strict';

    var remoteConfigList = null;
    var restPath = 'themes/';

    function ColorThemesConfigProvider () {

        var provider = this;

        /**
         * Change the REST path in config phase.
         *
         * @param _restPath
         */
        provider.changeRestPath = function (_restPath) {
            restPath = _restPath;
        };

        function ColorThemesConfigFactory (assert, BASE_URL, $http) {
            var remoteThemseApi;

            /**
             * Returns the REST url
             *
             * @returns {string}
             * @private
             */
            function _getUrl () {
                return BASE_URL + '/' + restPath;
            }

            /**
             * This is used by the Loader module. It loads the remote configuration and stores in remoteConfigList
             *
             * @returns {Promise.<T>|*}
             */
            function initThemes () {
                return $http.get(remoteThemseApi._getUrl())
                    .then(function (res) {
                        //Init the colors set
                        _.forOwn(res.data.data, function(value, key) {
                            document.documentElement.style.setProperty(`--${key}`, value);
                        });
                    })
                    .catch(function (err) {
                        console.error('Remote configuration could not be loaded due to an http error.');
                        console.error(err);
                        remoteConfigList = {};
                    });
            }
            //
            // /**
            //  * Returns a duplicated list of remote config list.
            //  *
            //  * @returns {Array<{key: string, value: string}>}
            //  */
            // function getRemoteConfigList () {
            //     return _.map(remoteConfigList, function (configurationItem) {
            //         return _.merge({}, configurationItem);
            //     });
            // }



            remoteThemseApi = {
                _getUrl: _getUrl,
                initThemes: initThemes,

            };

            return remoteThemseApi;
        }

        provider.$get = [
            'assert', 'BASE_URL', '$http',
            ColorThemesConfigFactory
        ];
    }

    ColorThemesConfigProvider.$inject = [];

    angular.module('ColorThemes', [])
        .provider('colorThemes', ColorThemesConfigProvider);
}());
