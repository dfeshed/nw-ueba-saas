(function () {
    'use strict';
    angular.module('Fortscale.shared.components.fsChart')
        .value('fsChart.settings.columnRange', {
            chart: {
                type: 'columnrange'
            },
            legend: {
                enabled: false
            },
            colors: [
                'rgba(103, 132, 185, 0.8)',
                'rgba(195, 124, 183, 0.8)',
                'rgba(245, 164, 153, 0.8)',
                'rgba(176, 199, 94, 0.8)',
                'rgba(242, 196, 70, 0.8)',
                'rgba(103, 185, 181, 0.8)',
                'rgba(233, 152, 72, 0.8)',
                'rgba(162, 141, 214, 0.8)',
                'rgba(143, 189, 228, 0.8)',
                'rgba(129, 206, 158, 0.8)'
            ],
            plotOptions: {
                series: {
                    colorByPoint: true,
                    pointWidth: 20
                }
            },
            xAxis: {
                type: 'category',
                labels: {
                    style: {
                        fontSize: '12px',
                        fontFamily: '\'Roboto\', sans-serif',
                        color: '#818285'
                    }
                }

            },
            yAxis: {
                labels: {
                    style: {
                        fontSize: '12px',
                        fontFamily: '\'Roboto\', sans-serif',
                        color: '#818285'
                    }
                }

            },
            tooltip: {
                enabled: false
            }
        });
}());
