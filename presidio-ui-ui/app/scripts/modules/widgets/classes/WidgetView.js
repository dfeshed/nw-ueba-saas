(function () {
    'use strict';

    function WidgetViewClass (widgetViews) {
        /**
         * Constructor for widget views
         * @param config
         * @constructor
         */
        function WidgetView (config) {
            this.type = config.type;
            this.settings = config.settings;
            this.templateUrl =
                'widgets/' + this.type.replace(/\./g, '/') + '/' + this.type.split('.').pop() + '.view.html';
        }

        /**
         * Sets data to the view, which parses it according to its view type.
         * @param {Array} data
         * @param {Object} state
         */
        WidgetView.prototype.setData = function (data, state) {
            var view = this;
            view.data = null;

            widgetViews.parseViewData(view, data, state).then(function (viewData) {
                view.data = viewData;
            }, function (error) {
                view.error = error;
            });
        };

        /**
         * To be called when the view is no longer needed, to free memory, etc.
         */
        WidgetView.prototype.destroy = function () {
            this.data = null;
            this.error = null;
        };

        return WidgetView;
    }

    WidgetViewClass.$inject = ["widgetViews"];

    angular.module("Widgets").factory("WidgetView", WidgetViewClass);
})();
