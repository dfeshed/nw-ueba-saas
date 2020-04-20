(function () {
    'use strict';

    angular.module("TabsWidget", ["Utils", "Styles", "Widgets"]).run(["utils", "styles", "widgetViews",
        function (utils, styles, widgetViews) {

            function tabsDataParser (view, data, params) {
                var viewData;

                if (view.settings.tab) {
                    viewData = [];

                    angular.forEach(data, function (item, itemIndex) {
                        var itemData = {};
                        itemData.display = utils.strings.parseValue(view.settings.tab.display, item, params, itemIndex);
                        itemData.id = utils.strings.parseValue(view.settings.tab.id, item, params, itemIndex);

                        if (view.settings.tab.style) {
                            styles.getStyle(view.settings.tab, item).then(function (style) {
                                itemData.style = style;
                            });
                        }

                        if (view.settings.label) {
                            itemData.label = {
                                value: utils.strings.parseValue(view.settings.label.value, item, params, itemIndex)
                            };

                            if (view.settings.label.style) {
                                styles.getStyle(view.settings.label, item).then(function (style) {
                                    itemData.label.style = style;
                                });
                            }
                        }

                        viewData.push(itemData);
                    });
                }
                else if (view.settings.tabs) {
                    viewData = view.settings.tabs;
                }

                return viewData;
            }

            widgetViews.registerView("tabs", {dataParser: tabsDataParser});

        }]);
}());
