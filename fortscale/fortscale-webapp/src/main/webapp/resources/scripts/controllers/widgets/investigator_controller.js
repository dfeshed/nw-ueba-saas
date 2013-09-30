angular.module("Fortscale").controller("InvestigatorController", ["$scope", "$location", "$timeout", "entities", "database", "widgets", "conditions", "widgetTypes",
    function($scope, $location, $timeout, entities, database, widgets, conditions, widgetTypes){
    var paging = {
            pageSize: 20,
            page: 1
        },
        tableSort;

    $scope.config = {};
    $scope.view = {
        "type": "table",
        "settings": {
            "allowPaging": true,
            "pageSize": paging.pageSize
        }
    };

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
            $scope.view.settings.fields = getEntityTableFields($scope.currentEntity);
            getData();
        }
    };

    $scope.fields = {
        objectArray: {
            addMember: function(field){
                var setting = $scope.config.view.settings[field.id];
                if (!setting)
                    setting = $scope.config.view.settings[field.id] = [];

                setting.push({});
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
    }

    function getEntityTableFields(entity){
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
                    fieldSetting.format = "date";
                    fieldSetting.formatOptions = {
                        format: "MM/DD/YY HH:mm"
                    }
                }

                if (/score/i.test(field.id)){
                    fieldSetting = angular.extend(fieldSetting, {
                        "style": "score",
                        "styleParams": {
                            "value": field.id
                        }
                    });
                }

                fields.push(fieldSetting)
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

        database.query($scope.widget.report.query).then(function(results){
            $scope.view.data = widgets.setViewValues($scope.view, results.data, {});
            $scope.view.dataTotalResults = results.total
        });
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

    function init(){
        entities.getEntities().then(function(entitiesData){
            $scope.entities = entitiesData;

            if ($location.search().entity){
                $scope.config.entity = $location.search().entity;
                $scope.onEntitySelect();

                getFiltersFromUrl();
                getFieldsFromUrl();
                $scope.update();
            }
        });

        widgetTypes.getWidgetTypes().then(function(widgetTypesData){
            $scope.viewTypes = widgetTypesData;
            $scope.currentViewType = widgetTypesData.table;

            $scope.config.view = { type: widgetTypesData.table.type, settings: {} };
        });
    }

    init();
}]);