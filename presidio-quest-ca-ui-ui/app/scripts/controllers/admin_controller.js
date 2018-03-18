(function () {
    'use strict';

    angular.module("FortscaleAdmin").controller("AdminController", ["$scope", "auth", function ($scope, auth) {
        auth.getAllUsers().then(function (users) {
            $scope.users = users;
        }, function (error) {
            $scope.error = error;
        });

        auth.getCurrentUser().then(function (user) {
            $scope.currentUser = user;
        });

        function validateEmailAndPasswords() {
            if (!$scope.email || !$scope.password) {
                $scope.error = "Please enter email and password";
                return;
            }

            if (!auth.validateUsername($scope.email)) {
                $scope.error = "Invalid email address";
                return;
            }

            if (!$scope.password) {
                $scope.error = "Please enter password";
                return;
            }

            return true;
        }

        function validatePasswordsMatch() {
            if (!$scope.passwordConfirm) {
                $scope.error = "Please enter password confirmation";
                return;
            }

            if ($scope.password !== $scope.passwordConfirm) {
                $scope.error = "Passwords don't match";
                return;
            }

            return true;
        }

        $scope.renewPasswordModal = {
            close: function () {
                $scope.showRenewPasswordModal = false;
                $scope.renewPasswordModal.error = null;
                $scope.renewPasswordModal.adminPassword = null;
                $scope.renewPasswordModal.renewPasswordUser = null;
                $scope.renewPasswordModal.newPassword = null;
                $scope.renewPasswordModal.newPasswordVerify = null;
            },
            update: function () {
                if (!$scope.renewPasswordModal.adminPassword) {
                    $scope.renewPasswordModal.error = "Please enter your password.";
                    return $scope.renewPasswordModal.error;
                }

                if (!$scope.renewPasswordModal.newPassword) {
                    $scope.renewPasswordModal.error = "Please enter a new password.";
                    return $scope.renewPasswordModal.error;
                }

                if (!$scope.renewPasswordModal.newPasswordVerify) {
                    $scope.renewPasswordModal.error = "Please verify the new password.";
                    return $scope.renewPasswordModal.error;
                }

                if ($scope.renewPasswordModal.newPassword !== $scope.renewPasswordModal.newPasswordVerify) {
                    $scope.renewPasswordModal.error = "Passwords don't match.";
                    return $scope.renewPasswordModal.error;
                }

                auth.renewPassword(
                    $scope.renewPasswordModal.renewPasswordUser.userName,
                    $scope.renewPasswordModal.adminPassword,
                    $scope.renewPasswordModal.newPassword
                )
                    .then(function () {
                        $scope.renewPasswordModal.error = null;
                        alert("Password for user " + $scope.renewPasswordModal.renewPasswordUser.fullName +
                            " has been changed.");
                        $scope.renewPasswordModal.close();
                    }, function (error) {
                        $scope.renewPasswordModal.error = error.status === 404 ? "Renew API not found." :
                        error.message || error.data;
                    });
            }
        };

        $scope.signIn = function () {
            var validated = validateEmailAndPasswords();
            if (!validated) {
                return;
            }
            
        };

        $scope.signUp = function () {
            if (!$scope.firstName || !$scope.lastName) {
                $scope.error = "Please enter first name and last name";
                return;
            }

            var validated = validateEmailAndPasswords();
            if (!validated) {
                return;
            }

            validated = validatePasswordsMatch();
            if (!validated) {
                return;
            }

            auth.signUp({
                firstName: $scope.firstName,
                lastName: $scope.lastName,
                username: $scope.email,
                password: $scope.password
            }).then(function () {
                // window.location.href = window.location.href.replace(/signup\.html.*/, "signin.html");
            }, function (error) {
                $scope.error = error;
            });
        };

        $scope.deleteUser = function (user) {
            if (confirm("Are you sure you wish to delete user '" + user.userName + "'?")) {
                auth.deleteUser(user.userName).then(function () {
                    $scope.users.splice($scope.users.indexOf(user), 1);
                }, function (error) {
                    $scope.error = error;
                });
            }
        };

        $scope.validateNewUser = function () {
            var valid = $scope.newUser.username &&
                $scope.newUser.firstName &&
                $scope.newUser.lastName &&
                $scope.newUser.password;

            if (valid) {
                valid = valid | auth.validateUsername($scope.newUser.username);
            }

            $scope.newUserValidated = valid;
        };

        $scope.cancelNewUser = function () {
            $scope.newUser = null;
        };

        $scope.addUser = function () {
            if ($scope.newUser) {
                return;
            }

            $scope.newUser = {};
        };

        $scope.createUser = function () {
            var newUserData = angular.copy($scope.newUser);
            delete newUserData.$$hashKey;

            auth.createUser(newUserData).then(function () {
                newUserData.userName = newUserData.username;
                $scope.users.splice(0, 0, newUserData);
                $scope.newUser = null;
                $scope.newUserValidated = false;
            }, function (error) {
                $scope.error = error;
            });
        };

        $scope.changePassword = function () {
            var validated = validateEmailAndPasswords();
            if (!validated) {
                return;
            }

            validated = validatePasswordsMatch();
            if (!validated) {
                return;
            }

            auth.changePassword($scope.email, $scope.currentPassword, $scope.password).then(function () {
                window.location.href = window.location.href.replace(/change_password\.html.*/,
                    "index.html#/d/overview");
            }, function (error) {
                $scope.error = error;
            });
        };

        $scope.renewPassword = function (user) {
            $scope.renewPasswordModal.renewPasswordUser = user;
            $scope.showRenewPasswordModal = true;
        };
    }]);
}());
