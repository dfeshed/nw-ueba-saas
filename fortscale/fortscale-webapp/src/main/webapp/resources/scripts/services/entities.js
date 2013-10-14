angular.module("Fortscale").factory("entities", ["$q", "DAL", function($q, DAL){
    var cachedEntities;
    var useServerEntities = true;

    var methods = {
        getEntitiesConnections: function(entities){
            var deferred = $q.defer();

            var foundConnections = {},
                promises = [];

            var entityIds = {};

            entities.forEach(function(entity){
                entityIds[entity.id] = true;
                promises.push(methods.getEntityConnections(entity));
            });

            $q.all(promises).then(function(connections){
                connections.forEach(function(entityConnections, entityIndex){
                    angular.forEach(entityConnections, function(entityConnection){
                        if (!entityIds[entityConnection.entity.id]){
                            var foundConnection = foundConnections[entityConnection.entity.id];
                            if (!foundConnection)
                                foundConnection = foundConnections[entityConnection.entity.id] = { entity: entityConnection.entity, fields: []};

                            foundConnection.fields = foundConnection.fields.concat(entityConnection.fields);
                        }
                    });
                });

                var foundConnectionsArray = [];
                for(var entityId in foundConnections){
                    foundConnectionsArray.push(foundConnections[entityId]);
                }

                deferred.resolve(foundConnectionsArray);
            }, deferred.reject);

            return deferred.promise;
        },
        getEntityConnections: function(entity){
            var deferred = $q.defer();

            methods.getEntities().then(function(entities){
                var connectedEntities = [],
                    entityKeys = {};

                angular.forEach(entity.fields, function(field, fieldIndex){
                    if (field.key){
                        entityKeys[field.key] = fieldIndex;
                    }
                });

                angular.forEach(entities, function(anEntity){
                    if (anEntity.id !== entity.id){
                        var connection;

                        for(var i= 0, field; field = anEntity.fields[i]; i++){
                            if (field.key && entityKeys[field.key] !== undefined){
                                if (!connection)
                                    connection = { entity: anEntity, fields: [] };

                                connection.fields.push({ field: field.id, connectedField: entity.fields[entityKeys[field.key]].id });
                            }
                        }

                        if (connection)
                            connectedEntities.push(connection);
                    }
                });

                deferred.resolve(connectedEntities);
            }, deferred.reject);

            return deferred.promise;
        },
        getEntities: function(){
            var deferred = $q.defer();

            if (cachedEntities)
                deferred.resolve(cachedEntities);
            else{
                DAL.entities.getEntities().then(function(entitiesData){
                    angular.forEach(entitiesData, function(entity){
                        angular.forEach(entity.fields, function(field){
                            field.entity = entity;
                        });
                        angular.forEach(entity.computedFields, function(field){
                            field.entity = entity;
                        });
                    });

                    cachedEntities = entitiesData;
                    deferred.resolve(entitiesData);
                }, deferred.reject);
            }

            return deferred.promise;
        }
    };

    return methods;
}]);