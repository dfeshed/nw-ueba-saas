/**
 * Resolve object for alerts
 */
(function () {
    'use strict';


    var staleAccountsMonitoringResolve = {
        resolve: {
            tableInitialSettings: [
                'jsonLoader',
                function (jsonLoader) {
                    return jsonLoader
                        .load('app/layouts/reports/layouts/stale-accounts-monitoring/settings/' +
                        'table-initial.settings.json');
                }
            ],
            terminatedTableInitialSettings: [
                'jsonLoader',
                function (jsonLoader) {
                    return jsonLoader
                        .load('app/layouts/reports/layouts/stale-accounts-monitoring/settings/' +
                        'table-terminated-initial.settings.json');
                }
            ],
            tableInitialState: [
                'jsonLoader',
                function (jsonLoader) {
                    return jsonLoader
                        .load('app/layouts/reports/layouts/stale-accounts-monitoring/settings/' +
                        'table-initial.state.json');
                }
            ]
        },
        disabledUserAccountsResolve: {
            disabledUsersStatusResource: [
                'jsonLoader',
                function (jsonLoader) {
                    return jsonLoader
                        .load('app/layouts/reports/layouts/stale-accounts-monitoring/settings/' +
                        'disabled-users-status.resource.json');
                }
            ]
        },
        inactiveUserAccountsResolve: {
            inactiveUsersStatusResource: [
                'jsonLoader',
                function (jsonLoader) {
                    return jsonLoader
                        .load('app/layouts/reports/layouts/stale-accounts-monitoring/settings/' +
                        'inactive-users-status.resource.json');
                }
            ]
        },
        disabledUserWithNetworkResolve: {
            disabledUsersWithNetworkResource: [
                'jsonLoader',
                function (jsonLoader) {
                    return jsonLoader
                        .load('app/layouts/reports/layouts/stale-accounts-monitoring/settings/' +
                        'disabled-users-with-network.resource.json');
                }
            ]
        },
        terminatedUserWithNetworkResolve: {
            terminatedUsersWithNetworkResource: [
            'jsonLoader',
            function (jsonLoader) {
            return jsonLoader
            .load('app/layouts/reports/layouts/stale-accounts-monitoring/settings/' +
            'terminated-users-with-network.resource.json');
            }
    ]
}
    };


    angular.module('Config')
        .constant('staleAccountsMonitoringResolve', staleAccountsMonitoringResolve);
}());
