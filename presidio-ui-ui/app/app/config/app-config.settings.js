(function () {
    'use strict';

    function appConfig (appConfigProvider) {
        appConfigProvider

        /**
         * Default Configuration
         */
            .addConfigContainer({
                id: 'default',
                displayName: 'Default',
                description: 'All Default configuration'
            })
            .addConfigItem({
                id: 'default.daysRange',
                displayName: 'Date Range in Days',
                description: 'Default value for all Date Range in Days in the application, in days, from now.',
                type: 'integer',
                value: 180
            })
            .addConfigItem({
                id: 'default.daysAgo',
                displayName: 'Days Ago',
                description: 'Default date value for all Days Ago in the application, in days, from now.',
                type: 'integer',
                value: 30
            })
            .addConfigItem({
                id: 'default.shortDaysRange',
                displayName: 'Short Days Range in Days',
                description: 'Default short date range value, in days, from now.',
                type: 'integer',
                value: 1
            })
            .addConfigItem({
                id: 'default.longDaysRange',
                displayName: 'Long Days Range in Days',
                description: 'Default long date range value, in days, from now.',
                type: 'integer',
                value: 7
            })
            .addConfigItem({
                id: 'default.topRelatedDaysRange',
                displayName: 'Top Related Days Range',
                description: 'Default value for User-Overview Top-Related date range, in days, from now.',
                type: 'integer',
                value: 180
            })
            .addConfigItem({
                id: 'default.topRelatedListLimit',
                displayName: 'Top Related List Limit',
                description: 'Default value for Top-Related items limit.',
                type: 'integer',
                value: 5
            })
            .addConfigItem({
                id: 'default.userNameFallbackOrder',
                displayName: 'User Name Fallback Order',
                description: 'Default user name order. A CSV string value describing the priority order of property' +
                ' names on the user that might have a user name value.',
                type: 'string',
                value: 'displayName,noDomainUsername,username'
            })
            /**
             * END OF Default Configuration
             */
            .addConfigContainer({
                id: 'ui',
                displayName: 'User Interface',
                description: 'User interface configuration'
            })

            /**
             * Overview Configuration
             */
            .addConfigContainer({
                id: 'ui.overview',
                displayName: 'Overview',
                description: 'Overview page configuration'
            })
            .addConfigItem({
                id: 'ui.overview.shortDaysRange',
                displayName: 'Short Date Range in Days',
                description: 'Overview\'s short date range value, in days, from now. ' +
                'Used in "Alerts Status"s left chart.',
                type: 'integer',
                value: 30
            })
            .addConfigItem({
                id: 'ui.overview.longDaysRange',
                displayName: 'Long Date Range in Days',
                description: 'Overview\'s long date range value, in days, from now. ' +
                'Used in "Alerts Status"s right chart, and in "Alerts Severity"s chart.',
                type: 'integer',
                value: 180
            })
            .addConfigItem({
                id: 'ui.overview.numberOfRiskUsers',
                displayName: 'Number Of Risk Users',
                description: 'Overview\'s number of requested risk users',
                type: 'integer',
                value: 5
            })
            /**
             * END OF Overview Configuration
             */

            /**
             * Alerts Configuration
             */
            .addConfigContainer({
                id: 'ui.alerts',
                displayName: 'Alerts',
                description: 'Alerts page configuration'
            })
            .addConfigItem({
                id: 'ui.alerts.daysRange',
                displayName: 'Date Range in Days',
                description: 'Alerts\'s "Alert Start" filter\'s date range value, in days, from now.',
                type: 'integer',
                value: null
            })
            /**
             * END OF Alerts Configuration
             */

            /**
             * Alerts Configuration
             */
            .addConfigContainer({
                id: 'ui.userProfile',
                displayName: 'User Profile',
                description: 'Use the following configurations to change the default settings of the user profile'
            })
            .addConfigItem({
                id: 'ui.userProfile.activities_auth_mode',
                displayName: 'Attributes',
                description: 'Choose which type of user attributes will be displayed on user profiles. \n' +
                'Choosing Authentication will display the following attributes: Location, Authentication, Working Hours, Source Devices, Target Devices, Resource Usage. \n' +
                'Choosing DLP will display the following attributes: Top Applications, Top Directories, Risk Exposure, User Devices, Email Recipient Domains, Data Transfers.\n' +
                'True =>Authentication, False=>DLP',

                type: 'boolean',
                value: true
            })
            /**
             * END OF Alerts Configuration
             */

            /**
             * Explore Configuration
             */
            .addConfigContainer({
                id: 'ui.explore',
                displayName: 'Explore',
                description: 'Explore page configuration'
            })
            .addConfigItem({
                id: 'ui.explore.daysRange',
                displayName: 'Date Range in Days',
                description: 'Explore\'s "Result Between" control\'s date range value, in days, from now.',
                type: 'integer',
                value: null
            })
            /**
             * END OF Explore Configuration
             */

            /**
             * Reports Configuration
             */
            .addConfigContainer({
                id: 'ui.reports',
                displayName: 'Reports',
                description: 'All Reports default configuration'
            })
            .addConfigItem({
                id: 'ui.reports.daysRange',
                displayName: 'Date Range in Days',
                description: 'Reports default value for all Date Range in Days , in days, from now.',
                type: 'integer',
                value: null
            })
            .addConfigItem({
                id: 'ui.reports.daysAgo',
                displayName: 'Days Ago',
                description: 'Reports default date value for all Days Ago, in days, from now.',
                type: 'integer',
                value: null
            })

            /**
             * Reports:High Privileged Users
             */
            .addConfigContainer({
                id: 'ui.reports.highPrivilegedUsersMonitoring',
                displayName: 'High Privileged Users',
                description: 'High Privileged Users reports configuration'
            })
            .addConfigItem({
                id: 'ui.reports.highPrivilegedUsersMonitoring.daysRange',
                displayName: 'Date Range in Days',
                description: 'High Privileged Users Reports default Date Range in Days value, ' +
                'in days, from now.',
                type: 'integer',
                value: null
            })

            .addConfigContainer({
                id: 'ui.reports.highPrivilegedUsersMonitoring.admins',
                displayName: 'Admin Accounts',
                description: 'Admin Accounts report configuration'
            })
            .addConfigItem({
                id: 'ui.reports.highPrivilegedUsersMonitoring.admins.daysRange',
                displayName: 'Date Range in Days',
                description: 'Admin Accounts report\'s "Events Time" control\'s date range value, in days, from now.',
                type: 'integer',
                value: null
            })

            .addConfigContainer({
                id: 'ui.reports.highPrivilegedUsersMonitoring.executives',
                displayName: 'Executive Accounts',
                description: 'Executive Accounts report configuration'
            })
            .addConfigItem({
                id: 'ui.reports.highPrivilegedUsersMonitoring.executives.daysRange',
                displayName: 'Date Range in Days',
                description: 'Executive Accounts report\'s "Events Time" control\'s date range value, ' +
                'in days, from now.',
                type: 'integer',
                value: null
            })

            .addConfigContainer({
                id: 'ui.reports.highPrivilegedUsersMonitoring.serviceAccounts',
                displayName: 'Service Accounts',
                description: 'Service Accounts report configuration'
            })
            .addConfigItem({
                id: 'ui.reports.highPrivilegedUsersMonitoring.serviceAccounts.daysRange',
                displayName: 'Date Range in Days',
                description: 'Service Accounts report\'s "Events Time" control\'s date range value, in days, from now.',
                type: 'integer',
                value: null
            })
            /**
             * END OF Reports:High Privileged Users Monitoring Configuration
             */

            /**
             * Reports:External Access to the Network Configuration
             */
            .addConfigContainer({
                id: 'ui.reports.externalAccessToNetwork',
                displayName: 'External Access to the Network',
                description: 'External Access to the Network reports configuration'
            })
            .addConfigItem({
                id: 'ui.reports.externalAccessToNetwork.daysRange',
                displayName: 'Date Range in Days',
                description: 'Service Accounts report\'s "Events Time" control\'s date range value, in days, from now.',
                type: 'integer',
                value: null
            })

            .addConfigContainer({
                id: 'ui.reports.externalAccessToNetwork.suspiciousVPNDataAmount',
                displayName: 'VPN Anomalous Data Usage',
                description: 'VPN Anomalous Data Usage report configuration'
            })
            .addConfigItem({
                id: 'ui.reports.externalAccessToNetwork.suspiciousVPNDataAmount.daysRange',
                displayName: 'Date Range in Days',
                description: 'VPN Anomalous Data Usage report\'s "Events Time" control\'s date range ' +
                'value, in days, from now.',
                type: 'integer',
                value: null
            })

            .addConfigContainer({
                id: 'ui.reports.externalAccessToNetwork.VPNGeoHopping',
                displayName: 'VPN Anomalous Geolocation Sequences',
                description: 'VPN Anomalous Geolocation Sequences report configuration'
            })
            .addConfigItem({
                id: 'ui.reports.externalAccessToNetwork.VPNGeoHopping.daysRange',
                displayName: 'Date Range in Days',
                description: 'VPN Anomalous Geolocation Sequences report\'s "Events Time" control\'s ' +
                'date range value, in days, from now.',
                type: 'integer',
                value: null
            })

            /**
             * END OF Reports:External Access to the Network Configuration
             */

            /**
             * Reports:Device Monitoring Configuration
             */
            .addConfigContainer({
                id: 'ui.reports.deviceMonitoring',
                displayName: 'Device Investigation',
                description: 'Device Investigation reports configuration'
            })
            .addConfigItem({
                id: 'ui.reports.deviceMonitoring.daysRange',
                displayName: 'Date Range in Days',
                description: 'Device Investigation report\'s "Events Time" control\'s date range value, in days,' +
                ' from now.',
                type: 'integer',
                value: null
            })

            .addConfigContainer({
                id: 'ui.reports.deviceMonitoring.IPInvestigation',
                displayName: 'IP Investigation',
                description: 'IP Investigation report configuration'
            })
            .addConfigItem({
                id: 'ui.reports.deviceMonitoring.IPInvestigation.daysRange',
                displayName: 'Date Range in Days',
                description: 'IP Investigation report\'s "Events Time" control\'s date range value, in days,' +
                ' from now.',
                type: 'integer',
                value: null
            })

            .addConfigContainer({
                id: 'ui.reports.deviceMonitoring.suspiciousEndpointAccess',
                displayName: 'Suspicious Device Access',
                description: 'Suspicious Device Access report configuration'
            })
            .addConfigItem({
                id: 'ui.reports.deviceMonitoring.suspiciousEndpointAccess.daysRange',
                displayName: 'Date Range in Days',
                description: 'Suspicious Device Access report\'s "Events Time" control\'s date range value, ' +
                'in days, from now.',
                type: 'integer',
                value: null
            })

            .addConfigContainer({
                id: 'ui.reports.deviceMonitoring.sensitiveResourcesMonitoring',
                displayName: 'Sensitive Resources Monitoring',
                description: 'Sensitive Resources Monitoring report configuration'
            })
            .addConfigItem({
                id: 'ui.reports.deviceMonitoring.sensitiveResourcesMonitoring.daysRange',
                displayName: 'Date Range in Days',
                description: 'Sensitive Resources Monitoring report\'s "Events Time" control\'s date range value, ' +
                'in days, from now.',
                type: 'integer',
                value: null
            })

            /**
             * END OF Reports:Device Investigation Configuration
             */

            /**
             * Reports:Stale Accounts Monitoring Configuration
             */
            .addConfigContainer({
                id: 'ui.reports.staleAccountsMonitoring',
                displayName: 'Stale Accounts',
                description: 'Stale Accounts reports configuration'
            })
            .addConfigItem({
                id: 'ui.reports.staleAccountsMonitoring.daysAgo',
                displayName: 'Days Ago',
                description: 'Stale Accounts reports default date value for all Days Ago, ' +
                'in days, from now.',
                type: 'integer',
                value: null
            })

            .addConfigContainer({
                id: 'ui.reports.staleAccountsMonitoring.disabledUserAccounts',
                displayName: 'Disabled Accounts',
                description: 'Disabled Accounts report configuration'
            })
            .addConfigItem({
                id: 'ui.reports.staleAccountsMonitoring.disabledUserAccounts.daysAgo',
                displayName: 'Days Ago',
                description: 'Disabled Accounts report\'s "Disabled Since" control\'s date value, ' +
                'in days, from now, will be set to start of day.',
                type: 'integer',
                value: null
            })

            .addConfigContainer({
                id: 'ui.reports.staleAccountsMonitoring.inactiveUserAccounts',
                displayName: 'Inactive Accounts',
                description: 'Inactive Accounts report configuration'
            })
            .addConfigItem({
                id: 'ui.reports.staleAccountsMonitoring.inactiveUserAccounts.daysAgo',
                displayName: 'Days Ago',
                description: 'Inactive Accounts report\'s "No Activity Since" control\'s date value, ' +
                'in days, from now, will be set to start of day.',
                type: 'integer',
                value: null
            })

            /**
             * END OF Reports:Stale Accounts Configuration
             */

            /**
             * END OF Reports Configuration
             */

            /**
             * System Configuration
             */
            .addConfigContainer({
                id: 'system',
                displayName: 'System',
                description: 'Holds system configuration'
            })


            /**
             * Email Configuration
             */
            .addConfigContainer({
                id: 'system.email',
                displayName: 'Email',
                description: 'Holds all generic email configuration'
            })
            .addConfigItem({
                id: 'system.email.from',
                displayName: 'From',
                type: 'string',
                validators: ['required'],
                value: null
            })
            .addConfigItem({
                id: 'system.email.username',
                displayName: 'User',
                type: 'string',
                value: null
            })
            .addConfigItem({
                id: 'system.email.password',
                displayName: 'Password',
                type: 'password',
                value: null,
                meta: {
                    encrypt: true
                }
            })
            .addConfigItem({
                id: 'system.email.port',
                displayName: 'Port',
                type: 'integer',
                validators: ['required', 'integer', 'port'],
                value: null
            })
            .addConfigItem({
                id: 'system.email.host',
                displayName: 'Host',
                type: 'string',
                validators: ['required'],
                value: null
            })
            .addConfigItem({
                id: 'system.email.auth',
                displayName: 'Authentication',
                type: 'string',
                validators: ['required'],
                value: null
            })

            /**
             * Alerts Email Configuration
             */
            .addConfigContainer({
                id: 'system.alertsEmail',
                displayName: 'Alerts Email',
                description: 'Holds configuration for Alerts email'
            })
            .addConfigItem({
                id: 'system.alertsEmail.settings',
                displayName: 'Alerts Mail Settings',
                type: 'string',
                validators: ['required'],
                value: null
            })
            /**
             * END OF Alerts Email Configuration
             */

            /**
             * Log Email Configuration
             */
            .addConfigContainer({
                id: 'system.logEmail',
                displayName: 'Log Email',
                description: 'Holds configuration for Log email'
            })
            .addConfigItem({
                id: 'system.logEmail.subscribers',
                displayName: 'Log Mail Settings',
                type: 'string',
                value: null
            })
            /**
             * END OF Alerts Email Configuration
             */


            /**
             * Syslog Configuration
             */
            .addConfigContainer({
                id: 'system.syslogforwarding',
                displayName: 'Alert Forwarding via Syslog',
                description: 'Set up properties for forwarding alerts',
                note: 'Note: All alerts will be forwarded via TCP'
            })
            .addConfigItem({
                id: 'system.syslogforwarding.enabled',
                displayName: 'Enable Forwarding?',
                type: 'boolean',
                value: null
            })
            .addConfigItem({
                id: 'system.syslogforwarding.forwardingtype',
                displayName: 'Forwarding Type',
                type: 'string',
                validators: ['required'],
                value: null
            })
            .addConfigItem({
                id: 'system.syslogforwarding.ip',
                displayName: 'IP',
                type: 'string',
                validators: ['required', 'ip'],
                value: null
            })
            .addConfigItem({
                id: 'system.syslogforwarding.port',
                displayName: 'Port',
                type: 'integer',
                validators: ['required', 'port'],
                value: null
            })
            .addConfigItem({
                id: 'system.syslogforwarding.messageformat',
                displayName: 'Message Format',
                type: 'string',
                validators: ['required'],
                value: null
            })
            .addConfigItem({
                id: 'system.syslogforwarding.alertseverity',
                displayName: 'Selective Forwarding: Alert Severity',
                type: 'string',
                value: null
            })
            .addConfigItem({
                id: 'system.syslogforwarding.usertypes',
                displayName: 'Selective Forwarding: User Tags',
                type: 'string',
                value: null
            })


            /**
             * END OF Syslog Configuration
             */


            /**
             * Locale configuration
             */
            .addConfigContainer({
                id: 'system.locale',
                displayName: 'Locale',
                configurable: false
            })
            .addConfigItem({
                id: 'system.locale.settings',
                displayName: 'Locale',
                type: 'string',
                value: 'en'
            });
        /**
         * END OF Locale configuration
         */

        /**
         * END OF System Configuration
         */

    }

    appConfig.$inject = ['appConfigProvider'];

    angular.module('Fortscale.appConfig')
        .config(appConfig);
}());
