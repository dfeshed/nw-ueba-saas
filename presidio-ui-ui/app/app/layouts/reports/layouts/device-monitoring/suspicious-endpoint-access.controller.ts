module Fortscale {
    'use strict';


    (function () {
        'use strict';

        function SuspiciousEndpointAccessController (appConfig, $state, dateRanges, mainState, endpointControlSettings,
            endpointControlResource, resourceSettings, tableSettings, userUtils, fsNanobarAutomationService) {

            // Put injections on instance
            this.state = _.merge({}, $state.current.data);
            this._defaultDaysRange = appConfig.getConfigValue('ui.' + $state.current.name, 'daysRange');

            this.mainState = _.merge({}, mainState, {
                "events_time": {
                    "value": dateRanges.getByDaysRange(this._defaultDaysRange, 'short')
                }

            });

            this.endpointControlSettings = _.merge({}, endpointControlSettings);
            this.endpointControlResource = _.merge({}, endpointControlResource);
            this.resourceSettings = resourceSettings;
            this.tableSettings = _.merge({}, tableSettings);

            // Add user control settings
            this.userControlSettings = {
                "dataValueField": "username",
                "dataTextField": "fallBackDisplayName",
                /**
                 * Takes received users and creates fallBack display name for each, and prevents duplications.
                 * @param users
                 */
                "dataTextFn": function (users) {
                    userUtils.setFallBackDisplayNames(users);
                    userUtils.preventFallBackDisplayNameDuplications(users);
                },
                "placeholder": "All Users"
            };

            // Add user control resource settings
            this.userControlResource = {
                "entity": "user",
                "params": {
                    "page": 1,
                    "size": 10,
                    "sort_field": "displayName",
                    "sort_direction": "ASC",
                    "search_field_contains": "{{search}}"
                }
            };

            this.init();

            this.NANOBAR_ID = 'reports';
            this.dataFetchDelegate = (promise) => {
                fsNanobarAutomationService.addPromise(this.NANOBAR_ID, promise);
            };
        }

        _.merge(SuspiciousEndpointAccessController.prototype, {
            init: function () {
            }
        });

        SuspiciousEndpointAccessController.$inject =
            ['appConfig', '$state', 'dateRanges', 'mainState', 'endpointControlSettings', 'endpointControlResource',
                'resourceSettings', 'tableSettings', 'userUtils', 'fsNanobarAutomationService'];
        angular.module('Fortscale.layouts.reports')
            .controller('SuspiciousEndpointAccessController', SuspiciousEndpointAccessController);
    }());
}
