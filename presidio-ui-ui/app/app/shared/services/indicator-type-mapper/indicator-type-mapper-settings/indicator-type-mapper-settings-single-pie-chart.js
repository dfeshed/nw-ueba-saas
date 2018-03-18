(function () {
    'use strict';

    let colors = ["#0D8ECF", "#0D6ECD","#0A335C","#1689FA"];
    let colorIndex = 0;

    angular.module('Fortscale.shared.services.indicatorTypeMapper')
    /**
     * Filter which generate the relevant message key from the anomalyTypeFieldName and title or axisYtitle
     */
        .filter('buildPieKey', function () {
            var prefix = "evidence.single.singlePieHistogram.";
            return function (anomalyTypeFieldName, postfix) {
                return prefix + anomalyTypeFieldName + ".chart." + postfix;
            };
        })
        .factory('indicatorTypeMapper.pie', [
            '$filter',
            'indicatorChartTransitionUtil',
            function ($filter, indicatorChartTransitionUtil) {
                return {
                    settings: {
                        pie: {
                            params: {

                                feature: '{{anomalyTypeFieldName}}',
                                'function': 'Count'
                            },
                            styleSettings: {},
                            templates: {
                                titles: {
                                    'Title-1': '{{ anomalyTypeFieldName  | buildPieKey: \'title\' ' +
                                    '| translate: this}}' //"this" refer to the scope (the indicator object)'
                                }
                            },
                            sortData: data => {
                                return _.orderBy(data, ['anomaly', 'value'], ['asc', 'desc']);
                            },
                            dataAdapter: (indicator, dataItem) => {
                                let chartItem = {
                                    category: $filter('anomalyTypeFormatter')(dataItem.keys[0], indicator),
                                    originalCategory: dataItem.keys[0],
                                    value: dataItem.value
                                };

                                if (dataItem.anomaly) {
                                    chartItem.color = '#CC3300';
                                } else {
                                    // Alternate colors
                                    chartItem.color = colors[colorIndex%4];
                                    colorIndex += 1;
                                }

                                return chartItem;
                            },
                            handlers: {
                                "clickGraphItem": function (indicator, item) {
                                    indicatorChartTransitionUtil.go('pie', indicator, item);
                                }

                            },
                            chartSettings: {
                                "type": "pie",
                                "legend" :{
                                    "position":"right",
                                        "marginRight":40,
                                        "autoMargins":false,
                                        "textClickEnabled": true
                                },
                                "balloon": {
                                    "maxWidth": 400
                                },

                                "balloonText": "<span style='word-break: break-all'>[[title]]</span><br><span style='font-size:14px;padding-left:5px'><b>[[value]]</b> ([[percents]]%)</span>",
                                "innerRadius": "60%",
                                "labelRadius": 10,
                                "pullOutRadius": 10,
                                "radius": "40%",
                                "startRadius": 0,
                                "colors": ["#0D8ECF", "#0D6ECD",'#2A0DCD','#315936'],
                                "colorField": "color",
                                "hideLabelsPercent": 10,
                                "maxLabelWidth": 199,
                                "pullOutDuration": 0,
                                "pullOutEffect": "easeOutSine",
                                "startAlpha": 1,
                                "titleField": "category",
                                "valueField": "value",
                                "color": "#989191",
                                "creditsPosition": "bottom-right",
                                "fontFamily": "Open Sans",
                                "fontSize": 12,
                                "processCount": 999,
                                "titles": [
                                    {
                                        "id": "Title-1",
                                        "fontFamily": "'Open Sans', sans-serif",
                                        "color": "#666666",
                                        "size": 12
                                    }
                                ]

                            }
                            // styleSettings: {
                            //     height: '28.125rem'
                            // },
                            // chartSettings: {
                            //     title: {
                            //         text: "{{ anomalyTypeFieldName  | buildPieKey: \'title\' " +
                            //             "| translate: this}}" //"this" refer to the scope (the indicator object)
                            //
                            //
                            //     },
                            //     tooltip: {
                            //         pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
                            //     },
                            //     plotOptions: {
                            //         pie: {
                            //             allowPointSelect: true,
                            //             cursor: 'pointer',
                            //             dataLabels: {
                            //                 enabled: true,
                            //                 format: '<b>{point.name}</b>: {point.percentage:.1f} %'
                            //             }
                            //         }
                            //     },
                            //     "series": [{
                            //         "name": "{{ anomalyTypeFieldName  | buildPieKey: \'seriesName\' |
                            // translate:this}}" //"this" refer to the scope (the indicator object) }] }
                        }
                    }
                };
            }]
        );
}());

