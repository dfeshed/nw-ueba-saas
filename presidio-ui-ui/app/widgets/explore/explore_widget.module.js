(function () {
    'use strict';

    angular.module("ExploreWidget", ["Explore", "Explore.Filters", "Widgets"]).run(["widgetViews",
        function (widgetViews) {
            widgetViews.registerView("explore");
        }]);
}());
