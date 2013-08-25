'use strict';

angular.module('Fortscale')
    .directive('barChart', function () {
        return {
            template: "<div></div>",
            restrict: 'E',
            replace: true,
            require: "?ngModel",
            link: function postLink(scope, element, attrs, ngModel) {
                var data, dataChanged, settings, items,
                    chartElement = element[0],
                    yFields,
                    chart;

                scope.$watch(attrs.ngModel, function(chartData){
                    data = chartData;
                    dataChanged = true;
                    drawChart();
                });

                scope.$watch(attrs.graphSettings, function(value){
                    settings = value;

                    yFields = [];
                    angular.forEach(settings.axes.y.fields, function(yField){
                        yFields.push(yField.name)
                    });

                    drawChart();
                });

                function drawChart(){
                    if (!data || !settings)
                        return;

                    if (!dataChanged)
                        return;

                    chartElement.style.width = settings.width;
                    chartElement.style.height = settings.height;

                    if (!chart){
                        chart = Morris.Bar({
                            element: chartElement,
                            xkey: settings.axes.x.field,
                            ykeys: yFields,
                            labels: settings.axes.labels,
                            hideHover: "auto"
                        });
                    }

                    chart.setData(data.chartValues);

                    if (settings.events){
                        angular.forEach(settings.events, function(eventSettings){
                            chart.on(eventSettings.eventName, function(index){
                                scope.$apply(function(){
                                    scope.$emit("widgetEvent", { event: eventSettings, data: data.chartValues[index], widget: scope.widget });
                                });
                            })
                        });
                    }
                }
            }
        };
    });