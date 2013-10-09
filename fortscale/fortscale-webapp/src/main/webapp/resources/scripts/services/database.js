angular.module("Fortscale").factory("database", ["$q", "$http", "version", "conditions", function ($q, $http, version, conditions) {
    var cachedData = {};

    function getDatabaseData(databaseId){
        var deferred = $q.defer();

        var data = cachedData[databaseId];
        if (data)
            deferred.resolve(data);
        else{
            if (data === null)
                deferred.reject();
            else {
                $http.get("data/database/" + databaseId + ".json?v=" + version)
                    .success(function (result) {
                        data = cachedData[databaseId] = result;
                        deferred.resolve(data);
                    }, function (error) {
                        cachedData[databaseId] = null;
                        deferred.reject(error);
                    });
            }
        }
        return deferred.promise;
    }

    var methods = {
        query: function (query, params) {
            var deferred = $q.defer();

            if (!query || (!query.entity && !query.entities)) {
                deferred.reject();
            }
            else {
                if (query.entities)
                    query.entity = query.entities[0].id;

                getDatabaseData(query.entity).then(withData, deferred.reject);

                function withData(data) {
                    var queryResults = [],
                        groupByIndex = {};

                    if (query.fields)
                        query.fieldsMap = query.fieldsMap || {};
                    var getRow = query.fields
                        ? function(data){
                            var row = {};
                            angular.forEach(query.fields, function(fieldName){
                                row[fieldName] = data[fieldName];
                            });

                            return row;
                        }
                        : function(data){ return data; };

                    try{
                        if (query.conditions && query.conditions.length) {
                            angular.forEach(data, function (row, rowIndex) {
                                if (conditions.validateConditions(query.conditions, row, params)){
                                    if (query.groupBy){
                                        if (!groupByIndex[row[query.groupBy]]){
                                            groupByIndex[row[query.groupBy]] = true;
                                            queryResults.push(getRow(row));
                                        }
                                    }
                                    else
                                        queryResults.push(getRow(row));
                                }
                            });
                        }
                        else
                            queryResults = data;

                        var total = queryResults.length;

                        if (query.sort){
                            var sortFunction = function(a,b){
                                var aValue = a[query.sort.field],
                                    bValue = b[query.sort.field];

                                if (aValue === bValue)
                                    return 0;

                                var result = aValue > bValue ? 1 : -1;
                                result *= query.sort.direction;
                                return result;
                            }

                            queryResults.sort(sortFunction);
                        }

                        if (query.paging && query.paging.pageSize){
                            if (!query.paging.pageSize)
                                throw new Error("Paging requires page size.");

                            var offset = ((query.paging.page || 1) - 1) * query.paging.pageSize;
                            queryResults = queryResults.slice(offset, offset + query.paging.pageSize)
                        }

                        deferred.resolve({ data: queryResults, total: total });
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