(function () {
    'use strict';

    angular.module("Tooltip", []).directive("tooltip", ["$timeout", function ($timeout) {
        return {
            restrict: 'E',
            transclude: true,
            template: '<div class="tooltip-directive" ng-show="isOpen" ng-transclude></div>',
            require: '?ngModel',
            link: function postLink (scope, element, attrs, ngModel) {

                function open () {
                    scope.isOpen = true;
                    element.css("opacity", 0);
                    document.body.addEventListener("mousedown", onBackgroundClick);

                    $timeout(function () {
                        setPosition();
                        element.css("opacity", 1);
                    }, 50);
                }

                function close () {
                    document.body.removeEventListener("mousedown", onBackgroundClick);
                    scope.isOpen = false;
                    ngModel.$setViewValue(false);
                }

                function setPosition () {
                    var width = document.documentElement.clientWidth,
                        height = document.documentElement.clientHeight,
                        tooltipWidth = tooltip.outerWidth(),
                        tooltipHeight = tooltip.outerHeight(),
                        margin = 20;

                    if (currentPosition.left + tooltipWidth > width - margin) {
                        currentPosition.left = currentPosition.left - tooltipWidth;
                    }

                    if (currentPosition.top + tooltipHeight > height - margin) {
                        currentPosition.top = currentPosition.top - tooltipHeight;
                    }

                    tooltip.css(currentPosition);
                }

                function onBackgroundClick (e) {
                    var tooltipElement = $(e.target).closest(".tooltip-directive");
                    if (!tooltipElement.length || tooltipElement[0] !== tooltip[0]) {
                        scope.$apply(close);
                    }

                    return true;
                }

                var tooltip = element.find(".tooltip-directive"),
                    currentPosition;

                scope.isOpen = false;

                ngModel.$render = function () {
                    if (ngModel.$viewValue) {
                        open();
                    } else {
                        close();
                    }
                };

                scope.$watch(attrs.position, function (value) {
                    if (value) {
                        currentPosition = value;
                    }
                });

            }
        };
    }]);
}());
