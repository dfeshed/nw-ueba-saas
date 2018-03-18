/**
 * Resolve object for alerts
 */
(function () {
    'use strict';

    var externalAccessToNetworkData = {
        VPNGeoHopping: {
            reportTitle: 'VPN Anomalous Geolocation Sequences',
            reportDescription: 'Investigate VPN connections coming from distant locations in an unrealistically ' +
            'short time frames.',
            sessionPerUserWidgetTitle: 'Users',
            sessionPerUserWidgetNoItems: 'No Sessions Found.',
            sessionPerUserTableDescription: 'VPN Events'
        },
        suspiciousVPNDataAmount: {
            reportTitle: 'VPN Anomalous Data Usage',
            reportDescription: 'Investigate excessive data usage over VPN.',
            sessionPerTimeChartTitle: 'Sessions',
            sessionPerTimeChartNoItems: 'No Events Found',
            sessionPerTimeChartNoTable: 'No Events Found'
        }
    };

    angular.module('Config')
        .constant('externalAccessToNetworkData', externalAccessToNetworkData);
}());

