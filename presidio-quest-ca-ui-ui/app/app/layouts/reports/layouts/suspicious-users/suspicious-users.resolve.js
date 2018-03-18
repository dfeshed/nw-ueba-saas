/**
 * Resolve object for alerts
 */
(function () {
    'use strict';


    var suspiciousUsersResolve = {
        tableResource: [
            'jsonLoader',
            function (jsonLoader) {
                return jsonLoader
                    .load('app/layouts/reports/layouts/suspicious-users/settings/suspicious-users-table.resource.json');
            }
        ],
        tableSettings: [
            'jsonLoader',
            function (jsonLoader) {
                return jsonLoader
                    .load('app/layouts/reports/layouts/suspicious-users/settings/suspicious-users-table.settings.json');
            }
        ]
    };


    angular.module('Config')
        .constant('suspiciousUsersResolve', suspiciousUsersResolve);
}());
