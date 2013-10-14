angular.module("FortscaleSignin").controller("SigninController", ["$scope", "auth", function($scope, auth){
    function validateEmailAndPasswords(){
        if (!$scope.email || !$scope.password){
            $scope.error = "Please enter email and password";
            return;
        }

        if (!auth.validateUsername($scope.email)){
            $scope.error = "Invalid email address";
            return;
        }

        if (!$scope.password){
            $scope.error = "Please enter password";
            return;
        }

        return true;
    }

    $scope.signIn = function(){
        var validated = validateEmailAndPasswords();
        if (!validated)
            return;

        auth.login($scope.email, $scope.password, $scope.rememberMe).then(function(){
            window.location.href = window.location.href.replace(/signin\.html.*/, "index.html#/d/main");
        }, function(error){
            $scope.error = error;
        })
    };

    $scope.signUp = function(){
        if (!$scope.firstName || !$scope.lastName){
            $scope.error = "Please enter first name and last name";
            return;
        }

        var validated = validateEmailAndPasswords();
        if (!validated)
            return;

        if (!$scope.passwordConfirm){
            $scope.error = "Please enter password confirmation";
            return;
        }

        if ($scope.password !== $scope.passwordConfirm){
            $scope.error = "Passwords don't match";
            return;
        }

        auth.signUp({
            firstName: $scope.firstName,
            lastName: $scope.lastName,
            username: $scope.email,
            password: $scope.password
        }).then(function(){
            window.location.href = window.location.href.replace(/signup\.html.*/, "signin.html");
        }, function(error){
            $scope.error = error;
        });
    };
}]);