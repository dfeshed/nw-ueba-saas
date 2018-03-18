(function () {
    'use strict';

    function widgetViews ($q) {

        function getView (viewType) {
            var view = registeredViews[viewType];
            if (!view) {
                throw new Error("Unknown view, '" + viewType + "'.");
            }

            return view;
        }

        var registeredViews = {};

        return {
            parseViewData: function (_view, data, params, rawData) {
                var view = getView(_view.type);

                if (view.dataParser) {
                    return $q.when(view.dataParser(_view, data, params, rawData));
                }

                return $q.when(data);
            },
            registerView: function (viewId, view) {
                if (!view) {
                    view = {};
                }

                if (Object(view) !== view) {
                    throw new TypeError("Can't register view, expected an object but got " + view);
                }

                if (view.dataParser && view.dataParser.constructor !== Function) {
                    throw new TypeError("Can't register view, expected a function for dataParser but got " +
                        view.dataParser);
                }

                registeredViews[viewId] = view;
            },
            validateSettings: function (viewId, settings) {
                var view = getView(viewId);

                if (!settings) {
                    return true;
                }

                if (!view.validate) {
                    return true;
                }

                return view.validate(settings);
            },
            viewExists: function (viewId) {
                return !!registeredViews[viewId];
            }
        };

    }

    widgetViews.$inject = ["$q"];

    angular.module("Widgets").factory("widgetViews", widgetViews);
})();
