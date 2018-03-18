(function () {
    'use strict';

    function widgetDirective (Widget, state, widgets) {

        function linkFn (scope) {
            scope.$on("$destroy", function () {
                scope.widget.destroy();
                removeWidgetListeners();
            });

            scope.$watch("widgetModel", function (value) {
                if (value && !(value instanceof Widget)) {
                    throw new TypeError("Invalid widgetModel for widget directive, expected an instance of Widget.");
                }

                scope.widget = value;
                setWidgetListeners(scope.widget);
            });

            function setWidgetListeners (widget) {
                removeWidgetListeners();

                if (widget) {
                    if (Object.keys(widget.refreshOn).length) {
                        state.onStateChange.subscribe(onStateChange);
                    }
                }

                widgets.onRefreshAll.subscribe(onRefreshAll);
            }

            function removeWidgetListeners () {
                state.onStateChange.unsubscribe(onStateChange);
                widgets.onRefreshAll.unsubscribe(onRefreshAll);
            }

            function onStateChange (e, data) {
                scope.widget.refreshIfRequired(data.params);
            }

            function onRefreshAll () {
                scope.widget.refresh();
            }
        }

        return {
            templateUrl: "scripts/modules/widgets/widget.directive/widget.directive.template.html",
            restrict: 'E',
            replace: true,
            scope: {
                widgetModel: "="
            },
            link: linkFn
        };

    }


    /**
     * Decorator for the original directive.
     * @param Widget
     * @param state
     * @param widgets
     * @returns {{templateUrl, restrict, replace, scope, link}|*}
     */
    function widgetExploreDirective (Widget, state, widgets) {
        var origDirective = widgetDirective(Widget, state, widgets);
        origDirective.templateUrl =
            'scripts/modules/widgets/widget.directive/widget-explore.directive.template.html';
        return origDirective;
    }

    widgetDirective.$inject = ["Widget", "state", "widgets"];
    widgetExploreDirective.$inject = ["Widget", "state", "widgets"];

    angular.module("Widgets").directive("widget", widgetDirective);
    angular.module("Widgets").directive("widgetExplore", widgetExploreDirective);
})();
