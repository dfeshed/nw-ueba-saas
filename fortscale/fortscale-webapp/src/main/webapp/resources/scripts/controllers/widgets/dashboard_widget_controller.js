angular.module("Fortscale").controller("DashboardWidgetController", ["$scope", "dashboards", "widgets", function($scope, dashboards, widgets){
    $scope.dashboard = null;
    $scope.isDashboardWidget = true;

    var currentDashboardId = $scope.view.settings.defaultDashboardId || $scope.view.settings.dashboardId;

    if (currentDashboardId)
        setDashboard(currentDashboardId);

    if ($scope.view.settings.dashboardId){
        $scope.$on("dashboardParamsChange", function(e, params){
            var dashboardId = widgets.parseFieldValue($scope.view.settings, $scope.view.settings.dashboardId, {}, 0, params);
            if (dashboardId && dashboardId !== currentDashboardId)
                setDashboard(dashboardId);
        });
    }

    function setDashboard(dashboardId){
        currentDashboardId = dashboardId;

        dashboards.getDashboardById(dashboardId).then(function(dashboard){
            $scope.dashboard = dashboard;
            $scope.dashboardParams = dashboard.params || {};
        }, $scope.report.error);
    }
}]);