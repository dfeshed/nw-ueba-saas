angular.module("Fortscale").controller("InvestigatorController", ["$scope", "$location", "$timeout", "entities", "database", "widgets", function($scope, $location, $timeout, entities, database, widgets){
    $scope.config = {};
    $scope.view = {
        "type": "table",
        "settings": {}
    };

    $scope.onEntitySelect = function(){
        var foundEntity;

        for(var i= 0, entity; (entity = $scope.entities[i]) && !foundEntity; i++){
            if (entity.id === $scope.config.entity)
                foundEntity = entity;
        }

        if (foundEntity){
            $scope.currentEntity = foundEntity;
            angular.forEach(foundEntity.fields, function(field){
                field.enabled = true;
            });

            $scope.update();
        }
    };

    $scope.fieldTypeIcons = {
        "string": "font",
        "number": "plus",
        "date": "calendar"
    };

    var updateTimeoutPromise;
    $scope.update = function(){
        $timeout.cancel(updateTimeoutPromise);
        updateTimeoutPromise = $timeout(function(){
            $scope.view.settings.fields = getEntityTableFields($scope.currentEntity);
            getData();
        }, 500);
    };

    function getEntityTableFields(entity){
        var fields = [];
        angular.forEach(entity.fields, function(field){
            if (field.enabled){
                var fieldSetting = {
                    name: field.name,
                    sortBy: field.id,
                    value: "{{" + field.id + "}}"
                };

                fields.push(fieldSetting)
            }
        });

        return fields;
    }

    function getQueryFields(){
        var fields = [];

        angular.forEach($scope.currentEntity.fields, function(field){
            if (field.enabled)
                fields.push(field.id);
        });

        return fields;
    }

    function getData(){
        database.query({
            entity: $scope.config.entity,
            fields: getQueryFields()
        }).then(function(results){
            $scope.view.data = widgets.setViewValues($scope.view, results.data, {});
        });
    }

    function init(){
        entities.getEntities().then(function(entitiesData){
            $scope.entities = entitiesData;

            if ($location.search().entity){
                $scope.config.entity = $location.search().entity;
                $scope.onEntitySelect();
            }
        });
    }

    init();
}]);