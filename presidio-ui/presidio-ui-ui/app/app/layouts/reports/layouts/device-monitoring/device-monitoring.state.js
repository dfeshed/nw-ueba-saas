/**
 * Resolve object for alerts
 */
(function () {
    'use strict';

    var deviceMonitoringData = {
        ipInvestigationData: {
            reportTitle: 'IP Investigation',
            reportDescription: 'Investigate historic risky user behavior originating from an IP Address.',
            reportClass: 'regular',
            ipAddressControlTooltip: 'Please enter a valid IP address.',
            machinesForIpTitle: 'Devices found at this IP Address.',
            machinesForIpNoItems: 'No Devices were found from this IP Address.',
            usersForIpTitle: 'Users found at this IP Address',
            usersForIpNoItems: 'No users were found at this IP Address.'
        },
        suspiciousEndpointAccessData: {
            reportTitle: 'Suspicious Device Access',
            reportDescription: 'Detect historic risky user access to devices.',
            reportClass: 'regular',
            suspiciousEndpointsNoItems: 'No Events Found'
        },
        sensitiveResourcesMonitoringData: {
            reportTitle: 'Sensitive Resources Monitoring',
            reportDescription: 'Investigate historic risky user access to sensitive resources.',
            sensitiveMachinesStatusTableTitle: 'Sensitive Resources Risk History',
            sensitiveMachinesStatusTableDescription: ' Highest scored events observed from sensitive resources.',
            sensitiveMachinesStatusTableNoItems: 'No Events Found',
            usersAccessResourcesTableTitle: 'User Access to Sensitive Resources',
            usersAccessResourcesTableDescription: 'Investigate users who have accessed sensitive resources.',
            usersAccessResourcesTableNoItems: 'No Events Found',
            eventsSensitiveMachinesTableTitle: 'Access to Sensitive Devices',
            eventsSensitiveMachinesTableDescription: ' Investigate all access events targeting sensitive resources.',
            eventsSensitiveMachinesTableNoItems: 'No Events Found'
        }
    };

    angular.module('Config')
        .constant('deviceMonitoringData', deviceMonitoringData);
}());
