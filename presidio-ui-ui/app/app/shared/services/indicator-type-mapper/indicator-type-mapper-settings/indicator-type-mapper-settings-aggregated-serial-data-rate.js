/**
 * This file will contain mapping settings for serial chart used for aggregation indicators
 */

(function () {
    'use strict';

    angular.module('Fortscale.shared.services.indicatorTypeMapper').

    /**
     * Filter which generate the relevant message key from the anomalyTypeFieldName and title or axisYtitle
     */
    filter('buildAggregatedKey', function () {
        var prefix = "evidence.aggregated.";
        return function (anomalyTypeFieldName, postfix) {
            return prefix + anomalyTypeFieldName + "." + postfix;
        };
    })
        .factory('indicatorTypeMapper.aggregated-serial-data-rate', ['$filter',
            'indicatorTypeMapper.commonQueryParams',

            function ($filter, commonQueryParams) {
                return {
                    settings: {
                        scatterSettings: {
                            params: commonQueryParams.entityTypeAnomalyTypeCount30days,
                            templates: {
                                titles: {
                                    'Title-1': 'VPN Exfiltration Rate for {{entityName}} (Last 30 Days)'
                                }
                            },
                            sortData: data => {
                                return _.orderBy(data, [dataItem => parseInt(dataItem.keys[0], 10)], ['asc']);
                            },
                            dataAdapter: (indicator, dataItem) => {
                                let chartItem = {
                                    category: new Date(parseInt(dataItem.keys[0], 10)),
                                    originalCategory: dataItem.keys[0],
                                    value: dataItem.value
                                };

                                if (dataItem.anomaly) {
                                    chartItem.color = '#FF0000';
                                }

                                return chartItem;
                            },
                            handlers: {
                                "clickGraphItem": function (indicator, item) {
                                    // indicatorChartTransitionUtil.go('columnEntity', indicator, item);
                                }
                            },
                            chartSettings: {
                                "type": "serial",
                                "categoryField": "category",
                                "startDuration": 1,
                                "color": "#666666",
                                "fontFamily": "'Open Sans', sans-serif",
                                "export": {
                                    "enabled": true
                                },
                                "categoryAxis": {
                                    "axisColor": "#1C1A1A",
                                    "color": "#666666",
                                    "fontSize": 10,
                                    "gridColor": "#FFFFFF",
                                    "parseDates": true,
                                    "equalSpacing": true,
                                    'minPeriod': 'hh',
                                    dateFormats: [{
                                        period: 'fff',
                                        format: 'JJ:NN:SS'
                                    }, {
                                        period: 'ss',
                                        format: 'JJ:NN:SS'
                                    }, {
                                        period: 'mm',
                                        format: 'JJ:NN'
                                    }, {
                                        period: 'hh',
                                        format: 'MMM DD JJ:NN'
                                    }, {
                                        period: 'DD',
                                        format: 'MMM DD'
                                    }, {
                                        period: 'WW',
                                        format: 'MMM DD'
                                    }, {
                                        period: 'MM',
                                        format: 'MMM YYYY'
                                    }, {
                                        period: 'YYYY',
                                        format: 'MMM YYYY'
                                    }]
                                },
                                "chartScrollbar": {
                                    "enabled": true
                                },
                                "trendLines": [],
                                "graphs": [
                                    {
                                        "colorField": "color",
                                        "columnWidth": 0.6,
                                        "fillAlphas": 1,
                                        "fillColors": "#C9E6F9",
                                        "id": "AmGraph-1",
                                        "lineColor": "#C9E6F9",
                                        "lineColorField": "color",
                                        "title": "graph 1",
                                        "type": "column",
                                        "valueField": "value",
                                        balloonFunction: (dataItem) => {
                                            return moment(dataItem.dataContext.category).utc().format('YYYY MMM DD HH:mm') + ' : ' +
                                                $filter('prettyBytes')(dataItem.values.value)+'/s';
                                        }
                                    }
                                ],
                                "guides": [],
                                "valueAxes": [
                                    {
                                        "id": "ValueAxis-1",
                                        "axisThickness": 0,
                                        "color": "#666666",
                                        "showFirstLabel": false,
                                        "showLastLabel": false,
                                        "tickLength": -1,
                                        "titleBold": false,
                                        "titleColor": "#666666",
                                        "titleFontSize": 14,
                                        "precision": 0,
                                        "baseValue": -10,
                                        "title": 'Rate',
                                        labelFunction: (value) => {
                                            return $filter('prettyBytes')(value)+'/s';
                                        }
                                    }
                                ],
                                "allLabels": [],
                                "balloon": {},
                                "titles": [
                                    {
                                        "id": "Title-1",
                                        "size": 15
                                    }
                                ]
                            }
                        }
                    }
                };
            }]
        );
}());

