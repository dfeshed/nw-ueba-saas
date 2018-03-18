(function () {
    'use strict';

    angular.module('Fortscale')
        .directive('progressBar', function ($timeout, reports, transforms) {
            return {
                template: "<div class='progress-bar' ng-style='{width: width}' title='{{tooltip}}'><span " +
                "ng-style='{width: progressValue, background: progressColor}'><em ng-show='showArrow'></em></span>" +
                "</div>",
                restrict: 'E',
                replace: true,
                require: "?ngModel",
                link: function postLink(scope, element, attrs, ngModel) {
                    var rawValue,
                        settings,
                        colors = ["#90CA77", "#E9B64D", "#E48743", "#E48743", "#9E3B33"];

                    scope.progressValue = 0;

                    scope.$watch(attrs.ngModel, function (value) {
                        rawValue = parseFloat(value, 10);
                        setValue();
                    });

                    scope.$watch(attrs.settings, function (value) {
                        settings = value;
                        scope.width = settings.width;
                        if (settings.tooltip) {
                            scope.tooltip = settings.tooltip;
                        }

                        setValue();
                    });

                    scope.$watch(attrs.tooltip, function (value) {
                        scope.tooltip = value;
                    });

                    function getColor(value) {
                        var color,
                            colorStep = 1 / colors.length;

                        for (var i = 0; i < colors.length; i++) {
                            color = colors[i];
                            if (value < (i + 1) * colorStep) {
                                return color;
                            }
                        }

                        return color;
                    }

                    function setValue() {
                        if (rawValue === undefined || !settings) {
                            return;
                        }

                        rawValue = Math.max(settings.min, rawValue);
                        rawValue = Math.min(settings.max, rawValue);

                        var valueFracture = (rawValue - settings.min) / (settings.max - settings.min);
                        scope.progressValue = (100 * valueFracture) + "%";
                        scope.progressColor = getColor(valueFracture);
                        scope.showArrow = valueFracture > 0.05 && valueFracture < 0.95;
                    }
                }
            };
        });
}());
