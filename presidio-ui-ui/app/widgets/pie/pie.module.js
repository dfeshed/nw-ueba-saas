(function () {
    'use strict';

    angular.module("PieWidget", ["Utils", "Chart", "Widgets"]).run(["widgetViews", function (widgetViews) {

        function pieDataParser (view, data) {
            var total = 0;

            data.forEach(function (item) {
                var labelValue = item[view.settings.label];
                if ((!labelValue || labelValue === "null" ) && labelValue !== 0 && view.settings.emptyLabelDisplay) {
                    item[view.settings.label] = view.settings.emptyLabelDisplay;
                }

                total += item[view.settings.value] || 0;
            });

            data.forEach(function (item) {
                item._percent = 100 * item[view.settings.value] / total;
            });

            return data;
        }

        widgetViews.registerView("pie", {dataParser: pieDataParser});

    }]);
}());
