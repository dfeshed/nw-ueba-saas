angular.module("Fortscale").controller("ViewController", ["$scope", "conditions", function($scope, conditions){
    function setViewShow(){
        if ($scope.view.show){
            var previousValue = !!$scope.view.show.value;
            $scope.view.show.value = conditions.validateConditions($scope.view.show.conditions, $scope.view.data, $scope.getWidgetParams());

            if ($scope.view.show.value !== previousValue){
                if ($scope.view.show.value)
                    $scope.$broadcast("show");
                else
                    $scope.$broadcast("hide");
            }
        }
    }

    setViewShow();
}]);