angular.module("Fortscale").factory("server", ["$q", "$http", "$resource", function ($q, $http, $resource) {
    var apiResource = $resource("/fortscale-webapp/api/:queryName/:id", {
        queryName: "@queryName",
        id: "@id"
    });

    var methods = {
        getDashboard: function (dashboardName) {
            var deferred = $q.defer();

            if (!dashboardName){
                deferred.reject("No dashboard specified.");
            }
            else{
                $http.get("data/dashboards/" + dashboardName + ".json")
                    .success(function (response) {
                        deferred.resolve(response);
                    }, deferred.reject);
            }
            return deferred.promise;
        },
        query: function (queryName, params, options) {
            var deferred = $q.defer();

            var paramsQuery = "";
            for (var paramName in params) {
                paramsQuery += paramName + "-" + params[paramName];
            }

            $http.get(("data/search/" + queryName + (paramsQuery ? "." + paramsQuery : "") + ".json?t=" + new Date().valueOf()).toLowerCase())
                .success(function(data){
                    console.log("DATA: ", data);
                    deferred.resolve(data);
                })
                .error(deferred.reject);

            return deferred.promise;
        },
        queryServer: function (queryName, params, options) {
            var deferred = $q.defer();

            var queryResult = apiResource.get(angular.extend({}, options, params, { queryName: queryName }), function(){
                if (queryResult)
                    deferred.resolve(queryResult);
                else
                    methods.query(queryName, params, options)
                        .success(deferred.resolve)
                        .error(deferred.reject);
            }, function(error){
                methods.query(queryName, params, options).then(deferred.resolve, deferred.reject);
            });

            return deferred.promise;
        }
    };

    return methods;
}]);