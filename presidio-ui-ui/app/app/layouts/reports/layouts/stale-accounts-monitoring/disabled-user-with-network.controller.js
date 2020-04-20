(function () {
    'use strict';

    function DisabledUserWithNetworkController ($state, tableInitialState, tableInitialSettings,
        disabledUsersWithNetworkResource, fsNanobarAutomationService) {

        // Put injections on instance
        this.state = _.merge({}, $state.current.data);
        this.disabledUsersWithNetworkResource = _.merge({}, disabledUsersWithNetworkResource);
        this.disabledUsersWithNetworkTableSettings = _.merge({}, tableInitialSettings);
        this.mainState = {
            disabled_user_table: tableInitialState
        };


        this.NANOBAR_ID = 'reports';
        this.dataFetchDelegate = (promise) => {
            fsNanobarAutomationService.addPromise(this.NANOBAR_ID, promise);
        };

        this.init();
    }

    _.merge(DisabledUserWithNetworkController.prototype, {

        init: function () {}
    });

    DisabledUserWithNetworkController.$inject = ['$state', 'tableInitialState', 'tableInitialSettings',
        'disabledUsersWithNetworkResource', 'fsNanobarAutomationService'];
    angular.module('Fortscale.layouts.reports')
        .controller('DisabledUserWithNetworkController', DisabledUserWithNetworkController);
}());
