module Fortscale.layouts.overview {

    'use strict';

    angular.module('Fortscale.layouts.overview')
        .config([
            '$stateProvider',
            function ($stateProvider, overviewPageResolve) {
                $stateProvider
                    .state('overview', {
                        url: '/overview',
                        templateUrl: 'app/layouts/overview/overview.view.html',
                        controller: 'OverviewController',
                        controllerAs: 'overviewCtrl'
                    });
            }
        ]);

}
