'use strict';

angular.module('PieChartWidget')
    .directive('pieChart', function () {
        return {
            templateUrl: "widgets/pieChart/pieChart_template.html",
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
                    if (settings){
                        scope.showInfo = settings.showInfo;
                        drawChart();
                    }
                });

                function drawChart(){
                    if (!data || !settings || !data.chartValues)
                        return;

                    if (!dataChanged)
                        return;

                    var size = settings.size ? settings.size + "%" : "200px";
                    chart.style.width = size;

                    scope.currentItem = data.items[0];

                    if (settings.calculatePercent){
                        var total = 0;

                        angular.forEach(data.chartValues, function(item){
                            total += item.value;
                        });

                        angular.forEach(data.chartValues, function(item){
                            item.value = Math.round(item.value * 100 / total);
                        });
                    }

                    var donut = Morris.Donut({
                        element: chart,
                        data: data.chartValues,
                        formatter: function (y) { return y + (settings.calculatePercent ? "%" : "") }
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