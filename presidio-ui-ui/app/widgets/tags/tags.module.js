(function () {
    'use strict';

    angular.module("TagsWidget", ["Tags", "Widgets"]).run(["tags", "widgetViews", function (tags, widgetViews) {

        function tagsDataParser (view, data, params) {
            if (!data || !data.length) {
                return null;
            }

            return tags.getTags(view.settings.tags, data[0]);
        }

        widgetViews.registerView("tags", {dataParser: tagsDataParser});

    }]);
}());
