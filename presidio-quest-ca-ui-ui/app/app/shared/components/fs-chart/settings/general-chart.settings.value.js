(function () {
    'use strict';
    angular.module('Fortscale.shared.components.fsChart')
        .value('fsChart.settings.general', {
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false
            },
            credits: {
                enabled: false
            }
        });
}());
