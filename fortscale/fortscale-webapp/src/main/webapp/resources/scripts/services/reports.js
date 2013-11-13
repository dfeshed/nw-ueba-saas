angular.module("Fortscale").factory("reports", ["$q", "DAL", "Cache", "reportsProcess", function($q, DAL, Cache, reportsProcess){
    var cache = new Cache({ id: "reports" }), // this is for data
        cachedReports = {}; // This one is just for the report definitions

    function getSearchParams(report, params){
        var searchParams = {};
        angular.forEach(report.query.params, function(param){
            var paramValue = params[param.dashboardParam];
            if (paramValue !== undefined && paramValue !== null && paramValue !== "")
                searchParams[param.field] = paramValue;
            else if ((searchParams[param.field] === undefined || searchParams[param.field] === null || searchParams[param.field] === "") && param.default)
                searchParams[param.field] = param.default;
        });

        return searchParams;
    }

    function getInSeconds(value){
        var valueMatch = value.match(/^(\d+)(\w)$/);
        if (!valueMatch)
            throw new Error("Invalid time period value: " + value);

        var int = parseInt(valueMatch[1], 10),
            unit = valueMatch[2];

        if (unit === "s")
            return int;

        if (unit === "m")
            return int * 60;

        if (unit === "h")
            return int * 3600;

        if (unit === "d")
            return int * 3600 * 24;

        throw new Error("Invalid time period value: " + value);
    }

    var methods = {
        getAllReports: function(){
            var deferred = $q.deferred();

            DAL.reports.getAllReports()
                .success(deferred.resolve)
                .error(deferred.reject);

            return deferred.promise;
        },
        getReport: function(reportId){
            var deferred = $q.defer();

            if (cachedReports[reportId])
                deferred.resolve(cachedReports[reportId]);
            else{
                DAL.reports.getReport(reportId).then(function(report){
                    cachedReports[reportId] = report;
                    deferred.resolve(report);
                }, deferred.reject);
            }

            return deferred.promise;
        },
        runReport: function(report, params, forceRefresh){
            var deferred = $q.defer();

            if (report.query.searchId){
                var cacheItemKey = "search_" + report.query.searchId;

                if (forceRefresh)
                    cache.removeItem(cacheItemKey);

                if (report.query.cache){
                    var cachedData = cache.getItem(cacheItemKey, { hold: true });
                    if (cachedData){
                        deferred.resolve(cachedData);
                        return deferred.promise;
                    }
                }
            }

            DAL.reports.runSearch(report.query, getSearchParams(report, params))
                .then(function(results){
                    if (report.transform){
                        var transformedResults = [];

                        try{
                            if (report.transform.groupBy){
                                var groupMap = {},
                                    groupByField,
                                    groupByFieldIndex,
                                    currentTransformedResult;

                                angular.forEach(results, function(result){
                                    var item;

                                    groupByField = result[report.transform.groupBy];
                                    if (groupByField !== undefined){
                                        groupByFieldIndex = groupMap[groupByField];

                                        if (groupByFieldIndex === undefined){
                                            groupByFieldIndex = groupMap[groupByField] = transformedResults.length;

                                            var newTransformedResult = { items: [] };
                                            newTransformedResult[report.transform.groupBy] = groupByField;
                                            transformedResults.push(newTransformedResult)
                                            currentTransformedResult = transformedResults[groupByFieldIndex];
                                        }
                                        else{
                                            currentTransformedResult = transformedResults[groupByFieldIndex];
                                        }

                                        item = {};
                                        for(var property in result){
                                            if (property !== report.transform.groupBy)
                                                item[property] = result[property];
                                        }
                                        currentTransformedResult.items.push(item);
                                    }
                                });
                            }

                            resolve(transformedResults);
                        }
                        catch(error){
                            deferred.reject({ error: "Can't transform data: " + error.message });
                        }
                    }
                    else{
                        resolve(results);
                    }

                    if (!results || !results.data || !results.data.length)
                        cache.removeItem(cacheItemKey);
                }, onError);

            function resolve(results){
                if (report.query.process){
                    reportsProcess.processData(report.query.process.processId, results, report.query.process.params).then(function(processedResults){
                        finishResolve(processedResults);
                    }, onError);
                }
                else
                    finishResolve(results);
            }

            function finishResolve(results){
                deferred.resolve(results);
                if (report.query.cache && results.data.length)
                    cache.setItem(cacheItemKey, results, { expiresIn: getInSeconds(report.query.cache), hold: true });
            }

            function onError(error){
                if (report.query.cache)
                    cache.removeItem(cacheItemKey);

                deferred.reject(error);
            }

            return deferred.promise;
        },
        runReports: function(reports, params, forceRefresh){
            var promises = [];

            angular.forEach(reports, function(report){
                promises.push(methods.runReport(report, params, forceRefresh));
            });

            return $q.all(promises);
        }
    };

    return methods;
}]);