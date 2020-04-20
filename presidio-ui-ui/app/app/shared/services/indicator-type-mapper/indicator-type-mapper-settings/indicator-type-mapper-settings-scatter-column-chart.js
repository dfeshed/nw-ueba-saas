/**
 * This file contains setting for both column charts in any indicator which display
 * two column charts
 */

(function () {
    'use strict';

var aggregatedIndicatorsTablesMap = {
};

angular.module('Fortscale.shared.services.indicatorTypeMapper')
    .filter('ScatterColumnTitleFilter', function () {
        return function (val, propName) {
            if (val && aggregatedIndicatorsTablesMap[val]) {
                return aggregatedIndicatorsTablesMap[val][propName];
            } else if (val) {
                return val;
            }
        };
    })
    .factory('indicatorTypeMapper.scatter-column',['$filter',
    'indicatorTypeMapper.commonQueryParams',
    function ($filter,commonQueryParams) {
        return {
            settings:  {
                column: { //Left column
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
                                text: '{{anomalyTypeFieldName | ScatterColumnTitleFilter: \'axisYtitleColumn\'}}'
                            }
                        },
                        xAxis: {
                            title: {
                                enabled: true,
                                text: '{{anomalyTypeFieldName | ScatterColumnTitleFilter: \'axisXtitleColumn\'}}'
                            }
                        }
                    }
                },
                scatter: { //Right column
                    params: commonQueryParams.entityTypeCountByTime,
                    styleSettings: {
                        height: '24rem'
                    },
                    chartSettings: {
                        title: {
                            text: '<span class="chart-title">' +
                            '{{dataEntitiesIds[0]|entityIdToName}} Actions on ' +
                            '{{anomalyValue}} ' +
                            '(Last 30 days)</span>'
                        },
                        yAxis: {
                            title: {
                                text: '{{anomalyTypeFieldName | ScatterColumnTitleFilter: \'axisYtitleScatter\'}}'
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

