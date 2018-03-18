(function () {
    'use strict';

    angular.module("Fortscale")
        .run(["widgetViews", function (widgetViews) {
            widgetViews.registerView("dashboardWidget", {});
        }])
        .controller("DashboardWidgetController", ["$scope", "dashboards", "utils", "$timeout", "eventBus", "state",
            function ($scope, dashboards, utils, $timeout, eventBus, state) {
                var setDashboardTimeout,
                    currentDashboardId;

                $scope.dashboard = null;
                $scope.isDashboardWidget = true;

                $scope.$on("$destroy", function () {
                    $timeout.cancel(setDashboardTimeout);
                    state.onStateChange.unsubscribe(onParamsChange);
                });

                function allParamsAvailable (params) {
                    var paramValue;
                    for (var i = 0; i < $scope.view.settings.requiredParams.length; i++) {
                        paramValue = params[$scope.view.settings.requiredParams[i]];
                        if (!paramValue && paramValue !== 0) {
                            return false;
                        }
                    }

                    return true;
                }

                function setDashboard (dashboardId) {
                    $scope.dashboard = null;
                    setDashboardTimeout = $timeout(function () {
                        currentDashboardId = dashboardId;

                        dashboards.getDashboardById(dashboardId).then(function (dashboard) {
                            $scope.dashboard = angular.extend(dashboard, $scope.view.settings);
                            if ($scope.dashboard.params && $scope.mainDashboardParams) {
                                utils.objects.extend($scope.mainDashboardParams, $scope.dashboard.params);
                            }

                        }, function (error) {
                            console.error("Can't load dashboard: ", error);
                        });
                    }, 40);
                }

                function init () {
                    var widgetParams = state.currentParams;
                    currentDashboardId = utils.strings.parseValue($scope.view.settings.defaultDashboardId ||
                        $scope.view.settings.dashboardId, {}, widgetParams);

                    if (currentDashboardId &&
                        (!$scope.view.settings.requiredParams || allParamsAvailable(widgetParams))) {
                        setDashboard(currentDashboardId);
                    }

                    if ($scope.view.settings.dashboardId) {
                        state.onStateChange.subscribe(onParamsChange);
                    }
                }

                function onParamsChange (e, params) {
                    var dashboardId = utils.strings.parseValue($scope.view.settings.dashboardId, {},
                        state.currentParams);
                    if (dashboardId && dashboardId !== currentDashboardId) {
                        currentDashboardId = dashboardId;

                        if (!$scope.view.settings.requiredParams || allParamsAvailable(params)) {
                            setDashboard(dashboardId);
                        }
                    }

                    if ($scope.dashboard) {
                        $scope.dashboard.update();
                    }
                }

                init();
            }]);
}());
