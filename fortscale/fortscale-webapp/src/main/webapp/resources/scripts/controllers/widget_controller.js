angular.module("Fortscale").controller("WidgetController", ["$scope", "$timeout", "$rootScope", "$q", "dashboards", "widgets", "reports", "transforms", "conditions", "controls",
    function($scope, $timeout, $rootScope, $q, dashboards, widgets, reports, transforms, conditions, controls){
        var eventDeregistrationFunctions = [],
            createdRefreshOnListeners;

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
                    $scope.runWidgetReport(true);
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
                $scope.runWidgetReport(true);
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
                recursiveParams = angular.extend(getRecursiveDashboardParams(scope.$parent), recursiveParams);

            return recursiveParams;
        }

        function setWidgetTitle(params){
            if ($scope.widget.title)
                $scope.widget.parsedTitle = widgets.parseFieldValue($scope.widget, $scope.widget.title, {}, 0, params || $scope.dashboardParams);
        }

        function setWidgetShow(){
            if ($scope.widget.show){
                var previousValue = !!$scope.widget.show.value;
                $scope.widget.show.value = conditions.validateConditions($scope.widget.show.conditions, {}, getWidgetParams($scope.widget));
                if ($scope.widget.show.value !== previousValue){
                    if ($scope.widget.show.value)
                        $scope.$broadcast("show");
                    else
                        $scope.$broadcast("hide");
                }
            }
        }

        function setViewShow(view){
            if (view.show){
                var previousValue = !!view.show.value;
                view.show.value = conditions.validateConditions(view.show.conditions, view.data, getWidgetParams($scope.widget));

                if (view.show.value !== previousValue){
                    if (view.show.value)
                        $scope.$broadcast("show");
                    else
                        $scope.$broadcast("hide");
                }
            }
        }

        function getWidgetParams(){
            var widgetParams = angular.extend({}, getRecursiveDashboardParams($scope), $scope.widget.params);

            if ($scope.widget.controls){
                angular.forEach($scope.widget.controls, function(control){
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
            e.stopPropagation();

            if (data.widget)
                data.params = getWidgetParams(data.widget);

            $scope.dashboardEvent(data);
        });

        $scope.initWidget = function(widget){
            if (!widget.widgetId)
                withWidgetData();
            else if (widget.widgetId){
                if (widget._ready)
                    withWidgetData();
                else{
                    widgets.getWidget(widget.widgetId).then(function(widgetConfig){
                        jQuery.extend(widget, widgetConfig);
                        widget._ready = true;
                        withWidgetData();
                    });
                }
            }
            else if (widget.reportId){
                reports.getReport(widget.reportId).then(function(report){
                    widget.report = jQuery.extend(true, { query: report }, widget.report);
                    widget._ready;
                    withWidgetData();
                })
            }
            function withWidgetData(){
                setWidgetTitle();

                if (widget.requiredParams){
                    widget.showInitMessage = !widgets.checkRequiredParams(widget, getRecursiveDashboardParams($scope));
                    if (!widget.showInitMessage)
                        $scope.runWidgetReport();
                }
                else
                    $scope.runWidgetReport();
            }
        };

        $scope.initControl = function(control){
            controls.initControl(control, getWidgetParams($scope.widget));
            if (control.refreshOn){
                eventDeregistrationFunctions.push($scope.$on("dashboardParamsChange", function(e, changedParams){
                    for(var i= 0, paramValue; i < control.refreshOn.length; i++){
                        if (changedParams[control.refreshOn[i]] !== undefined){
                            controls.initControl(control, getWidgetParams($scope.widget));
                            return;
                        }
                    }
                }));
            }
        };

        var runReportTimeoutPromise;

        $scope.runWidgetReport = function(forceRefresh){
            var widgetParams = getWidgetParams();

            if (eventDeregistrationFunctions.length){
                angular.forEach(eventDeregistrationFunctions, function(deregistrationFunction){
                    deregistrationFunction();
                });
            }

            if ($scope.widget.report)
                runReport(forceRefresh);
            else if ($scope.widget.reportId){
                reports.getReport($scope.widget.reportId).then(function(report){
                    $scope.widget.report = { query: report };
                    runReport(forceRefresh);
                }, function(error){
                    console.error("Can't get report with ID '%s' for widget.", $scope.widget.reportId, $scope.widget);
                });
            }
            else{
                angular.forEach($scope.widget.views, function(view){
                    view.data = widgets.setViewValues(view, [], widgetParams);
                });
            }

            if ($scope.widget.refreshOn){
                createdRefreshOnListeners = true;

                if (!angular.isArray($scope.widget.refreshOn))
                    $scope.widget.refreshOn = [$scope.widget.refreshOn];

                angular.forEach($scope.widget.refreshOn, function(param){
                    eventDeregistrationFunctions.push($scope.$on(param, function(value){
                        if (value)
                            $scope.runWidgetReport(true);
                    }));
                });

                if (typeof(window.currentDashboardListener) === "undefined")
                    window.currentDashboardListener = 1;
                else
                    window.currentDashboardListener++;

                eventDeregistrationFunctions.push($scope.$on("dashboardParamsChange", function(e, changedParams){
                    setWidgetTitle(changedParams);
                    angular.extend($scope.widget.params, changedParams);
                    setWidgetShow();
                    angular.forEach($scope.widget.views, setViewShow);

                    for(var i=0; i < $scope.widget.refreshOn.length; i++){
                        if (changedParams[$scope.widget.refreshOn[i]] !== undefined){
                            $scope.runWidgetReport(true);
                            return;
                        }
                    }
                }));
            }
            else{
                eventDeregistrationFunctions.push($scope.$on("dashboardParamsChange", function(e, changedParams){
                    setWidgetShow();
                    angular.forEach($scope.widget.views, setViewShow);
                }));
            }
        };

        function runReport(forceRefresh){
            $scope.widget.loading = true;
            $scope.widget.error = false;
            $scope.widget.warning = null;
            $scope.widget.noData = false;
            $scope.widget.showInitMessage = false;

            var widgetParams = getWidgetParams();
            reports.runReport($scope.widget.report, widgetParams, forceRefresh).then(function(results){
                if (!results.data || !results.data.length){
                    $scope.widget.noData = true;
                    $scope.widget.totalResults = 0;
                    angular.forEach($scope.widget.views, function(view){
                        view.data = null;
                    });
                }
                else{
                    $scope.widget.totalResults = results.total;

                    angular.forEach($scope.widget.views, function(view){
                        view.data = widgets.setViewValues(view, results.data, widgetParams, results.rawData);
                        view.rawData = results.data;
                    });
                }

                $scope.widget.loading = false;
                $scope.$broadcast("onWidgetData", { widget: $scope.widget });

                if ($scope.widget.onDataEvent){
                    $scope.dashboardEvent({
                        event: $scope.widget.onDataEvent,
                        data: results.data
                    });
                }

                if ($scope.widget.autoRefresh)
                    $timeout(function(){ $scope.runWidgetReport(); }, 15 * 60 * 1000); // Refresh the widget in 15 minutes

                $scope.widget.error = null;
            }, function(error){
                $scope.widget.error = "Error loading data";
                console.error("Error retrieving data for widget: ", error);
                $scope.widget.loading = false;
                $scope.widget.warning = null;
                $scope.widget.noData = false;
                angular.forEach($scope.widget.views, function(view){
                    view.data = null;
                });
            });
        }

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
        };

        var initWidgetTimeout;
        $scope.onControlParamsChange = function(control){
            if (control.updateDataOnChange){
                $timeout.cancel(initWidgetTimeout);
                initWidgetTimeout = $timeout(function(){
                    $scope.runWidgetReport(true);
                }, 100);
            }
        }
}]);
