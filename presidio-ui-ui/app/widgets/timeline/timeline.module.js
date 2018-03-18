(function () {
    'use strict';

    angular.module("TimelineWidget", ["Utils", "ChartWidgets", "Widgets", "Events"]).run(["widgetViews",
        function (widgetViews) {

            function timelineDataParser (view, data) {
                var viewData = data;

                if (view.settings.legend) {
                    viewData.legend = view.settings.legend;
                }

                return viewData;
            }

            widgetViews.registerView("timeline", {dataParser: timelineDataParser});

        }]);
}());
