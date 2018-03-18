(function () {
    'use strict';

    angular.module("ScatterPlotWidget").directive("scatterPlot",
        ["$rootScope", "utils", "fsHighChartService", "SCORE_COLOR_META_DATA",
            function ($rootScope, utils, fsHighChartService, SCORE_COLOR_META_DATA) {

                //Constants Decleration
                var MIN_NUMBER_OF_DAYS = 7;
                var MILISECONDS_IN_DAY = 24 * 3600 * 1000;

                var symbols = [{
                    "shape": 'circle',
                    "color": '#DE07CC'
                },
                    {
                        "shape": 'square',
                        "color": '#07874D'
                    },
                    {
                        "shape": 'triangle',
                        "color": '#3DDDF2'
                    }, {
                        "shape": 'triangle-down',
                        "color": '#000000'
                    }, {
                        "shape": 'diamond',
                        "color": '#1E25E8'
                    }];

                /**
                 * Note for the directive:
                 *    Currently we are not using HIGHCHART-NG http://ngmodules.org/modules/highcharts-ng
                 *    If you need extra watchers and data binding, or any other funcenelity consider using highcharts-ng
                 *    instead of extending this code.
                 */
                return {

                    template: '<div class="highchart-chart-container" ></div>',

                    restrict: 'E',
                    scope: {
                        model: '=',
                        graphSettings: '='
                    },
                    //Link function update the UI elements
                    controller: function ($scope) {
                        /**
                         *
                         * @returns {boolean} true axis X look only on the date part in a day resolution and ingore
                         *     hour/minute/second
                         */
                        var ctrl = this;
                        this.xAxisIgnoreHours = function () {
                            return !$scope.graphSettings.yField;
                        };

                        //Change the first and last days according to the data
                        this.rescaleXaxis = function (chartData, xField) {
                            var domain = d3.extent(chartData, function (d) {
                                return d[xField];
                            });

                            var m1 = utils.date.getMoment(domain[0]).startOf('day'),
                                m2 = utils.date.getMoment(domain[1]).endOf('day'),
                                diff = Math.abs(m1.diff(m2, "days"));

                            if (diff < MIN_NUMBER_OF_DAYS) {
                                var daysToAdd = Math.floor((MIN_NUMBER_OF_DAYS - diff) / 2);
                                m1.subtract(daysToAdd, "days");
                                m2.add(daysToAdd, "days");
                            }
                            domain[0] = m1.valueOf();
                            domain[1] = m2.valueOf();

                            $scope.chart.xAxis[0].update({
                                min: domain[0],
                                max: domain[1]
                            });
                        };

                        /**
                         * Iterate the array of SCORE_COLOR_META_DATA and return the name of the SCORE COLOR (LOW,
                         * MEDIUM, ETC...) which the given store match
                         * @param score
                         * @returns {string}
                         * @throws InternalError if given score is out of range
                         */
                        this.getRiskKeyByScore = function (score) {
                            for (var propertyName in SCORE_COLOR_META_DATA) {
                                if (SCORE_COLOR_META_DATA.hasOwnProperty(propertyName)) {
                                    var scoreColorMetaData = SCORE_COLOR_META_DATA[propertyName];
                                    if (score >= scoreColorMetaData.minScore &&
                                        score < scoreColorMetaData.maxScore) {
                                        return propertyName;
                                    }
                                }
                            }
                            //If nothing match, throw error
                            throw new RangeError("Score must be 0-100");

                        };

                        /**
                         *
                         * @param data - array of point (x,y) for highchart
                         * @param color - the required color for the series - affect legend and points
                         * @param name - the name of the series- affect the legend
                         * @param markerSymbol - the symbole for the point
                         * @param seriesIndex - order of the series in the chart & legend. Start with 0.
                         */
                        this.addOrUpdateSeries = function (data, color, name, markerSymbol, seriesIndex) {
                            //Create the series object
                            var highchartSeriesObject = {
                                "data": data,
                                "color": color,
                                "name": name,
                                "marker": {
                                    "symbol": markerSymbol
                                }

                            };

                            if ($scope.chart.series && $scope.chart.series[seriesIndex]) {
                                //Update series if series with the same index already exists
                                $scope.chart.series[seriesIndex].update(
                                    highchartSeriesObject
                                );
                            } else {
                                //Add new series if the index is not exists
                                $scope.chart.addSeries(
                                    highchartSeriesObject
                                );
                            }
                        };

                        this.refreshData = function (chartData) {

                            function populateEntityTypePoints (point) {
                                entityTypePoints.push(point);
                            }

                            if (chartData && chartData.length > 0) {
                                /**
                                 * xField, yField, colorField - column names which contain the name of columns which
                                 * have the relevant data for each dimension
                                 */
                                var xField = $scope.graphSettings.timeField;
                                var yField = $scope.graphSettings.yField || $scope.graphSettings.timeFieldHour;
                                var colorField = $scope.graphSettings.colorField;

                                //catagorizedData - The manipulated data will be stores in catagorizedData object.
                                // 					The structure of the data should be:
                                // catagorizedData.entityType[priority] When entityType is VPN Session, SSH, etc... and
                                // priority is 0..3 when 0 is critical and 3 is low

                                var catagorizedData = this._getDataCatagorized(chartData, xField, yField, colorField);

                                //Add / Update the series data
                                var entityTypesArr = Object.keys(catagorizedData);
                                var riskTypesArr = Object.keys(SCORE_COLOR_META_DATA);
                                var isMultiSeries = entityTypesArr.length > 1;
                                var i;
                                var data, color, name, symbol;

                                if (!isMultiSeries) {
                                    //Case 1- single series (single data source)

                                    //Get the all the risk series on the single data source
                                    var entityTypeDataPerRisk = catagorizedData[entityTypesArr[0]];
                                    for (i = 0; i < riskTypesArr.length; i++) {
                                        var riskName = [riskTypesArr[i]];
                                        data = entityTypeDataPerRisk[riskName];
                                        color = SCORE_COLOR_META_DATA[riskName].color;
                                        name = utils.strings.capitalize(SCORE_COLOR_META_DATA[riskName].name);
                                        symbol = symbols[0].shape;
                                        this.addOrUpdateSeries(data, color, name, symbol, i);
                                    }

                                } else {
                                    //Case 2 - multi series (multi data source)

                                    for (i = 0; i < entityTypesArr.length; i++) {
                                        var entityType = [entityTypesArr[i]];
                                        var entityTypePoints = [];
                                        //Aggregate all risk series into single array of the data source

                                        for (var j = 0; j < riskTypesArr.length; j++) {
                                            catagorizedData[entityType][riskTypesArr[j]]
                                                .forEach(populateEntityTypePoints);
                                        }

                                        data = entityTypePoints;
                                        color = symbols[i].color;
                                        name = entityType;
                                        symbol = symbols[i].shape;
                                        this.addOrUpdateSeries(data, color, name, symbol, i);

                                    }
                                }

                                this.rescaleXaxis(chartData, xField);
                                $scope.chart.redraw(); //Redraw required because axis x' length changed and I need to
                                                       // resize the chart

                            }
                        };

                        /**
                         * This method convert the data into two dimmensions array [entityName][riskLevel]
                         * name
                         * @param chartData - array of data objects
                         * @param xField - the name of field for Axis X
                         * @param yField - the name of field for Axis Y
                         * @param colorField - the name of field for the color
                         * @private
                         * @return {} two dimensions array [entityName][riskLevel]
                         */
                        this._getDataCatagorized = function (chartData, xField, yField, colorField) {
                            var catagorizedData = {};
                            //For each data in the original array, manipulate the data in "point" variable, and add the
                            // point to catagorizedData. entry - represents entry of original data from the original
                            // array
                            chartData.forEach(function (entry) {

                                //Get the x value, calculated from Date object.
                                var timeValue;
                                if (ctrl.xAxisIgnoreHours()) {
                                    //Normalize the time to date only, in resolution of one day
                                    timeValue = utils.date.getMoment(entry[xField]).startOf('day').toDate();
                                } else {
                                    timeValue = entry[xField];
                                }

                                var point = {
                                    x: timeValue.valueOf(),
                                    y: entry[yField] ? entry[yField] : 0,
                                    entry: entry

                                };

                                var riskTypesArr = Object.keys(SCORE_COLOR_META_DATA);
                                //Add the point to the temporal array catagorizedData- split the data between the
                                // buckets [entity name][critical / high/  medium/low]
                                if (!catagorizedData[entry.type]) {

                                    catagorizedData[entry.type] = {};
                                    //Init catagorizedData[entry.type][riskTypesArr[i]] as empty array
                                    for (var i = 0; i < riskTypesArr.length; i++) {
                                        catagorizedData[entry.type][riskTypesArr[i]] = [];
                                    }

                                }
                                var riskKeys = ctrl.getRiskKeyByScore(entry[colorField]);
                                catagorizedData[entry.type][riskKeys].push(point);

                            });

                            return catagorizedData;
                        };
                    },
                    link: function postLink (scope, element, attrs, ctrl) {



                        //Set unique ID for the div, Highchart need it.
                        var chartId = Math.random().toString(36).slice(2);
                        //Set unique ID on the root element of the template
                        element.children('.highchart-chart-container').attr('id', chartId);
                        if (scope.graphSettings.height) {
                            element.css('height', scope.graphSettings.height + "px");
                        }

                        //Prepare configurations for chartConfig:
                        var calculatedMinHeight = scope.graphSettings.scales.y.domain ?
                            scope.graphSettings.scales.y.domain[0] : scope.graphSettings.scales.y.minValue;
                        var calculatedMaxHeight = scope.graphSettings.scales.y.domain ?
                            scope.graphSettings.scales.y.domain[1] : undefined;
                        var calculatedYAxistickInterval = scope.graphSettings.scales.y.ticks ?
                            scope.graphSettings.scales.y.ticks.interval : undefined;
                        var calculatedXAxistickInterval = ctrl.xAxisIgnoreHours() ? MILISECONDS_IN_DAY : undefined;

                        //Create the configuration chart for the scatterPlot;
                        var chartConfig = {

                            "chart": {
                                "renderTo": chartId,
                                "type": "scatter",
                                "zoomType": 'xy',
                                "height": scope.graphSettings.height,
                                events: {
                                    load: function () {
                                        //The default behviour for highchart is so the tooltip displayed on hover.
                                        //I have to change it to be onclick only, therefore I have to override the
                                        // default tooltip after page rendered.
                                        this.myTooltip = new Highcharts.Tooltip(this, this.options.tooltip);
                                    }
                                }
                            },
                            rangeSelector: {
                                selected: 1
                            },

                            legend: {
                                enabled: true,
                                layout: 'vertical',
                                align: 'right',
                                verticalAlign: 'top',
                                x: 10,
                                y: 40,
                                borderWidth: 0
                            },
                            "plotOptions": {
                                scatter: {
                                    lineWidth: 0
                                },
                                area: {
                                    marker: {
                                        enabled: false
                                    },
                                    cursor: 'Pointer',
                                    stacking: 'normal'
                                },
                                series: {

                                    stickyTracking: false,
                                    cursor: 'pointer',
                                    events: {
                                        //The default behvioud for highchart is so the tooltip displayed on hover.
                                        //I have to change it to be onclick only. I add to add events on all series
                                        // that catch click on item, and mouseout on item
                                        click: function (evt) {
                                            this.chart.myTooltip.refresh(evt.point, evt);
                                        },
                                        mouseOut: function () {
                                            this.chart.myTooltip.hide();
                                        }
                                    }

                                }
                            },

                            yAxis: {
                                title: {
                                    text: '<b>' + scope.graphSettings.axes.y.label + '</b>'
                                },
                                //The min value might come from y.domain[0] or y.minValue. Depend on configuratin
                                min: calculatedMinHeight,
                                //Max value not always given.
                                max: calculatedMaxHeight,
                                tickInterval: calculatedYAxistickInterval
                            },
                            xAxis: {
                                title: {
                                    enabled: true,
                                    text: '<b>' + scope.graphSettings.axes.x.label + '</b>'

                                },
                                type: 'datetime',
                                tickInterval: calculatedXAxistickInterval,  //One line per day
                                gridLineWidth: 0.5,
                                gridLineColor: "#E2E3E1"
                            },
                            tooltip: {

                                useHTML: true,

                                //Function which generate the tooltip as HTML
                                formatter: function () {

                                    var entry = this.point.entry; //All the data of the point
                                    var tooltipHtml = '<table>';
                                    var toolTipFields = scope.graphSettings.onSelect.actionOptions.table.rows;

                                    // Create a Set to prevent field duplications
                                    var toolTipSet = _.indexBy(toolTipFields, 'label');

                                    //For each field given in the tooltip descriptor, we add a line to tooltip with
                                    // "label: value". We are using utils.strings.parseValue to evaluate the label and
                                    // value from the given string.
                                    _.each(toolTipSet, function (toolTipLine) {

                                        var label = utils.strings.parseValue(toolTipLine.label, entry);
                                        var value = '<b>' + utils.strings.parseValue(toolTipLine.value, entry) + '</b>';
                                        tooltipHtml =
                                            tooltipHtml + '<tr><td>' + label + ': </td><td>' + value + '</td></tr>';

                                    });

                                    tooltipHtml += "</table>";
                                    return tooltipHtml;
                                }
                            }

                        };

                        $.extend(true, chartConfig, fsHighChartService.getBasicChartConfiguration());

                        //Create the chart object and save it on the scope
                        scope.$applyAsync(function () {
                            scope.chart = new Highcharts.Chart(chartConfig);
                        });

                        //The data of the chart mostly updated after the chart was rendered, and when clicking refresh.
                        //We are using watch to identify data changes.
                        scope.$watch('model', function (chartData) {
                            ctrl.refreshData(chartData);

                        });

                    }
                };
            }]);
}());
