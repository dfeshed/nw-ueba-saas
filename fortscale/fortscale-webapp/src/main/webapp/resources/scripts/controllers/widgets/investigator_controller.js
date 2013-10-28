angular.module("Fortscale").controller("InvestigatorController", [
    "$scope", "$q", "$location", "$timeout", "entities", "database", "widgets", "conditions", "widgetTypes", "widgetsData", "server",
    function($scope, $q, $location, $timeout, entities, database, widgets, conditions, widgetTypes, widgetsData, server){
    var paging = {
            pageSize: 20,
            page: 1
        },
        tableSort,
        rawData,
        widgetTypesData;

    $scope.config = { entities: [] };
    $scope.fields = [];
    $scope.paging = paging;
    $scope.allowMultipleEntities = false;

    $scope.isDebug = !!localStorage.debug;

    $scope.sections = {
        sql: {
            collapsed: true
        },
        view: {
            collapsed: false
        }
    };

    $scope.selectEntity = function(update){
        $scope.config.entities = [$scope.currentEntity];

        if (update){
            setEntitiesToUrl();
            $location.search("params", null);
            $location.search("sql", null);
            $location.search("computedFields", null);
            return;
        }

        $scope.fields = $scope.currentEntity.fields;
        angular.forEach($scope.fields, function(field){
            field.enabled = true;
        });
    };

    $scope.addEntity = function(entity, update){
        $scope.config.entities.push(entity);

        if(update){
            setEntitiesToUrl();
            return;
        }

        setAvailableEntities();

        angular.forEach(entity.fields, function(field){
            field.enabled = true;
        });

        $scope.fields = $scope.fields.concat(entity.fields);
    };

    $scope.removeEntity = function(entityIndex){
        var entity = $scope.config.entities.splice(entityIndex, 1)[0];
        setEntitiesToUrl();
        entity.fields.forEach(function(field){
            var fieldIndex = $scope.fields.indexOf(field);
            if (~fieldIndex){
                $scope.fields.splice(fieldIndex, 1);
            }
        });
    };

    $scope.fieldTypeIcons = {
        "string": "font",
        "number": "plus",
        "date": "calendar",
        "computed": "beaker"
    };

    $scope.operators = conditions.operators;
    $scope.operatorsIndex = {};
    $scope.operatorsForType = {};
    $scope.fieldsChanged = false;

    angular.forEach($scope.operators, function(operator){
        $scope.operatorsIndex[operator.name] = operator;
        if (operator.types){
            angular.forEach(operator.types, function(operatorType){
                var operatorsType = $scope.operatorsForType[operatorType];
                if (!operatorsType)
                    operatorsType = $scope.operatorsForType[operatorType] = [];

                operatorsType.push(operator);
            });
        }
    });

    $scope.addFilter = function(field){
        if (!field.filters)
            field.filters = [];

        field.filters.push({
            operator: field.type === "date" ? "dateRange" : "equals",
            "value": ""
        })
    };

    $scope.removeFilter = function(field, filterIndex){
        field.filters.splice(filterIndex, 1);
        $scope.onFieldChange();
    };

    $scope.onFieldChange = function(field, value){
        $scope.fieldsChanged = true;
    };

    $scope.update = function(updateUrl){
        if (updateUrl)
            setUrlParams();
        else{
            $scope.fieldsChanged = false;
            getData();
        }
    };

    $scope.fieldTypes = {
        objectArray: {
            addMember: function(setting, memberValues){
                var member = {
                    _settings: angular.copy(setting.memberSettings)
                };

                angular.forEach(member._settings, function(memberSetting){
                    setSettingValues(member, memberSetting, memberValues);
                });

                setting.settingValues[setting.id].push(member);
            }
        },
        table: {
            addComputedField: function(entity){
                if (entity.computedFields && entity.computedFields.length){
                    var firstComputedField = entity.computedFields.splice(0, 1)[0];
                    firstComputedField.enabled = true;
                    entity.fields.push(firstComputedField);
                    $scope.onFieldChange();
                }
            },
            removeComputedField: function(entity, fieldIndex){
                var computedField = entity.fields.splice(fieldIndex, 1)[0];
                entity.computedFields.splice(0, 0, computedField);
                $scope.onFieldChange();
            }
        }
    };

    function findEntity(entityId){
        for(var i= 0, entity; entity = $scope.entities[i]; i++){
            if (entity.id === entityId){
                return entity;
            }
        }

        return null;
    }

    function findEntities(entityIds){
        var entities = [];

        entityIds.forEach(function(entityId){
            var foundEntity = findEntity(entityId);
            if (foundEntity)
                entities.push(foundEntity);
        });

        return entities;
    }

    function setAvailableEntities(){
        entities.getEntitiesConnections($scope.config.entities).then(function(entityConnections){
            console.log("conn: ", entityConnections)
            $scope.availableEntities = [];
            angular.forEach(entityConnections, function(connection){
                $scope.availableEntities.push(connection.entity);
            });

            $scope.entityToAdd = $scope.availableEntities[0];
        });
    }

    function setEntitiesToUrl(){
        var entityIds = [];
        $scope.config.entities.forEach(function(configEntity){
            entityIds.push(configEntity.id);
        });

        $location.search("entities", entityIds.join(","));
    }
    function setUrlParams(){
        var params = {},
            hasFilters,
            enabledFields = [],
            enabledComputedFields = [],
            allFields = true;

        angular.forEach($scope.config.entities, function(entity){
            angular.forEach(entity.fields, function(field){
                if (field.enabled){
                    var fieldsArray = field.isComputedField ? enabledComputedFields : enabledFields;
                    fieldsArray.push([field.entity.id, field.id].join("."));
                }
                else
                    allFields = false;

                if (field.filters && field.filters.length){
                    hasFilters = true;
                    var fieldParams = params[field.id];
                    if (!fieldParams)
                        fieldParams = params[field.id] = [];

                    angular.forEach(field.filters, function(filter){
                        var filterCopy = angular.copy(filter);
                        delete filterCopy.$$hashKey;

                        fieldParams.push(filterCopy);
                    });
                }
            });
        });
        var currentParams = $location.search();
        for(var paramName in currentParams){
            if (paramName !== "entities")
                $location.search(paramName, null);
        }

        $location.search("params", hasFilters ? JSON.stringify(params) : null);
        if (!allFields)
            $location.search("fields", enabledFields.join(","));
        $location.search("viewType", $scope.currentViewType.type);
        $location.search("paging", JSON.stringify(paging));

        if (enabledComputedFields.length)
            $location.search("computedFields", enabledComputedFields.join(","));
    }

    function getEntityTableFields(entities, fieldsSetting){
        var fields = [],
            sortField,
            dateField;

        angular.forEach(entities, function(entity){
            angular.forEach(entity.fields, function(field){
                if (field.enabled){
                    var fieldSetting = {
                        name: field.name,
                        sortBy: field.id,
                        value: "{{" + field.id + "}}",
                        link: getTableFieldLink(field)
                    };

                    if (field.type === "date" || (field.type === "number" && /time/i.test(field.id))){
                        fieldSetting.transform = {
                            method: "date",
                            options: {
                                format: "MM/DD/YY HH:mm"
                            }
                        };

                        dateField = fieldSetting;
                    }

                    if (/score/i.test(field.id)){
                        fieldSetting = angular.extend(fieldSetting, {
                            "style": "score",
                            "styleParams": {
                                "value": field.id
                            },
                            transform: {
                                method: "round"
                            }
                        });

                        if (!sortField){
                            fieldSetting.sortDirection = -1;
                            tableSort = { field: field.id, direction: -1 };
                            sortField = field;
                        }
                    }

                    $scope.fieldTypes.objectArray.addMember(fieldsSetting, fieldSetting);
                }
            });
        });

        if (!sortField && dateField){
            dateField.sortDirection = -1;
            tableSort = { field: dateField.sortBy, direction: -1 };
        }

        return fields;
    }

    function getTableFieldLink(field){
        if (field.isComputedField)
            return null;

        var link = window.location.href,
            params = {};

        params[field.id] = {
            operator: field.type === "date" ? "dateRange" : "equals",
            field: field.id,
            entity: field.entity.id,
            value: "{{" + field.id + "}}",
            transform: {
                method: "date",
                options: {
                    format: "MM/DD/YYYY"
                }
            }
        };

        link += ~link.indexOf("?") ? "&" : "?";

        link += "params=" + JSON.stringify(params);
        return link;
    }

    function getQueryFields(){
        var fields = [];

        angular.forEach($scope.config.entities, function(entity){
            angular.forEach(entity.fields, function(field){
                if (field.enabled && !field.isComputedField)
                    fields.push(($scope.allowMultipleEntities ? field.entity.id + "." : "") + field.id);
            });
        });

        return fields;
    }

    function getQueryConditions(){
        var conditions = [];

        angular.forEach($scope.config.entities, function(entity){
            angular.forEach(entity.fields, function(field){
                if (field.filters){
                    angular.forEach(field.filters, function(filter){
                        conditions.push({
                            field: field.id,
                            operator: filter.operator,
                            value: field.type === "number" ? parseInt(filter.value, 10) : filter.value,
                            or: true
                        })
                    });
                }
            });
        });
        return conditions;
    }

    $scope.$on("tablePage", function(e, data){
        paging = data;
        getData();
    });

    $scope.$on("tableSort", function(e, data){
        tableSort = data;
        getData();
    });

    $scope.onViewTypeSelect = function(setToUrl){
        if (setToUrl){
            $location.search("viewType", $scope.currentViewType.type);
            return;
        }

        $scope.view = $scope.currentViewType;
        setCurrentView($scope.currentViewType);
        setViewData();
    };

    $scope.onSettingsChange = function(){
        setViewData();
    };

    $scope.querySql = function(sqlQuery, setToUrl){
        var deferred = $q.defer();

        $scope.error = null;
        $scope.noData = false;
        $scope.loading = true;

        if (setToUrl){
            $location.search("sql", sqlQuery);
            $location.search("entities", null);
            deferred.resolve();
            return deferred.promise;
        }

        server.sqlQuery(sqlQuery).then(function(results){
            setSqlData(results);
            deferred.resolve();
        }, function(error){
            setSqlData({ data: [], total: 0 });
            $scope.error = "Can't get SQL results.";
            $scope.noData = false;
            deferred.reject();
        });

        return deferred.promise;
    };

    function setSqlData(sqlResults){
        var data = sqlResults.data;
        rawData = sqlResults;

        if (!data || !data.length){
            $scope.view.data = null;
            $scope.config.entities = [];
            $scope.noData = true;
        }
        else{
            $scope.config.entities = [{ fields: [] }];
            for(var fieldName in data[0]){
                $scope.config.entities[0].fields.push({
                    id: fieldName,
                    name: fieldName,
                    type: typeof(data[0][fieldName]),
                    entity: $scope.config.entities[0],
                    enabled: true
                });
            }
        }

        $scope.loading = false;
    }

    function getEntityDefaultSort(entity){
        var sort,
            dateField;

        for(var i= 0, field; field = entity.fields[i]; i++){
            if (field.isComputedField && /score/i.test(field.id)){
                sort = { field: field.id, direction: -1 };
                break;
            }
            else if (field.type === "date")
                dateField = field;
        }

        if (!sort && dateField)
            sort = { field: dateField.id, direction: -1 };

        return sort;
    }

    function setDisplayWidget(){
        $scope.widgets = [{
            title: "Results",
            views: [{
                type: $scope.config.view.type,
                settings: angular.extend($scope.currentViewSettings, paging)
            }]
        }];

        $scope.widgets[0].report = {
            query: {
                dataSource: "api",
                sql: true,
                endpoint: {
                    entities: $scope.config.entities,
                    fields: getQueryFields(),
                    conditions: getQueryConditions(),
                    paging: paging,
                    sort: tableSort && tableSort.field ? tableSort : getEntityDefaultSort($scope.config.entities[0])
                }
            }
        };
    }
    function getData(){
        setDisplayWidget();
    }

    function setViewData(results){
        if (results)
            rawData = results;

        $scope.view.settings = $scope.currentViewSettings;
        setDisplayWidget();
    }

    function getTypeDefaultValue(type){
        switch(type){
            case 'string':
                return "";
            case 'number':
                return 0;
            case 'boolean':
                return true;
            case 'date':
                return new Date();
            case 'objectArray':
                return [];
            default:
                return null;
        }
    }

    function setSettingValues(parent, settingDefinition, settingValues){
        settingDefinition.settingValues = parent;

        if (settingDefinition.type === "object"){
            parent[settingDefinition.id] = {};

            angular.forEach(settingDefinition.properties, function(property){
                parent[settingDefinition.id][property.id] = settingValues && settingValues[settingDefinition.id] !== undefined ? settingValues[settingDefinition.id][property.id] : setSettingValues(parent[settingDefinition.id], property);
            });
        }
        else{
            if (settingDefinition.default !== undefined)
                parent[settingDefinition.id] = settingValues ? settingValues[settingDefinition.id] : settingDefinition.default;
            else
                parent[settingDefinition.id] = settingValues ? settingValues[settingDefinition.id] : getTypeDefaultValue(settingDefinition.type);
        }
    }

    function setViewSettings(){
        if (!$scope.currentViewType)
            return;

        var currentViewSettingsDefinition = $scope.currentViewType.settings;
        var settings = {};

        angular.forEach(currentViewSettingsDefinition, function(settingDefinition){
            setSettingValues(settings, settingDefinition);
        });

        $scope.currentViewSettings = settings;
    }

    function getField(fieldId, entityId){
        var fieldEntity;
        if (!entityId)
            fieldEntity = $scope.config.entities[0];
        else{
            for(var entityIndex = 0, entity; entity = $scope.config.entities[entityIndex]; entityIndex++){
                if (entity.id === entityId){
                    fieldEntity = entity;
                    break;
                }
            }
        }

        if (!fieldEntity)
            return null;

        for(var i= 0, field; field = fieldEntity.fields[i]; i++){
            if (fieldId === field.id)
                return field;
        }

        return null;
    }

    function getFiltersFromUrl(){
        var queryParams = $location.search(),
            filterParams = $location.search().params ? JSON.parse(decodeURIComponent($location.search().params)) : {},
            field;

        clearFilters();

        for(var paramName in queryParams){
            if (!~["entities", "params", "sql"].indexOf(paramName) && !filterParams[paramName]){
                filterParams[paramName] = {
                    operator: "equals",
                    field: paramName,
                    value: queryParams[paramName]
                }
            }
        }

        for(var fieldId in filterParams){
            if (~["sql", "entities"].indexOf(fieldId))
                continue;

            field = getField(fieldId, filterParams[fieldId].entity);
            if (field){
                field.filters = field.filters || [];
                if (angular.isArray(filterParams[fieldId]))
                    field.filters = field.filters.concat(filterParams[fieldId]);
                else
                    field.filters.push(filterParams[fieldId]);
            }
        }
    }

    function getFieldsFromUrl(){
        var queryParams = $location.search();
        if (!queryParams.fields && !queryParams.computedFields)
            return;

        var fields = queryParams.fields ? queryParams.fields.split(",") : null,
            computedFields = queryParams.computedFields ? queryParams.computedFields.split(",") : [],
            entityFieldsIndex = {};

        forEachField(fields);
        forEachField(computedFields);

        function forEachField(fields){
            angular.forEach(fields, function(field){
                var fieldParts = field.split("."),
                    entityId = fieldParts[0],
                    fieldId = fieldParts[1],
                    entityFields = entityFieldsIndex[entityId];

                if (!entityFields)
                    entityFields = entityFieldsIndex[entityId] = [];

                entityFields.push(fieldId);
            });
        }

        var entity;
        for(var entityId in entityFieldsIndex){
            entity = findEntity(entityId);
            if (entity){
                angular.forEach(entity.fields, function(entityField){
                    entityField.enabled = !fields || !!~entityFieldsIndex[entityId].indexOf(entityField.id);
                });
                if (entity.computedFields && entity.computedFields.length){
                    for (var i = entity.computedFields.length - 1, computedField; computedField = entity.computedFields[i]; i--){
                        if (~entityFieldsIndex[entityId].indexOf(computedField.id)){
                            computedField.enabled = true;
                            entity.fields.push(computedField);
                            entity.computedFields.splice(i, 1);
                            $scope.fields.push(computedField);
                        }
                    }
                }
            }
        }
    }

    function clearFilters(){
        angular.forEach($scope.config.entities, function(entity){
            angular.forEach(entity.fields, function(field){
                if (field.filters)
                    field.filters = [];
            });
        });
    }

    function findSettingDefinition(viewSettings, settingId){
        for(var i= 0, settingDefinition; settingDefinition = viewSettings[i]; i++){
            if (settingDefinition.id === settingId)
                return settingDefinition;
        }

        return null;
    }

    function setCurrentView(viewType){
        if (!viewType && $scope.config.view)
            viewType = $scope.config.view;

        $scope.currentViewType = $scope.config.view = viewType;
        setViewSettings();

        if (viewType === widgetTypesData.table){
            getEntityTableFields($scope.config.entities, findSettingDefinition(viewType.settings, "fields"));
        }
        else{
            if (viewType === widgetTypesData.pieChart){
                var firstNumericField,
                    firstStringField;

                for(var i= 0, field; field = $scope.fields[i]; i++){
                    if (!field.enabled)
                        continue;

                    if (field.type === "number"){
                        firstNumericField = field;
                        if (firstStringField)
                            break;
                    }
                    else if (field.type === "string"){
                        firstStringField = field;
                        if (firstNumericField)
                            break;
                    }
                }

                if (firstNumericField)
                    $scope.currentViewSettings.chartValue = firstNumericField.id;
                if (firstStringField)
                    $scope.currentViewSettings.chartLabel = firstStringField.id;
            }
        }

        if ($scope.config.view){
            $scope.view = {
                type: $scope.config.view.type,
                settings: $scope.currentViewSettings
            };
        }
    }

    function init(){
        $q.all([ entities.getEntities(), widgetTypes.getWidgetTypes()]).then(function(results){
            var entitiesData = results[0];
            widgetTypesData = results[1];

            function withEntity(){
                getFiltersFromUrl();
                getFieldsFromUrl();

                if ($location.search().paging){
                    $scope.paging = paging = JSON.parse($location.search().paging);
                }

                $scope.viewTypes = widgetTypesData;

                var viewType = $location.search().viewType;
                if (!viewType){
                    viewType = "table";
                    $scope.sections.view.collapsed = true;
                }

                setCurrentView(widgetTypesData[viewType]);

                if ($location.search().sql)
                    setViewData(rawData);
                else
                    $scope.update();
            }

            $scope.entities = entitiesData;
            $scope.availableEntities = $scope.entities;

            if ($location.search().entities || $location.search().sql){
                if ($location.search().entities){
                    var urlEntities = findEntities($location.search().entities.split(","));
                    if ($scope.allowMultipleEntities){
                        urlEntities.forEach(function(urlEntity){
                            $scope.addEntity(urlEntity);
                        });
                    }
                    else{
                        $scope.currentEntity = urlEntities[0];
                        $scope.selectEntity();
                    }

                    withEntity();
                }
                else if ($location.search().sql){
                    $scope.sqlQuery = $location.search().sql;
                    $scope.querySql($location.search().sql).then(withEntity);
                }
            }
        });
    }

    init();
}]);