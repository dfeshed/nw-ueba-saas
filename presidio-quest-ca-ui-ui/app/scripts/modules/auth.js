(function () {
    'use strict';

    angular.module("FortscaleAuth", ["Utils"]).factory("auth", ["$q", "$http", "utils", "configFlags",
        function ($q, $http, utils, configFlags) {
            var apiUrl = "api/analyst/",
                adminApiUrl = "api/admin/";

            var emailRegExp = /^(([a-zA-Z0-9_.-])+@([a-zA-Z0-9_.-])+\.([a-zA-Z])+([a-zA-Z])+)?$/;

            var methods = {
                changePassword: function (username, currentPassword, newPassword) {
                    var deferred = $q.defer();

                    $http({
                        method: "POST",
                        url: apiUrl + "changePassword",
                        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                        data: $.param({
                            password: currentPassword,
                            newPassword: newPassword,
                            username: username
                        })
                    })
                        .success(function (response) {
                            deferred.resolve(response);
                        })
                        .error(function (error, httpCode) {
                            deferred.reject(getErrorMessage(error, httpCode));
                        });

                    return deferred.promise;
                },
                createUser: function (accountData) {
                    var deferred = $q.defer();

                    $http({
                        method: "POST",
                        url: adminApiUrl + "analyst/addAnalyst",
                        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                        data: $.param(accountData)
                    })
                        .success(deferred.resolve)
                        .error(function (error, httpCode) {
                            deferred.reject(getErrorMessage(error, httpCode));
                        });

                    return deferred.promise;
                },
                deleteUser: function (username) {
                    var deferred = $q.defer();

                    $http({
                        method: "POST",
                        url: adminApiUrl + "analyst/disableAnalyst",
                        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                        data: $.param({username: username})
                    })
                        .success(deferred.resolve)
                        .error(function (error, httpCode) {
                            deferred.reject(getErrorMessage(error, httpCode));
                        });

                    return deferred.promise;
                },
                editUser: function (accountData) {
                    var deferred = $q.defer();

                    $http({
                        method: "POST",
                        url: apiUrl + "analyst/update",
                        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                        data: $.param(accountData)
                    })
                        .success(deferred.resolve)
                        .error(function (error, httpCode) {
                            deferred.reject(getErrorMessage(error, httpCode));
                        });

                    return deferred.promise;
                },
                getAllUsers: function () {
                    if (configFlags.mockData) {
                        return utils.http.wrappedHttpGet("data/mock_data/admin_users.json").then(function (results) {
                            return results.data;
                        });
                    }
                    else {
                        return utils.http.wrappedHttpGet(adminApiUrl + "analyst/details").then(function (results) {
                            var users = [];
                            results.data.forEach(function (user) {
                                user.fullName = user.firstName + " " + user.lastName;
                                users.push(user);
                            });
                            return users;
                        }, function (error, httpCode) {
                            return $q.reject(getErrorMessage(error, httpCode));
                        });
                    }
                },
                getCurrentUser: function () {
                    if (configFlags.mockData) {
                        return utils.http.wrappedHttpGet("data/mock_data/currentUser.json").then(function (result) {
                            if (result && result.data) {
                                var userData = result.data[0];
                                userData.fullName = [userData.firstName, userData.lastName].join(" ");
                                return userData;
                            }
                            else {
                                return $q.reject("Current user unavailable.");
                            }
                        });
                    }


                    return utils.http.wrappedHttpGet(apiUrl + "me/details", {cache: true}).then(function (result) {
                        if (result && result.data) {
                            var userData = result.data[0];
                            userData.fullName = [userData.firstName, userData.lastName].join(" ");
                            return userData;
                        }
                        else {
                            return $q.reject("Can't get current user");
                        }
                    }, function (error, httpCode) {
                        return $q.reject(getErrorMessage(error, httpCode));
                    });
                },
                getLastLoggedInUser: function () {
                    return localStorage.getItem("lastUser");
                },
                login: function (username, password, remember) {
                    var deferred = $q.defer();

                    $http({
                        method: "POST",
                        url: apiUrl + "login",
                        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                        data: $.param({
                            j_username: username,
                            j_password: password,
                            _spring_security_remember_me: remember ? "yes" : "no"
                        })
                    })
                        .success(function (response) {
                            localStorage.setItem("lastUser", username);
                            deferred.resolve(response);
                        })
                        .error(function (error, httpCode) {
                            deferred.reject(getErrorMessage(error, httpCode));
                        });

                    return deferred.promise;
                },
                logout: function () {
                    $http.post(apiUrl + "logout").then(function () {
                        sessionStorage.removeItem('session-login');
                        localStorage.removeItem('last-login-time');
                        //Redirect to login
                    }, function (error) {
                        alert("Logout error: " + error.data.message);
                    });
                },
                renewPassword: function (user, adminPassword, newPassword) {
                    return utils.http.wrappedHttpPost(adminApiUrl + "analyst/renewPassword", {
                        password: adminPassword,
                        username: user,
                        newPassword: newPassword
                    });
                },
                validateUsername: function (username) {
                    return emailRegExp.test(username);
                }
            };

            function getErrorMessage(error, httpCode) {
                var errorMessage = {error: true};

                switch (httpCode) {
                    case 401:
                        errorMessage.message = "Wrong username or password.";
                        break;
                    case 403:
                        errorMessage.message = "Password has expired.";
                        errorMessage.expired = true;
                        break;
                    case 400:
                        errorMessage.message = error.message;
                        break;
                    default:
                        errorMessage.message = "Can't access server (error code " + httpCode + ").";
                }

                return errorMessage;
            }

            return methods;
        }]);
}());
