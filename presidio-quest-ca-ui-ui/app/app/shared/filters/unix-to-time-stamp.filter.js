(function () {
    'use strict';

    function unixToTimeStamp () {

        /**
         * Takes a unix time stamp and convert it to timestamp in milliseconds
         *
         * @param {string|undefined} val
         * @returns {string|undefined}
         */
        function unixToTimeStampFilter (val) {

            // If val is falsy return it.
            if (!val) {
                return val;
            }

            var values = val.split(',');

            var timeStamps = _.map(values, function (timeStr) {

                var trimmedTimeStr = timeStr.trim();
                // If its a string and the length of unix, add '000' and return
                if (/^(\d{10})$/.test(trimmedTimeStr)) {
                    return trimmedTimeStr + '000';
                }

                // If previous condition is false then return the value as is.
                throw new RangeError('unixToTimeStamp.filter: csv must be of 10 ' +
                    'chars long unix time stamps. "' + trimmedTimeStr + '" is not.');

            });

            // Return a CSV
            return timeStamps.join(',');
        }

        return unixToTimeStampFilter;

    }

    angular.module('Fortscale.shared.filters')
        .filter('unixToTimeStamp', unixToTimeStamp);
}());
