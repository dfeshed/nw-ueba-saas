angular.module("ScatterPlotWidget", ["Utils", "ChartWidgets", "Widgets"])
    .run(["utils", "widgetViews", function (utils, widgetViews) {
    'use strict';

    function scatterPlotDataParser(view, data, params){
        var timeField = view.settings.timeField;

        if (timeField) {
            var hourFieldName = view.settings.timeFieldHour = timeField + "_hour";

            data.forEach(function (row) {
                var timeValue = row[timeField];
                var timeMoment;

                if (timeValue) {
                    timeMoment = utils.date.getMoment(timeValue);

                    // Update time value to be a Date value
                    row[timeField] = timeMoment.isValid() ? timeMoment.toDate() : null;
                    // Add an hour value
                    row[hourFieldName] = timeMoment.hours() + (timeMoment.minutes() / 60);
                }
            });
        }

        return data;
    }

    widgetViews.registerView("scatterPlot", {
        dataParser: scatterPlotDataParser
    });
}]);
