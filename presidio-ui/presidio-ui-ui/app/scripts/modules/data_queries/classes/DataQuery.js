(function () {
    'use strict';

    angular.module("DataQueries").factory("DataQuery",
        ["utils", "conditions", "dataEntities", "queryOperators", "DataEntity",
            function (utils, conditions, dataEntities, queryOperators, DataEntity) {

                function dateTimeParse (value) {
                    if (typeof(value) === "string") {
                        var dateRangeMatch = value.match(dateRangeRegExp);
                        if (dateRangeMatch) {
                            value = {timeStart: parseInt(dateRangeMatch[1]), timeEnd: parseInt(dateRangeMatch[2])};
                        }
                    }

                    if (Object(value) === value && !angular.isDate(value)) {
                        return dateTimeParse(value.timeStart) + "," + dateTimeParse(value.timeEnd);
                    }

                    var _value = value,
                        momentValue = utils.date.getMoment(value);

                    if (momentValue.isValid()) {
                        return momentValue.unix();
                    }

                    return _value;
                }

                function numberRangeParse (value) {
                    if (typeof(value) === "number") {
                        return value;
                    }

                    if (typeof(value) === "string") {
                        var parsedNumber = parseFloat(value);
                        if (!isNaN(parsedNumber)) {
                            return parsedNumber;
                        }

                        var numberRangeMatch = value.match(numberRangeRegExp);

                        if (numberRangeMatch) {
                            value = {fromValue: parseInt(numberRangeMatch[1]), toValue: parseInt(numberRangeMatch[2])};
                        } else {
                            throw new Error("Unable to parse string into int:  " + value);
                        }
                    }
                    return value.fromValue + "," + value.toValue;
                }

                function validateEntity (entityId) {
                    if (!dataEntities.getEntityById(entityId)) {
                        throw new Error("Unknown entity, '" + entityId + "'.");
                    }
                }

                function validateField (entityId, fieldId) {
                    var entity = dataEntities.getEntityById(entityId);
                    if (!entity) {
                        throw new Error("Unknown DataEntity, '" + entityId + "'.");
                    }

                    if (!entity.fields.get(fieldId)) {
                        throw new Error("Unknown DataQuery field in entity " + entityId + ": " + fieldId);
                    }
                }

                // Helper to check if an entity exists in a DataQuery, for validating fields:
                function validateEntityExistsInDataQuery (dataQuery, entityId) {
                    if (!entityId || typeof(entityId) !== "string") {
                        throw new Error("Invalid entityId to validate, expected a string, got: " + entityId);
                    }

                    if (~dataQuery.entities.indexOf(entityId)) {
                        return true;
                    }

                    var found;

                    if (dataQuery.join) {
                        found = dataQuery.join.some(function (join) {
                            return join.entity === entityId;
                        });
                    }

                    if (!found && dataQuery.subQuery) {
                        found = dataQuery.subQuery.dataQueries.some(function (subQuery) {
                            try {
                                validateEntityExistsInDataQuery(subQuery, entityId);
                                return true;
                            }
                            catch (error) {
                                return false;
                            }
                        });
                    }

                    if (found) {
                        return true;
                    }

                    throw new Error("Entity '" + entityId + "' is not available in the DataQuery.");
                }

                function DataQuery (data, params) {
                    var dataQuery = this;

                    this.fields = [];
                    this.entities = [];
                    this.sort = [];

                    // Setting to undefined just so it's clear they're available:
                    this.conditions = undefined;
                    this.limit = undefined;
                    this.offset = undefined;
                    this.subQuery = undefined;

                    if (data && Object(data) === data) {
                        if (data.entity) {
                            if (typeof(data.entity) !== "string") {
                                throw new TypeError("Invalid entity for DataQuery, expected string but got " +
                                    typeof(data.entity));
                            }

                            data.entities = this.entities = [data.entity];
                        }
                        else if (data.entities) {
                            if (!angular.isArray(data.entities)) {
                                throw new TypeError("Expected array for DataQuery.entities, got " +
                                    typeof(data.entities));
                            }

                            this.entities = data.entities;
                        }

                        // Make sure the DataQuery doesn't use unknown entities:
                        this.entities.forEach(validateEntity);

                        if (data.entitiesJoin) {
                            var entitiesJoin = data.entitiesJoin.constructor === Array ? data.entitiesJoin :
                                [data.entitiesJoin];
                            this.join = entitiesJoin.map(function (joinConfig) {
                                return new DataQueryJoin(joinConfig, dataQuery.entities, params);
                            });
                        }

                        if (data.subQuery) {
                            if (this.entities && this.entities.length) {
                                throw new Error("A DataQuery can't have both an entity and a subquery.");
                            }

                            this.subQuery = new DataQuerySubQuery(data.subQuery, params);
                        }

                        if (data.fields) {
                            if (!angular.isArray(data.fields)) {
                                throw new TypeError("Expected array for DataQuery.fields, got " + typeof(data.fields));
                            }

                            this.fields = data.fields.map(function (field) {
                                return new DataQueryField(field, dataQuery, params);
                            });
                        }

                        if (data.conditions) {
                            var rootTerm = angular.isArray(data.conditions) ? {
                                operator: "AND",
                                terms: data.conditions
                            } : data.conditions;
                            if (!angular.isObject(rootTerm)) {
                                throw new Error("Invalid conditions for data query, must be either an object or " +
                                    "array.");
                            }

                            if (isConditionEnabled(rootTerm, params)) {
                                this.conditions = new DataQueryCondition(rootTerm, params, this);
                            }
                        }

                        if (data.groupBy) {
                            var groupByArray = angular.isArray(data.groupBy) ? data.groupBy : [data.groupBy];
                            this.groupBy = groupByArray.map(function (groupByField) {
                                return new DataQueryField(groupByField, dataQuery, params);
                            });
                        }

                        if (data.sort) {
                            var sortArray = angular.isArray(data.sort) ? data.sort : [data.sort];
                            this.sort = sortArray.map(function (sortItem) {
                                return new DataQuerySort(sortItem, dataQuery, params);
                            });
                        }

                        if (data.limit !== null && typeof(data.limit) !== 'undefined') {
                            var limit = data.limit;
                            if (typeof(data.limit) === "string") {
                                limit = Number(utils.strings.parseValue(limit));
                            }

                            // -1 == no limit
                            if (isNaN(limit) || !angular.isNumber(limit) || limit < -1 || Math.floor(limit) !== limit) {
                                throw new TypeError("Invalid limit for DataQuery, must be a positive integer");
                            }

                            this.limit = limit;
                            this.offset = 0;
                        }

                        if (data.offset) {
                            var offset = data.offset;
                            if (typeof(data.offset) === "string") {
                                offset = Number(utils.strings.parseValue(offset));
                            }

                            if (isNaN(offset) || !angular.isNumber(data.offset) || data.offset < 1 ||
                                Math.floor(data.offset) !== data.offset) {
                                throw new TypeError("Invalid offset for DataQuery, must be a positive integer");
                            }

                            this.offset = data.offset;
                        }
                    }
                }

                function isConditionEnabled (condition, params) {
                    if (condition.enabled === undefined) {
                        return true;
                    }

                    if (condition.enabled === true || condition.enabled === false) {
                        return condition.enabled;
                    }

                    return conditions.validateConditions(condition.enabled, params, params);
                }

                function DataQueryField (data, dataQuery, params) {
                    if (typeof(data) === "string") {
                        var parts = data.split(".");
                        if (parts.length === 1) {
                            data = {id: data};
                        } else if (parts.length === 2) {
                            data = {entity: parts[0]};
                            if (parts[1] === "*") {
                                data.allFields = true;
                            } else {
                                data.id = parts[1];
                            }
                        }
                        else {
                            throw new Error("Invalid field, '" + data + "'");
                        }
                    }

                    if (Object(data) !== data) {
                        throw new TypeError("Invalid configuration for DataQueryField. Expected string or object, " +
                            "got " + typeof(data));
                    }

                    if (!data.id && !data.alias && !data.allFields) {
                        throw new Error("Invalid DataQuery field - must have either id, alias or allFields.");
                    }

                    if (data.alias) {
                        this.alias = data.alias;
                    }

                    if (data.entity) {
                        if (!dataEntities.getEntityById(data.entity)) {
                            throw new Error("Unknown entity, '" + data.entity + "', for field '" +
                                (data.alias || data.id) + "'.");
                        }

                        this.entity = data.entity;
                    }

                    if (data.id) {
                        this.id = utils.strings.parseValue(data.id, {}, params);
                    }

                    if (data.allFields) {
                        this.allFields = true;
                    }

                    if (data.func) {
                        this.func = new DataQueryFieldFunction(data.func, params);
                    }

                    if (data.valueParam) {
                        this.value = params[data.valueParam];
                    } else if (data.value !== undefined) {
                        if (data.value === null) {
                            throw new Error("The value of a DataQueryField can't be null.");
                        }

                        this.value =
                            typeof(data.value) === "string" ? utils.strings.parseValue(data.value, {}, params) :
                                data.value;
                    }

                    if (this.value !== undefined) {
                        if (this.value !== null && typeof(this.value) === "object" && !angular.isDate(this.value) &&
                            !angular.isArray(this.value)) {
                            throw new TypeError("Invalid value for field - can't be a literal object.");
                        }

                        this.valueType = this.value === null ? "STRING" : String(typeof(this.value)).toUpperCase();
                        if (angular.isDate(this.value)) {
                            this.valueType = "TIMESTAMP";
                        } else if (angular.isArray(this.value)) {
                            this.valueType = "ARRAY";
                        }
                    }

                    var entityId = this.entity;

                    // If an entity is explicitly specified for the field, validate that it's available in the DataQuery
                    if (entityId) {
                        try {
                            validateEntityExistsInDataQuery(dataQuery, entityId);
                        }
                        catch (error) {
                            throw new Error("Unavailable entity '" + entityId + "' for field: " + JSON.stringify(this));
                        }
                    }
                    else if (dataQuery.entities && dataQuery.entities.length) {
                        // Otherwise, assume the DataQuery's entity
                        entityId = dataQuery.entities[0];
                    }

                    if (this.id && entityId) {
                        validateField(entityId, this.id);
                    }
                }

                function DataQueryCondition (data, params, dataQuery) {
                    if (Object(data) !== data) {
                        throw new TypeError("Invalid type for DataQuery condition, should be an object");
                    }

                    if (!dataQuery) {
                        throw new Error("Missing dataQuery.");
                    }

                    if (!(dataQuery instanceof DataQuery)) {
                        throw new TypeError("Can't create DataQueryCondition, expected dataQuery to be an " +
                            "instance of DataQuery, got " + dataQuery.constructor.name + ".");
                    }

                    this.type = "term";

                    if (data.operator) {
                        if (typeof(data.operator) !== "string") {
                            throw new TypeError("Invalid value for DataQueryCondition.operator, " +
                                "expecting a string, got " + typeof(data.operator));
                        }

                        var upperCaseType = data.operator.toUpperCase();
                        if (upperCaseType !== "AND" && upperCaseType !== "OR") {
                            throw new Error("Invalid value for DataQueryCondition.operator, must be either " +
                                "'AND' or 'OR' (case insensitive)");
                        }

                        this.logicalOperator = upperCaseType;
                    }
                    else {
                        this.logicalOperator = "AND";
                    }

                    if (data.terms) {
                        this.terms = [];
                    }

                    for (var i = 0, term; !!(term = data.terms[i]); i++) {
                        if (term.type && term.type === "term") {
                            if (isConditionEnabled(term, params)) {
                                this.terms.push(new DataQueryCondition(term, params, dataQuery));
                            }
                        }
                        else {
                            if (isConditionEnabled(term, params)) {
                                this.terms.push(new DataQueryConditionField(term, params, dataQuery));
                            }
                        }
                    }
                }

                function DataQueryConditionField (data, params, dataQuery) {
                    if (Object(data) !== data) {
                        throw new TypeError("Invalid type for DataQuery condition field, should be an object");
                    }

                    var dataCopy = angular.copy(data);

                    var conditionValue = dataCopy.valueParam ? params[dataCopy.valueParam] : dataCopy.value;

                    delete dataCopy.value;
                    delete dataCopy.valueParam;

                    this.field = new DataQueryField(dataCopy, dataQuery, params);

                    if (!data.operator || typeof(data.operator) !== "string") {
                        throw new Error("Can't create DataQueryConditionField - operator property is missing " +
                            "or is not a string");
                    }

                    var conditionOperator = queryOperators.operators.get(data.operator);
                    if (!conditionOperator) {
                        throw new Error("Can't create DataQueryConditionField - unknown operator, '" + data.operator +
                            "'");
                    }

                    this.queryOperator = conditionOperator.dataQueryOperator;
                    this.type = "field";

                    if (conditionOperator.requiresValue) {

                        // The condition can be evaluated against another field, like "event_score > event_time_score",
                        // rather than "event_score > 50":
                        if (data.valueField) {
                            this.valueField = new DataQueryField(data.valueField, dataQuery, params);
                        }

                        if (conditionValue === undefined && !this.valueField) {
                            throw new Error("Can't create DataQueryConditionField - a value is required for " +
                                "operator '" + data.operator + "'");
                        }

                        this.value = conditionValue;
                        this.valueType = this.field.valueType;

                        if (this.field.id) {
                            var entityId = this.field.entity || dataQuery.entities[0];
                            validateField(entityId, this.field.id);

                            // If valueField is used, meaning that another field is used as value, there's no need to
                            // do anything about the condition's value:
                            if (!this.valueField) {
                                var entity = dataEntities.getEntityById(entityId);
                                var entityField = entity.fields.get(this.field.id);

                                if (typeof(this.value) === "string") {
                                    this.value = utils.strings.parseValue(this.value, {}, params);
                                }

                                // Some field types have parsers, since the data might have to be formatted before
                                // sending:
                                var valueParser = valueTypeParsers[entityField.type.id];
                                if (valueParser) {
                                    this.value = valueParser(this.value);
                                }

                                if (angular.isArray(this.value)) {
                                    this.value = this.value.map(function (val) {
                                        return val.replace(/[,]/g, "~~");
                                    }).join();
                                }
                            }
                        }
                    }

                    delete this.field.valueType;
                }

                function DataQuerySort (data, dataQuery, params) {
                    if (typeof(data) === "string") {
                        this.field = new DataQueryField(data, dataQuery, params);
                    }
                    if (typeof(data.field) === "string") {
                        this.field = new DataQueryField(data.field, dataQuery, params);
                    } else if (Object(data) === data) {
                        this.field = new DataQueryField(data.field || data, dataQuery, params);
                    }

                    if (!this.field) {
                        throw new Error("Can't create DataQuery sort - missing the field property");
                    }

                    if (data.direction) {
                        if (typeof(data.direction) !== "string") {
                            throw new TypeError("Invalid direction for DataQuerySort - expected string but got " +
                                typeof(data.direction));
                        }

                        var directionUpperCase = data.direction.toUpperCase();

                        if (directionUpperCase !== "ASC" && directionUpperCase !== "DESC") {
                            throw new Error("Invalid direction for DataQuery sort, expected either 'ASC' or " +
                                "'DESC' (case insensitive) but got '" + data.direction + "'");
                        }

                        this.direction = directionUpperCase;
                    }
                }

                function DataQueryFieldFunction (data) {
                    if (!data.name) {
                        throw new Error("Can't create DataQuery field function - missing function name.");
                    }

                    if (typeof(data.name) !== "string") {
                        throw new Error("Can't create DataQuery field function - function name must be a string.");
                    }

                    this.name = data.name.toLowerCase();
                    if (data.params) {
                        this.params = {};
                        for (var p in data.params) {
                            if (data.params.hasOwnProperty(p)) {
                                this.params[p] = String(data.params[p]);
                            }
                        }
                    }
                }

                function DataQueryJoin (data, entities) {
                    if (data.joinType) {
                        if (typeof(data.joinType) !== "string") {
                            throw new TypeError("Invalid joinType for DataQueryJoin. Expected string, got " +
                                typeof(data.joinType) + ".");
                        }

                        var joinType = data.joinType.toUpperCase();
                        if (!~["RIGHT", "LEFT"].indexOf(joinType)) {
                            throw new Error("Invalid joinType, expected either 'RIGHT' or 'LEFT' " +
                                "(case-insensitive), got '" + joinType + "'.");
                        }

                        this.type = joinType;
                    }
                    else {
                        this.type = "LEFT";
                    }

                    if (!data.entity) {
                        throw new Error("Missing entity for DataQueryJoin.");
                    }

                    this.entity = data.entity instanceof DataEntity ? data.entity.id : data.entity;

                    if (typeof(this.entity) !== "string") {
                        throw new TypeError("Invalid entity for DataQueryJoin. Expected string, got " +
                            typeof(this.entity));
                    }

                    if (~entities.indexOf(data.entity)) {
                        throw new Error("Can't create DataQueryJoin, the entity " + data.entity +
                            " already exists in the DataQuery.");
                    }

                    if (!data.joinFields.left || !data.joinFields.right) {
                        throw new Error("DataQueryJoin.joinFields should contain both left and right fields.");
                    }

                    ["left", "right"].forEach(function (side) {
                        var entityField = data.joinFields[side].split(".");
                        if (entityField.length !== 2) {
                            throw new Error("Invalid " + side + " joinField. Expected [entity].[field] but got '" +
                                data.joinFields[side] + "'.");
                        }

                        validateEntity(entityField[0]);
                        validateField(entityField[0], entityField[1]);
                        this[side] = {entity: entityField[0], field: entityField[1]};
                    }.bind(this));
                }

                function DataQuerySubQuery (data, params) {
                    if (data.combineMethod && typeof(data.combineMethod) !== "string") {
                        throw new TypeError("Invalid combineMethod for DataQuerySubQuery, expected a string but got " +
                            typeof(data.combineMethod) + ".");
                    }

                    this.combineMethod = data.combineMethod ? queryCombineMethods[data.combineMethod] :
                        queryCombineMethods.UnionDistinct;
                    if (!this.combineMethod) {
                        throw new Error("Invalid combineMethod for DataQuerySubQuery: '" + data.combineMethod + "'.");
                    }

                    if (data.dataQueries) {
                        if (!angular.isArray(data.dataQueries)) {
                            throw new TypeError("Can't create DataQuerySubQuery, expected dataQueries to be an array.");
                        }

                        this.dataQueries = data.dataQueries.map(function (dataQueryConfig) {
                            var extendedDataQueryConfig = utils.objects.extend({}, dataQueryConfig, data.common);
                            return new DataQuery(extendedDataQueryConfig, params);
                        });
                    }
                    else {
                        this.dataQueries = [];
                    }
                }

                var queryCombineMethods = {
                    "UnionAll": "UnionAll",
                    "UnionDistinct": "UnionDistinct"
                };

                /**
                 * valueTypeParsers are used for preparing values to be sent to the server in data queries.
                 * DataQueryConditionField.value, for example, needs to be parsed. All parsers should return a string,
                 * since the dataQuery API expects values to be sent as strings.
                 */
                var valueTypeParsers = {
                    "DATE_TIME": dateTimeParse,
                    "TIMESTAMP": dateTimeParse,
                    "NUMBER": numberRangeParse
                };
                var numberRangeRegExp = /^(\d+),(\d+)$/;
                var dateRangeRegExp = /^(\d+),(\d+)$/;

                return DataQuery;

            }]);
}());
