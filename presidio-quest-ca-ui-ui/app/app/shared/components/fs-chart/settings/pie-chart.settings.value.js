(function () {
    'use strict';
    angular.module('Fortscale.shared.components.fsChart')
        .value('fsChart.settings.pie', {
            chart: {
                type: 'pie'
            },
            tooltip: {
                pointFormat: '</div>{series.name}: <b>{point.y}</b>',
                backgroundColor: 'rgba(245, 245, 245, 0.8)'
            },
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: true,
                        format: '<span class="pie-chart-data-label">{point.name}: ' +
                        '<span style="color: blue;">{point.y}</span>' +
                        ' ({point.percentage:.1f} %)<span>',
                        shadow: false,
                        useHTML: false,
                        style: {
                            textShadow: false,
                            fontFamily: '"Open Sans", sans-serif'
                        }
                    }
                }
            },
            series: [{
                "colorByPoint": true
            }]
        });
}());
