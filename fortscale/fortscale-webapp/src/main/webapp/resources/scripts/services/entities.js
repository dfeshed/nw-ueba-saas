angular.module("Fortscale").factory("entities", ["$q", "DAL", function($q, DAL){
    var cachedEntities = {};

    var methods = {
        getEntity: function(entityType){
            var deferred = $q.defer();

            if (cachedEntities[entityType])
                deferred.resolve(cachedEntities[entityType]);
            else
                DAL.entities.getEntity(entityType).then(function(entity){
                    cachedEntities[entityType] = entity;
                    deferred.resolve(entity);
                }, deferred.reject);

            return deferred.promise;
        },
        getEntityFeatureById: function(entity, featureId){
            for(var i= 0, feature; feature = entity.features[i]; i++){
                if (feature.featureId === featureId)
                    return feature;
            }

            return null;
        }
    };

    return methods;
}]);