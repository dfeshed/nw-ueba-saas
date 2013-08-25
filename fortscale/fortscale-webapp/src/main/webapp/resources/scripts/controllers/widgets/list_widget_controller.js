angular.module("Fortscale").controller("ListWidgetController", ["$scope", function($scope){
    $scope.items = $scope.view.data;
    $scope.$on("onWidgetData", function(e, data){
        $scope.items = $scope.view.data;
    });
}]);