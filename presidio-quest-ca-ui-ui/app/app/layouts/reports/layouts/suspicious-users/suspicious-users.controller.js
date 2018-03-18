(function () {
    'use strict';

    function RiskyTaggedUsersController ($state, $stateParams, dataEntities, tableResource, tableSettings,
        dataEntitiesList, fsNanobarAutomationService) {

        // Put injections on instance
        this.state = _.merge({}, $state.current.data);
        this.$stateParams = $stateParams;


        this.tagName = $stateParams.tagName;

        this.mainState = _.merge({

            userTags: {
                value: this.tagName
            },
            users_table: {
                value: {
                    pageSize: 20,
                    page: 1,
                    sortBy: 'score',
                    sortDirection: 'DESC'
                }
            }
        });

        this.tableResource = _.merge({}, tableResource);
        this.tableSettings = _.merge({}, tableSettings);

        this.NANOBAR_ID = 'reports';
        this.dataFetchDelegate = (promise) => {
            fsNanobarAutomationService.addPromise(this.NANOBAR_ID, promise);
        };

        this.init();
    }

    _.merge(RiskyTaggedUsersController.prototype, {


        init: function () {

        }


    });

    RiskyTaggedUsersController.$inject =
        ['$state', '$stateParams', 'dataEntities', 'tableResource', 'tableSettings', 'dataEntitiesList',
            'fsNanobarAutomationService'];
    angular.module('Fortscale.layouts.reports')
        .controller('RiskyTaggedUsersController', RiskyTaggedUsersController);
}());
