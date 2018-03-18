(function () {
    'use strict';
    angular.module('Fortscale.shared.components.fsChart')
        .value('fsChart.settings.scatter', {
            chart: {
                type: 'scatter',
                zoomType: 'xy'
            },
            legend: {
                enabled: false
            },
            tooltip: {
                pointFormat: '{series.name}: <b>{point.y}</b>'
            },
            plotOptions: {
                scatter: {
                    marker: {
                        radius: 3,
                        states: {
                            hover: {
                                enabled: true,
                                lineColor: 'rgba(143,189.228,0.5)'
                            }
                        }
                    },
                    states: {
                        hover: {
                            marker: {
                                enabled: false
                            }
                        }
                    }
                }
            },
            series: [{
                turboThreshold: 4000
            }],
            xAxis: [{
                type: 'datetime',
                color: 'rgba(143,189.228,0.5)',

                startOnTick: false,
                lineWidth: 0,
                minorGridLineWidth: 0,
                lineColor: 'transparent',
                minorTickLength: 0,
                tickLength: 0
            }],
            yAxis: {
                minTickInterval: 1
            }

        });
}());
