/**
 * This file will contain mapping settings which required  by  date rate indicator
 */

(function () {
    'use strict';
    angular.module('Fortscale.shared.services.indicatorTypeMapper')

            .factory('indicatorTypeMapper.data-rate-scatter-plot',['$filter',
                'indicatorTypeMapper.commonQueryParams',
                function ($filter, commonQueryParams) {

                return {
                    settings: {
                        scatterSettings: {
                            params: commonQueryParams.entityTypeAnomalyTypeCount30days,

                            chartSettings: {
                                title: {
                                    text: 'VPN Exfiltration Rate for {{entityName}} (Last 30 Days)'
                                },
                                yAxis: {
                                    title: {
                                        text: 'Rate'
                                    }
                                },
                                tooltip: {
                                    formatter: function () {
                                        var tooltipText = 'Rate: ' + $filter('prettyBytes')(this.point.y) + '/Sec<br>';
                                        tooltipText += 'Total: ' + this.point.total + '<br>';
                                        tooltipText += 'Duration: ' + this.point.duration + ' (HH:MM:SS)';
                                        return tooltipText;
                                    }
                                }
                            }
                        }
                    }

                };
            }]);
}());

