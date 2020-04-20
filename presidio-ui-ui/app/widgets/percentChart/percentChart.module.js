(function () {
    'use strict';

    angular.module("PercentChartWidget", ["Utils", "ChartWidgets", "Widgets"]).run(["chartWidgetsData", "widgetViews",
        function (chartWidgetsData, widgetViews) {
            widgetViews.registerView("percentChart", {dataParser: chartWidgetsData.getData});
        }]);
}());
