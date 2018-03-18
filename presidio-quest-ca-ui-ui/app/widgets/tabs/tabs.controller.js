(function () {
    'use strict';

    angular.module("Fortscale").controller("TabsWidgetController",
        ["$scope", "eventBus", "dashboards", "events", "state", function ($scope, eventBus, dashboards, events, state) {
            var selectedDashboardParam;

            $scope.dashboard = null;

            $scope.$on("$destroy", function () {
                eventBus.unsubscribe("dashboardParamsChange", onParamsChange);
            });

            $scope.initDashboardForAtab = function (tab) {
                if (tab.dashboard) {
                    $scope.dashboard = dashboards.createDashboard(tab.dashboard);
                }
            };

            $scope.selectTab = function (tab, tabIndex, isDefaultTab) {

                if (!setCurrentTab(tabIndex)) {
                    return;
                }
                else if (tab.dashboardId) {
                    dashboards.getDashboardById(tab.dashboardId).then(function (dashboard) {
                        tab.dashboard = $scope.dashboard = dashboard;
                    });
                }

                if ($scope.view.settings.events) {
                    angular.forEach($scope.view.settings.events, function (eventSettings) {
                        if (eventSettings.eventName === "select") {
                            var eventSettingsCopy = angular.copy(eventSettings);
                            if (isDefaultTab && eventSettingsCopy.actionOptions) {
                                eventSettingsCopy.actionOptions.updateUrl = false;
                            }

                            events.triggerDashboardEvent(eventSettingsCopy, tab, state.currentParams);
                        }
                    });
                }

                if ($scope.view.settings.onSelect) {
                    $scope.view.settings.onSelect(tab);
                }

            };

            function setCurrentTabFromDashboardParam () {
                var currentlySelectedTab = $scope.getWidgetParams()[selectedDashboardParam];
                if (currentlySelectedTab) {
                    var tabToSelect = findTab(currentlySelectedTab);
                    if (tabToSelect) {

                        if (setCurrentTab(tabToSelect.tabIndex)) {

                            return true;
                        }
                        else {
                            return -1;
                        }
                    }
                }

                return false;
            }

            function onParamsChange (e, changedParams) {
                if (changedParams[selectedDashboardParam]) {
                    var tabToSelect = findTab(changedParams[selectedDashboardParam]);
                    if (tabToSelect) {
                        setCurrentTab(tabToSelect.tabIndex);
                    } else {
                        $scope.currentTab = null;
                        $scope.currentTabIndex = null;
                    }
                }
            }

            function init () {
                var selectedMatch;

                if ($scope.view.settings.tab && $scope.view.settings.tab.selected) {
                    selectedMatch = $scope.view.settings.tab.selected.match(/^@(.*)$/);
                    if (selectedMatch) {
                        selectedDashboardParam = selectedMatch[1];
                        eventBus.subscribe("dashboardParamsChange", onParamsChange);
                    }
                    else {
                        console.error("Invalid selected property for tabs - " +
                            "must be a dashboard param and start with '@'.");
                    }
                }
                else if ($scope.view.settings.tabs && $scope.view.settings.selectedTabId) {
                    selectedMatch = $scope.view.settings.selectedTabId.match(/^@(.*)$/);
                    if (selectedMatch) {
                        selectedDashboardParam = selectedMatch[1];
                    }
                }

                $scope.$on("onWidgetData", function () {
                    if ($scope.view.data) {
                        setCurrentTabFromDashboardParam();
                    }
                });

                if (!setCurrentTabFromDashboardParam()) {
                    selectFirstTab();
                }

                if ($scope.view.settings.refreshOn) {
                    angular.forEach($scope.view.settings.refreshOn, function (refreshParam) {
                        $scope.$on("dashboardParamsChange", function (e, changedParams) {
                            if (changedParams[refreshParam]) {
                                setCurrentTabFromDashboardParam();
                            }
                        });
                    });
                }

            }

            function selectFirstTab () {
                if ($scope.view.settings.tabs && $scope.view.settings.tabs.length) {
                    $scope.selectTab($scope.view.settings.tabs[0], 0, true);
                } else if ($scope.view.settings.tab && $scope.view.data && $scope.view.data.length) {
                    $scope.selectTab($scope.view.data[0], 0, true);
                }
            }

            function setCurrentTab (tabIndex) {
                if (tabIndex !== $scope.currentTabIndex && $scope.view.data) {
                    $scope.currentTab = $scope.view.data[tabIndex];
                    $scope.initDashboardForAtab($scope.currentTab);
                    $scope.currentTabIndex = tabIndex;

                    return true;
                }

                return false;
            }

            function findTab (id) {
                if (!id || !$scope.view.data) {
                    return null;
                }

                for (var i = 0, tab; !!(tab = $scope.view.data[i]); i++) {
                    if (tab.id === id) {
                        return {tab: tab, tabIndex: i};
                    }
                }

                return null;
            }

            init();
        }]);
}());
