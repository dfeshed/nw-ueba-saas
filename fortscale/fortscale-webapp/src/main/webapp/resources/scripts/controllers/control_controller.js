angular.module("Fortscale").controller("WidgetControlController", ["$scope", "controls", function($scope, controls){
    function init(){
        controls.initControl($scope.control, $scope.getWidgetParams($scope.widget));
        if ($scope.control.refreshOn){
            $scope.$on("dashboardParamsChange", function(e, changedParams){
                for(var i= 0; i < $scope.control.refreshOn.length; i++){
                    if (changedParams[$scope.control.refreshOn[i]] !== undefined){
                        controls.initControl($scope.control, $scope.getWidgetParams());
                        return;
                    }
                }
            });
        }
    }

    init();
}]);