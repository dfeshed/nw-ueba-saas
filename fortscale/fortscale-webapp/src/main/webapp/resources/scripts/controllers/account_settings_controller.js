angular.module("Fortscale").controller("AccountSettingsController", ["$scope", "auth", function($scope, auth){
    var originalData;

    $scope.modified = false;
    $scope.onChanged = function(){
        $scope.modified = true;
    };

    auth.getCurrentUser().then(function(user){
        $scope.account = user;
    });

    $scope.changePassword = function(){
        if (!$scope.currentPassword){
            $scope.error = { message: "Please enter your current password." };
            return;
        }

        if (validatePasswordsMatch()){
            auth.changePassword($scope.account.userName, $scope.currentPassword, $scope.newPassword).then(function(){
                $scope.success = { message: "Password change successfully." };
                $scope.currentPassword = $scope.newPassword = $scope.newPasswordConfirm = "";
            }, function(error){
                $scope.error = { message: "Failed to change password. (Server error)" };
            });
        }
    };

    function validatePasswordsMatch(){
        if (!$scope.newPasswordConfirm){
            $scope.error = { message: "Please enter password confirmation." };
            return;
        }

        if ($scope.newPassword !== $scope.newPasswordConfirm){
            $scope.error = { message: "Passwords don't match." };
            return;
        }

        return true;
    }
}]);