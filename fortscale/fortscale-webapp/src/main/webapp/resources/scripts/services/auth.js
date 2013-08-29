angular.module("FortscaleAuth", []).factory("auth", ["$q", "$http", function($q, $http){
    var apiUrl = "/api/",
        authToken,
        authTokenExpires;

    var emailRegExp = /^(([a-zA-Z0-9_.-])+@([a-zA-Z0-9_.-])+\.([a-zA-Z])+([a-zA-Z])+)?$/;

    var methods = {
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
        login: function(username, password, remember){
            var deferred = $q.defer();

            deferred.resolve();
            return deferred.promise;

            $http.post(apiUrl + "login", { username: username, password: password, remember: remember })
                .success(function(response){
                    authToken = response.authToken;
                    authTokenExpires = response.expires;

                    localStorage.authToken = authToken;
                    localStorage.authTokenExpires = response.expires;

                    deferred.resolve(authToken);
                })
                .error(function(error){
                    deferred.reject(error);
                });

            return deferred.promise;
        },
        logout: function(){
            $http.post(apiUrl + "login", { authToken: authToken });
            authToken = null;
        },
        validateUsername: function(username){
            return emailRegExp.test(username);
        }
    };

    return methods;
}]);