angular.module("Fortscale").controller("AccountSettingsController", ["$scope", "auth", function($scope, auth){
    var originalData;

    $scope.modified = false;
    $scope.onChanged = function(){
        $scope.modified = true;
    };

    auth.getCurrentUser().then(function(user){
        $scope.account = user;
    });
}]);