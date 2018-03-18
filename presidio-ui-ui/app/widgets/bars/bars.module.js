(function () {
    'use strict';

    angular.module("BarsWidget", ["Utils", "Chart", "Widgets"]).run(["widgetViews", function (widgetViews) {

        function barsDataParser (view, data) {
            if (view.settings.calculatePercentage) {
                var total = 0;
                data.forEach(function (row) {
                    total += row[view.settings.value];
                });

                data.forEach(function (row) {
                    row.label = row.label ? row.label : "Unknown";
                    row._percent = (row[view.settings.value] / total) * 100;
                });
            }

            return data;
        }

        widgetViews.registerView("bars", {dataParser: barsDataParser});

    }]);
}());
