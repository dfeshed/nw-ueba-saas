module Fortscale.layouts.user {
    'use strict';

    angular.module('Fortscale.layouts.user')
        .config([
            '$stateProvider',
            function ($stateProvider) {
                $stateProvider
                // This state (And controller) is used as a go between for cases where you only have a user's
                // normalized username and not he's id
                    .state('username', {
                        url: '/username/:username',
                        controller: class UsernameController {
                            static $inject = ['$stateParams', 'userUtils', '$location', '$log', 'toastrService'];
                            constructor ($stateParams, userUtils, $location, $log, toastrService) {
                                userUtils.getUserByUsername($stateParams.username)
                                    .then((user) => {
                                        $location.path(`/user/${user.id}/baseline`);
                                        $location.replace();
                                    })
                                    .catch((err) => {
                                        toastrService.error(
                                            'There was an unexpected server error while trying to load a user profile',
                                            'User Profile Error');
                                        $log.error(err);
                                        $location.path(`/overview`);
                                        $location.replace();
                                    });
                            }
                        },
                    })
                    .state('user', {
                        url: '/user/:userId',
                        templateUrl: 'app/layouts/user/user.view.html',
                        controller: 'UserController',
                        controllerAs: 'userCtrl'
                    })
                    .state('user.baseline', {
                        url: '/baseline',
                        // controller: 'userAttributesController',
                        // controllerAs: 'attrCtrl',
                        // templateUrl: 'app/layouts/user/components/user-attributes/user-attributes.view.html'
                        templateUrl: 'app/layouts/user/components/user-alert-overview/user-alert-overview.view.html',
                        controller: 'userAlertOverviewController',
                        controllerAs: 'userAlertOverviewController'
                    })
                    .state('user.alert-overview', {
                        url: '/alert/:alertId',
                        templateUrl: 'app/layouts/user/components/user-alert-overview/user-alert-overview.view.html',
                        controller: 'userAlertOverviewController',
                        controllerAs: 'userAlertOverviewController'
                    })
                    .state('user.indicator', {
                        url: '/alert/:alertId/indicator/:indicatorId',
                        templateUrl: 'app/layouts/user/components/user-indicator/user-indicator.view.html',
                        controller: 'userIndicatorController',
                        controllerAs: 'userIndicatorCtrl'
                    });


            }
        ]);

}
