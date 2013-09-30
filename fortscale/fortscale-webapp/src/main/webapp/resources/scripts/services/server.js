angular.module("Fortscale").factory("server", ["$q", "$http", "$resource", "version", "utils", function ($q, $http, $resource, version, utils) {
    var apiResource = $resource("/fortscale-webapp/api/:entity/:id/:method", {
        id: "@id"
    });

    var apiWithSubEntityResource = $resource("/fortscale-webapp/api/:entity/:id/:subEntityName/:subEntityId/:method", {
        id: "@id",
        subEntityName: "@subEntityName",
        subEntityId: "@subEntityId"
    });

    var methods = {
        getDashboard: function (dashboardName) {
            var deferred = $q.defer();

            if (!dashboardName){
                deferred.reject("No dashboard specified.");
            }
            else{
                $http.get("data/dashboards/" + dashboardName + ".json?v=" + version)
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

            $http.get(("data/search/" + queryName + (paramsQuery ? "." + paramsQuery : "") + ".json?v=" + version).toLowerCase())
                .success(function(data){
                    deferred.resolve(data);
                })
                .error(deferred.reject);

            return deferred.promise;
        },
        queryServer: function (query, params, options) {
            var deferred = $q.defer(),
                resource = query.endpoint.subEntityName ? apiWithSubEntityResource : apiResource,
                resourceData = angular.extend({}, options, params, query.endpoint);

            for(var property in resourceData){
                if (angular.isString(resourceData[property])){
                    resourceData[property] = utils.strings.parseValue(resourceData[property], {}, params);
                }
            }

            var queryResult = resource.get(resourceData, function(){
                if (queryResult)
                    deferred.resolve(queryResult);
                else
                    deferred.reject();
            }, function(error){
                deferred.reject();
            });

            return deferred.promise;
        }
    };

    return methods;
}]);