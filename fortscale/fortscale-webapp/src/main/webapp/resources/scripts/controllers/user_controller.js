angular.module("Fortscale").controller("UserController", ["$scope", function($scope){
    $scope.currentGraph = "score";

    $scope.openGraph = function(graphName){
        if ($scope.currentGraph === graphName)
            $scope.currentGraph = null;
        else
            $scope.currentGraph = graphName;
    }
}]);
