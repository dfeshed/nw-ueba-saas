/**
 * Resolve object for alerts
 */
(function () {
    'use strict';

    var suspiciousUsersData = {
        reportTitle: 'Accounts',
        reportDescription: 'The following report lists Privileged Users With high-risk scores. Click a username To drill-down into score breakdown and investigate anomalous behaviors.',
        reportClass: 'regular',
        noItems: 'No Users Found'
    };

    angular.module('Config')
        .constant('suspiciousUsersData', suspiciousUsersData);
}());
