angular.module("Fortscale").factory("DAL", ["$http", "$q", "splunk", "server", function($http, $q, splunk, server){
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
            getEntity: function(entityType){
                var deferred = $q.defer();

                $http.get("data/get_entity_" + entityType + ".json")
                    .success(function(response){
                        deferred.resolve(response);
                    }, deferred.reject);

                return deferred.promise;
            }
        },
        reports: {
            getAllReports: function(){
                return $http.get("data/get_all_reports.json");
            },
            runSearch: function(searchId, dataSource, params, options){
                var deferred = $q.defer();

                if (dataSource === "splunk")
                    splunk.runSearch(searchId, params, options).then(deferred.resolve, deferred.reject);
                else if (dataSource === "api")
                    server.queryServer(searchId, params, options).success(deferred.resolve).error(deferred.reject);
                else
                    server.query(searchId, params, options).success(deferred.resolve).error(deferred.reject);

                return deferred.promise;
            }
        },
        widgets: {
            getDashboardWidgets: function(dashboardId){
                return $http.get("data/get_widgets.json");
            }
        }
    };

    return methods;
}]);