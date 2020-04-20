(function () {
    'use strict';

    angular.module("MultiTimelineWidget", ["Utils", "Chart", "Widgets"]).run(["widgetViews", function (widgetViews) {
        widgetViews.registerView("multiTimeline");
    }]);
}());
