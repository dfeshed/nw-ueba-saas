angular.module("Fortscale").factory("dashboards", ["$q", "DAL", function($q, DAL){
    var cachedDashboards = {};

    var methods = {
        getDashboardsList: function(){
            var deferred = $q.defer();

            DAL.dashboards.getDashboardsList().then(function(results){
                deferred.resolve(results.data);
            }, deferred.reject);

            return deferred.promise;
        },
        getDashboardById: function(dashboardId){
            var deferred = $q.defer();

            if (cachedDashboards[dashboardId])
                deferred.resolve(angular.copy(cachedDashboards[dashboardId]));
            else
                DAL.dashboards.getDashboardById(dashboardId).then(function(dashboard){
                    cachedDashboards[dashboardId] = dashboard;
                    deferred.resolve(angular.copy(dashboard));
                }, deferred.reject);

            return deferred.promise;
        }
    };

    return methods;
}]);