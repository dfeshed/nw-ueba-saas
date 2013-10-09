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

    $scope.config = {};

    $scope.onEntitySelect = function(update){
        var foundEntity;

        if(update){
            $location.search("entity", $scope.config.entity);
            $location.search("params", null);
            return;
        }
        for(var i= 0, entity; (entity = $scope.entities[i]) && !foundEntity; i++){
            if (entity.id === $scope.config.entity)
                foundEntity = entity;
        }

        if (foundEntity){
            $scope.currentEntity = foundEntity;
            angular.forEach(foundEntity.fields, function(field){
                field.entity = foundEntity;
                field.enabled = true;
            });

        }
    };

    $scope.fieldTypeIcons = {
        "string": "font",
        "number": "plus",
        "date": "calendar"
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
            operator: "equals",
            "value": ""
        })
    };

    $scope.removeFilter = function(field, filterIndex){
        field.filters.splice(filterIndex, 1);
        $scope.onFieldChange();
    };

    $scope.onFieldChange = function(){
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

    $scope.fields = {
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
        }
    };

    function setUrlParams(){
        var params = {},
            hasFilters,
            enabledFields = [];

        angular.forEach($scope.currentEntity.fields, function(field){
            if (field.enabled)
                enabledFields.push([field.entity.id, field.id].join("."));

            if (field.filters && field.filters.length){
                hasFilters = true;
                var fieldParams = params[field.id];
                if (!fieldParams)
                    fieldParams = params[field.id] = [];

                angular.forEach(field.filters, function(filter){
                    fieldParams.push(filter);
                });
            }
        });

        var currentParams = $location.search();
        for(var paramName in currentParams){
            if (paramName !== "entity")
                $location.search(paramName, null);
        }

        $location.search("params", hasFilters ? JSON.stringify(params) : null);
        $location.search("fields", enabledFields.join(","));
        $location.search("viewType", $scope.currentViewType.type);
    }

    function getEntityTableFields(entity, fieldsSetting){
        var fields = [];
        angular.forEach(entity.fields, function(field){
            if (field.enabled){
                var fieldSetting = {
                    name: field.name,
                    sortBy: field.id,
                    value: "{{" + field.id + "}}",
                    link: getTableFieldLink(field)
                };

                if (field.type === "date"){
                    fieldSetting.transform = {
                        method: "date",
                        options: {
                            format: "MM/DD/YY HH:mm"
                        }
                    };
                }

                if (/score/i.test(field.id)){
                    fieldSetting = angular.extend(fieldSetting, {
                        "style": "score",
                        "styleParams": {
                            "value": field.id
                        }
                    });
                }

                $scope.fields.objectArray.addMember(fieldsSetting, fieldSetting);
            }
        });

        return fields;
    }

    function getTableFieldLink(field){
        var link = window.location.href,
            params = {};

        params[field.id] = {
            operator: "equals",
            field: field.id,
            value: "{{" + field.id + "}}"
        };

        link += ~link.indexOf("?") ? "&" : "?";

        link += "params=" + JSON.stringify(params);
        return link;
    }

    function getQueryFields(){
        var fields = [];

        angular.forEach($scope.currentEntity.fields, function(field){
            if (field.enabled)
                fields.push(field.id);
        });

        return fields;
    }

    function getQueryConditions(){
        var conditions = [];

        angular.forEach($scope.currentEntity.fields, function(field){
            if (field.filters){
                angular.forEach(field.filters, function(filter){
                    conditions.push({
                        field: field.id,
                        operator: filter.operator,
                        value: filter.value,
                        or: true
                    })
                });
            }
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
        if (setToUrl){
            $location.search("sql", sqlQuery);
            return;
        }

        server.queryServer({
            endpoint: {
                entity: "investigate",
                query: sqlQuery
            }
        }).then(function(results){
            setSqlData(results);
        }, function(error){
            setSqlData({ data: [], total: 0 });
            console.error("Can't get SQL results.");
        });
    };

    function setSqlData(sqlResults){
        var data = sqlResults.data;
        rawData = sqlResults;

        if (!data || !data.length){
            $scope.view.data = null;
            $scope.currentEntity = null;
        }
        else{
            $scope.currentEntity = { fields: [] };
            for(var fieldName in data[0]){
                $scope.currentEntity.fields.push({
                    id: fieldName,
                    name: fieldName,
                    type: typeof(data[0][fieldName]),
                    entity: $scope.currentEntity,
                    enabled: true
                });
            }

            setCurrentView();
            setViewData(sqlResults);
        }
    }

    function getData(){
        $scope.widget.report = {
            query: {
                entity: $scope.config.entity,
                fields: getQueryFields(),
                conditions: getQueryConditions(),
                paging: paging,
                sort: tableSort
            }
        };

        database.query($scope.widget.report.query).then(setViewData);
    }

    function setViewData(results){
        if (results)
            rawData = results;

        $scope.view.settings = $scope.currentViewSettings;

        var widgetDataParser = widgetsData[$scope.view.type];
        $scope.view.data = widgetDataParser ? widgetDataParser($scope.view, rawData.data, {}) : rawData.data;
        $scope.view.dataTotalResults = rawData.total;
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
        var currentViewSettingsDefinition = $scope.currentViewType.settings;
        var settings = {};

        angular.forEach(currentViewSettingsDefinition, function(settingDefinition){
            setSettingValues(settings, settingDefinition);
        });

        $scope.currentViewSettings = settings;
    }

    function getField(fieldId){
        for(var i= 0, field; field = $scope.currentEntity.fields[i]; i++){
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
            if (!~["entity", "params"].indexOf(paramName) && !filterParams[paramName]){
                filterParams[paramName] = {
                    operator: "equals",
                    field: paramName,
                    value: queryParams[paramName]
                }
            }
        }

        for(var fieldId in filterParams){
            field = getField(fieldId);
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
        if (!queryParams.fields)
            return;

        var fields = queryParams.fields.split(","),
            entityFieldsIndex = {};

        angular.forEach(fields, function(field){
            var fieldParts = field.split("."),
                entityId = fieldParts[0],
                fieldId = fieldParts[1],
                entityFields = entityFieldsIndex[entityId];

            if (!entityFields)
                entityFields = entityFieldsIndex[entityId] = [];

            entityFields.push(fieldId);
        });

        for(var entityId in entityFieldsIndex){
            // TODO: when using multiple entities, change this.
            if (entityId === $scope.currentEntity.id){
                angular.forEach($scope.currentEntity.fields, function(entityField){
                    entityField.enabled = !!~entityFieldsIndex[entityId].indexOf(entityField.id);
                });
            }
        }
    }

    function clearFilters(){
        angular.forEach($scope.currentEntity.fields, function(field){
            if (field.filters)
                field.filters = [];
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
            getEntityTableFields($scope.currentEntity, findSettingDefinition(viewType.settings, "fields"));
        }
        else if (viewType === widgetTypesData.pieChart){
            var firstNumericField,
                firstStringField;

            for(var i= 0, field; field = $scope.currentEntity.fields[i]; i++){
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

        $scope.view = {
            type: $scope.config.view.type,
            settings: $scope.currentViewSettings
        };
    }

    function init(){
        $q.all([ entities.getEntities(), widgetTypes.getWidgetTypes()]).then(function(results){
            var entitiesData = results[0];
            widgetTypesData = results[1];

            $scope.entities = entitiesData;

            if ($location.search().entity){
                $scope.config.entity = $location.search().entity;
                $scope.onEntitySelect();
                if ($location.search().sql){
                    $scope.sqlQuery = $location.search().sql;
                    $scope.querySql($location.search().sql);
                }

                getFiltersFromUrl();
                getFieldsFromUrl();

                $scope.viewTypes = widgetTypesData;

                setCurrentView(widgetTypesData[$location.search().viewType || "table"]);

                $scope.update();
            }
        });
    }

    init();
}]);