(function () {
    'use strict';

    angular.module("Fortscale")
        .config(function ($routeProvider, $locationProvider) {

            /**
             * LEGACY
             * Setup Routes
             */
            $routeProvider
                .when('/d/:dashboardId', {
                    templateUrl: 'views/pages/main_dashboard.html',
                    controller: "MainDashboardController",
                    reloadOnSearch: false
                })
                .when('/d/:dashboardId/:entityId', {
                    templateUrl: 'views/pages/main_dashboard.html',
                    controller: 'MainDashboardController',
                    reloadOnSearch: false,
                    resolve: {}
                })
                .when('/account', {
                    templateUrl: 'views/pages/account.html',
                    controller: "AccountSettingsController"
                })
                .when('/global_settings', {
                    templateUrl: 'views/pages/global_settings.html',
                    controller: "GlobalSettingsController"
                })
            /** Ghost routes to avoid capture of ui routes by angular router **/
                .when('/alerts/:alertId', {})
                .when('/alerts/:alertId/:evidenceId', {})
                .when('/alerts/:alertId/evidence/:evidenceId', {})
                .when('/username/:username', {})
                .when('/user/:userId', {})
                .when('/user/:userId/baseline', {})
                .when('/reports/:reportId/:subReportId', {})
                .when('/configuration/:stateName', {})
                .when('/users', {})
            /** END OF Ghost routes **/
                .when('/:dashboardId/:entityId/:subDashboardId', {
                    templateUrl: 'views/pages/main_dashboard.html',
                    controller: "MainDashboardController",
                    reloadOnSearch: false
                })
                .when('/:dashboardId/:subDashboardId', {
                    templateUrl: 'views/pages/main_dashboard.html',
                    controller: "MainDashboardController",
                    reloadOnSearch: false
                });

            $locationProvider.html5Mode(false);
        })
        .config([
            '$sceProvider',
            '$stateProvider',
            '$urlRouterProvider',
            'alertsPageResolve',
            function ($sceProvider, $stateProvider, $urlRouterProvider, alertsPageResolve) {
                // LEGACY
                // Completely disable SCE.
                // For demonstration purposes only! Do not use in new projects.
                $sceProvider.enabled(false);

                //Configuration for UI-Router
                $urlRouterProvider.otherwise('/overview');

                $stateProvider

                /**
                 * Alerts Page
                 */

                    .state('alerts', {
                        url: '/alerts',
                        templateUrl: 'app/layouts/alerts/alerts.view.html',
                        controller: 'AlertsController',
                        controllerAs: 'alerts',
                        resolve: alertsPageResolve
                    })

                /**
                 * END OF Alerts Page
                 */

                /**
                 * Users Page
                 */

                    .state('users', {
                        url: '/users',
                        templateUrl: 'app/layouts/users/users.view.html',
                        controller: 'UsersController',
                        controllerAs: 'usersCtrl'
                    })

                /**
                 * END OF Alerts Page
                 */

                /**
                 * UI Config Page
                 */

                    .state('configuration', {
                        url: '/configuration',
                        templateUrl: 'app/layouts/configuration/configuration.view.html',
                        controller: 'ConfigurationController',
                        controllerAs: 'configuration'
                    })
                    .state('configuration.configForm', {
                        url: '/:stateName',
                        templateUrl: 'app/layouts/configuration/configuration-form.view.html',
                        controller: 'ConfigurationFormController',
                        controllerAs: 'configFormCtrl'
                    })

                /**
                 * END OF UI Config Page
                 */

                    // We are using NG-ROUTE for legacy pages (before 2.0) and UI-ROUTER.
                    // To be able to work with both, we have to duplicate the states of ng-route
                    // into UI-Router, without template.
                    // All the dummy states (states that use NG-ROUTE) start in the following
                    // configuration with ng-route,
                    // Just as internal convention, for example
                    // ".state('ng-route-d-dashboardId'," is duplication of $routeProvider's
                    // ".when('/d/:dashboardId', "
                    .state('ng-route-d-dashboardId', {
                        url: '/d/:dashboardId'
                    })
                    .state('explore', {
                        url: '/d/:dashboardId/:entityId'
                    })
                    .state('ng-route-d-dashboardId-entity-subDashboardId', {
                        url: '/:dashboardId/:entityId/:subDashboardId'
                    })
                    .state('ng-route-d-dashboardId-subDashboardId', {
                        url: '/:dashboardId/:subDashboardId'
                    })
                    .state('ng-route-count', {
                        url: '/account'
                    })
                    .state('ng-route-global-settings', {
                        url: '/global_settings'
                    });
            }]);
}());
