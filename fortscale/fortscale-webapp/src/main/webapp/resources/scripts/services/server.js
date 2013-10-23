angular.module("Fortscale").factory("server", ["$q", "$http", "$resource", "version", "utils", "conditions", "Cache", function ($q, $http, $resource, version, utils, conditions, Cache) {
    var runTimeCache = new Cache({ id: "runtime", hold: true, itemsExpireIn: 30 * 60 });

    var apiResource = $resource("/fortscale-webapp/api/:entity/:id/:method", {
        id: "@id"
    });

    var apiWithSubEntityResource = $resource("/fortscale-webapp/api/:entity/:id/:subEntityName/:subEntityId/:method", {
        id: "@id",
        subEntityName: "@subEntityName",
        subEntityId: "@subEntityId"
    });

    function queryToSql(query, isCount){
        var sql = ["SELECT"],
            sqlStr;

        var tables = [];
        angular.forEach(query.entities, function(entity){
            tables.push(entity.id);
        });

        if (isCount)
            sql.push("COUNT(*)");
        else
            sql.push(query.fields && query.fields.length ? query.fields.join(", ") : "*");

        sql.push("FROM");
        sql.push(tables.join(", "));
        if (query.conditions && query.conditions.length)
            sql.push("WHERE", conditions.conditionsToSql(query.conditions));

        if (!isCount){
            if (query.sort)
                sql.push("ORDER BY " + query.sort.field + (query.sort.direction === 1 ? " ASC" : " DESC"));

            if (query.paging)
                sql.push("LIMIT", query.paging.page ? query.paging.page * query.paging.pageSize : query.paging.pageSize);

            sqlStr = sql.join(" ");

            if (query.paging && query.paging.page && query.paging.page > 1){
                sqlStr = "SELECT * FROM (" + sqlStr + ") as tmp" + (query.sort ? " ORDER BY tmp." + query.sort.field + " " + (query.sort.direction === 1 ? "DESC" : "ASC") : "") + " LIMIT " + query.paging.pageSize;
                if (query.sort)
                    sqlStr = "SELECT * FROM (" + sqlStr + ") as tmpOrder ORDER BY tmpOrder." + query.sort.field + " " + (query.sort.direction === 1 ? "ASC" : "DESC") + " LIMIT " + query.paging.pageSize;
            }
        }
        else
            sqlStr = sql.join(" ");

        return sqlStr;
    }

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
            if (query.sql)
                return methods.sqlQuery(query.endpoint, params, options);

            var deferred = $q.defer();

            if (query.endpoint && query.endpoint.sql){
                query.endpoint.entity = "investigate";
                query.endpoint.query = utils.strings.parseValue(query.endpoint.sql, {}, params);
                query.endpoint.countQuery = query.endpoint.query.replace(/SELECT (.*) FROM/i, "SELECT COUNT(*) FROM").replace(/\slimit\s\d+/i, "");
            }

            var resource = query.endpoint.subEntityName ? apiWithSubEntityResource : apiResource,
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
        },
        sqlQuery: function(sqlQuery, params, options){
            var tableName = sqlQuery.entities[0].id,
                runtime = runTimeCache.getItem(tableName),
                deferred = $q.defer(),
                useEBS = sqlQuery.entities[0].useEBS;

            function withRuntime(){
                if (runtime){
                    sqlQuery.conditions = sqlQuery.conditions || [];
                    sqlQuery.conditions.push({
                        field: "runtime",
                        operator: "equals",
                        value: runtime
                    });
                }

                if (useEBS){
                    if (sqlQuery.paging){
                        var paging = {
                            limit: sqlQuery.paging.pageSize,
                            offset: ((sqlQuery.paging.page || 1) - 1) * sqlQuery.paging.pageSize
                        };
                        delete sqlQuery.paging;
                    }

                    if (sqlQuery.sort){
                        var sort = {
                            orderBy: sqlQuery.sort.field,
                            orderByDirection: sqlQuery.sort.direction === 1 ? "ASC" : "DESC"
                        };
                        delete sqlQuery.sort;
                    }
                }

                var query = {
                    endpoint: {
                        entity: "investigate" + (useEBS ? "WithEBS" : ""),
                        query: typeof(sqlQuery) === "string" ? sqlQuery : queryToSql(sqlQuery)
                    }
                };

                if (paging)
                    angular.extend(query.endpoint, paging);

                if (sort)
                    angular.extend(query.endpoint, sort);

                if (!useEBS)
                    query.endpoint.countQuery = typeof(sqlQuery) === "string" ? sqlQuery.replace(/SELECT (.*) FROM/i, "SELECT COUNT(*) FROM") : queryToSql(sqlQuery, true);

                methods.queryServer(query).then(deferred.resolve, deferred.reject);
            }

            if (runtime || runtime === false)
                withRuntime();
            else{
                $http.get("/fortscale-webapp/api/getLatestRuntime?tableName=" + encodeURIComponent(tableName))
                    .success(function(lastRuntimeResult){
                        runtime = parseInt(lastRuntimeResult);
                        withRuntime();
                        runTimeCache.setItem(tableName, runtime);
                    })
                    .error(function(){
                        withRuntime();
                        runTimeCache.setItem(tableName, false);
                    });
            }

            return deferred.promise;

        }
    };

    return methods;
}]);