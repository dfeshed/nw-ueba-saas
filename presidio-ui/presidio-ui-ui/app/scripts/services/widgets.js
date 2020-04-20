(function () {
    'use strict';

    function widgets ($q, DAL, format, reports, widgetViews, EventBus, Widget) {
        var cachedWidgets = {};

        function parseFieldValue (field, value, data, index, params, item) {
            return value.replace(/\{\{([^\}]+)\}\}/g, function (match, variable) {
                if (/^@/.test(variable)) {
                    var param = variable.replace("@", "");
                    if (param === "index") {
                        return index;
                    } else if (param === "item") {
                        return item;
                    }

                    return params[param] || "";
                } else {
                    var dataValue = data[variable];
                    if (dataValue !== undefined && dataValue !== null) {
                        if (field.format) {
                            return format[field.format](dataValue, field.formatOptions);
                        }

                        return dataValue;
                    }

                    return "";
                }
            });

        }

        var methods = {
            getWidget: function (widgetId, uniqueWidgetId) {
                var cacheWidgetId = uniqueWidgetId || widgetId;

                if (cachedWidgets[cacheWidgetId]) {
                    return $q.when(angular.copy(cachedWidgets[cacheWidgetId]));
                } else {
                    return DAL.widgets.getWidget(widgetId).then(function (widgetConfig) {
                        var widget = Widget.loadWidget(widgetConfig);

                        if (widgetConfig.reportId) {
                            return reports.getReport(widgetConfig.reportId).then(function (report) {
                                widget.report = report;

                            });
                        }
                        cachedWidgets[cacheWidgetId] = widget;
                        return widget;

                    }, function () {
                        return $q.reject("Widget with ID '" + widgetId + "' not found.");
                    });
                }
            }, parseFieldValue: parseFieldValue, refreshAll: function () {
                widgetsEventBus.triggerEvent("refreshAll");
            }, setViewValues: function (view, data, params, rawData) {
                return $q.when(widgetViews.parseViewData(view, data, params, rawData));
            }
        };

        var widgetsEventBus = EventBus.setToObject(methods, ["refreshAll"]);

        return methods;
    }

    widgets.$inject = ["$q", "DAL", "format", "reports", "widgetViews", "EventBus", "Widget"];

    angular.module("Fortscale").factory("widgets", widgets);

})();
