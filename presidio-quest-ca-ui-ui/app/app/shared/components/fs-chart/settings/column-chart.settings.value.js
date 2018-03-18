(function () {
    'use strict';
    angular.module('Fortscale.shared.components.fsChart')
        .value('fsChart.settings.column', {
            chart: {
                type: 'column'
            },
            legend: {
                enabled: false
            },
            xAxis: {
                type: 'category',
                labels: {

                    style: {
                        fontSize: '12px',
                        fontFamily: 'Verdana, sans-serif'
                    }
                }
            },
            tooltip: {
                enabled: false
            },
            plotOptions: {
                column: {
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: true,
                        style: {
                            textShadow: ''
                        },
                        useHTML: true
                    }

                },
                series: {
                    color: '#373298'
                }
            }




        });
}());
