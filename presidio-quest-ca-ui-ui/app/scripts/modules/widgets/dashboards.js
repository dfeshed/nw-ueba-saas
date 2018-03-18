(function () {
    'use strict';

    function dashboards ($q, DAL, Widget, Dashboard, utils, Report) {

        function loadWidgets (widgets) {
            var loadPromises = widgets.map(function (widgetConfig) {
                return Widget.loadWidget(widgetConfig);
            });

            return $q.all(loadPromises);
        }

        function loadReports (reports) {
            var loadPromises = reports.map(function (reportConfig) {
                return Report.loadReport(reportConfig).then(function (report) {
                    var loadedConfig = {
                        report: report
                    };

                    return utils.objects.extend({}, reportConfig, loadedConfig);
                });
            });

            return $q.all(loadPromises);
        }

        function getColumnWidgets (column) {
            var promises = [];

            if (column.widgets) {
                promises.push(loadWidgets(column.widgets).then(function (widgets) {
                    column.widgets = prepareDashboardWidgets(widgets);
                }));
                column.widgets = [];
            }

            if (column.rows) {
                column.rows.forEach(function (row) {
                    if (row.widgets) {
                        promises.push(loadWidgets(row.widgets).then(function (widgets) {
                            row.widgets = prepareDashboardWidgets(widgets);
                        }));
                        row.widgets = [];
                    }

                    if (row.columns) {
                        row.columns.forEach(function (column) {
                            promises.push(getColumnWidgets(column));
                        });
                    }
                });
            }

            return $q.all(promises);
        }

        /**
         * Creates a closure to use for preparing widgets to be placed in a dashboard
         * @param widgets
         * @returns {Function}
         */
        function prepareDashboardWidgets (widgets) {
            return widgets.map(function (widget) {
                return widget.clone();
            });
        }

        function getDashboardWidgets (dashboardConfig) {
            dashboardConfig = utils.objects.copy(dashboardConfig);

            var dashboardPromises = [];

            if (dashboardConfig.widgets) {
                dashboardPromises.push(loadWidgets(dashboardConfig.widgets).then(function (widgets) {
                    dashboardConfig.widgets = prepareDashboardWidgets(widgets);
                }));
            }

            if (dashboardConfig.columns) {
                dashboardConfig.columns.forEach(function (column) {
                    dashboardPromises.push(getColumnWidgets(column));
                });
            }

            if (dashboardConfig.reports) {
                dashboardPromises.push(loadReports(dashboardConfig.reports).then(function (reports) {
                    dashboardConfig.reports = reports;
                }));
            }

            return $q.all(dashboardPromises).then(function () {
                return new Dashboard(dashboardConfig);
            });
        }

        var cachedDashboardConfigs = {};

        var methods = {
            getDashboardById: function (dashboardId, dashboardDataOnly) {
                if (cachedDashboardConfigs[dashboardId]) {
                    return getDashboardWidgets(cachedDashboardConfigs[dashboardId]).then(function (dashboard) {
                        return dashboard;
                    });
                }
                else {
                    return DAL.dashboards.getDashboardById(dashboardId).then(function (dashboard) {
                        function getWidgetsAndReturn (dashboardConfig) {
                            cachedDashboardConfigs[dashboardId] = dashboardConfig;

                            return getDashboardWidgets(dashboardConfig).then(function (dashboard) {
                                return dashboard;
                            });
                        }

                        if (dashboard.extends) {
                            return methods.getDashboardById(dashboard.extends, true).then(function (extendedDashboard) {
                                var dashboardConfig = utils.objects.extend({}, extendedDashboard, dashboard);
                                delete dashboardConfig.extends;

                                return getWidgetsAndReturn(dashboardConfig);
                            });
                        }
                        else {
                            if (dashboardDataOnly) {
                                return dashboard;
                            }

                            return getWidgetsAndReturn(dashboard);
                        }

                    });
                }
            },
            createDashboard: function (settings) {
                var dashboard = {
                    id: "dashboard_" + new Date().valueOf(),
                    name: "Untitled Dashboard",
                    columns: [
                        {widgets: []}
                    ]
                };
                return angular.extend(dashboard, settings);
            }
        };

        return methods;


    }

    dashboards.$inject = ["$q", "DAL", "Widget", "Dashboard", "utils", "Report"];

    angular.module("Widgets").factory("dashboards", dashboards);
})();
