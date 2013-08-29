angular.module("FortscaleSignin").controller("SigninController", ["$scope", "auth", function($scope, auth){
    $scope.signIn = function(){
        if (!auth.validateUsername($scope.email)){
            $scope.error = "Invalid email address";
            return;
        }

        if (!$scope.password){
            $scope.error = "Please enter password";
            return;
        }

        auth.login($scope.email, $scope.password, $scope.rememberMe).then(function(){
            $scope.error = null;
            window.location.href = window.location.href.replace(/signin\.html.*/, "index.html");
        }, function(error){
            $scope.error = error;
        })
    };
}]);