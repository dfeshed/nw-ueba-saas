/**
 * Resolve object for alerts
 */
(function () {
    'use strict';

    var staleAccountsMonitoringData = {
        disabledUserAccountsData: {
            reportTitle: 'Disabled Accounts',
            reportDescription: 'Investigate accounts registered as disabled in Active Directory.',
            reportClass: 'regular'
        },
        inactiveUserAccountsData: {
            reportTitle: 'Inactive Accounts',
            reportDescription: 'Investigate inactive accounts that are NOT disabled in Active Directory.',
            reportClass: 'regular'
        },
        disabledUserWithNetworkData: {
            reportTitle: 'Disabled Accounts with Network Activity',
            reportDescription: 'Investigate active accounts registered as disabled in Active Directory.',
            reportClass: 'regular'
        },
        terminatedUserWithNetworkData: {
            reportTitle: 'Terminated Accounts with Network Activity',
            reportDescription: 'Investigate active accounts registered as terminated in Active Directory.',
            reportClass: 'regular'
        }
    };


    angular.module('Config')
        .constant('staleAccountsMonitoringData', staleAccountsMonitoringData);
}());
