angular.module("Fortscale").controller("EntityController", ["$scope", "$routeParams", "entities", "dashboards", "utils", "transforms", function($scope, $routeParams, entities, dashboards, utils, transforms){
    $scope.currentEntityType = $routeParams.entityType;

    $scope.setTabParams = function(params){
        $scope.tabParams = params ? "?" + utils.url.getQuery(params) : "";
    };

    function loadFeature(entity, featureId){
        var feature = entities.getEntityFeatureById(entity, featureId);
        $scope.selectedFeature = feature;

        if (feature && feature.dashboardId){
            dashboards.getDashboardById(feature.dashboardId).then(function(dashboard){
                $scope.$broadcast("onDashboard", { dashboard: dashboard });

                if ($routeParams.params && dashboard.controls){
                    var params = JSON.parse($routeParams.params),
                        paramTransform;

                    angular.forEach(dashboard.controls, function(control){
                        if (control.params){
                            for(var paramName in params){
                                if (control.params[paramName]){
                                    paramTransform = control.paramsInitTransform && control.paramsInitTransform[paramName];
                                    if (paramTransform){
                                        control.params[paramName] = transforms[paramTransform.type](params[paramName], paramTransform);
                                    }
                                    else
                                        control.params[paramName] = params[paramName];

                                }
                            }
                        }
                    });
                }

                $scope.dashboard = dashboard;

            }, $scope.report.error);
        }
    }

    entities.getEntity($routeParams.entityType).then(function(entity){
        $scope.currentEntity = entity;
        $scope.currentDashboardId = $routeParams.dashboardId;
        $scope.entityId = $routeParams.entityId;

        dashboards.getDashboardById($routeParams.dashboardId).then(function(dashboard){
            $scope.mainDashboard = dashboard;
        });

        if ($routeParams.featureId){
            loadFeature(entity, $routeParams.featureId);
        }
        else if ($routeParams.tabId){
            $scope.selectedFeature = null;
        }
        else if (entity.features.length){
            loadFeature(entity, entity.features[0].featureId);
        }
    }, $scope.report.error);
}]);