(function () {
    'use strict';



    function prettyMessage ($filter, assert, appConfig) {
        var _CONFIG_LOCALE_KEY = 'system.locale.settings';


        /**
         * This filter will look for the key in the messages and return the message
         * If the message exists - return the value of the message
         * If the value is not exists - return valueIfNotExits.
         * If the value is not exists and valueIfNotExists is not defined - return the original key
         *
         * @param {*} value
         * @param {{}} valueIfNotExists - optional value
         * @returns {*}
         */

        function prettyMessageFilter (messageKey, valueIfNotExists) {
            assert.isString(messageKey, 'message key is not defined');

            var messagePrefix = 'messages.'+appConfig.getConfigItem(_CONFIG_LOCALE_KEY).value;
            var fullMessageKey = messagePrefix +"."+messageKey;
            var configItem =  appConfig.getConfigItem(fullMessageKey);
            if (configItem !== undefined && configItem !== null){
                return configItem.value;
            } else {
                if ((valueIfNotExists !== undefined) && (valueIfNotExists !== '')){
                    return valueIfNotExists;
                } else {
                    return messageKey;
                }
            }

        }

        return prettyMessageFilter;
    }

    prettyMessage.$inject = ['$filter', 'assert', 'appConfig'];

    angular.module('Fortscale.shared.filters')
        .filter('prettyMessage', prettyMessage);
}());
