(function () {
    'use strict';
    angular.module('Fortscale.shared.components.fsChart')
        .value('fsChart.settings.heatmap', {
            chart: {
                type: 'heatmap'
            },
            tooltip: {
                pointFormat: 'Logged in <b>{point.value}</b> times'
            },
            series: [{
                borderWidth: 1,
                dataLabels: {
                    enabled: true,
                    color: '#fff',
                    style: {
                        textShadow: '1px 1px 0 #777, -1px -1px 0 #777'
                    }
                }
            }]
        });
}());
