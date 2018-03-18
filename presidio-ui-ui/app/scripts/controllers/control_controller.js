(function () {
    'use strict';

    angular.module("Fortscale")
        .controller("WidgetControlController", ["$scope", "controls", "eventBus", "events",
            function ($scope, controls, eventBus, events) {
                function onParamsChange(e, changedParams) {
                    for (var i = 0; i < $scope.control.refreshOn.length; i++) {
                        if (changedParams[$scope.control.refreshOn[i]] !== undefined) {
                            controls.initControl($scope.control, $scope.params,
                                angular.extend($scope.getWidgetParams(), changedParams));
                            return;
                        }
                    }
                }

                $scope.onEvent = function (e, eventConfig) {
                    if (eventConfig) {
                        events.triggerDashboardEvent(eventConfig);
                    }
                };

                function init() {
                    var widgetParams = $scope.getWidgetParams ? $scope.getWidgetParams() : $scope.mainDashboardParams;
                    controls.initControl($scope.control, $scope.params, widgetParams);
                    if ($scope.control.refreshOn) {
                        eventBus.subscribe("dashboardParamsChange", onParamsChange);
                    }

                    $scope.$on("$destroy", function () {
                        eventBus.unsubscribe("dashboardParamsChange", onParamsChange);
                    });
                }

                init();
            }]);
}());
