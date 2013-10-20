angular.module("Fortscale").factory("server", ["$q", "$http", "$resource", "version", "utils", "conditions", function ($q, $http, $resource, version, utils, conditions) {
    var apiResource = $resource("/fortscale-webapp/api/:entity/:id/:method", {
        id: "@id"
    });

    var apiWithSubEntityResource = $resource("/fortscale-webapp/api/:entity/:id/:subEntityName/:subEntityId/:method", {
        id: "@id",
        subEntityName: "@subEntityName",
        subEntityId: "@subEntityId"
    });

    function queryToSql(query, isCount){
        var sql = ["SELECT"];

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

        if (query.sort)
            sql.push("ORDER BY " + query.sort.field + (query.sort.direction === 1 ? " ASC" : " DESC"));

        if (query.paging)
            sql.push("LIMIT", query.paging.pageSize);

        return sql.join(" ");
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
            var deferred = $q.defer();

            if (query.endpoint && query.endpoint.sql){
                query.endpoint.entity = "investigate";
                query.endpoint.query = utils.strings.parseValue(query.endpoint.sql, {}, params);
                query.endpoint.countQuery = query.endpoint.query.replace(/SELECT (.*) FROM/i, "SELECT COUNT(*) FROM");
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
            return this.queryServer({
                endpoint: {
                    entity: "investigate",
                    query: typeof(sqlQuery) === "string" ? sqlQuery : queryToSql(sqlQuery),
                    countQuery: typeof(sqlQuery) === "string" ? sqlQuery.replace(/SELECT (.*) FROM/i, "SELECT COUNT(*) FROM") : queryToSql(sqlQuery, true)
                }
            });
        }
    };

    return methods;
}]);