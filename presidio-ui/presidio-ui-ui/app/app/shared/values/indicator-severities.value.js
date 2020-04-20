(function () {
    'use strict';
    angular.module('Fortscale')
        .value('indicatorSeverities', {
            Critical: {
                order: 0,
                id: 'critical',
                name: 'Critical',
                count: 0
            },
            High: {
                order: 1,
                id: 'high',
                name: 'High',
                count: 0
            },
            Medium: {
                order: 2,
                id: 'medium',
                name: 'Medium',
                count: 0
            },
            Low: {
                order: 3,
                id: 'low',
                name: 'Low',
                count: 0
            }
        });
}());
