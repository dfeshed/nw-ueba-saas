/**
 * This file contains setting for both column charts in any indicator which display
 * two column charts
 */

(function () {
    'use strict';

    //Adding map of the anomalyTypeFieldName to pretty name,
    //For each chart title (chart1, chart2)
    var indicatorsChartsTitles = {
        normalized_src_machine: {
            title_chart1: 'Source Devices',
            title_chart2: 'Users for Source Device',
            axisY_chart1: 'Number Of Devices',
            axisY_chart2: 'Number Of Users'
        },
        normalized_dst_machine: {
            title_chart1: 'Target Devices',
            title_chart2: 'Users for Target Device',
            axisY_chart1: 'Number Of Devices',
            axisY_chart2: 'Number Of Users'
        },
        country: {
            title_chart1: 'Countries',
            title_chart2: 'Users for',
            axisY_chart1: 'Number Of Countries',
            axisY_chart2: 'Number Of Users'
        },
        db_object: {
            title_chart1: 'DB Objects',
            title_chart2: 'Users for',
            axisY_chart1: 'Number Of DB Objects',
            axisY_chart2: 'Number Of Users'
        },
        db_username: {
            title_chart1: 'DB Username',
            title_chart2: 'Users for',
            axisY_chart1: 'Number DB Usernames',
            axisY_chart2: 'Number Of Users'
        },
        email_sender: {
            title_chart1: 'Sender',
            axisY_chart1: 'Number Of Emails Sent'
        },

        email_recipient_domain: {
            title_chart1: 'Email Recipient Domains',
            title_chart2: 'Users for Email Recipient Domain',
            axisY_chart1: 'Number Of Domains',
            axisY_chart2: 'Number Of Emails Sent'
        },

        account_management_change_anomaly: {
            title_chart1: 'Account Management Changes',
            axisY_chart1: 'Number Of Account Management Changes'

        }

    };

    angular.module('Fortscale.shared.services.indicatorTypeMapper')
        .filter('fetchDualIndicatorMapProperties', function () {
            return function (anomalyTypeFieldName, chartOrder) {
                //Adding a filter which extract the name of the titles, according to
                //anomalyTypeFieldName and chart title
                if (anomalyTypeFieldName && indicatorsChartsTitles[anomalyTypeFieldName]) {
                    return indicatorsChartsTitles[anomalyTypeFieldName][chartOrder];
                } else if (anomalyTypeFieldName) {
                    return anomalyTypeFieldName;
                }
            };
        })
        .factory('indicatorTypeMapper.dual-column', ['$filter',
            'indicatorTypeMapper.commonQueryParams',
            'fsIndicatorGraphsHandler',
            'indicatorChartTransitionUtil',
            function ($filter, commonQueryParams, fsIndicatorGraphsHandler, indicatorChartTransitionUtil) {


                return {
                    settings: {
                        firstColumn: { //Left column
                            params: commonQueryParams.entityTypeAnomalyTypeCount,
                            styleSettings: {},
                            templates: {
                                titles: {
                                    'Title-1': '{{dataEntitiesIds[0]|entityIdToName}} ' +
                                    '{{anomalyTypeFieldName | fetchDualIndicatorMapProperties: ' +
                                    '\'title_chart1\'}} for {{entityName}} (Last 90 Days)'
                                },
                                valueAxes: {
                                    'ValueAxis-1': '{{anomalyTypeFieldName | fetchDualIndicatorMapProperties: ' +
                                    '\'axisY_chart1\'}}'
                                }
                            },
                            sortData: data => {
                                return _.orderBy(data, ['anomaly', 'value'], ['asc', 'desc']);
                            },
                            dataAdapter:  (indicator, dataItem) => {
                                let chartItem = {
                                    category: $filter('anomalyTypeFormatter')(dataItem.keys[0], indicator),
                                    originalCategory: dataItem.keys[0],
                                    value: dataItem.value
                                };

                                if (dataItem.anomaly) {
                                    chartItem.color = '#CC3300';
                                }

                                return chartItem;
                            },
                            handlers: {
                                "clickGraphItem": function (indicator, item) {
                                    indicatorChartTransitionUtil.go('columnAnomaly', indicator, item);
                                }
                            },
                            chartSettings: {
                                "type": "serial",
                                "categoryField": "category",
                                "plotAreaBorderColor": "#F0F7F8",
                                "colors": [
                                    "#9EC8E4"
                                ],
                                "startDuration": 1,
                                "backgroundColor": "#F0F7F8",
                                "fontFamily": "Open Sans",
                                "fontSize": 12,
                                "export": {
                                    "enabled": true
                                },
                                "categoryAxis": {
                                    "gridPosition": "start",
                                    "axisColor": "#BCB5B5",
                                    "gridColor": "#FFFFFF",
                                    "title": "",
                                    "fontFamily": "'Open Sans', sans-serif",
                                    fontSize: 10,
                                    autoWrap: true,
                                    labelFunction: (value, valueString, axis) => {
                                        return value;
                                    }
                                },
                                "trendLines": [],
                                "graphs": [
                                    {
                                        "colorField": "color",
                                        "columnWidth": 0,
                                        "fillAlphas": 1,
                                        "fillColors": "",
                                        "fixedColumnWidth": 20,
                                        "fontSize": -1,
                                        "id": "AmGraph-1",
                                        "lineColor": "",
                                        "lineColorField": "color",
                                        "title": "graph 1",
                                        "type": "column",
                                        "valueField": "value",
                                        showHandOnHover: true
                                    }
                                ],
                                "valueAxes": [
                                    {
                                        "id": "ValueAxis-1",
                                        "axisAlpha": 0,
                                        "axisColor": "#",
                                        "fontSize": 12,
                                        "gridColor": "#666666",
                                        "labelOffset": -1,
                                        "showFirstLabel": false,
                                        "titleColor": "#666666",
                                        "fontFamily": "'Open Sans', sans-serif",
                                        "minimum": 0
                                    }
                                ],
                                "titles": [
                                    {
                                        "id": "Title-1",
                                        "fontFamily": "'Open Sans', sans-serif",
                                        "color": "#666666",
                                        "size": 12
                                    }
                                ]

                            }
                        },
                        secondColumn: { //Right column
                            params: commonQueryParams.anomalyTypeEntityTypeCount,
                            styleSettings: {
                            },
                            templates: {
                                titles: {
                                    'Title-1': '{{dataEntitiesIds[0]|entityIdToName}} ' +
                                    '{{anomalyTypeFieldName | fetchDualIndicatorMapProperties: ' +
                                    '\'title_chart2\'}} {{anomalyValue}} (Last 90 Days)'
                                },
                                valueAxes: {
                                    'ValueAxis-2': '{{anomalyTypeFieldName | fetchDualIndicatorMapProperties: ' +
                                    '\'axisY_chart2\'}}'
                                }

                            },
                            sortData: data => {
                                return _.orderBy(data, ['anomaly', 'value'], ['asc', 'desc']);
                            },
                            dataAdapter:  (indicator, dataItem) => {
                                let chartItem = {
                                    category: $filter('anomalyTypeFormatter')(dataItem.keys[0], indicator),
                                    originalCategory: dataItem.keys[0],
                                    value: dataItem.value
                                };

                                if (dataItem.anomaly) {
                                    chartItem.color = '#CC3300';
                                }

                                return chartItem;
                            },
                            handlers: {
                                "clickGraphItem": function (indicator, item) {
                                    indicatorChartTransitionUtil.go('columnEntity', indicator, item);
                                }
                            },
                            chartSettings: {
                                "type": "serial",
                                "categoryField": "category",
                                "plotAreaBorderColor": "#F0F7F8",
                                "colors": [
                                    "#9EC8E4"
                                ],
                                "startDuration": 1,
                                "backgroundColor": "#F0F7F8",
                                "fontFamily": "Open Sans",
                                "fontSize": 12,
                                "export": {
                                    "enabled": true
                                },
                                "categoryAxis": {
                                    "gridPosition": "start",
                                    "axisColor": "#BCB5B5",
                                    "gridColor": "#FFFFFF",
                                    "title": "",
                                    "fontFamily": "'Open Sans', sans-serif",
                                    fontSize: 10,
                                    autoWrap: true,
                                    labelFunction: (value, valueString, axis) => {
                                        return value;
                                    }
                                },
                                "trendLines": [],
                                "graphs": [
                                    {
                                        "colorField": "color",
                                        "columnWidth": 0,
                                        "fillAlphas": 1,
                                        "fillColors": "",
                                        "fixedColumnWidth": 20,
                                        "fontSize": -1,
                                        "id": "AmGraph-1",
                                        "lineColor": "",
                                        "lineColorField": "color",
                                        "title": "graph 1",
                                        "type": "column",
                                        "valueField": "value",
                                        showHandOnHover: true
                                    }
                                ],
                                "valueAxes": [
                                    {
                                        "id": "ValueAxis-2",
                                        "axisAlpha": 0,
                                        "axisColor": "#",
                                        "fontSize": 12,
                                        "gridColor": "#666666",
                                        "labelOffset": -1,
                                        "showFirstLabel": false,
                                        "titleColor": "#666666",
                                        "fontFamily": "'Open Sans', sans-serif",
                                        "minimum": 0
                                    }
                                ],
                                "titles": [
                                    {
                                        "id": "Title-1",
                                        "fontFamily": "'Open Sans', sans-serif",
                                        "color": "#666666",
                                        "size": 12
                                    }
                                ]
                            }
                        }
                    }
                };
            }]
        );
}());

