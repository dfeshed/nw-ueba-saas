/**
 * Settings for indicator which contains heatmp
 */

(function () {
    'use strict';

    function sharedCredentialsFactory () {

        return {
            settings: {
                sharedCredentials: {
                    params: {
                        context_type: '{{entityTypeFieldName}}',
                        context_value: '{{entityName}}',
                        feature: '{{anomalyTypeFieldName}}',
                        'function': 'timeIntervals'
                    },
                    styleSettings: {},
                    templates: {},
                    sortData: data => {
                        return _.orderBy(data, ['value'], ['asc']);
                    },
                    dataAdapter: (indicator, dataItem) => {
                        let open = moment(parseInt(dataItem.keys[0], 10)).utc();
                        let close = moment(parseInt(dataItem.keys[1], 10)).utc();
                        let chartItem = {
                            category: dataItem.value,
                            open: open.hour() + open.minute() / 60,
                            openDate: open.format('HH:mm:ss'),
                            close: close.hour() + close.minute() / 60,
                            closeDate: close.format('HH:mm:ss'),
                            indicator: indicator
                        };

                        return chartItem;
                    },
                    handlers: {
                        "clickGraphItem": function (indicator, item) {
                            // indicatorChartTransitionUtil.go('columnAnomaly', indicator, item);
                        }
                    },
                    chartSettings: {
                        "type": "serial",
                        "categoryField": "category",
                        "rotate": true,
                        "startDuration": 1,
                        "fontFamily": "'Open Sans', sans-serif",
                        "categoryAxis": {
                            "gridPosition": "start",
                            "axisAlpha": 0,
                            "axisThickness": 0,
                            "gridColor": "#989191",
                            "gridThickness": 0,
                            "title": "Session Source IP",
                            "titleColor": "#989191",
                            "fontFamily": "'Open Sans', sans-serif"
                        },
                        "colors": [
                            "#0D8ECF"
                        ],
                        "trendLines": [],
                        "graphs": [
                            {
                                "balloonText": "Session Start: <b>[[openDate]]</b><br>Session End: <b>[[closeDate]]</b>",
                                "closeField": "close",
                                "colorField": "color",
                                "fillAlphas": 1,
                                // "fixedColumnWidth": 15,
                                "columnWidth": 0.5,
                                "fontSize": 4,
                                "id": "AmGraph-1",
                                "lineThickness": 0,
                                "negativeFillColors": "#FF0000",
                                "openField": "open",
                                "title": "graph 1",
                                "type": "column"
                            }
                        ],
                        "guides": [],
                        "valueAxes": [
                            {
                                "id": "ValueAxis-1",
                                "stackType": "regular",
                                "axisColor": "#BCB5B5",
                                "gridColor": "#989191",
                                // "labelFrequency": 2,
                                // "minHorizontalGap": 10,
                                // "minVerticalGap": 10,
                                "showFirstLabel": true,
                                "showLastLabel": true,
                                "title": "Time of Day",
                                "titleColor": "#989191",
                                precision: 2,
                                labelFunction: (value) => {
                                    let hours = Math.floor(value);
                                    let minutes = Math.floor((value % 1) * 60);
                                    minutes = minutes < 10 ? '0' + minutes : minutes;
                                    return hours + ':' + minutes;
                                }
                            }
                        ],
                        "allLabels": [],
                        "balloon": {},
                        "titles": [
                            {
                                "color": "#989191",
                                "id": "Title-1",
                                "size": 15,
                                "text": "Concurrent Sessions",
                                "fontFamily": "'Open Sans', sans-serif"

                            }
                        ]

                    }
                }

            }
        };
    }

    sharedCredentialsFactory.inject = [];

    angular.module('Fortscale.shared.services.indicatorTypeMapper')
        .factory('indicatorTypeMapper.sharedCredentials', sharedCredentialsFactory);
    /**
     a = {
    title: {
        text: 'Overlapping VPN Sessions by Time'
    }
    ,
    yAxis: {
        title: {
            text: 'Time'
        }
    }
    ,
    xAxis: {
        title: {
            text: 'Session Source IP'
        }
    }

}
     **/
}());

