angular.module("Fortscale").factory("entities", ["$q", "DAL", function($q, DAL){
    var cachedEntities;

    var methods = {
        getEntities: function(){
            var deferred = $q.defer();

            if (cachedEntities)
                deferred.resolve(cachedEntities);
            else{
                DAL.entities.getEntities().then(function(entitiesData){
                    cachedEntities = entitiesData;
                    deferred.resolve(entitiesData);
                }, deferred.reject);
            }

            return deferred.promise;
        }
    };

    return methods;
}]);