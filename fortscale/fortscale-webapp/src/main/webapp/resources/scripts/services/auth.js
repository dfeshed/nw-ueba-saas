angular.module("FortscaleAuth", []).factory("auth", ["$q", "$http", function($q, $http){
    var apiUrl = "/fortscale-webapp/api/analyst/",
        adminApiUrl = "/fortscale-webapp/api/admin/",
        authToken,
        authTokenExpires;

    var emailRegExp = /^(([a-zA-Z0-9_.-])+@([a-zA-Z0-9_.-])+\.([a-zA-Z])+([a-zA-Z])+)?$/;

    var methods = {
        changePassword: function(username, currentPassword, newPassword){
            var deferred = $q.defer();

            deferred.resolve();

            return deferred.promise;
        },
        createUser: function(accountData){
            var deferred = $q.defer();

            $http({
                method: "POST",
                url: adminApiUrl + "analyst/addAnalyst",
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                data: $.param(accountData)
            })
                .success(deferred.resolve)
                .error(deferred.reject);

            return deferred.promise;
        },
        deleteUser: function(username){
            var deferred = $q.defer();

            $http({
                method: "POST",
                url: adminApiUrl + "analyst/disableAnalyst",
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                data: $.param({ username: username })
            })
                .success(deferred.resolve)
                .error(deferred.reject);

            return deferred.promise;
        },
        getAllUsers: function(){
            var deferred = $q.defer();

            $http.get(adminApiUrl + "analyst/details")
                .success(function(result){
                    deferred.resolve(result.data);
                })
                .error(deferred.reject);

            return deferred.promise;
        },
        getAuthToken: function(){
            if (authToken)
                return authToken;

            authToken = localStorage.authToken;
            if (authToken){
                authTokenExpires = new Date(parseInt(localStorage.authTokenExpires, 10));
                if (new Date() < authTokenExpires)
                    return authToken;
                else
                    authToken = null;
            }

            return null;
        },
        getCurrentUser: function(){
            var deferred = $q.defer();

            $http.get(apiUrl + "me/details")
                .success(function(result){
                    if (result && result.data){
                        var userData = result.data[0];
                        userData.fullName = [userData.firstName, userData.lastName].join(" ");
                        deferred.resolve(userData);
                    }
                    else
                        deferred.reject();
                })
                .error(deferred.reject);

            return deferred.promise;
        },
        login: function(username, password, remember){
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
                .success(function(response){
                    deferred.resolve(response);
                })
                .error(function(error, httpCode){
                    var errorMessage = httpCode === 200 ? error : "Can't access server.";
                    deferred.reject(errorMessage);
                });

            return deferred.promise;
        },
        logout: function(){
            $http.post(apiUrl + "logout").then(function(){
                window.location.href = window.location.href.replace(/fortscale-webapp\/.*/, "fortscale-webapp/signin.html");
            }, function(error){
                alert(error);
            });
        },
        validateUsername: function(username){
            return emailRegExp.test(username);
        }
    };

    return methods;
}]);