(function () {
    'use strict';

    function DisabledUserAccountsController (appConfig, $state, dateRanges, tableInitialState, tableInitialSettings,
        disabledUsersStatusResource, fsNanobarAutomationService) {

        // Put injections on instance
        this.state = _.merge({}, $state.current.data);
        this.disabledUsersStatusResource = _.merge({}, disabledUsersStatusResource);
        this.disabledUsersTableSettings = _.merge({}, tableInitialSettings);

        this._defaultStartOfDayDaysAgo = appConfig.getConfigValue('ui.' + $state.current.name, 'daysAgo');

        this.mainState = {
            disabled_user_table: tableInitialState,
            disabled_since: {
                value: dateRanges.getStartOfDayByDaysAgo(this._defaultStartOfDayDaysAgo)
            }
        };


        this.NANOBAR_ID = 'reports';
        this.dataFetchDelegate = (promise) => {
            fsNanobarAutomationService.addPromise(this.NANOBAR_ID, promise);
        };

        this.init();
    }

    _.merge(DisabledUserAccountsController.prototype, {

        init: function () {
        }
    });

    DisabledUserAccountsController.$inject =
        ['appConfig', '$state', 'dateRanges', 'tableInitialState', 'tableInitialSettings',
            'disabledUsersStatusResource', 'fsNanobarAutomationService'];
    angular.module('Fortscale.layouts.reports')
        .controller('DisabledUserAccountsController', DisabledUserAccountsController);
}());
