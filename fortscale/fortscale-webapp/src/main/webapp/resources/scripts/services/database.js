angular.module("Fortscale").factory("database", ["$q", "$http", "version", "conditions", function ($q, $http, version, conditions) {
    var cachedData = {};

    var methods = {
        query: function (query, params) {
            var deferred = $q.defer();

            if (!query.entity) {
                deferred.reject();
            }
            else {
                var data = cachedData[query.entity];
                if (data)
                    withData();
                else {
                    if (data === null)
                        deferred.reject();
                    else {
                        $http.get("data/database/" + query.entity + ".json?v=" + version)
                            .success(function (result) {
                                data = cachedData[query.entity] = result;
                                withData();
                            }, function (error) {
                                cachedData[query.entity] = null;
                                deferred.reject(error);
                            });
                    }
                }

                function withData() {
                    var queryResults = [],
                        groupByIndex = {};

                    try{
                        if (query.conditions) {
                            angular.forEach(data, function (row, rowIndex) {
                                if (conditions.validateConditions(query.conditions, row, params)){
                                    if (query.groupBy){
                                        if (!groupByIndex[row[query.groupBy]]){
                                            groupByIndex[row[query.groupBy]] = true;
                                            queryResults.push(row);
                                        }
                                    }
                                    else
                                        queryResults.push(row);
                                }
                            });
                        }

                        deferred.resolve({ data: queryResults });
                    } catch(error){
                        deferred.reject(error.message);
                    }
                }
            }
            return deferred.promise;
        }
    };

    return methods;
}]);