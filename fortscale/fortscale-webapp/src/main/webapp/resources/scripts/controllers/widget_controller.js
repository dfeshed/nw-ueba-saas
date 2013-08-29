angular.module("Fortscale").controller("WidgetController", ["$scope", "$timeout", "$rootScope", "$q", "dashboards", "widgets", "reports", "transforms", "conditions",
    function($scope, $timeout, $rootScope, $q, dashboards, widgets, reports, transforms, conditions){
        var eventDeregistrationFunctions = [];

        $scope.widget.params = $scope.widget.params || {};
        setWidgetTitle();
        setWidgetShow();

        var eventActions = {
            closePopup: function(){
                $scope.closePopup();
            },
            openUrl: function(options, event, data){
                var deferred = $q.defer();
                var url = options.url,
                    target = options.target || "_blank";

                if (/^#/.test(url))
                    url = document.location.href.match(/^([^#]+)/)[1] + url;

                url = widgets.parseFieldValue({}, url, data, 0, getWidgetParams($scope.widget));
                if (target === "_blank")
                    window.open(url);
                else
                    document.location.href = url;

                deferred.resolve(url);
                return deferred.promise;
            },
            refreshWidget: function(options, event, data){
                $scope.safeApply(function(){
                    $scope.runWidgetReport($scope.widget, true);
                });
            },
            runReport: function(options, event, data){
                var deferred = $q.defer();

                reports.runReport(options.report, data, true).then(function(results){
                    deferred.resolve(results);
                }, deferred.reject);

                return deferred.promise;
            },
            setGlobalParam: function(options, event, data){
                $scope.safeApply(function(){
                    $rootScope[options.param] = widgets.parseFieldValue({}, options.value, data, 0, $scope.dashboardParams);
                });
            },
            showPopup: function(options, event, data){
                var popup = angular.copy(options);
                popup.position = {
                    left: event.clientX,
                    top: event.clientY
                };

                popup.data = data;

                popup.popupTitle = widgets.parseFieldValue({}, options.popupTitle, data);
                dashboards.getDashboardById(options.dashboardId).then(function(dashboard){
                    var dashboardParams = {};

                    for(var paramName in options.dashboardParams){
                        dashboardParams[paramName] = widgets.parseFieldValue({}, options.dashboardParams[paramName], data);
                    }

                    popup.popupScope = {
                        dashboard: dashboard,
                        dashboardParams: dashboardParams
                    };

                    popup.src = "views/pages/dashboard.html";
                }, function(error){
                    $scope.report.error("Can't load popup");
                    console.error("Failed to load popup dashboard: ", error);
                });

                $scope.showPopup(popup);
            }
        };

        var widgetSettings = {
            refresh: function(widget){
                $scope.runWidgetReport(widget, true);
            }
        };

        $scope.initView = function(view){
            setViewShow(view);
        };

        function getRecursiveDashboardParams(scope){
            var recursiveParams = angular.copy(scope.dashboardParams) || {};
            for(var paramName in recursiveParams){
                if (recursiveParams[paramName] === null)
                    delete recursiveParams[paramName];
            }

            if (scope.$parent)
                angular.extend(recursiveParams, getRecursiveDashboardParams(scope.$parent));

            return recursiveParams;
        }

        function setWidgetTitle(){
            if ($scope.widget.title)
                $scope.widget.parsedTitle = widgets.parseFieldValue($scope.widget, $scope.widget.title, {}, 0, $scope.dashboardParams);
        }

        function setWidgetShow(){
            if ($scope.widget.show){
                $scope.widget.show.value = conditions.validateConditions($scope.widget.show.conditions, {}, getWidgetParams($scope.widget));
            }
        }

        function setViewShow(view){
            if (view.show){
                view.show.value = conditions.validateConditions(view.show.conditions, view.data, getWidgetParams($scope.widget));
            }
        }

        function getWidgetParams(widget){
            var widgetParams = angular.extend({}, getRecursiveDashboardParams($scope), widget.params);

            if (widget.controls){
                angular.forEach(widget.controls, function(control){
                    angular.extend(widgetParams, transforms.transformParams(control.params, control.paramsTransform));
                });
            }

            return widgetParams;
        }

        $scope.getWidgetParams = getWidgetParams;

        $scope.settings = [
            { name: "refresh", display: "Refresh", icon: "refresh" },
            { name: "edit", display: "Edit widget", icon: "pencil", disabled: true },
            { name: "remove", display: "Remove widget", icon: "remove", disabled: true }
        ];

        $scope.callSetting = function(setting, widget){
            var settingMethod = widgetSettings[setting.name];
            if (settingMethod)
                settingMethod(widget);

            widget.showSettings = false;
        };

        $scope.$on("widgetEvent", function(e, data){
            if (data.widget)
                data.params = getWidgetParams(data.widget);

            $scope.$emit("dashboardEvent", data);
        });

        $scope.initWidget = function(widget){
            if (widget.requiredParams){
                widget.showInitMessage = !widgets.checkRequiredParams(widget, getRecursiveDashboardParams($scope));
                if (!widget.showInitMessage)
                    $scope.runWidgetReport(widget);
            }
            else
                $scope.runWidgetReport(widget);

        };

        $scope.runWidgetReport = function(widget, forceRefresh){
            var widgetParams = getWidgetParams(widget);
            if (eventDeregistrationFunctions.length){
                angular.forEach(eventDeregistrationFunctions, function(deregistrationFunction){
                    deregistrationFunction();
                });
            }

            if (widget.report){
                widget.loading = true;
                widget.error = false;
                widget.warning = null;
                widget.noData = false;
                widget.showInitMessage = false;

                reports.runReport(widget.report, widgetParams, forceRefresh).then(function(results){
                    if (!results.data || !results.data.length){
                        widget.noData = true;
                        widget.totalResults = 0;
                        angular.forEach(widget.views, function(view){
                            view.data = null;
                        });
                    }
                    else{
                        widget.totalResults = results.totalResults;

                        angular.forEach(widget.views, function(view){
                            view.data = widgets.setViewValues(view, results.data, widgetParams, results.rawData);
                            view.rawData = results.data;
                        });
                    }

                    widget.loading = false;
                    $scope.$broadcast("onWidgetData", { widget: widget });

                    if (widget.autoRefresh)
                        $timeout(function(){ $scope.runWidgetReport(widget); }, 15 * 60 * 1000); // Refresh the widget in 15 minutes

                    widget.error = null;
                }, function(error){
                    widget.error = "Error retrieving data.";
                    console.error("Error retrieving data for widget: ", error);
                    widget.loading = false;
                    widget.warning = null;
                    widget.noData = false;
                    angular.forEach(widget.views, function(view){
                        view.data = null;
                    });
                    $timeout(function(){ $scope.runWidgetReport(widget); }, 1 * 60 * 1000); // Refresh the widget in 1 minute
                });
            }
            else{
                angular.forEach(widget.views, function(view){
                    view.data = widgets.setViewValues(view, [], widgetParams);
                });
            }

            if (widget.refreshOn){
                angular.forEach(widget.refreshOn, function(param){
                    eventDeregistrationFunctions.push($scope.$on(param, function(value){
                        if (value)
                            $scope.runWidgetReport(widget, true);
                    }));
                });

                eventDeregistrationFunctions.push($scope.$on("dashboardParamsChange", function(e, changedParams){
                    setWidgetTitle();
                    setWidgetShow();

                    angular.forEach(widget.views, setViewShow);
                    for(var i=0; i < widget.refreshOn.length; i++){
                        if (changedParams[widget.refreshOn[i]] !== undefined){
                            $scope.runWidgetReport(widget, true);
                            return;
                        }
                    }
                }));
            }
        };

        $scope.fireEvent = function(event, rawEvent, data, field){
            var action = eventActions[event.action];
            if (action){
                var deferredPromise = action(event.actionOptions, rawEvent, data);
                if (deferredPromise && event.afterAction){
                    deferredPromise.then(function(result){
                        if (event.afterAction.success)
                            $scope.fireEvent(event.afterAction.success, result, data, field);
                    }, function(error){
                        if (event.afterAction.error)
                            $scope.fireEvent(event.afterAction.error, error, data, field);
                    });
                }
            }
            else{
                console.error("Action no found: ", event.action);
            }
        };

        var initWidgetTimeout;
        $scope.onControlParamsChange = function(control){
            if (control.updateDataOnChange){
                $timeout.cancel(initWidgetTimeout);
                initWidgetTimeout = $timeout(function(){
                    $scope.runWidgetReport($scope.widget, true);
                }, 100);
            }
        }
}]);