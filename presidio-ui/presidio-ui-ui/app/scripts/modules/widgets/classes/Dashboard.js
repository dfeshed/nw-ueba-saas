(function () {
    'use strict';

    function DashboardClass ($q, Widget, WidgetButton, DashboardLayout, utils, conditions, Report, reports, state,
        ControlList) {
        // String properties of widgets which are affected by state changes:
        var parsableProperties = ["params", "title", "description", "browserTitle"];

        function Dashboard (config) {
            function checkConditions (navItem) {
                if (navItem.children) {
                    navItem.children = navItem.children.filter(checkConditions);
                }
                return !(navItem.conditions &&
                !conditions.validateConditions(navItem.conditions, null, state.currentParams));
            }

            this.validate(config);
            this._config = config;

            this.id = config.id || config.dashboardId;
            this.config = {};

            for (var parsableProperty of parsableProperties) {
                this.config[parsableProperty] = config[parsableProperty];
            }

            if (config.controls) {
                this.controlsList = new ControlList(config.controls);
            }

            if (config.navigation) {
                // TODO: Create a DashboardNavigationClass
                this.navigation = config.navigation;
                if (this.navigation.children) {
                    this.navigation.children = this.navigation.children.filter(checkConditions);
                }
            }

            if (config.className) {
                this.className = config.className;
            }

            // The config.details have to be validated and maybe have a class of their own:
            if (config.details) {
                this.details = config.details;
            }

            // TODO: Validate and create a class for DashboardMessage
            if (config.messages) {
                this.messages = config.messages;
            }

            if (config.requiredParams) {
                this.requiredParams = config.requiredParams;
                this.checkReady();
            }
            else {
                this.ready = true;
            }

            if (config.search) {
                this.search = config.search;
            }

            this.update();

            if (config.reports) {
                this.ready = false;

                this.reports = config.reports.map(function (report) {
                    return new DashboardReport(report);
                });
            }

            if (config.buttons) {
                this.setDashboardButtons(config.buttons);
            }

            if (config.widgets) {
                this.setWidgets(config.widgets);
            }

            if (config.columns) {
                this.setColumns(config.columns);
            }

            if (config.rows) {
                this.setRows(config.rows);
            }

            this.runReports();
        }

        Dashboard.prototype.validate = function (config) {
            /*
             if (!config.widgets && !config.columns && !config.rows)
             throw new Error("Can't create Dashboard, missing one of the following: widgets, columns or rows.");
             */
            if (config.name && typeof(config.name) !== "string") {
                throw new TypeError("Invalid name for Dashboard, expected a string but got " +
                    config.name.constructor.name + ".");
            }

            if (config.reports) {
                if (config.reports.constructor !== Array) {
                    throw new TypeError("Invalid reports for Dashboard, expected an array but got " +
                        config.reports.constructor.name + ".");
                }

                config.reports.forEach(function (reportConfig) {
                    if (!(reportConfig.report instanceof Report)) {
                        throw new TypeError("Can't create dashboard, report is not an instance of Report: " +
                            reportConfig.report + ".");
                    }
                });
            }

            if (config.requiredParams) {
                if (config.requiredParams.constructor !== Array) {
                    throw new TypeError("Invalid requiredParams for Dashboard, expected an Array but got " +
                        config.requiredParams.constructor.name + ".");
                }

                config.requiredParams.forEach(function (param) {
                    if (typeof(param) !== "string") {
                        throw new TypeError("Invalid param name. Expected a string but got " + param + ".");
                    }
                });
            }
        };

        /**
         * Check if all the dashboard's required params exist, then set the 'ready' property to true/false.
         * @returns {boolean}
         */
        Dashboard.prototype.checkReady = function () {
            if (!this.requiredParams) {
                this.ready = true;
                return true;
            }

            var currentState = this.getState();
            for (var param of this.requiredParams) {
                if (!currentState[param]) {
                    this.ready = false;
                    return false;
                }
            }
            this.ready = true;
            return true;
        };

        /**
         * Returns true if all the dashboard's required params are available, false if not.
         * @returns {boolean|*}
         */
        Dashboard.prototype.isReady = function () {
            return this.ready;
        };

        /**
         * If the dashboard has reports, run them, set the returned data to the dashboard's state, then refresh any
         * widgets and buttons that need to be refreshed
         * @returns {Promise}
         */
        Dashboard.prototype.runReports = function () {
            if (!this.reports) {
                return $q.when();
            }

            var dashboard = this,
                dashboardState = this.getState();

            var reportPromises = this.reports.map(function (dashboardReport) {
                return reports.runReport(dashboardReport.report, dashboardState, false).then(function (results) {
                    if (results.data) {
                        // replace the userId from the URL (e.g"55133cede4b0fc25429d9770") to meaningfull username (e.g
                        // "user tag")
                        var paramsData = results.data.constructor === Array ? results.data[0] : results.data;
                        return state.mapParams(dashboardReport.dashboardParams, paramsData);
                    }
                });
            });

            return $q.all(reportPromises).then(function (reportsParams) {
                for (var params of reportsParams) {
                    dashboard.setParams(params);
                }

                if (dashboard.checkReady()) {
                    dashboard.getAllWidgets().forEach(function (widget) {
                        widget.refreshIfRequired(params);
                    });

                    if (dashboard.buttons) {
                        dashboard.buttons.forEach(function (button) {
                            button.refresh();
                        });
                    }

                    delete dashboard._messages;
                    state.setParams(params, false);
                }
            });
        };

        /**
         * Extends the dashboard's params with the specified params
         * @param {Object} params
         */
        Dashboard.prototype.setParams = function (params) {
            if (!this.params) {
                this.params = {};
            }

            utils.objects.extend(this.params, params);
        };

        /**
         * Sets the Dashboard's widgets and sets the Dashboard as the widgets' parent
         * @param widgets
         */
        Dashboard.prototype.setWidgets = function (widgets) {
            this.widgets = widgets.map(function (widgetConfig) {
                if (widgetConfig instanceof Widget) {
                    widgetConfig.setParent(this);
                    return widgetConfig;
                }

                return new Widget(widgetConfig, this);
            }.bind(this));
        };

        /**
         * Sets the Dashboard's buttons and sets the Dashboard as the widgetButton' parent
         * @param buttons
         */
        Dashboard.prototype.setDashboardButtons = function (buttons) {
            this.buttons = buttons.map(function (buttonConfig) {
                return new WidgetButton(buttonConfig, this);
            }.bind(this));
        };

        /**
         * Sets the Dashboard's columns and sets the Dashboard as the columns' parent
         * @param columns
         */
        Dashboard.prototype.setColumns = function (columns) {
            this.columns = columns.map(function (columnConfig) {
                return new DashboardLayout.Column(columnConfig, this);
            }.bind(this));

            this.columns = this.columns.filter(function (column) {
                return column.show;
            });
            DashboardLayout.setColumnSpans(this.columns);
        };

        /**
         * Sets the Dashboard's rows and sets the Dashboard as the rows' parent
         * @param rows
         */
        Dashboard.prototype.setRows = function (rows) {
            this.rows = rows.map(function (rowConfig) {
                return new DashboardLayout.Row(rowConfig, this);
            }.bind(this));
        };

        /**
         * Updates any parsable properties according to the current dashboard and application state
         * Also updates views
         */
        Dashboard.prototype.update = function () {

            function getParsedValue (parsableProperty) {
                var propertyConfig = dashboard.config[parsableProperty];

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
                return utils.strings.parseValue(value, {}, dashboardState);
            }

            var dashboardState = this.getState(),
                dashboard = this;

            for (var parsableProperty of parsableProperties) {
                this[parsableProperty] = getParsedValue(parsableProperty);
            }

        };

        /**
         * Returns all the widgets in the dashboard: in columns, rows and the dashboard itself
         */
        Dashboard.prototype.getAllWidgets = function () {
            var widgets = this.widgets || [];

            if (this.columns) {
                for (var column of this.columns) {
                    widgets = widgets.concat(column.getAllWidgets());
                }
            }

            if (this.rows) {
                for (var row of this.rows) {
                    widgets = widgets.concat(row.getAllWidgets());
                }
            }

            return widgets;
        };

        Dashboard.prototype.getState = function () {
            return utils.objects.extend({}, state.currentParams, this.params,
                this.controlsList && this.controlsList.getParams());
        };

        function DashboardReport (config) {
            this.report = config.report;
            this.dashboardParams = config.dashboardParams;
            this.useFirstIndex = !!config.useFirstIndex;
        }

        Dashboard.prototype.getDefaultUrlNavigation = function () {

            function getNavigationUrl (navItem) {
                if (navItem.url) {
                    return navItem.url;
                }
                if (navItem.children && navItem.children.length > 0) {
                    for (var childIndex in navItem.children) {
                        if (navItem.children.hasOwnProperty(childIndex)) {
                            var url = getNavigationUrl(navItem.children[childIndex]);
                            if (url) {
                                return url;
                            }
                        }
                    }
                }
            }

            if (this.navigation) {
                return getNavigationUrl(this.navigation);
            }

        };

        return Dashboard;
    }

    DashboardClass.$inject =
        ["$q", "Widget", "WidgetButton", "DashboardLayout", "utils", "conditions", "Report", "reports", "state",
            "ControlList"];

    angular.module("Widgets").factory("Dashboard", DashboardClass);
})();
