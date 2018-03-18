/**
 * This file contains setting for both column charts in any indicator which display
 * two column charts
 */

(function () {
    'use strict';

var aggregatedIndicatorsTablesMap = {
};

angular.module('Fortscale.shared.services.indicatorTypeMapper')
    .filter('ScatterPieTitleFilter', function () {
        return function (val, propName) {
            if (val && aggregatedIndicatorsTablesMap[val]) {
                return aggregatedIndicatorsTablesMap[val][propName];
            } else if (val) {
                return val;
            }
        };
    })
    .factory('indicatorTypeMapper.scatter-pie',['$filter',
    'indicatorTypeMapper.commonQueryParams',
    function ($filter,commonQueryParams) {
        return {
            settings:  {
                pie: { //Left column
                    params: commonQueryParams.entityTypeActionCodeCount,
                    styleSettings: {
                        height: '24rem'
                    },
                    chartSettings: {
                        title: {
                            text: '<span class="chart-title">' +
                            '{{dataEntitiesIds[0]|entityIdToName}} Actions on {{anomalyValue}} ' +
                            '(Last 30 Days)</span>'
                        },
                        yAxis: {
                            title: {
                                enabled: true,
                                text: '{{anomalyTypeFieldName | ScatterPieTitleFilter: \'axisYtitlePie\'}}'
                            }
                        },
                        xAxis: {
                            title: {
                                enabled: true,
                                text: '{{anomalyTypeFieldName | ScatterPieTitleFilter: \'axisXtitlePie\'}}'
                            }
                        },
                        tooltip: {
                             headerFormat: '',
                             formatter: function () {
                                return '<b>' + this.point.name + ':</b> ' +
                                    Highcharts.numberFormat(this.y, 0, '', ',') + '<br/>';
                             }
                        }
                    }
                },
                scatter: { //Right column
                    params: commonQueryParams.entityTypeCountByTime,
                    chartSettings: {
                        title: {
                            text: '<span class="chart-title">' +
                            '{{dataEntitiesIds[0]|entityIdToName}} Actions on ' +
                            '{{anomalyValue}} ' +
                            '(Last 30 days)</span>'
                        },
                        yAxis: {
                            title: {
                                text: '{{anomalyTypeFieldName | ScatterPieTitleFilter: \'axisYtitleScatter\'}}'
                            }
                        },
                        tooltip: {
                            enabled: false
                        }
                    }
                }
            }
        };
    }]
    );
}());

