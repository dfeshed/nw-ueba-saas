angular.module("Fortscale").factory("layout", ["$q", "auth", "Cache", function($q, auth, Cache){
    var cache = new Cache({ id: "layout" });

    var methods = {
        createDashboard: function(dashboardName){

        },
        getDashboardsList: function(){

        },
        getDashboard: function(dashboardId){

        },
        saveDashboard: function(dashboard){

        }
    };

    return methods;
}]);