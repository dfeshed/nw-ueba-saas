angular.module("Fortscale").factory("server", ["$q", "$http", function($q, $http){
   var methods = {
       getDashboard: function(dashboardName){
           var deferred = $q.defer();

           $http.get("data/dashboards/" + dashboardName + ".json")
               .success(function(response){
                   deferred.resolve(response);
               }, deferred.reject);

           return deferred.promise;
       },
       query: function(queryName, params, options){
           var paramsQuery = "";
           for(var paramName in params){
               paramsQuery += paramName + "-" + params[paramName];
           }

           return $http.get(("data/search/" + queryName + (paramsQuery ? "." + paramsQuery : "") + ".json?t=" + new Date().valueOf()).toLowerCase());
       },
       queryServer: function(queryName, params, options){
           var url = "/api/" + queryName + "/" + params[options.mainParam];

           return $http.get(url);
       }
   };

    return methods;
}]);