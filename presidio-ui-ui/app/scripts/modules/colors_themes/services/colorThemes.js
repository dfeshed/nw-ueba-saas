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

        function ColorThemesConfigFactory (assert, BASE_URL, $http, $timeout) {
            var remoteThemseApi;
            var colorsCache;

            let constants = {
                MAIN_WIDGET_TITLES_TEXT_COLOR: 'main-widgets-titles-text-color',
                SEVERITY_CRITICAL_COLOR: 'main-critical-severity-color',
                SEVERITY_HIGH_COLOR: 'main-high-severity-color',
                SEVERITY_MEDIUM_COLOR: 'main-medium-severity-color',
                SEVERITY_LOW_COLOR: 'main-low-severity-color',
                POPUP_BACKGROUND_COLOR: 'main-popup-background-color'
            };


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
                let ctrl = this;
                //Properties already loaded
                if (!_.isNil(ctrl.colorsCache)){
                    return Promise.resolve(ctrl.colorsCache);
                }
                return $http.get(remoteThemseApi._getUrl())
                    .then(function (res) {
                        //Init the colors set
                        // $timeout(function() {
                            let tempMap = {};
                            _.forOwn(res.data.data, function(value, key) {
                                tempMap[key] = value;
                                angular.element('body')[0].style.setProperty("--"+key,value);
                            });
                            ctrl.colorsCache = tempMap;
                            return tempMap;
                        // });
                    })
                    .catch(function (err) {
                        console.error('Remote configuration could not be loaded due to an http error.');
                        console.error(err);
                        remoteConfigList = {};
                    });
            }

            function getThemseKeysAndValues(){
                return this.initThemes().then(function(res) {
                    return res;
                });

            }

            remoteThemseApi = {
                _getUrl: _getUrl,
                initThemes: initThemes,
                getThemseKeysAndValues: getThemseKeysAndValues,
                constants:constants

            };

            return remoteThemseApi;
        };

        provider.$get = [
            'assert', 'BASE_URL', '$http','$timeout',
            ColorThemesConfigFactory
        ];
    };

    ColorThemesConfigProvider.$inject = [];

    angular.module('ColorThemes', [])
        .provider('colorThemes', ColorThemesConfigProvider);
}());
