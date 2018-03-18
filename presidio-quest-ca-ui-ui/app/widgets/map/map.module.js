(function () {
    'use strict';

    angular.module("MapWidget", ["Utils", "Chart", "Widgets", "Events"]).run(["widgetViews", function (widgetViews) {
        widgetViews.registerView("map");
    }]);
}());
