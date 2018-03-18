(function () {
    'use strict';

    function SensitiveResourcesMonitoringController (appConfig, $state, dateRanges,
        sensitiveMachinesStatusTableResource, sensitiveMachinesStatusTableSettings, usersAccessResourcesTableResource,
        usersAccessResourcesTableSettings, eventsSensitiveMachinesTableResource, eventsSensitiveMachinesTableSettings, fsNanobarAutomationService) {

        // Put injections on instance
        this.state = _.merge({}, $state.current.data);
        this._defaultDaysRange = appConfig.getConfigValue('ui.' + $state.current.name, 'daysRange');

        this.mainState = _.merge({}, {
            min_score: {
                value: 50
            },
            events_time: {
                value: dateRanges.getByDaysRange(this._defaultDaysRange, 'short')
            }
        });

        this.sensitiveMachinesStatusTableResource = _.merge({}, sensitiveMachinesStatusTableResource);
        this.sensitiveMachinesStatusTableSettings = _.merge({}, sensitiveMachinesStatusTableSettings);
        this.usersAccessResourcesTableResource = _.merge({}, usersAccessResourcesTableResource);
        this.usersAccessResourcesTableSettings = _.merge({}, usersAccessResourcesTableSettings);
        this.eventsSensitiveMachinesTableResource = _.merge({}, eventsSensitiveMachinesTableResource);
        this.eventsSensitiveMachinesTableSettings = _.merge({}, eventsSensitiveMachinesTableSettings);


        this.NANOBAR_ID = 'reports';
        this.dataFetchDelegate = (promise) => {
            fsNanobarAutomationService.addPromise(this.NANOBAR_ID, promise);
        };

        this.init();
    }

    _.merge(SensitiveResourcesMonitoringController.prototype, {
        init: function () {
        }
    });

    SensitiveResourcesMonitoringController.$inject =
        ['appConfig', '$state', 'dateRanges', 'sensitiveMachinesStatusTableResource',
            'sensitiveMachinesStatusTableSettings', 'usersAccessResourcesTableResource',
            'usersAccessResourcesTableSettings', 'eventsSensitiveMachinesTableResource',
            'eventsSensitiveMachinesTableSettings', 'fsNanobarAutomationService'];
    angular.module('Fortscale.layouts.reports')
        .controller('SensitiveResourcesMonitoringController', SensitiveResourcesMonitoringController);
}());
