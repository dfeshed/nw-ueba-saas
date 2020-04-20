/**
 * This file will contain mapping settings which required  all scatter-plot single charts
 */

(function () {
    'use strict';
    angular.module('Fortscale.shared.services.indicatorTypeMapper')
        .value('indicatorTypeMapper.abstract-scatter-plot',{
            templateUrl: 'app/layouts/alert/layouts/indicator-templates/singlescatterplot.html',
            settings: {
                scatterSettings: {
                    styleSettings: {
                        height: '25rem',
                        width: '100%',
                        padding : '0 1.25rem',
                        boxSizing: 'border-box'
                    },
                    chartSettings: {
                        xAxis: [{
                            type: "datetime"
                        }],
                        yAxis: {
                            min: 0
                        }
                    }
                }
            },
            indicatorClass: 'gen'
        });
}());
