'use strict';

angular.module('Fortscale')
    .directive('graph', function () {
        return {
            template: '<div class="widget-graph"><div></div></div>',
            restrict: 'E',
            require: "?ngModel",
            link: function postLink(scope, element, attrs, ngModel) {
                var defaultOptions = {
                        'width':'100%',
                        fontSize: 10,
                        vAxis: { minValue: 0 },
                        chartArea:{ left:35, top:20 },
                        interpolateNulls: true,
						thickness: 1
                    },
                    graphOptions,
                    data,
					dataTableColumns,
					dataChanged,
                    chart,
                    events,
                    settings,
                    gotHeight;

                // Load the Visualization API and the piechart package.
                //google.load('visualization', '1.0', {'packages':['corechart']});

                scope.$watch(attrs.ngModel, function(chartData){
                    data = chartData;
					dataChanged = true;
                    drawChart();
                });

                scope.$watch(attrs.graphSettings, function(value){
                    events = value.events;
                    settings = value;
                    graphOptions = angular.extend({}, defaultOptions, settings.graphOptions);

                    drawChart();
                });

                scope.$watch(attrs.graphHeight, function(height){
                    element[0].firstElementChild.style.height = attrs.graphHeight + "px";
                    gotHeight = true;
                    drawChart();
                });

                function eventHandler(eventSettings, e){
                    scope.$apply(function(){
                        scope.$emit("widgetEvent", { event: eventSettings, data: e, widget: scope.widget });
                    });
                }

                function getPointSelectionValue(){
                    var selection = chart.getSelection();
                    if (selection.length){
                        var pointData = selection[0];
                        if (pointData.row && pointData.column){
                            var point = {};
                            point[settings.axes.x.field || settings.axes.x] = data[pointData.row][0];
                            point[settings.axes.y.field || settings.axes.y] = data[pointData.row][pointData.column];

                            if (dataTableColumns)
                                point.column = dataTableColumns[pointData.column].id;

                            return point;
                        }
                    }

                    return null;
                }

                function highlight(e){
                    element.find("g > g > g > path").each(function(i, path){ path.style.strokeOpacity = 0.1; });
                    element.find("g > g > g > g > path").each(function(i, path){ path.style.strokeOpacity = i ? 1 : 0 });
                }
                function removeHighlight(e){
                    var selectedPaths = element.find("g > g > g > g > path");
                    if (selectedPaths.length)
                        selectedPaths.each(function(i, path){ path.style.strokeOpacity = i ? 1 : 0 });
                    else
                        element.find("path").each(function(i, path){ path.style.strokeOpacity = 1; });
                }
                // Callback that creates and populates a data table,
                // instantiates the pie chart, passes in the data and
                // draws it.
                function drawChart() {
                    if (!settings || !gotHeight)
                        return;

                    if (!data || !data.length){
                        if (chart && chart.clearChart)
                            chart.clearChart();

                        return;
                    }

					if (!dataChanged)
						return;
						
					dataTableColumns = [];

					for(var i=0; i < data[0].length; i++){
						dataTableColumns.push({ id: data[0][i], label: data[0][i], type: i ? "number" : settings.axes.x.values.type || "string" });
					}
					data.splice(0, 1);

					var chartData = new google.visualization.DataTable({ cols: dataTableColumns }),
						chartType = settings.graphType;

					chartData.addRows(data);
					dataChanged = false;

                    graphOptions.pointSize = data.length > 30 ? 0 :
                                                data.length > 20 ? 3 :
                                                    data.length > 5 ? 5 : 10;

                    graphOptions.chartArea.width = "80%";
                    graphOptions.chartArea.height = attrs.graphHeight - graphOptions.chartArea.top - 50;

                    // Instantiate and draw our chart, passing in some options.
                    if (!chart){
                        chart = new google.visualization[chartType](element[0].firstElementChild);
                        if (events){
                            angular.forEach(events, function(event){
                                google.visualization.events.addListener(chart, event.eventName, function(e){
                                    if (event.scope === "point"){
                                        eventHandler(event, getPointSelectionValue());
                                        chart.setSelection();
                                    }
                                });
                            });
                        }

                        /* Enabled this when I'm working on highlighting:
                        google.visualization.events.addListener(chart, "onmouseover", highlight);
                        google.visualization.events.addListener(chart, "onmouseout", removeHighlight);
                        google.visualization.events.addListener(chart, "select", highlight);
                        */
                    }

                    chart.draw(chartData, graphOptions);
                }
            }
        };
    });
