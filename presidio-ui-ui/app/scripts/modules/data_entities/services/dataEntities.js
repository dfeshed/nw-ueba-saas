(function () {
    'use strict';

    angular.module("DataEntities").factory("dataEntities",
        ["DataEntity", "$q", "utils", "configFlags", function (DataEntity, $q, utils, configFlags) {

            function checkInit () {
                if (!entities) {
                    throw new Error("Entities are not initialized yet.");
                }
            }

            function setEntities () {
                entities = new Map();
                window.__entitiesConfig__.forEach(function (entityConfig) {
                    var entity = new DataEntity(entityConfig);
                    entities.set(entity.id, entity);
                });

                getAllEntities();

                // Set the base entities:
                entities.forEach(function (entity) {
                    if (entity.baseEntityId) {
                        var baseEntity = entities.get(entity.baseEntityId);
                        if (!baseEntity) {
                            throw new Error("Unknown base entity, '" + entity.baseEntityId + "'.");
                        }

                        entity.baseEntity = baseEntity;
                        delete entity.baseEntityId;
                        entity.linkedEntities = getLinkedEntities(entity);
                    }
                });

                // Clean-up, remove the temporary entities:
                delete window.__entitiesConfig__;
            }


            function getEntityById (entityId) {
                checkInit();
                return entities.get(entityId);
            }

            /**
             * Given a DataEntity, returns all entities with which JOIN data queries can be done.
             * @param dataEntity
             */
            function getLinkedEntities (dataEntity) {
                if (!(dataEntity instanceof DataEntity)) {
                    throw new TypeError("Expected an instance of DataEntity, got: " + dataEntity + ".");
                }

                if (!dataEntity._joinFromFields) {
                    dataEntity._joinFromFields = dataEntity.fieldsArray.filter(function (field) {
                        return field.joinFrom;
                    });

                    dataEntity._joinFromFields = utils.objects.arrayToObject(dataEntity._joinFromFields, "joinFrom");
                }

                if (!Object.keys(dataEntity._joinFromFields).length) {
                    return [];
                }

                var linkedEntities = [];

                entitiesArray.forEach(function (entity) {
                    if (entity === dataEntity) {
                        return true;
                    }

                    if (!entity._joinToFields) {
                        entity._joinToFields = entity.fieldsArray.filter(function (entityField) {
                            return entityField.joinTo;
                        });

                        entity._joinToFields = utils.objects.arrayToObject(entity._joinToFields, "joinTo");
                    }

                    for (var joinTo in entity._joinToFields) {
                        if (entity._joinToFields.hasOwnProperty(joinTo)) {
                            if (dataEntity._joinFromFields[joinTo]) {
                                linkedEntities.push({
                                    entity: entity.id,
                                    joinFields: {
                                        left: dataEntity.id + "." + dataEntity._joinFromFields[joinTo].id,
                                        right: entity.id + "." + entity._joinToFields[joinTo].id
                                    }
                                });
                            }
                        }
                    }
                });

                return linkedEntities;
            }

            /**
             * Returns all the non-abstract entities
             * @returns {*}
             */
            function getAllEntities () {
                if (entitiesArray && entitiesArray.length) {
                    return entitiesArray;
                }

                entitiesArray = [];
                for (var entity of Array.from(entities)) {
                    if (!entity[1].isAbstract) {
                        entitiesArray.push(entity[1]);
                    }
                }
                return entitiesArray;
            }

            var entities,
                entitiesArray;

            // Getting pre-loaded entities:
            if (window.__entitiesConfig__) {
                setEntities();
            }



            return {
                entityExists: function (entityId) {
                    return entities.has(entityId);
                },
                getAllEntities: getAllEntities,
                getEntityById: getEntityById,
                /**
                 * Gets all non-abstract entities that extend the specified base entity
                 * @param baseEntityId
                 * @returns {*}
                 */
                getExtendingEntities: function (baseEntityId) {
                    var baseEntity = entities.get(baseEntityId);
                    if (!baseEntity) {
                        throw new Error("Unknown base entity, '" + baseEntityId + "'.");
                    }

                    if (baseEntity._childEntities !== undefined) {
                        return baseEntity._childEntities;
                    }

                    var childEntities = [];

                    entities.forEach(function (entity) {
                        if (!entity.isAbstract && entity.extendsEntity(baseEntity)) {
                            childEntities.push(entity);
                        }
                    });

                    baseEntity._childEntities = childEntities;
                    return childEntities;
                },
                getLinkedEntities: getLinkedEntities,
                getField: function (entityId, fieldId) {
                    var field;
                    var entity = getEntityById(entityId);
                    if (entity) {
                        field = entity.fields.get(fieldId);
                    }

                    return field;
                }.bind(this),
                /**
                 * Inits the entities. This should be done before any usage of other methods in this service.
                 * Runs in the Loader app, NOT in Fortscale app!
                 * @returns {*}
                 */
                initEntities: function () {
                    if (entities) {
                        return $q.when(entities);
                    }

                    return utils.http.wrappedHttpGet(configFlags.mockData ? "data/mock_data/getentities.json" :
                        "api/getEntities").then(function (results) {
                        // Put the entities data temporarily in the global scope, since after this the Fortscale
                        // angular app will start
                        window.__entitiesConfig__ = results.data;
                    }, function (error) {
                        console.error("Error getting entities: ", error);
                    });
                }
            };

        }]);
}());
