/**
 * Settings for indicator which contains heatmp
 */

(function () {
    'use strict';

    function ColumnRangeFactory(){

        return {
            settings: {
                columnRange: {
                    params: {
                        context_type: '{{entityTypeFieldName}}',
                        context_value: '{{entityName}}',
                        feature: '{{anomalyTypeFieldName}}',
                        'function': 'timeIntervals'
                    },
                    styleSettings: {
                        height: '28.125rem'
                    },
                    chartSettings: {
                        chart: {
                            inverted: true
                        },
                        yAxis: {
                            type: 'datetime'
                        },
                        series: [{
                        }]
                    }
                }

            }
        };
    }

    ColumnRangeFactory.inject = [];

    angular.module('Fortscale.shared.services.indicatorTypeMapper')
        .factory('indicatorTypeMapper.columnRange', ColumnRangeFactory);
}());

