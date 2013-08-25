angular.module("Fortscale").controller("MainDashboardController", ["$scope", "$routeParams", "dashboards", function($scope, $routeParams, dashboards){
    dashboards.getDashboardById($routeParams.dashboardId).then(function(dashboard){
        $scope.dashboard = dashboard;
        $scope.$broadcast("onMainDashboard", { dashboard: dashboard });
    }, $scope.report.error);
}]);