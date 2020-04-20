/**
 * Settings for indicator which contains heatmp
 */

(function () {
    'use strict';

    function HeatMapFactory($filter){
        /**
         * Used as weekDaysUs axis
         */
        var weekDaysUS = [
            'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'
        ];

        /**
         * Used as hours in a day axis.
         */
        var HoursInDay = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16,
            17, 18, 19, 20, 21, 22, 23];

        return {
            settings: {
                heatmap: {
                    params: {

                        feature: '{{anomalyTypeFieldName}}',
                        'function': 'hourlyCountGroupByDayOfWeek'
                    },

                    styleSettings: {
                        height: '28.125rem'
                    },
                    chartSettings: {
                        chart: {
                            inverted: true
                        },
                        xAxis: {
                            categories: weekDaysUS.reverse(),
                            title: {
                                text: 'Week days'
                            },
                            labels: {
                                formatter: function () {
                                    return $filter('pascalCase')(this.value);
                                }
                            }
                        },
                        yAxis: {
                            title: {
                                text: 'Hours'
                            },
                            categories: HoursInDay
                        },
                        colorAxis: {
                            min: 1,
                            minColor: '#8fbde4',
                            maxColor: '#2766a9'
                        },
                        title: {
                            text: '{{dataEntitiesIds[0]|entityIdToName}} ' +
                            'Authentication Times (Last 90 days)'
                        },
                        "series": [{
                            "name": "{{dataEntitiesIds[0]|entityIdToName}} " +
                            "Authentication Times (Last 90 days) "
                        }]
                    }
                }

            }
        };
    }

    HeatMapFactory.inject = ['$filter'];

    angular.module('Fortscale.shared.services.indicatorTypeMapper')
        .factory('indicatorTypeMapper.heatmap', HeatMapFactory);
}());

