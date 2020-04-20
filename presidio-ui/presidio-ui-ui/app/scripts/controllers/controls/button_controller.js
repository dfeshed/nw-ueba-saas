(function () {
    'use strict';

    angular.module("Fortscale").controller("ButtonController",
        ["$scope", "utils", "eventBus", "events", "state", function ($scope, utils, eventBus, events, state) {
            var buttonState;

            $scope.$on("$destroy", function () {
                eventBus.unsubscribe("dashboardParamsChange", onParamsChange);
            });

            $scope.buttonClick = function () {
                var events = angular.copy(angular.isArray($scope.control.onClick) ? $scope.control.onClick :
                    [$scope.control.onClick]);
                angular.forEach(events, function (event) {
                    if ($scope.button.actionOptions) {
                        event.actionOptions = jQuery.extend(true, event.actionOptions, $scope.button.actionOptions);
                    }
                });

                events.triggerDashboardEvent(events, null, state.currentParams);

                if ($scope.control.toggle) {
                    buttonState = buttonState === "on" ? "off" : "on";
                    $scope.button = $scope.control.toggle[buttonState];
                    $scope.button.state = buttonState;
                    setTexts();
                }
            };

            function setTexts () {
                if ($scope.button.text) {
                    $scope.button.text = utils.strings.parseValue($scope.button.text, {}, $scope.mainDashboardParams);
                }

                if ($scope.button.tooltip) {
                    $scope.button.tooltip =
                        utils.strings.parseValue($scope.button.tooltip, {}, $scope.mainDashboardParams);
                }
            }

            function init () {
                var buttonSettings;

                if ($scope.control.toggle) {
                    buttonState = $scope.mainDashboardParams[$scope.control.toggle.toggleParam] ? "on" :
                    $scope.control.toggle.defaultState || "off";
                    buttonSettings = $scope.control.toggle[buttonState];
                    buttonSettings.state = buttonState;
                }
                else {
                    buttonSettings = $scope.control;
                }

                $scope.button = angular.extend({}, $scope.control, buttonSettings);
                setTexts();
            }

            function needsRefresh (params) {
                if ($scope.control.toggle && params[$scope.control.toggle.toggleParam] !== undefined) {
                    var toggleParamState = params[$scope.control.toggle.toggleParam] ? "on" : "off";
                    return toggleParamState !== buttonState;
                }

                if ($scope.control.refreshOn) {
                    for (var i = 0; i < $scope.control.refreshOn.length; i++) {
                        if (params[$scope.control.refreshOn[i]] !== undefined) {
                            return true;
                        }
                    }
                }

                return false;
            }

            function onParamsChange (e, changedParams) {
                if (needsRefresh(changedParams)) {
                    init();
                }
            }

            if ($scope.control.toggle && $scope.control.refreshOn) {
                eventBus.subscribe("dashboardParamsChange", onParamsChange);
            }


            init();
        }]);
}());
