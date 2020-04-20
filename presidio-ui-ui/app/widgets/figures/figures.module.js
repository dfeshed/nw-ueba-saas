(function () {
    'use strict';

    angular.module("FiguresWidget", ["Utils", "Widgets"]).run(["utils", "widgetViews", function (utils, widgetViews) {

        function figuresDataParser (view, data, params) {
            var figuresData = [],
                noValueDisplay = view.settings.noValue || "(No value)";

            data.forEach(function (item, i) {
                var value = view.settings.value ? utils.strings.parseValue(view.settings.value, item, params) :
                    item.value;
                if (!value && value !== 0) {
                    value = noValueDisplay;
                }

                figuresData.push({
                    label: getLabel(item, i),
                    value: value
                });
            });

            function getLabel (item, index) {
                var label;
                if (view.settings.label) {
                    label = item[view.settings.label];
                }

                if (!label && view.settings.labels) {
                    label = view.settings.labels[index];
                }

                return label;
            }

            return figuresData;
        }

        widgetViews.registerView("figures", {dataParser: figuresDataParser});

    }]);
}());
