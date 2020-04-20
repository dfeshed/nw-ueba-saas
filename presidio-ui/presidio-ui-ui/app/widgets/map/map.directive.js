(function () {
    'use strict';

    angular.module('MapWidget')
        .directive('map', ["fsHighChartService", "COLORS_RANGE_BLUE", function (fsHighChartService, COLORS_RANGE_BLUE) {

            return {
                template: '<div class="highchart-chart-container"></div>',

                restrict: 'E',
                scope: {
                    model: '=',
                    graphSettings: '='
                },
                controller: function ($scope) {
                    var ctrl = this;

                    /**
                     * This method triggered when data changed
                     * @param chartData
                     */
                    this.refreshData = function (chartData) {
                        if (chartData) {

                            //Get all countries from server in a map, which the key is the country code
                            var chartDataIndexedByCountriesCode = this._indexBy(chartData, "code");

                            //Get the original list of countries, into updated data
                            var countriesListForUpdate = ctrl.countriesList;

                            //maxCounter used to rescale the legend / color
                            var maxCounted = 0;

                            //For each country in the countries list (dataPoint) -
                            //check if this country arrived in the data from server, and if so - set the relevant count
                            // to the country
                            countriesListForUpdate.forEach(function (dataPoint) {
                                var countryCode = dataPoint.properties['iso-a2'];
                                var chartDataEntry = chartDataIndexedByCountriesCode[countryCode];

                                //Country has data from server:
                                if (chartDataEntry) {
                                    var counted = chartDataEntry.event_count;
                                    dataPoint.value = counted;
                                    maxCounted = Math.max(maxCounted, counted);
                                } else {
                                    //Country doesn't has data. Init the countries value to zero, in case it's derty
                                    // from previous refresh
                                    dataPoint.value = 0;
                                }
                            });

                            //Destroy and recreate the chart.
                            //I would prefer to update the colorAxis instead, but because of bug
                            // "https://github.com/highslide-software/highcharts.com/issues/3207" Highchart is not
                            // updating the legend with the new colors.
                            $scope.chart.destroy();
                            $scope.chartConfig.colorAxis = this._getColorAxis(maxCounted);
                            $scope.chart = new Highcharts.Map($scope.chartConfig);
                            $scope.chart.addSeries(ctrl._getSeries(countriesListForUpdate));

                        }
                    };

                    /**
                     * Get an array and convert it to map object.
                     * Each key of the map, is property of the objects in the array, according to the 'property' param
                     * If we have 2 objects in the array with the same value in object.property, one of them will
                     * override the other.
                     *
                     * @param chartData - list objects
                     * @param property - the name of the property which will be used to as the new object key.
                     * @return {{}} - map object, each attribute contain an object from the original array.
                     * @private
                     */
                    this._indexBy = function (chartData, property) {

                        var indexedMap = {};
                        chartData.forEach(function (entry) {
                            indexedMap[entry[property]] = entry;
                        });
                        return indexedMap;
                    };

                    /**
                     * This function use to split the data into buccets, each bucket will have different color.
                     * For now, all buckets are in the same value (maxCount /5), but we consdiering to change the
                     * algorithm. See https://fortscale.atlassian.net/browse/FV-7103 also count = 0 has different
                     * color.
                     * @param maxCountValue - the maximum count for a country
                     * @return {} the color axis {dataClassColor: 'category', 	dataClasses: categoriesArray}
                     }
                     */
                    this._getColorAxis = function (maxCountValue) {

                        if (maxCountValue < 0) {
                            throw new RangeError('Score must be positive or zero');
                        }
                        var space; //= MaxValue - MinValue for each bucket

                        var categoriesArray = [{to: 1}]; //For all cases
                        var roundedMaximumValue;

                        var i;

                        if (maxCountValue <= 5) {
                            space = 1;
                        } else {
                            //maxCount value greate then 5, and categories ranges should be calucated manually.
                            //Numbers will be rounded to : less then 15 --> 15. less then 25 --> 25. other will be
                            // rounded to the nears Multiplier of 10. The same with less then 150 to 150, less then 250
                            // --> 250, other will be rounded to the nears Multiplier of 10. and the same to 1000+
                            if (maxCountValue >= 10) {
                                var countNumberOfDigits = maxCountValue.toString().length;
                                var nearest10pow = Math.pow(10, countNumberOfDigits);
                                //Percntage- the given maximum as part of the next pow of ten.
                                var maxCountValueAsPercentage = maxCountValue / nearest10pow;

                                var STEPS_PERCENTAGE_ARRAY = [15, 25, 30, 40, 50, 60, 70, 80, 90];

                                for (i = 0; i < STEPS_PERCENTAGE_ARRAY.length && !roundedMaximumValue; i++) {
                                    if (maxCountValueAsPercentage < (STEPS_PERCENTAGE_ARRAY[i] / 100)) {
                                        roundedMaximumValue = STEPS_PERCENTAGE_ARRAY[i] / 100 * nearest10pow;
                                    }
                                }
                                if (!roundedMaximumValue) {
                                    roundedMaximumValue = nearest10pow;
                                }
                            } else { //For maxCountValue 6-9
                                roundedMaximumValue = 10;
                            }
                            space = roundedMaximumValue / 5;
                            categoriesArray.push({
                                from: 1,
                                to: space
                            });
                        }

                        for (i = 1; i < 5; i++) {
                            categoriesArray.push({
                                from: space * i,
                                to: space * (i + 1)
                            });
                        }

                        return {
                            dataClassColor: 'category',
                            dataClasses: categoriesArray
                        };
                    };

                    /**
                     * Get high chart series of data.
                     * @param data  -data for series
                     * @return {{data: *, name: string, dataLabels: {enabled: boolean, format: string}, states: {hover:
                     *     {color: string}}}}
                     */
                    this._getSeries = function (data) {
                        return {
                            data: data,
                            name: 'World',
                            dataLabels: {
                                enabled: true,
                                format: '{point.properties.name}'
                            },
                            states: {
                                hover: {
                                    color: '#BADA55'
                                }
                            }
                        };
                    };

                },
                link: function postLink (scope, element, attrs, ctrl) {

                    //Set unique ID for the div, Highchart need it.
                    var chartId = Math.random().toString(36).slice(2);
                    //Set unique ID on the root element of the template
                    element.children('.highchart-chart-container').attr('id', chartId);

                    ctrl.countriesList = Highcharts.geojson(Highcharts.maps['custom/world']);

                    //Set default data - each point will have value of 0, and the flag will be the country code

                    ctrl.countriesList.forEach(function (dataPoint) {
                        dataPoint.value = 0;
                        dataPoint.flag = dataPoint.properties["hc-key"];
                    });

                    scope.chartConfig = {
                        chart: {
                            "renderTo": chartId

                        },
                        colors: COLORS_RANGE_BLUE,
                        //On small screen- use default legend, else set the legend location to right
                        legend: $('.highchart-chart-container').width() < 400 ? {} : {
                            layout: 'vertical',
                            align: 'right',
                            verticalAlign: 'middle',
                            valueDecimals: 0,
                            title: {
                                text: 'Events per Country'
                            }
                        },
                        //Tooltip will display country name, country value and flag.
                        //The tooltip always be on the left side
                        tooltip: {
                            backgroundColor: 'none',
                            borderWidth: 0,
                            shadow: false,
                            useHTML: true,
                            padding: 0,
                            enabled: true,
                            positioner: function () {
                                return {x: 0, y: 250};
                            },
                            pointFormat: '<span class="f32"><span class="flag {point.flag}"></span></span>' +
                            ' {point.name}: <b>{point.value}</b>'
                        },
                        colorAxis: ctrl._getColorAxis(0),

                        mapNavigation: {
                            enabled: true,
                            buttonOptions: {
                                verticalAlign: 'bottom'
                            }
                        },

                        plotOptions: {
                            map: {
                                states: {
                                    hover: {
                                        color: '#EEDD66'
                                    }
                                }
                            }
                        },
                        series: [ctrl._getSeries(ctrl.countriesList)]
                    };

                    scope.chartConfig =
                        $.extend(true, fsHighChartService.getBasicChartConfiguration(), scope.chartConfig);

                    //Create the chart object and save it on the scope
                    scope.$applyAsync(function () {
                        scope.chart = new Highcharts.Map(scope.chartConfig);
                    });

                    scope.$watch('model', function (chartData) {
                        ctrl.refreshData(chartData);

                    });
                }
            };

        }]);
}());
