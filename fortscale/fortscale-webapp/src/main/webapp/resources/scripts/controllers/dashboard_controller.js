angular.module("Fortscale").controller("DashboardController", ["$scope", "$routeParams", "$timeout", "$location", "transforms", "widgets", "utils",
    function($scope, $routeParams, $timeout, $location, transforms, widgets, utils){
        if (!$scope.dashboardParams)
            $scope.dashboardParams = $scope.dashboard && $scope.dashboard.params || {};

        if (!$scope.dashboardParamsOptions)
            $scope.dashboardParamsOptions = {};

        if ($routeParams.params){
            $scope.dashboardParams = JSON.parse($routeParams.params);
            $scope.setTabParams && $scope.setTabParams({ params: $scope.dashboardParams });
        }

        setDashboardFieldValues($scope.dashboard);
        setDashboardParamsToControls();

        $scope.dashboardSubtitle = null;
        $scope.dashboardIconUrl = null;

        function updateControlParams(){
            var paramsObj = { params: angular.copy($scope.dashboardParams) };
            delete paramsObj.params.entityId;

            for(var paramName in $scope.dashboardParamsOptions){
                if ($scope.dashboardParamsOptions[paramName] && $scope.dashboardParamsOptions[paramName].persist === false && paramsObj.params[paramName])
                    delete paramsObj.params[paramName];
            }

            $location.search('params', JSON.stringify(paramsObj.params));
            //utils.url.setHashQuery(paramsObj);
        }

        function setDashboardFieldValues(dashboard){
            if (dashboard){
                if (dashboard.name){
                    var dashboardTitle = widgets.parseFieldValue({}, dashboard.name, {}, 0, $scope.dashboardParams);
                    if (dashboardTitle){
                        dashboard.title = dashboardTitle;
                        $scope.setPageTitle(dashboardTitle);
                    }
                }
                
                if (dashboard.subtitle){
                    var dashboardSubtitle = widgets.parseFieldValue({}, dashboard.subtitle, {}, 0, $scope.dashboardParams);
                    $scope.dashboardSubtitle = dashboardSubtitle || null;
                }

                if (dashboard.iconUrl){
                    var dashboardIconUrl = widgets.parseFieldValue({}, dashboard.iconUrl, {}, 0, $scope.dashboardParams);
                    $scope.dashboardIconUrl = dashboardIconUrl || null;
                }
            }
        }

        function setDashboardParamsToControls(){
            var dashboardParams = $scope.dashboardParams;

            function setToControl(control){
                if (control.params){
                    for(var paramName in control.params){
                        if (dashboardParams[paramName] !== undefined)
                            control.params[paramName] = dashboardParams[paramName];
                    }
                }
            }

            if ($scope.dashboard){
                if ($scope.dashboard.controls)
                    angular.forEach($scope.dashboard.controls, setToControl);

                angular.forEach($scope.dashboard.widgets, function(widget){
                    if (widget.controls){
                        angular.forEach(widget.controls, setToControl);
                    }

                    angular.forEach(widget.views, function(view){
                        if (view.controls)
                            angular.forEach(view.controls, setToControl);
                    });
                });
            }
        }

        var dashboardEvents = {
            innerUrl: function(options, data, widgetParams){
                var hash = "#" + utils.strings.parseValue(options.url, data, widgetParams || $scope.dashboardParams);
                if (options.params){
                    var params = {};
                    for(var paramName in options.params){
                        params[paramName] = widgets.parseFieldValue(options, options.params[paramName], data, undefined, widgetParams);
                    }
                    params = transforms.transformParams(params, options.paramsTransform);
                    hash += "?params=" + encodeURIComponent(JSON.stringify(params));
                }
                window.location.hash = hash;
            },
            removeParams: function(options, data){
                var changedParams = {};
                angular.forEach(options.params, function(paramName){
                    changedParams[paramName]  = $scope.dashboardParams[paramName] = null;
                    $scope.dashboardParamsOptions[paramName] = null;
                });

                $scope.$broadcast("dashboardParamsChange", changedParams);
            },
            setParams: function(options, data){
                var params = {},
                    paramValue,
                    paramData,
                    paramStrConfig,
                    useParam;

                for(var paramName in options.params){
                    paramStrConfig = options.params[paramName];
                    useParam = true;

                    if (angular.isArray(data) && angular.isObject(paramStrConfig)){
                        paramData = data[paramStrConfig.itemIndex || 0];
                        if (paramStrConfig.setIf){
                            if (!paramData[paramStrConfig.setIf])
                                useParam = false;
                        }
                        else
                            paramStrConfig = paramStrConfig.value;
                    }
                    else
                        paramData = data;

                    if (!useParam)
                        continue;

                    if (paramStrConfig)
                        paramValue = utils.strings.parseValue(paramStrConfig.value || paramStrConfig, paramData || {}, $scope.dashboardParams);
                    else if (paramStrConfig === null)
                        paramValue = null;

                    params[paramName] = paramValue;
                }

                params = transforms.transformParams(params, options.paramsTransform);
                angular.extend($scope.dashboardParams, params);
                angular.extend($scope.dashboardParamsOptions, options.paramsOptions);
                $scope.$broadcast("dashboardParamsChange", params);
                setDashboardFieldValues($scope.dashboard);

                if (options.updateUrl !== false)
                    updateControlParams();
            }
        };

        $scope.dashboardParams.entityId = $routeParams.entityId;

        $scope.$on("dashboardEvent", function(e, data){
            $scope.dashboardEvent(data);
        });

        function getTopDashboardEvent(){
            var topDashboardEvent = $scope.callDashboardEvent,
                scope = $scope.$parent;

            while(scope){
                if (scope && scope.dashboardEvent)
                    topDashboardEvent = scope.callDashboardEvent;

                scope = scope.$parent;
            }
            return topDashboardEvent;
        }

        $scope.callDashboardEvent = function(data){
            if (data.event && data.event.action){
                var event = dashboardEvents[data.event.action];
                if (event){
                    event(data.event.actionOptions, data.data, data.params);
                }
                else
                    console.error("Dashboard event not found: ", data.event.action);
            }
        }

        $scope.dashboardEvent = getTopDashboardEvent();

        function setControlParamsToDashboard(control, useExistingParamsIfAvailable){
            if (control.params){
                var transformedParams = transforms.transformParams(control.params, control.paramsTransform);

                for (var paramName in transformedParams){
                    if (!useExistingParamsIfAvailable || $scope.dashboardParams[paramName] === undefined)
                        $scope.dashboardParams[paramName] = transformedParams[paramName];
                }
            }
        }
        $scope.onControlParamsChange = function(control){
            if (control.updateDataOnChange){
                setControlParamsToDashboard(control);
                updateControlParams();
            }
        };

        function onDashboard(dashboard){
            if (dashboard.controls){
                angular.forEach(dashboard.controls, function(control){
                    setControlParamsToDashboard(control, true);
                });
            }
            setDashboardFieldValues(dashboard);
        }

        $scope.$on("onDashboard", function(e, data){
            onDashboard(data.dashboard);
        });

        $scope.$on("onMainDashboard", function(e, data){
            onDashboard(data.dashboard);
        });

        if ($scope.dashboard)
            onDashboard($scope.dashboard);
    }]);