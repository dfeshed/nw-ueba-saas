(function () {
    'use strict';

    angular.module("Fortscale")
        .run(["widgetViews", function (widgetViews) {
            widgetViews.registerView("htmlWidget", {});
        }]);
}());
