(function () {
    'use strict';

    angular.module("DataEntities").factory("DataEntity",
        ["DataEntityField", "DataEntitySort", function (DataEntityField, DataEntitySort) {

            function DataEntity (config) {
                if (config) {
                    this.validate(config);
                    this.name = config.name;
                    this.id = config.id;
                    this.baseEntityId = config.extendsEntity;
                    this.isAbstract = config.isAbstract;
                    this.showInExplore = config.showInExplore;

                    this.fields = new Map();

                    for (var field of config.fields) {
                        this.fields.set(field.id, new DataEntityField(field, this));
                    }

                    // link scoredFields:
                    var scoreFieldId;
                    for (field of Array.from(this.fields.values())) {
                        if (!!(scoreFieldId = field.scoreField)) {
                            field.scoreField = this.fields.get(scoreFieldId);
                            if (!field.scoreField) {
                                throw new Error("Can't create DataEntity, score field '" + scoreFieldId +
                                    "' not found.");
                            }
                        }
                    }

                    this.eventsEntity = config.eventsEntity || null;
                    this.sessionEntity = config.sessionEntity || null;
                    this.requiredFields = config.requiredFields || [];
                    this.nameForMenu = config.nameForMenu || null; //nameForMenu is sometimes different than entity
                                                                   // name. for example: name: 'SSH'. nameForMenu: 'SSH
                                                                   // events'

                    if (config.defaultSort) {
                        var sort = config.defaultSort;
                        if (sort.constructor !== Array) {
                            sort = [sort];
                        }

                        this.defaultSort = sort.map(function (sortField) {
                            return new DataEntitySort(sortField);
                        });
                    }

                    // TODO: Get this from the server!!
                    if (this.fields.has("event_score") || this.fields.has("session_score")) {
                        this.performanceField = {
                            field: this.fields.get("event_score") || this.fields.get("session_score"),
                            value: 50
                        };
                    }
                }
            }

            DataEntity.prototype.validate = function (config) {
                if (Object(config) !== config) {
                    throw new TypeError("Invalid configuration for DataEntity, expected an object, got " +
                        typeof(config) + ".");
                }

                if (config.name) {
                    if (typeof(config.name) !== "string") {
                        throw new TypeError("Invalid name for DataEntity, expected a string but got " +
                            typeof(config.name));
                    }
                }
                else {
                    throw new Error("Can't instantiate DataEntity, missing the 'name' property.");
                }

                if (config.id) {
                    if (typeof(config.id) !== "string") {
                        throw new TypeError("Invalid id for DataEntity, expected a string but got " +
                            typeof(config.id));
                    }
                }
                else {
                    throw new Error("Can't instantiate DataEntity, missing the 'id' property.");
                }

                if (config.extends) {
                    if (typeof(config.extends) !== "string") {
                        throw new TypeError("Invalid extends for DataEntity, expected a string but got " +
                            typeof(config.extends));
                    }
                }

                if (config.fields) {
                    if (config.fields.constructor !== Array) {
                        throw new TypeError("Invalid fields for DataEntity, expected and array but got " +
                            typeof(config.fields));
                    }
                }
                else {
                    throw new Error("Can't instantiate DataEntity, missing the 'fields' property.");
                }

                if (config.requiredFields) {
                    if (config.requiredFields.constructor !== Array) {
                        throw new TypeError("Invalid requiredFields for DataEntity. Expected array but got " +
                            typeof(config.requiredFields.constructor));
                    }

                    for (var field of config.requiredFields) {
                        if (typeof(field) !== "string") {
                            throw new TypeError("Invalid required field for DataEntity. Expected a string but got " +
                                typeof(field));
                        }
                    }
                }

                if (config.eventsEntity && typeof(config.eventsEntity) !== "string") {
                    throw new TypeError("Invalid eventsEntity for DataEntity. Expected a string but got " +
                        typeof(config.eventsEntity));
                }

                if (config.sessionEntity && typeof(config.sessionEntity) !== "string") {
                    throw new TypeError("Invalid sessionEntity for DataEntity. Expected a string but got " +
                        typeof(config.sessionEntity));
                }
            };

            /**
             * Checks whether this DataEntity extends the specified DataEntity by checking the baseEntity tree.
             * @param anotherDataEntity
             * @returns {Boolean}
             */
            DataEntity.prototype.extendsEntity = function (anotherDataEntity) {
                if (!this.baseEntity || !anotherDataEntity || !(anotherDataEntity instanceof DataEntity)) {
                    return false;
                }

                if (this.baseEntity === anotherDataEntity) {
                    return true;
                }

                return this.baseEntity.extendsEntity(anotherDataEntity);
            };

            DataEntity.prototype.__defineGetter__("fieldsArray", function () {
                if (!this._fieldsArray) {
                    this._fieldsArray = [];
                    for (var field of Array.from(this.fields.values())) {
                        this._fieldsArray.push(field);
                    }
                }

                return this._fieldsArray;
            });

            /**
             * Given a the ID of another DataEntity, returns the possible JOINS between this entity and that one, which
             * can be used in a DataQuery
             * @param entityId
             */
            DataEntity.prototype.getEntityJoin = function (entityId) {
                if (!entityId || typeof(entityId) !== "string") {
                    throw new Error("Invalid joinedEntityId, expected a string but got " + entityId + ".");
                }

                if (entityId === this.id) {
                    return [];
                }

                return (this.linkedEntities || []).filter(function (linkedEntity) {
                    return linkedEntity.entity === entityId;
                });
            };

            return DataEntity;
        }]);
}());
