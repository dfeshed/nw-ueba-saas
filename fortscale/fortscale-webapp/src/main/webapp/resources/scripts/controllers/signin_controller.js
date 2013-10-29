angular.module("FortscaleSignin").controller("SigninController", ["$scope", "auth", "utils", function($scope, auth, utils){
    var queryParams = utils.url.getQueryParams();
    if (queryParams.username){
        $scope.email = queryParams.username;
    }
    else if (/change_password/.test(window.location.href))
        $scope.error = { message: "No username specified." };
    else
        $scope.email = auth.getLastLoggedInUser();

    if ($scope.email)
        $scope.focusOnPassword = true;

    $scope.rememberMe = true;

    function validateEmailAndPasswords(){
        if (!$scope.email || !$scope.password){
            $scope.error = { message: "Please enter email and password" };
            return;
        }

        if (!auth.validateUsername($scope.email)){
            $scope.error = { message: "Invalid email address" };
            return;
        }

        if (!$scope.password){
            $scope.error = { message: "Please enter password" };
            return;
        }

        return true;
    }

    function validatePasswordsMatch(){
        if (!$scope.passwordConfirm){
            $scope.error = { message: "Please enter password confirmation" };
            return;
        }

        if ($scope.password !== $scope.passwordConfirm){
            $scope.error = { message: "Passwords don't match" };
            return;
        }

        return true;
    }

    $scope.clearError = function(){
        $scope.error = null;
    };

    $scope.signIn = function(){
        var validated = validateEmailAndPasswords();
        if (!validated)
            return;

        doLogin();
    };

    function doLogin(){
        auth.login($scope.email, $scope.password, $scope.rememberMe).then(function(){
            window.location.href = window.location.href.replace(/\/[\w_\-]+\.html.*/, "/index.html" + (queryParams.redirect || "#/d/main"));
        }, function(error){
            $scope.error = error;
        });
    }
    $scope.signUp = function(){
        if (!$scope.firstName || !$scope.lastName){
            $scope.error = { message: "Please enter first name and last name" };
            return;
        }

        var validated = validateEmailAndPasswords();
        if (!validated)
            return;

        validated = validatePasswordsMatch();
        if (!validated)
            return;

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

    $scope.changePassword = function(){
        var validated = validateEmailAndPasswords();
        if (!validated)
            return;

        validated = validatePasswordsMatch();
        if (!validated)
            return;

        auth.changePassword($scope.email, $scope.currentPassword, $scope.password).then(function(){
            doLogin();
        }, function(error){
            $scope.error = error;
        });
    };
}]);