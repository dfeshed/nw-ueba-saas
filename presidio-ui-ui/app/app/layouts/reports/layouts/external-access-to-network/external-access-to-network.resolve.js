/**
 * Resolve object for alerts
 */
(function () {
    'use strict';

    var externalAccessToNetworkResolve = {
        resolve: {},
        VPNGeoHopping: {
            userControlSettings: [
                'jsonLoader',
                function (jsonLoader) {
                    return jsonLoader
                        .load('app/layouts/reports/layouts/external-access-to-network/settings/' +
                        'vpn-geo-hopping-user-control.settings.json');
                }
            ],
            userControlResource: [
                'jsonLoader',
                function (jsonLoader) {
                    return jsonLoader
                        .load('app/layouts/reports/layouts/external-access-to-network/settings/' +
                        'vpn-geo-hopping-user-control.resource.json');
                }
            ],
            VPNGeoHoppingResource: [
                'jsonLoader',
                function (jsonLoader) {
                    return jsonLoader
                        .load('app/layouts/reports/layouts/external-access-to-network/settings/' +
                        'vpn-geo-hopping.resource.json');
                }
            ],
            VPNGeoHoppingTableSettings: [
                'jsonLoader',
                function (jsonLoader) {
                    return jsonLoader
                        .load('app/layouts/reports/layouts/external-access-to-network/settings/' +
                        'vpn-geo-hopping-table.settings.json');
                }
            ],
            VPNGeoHoppingChartSettings: [
                'jsonLoader',
                function (jsonLoader) {
                    return jsonLoader
                        .load('app/layouts/reports/layouts/external-access-to-network/settings/' +
                        'vpn-geo-hopping-chart.settings.json');
                }
            ]
        },
        suspiciousVPNDataAmount: {
            userControlSettings: [
                'jsonLoader',
                function (jsonLoader) {
                    return jsonLoader
                        .load('app/layouts/reports/layouts/external-access-to-network/settings/' +
                        'suspicious-vpn-data-amount-user-control.settings.json');
                }
            ],
            userControlResource: [
                'jsonLoader',
                function (jsonLoader) {
                    return jsonLoader
                        .load('app/layouts/reports/layouts/external-access-to-network/settings/' +
                        'suspicious-vpn-data-amount-user-control.resource.json');
                }
            ],
            tableResource: [
                'jsonLoader',
                function (jsonLoader) {
                    return jsonLoader
                        .loadJsonx('app/layouts/reports/layouts/external-access-to-network/settings/' +
                        'suspicious-vpn-data-amount-table.resource.jsonx');
                }
            ],
            tableSettings: [
                'jsonLoader',
                function (jsonLoader) {
                    return jsonLoader
                        .load('app/layouts/reports/layouts/external-access-to-network/settings/' +
                        'suspicious-vpn-data-amount-table.settings.json');
                }
            ],
            chartResource: [
                'jsonLoader',
                function (jsonLoader) {
                    return jsonLoader
                        .loadJsonx('app/layouts/reports/layouts/external-access-to-network/settings/' +
                        'suspicious-vpn-data-amount-chart.resource.jsonx');
                }
            ]
        }
    };

    angular.module('Config')
        .constant('externalAccessToNetworkResolve', externalAccessToNetworkResolve);
}());
