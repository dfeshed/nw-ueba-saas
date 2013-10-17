angular.module("Fortscale").controller("DashboardWidgetController", ["$scope", "dashboards", "utils", function($scope, dashboards, utils){
    $scope.dashboard = null;
    $scope.isDashboardWidget = true;

    var currentDashboardId = $scope.view.settings.defaultDashboardId || $scope.view.settings.dashboardId;
    currentDashboardId = utils.strings.parseValue(currentDashboardId, {}, $scope.dashboardParams);

    if (currentDashboardId && (!$scope.view.settings.requiredParams || allParamsAvailable($scope.dashboardParams)))
        setDashboard(currentDashboardId);

    if ($scope.view.settings.dashboardId){
        $scope.$on("dashboardParamsChange", function(e, params){
            var dashboardId = utils.strings.parseValue($scope.view.settings.dashboardId, {}, params);
            if (dashboardId && dashboardId !== currentDashboardId){
                if (!$scope.view.settings.requiredParams || allParamsAvailable(params))
                    setDashboard(dashboardId);
            }
        });
    }

    function allParamsAvailable(params){
        var paramValue;
        for(var i= 0; i < $scope.view.settings.requiredParams.length; i++){
            paramValue = params[$scope.view.settings.requiredParams[i]];
            if (!paramValue && paramValue !== 0)
                return false;
        }

        return true;
    }

    function setDashboard(dashboardId){
        currentDashboardId = dashboardId;

        dashboards.getDashboardById(dashboardId).then(function(dashboard){
            $scope.dashboard = dashboard;
            $scope.dashboardParams = dashboard.params || {};
        }, $scope.report.error);
    }
}]);