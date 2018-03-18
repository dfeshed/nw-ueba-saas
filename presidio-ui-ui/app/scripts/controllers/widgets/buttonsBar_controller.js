(function () {
    'use strict';

    angular.module("Fortscale").controller("ButtonsBarWidgetController",
        ["$scope", "events", function ($scope, events) {
            $scope.btnClick = function (button) {
                if (button.toggleOnClick) {
                    button.on = !button.on;
                }

                angular.forEach($scope.view.settings.events, function (event) {
                    if (event.eventName === "click") {
                        events.triggerDashboardEvent(event);
                    }
                });
            };
        }]);
}());
