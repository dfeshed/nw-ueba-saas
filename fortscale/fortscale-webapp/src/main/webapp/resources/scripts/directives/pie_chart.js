'use strict';

angular.module('Fortscale')
    .directive('pieChart', function () {
        return {
            templateUrl: "views/widgets/pieChart_template.html",
            restrict: 'E',
            replace: true,
            require: "?ngModel",
            link: function postLink(scope, element, attrs, ngModel) {
                var data, dataChanged, settings, items,
                    chart = element.find(".widget-pie-chart")[0];

                scope.$watch(attrs.ngModel, function(chartData){
                    if (chartData){
                        data = chartData;
                        dataChanged = true;
                        drawChart();
                        scope.items = data.items;
                    }
                });

                scope.$watch(attrs.graphSettings, function(value){
                    settings = value;
                    scope.showInfo = settings.showInfo;
                    drawChart();
                });

                function drawChart(){
                    if (!data || !settings)
                        return;

                    if (!dataChanged)
                        return;

                    chart.style.width = settings.width;
                    chart.style.height = settings.height;

                    scope.currentItem = data.items[0];

                    var donut = Morris.Donut({
                        element: chart,
                        data: data.chartValues,
                        formatter: function (y) { return y + "%" }
                    });

                    if (settings.showInfo){
                        donut.on("hover", function(index){
                            scope.safeApply(function(){
                                scope.currentItem = data.items[index];
                            });
                        })
                    }
                }
            }
        };
    });