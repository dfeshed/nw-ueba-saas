(function () {
    'use strict';

    function appConfig (appConfigProvider) {
        appConfigProvider

            .addConfigContainer({
                id: 'messages',
                displayName: 'Locale Messages',
                configurable: false,
                allowUpsert: true
            });

    }

    appConfig.$inject = ['appConfigProvider'];

    angular.module('Fortscale.appConfig')
        .config(appConfig);
}());
