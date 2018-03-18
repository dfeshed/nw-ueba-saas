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
        .factory('indicatorTypeMapper.aggregated-serial', ['$filter',
            'indicatorTypeMapper.commonQueryParams','utils',
            function ($filter, commonQueryParams,utils) {

                var listOfFileSizeIndicatiors = [
                    "sum_of_file_size_prnlog_",
                    "sum_of_attachments_to_external_recipient_size_dlpmail_",
                    "sum_of_email_attachment_file_size_dlpmail_",
                    "sum_of_moved_files_to_removable_device_size_dlpfile_",
                    "sum_of_copied_files_to_removable_device_size_dlpfile_",
                    "sum_of_moved_files_from_remote_device_size_dlpfile_",
                    "sum_of_copied_files_from_remote_device_size_dlpfile_"
                ];

                function prettyValue(value,anomalyTypeFieldName){
                    var isBytes = false;
                    _.each(listOfFileSizeIndicatiors,function(indicatorName){
                        if (_.includes(anomalyTypeFieldName, indicatorName)){
                            isBytes = true;
                        }
                    });

                    if (isBytes){
                        return $filter('prettyBytes')(value);
                    } else {
                        return value;
                    }

                }

                return {
                    settings: {
                        scatterSettings: {
                            params: commonQueryParams.aggregationIndicatorsByTime,
                            templates: {
                                titles: {
                                    'Title-1': '{{ anomalyTypeFieldName  | buildAggregatedKey: \'title\' | translate}}'
                                },
                                valueAxes: {
                                    'ValueAxis-1': '{{ anomalyTypeFieldName  | buildAggregatedKey: \'axisYtitle\' | ' +
                                    'translate}}'
                                }
                            },
                            sortData: data => {
                                return _.orderBy(data, [dataItem => parseInt(dataItem.keys[0], 10)], ['asc']);
                            },
                            dataAdapter: (indicator, dataItem) => {
                                let chartItem = {
                                    category: new Date(parseInt(dataItem.keys[0], 10)),
                                    originalCategory: dataItem.keys[0],
                                    value: dataItem.value,
                                    anomalyTypeFieldName:indicator.anomalyTypeFieldName
                                };

                                if (dataItem.anomaly) {
                                    chartItem.color = '#FF0000';
                                }

                                return chartItem;
                            },
                            handlers: {
                                // "clickGraphItem": function (indicator, item) {
                                //     indicatorChartTransitionUtil.go('columnEntity', indicator, item);
                                // }
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
                                    // dateFormats: [{
                                    //     period: 'fff',
                                    //     format: 'JJ:NN:SS'
                                    // }, {
                                    //     period: 'ss',
                                    //     format: 'JJ:NN:SS'
                                    // }, {
                                    //     period: 'mm',
                                    //     format: 'JJ:NN'
                                    // }, {
                                    //     period: 'hh',
                                    //     format: 'MMM DD JJ:NN'
                                    // }, {
                                    //     period: 'DD',
                                    //     format: 'MMM DD'
                                    // }, {
                                    //     period: 'WW',
                                    //     format: 'MMM DD'
                                    // }, {
                                    //     period: 'MM',
                                    //     format: 'MMM YYYY'
                                    // }, {
                                    //     period: 'YYYY',
                                    //     format: 'MMM YYYY'
                                    // }],

                                    'categoryFunction': (dataItem) => {
                                        var stringDataItem =  moment(dataItem).utc().format('YYYY MMM DD HH:mm');
                                        return moment(stringDataItem,'YYYY MMM DD HH:mm').toDate();
                                    }

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
                                            return moment(dataItem.dataContext.category).utc().format('YYYY MMM DD HH:mm') +
                                                ' : <b>' + prettyValue(dataItem.dataContext.value,dataItem.dataContext.anomalyTypeFieldName) + '</b>';
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
                                        "minimum": 0,
                                        labelFunction: (value) => {
                                            return prettyValue(value,arguments[2].data[0].dataContext.anomalyTypeFieldName);

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

