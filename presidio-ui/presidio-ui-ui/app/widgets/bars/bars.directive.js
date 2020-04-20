(function () {
    'use strict';

    angular.module('BarsWidget').directive('bars', ["utils", "fsHighChartService", 'SCORE_COLOR_META_DATA',
            function (utils, fsHighChartService, SCORE_COLOR_META_DATA) {

                //The chart height will be FIXED_SERIES_HEIGHT_IN_PIXEL * number of users / computers
                var FIXED_SERIES_HEIGHT_IN_PIXEL = '45';

                return {
                    template: '<div class="highchart-bar-chart-container-parent">' +
                    '<div class="highchart-bar-chart-container"></div></div>',

                    restrict: 'E', scope: {
                        model: '=', graphSettings: '='
                    }, controller: function ($scope) {
                        var ctrl = this;

                        this.refreshData = function (chartData) {
                            if (chartData && chartData.length > 0) {


                                //userOrComputerData - map which the key is name of user or computer,
                                // and for each user his count for each severity
                                var userOrComputerData = this._getPointByUserAndSeverity(chartData);

                                var dataForChart = this._prepareDataForChart(userOrComputerData);

                                //We need to re-calculate the hight depened on data.
                                //So we have to remove the chart, reset the container size, and re-create the cahrt

                                //Destroy current chart and handle contain height
                                var chartContainerDivId = $scope.chart.renderTo.id;
                                var chartContainer = $("#" + chartContainerDivId);
                                var chartAreaSizeStrWithPX = chartContainer.parent().css('height');
                                chartAreaSizeStrWithPX =
                                    chartAreaSizeStrWithPX.slice(0, chartAreaSizeStrWithPX.length - 2);
                                var containerSize = Math.max(chartData.length * FIXED_SERIES_HEIGHT_IN_PIXEL,
                                    Number(chartAreaSizeStrWithPX));
                                chartContainer.css('height', containerSize);

                                //Save which sevirities to show, before destroy and rebuild chart
                                for (var i = 0; i < $scope.chartConfig.series.length; i++) {
                                    $scope.chartConfig.series[i].visible = $scope.chart.series[i].visible;
                                }
                                $scope.chart.destroy();

                                //Re-Create the chart object and save it on the scope

                                $scope.chart = new Highcharts.Chart($scope.chartConfig);
                                $scope.chart.xAxis[0].setCategories(dataForChart.categories);

                                $scope.chart.series[0]
                                    .update({data: dataForChart.series[SCORE_COLOR_META_DATA.critical.name]});
                                $scope.chart.series[1]
                                    .update({data: dataForChart.series[SCORE_COLOR_META_DATA.high.name]});
                                $scope.chart.series[2]
                                    .update({data: dataForChart.series[SCORE_COLOR_META_DATA.medium.name]});
                                $scope.chart.series[3]
                                    .update({data: dataForChart.series[SCORE_COLOR_META_DATA.low.name]});

                            }
                        };

                        /*
                         This method prepare the categories and the data for each series in the chart.
                         @param userOrComputerData - object of the form - [user or coomputer name][seveiry] = counts

                         @return - {
                         categories - array of user or computer names
                         series - array of 4 series, one per serviriy (low, medium, high, critical).
                         each series contains array of counts
                         }
                         */
                        this._prepareDataForChart = function (userOrComputerData) {

                            var categories = [];
                            var dataForChartSeries = {};
                            dataForChartSeries[SCORE_COLOR_META_DATA.critical.name] = []; //Critical
                            dataForChartSeries[SCORE_COLOR_META_DATA.high.name] = []; //High
                            dataForChartSeries[SCORE_COLOR_META_DATA.medium.name] = []; //Medium
                            dataForChartSeries[SCORE_COLOR_META_DATA.low.name] = []; //Low

                            angular.forEach(userOrComputerData, function (sevirityAndCount, key) {
                                categories.push(key);

                                ctrl._pushPointForChartSeries(SCORE_COLOR_META_DATA.critical.name, sevirityAndCount,
                                    dataForChartSeries);
                                ctrl._pushPointForChartSeries(SCORE_COLOR_META_DATA.high.name, sevirityAndCount,
                                    dataForChartSeries);
                                ctrl._pushPointForChartSeries(SCORE_COLOR_META_DATA.medium.name, sevirityAndCount,
                                    dataForChartSeries);
                                ctrl._pushPointForChartSeries(SCORE_COLOR_META_DATA.low.name, sevirityAndCount,
                                    dataForChartSeries);
                            });

                            return {
                                categories: categories, series: dataForChartSeries
                            };

                        };

                        this._pushPointForChartSeries = function (seriesName, sourceArray, destArray) {

                            //Set the point data if exists, or zero if not exists
                            var point = sourceArray[seriesName] ? sourceArray[seriesName] : {y: 0, percentage: "0"};
                            destArray[seriesName].push(point);

                        };
                        /**
                         * @param chartData - array of data objects
                         * @return {} two dimensions array [user or coomputer name][seveiry] = counts
                         */
                        this._getPointByUserAndSeverity = function (chartData) {
                            var catagorizedData = {};
                            //For each user or computer - create and point with the value and percentage.
                            //entry - represents entry of original data from the original array
                            chartData.forEach(function (entry) {

                                //Add the point to the temporal array catagorizedData- split the data between the
                                // buckets [entity name][critical / high/  medium/low]
                                if (!catagorizedData[entry.label]) {
                                    catagorizedData[entry.label] = {};
                                }

                                var percentage = (Math.round(entry._percent * 100) / 100).toFixed(2); //Truncate 2
                                                                                                      // places after
                                                                                                      // comma
                                catagorizedData[entry.label][entry.severity.toLowerCase()] =
                                {y: entry.event_count, percentage: percentage};
                            });

                            return catagorizedData;
                        };

                    }, link: function postLink (scope, element, attrs, ctrl) {

                        //Set unique ID for the div, Highchart need it.
                        var chartId = Math.random().toString(36).slice(2);
                        //Set unique ID on the root element of the template
                        element.children().children('.highchart-bar-chart-container').attr('id', chartId);

                        scope.chartConfig = {

                            "chart": {
                                "renderTo": chartId, "type": "bar"
                            },

                            legend: {
                                enabled: true,
                                layout: 'vertical',
                                align: 'right',
                                verticalAlign: 'top',
                                x: 10,
                                y: 40,
                                borderWidth: 0
                            }, "plotOptions": {
                                bar: {

                                    minPointLength: 5, dataLabels: {
                                        enabled: true, formatter: function () {

                                            if (!this.point.percentage) {
                                                this.point.percentage = "0.00";
                                            }
                                            return this.point.y + " (" + this.point.percentage + "%)";
                                        }, style: {
                                            fontWeight: 'bold'
                                        }, x: 0, y: 0, align: 'right'

                                    }
                                }
                            }, scrollbar: {
                                enabled: true
                            }, yAxis: {
                                min: 0, maxPadding: 0, gridLineWidth: 0, title: {
                                    text: '', align: 'high'
                                }, labels: {
                                    overflow: 'justify', enabled: false
                                }, minTickInterval: 1, tickInterval: 1, endOnTick: true, minRange: 1

                            }, xAxis: {
                                categories: [], title: {
                                    text: null
                                }
                            }, //Always 4 series - critical, high, medium, low
                            series: [{
                                name: utils.strings.capitalize(SCORE_COLOR_META_DATA.critical.name),
                                data: [],
                                color: SCORE_COLOR_META_DATA.critical.color
                            }, {
                                name: utils.strings.capitalize(SCORE_COLOR_META_DATA.high.name),
                                data: [],
                                color: SCORE_COLOR_META_DATA.high.color
                            }, {
                                name: utils.strings.capitalize(SCORE_COLOR_META_DATA.medium.name),
                                data: [],
                                color: SCORE_COLOR_META_DATA.medium.color,
                                visible: false
                            }, {
                                name: utils.strings.capitalize(SCORE_COLOR_META_DATA.low.name),
                                data: [],
                                color: SCORE_COLOR_META_DATA.low.color,
                                visible: false
                            }]
                        };

                        $.extend(true, scope.chartConfig, fsHighChartService.getBasicChartConfiguration());

                        //Create the chart object and save it on the scope
                        scope.$applyAsync(function () {
                            scope.chart = new Highcharts.Chart(scope.chartConfig);
                        });

                        scope.$watch('model', function (chartData) {
                            ctrl.refreshData(chartData);

                        });
                    }
                };
            }]);

}());
