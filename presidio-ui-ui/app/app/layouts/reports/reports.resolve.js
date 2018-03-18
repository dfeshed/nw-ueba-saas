/**
 * Resolve object for alerts
 */
(function () {
    'use strict';


    var reportsResolve = {
        navBarSettings: [
            'jsonLoader',
            function (jsonLoader) {
                return jsonLoader
                    .load('app/layouts/reports/settings/nav-bar-settings.json');
            }
        ],
        dataEntitiesList: [
            function () {
                return [
                    {
                        id: 'ssh',
                        name: 'SSH'
                    },
                    {
                        id: 'vpn',
                        name: 'VPN'
                    },
                    {
                        id: 'auth',
                        name: 'Kerberos'
                    }
                ];
            }
        ]
    };


    angular.module('Config')
        .constant('reportsResolve', reportsResolve);
}());
