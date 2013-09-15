angular.module("Fortscale").factory("dashboards", ["$q", "DAL", "reports", "widgets", function($q, DAL, reports, widgets){
    var cachedDashboards = {};

    function getWidgetConfig(widgetId){
        return widgets.getWidget(widgetId);
    }

    function getDashboardWidgets(dashboard){
        var dashboardPromises = [];

        angular.forEach(dashboard.columns, function(column){
            angular.forEach(column.widgets, function(widget, widgetIndex){
                var promises = [],
                    loadWidget,
                    loadReport;

                if (widget.widgetId){
                    loadWidget = true;
                    promises.push(getWidgetConfig(widget.widgetId));
                }
                if (widget.reportId){
                    loadReport = true;
                    promises.push(reports.getReport(widget.reportId));
                }
                if (promises.length){
                    dashboardPromises.push($q.all(promises).then(function(results){
                        var widgetConfig = loadWidget ? results[0] : null,
                            reportConfig = loadReport ? loadWidget ? results[1] : results[0] : null;

                        widgetConfig = widgetConfig || {};

                        if (reportConfig)
                            jQuery.extend(true, widgetConfig, { report: { query: reportConfig } }, { report: widget.report });

                        jQuery.extend(true, widgetConfig, widget);

                        column.widgets[widgetIndex] = widgetConfig;
                        widgetConfig._ready = true;
                    }, function(error){
                        console.error("Can't get widget: ", error);
                    }));
                }
            });
        });

        return $q.all(dashboardPromises);
    }

    var methods = {
        getDashboardsList: function(){
            var deferred = $q.defer();

            DAL.dashboards.getDashboardsList().then(function(results){
                deferred.resolve(results.data);
            }, deferred.reject);

            return deferred.promise;
        },
        getDashboardById: function(dashboardId){
            var deferred = $q.defer();

            if (cachedDashboards[dashboardId])
                deferred.resolve(angular.copy(cachedDashboards[dashboardId]));
            else
                DAL.dashboards.getDashboardById(dashboardId).then(function(dashboard){
                    getDashboardWidgets(dashboard).then(function(){
                        cachedDashboards[dashboardId] = dashboard;
                        deferred.resolve(angular.copy(dashboard));
                    });
                }, deferred.reject);

            return deferred.promise;
        }
    };

    return methods;
}]);