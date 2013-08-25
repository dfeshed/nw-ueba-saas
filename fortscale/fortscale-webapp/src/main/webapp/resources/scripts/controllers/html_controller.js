angular.module("Fortscale").controller("HtmlController", ["$scope", function($scope){
    $scope.setPageTitle = function(pageTitle){
        $scope.title = "Fortscale - " + pageTitle;
    };
}]);