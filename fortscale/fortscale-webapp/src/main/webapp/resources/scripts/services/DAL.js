angular.module("Fortscale").factory("DAL", ["$http", "$q", "server", function($http, $q, server){
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
            runSearch: function(query, params){
                if (query.dataSource === "api"){
                    return server.queryServer(query, params, query.options);
                }
                else
                    return server.query(query.searchId, params, query.options);
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