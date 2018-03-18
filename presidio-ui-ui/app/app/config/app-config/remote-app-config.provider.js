(function () {
    'use strict';

    var remoteConfigList = null;
    var restPath = 'application_configuration';

    function RemoteAppConfigProvider () {

        var provider = this;

        /**
         * Change the REST path in config phase.
         *
         * @param _restPath
         */
        provider.changeRestPath = function (_restPath) {
            restPath = _restPath;
        };

        function RemoteAppConfigFactory (assert, BASE_URL, $http) {
            var remoteConfigApi;

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
            function initRemoteAppConfig () {
                return $http.get(remoteConfigApi._getUrl())
                    .then(function (res) {
                        remoteConfigList = res.data.data;
                    })
                    .catch(function (err) {
                        console.error('Remote configuration could not be loaded due to an http error.');
                        console.error(err);
                        remoteConfigList = {};
                    });
            }

            /**
             * Returns a duplicated list of remote config list.
             *
             * @returns {Array<{key: string, value: string}>}
             */
            function getRemoteConfigList () {
                return _.map(remoteConfigList, function (configurationItem) {
                    return _.merge({}, configurationItem);
                });
            }

            /**
             *
             * @param {Array<{key: string, value: string}>} configItemsList
             * @returns {HttpPromise}
             */
            function updateConfigItems (configItemsList) {
                return $http.post(remoteConfigApi._getUrl(), {
                    items: configItemsList
                });
            }

            remoteConfigApi = {
                _getUrl: _getUrl,
                initRemoteAppConfig: initRemoteAppConfig,
                getRemoteConfigList: getRemoteConfigList,
                updateConfigItems: updateConfigItems
            };

            return remoteConfigApi;
        }

        provider.$get = [
            'assert', 'BASE_URL', '$http',
            RemoteAppConfigFactory
        ];
    }

    RemoteAppConfigProvider.$inject = [];

    angular.module('Fortscale.remoteAppConfig', [])
        .provider('remoteAppConfig', RemoteAppConfigProvider);
}());
