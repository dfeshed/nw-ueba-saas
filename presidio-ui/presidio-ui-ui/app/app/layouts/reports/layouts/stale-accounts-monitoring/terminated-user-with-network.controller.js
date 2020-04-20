(function () {
    'use strict';

    function TerminatedUserWithNetworkController ($state, tableInitialState, terminatedTableInitialSettings,
        terminatedUsersWithNetworkResource, fsNanobarAutomationService) {

        // Put injections on instance
        this.state = _.merge({}, $state.current.data);
        this.terminatedUsersWithNetworkResource = _.merge({}, terminatedUsersWithNetworkResource);
        this.terminatedUsersWithNetworkTableSettings = _.merge({}, terminatedTableInitialSettings);
        this.mainState = {
            terminated_users_table: tableInitialState
        };


        this.NANOBAR_ID = 'reports';
        this.dataFetchDelegate = (promise) => {
            fsNanobarAutomationService.addPromise(this.NANOBAR_ID, promise);
        };

        this.init();
    }

    _.merge(TerminatedUserWithNetworkController.prototype, {

        init: function () {}
    });

    TerminatedUserWithNetworkController.$inject = ['$state', 'tableInitialState', 'terminatedTableInitialSettings',
        'terminatedUsersWithNetworkResource', 'fsNanobarAutomationService'];
    angular.module('Fortscale.layouts.reports')
        .controller('TerminatedUserWithNetworkController', TerminatedUserWithNetworkController);
}());
