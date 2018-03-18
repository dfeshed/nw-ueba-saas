(function () {
    'use strict';

    angular.module("BarsChartWidget", ["Utils", "ChartWidgets", "Widgets"]).run(["chartWidgetsData", "widgetViews",
        function (chartWidgetsData, widgetViews) {
            widgetViews.registerView("barsChart", {dataParser: chartWidgetsData.getData});
        }]);
}());
