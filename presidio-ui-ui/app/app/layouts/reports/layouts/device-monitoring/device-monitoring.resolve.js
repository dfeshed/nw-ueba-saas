/**
 * Resolve object for alerts
 */
(function () {
    'use strict';


    var deviceMonitoringResolve = {
        resolve: {

        },
        ipInvestigationResolve: {
            machinesForIpResource: [
                'jsonLoader',
                function (jsonLoader) {
                    return jsonLoader
                        .load('app/layouts/reports/layouts/device-monitoring/settings' +
                        '/ip-investigation-machines.resource.json');
                }
            ],
            machinesForIpTableSettings: [
                'jsonLoader',
                function (jsonLoader) {
                    return jsonLoader
                        .load('app/layouts/reports/layouts/device-monitoring/settings' +
                        '/ip-investigation-machines-table.settings.json');
                }
            ],
            usersForIpResource: [
                'jsonLoader',
                function (jsonLoader) {
                    return jsonLoader
                        .load('app/layouts/reports/layouts/device-monitoring/settings' +
                        '/ip-investigation-users.resource.json');
                }
            ],
            usersForIpTableSettings: [
                'jsonLoader',
                function (jsonLoader) {
                    return jsonLoader
                        .load('app/layouts/reports/layouts/device-monitoring/settings' +
                        '/ip-investigation-users-table.settings.json');
                }
            ]

        },
        suspiciousEndpointAccessResolve: {
            mainState: [
                'jsonLoader',
                function (jsonLoader) {
                    return jsonLoader
                        .load('app/layouts/reports/layouts/device-monitoring/settings' +
                        '/suspicious-endpoint-access-main-state.json');
                }
            ],
            endpointControlSettings: [
                'jsonLoader',
                function (jsonLoader) {
                    return jsonLoader
                        .load('app/layouts/reports/layouts/device-monitoring/settings' +
                        '/suspicious-endpoint-access-endpoint-control.settings.json');
                }
            ],
            endpointControlResource: [
                'jsonLoader',
                function (jsonLoader) {
                    return jsonLoader
                        .load('app/layouts/reports/layouts/device-monitoring/settings' +
                        '/suspicious-endpoint-access-endpoint-control.resource.json');
                }
            ],
            resourceSettings: [
                'jsonLoader',
                function (jsonLoader) {
                    return jsonLoader
                        .loadJsonx('app/layouts/reports/layouts/device-monitoring/settings' +
                        '/suspicious-endpoint-access.resource.jsonx');
                }
            ],

            tableSettings: [
                'jsonLoader',
                function (jsonLoader) {
                    return jsonLoader
                        .load('app/layouts/reports/layouts/device-monitoring/settings' +
                        '/suspicious-endpoint-access-table.settings.json');
                }
            ]
        },
        sensitiveResourcesMonitoringResolve: {
            sensitiveMachinesStatusTableResource: [
                'jsonLoader',
                function (jsonLoader) {
                    return jsonLoader
                        .load('app/layouts/reports/layouts/device-monitoring/settings/' +
                        'sensitive-machines-status-table.resource.json');
                }
            ],
            sensitiveMachinesStatusTableSettings: [
                'jsonLoader',
                function (jsonLoader) {
                    return jsonLoader
                        .load('app/layouts/reports/layouts/device-monitoring/settings/' +
                        'sensitive-machines-status-table.settings.json');
                }
            ],
            usersAccessResourcesTableResource: [
                'jsonLoader',
                function (jsonLoader) {
                    return jsonLoader
                        .load('app/layouts/reports/layouts/device-monitoring/settings/' +
                        'users-access-resources-table.resource.json');
                }
            ],
            usersAccessResourcesTableSettings: [
                'jsonLoader',
                function (jsonLoader) {
                    return jsonLoader
                        .load('app/layouts/reports/layouts/device-monitoring/settings/' +
                        'users-access-resources-table.settings.json');
                }
            ],
            eventsSensitiveMachinesTableResource: [
                'jsonLoader',
                function (jsonLoader) {
                    return jsonLoader
                        .load('app/layouts/reports/layouts/device-monitoring/settings/' +
                        'events-sensitive-machines-table.resource.json');
                }
            ],
            eventsSensitiveMachinesTableSettings: [
                'jsonLoader',
                function (jsonLoader) {
                    return jsonLoader
                        .load('app/layouts/reports/layouts/device-monitoring/settings/' +
                        'events-sensitive-machines-table.settings.json');
                }
            ]
        }
    };


    angular.module('Config')
        .constant('deviceMonitoringResolve', deviceMonitoringResolve);
}());
