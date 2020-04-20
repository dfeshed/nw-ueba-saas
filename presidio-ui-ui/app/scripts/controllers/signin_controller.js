(function () {
    'use strict';

    angular.module("FortscaleSignin")
        .controller("SigninController", ["$scope", "auth", "utils", function ($scope, auth, utils) {
            var queryParams = utils.url.getQueryParams();
            if (queryParams.username) {
                $scope.email = queryParams.username;
            } else if (/change_password/.test(window.location.href)) {
                auth.getCurrentUser().then(function (user) {
                    if (user && user.userName) {
                        $scope.email = user.userName;
                    } else {
                        $scope.error = {message: "No username specified."};
                    }
                }, function (error) {
                    $scope.error = {message: "No username specified."};
                });
            } else {
                $scope.email = auth.getLastLoggedInUser();
            }

            if ($scope.email) {
                $scope.focusOnPassword = true;
            }

            $scope.rememberMe = true;

            function validateEmailAndPasswords () {
                if (!$scope.email || !$scope.password) {
                    $scope.error = {message: "Please enter email and password"};
                    return;
                }

                if (!auth.validateUsername($scope.email)) {
                    $scope.error = {message: "Invalid email address"};
                    return;
                }

                if (!$scope.password) {
                    $scope.error = {message: "Please enter password"};
                    return;
                }

                return true;
            }

            function validatePasswordsMatch () {
                if (!$scope.passwordConfirm) {
                    $scope.error = {message: "Please enter password confirmation"};
                    return;
                }

                if ($scope.password !== $scope.passwordConfirm) {
                    $scope.error = {message: "Passwords don't match"};
                    return;
                }

                return true;
            }

            function registerLogin () {
                sessionStorage.setItem('session-login', 'loggedIn');
                localStorage.setItem('last-login-time', new Date().valueOf());
            }

            $scope.clearError = function () {
                $scope.error = null;
            };

            $scope.signIn = function () {
                var validated = validateEmailAndPasswords();
                if (!validated) {
                    return;
                }

                doLogin();
            };

            function doLogin () {
                $scope.focusOnPassword = false;

                auth.login($scope.email, $scope.password, $scope.rememberMe).then(function () {
                    registerLogin();
                    if (window.location.search.indexOf('?absRedirect=') !== -1) {
                        window.location.href =
                            decodeURIComponent(/absRedirect=([^&]*)/.exec(window.location.search)[1]);
                    } else {
                        window.location.href = window.location.href.replace(/\/[\w_\-]+\.html.*/, "/index.html" +
                            (queryParams.redirect || "#/overview"));
                    }
                }, function (error) {
                    if (error.expired) {
                        window.location.href = window.location.href.replace(/\/[\w_\-]+\.html.*/,
                            "/change_password.html?username=" + $scope.email);
                    }
                    else {
                        $scope.error = error;
                        $scope.password = null;
                        $scope.focusOnPassword = true;
                    }
                });
            }

            $scope.signUp = function () {
                if (!$scope.firstName || !$scope.lastName) {
                    $scope.error = {message: "Please enter first name and last name"};
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
                    registerLogin();
                    // window.location.href = window.location.href.replace(/signup\.html.*/, "signin.html");
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
                    doLogin();
                }, function (error) {
                    $scope.error = {message: "Failed to change password. (Server error)"};
                });
            };

            $scope.currentYear = new Date().getFullYear();
        }]);
}());
