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
                        scope.items = data.chartValues;
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
                    chart.style.height = chart.style.width = size;

                    scope.currentItem = data.chartValues[0];

                    if (settings.calculatePercent){
                        var total = 0;

                        angular.forEach(data.chartValues, function(item){
                            total += item.value;
                        });

                        angular.forEach(data.chartValues, function(item){
                            item.value = item.value * 100 / total;
                        });
                    }

                    var donut = Morris.Donut({
                        element: chart,
                        data: data.chartValues,
                        formatter: function (y) {
                            var value;

                            if (settings.calculatePercent){
                                value = y < 1 ? "< 1%" :
                                    y > 99 && y < 100 ? "> 99%" : Math.round(y) + "%";
                            }
                            else
                                value = y.toFixed(2);

                            return value;
                        }
                    });

                    if (settings.showInfo){
                        donut.on("hover", function(index){
                            scope.safeApply(function(){
                                scope.currentItem = data.chartValues[index];
                            });
                        })
                    }
                }
            }
        };
    });