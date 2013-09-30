 angular.module("Fortscale").factory("DAL", ["$http", "$q", "server", "version", "database", function($http, $q, server, version, database){
    function setVariables(str, data){
        var parsedValue = str.replace(/\{\{([^\}]+)\}\}/g, function(match, variable){
            var dataValue = data[variable];
            if (dataValue !== undefined && dataValue !== null){
                delete data[variable];
                return dataValue;
            }

            return "";
        });
    }

    var methods = {
        dashboards: {
            getDashboardsList: function(){
                return $http.get("data/get_dashboards_list.json");
            },
            getDashboardById: function(dashboardId){
                return server.getDashboard(dashboardId);
            }
        },
        entities: {
            getEntities: function(){
                var deferred = $q.defer();

                $http.get("data/entities.json?v=" + version)
                    .success(deferred.resolve)
                    .error(deferred.reject);

                return deferred.promise;
            }
        },
        reports: {
            getAllReports: function(){
                return $http.get("data/get_all_reports.json");
            },
            getReport: function(reportId){
                var deferred = $q.defer();

                $http.get("data/reports/" +reportId + ".json?v=" + version)
                    .success(deferred.resolve)
                    .error(deferred.reject);

                return deferred.promise;
            },
            runSearch: function(report, params){
                if (report.dataSource === "api"){
                    var deferred = $q.defer();

                    server.queryServer(report, params, report.options).then(deferred.resolve, deferred.reject);

                    return deferred.promise;
                }
                else if (report.dataSource === "database")
                    return database.query(report.query, params, report.params);
                else
                    return server.query(report.searchId, params, report.options);
            }
        },
        widgets: {
            getDashboardWidgets: function(dashboardId){
                return $http.get("data/get_widgets.json");
            },
            getWidget: function(widgetId){
                var deferred = $q.defer();

                $http.get("data/widgets/" + widgetId + ".json?v=" + version)
                    .success(deferred.resolve)
                    .error(deferred.reject);

                return deferred.promise;
            }
        }
    };

    return methods;
}]);