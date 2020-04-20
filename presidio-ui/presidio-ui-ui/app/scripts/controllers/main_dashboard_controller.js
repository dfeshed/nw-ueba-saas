(function () {
    'use strict';

    function MainDashboardController($scope, $q, $routeParams, $location, $timeout, dashboards, utils, transforms,
                                     reportsProcess, reports, Report, Cache, Widget, widgets, tags, eventBus,
                                     conditions, state, events, DAL) {

        var locationChangeHandlerTimeout,
            nonUrlParams = {};

        $scope.$on("$destroy", function (e, data) {
            $timeout.cancel(locationChangeHandlerTimeout);
        });

        $scope.mainDashboardParams = {entityId: $routeParams.entityId, dashboardId: $routeParams.dashboardId};
        $scope.isMainDashboard = true;

        dashboards.getDashboardById($routeParams.dashboardId).then(function (dashboard) {
            if ($routeParams.subDashboardId) {
                dashboards.getDashboardById("nav_dashboard").then(function (navDashboard) {
                    $scope.dashboard = angular.extend(navDashboard, dashboard);
                    if ($scope.dashboard.params) {
                        angular.extend($scope.mainDashboardParams, $scope.dashboard.params);
                    }

                    $scope.$broadcast("onMainDashboard", {dashboard: dashboard});
                });
            }
            else {
                $scope.dashboard = dashboard;
                $scope.$broadcast("onMainDashboard", {dashboard: dashboard});
            }
        }, $scope.report.error);

        var disableLocationChangeListener;

        $scope.closePopup = function () {
            $scope.popupShow = false;
        };

        var dashboardEvents = {
            broadcast: function (options) {
                $scope.$broadcast(options.event, options.data);
            },
            clearCache: function (options) {
                Cache.clearAll();
            },
            closeAllPopups: function () {
                $scope.$broadcast("closePopups");
            },
            innerUrl: function (options, data, widgetParams) {
                var hash = "#" + utils.strings.parseValue(options.url,
                        data, widgetParams || $scope.mainDashboardParams);
                if (options.params) {
                    var params = {};
                    for (var paramName in options.params) {
                        if (options.params.hasOwnProperty(paramName)) {
                            params[paramName] = utils.strings.parseValue(options.params[paramName], data, widgetParams);
                        }
                    }
                    params = transforms.transformParams(params, options.paramsTransform);
                    hash += (/\?/.test(hash) ? "&" : "?") + "params=" + encodeURIComponent(JSON.stringify(params));
                }

                hash = hash.replace(/%3A/g, ":");
                window.location.hash = hash;
            },
            openPopup: function (options, data, params) {
                var popupWidgetId = options.widgetId || (options.widget && options.widget.widgetId);

                var popup = {
                    width: options.width,
                    height: options.height,
                    title: utils.strings.parseValue(options.title, data, params),
                    src: popupWidgetId ? "views/widgets/standalone_widget.html" : "views/pages/dashboard.html",
                    show: true,
                    position: options.position,
                    data: data,
                    controls: options.controls
                };

                function showPopup() {
                    if ($scope.popup) {
                        $scope.popup = null;
                    }

                    popup.show = true;
                    $scope.popup = popup;
                }


                if (popupWidgetId) {
                    DAL.widgets.getWidget(popupWidgetId).then(function (widgetConfig) {
                        /*
                         parsing of dynamic report name (name that depends on data source type)
                         is executed before creation of Widget, since the creation of Report is done within Widget
                         constructor.
                         */
                        if (options.widget && options.widget.reportId) {
                            widgetConfig.reportId = utils.strings.parseValue(options.widget.reportId, data, params);
                        }
                        //returns new Widget
                        return Widget.loadWidget(widgetConfig);

                    }).then(function (widgetTemplate) {
                        var widget = angular.copy(widgetTemplate);
                        var parsedParams;
                        if (widget.exploreBased && state.__explore__ && widget.report &&
                            widget.report.endpoint && widget.report.endpoint.dataQuery) {
                            var exploreDataQuery = state.__explore__.getFiltersDataQuery();
                            if (exploreDataQuery.conditions) {
                                widget.report.endpoint.dataQuery.conditions =
                                    exploreDataQuery.conditions.terms
                                        .concat(widget.report.endpoint.dataQuery.conditions.terms);
                            }
                            widget.report.endpoint.dataQuery.entity = state.__explore__.dataEntity.id;
                            if (widget.report.endpoint.dataQuery.entitiesJoin) {
                                widget.report.endpoint.dataQuery.entitiesJoin.joinFields.left =
                                    widget.report.endpoint.dataQuery.entity + "." +
                                    widget.report.endpoint.dataQuery.entitiesJoin.joinFields.left;
                            }
                        }

                        if (options.widget) {
                            angular.extend(widget, options.widget);
                        }

                        if (options.params) {
                            parsedParams = {};
                            var paramValue;

                            for (var paramName in options.params) {
                                if (options.params.hasOwnProperty(paramName)) {
                                    paramValue = options.params[paramName];
                                    parsedParams[utils.strings.parseValue(paramName, data, params)] =
                                        typeof(paramValue) === "string" ?
                                            utils.strings.parseValue(paramValue, data, params) : paramValue;
                                }
                            }
                        }

                        if (!widget.className) {
                            widget.className = "no-shadow";
                        }
                        if (!widget.flags) {
                            widget.flags = {noBorder: true};
                        }
                        else {
                            widget.flags.noBorder = true;
                        }

                        popup.scope = {
                            widget: widget,
                            params: angular.extend({}, params, parsedParams)
                        };

                        var popupParent = angular.copy($scope.dashboard);
                        popupParent = angular.extend(popupParent, {params: parsedParams});
                        widget.setParent(popupParent);
                        widget.refresh();

                        showPopup();
                    });
                }
                else {
                    showPopup();
                }


            },
            refreshAll: function (options) {
                widgets.refreshAll();
            },
            refreshTags: function (options) {
                $scope.dashboard.runReports();
                if ($scope.dashboard.details.tags) {
                    tags.getTags($scope.dashboard.details.tags, state.currentParams[options.dashboardParam] || {})
                        .then(function (dashboardTags) {
                            $scope.dashboard.details.parsedTags = dashboardTags;
                        });
                }
            },
            runReport: function (options, data, widgetParams) {
                var report;

                if (!options.report && !options.reportId) {
                    return;
                }

                if (options.report) {
                    report = options.report;
                } else if (options.reportId) {
                    reports.getReport(options.reportId).then(function (_report) {
                        report = _report;
                        doRunReport();
                    }, function (error) {
                        console.error("Can't find report with ID %s: ", options.reportId, error);
                    });
                }

                function doRunReport() {
                    report = utils.objects.extend(report, options.reportParams);
                    reports.runReport(new Report(report), utils.objects.extend(widgetParams, data), true)
                        .then(function (results) {
                            if (options.onResults) {
                                $scope.dashboardEvent(utils.objects.extend(options.onResults, {
                                    data: data,
                                    params: widgetParams
                                }));
                            }
                        }, function (error) {
                            console.error(options.onError && options.onError.alert || "Error running report: ",
                                error, ". Report: ", options.report);
                        });
                }
            },
            setParams: function (options, data) {
                var params = {},
                    paramValue,
                    paramData,
                    paramStrConfig,
                    useParam,
                    parsedParamName,
                    urlParams = {},
                    existingParamValue;

                function getParamValue(paramConfig) {
                    var paramValue;
                    if (paramConfig) {
                        paramValue = angular.isString(paramConfig.value || paramConfig) ?
                            utils.strings.parseValue(paramConfig.value || paramConfig, paramData || {},
                                $scope.mainDashboardParams, undefined, true) :
                        paramConfig.value || paramConfig;
                    } else if (paramConfig === null) {
                        paramValue = null;
                    } else if (angular.isObject(options.params[paramName]) && options.params[paramName].dataValue) {
                        paramValue = paramData[options.params[paramName].dataValue];
                    } else {
                        paramValue = paramConfig;
                    }

                    return paramValue;
                }

                function addUrlParams(paramName, paramConfig, paramValue) {
                    if (!urlParams[paramName]) {
                        urlParams[paramName] = [];
                    }

                    if (angular.isArray(paramConfig)) {
                        paramConfig.forEach(function (param) {
                            addUrlParams(paramName, param, getParamValue(param));
                        });
                    } else {
                        urlParams[paramName].push(paramConfig && paramConfig.setToUrl === false ? null : paramValue);
                    }
                }

                for (var paramName in options.params) {
                    if (options.params.hasOwnProperty(paramName)) {
                        parsedParamName = utils.strings.parseValue(paramName, data, $scope.mainDashboardParams);
                        paramStrConfig = options.params[paramName];
                        useParam = true;

                        if (angular.isArray(data) && angular.isObject(paramStrConfig)) {
                            paramData = data[paramStrConfig.itemIndex || 0];
                            if (paramStrConfig.setIf) {
                                if (!paramData[paramStrConfig.setIf]) {
                                    useParam = false;
                                }
                            } else {
                                paramStrConfig = paramStrConfig.value;
                            }
                        } else {
                            paramData = data;
                        }

                        if (!useParam) {
                            continue;
                        }

                        if (options.updateUrl === false) {
                            nonUrlParams[paramName] = true;
                        } else {
                            delete nonUrlParams[paramName];
                        }

                        paramValue = getParamValue(paramStrConfig);

                        if (parsedParamName === "filters") {
                            existingParamValue = state.currentParams.filters;
                        } else {
                            existingParamValue = $scope.mainDashboardParams[parsedParamName];
                        }

                        if (options.addToParam && existingParamValue) {
                            // If the paramValue already exists in the param, there's no need to add it:
                            if (typeof(existingParamValue) === "string" &&
                                ~existingParamValue.split(",").indexOf(paramValue)) {
                                continue;
                            }

                            paramValue = existingParamValue + "," + paramValue;
                        }

                        params[parsedParamName] = paramValue;

                        addUrlParams(parsedParamName, paramStrConfig, paramValue);
                    }
                }

                params = transforms.transformParams(params, options.paramsTransform);
                angular.extend($scope.mainDashboardParams, params);

                state.setParams(params, false);

                if (options.updateUrl !== false) {
                    setParamsToUrl(urlParams);
                    state.setParams(urlParams);
                }

                if (params && Object.keys(params).length) {
                    eventBus.triggerEvent("dashboardParamsChange", params);
                }


            },
            showTooltip: function (options, data, params) {
                var tooltipContents = {},
                    tooltipOptions = options;

                if (options.switch) {
                    tooltipOptions = options.switchCases[data[options.switch]];
                }

                if (tooltipOptions.table && tooltipOptions.table.rows) {
                    tooltipContents.table = {rows: []};

                    tooltipOptions.table.rows.forEach(function (row) {
                        tooltipContents.table.rows.push({
                            label: utils.strings.parseValue(row.label, data, params),
                            value: utils.strings.parseValue(row.value, data, params)
                        });
                    });
                }

                tooltipContents.text = tooltipOptions.text;

                $scope.tooltipContents = tooltipContents;
                $scope.tooltipOpen = true;
                $scope.tooltipPosition = options.position;
            }
        };

        $scope.$on("dashboardEvent", function (e, data) {
            $scope.dashboardEvent(data);
        });

        events.onDashboardEvent.subscribe(function (e, data) {
            $scope.dashboardEvent(data);
        });

        $scope.dashboardEvent = function (data) {
            if (data.event) {
                runEvent(data.event, data.data, data.params);
            }
            else if (data.events) {
                angular.forEach(data.events, function (event) {
                    runEvent(event, data.data, data.params);
                });
            }
        };

        // Shortcut for setParams dashboardEvent:
        $scope.setParams = function (params, updateUrl) {
            $scope.dashboardEvent({
                event: {
                    action: "setParams",
                    actionOptions: {
                        updateUrl: updateUrl,
                        params: params
                    }
                }
            });
        };

        $scope.innerUrl = function (url) {
            $scope.dashboardEvent({
                event: {
                    action: "innerUrl",
                    actionOptions: {
                        url: url
                    }
                }
            });
        };

        $scope.menuSelect = function ($event, item, data, menu, widgetParams) {
            var preSelectData;
            function doSelect() {
                var events = angular.copy(item.onSelect),
                    eventData = menu.params ? {} : data,
                    params = widgetParams;

                if (!angular.isArray(events)) {
                    events = [events];
                }

                if (menu.params) {
                    var param;
                    for (var paramName in menu.params) {
                        if (menu.params.hasOwnProperty(paramName)) {
                            param = menu.params[paramName];
                            if (typeof(param) === "string") {
                                eventData[paramName] = utils.strings.parseValue(menu.params[paramName], data, params);
                            } else if (angular.isObject(param) && param.dashboardParam) {
                                eventData[paramName] = params[param.dashboardParam];
                            }
                        }
                    }
                }

                events.forEach(function (event) {
                    if (!event.actionOptions) {
                        event.actionOptions = {};
                    }

                    event.actionOptions.position = {top: $event.clientY, left: $event.clientX};
                });

                $scope.dashboardEvent({
                    events: events,
                    data: angular.extend(eventData, preSelectData),
                    params: widgetParams
                });
            }

            if (item.onSelect) {
                if (item.preSelectReport && (conditions.validateConditions(item.preSelectReport.conditions,
                        data, menu.params) === true)) {
                    Report.loadReport({reportId: item.preSelectReport.reportId}).then(function (preSelectReport) {

                        // resolve params
                        var itemParams = item.params ? {} : data;
                        if (item.params) {
                            var param;
                            for (var parameterName in item.params) {
                                if (item.params.hasOwnProperty(parameterName)) {
                                    param = item.params[parameterName];
                                    if (typeof(param) === "string") {
                                        itemParams[parameterName] =
                                            utils.strings.parseValue(item.params[parameterName], data);
                                    }

                                    // This else-if has a problem. params is not defined. I can't seem to find the
                                    // use-case for this else-if, so its hard to debug or understand original purpose,
                                    // and for that reason, this code will be commented! :
                                    //else if (angular.isObject(param) && param.dashboardParam)
                                    //    itemParams[parameterName] = params[param.dashboardParam];
                                }
                            }
                        }

                        // send request for preSelectReport
                        reports.runReport(preSelectReport, angular.extend({}, widgetParams, itemParams, data))
                            .then(function (results) {
                                preSelectData = results.data.length && item.preSelectReport.singleResult ?
                                    results.data[0] : results;
                                doSelect();
                            });
                    }, function (error) {
                        console.error("Can't get preSelectReport: ", error);
                    });
                } else {
                    doSelect();
                }
            }

        };

        function runEvent(event, data, params) {
            if (event && event.action) {
                var action = dashboardEvents[event.action];
                if (action) {
                    if (event.process) {
                        $q.when(reportsProcess.processData(event.process.processId, data, event.process.params))
                            .then(function (processedData) {
                                action(event.actionOptions, processedData.data.length && processedData.data[0],
                                    params);
                            }, function (error) {
                                console.error("Can't process data for event: ", error);
                            });
                    }
                    else {
                        action(event.actionOptions, data, params);
                    }
                }
                else {
                    console.error("Action not found: ", event.action);
                }
            }
        }

        $scope.onControlParamsChange = function (control) {
            if (control.updateDataOnChange) {
                setControlParamsToDashboard(control);
                setParamsToUrl();
            }
        };

        function setControlParamsToDashboard(control, useExistingParamsIfAvailable) {
            if (control.params) {
                var transformedParams = transforms.transformParams(control.params, control.paramsTransform);

                for (var paramName in transformedParams) {
                    if (!useExistingParamsIfAvailable || $scope.mainDashboardParams[paramName] === undefined) {
                        $scope.mainDashboardParams[paramName] = transformedParams[paramName];
                    }
                }
            }
        }

        function setParamsToUrl(urlParams) {
            var paramsObj = urlParams || angular.copy($scope.mainDashboardParams),
                currentUrlParams = $location.search(),
                paramValue;

            delete paramsObj.entityId;

            for (var paramName in paramsObj) {
                if (paramsObj.hasOwnProperty(paramName)) {
                    paramValue = paramsObj[paramName];

                    if (angular.isArray(paramValue) && (!paramValue.length || (paramValue.length === 1 &&
                        paramValue[0] === null))) {
                        paramValue = null;
                    }

                    if (paramValue === null || (paramValue && paramValue.length > 200)) {
                        delete paramsObj[paramName];
                    }

                    // If there's a query param of the same name as the param, remove it.
                    if (currentUrlParams[paramName]) {
                        $location.search(paramName, null);
                    }

                    //$location.search(paramName, angular.isObject(paramValue) ?
                    // JSON.stringify(paramValue) : paramValue);
                    $location.search(paramName, paramValue);
                }
            }

            disableLocationChangeListener = true;
            locationChangeHandlerTimeout = $timeout(function () {
                disableLocationChangeListener = false;
            }, 15);
        }
    }


    MainDashboardController.$inject = [
        "$scope", "$q", "$routeParams",
        "$location", "$timeout", "dashboards",
        "utils", "transforms", "reportsProcess",
        "reports", "Report", "Cache", "Widget", "widgets", "tags",
        "eventBus", "conditions", "state", "events", "DAL"];

    angular.module("Fortscale")
        .controller("MainDashboardController", MainDashboardController);

})();
