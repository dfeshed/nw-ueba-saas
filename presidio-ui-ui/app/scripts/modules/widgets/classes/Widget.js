(function () {
    'use strict';

    var widgetFlags = new Set(["alignControlsRight", "contentsOnly", "noBorder", "stretchVertically", "fullHeight"]);

    function WidgetClass ($q, $timeout, utils, widgetViews, WidgetView, WidgetButton, Report, conditions, state,
                          ControlList, EventBus) {

        function validateWidgetReport (config) {
            if (config.reportId && typeof(config.reportId) !== "string") {
                throw new TypeError("Can't create widget, reportId must be a string.");
            }

            if (config.reportId && config.report) {
                throw new Error("A widget cannot have both a 'reportId' and a 'report' properties.");
            }
        }

        /**
         Some widgets, e.g. users_dist, need additional data process after the report result gets back from server.
         in user_dist, we need to merge the results according to their label
         */
        function mergeResults (data) {
            var mergedData = {};
            for (var line of data) { // sum data according to label
                if (!mergedData[line.label]) {
                    mergedData[line.label] = 0;
                }
                mergedData[line.label] += line.login_count;
            }
            var newMergedData = [];
            for (var item in mergedData) {
                if (mergedData.hasOwnProperty(item)) {
                    newMergedData.push({"label": item, "login_count": mergedData[item]});
                }
            }
            return newMergedData;
        }

        /**
         * If the specified widget has a report and it has params, the dashboardParams from the report are added
         * to the widget's refreshOn map, so it refreshes when those params change.
         * @param widget
         */
        function setWidgetReportRefreshOn (widget) {
            if (!widget.report || !widget.report.params) {
                return;
            }

            if (!widget.refreshOn) {
                widget.refreshOn = {};
            }

            widget.report.params.forEach(function (reportParam) {
                widget.refreshOn[reportParam.dashboardParam] = true;
            });
        }

        /**
         * To avoid infinite recursion when working with widgets, a dashboard can't be set as a property of the widget
         * i holds
         * (It's possible, but Angular dies), just the properties required for the widget to work are set as the
         * parent.
         * @param parentObj
         * @returns {{getState: (Function|*|getState)}}
         */
        function getNonRecursiveParent (parentObj) {
            if (parentObj && parentObj.getState) {
                return {
                    getState: parentObj.getState.bind(parentObj),
                    isReady: parentObj.isReady ? parentObj.isReady.bind(parentObj) : function () {
                        return true;
                    }
                };
            }

            return null;
        }

        // String properties of widgets which are affected by state changes:
        var parsableProperties = ["params", "title", "noDataTitle", "loadingTitle", "description", "noDataMessage",
            "controlsTitle"];
        var STATE_CHANGE_EVENT = "stateChange";

        var allWidgets = {};

        function Widget (config, parent) {
            if (config instanceof Widget) {
                config = config._config;
            }

            var widget = this;

            this.validate(config);
            this._config = config;
            this.parent = getNonRecursiveParent(parent);
            this.refreshOn = {};

            this.id = config.id;
            this.config = {
                show: config.show
            };
            this.show = true;
            for (var parsableProperty of parsableProperties) {
                this.config[parsableProperty] = config[parsableProperty];
            }

            this.views = config.views.map(function (viewConfig) {
                return new WidgetView(viewConfig);
            });

            this.priority = config.priority || 0;
            this.hideOnNoData = !!config.hideOnNoData;
            this.hideOnError = !!config.hideOnError;

            if (config.forceRefresh) {
                this.forceRefresh = config.forceRefresh;
            }
            else {
                this.forceRefresh = false;
            }

            if (config.exploreBased) {
                this.exploreBased = config.exploreBased;
            }

            if (config.className) {
                this.className = config.className;
            }

            if (config.controls) {
                this.controlsList = new ControlList(config.controls);
            }

            this.setReport(config);

            this.flags = config.flags;
            this.loadingDataMessage = config.loadingDataMessage || "Loading data";

            if (config.height) {
                this.height = config.height;
            }

            if (config.buttons) {
                this.setWidgetButtons(config.buttons);
            }

            if (config.mergeResults) {
                this.mergeResults = config.mergeResults;
            }

            if (config.refreshOn) {
                var refreshOn = config.refreshOn.constructor === Array ? config.refreshOn : [config.refreshOn];
                refreshOn.forEach(function (param) {
                    widget.refreshOn[param] = true;
                });
            }

            this.update();
            this.getData();

            this._eventBus = EventBus.setToObject(this, [STATE_CHANGE_EVENT]);
        }

        /**
         * Creates a copy of the widget, by first creating another Widget with the same config, then setting the
         * current properties.
         * @param widget
         * @returns {WidgetClass.Widget}
         */
        Widget.copy = function (widget) {
            var newWidget = new Widget(widget._config);
            for (var p in widget) {
                if (widget.hasOwnProperty(p)) {

                    // Views are not copied from widget to widget, they're taken from configuration only.
                    if (p !== "views") {
                        newWidget[p] = widget[p];
                    }
                }
            }

            return newWidget;
        };

        /**
         * Returns a copy of this widget
         * @returns {WidgetClass.Widget}
         */
        Widget.prototype.clone = function () {
            return Widget.copy(this);
        };

        /**
         * Validates the configuration used to build the Widget. Throws error if invalid.
         * @param config
         */
        Widget.prototype.validate = function (config) {
            if (!config) {
                return;
            }

            if (config.id && typeof(config.id) !== "string") {
                throw new TypeError("Invalid ID for widget. Expected a string but got " + typeof(config.id));
            }

            validateWidgetReport(config);

            if (config.show) {
                if (!config.show.conditions) {
                    throw new Error("Can't create widget - 'show' exists without conditions.");
                }

                // TODO: Validate the conditions
            }

            if (config.priority && typeof(config.priority) !== "number") {
                throw new TypeError("Cant' create widget, priority must be a number.");
            }

            if (!config.views) {
                throw new Error("Can't create widget, missing the views array.");
            }

            if (config.views.constructor !== Array) {
                throw new TypeError("Invalid views property for widget, expected an array.");
            }

            for (var view of config.views) {
                if (!view.type) {
                    throw new Error("Can't create widget, view has no type.");
                }

                if (typeof(view.type) !== "string") {
                    throw new TypeError("Cant' create widget, view type must be a string.");
                }

                if (!widgetViews.viewExists(view.type)) {
                    throw new Error("Unknown view type, '" + view.type + "'.");
                }

                widgetViews.validateSettings(view.type, view.settings);
            }

            if (config.flags) {
                for (var flagName in config.flags) {
                    if (config.flags.hasOwnProperty(flagName)) {
                        if (!widgetFlags.has(flagName)) {
                            throw new Error("Unknown widget flag, '" + flagName + "'.");
                        }
                    }
                }
            }

            if (config.loadingDataMessage && typeof(config.loadingDataMessage) !== "string") {
                throw new TypeError("Invalid loadingDataMessage for widget, expected a string but got " +
                    config.loadingDataMessage);
            }

            if (config.height) {
                if (typeof(config.height) !== "number") {
                    throw new TypeError("Invalid height for widget, expected a number but got " +
                        typeof(config.height));
                }

                if (config.height < 0) {
                    throw new Error("Invalid height for widget, must be a positive number.");
                }
            }

            if (config.buttons && config.buttons.constructor !== Array) {
                throw new TypeError("Invalid buttons for Widget, expected an array but got " + config.buttons);
            }

            if (config.refreshOn) {
                if (config.refreshOn.constructor === Array) {
                    config.refreshOn.forEach(function (refreshOnItem) {
                        if (typeof(refreshOnItem) !== "string") {
                            throw new TypeError("Invalid refreshOn for widget, expected a string but got " +
                                refreshOnItem + ".");
                        }
                    });
                }
                else if (typeof(config.refreshOn) !== "string") {
                    throw new TypeError("Invalid refreshOn for widget, expected either an array or a string.");
                }
            }
        };

        /**
         * Called when the widget is no longer needed, to clear memory and event handlers.
         *
         */
        Widget.prototype.destroy = function () {
            this.parent = null;
            this.show = true;
            this.rawData = null;
            this.views.forEach(function (view) {
                view.destroy();
            });
            this.noData = false;
            this.error = null;
        };

        /**
         * Sets a report to the widget. Must be an object, containing either 'reportId' (string), Object report or a
         * Report instance.
         * @param reportConfig
         */
        Widget.prototype.setReport = function (reportConfig) {
            var widget = this;

            validateWidgetReport(reportConfig);

            if (reportConfig.reportId) {
                Report.loadReport(reportConfig).then(function (report) {
                    widget.report = report;
                    setWidgetReportRefreshOn(widget);
                });
            }
            else if (reportConfig.report) {
                if (reportConfig.report instanceof Report) {
                    this.report = reportConfig.report;
                } else {
                    this.report = new Report(utils.objects.copy(reportConfig.report));
                }

                setWidgetReportRefreshOn(widget);
            }

            return this;
        };

        /**
         * Sets the Widget's buttons and sets the Widget as the widgetButton' parent
         * @param buttons
         */
        Widget.prototype.setWidgetButtons = function (buttons) {
            this.buttons = buttons.map(function (buttonConfig) {
                return new WidgetButton(buttonConfig, this);
            }.bind(this));
        };

        /**
         * Re-runs the widget's data, without using ui cache
         * @returns {*}
         */
        Widget.prototype.refresh = function () {
            this.getData(true);
        };

        /**
         * Runs the widget's report and sets the data for the widget
         */
        Widget.prototype.getData = function (noCache) {

            if (this.forceRefresh) {
                state.refresh();
                return;
            }
            if (!this.report || !this.parent || this.parent.isReady() === false) {
                return;
            }

            if (this._widgetReportTimeout) {
                $timeout.cancel(this._widgetReportTimeout);
            }

            var widget = this;

            widget.isLoading = true;
            if (widget.loadingTitle) {
                widget.title = widget.loadingTitle;
            }

            this._widgetReportTimeout = $timeout(function () {
                // We need to run the report in case we plan to show the widget,
                // or in case we show it only if it has data
                if (widget.show || widget.hideOnNoData) {
                    widget.report.run(widget.getState(), widget.priority, noCache).then(function (results) {
                        widget.noData = results.data && !results.data.length;
                        var widgetState = widget.getState();
                        widget.rawData = results.data;
                        widget.total = results.total;
                        if (widget.mergeResults) {
                            widget.rawData = mergeResults(results.data);
                        }
                        widget.views.forEach(function (view) {
                            view.setData(widget.rawData, widgetState);
                        });
                        // Add a warning message to the widget if the number of results exceeds the maximum that can be
                        // displayed
                        if (widget._config.message && widget._config.message.params &&
                            widget._config.message.params.maxResults) {
                            var maxResults = widget._config.message.params.maxResults;
                            if (widget.total >= maxResults) {
                                widget.message = " Too many results, displaying the first " + maxResults + ".";
                            }
                            else if (widget.message) {
                                delete widget.message;
                            }
                        }
                    }, function (error) {
                        widget.error = "Error loading data";
                        console.error("Error getting data for widget: ", error);
                    }).finally(function () {
                        widget.isLoading = false;
                        widget.update();

                    });
                }
            }, 40);
        };

        /**
         * Re-runs the widget's data, without using ui cache and also force server not using cache
         * @returns {*}
         */
        Widget.prototype.manualRefresh = function () {
            if (this.report) {
                this.report.options = utils.objects.extend({}, this.report.options, {useCache: true});
            }
            return this.refresh();
        };

        /**
         * Gets an object containing the params for this widget, including parent's state and app state
         * @returns object
         */
        Widget.prototype.getState = function () {
            var parentState = this.parent && this.parent.getState && this.parent.getState();

            return utils.objects.extend({}, state.currentParams, parentState, this.params,
                this.controlsList && this.controlsList.getParams());
        };

        Widget.prototype.setState = function (params) {

            if (!this.params) {
                this.params = {};
            }

            var prevParams = utils.objects.copy(this.params);
            utils.objects.extend(this.params, params);
            if (!utils.objects.areEqual(prevParams, this.params)) {
                utils.objects.extend(this.params, params);
                state.setParams(params, true, false);
                this._eventBus.triggerEvent(STATE_CHANGE_EVENT, {params: params});
                this.refreshIfRequired(params);
            }
        };

        /**
         * Updates any parsable properties according to the current widget and application state
         * Also updates views
         */
        Widget.prototype.update = function () {

            function getParsedValue (parsableProperty) {
                var propertyConfig = widget.config[parsableProperty];

                if (!propertyConfig) {
                    return undefined;
                }

                if (Object(propertyConfig) === propertyConfig) {
                    var parsedValue = {};
                    for (var p in propertyConfig) {
                        if (propertyConfig.hasOwnProperty(p)) {
                            parsedValue[p] = parseStringValue(propertyConfig[p]);
                        }
                    }
                    return parsedValue;
                }
                else {
                    return parseStringValue(propertyConfig);
                }
            }

            function parseStringValue (value) {
                var widgetData = angular.extend({}, widget.params,
                    {total: widget.total, resultsCount: widget.rawData && widget.rawData.length});
                return utils.strings.parseValue(value, widgetData, widgetState);
            }

            var widgetState = this.getState(),
                widget = this;

            for (var parsableProperty of parsableProperties) {
                this[parsableProperty] = getParsedValue(parsableProperty);
            }

            if (this.noDataTitle && (this.noData || this.error)) {
                this.title = this.noDataTitle;
            }

            if (!this.noDataMessage) {
                this.noDataMessage = "No data to display";
            }

            if (widget.noData && widget.hideOnNoData) {
                widget.show = false;
            } else if (!(this.hideOnNoData && this.noData) && this.config.show) {
                this.show = conditions.validateConditions(this.config.show.conditions, {}, widgetState);
            } else {
                this.show = true;
            }

            if (this.rawData) {
                for (var view of this.views) {
                    view.setData(this.rawData, widgetState);
                }
            }

        };

        /**
         * Sets the parent object of the Widget, avoids infinite recursion
         * @param parent
         */
        Widget.prototype.setParent = function (parent) {
            this.parent = getNonRecursiveParent(parent);
        };

        Widget.loadWidget = function (config) {

            function loadWidgetReport (widgetConfig) {
                if (widgetConfig.reportId) {
                    return Report.loadReport(utils.objects.extend({}, widgetConfig.report,
                        {reportId: widgetConfig.reportId})).then(function (report) {
                        widgetConfig.report = report;
                        delete widgetConfig.reportId;
                        return widgetConfig;
                    });
                }

                return $q.when(widgetConfig);
            }

            if (config instanceof Widget) {
                return config.clone();
            }

            if (config.widgetId) {
                var widgetId = createWidgetId(config);
                if (!config.noCache) {
                    var existingWidget = allWidgets[widgetId];
                    if (existingWidget) {
                        return $q.when(existingWidget);
                    }
                }
                return utils.http.wrappedHttpGet("data/widgets/" + config.widgetId.replace(/\./g, "/") +
                    ".json").then(function (widgetConfig) {
                    var fullWidgetConfig = utils.objects.extend({}, widgetConfig, config),
                        widgetId = createWidgetId(fullWidgetConfig);

                    delete fullWidgetConfig.widgetId;

                    return loadWidgetReport(fullWidgetConfig).then(function (fullWidgetConfig) {
                        var widget = new Widget(fullWidgetConfig);
                        allWidgets[widgetId] = widget;
                        return widget;
                    });
                }, function (error) {
                    var errorMessage = error.status === 404 ? "Widget '" + widgetId + "' not found." :
                    "Can't get widget '" + widgetId + "'. Error: " + error.data;
                    return $q.reject(errorMessage);
                });
            }

            if (config.reportId) {
                return loadWidgetReport(config).then(function (widgetConfig) {
                    return new Widget(widgetConfig);
                });
            }

            return new Widget(config);

        };

        /**
         * This function get widgetr id and create an extend of widgetid if needed using the specificId configuration -
         * for example in graph case
         * @param config
         */
        function createWidgetId (config) {

            //if specific id is define at the configuration the widget id will be - widgetid+"_"+specificId
            return config.widgetId + (config.specificId ? "_" + config.specificId : "");

        }

        /**
         * Runs when the app's state changes. If the widget should be updated according to the refreshOn map, it is
         * refreshed.
         * @param params
         */
        Widget.prototype.refreshIfRequired = function (params) {
            for (var changedParam in params) {
                if (params.hasOwnProperty(changedParam)) {
                    if (this.refreshOn[changedParam]) {
                        this.refresh();
                        return this;
                    }
                }
            }

            return this;
        };

        return Widget;

    }

    WidgetClass.$inject =
        ["$q", "$timeout", "utils", "widgetViews", "WidgetView", "WidgetButton", "Report", "conditions", "state",
            "ControlList", "EventBus"];

    angular.module("Widgets").factory("Widget", WidgetClass);
})();
