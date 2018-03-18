(function () {
    'use strict';

    function prettyAlertName ($filter, assert) {

        /**
         * This filter will look for the alert name in messages and return the message
         * If the message exists - return the value of the message
         * or the original key if not exists
         *
         * @param {{name: string, timeframe: string}} alert
         * @returns {string}
         */
        function prettyAlertNameFilter (alert) {
            if (!alert) {
                return '';
            }
            assert.isString(alert.name, 'alert.name', 'prettyAlertNameFilter: ');

            var fullKey = `alert.${alert.name}.name`;
            var prettyAlertName = $filter('prettyMessage')(fullKey, alert.name);
            if (alert && alert.timeframe) {
                prettyAlertName = `
                    ${prettyAlertName} | <span class="alert-timeframe-tag alert-timeframe-tag--${alert.timeframe.toLowerCase()}">
                        ${alert.timeframe}
                    </span>`;
            }
            return prettyAlertName;

        }

        return prettyAlertNameFilter;
    }

    prettyAlertName.$inject = ['$filter', 'assert'];

    angular.module('Fortscale.shared.filters')
        .filter('prettyAlertName', prettyAlertName);
}());
