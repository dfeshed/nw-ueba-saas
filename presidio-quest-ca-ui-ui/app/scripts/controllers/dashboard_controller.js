(function () {
    'use strict';

    angular.module("Fortscale")
        .controller("DashboardController",
        ["$scope", "transforms", "utils", "eventBus", "reports", "tags", "menus", "page", "state", "events",
            function ($scope, transforms, utils, eventBus, reports, tags, menus, page, state, events) {
                var defaultMessages = {
                    requiredParams: {text: "Missing parameters.", type: "error"}
                };

                function setDashboardFieldValues(dashboard) {
                    if (dashboard) {
                        if (dashboard.title) {
                            page.setPageTitle(dashboard.title);
                        }

                        //if browserTitle attribute is found in dashboard json file it overrides the page title
                        // with its value
                        if (dashboard.browserTitle) {
                            page.setPageTitle(dashboard.browserTitle);
                        }

                        if (dashboard.navigation) {
                            parseNavigation(dashboard.navigation, state.currentParams);
                        }

                        if (dashboard.iconUrl) {
                            var dashboardIconUrl = utils.strings.parseValue(dashboard.iconUrl, {},
                                state.currentParams);
                            $scope.dashboardIconUrl = dashboardIconUrl || null;
                        }

                        if (dashboard.details) {
                            dashboard.currentDetails = {};
                            var detailValue, detailName;
                            for (detailName in dashboard.details) {
                                if( dashboard.details.hasOwnProperty( detailName ) ) {
                                    detailValue = dashboard.details[detailName];
                                    if (typeof(detailValue) === "string") {
                                        dashboard.currentDetails[detailName] = utils.strings.parseValue(detailValue, {},
                                            state.currentParams);
                                    }
                                }
                            }

                            setDashboardDetailsMenu(dashboard);

                            if (dashboard.details.tags) {
                                tags.getTags(dashboard.details.tags, state.currentParams || {})
                                    .then(function (dashboardTags) {
                                        $scope.dashboard.details.parsedTags = dashboardTags;
                                    });
                            }
                        }

                        dashboard._renderHeader = dashboard.renderHeader !== false &&
                            (dashboard.controls && dashboard.controls.length ||
                            dashboard.title || dashboard.description);
                        if (!(dashboard.allRequiredParamsAvailable = checkRequiredParams())) {
                            dashboard._messages = [dashboard.messages && dashboard.messages.requiredParams ||
                            defaultMessages.requiredParams];
                        }
                        else {
                            dashboard._messages = null;
                        }
                    }
                }
                function onParamsChange() {
                    if ($scope.dashboard) {
                        $scope.dashboard.checkReady();
                        $scope.dashboard.update();
                        setDashboardFieldValues($scope.dashboard);
                    }
                }


                function setDashboardDetailsMenu(dashboard) {
                    if (dashboard.details.menu) {
                        menus.getMenu(dashboard.details.menu, {}, state.currentParams).then(function (menu) {
                            $scope.dashboard.details.parsedMenu = menu;
                        });
                    }
                }

                function checkRequiredParams() {
                    if (!$scope.dashboard.requiredParams) {
                        return true;
                    }

                    for (var i = 0, param; undefined !== (param = $scope.dashboard.requiredParams[i]); i++) {
                        if (!state.currentParams[param]) {
                            return false;
                        }
                    }

                    return true;
                }

                function parseNavigation(rootItem, params) {
                    if (rootItem.url) {
                        rootItem.href = utils.strings.parseValue(rootItem.url, {}, params);
                    }

                    if (rootItem.children) {
                        rootItem.children.forEach(function (item) {
                            parseNavigation(item, params);

                        });

                    }
                }

                function onDashboard(dashboard) {
                    setDashboardFieldValues(dashboard);

                    if (dashboard) {
                        dashboard.paramsReady = !dashboard.controls;
                    }
                }

                setDashboardFieldValues($scope.dashboard);


                $scope.getDashboardParams = function () {
                    return state.currentParams;
                };

                $scope.dashboardSubtitle = null;
                $scope.dashboardIconUrl = null;

                $scope.setParams = state.setParams;
                $scope.showDashboardNameDescription = function ($event) {
                    events.triggerDashboardEvent({
                        action: "showTooltip",
                        actionOptions: {
                            text: $scope.dashboardNameDescription,
                            position: {top: $event.clientY, left: $event.clientX}
                        }
                    });
                };

                $scope.$on("$destroy", function (e, data) {
                    state.onStateChange.unsubscribe(onParamsChange);
                });
                $scope.$on("refresh", function () {
                    if ($scope.dashboard) {
                        $scope.dashboard.runReports();
                    }
                });

                state.onStateChange.subscribe(onParamsChange);


                $scope.$on("onMainDashboard", function (e, data) {
                    onDashboard(data.dashboard);
                });

                $scope.$watch("dashboard", onDashboard);

                if ($scope.dashboard) {
                    onDashboard($scope.dashboard);
                }
            }]);
}());
