(function () {
    'use strict';

    angular.module("Fortscale")
        .controller("AccountSettingsController", ["$scope", "auth", "page", function ($scope, auth, page) {
            var originalData;

            $scope.modified = false;
            $scope.onChanged = function () {
                $scope.modified = true;
            };

            page.setPageTitle("Account Settings");

            auth.getCurrentUser().then(function (user) {
                originalData = angular.copy(user);
                $scope.account = user;
            });

            $scope.changePassword = function () {
                $scope.passwordError = null;
                $scope.success = null;

                if (!$scope.currentPassword) {
                    $scope.passwordError = {message: "Please enter your current password."};
                    return;
                }

                if (validatePasswordsMatch()) {
                    if ($scope.account) {
                        doChangePassword();
                    }
                }
            };

            $scope.saveUser = function () {
                $scope.success = null;
                $scope.error = null;

                if (!$scope.userPassword) {
                    $scope.passwordRequired = true;
                    return;
                }

                auth.editUser({
                    username: $scope.account.userName,
                    password: $scope.userPassword,
                    firstName: $scope.account.firstName,
                    lastName: $scope.account.lastName
                }).then(function () {
                    $scope.success = {message: "User saved successfully."};
                }, function (error) {
                    $scope.error = {message: "Error saving user."};
                    console.error("Error saving user: ", error);
                });

                $scope.passwordRequired = false;
            };

            $scope.reset = function () {
                $scope.account = angular.copy(originalData);
                $scope.modified = false;
                $scope.currentPassword = $scope.newPassword = $scope.newPasswordConfirm = $scope.userPassword = "";
                $scope.passwordRequired = false;
                $scope.error = null;
            };

            function doChangePassword() {
                auth.changePassword($scope.account.userName, $scope.currentPassword, $scope.newPassword)
                    .then(function () {
                        $scope.passwordSuccess = {message: "Password changed successfully."};
                        $scope.currentPassword = $scope.newPassword = $scope.newPasswordConfirm = "";
                    }, function (error) {
                        $scope.passwordError = {message: error.message};
                    });
            }

            function validatePasswordsMatch() {
                if (!$scope.newPasswordConfirm) {
                    $scope.passwordError = {message: "Please enter password confirmation."};
                    return;
                }

                if ($scope.newPassword !== $scope.newPasswordConfirm) {
                    $scope.passwordError = {message: "Passwords don't match."};
                    return;
                }

                if ($scope.currentPassword === $scope.newPassword) {
                    $scope.passwordError = {message: "The new password can't be identical to the current one."};
                    return;
                }

                return true;
            }
        }]);
}());
