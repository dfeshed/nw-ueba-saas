(function () {
    'use strict';

    function InactiveUserAccountsController (appConfig, $state, dateRanges, tableInitialState, tableInitialSettings,
        inactiveUsersStatusResource, fsNanobarAutomationService) {

        // Put injections on instance
        this.state = _.merge({}, $state.current.data);
        this.inactiveUsersStatusResource = _.merge({}, inactiveUsersStatusResource);
        this.inactiveUsersTableSettings = _.merge({}, tableInitialSettings);

        this._defaultStartOfDayDaysAgo = appConfig.getConfigValue('ui.' + $state.current.name, 'daysAgo');

        this.mainState = {
            inactive_user_table: tableInitialState,
            inactive_since: {
                value: dateRanges.getStartOfDayByDaysAgo(this._defaultStartOfDayDaysAgo)
            }
        };


        this.NANOBAR_ID = 'reports';
        this.dataFetchDelegate = (promise) => {
            fsNanobarAutomationService.addPromise(this.NANOBAR_ID, promise);
        };

        this.init();
    }

    _.merge(InactiveUserAccountsController.prototype, {
        /**
         * Removes the unneeded (for this report) 'Disabled On' column
         *
         * @private
         */
        _removeDisabledOnColumn: function () {
            this.inactiveUsersTableSettings.columns = _.filter(this.inactiveUsersTableSettings.columns,
                function (column) {
                    return column.title !== 'Disabled On';
                });
        },
        init: function () {
            this._removeDisabledOnColumn();
        }
    });

    InactiveUserAccountsController.$inject =
        ['appConfig', '$state', 'dateRanges', 'tableInitialState', 'tableInitialSettings',
            'inactiveUsersStatusResource', 'fsNanobarAutomationService'];
    angular.module('Fortscale.layouts.reports')
        .controller('InactiveUserAccountsController', InactiveUserAccountsController);
}());
