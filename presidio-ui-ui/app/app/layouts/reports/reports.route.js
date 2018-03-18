(function () {
    'use strict';
    angular.module("Fortscale.layouts.reports")
        .config([
            '$stateProvider',
            'reportsResolve',
            'staleAccountsMonitoringResolve',
            'staleAccountsMonitoringData',

            'deviceMonitoringResolve',
            'deviceMonitoringData',
            'suspiciousUsersResolve',
            'suspiciousUsersData',
            'externalAccessToNetworkResolve',
            'externalAccessToNetworkData',
            function ($stateProvider, reportsResolve, staleAccountsMonitoringResolve, staleAccountsMonitoringData,
                deviceMonitoringResolve,
                deviceMonitoringData, suspiciousUsersResolve, suspiciousUsersData, externalAccessToNetworkResolve,
                externalAccessToNetworkData) {
                $stateProvider
                    .state('reports', {
                        url: '/reports',
                        templateUrl: 'app/layouts/reports/reports.view.html',
                        controller: 'ReportsController',
                        controllerAs: 'reports',
                        resolve: reportsResolve
                    })

                    // Stale Accounts Monitoring
                    .state('reports.staleAccountsMonitoring', {
                        url: '/stale_accounts_monitoring',
                        abstract: true,
                        template: '<ui-view></ui-view>',
                        resolve: staleAccountsMonitoringResolve.resolve
                    })
                    .state('reports.staleAccountsMonitoring.disabledUserAccounts', {
                        url: '/disabled_users_status',
                        templateUrl: 'app/layouts/reports/layouts/stale-accounts-monitoring/' +
                        'disabled-user-accounts.view.html',
                        resolve: staleAccountsMonitoringResolve.disabledUserAccountsResolve,
                        data: staleAccountsMonitoringData.disabledUserAccountsData,
                        controller: 'DisabledUserAccountsController',
                        controllerAs: 'report'
                    })
                    .state('reports.staleAccountsMonitoring.inactiveUserAccounts', {
                        url: '/inactive_users_status',
                        templateUrl: 'app/layouts/reports/layouts/stale-accounts-monitoring/' +
                        'inactive-user-accounts.view.html',
                        resolve: staleAccountsMonitoringResolve.inactiveUserAccountsResolve,
                        data: staleAccountsMonitoringData.inactiveUserAccountsData,
                        controller: 'InactiveUserAccountsController',
                        controllerAs: 'report'
                    })
                    .state('reports.staleAccountsMonitoring.disabledUserWithNetwork', {
                        url: '/disabled_users_with_network',
                        templateUrl: 'app/layouts/reports/layouts/stale-accounts-monitoring/' +
                        'disabled-user-with-network.view.html',
                        resolve: staleAccountsMonitoringResolve.disabledUserWithNetworkResolve,
                        data: staleAccountsMonitoringData.disabledUserWithNetworkData,
                        controller: 'DisabledUserWithNetworkController',
                        controllerAs: 'report'
                    })
                    .state('reports.staleAccountsMonitoring.terminatedUserWithNetwork', {
                        url: '/terminated_users_with_network',
                        templateUrl: 'app/layouts/reports/layouts/stale-accounts-monitoring/' +
                        'terminated-user-with-network.view.html',
                        resolve: staleAccountsMonitoringResolve.terminatedUserWithNetworkResolve,
                        data: staleAccountsMonitoringData.terminatedUserWithNetworkData,
                        controller: 'TerminatedUserWithNetworkController',
                        controllerAs: 'report'
                    })
                    // END OF Stale Accounts Monitoring

                    // Device Monitoring
                    .state('reports.deviceMonitoring', {
                        url: '/device_monitoring',
                        abstract: true,
                        template: '<ui-view></ui-view>',
                        resolve: deviceMonitoringResolve.resolve
                    })
                    .state('reports.deviceMonitoring.IPInvestigation', {
                        url: '/ip_investigation',
                        templateUrl: 'app/layouts/reports/layouts/device-monitoring/' +
                        'ip-investigation.view.html',
                        resolve: deviceMonitoringResolve.ipInvestigationResolve,
                        data: deviceMonitoringData.ipInvestigationData,
                        controller: 'IPInvestigationController',
                        controllerAs: 'report'
                    })
                    .state('reports.deviceMonitoring.suspiciousEndpointAccess', {
                        url: '/suspicious_endpoint_access',
                        templateUrl: 'app/layouts/reports/layouts/device-monitoring/' +
                        'suspicious-endpoint-access.view.html',
                        resolve: deviceMonitoringResolve.suspiciousEndpointAccessResolve,
                        data: deviceMonitoringData.suspiciousEndpointAccessData,
                        controller: 'SuspiciousEndpointAccessController',
                        controllerAs: 'report'
                    })
                    .state('reports.deviceMonitoring.sensitiveResourcesMonitoring', {
                        url: '/sensitive_resources_monitoring',
                        templateUrl: 'app/layouts/reports/layouts/device-monitoring/' +
                        'sensitive-resources-monitoring.view.html',
                        resolve: deviceMonitoringResolve.sensitiveResourcesMonitoringResolve,
                        data: deviceMonitoringData.sensitiveResourcesMonitoringData,
                        controller: 'SensitiveResourcesMonitoringController',
                        controllerAs: 'report'
                    })
                    // END OF Device Monitoring

                    // Suspicious Users
                    .state('reports.topRiskyTagged', {
                        url: '/top_risky_tagged/:tagName',
                        templateUrl: 'app/layouts/reports/layouts/suspicious-users/suspicious-users.view.html',
                        resolve: suspiciousUsersResolve,
                        data: suspiciousUsersData,
                        controller: 'RiskyTaggedUsersController',
                        controllerAs: 'report'
                    })
                    // END OF Suspicious Users

                    // External access to network
                    .state('reports.externalAccessToNetwork', {
                        url: '/external_access_to_network',
                        abstract: true,
                        template: '<ui-view></ui-view>',
                        resolve: externalAccessToNetworkResolve.resolve
                    })

                    .state('reports.externalAccessToNetwork.suspiciousVPNDataAmount', {
                        url: '/suspicious_vpn_data_amount',
                        templateUrl: 'app/layouts/reports/layouts/external-access-to-network/' +
                        'suspicious-vpn-data-amount.view.html',
                        resolve: externalAccessToNetworkResolve.suspiciousVPNDataAmount,
                        data: externalAccessToNetworkData.suspiciousVPNDataAmount,
                        controller: 'suspiciousVPNDataAmountController',
                        controllerAs: 'report'
                    })
                    .state('reports.externalAccessToNetwork.VPNGeoHopping', {
                        url: '/vpn_geo_hopping',
                        templateUrl: 'app/layouts/reports/layouts/external-access-to-network/' +
                        'vpn-geo-hopping.view.html',
                        resolve: externalAccessToNetworkResolve.VPNGeoHopping,
                        data: externalAccessToNetworkData.VPNGeoHopping,
                        controller: 'VPNGeoHoppingController',
                        controllerAs: 'report'
                    });
                // END OF External access to network

            }
        ]);

}());
