/**
 * Based on this solution:
 * http://stackoverflow.com/questions/15900485/correct-way-to-convert-size-in-bytes-to-kb-mb-gb-in-javascript
 *
 * Converts Bytes to pretty bytes.
 */
(function () {
    'use strict';
    function prettyBytes () {
        return function (bytes, precision) {

            if (bytes === null || bytes === undefined) {
                return bytes;
            }

            if (isNaN(parseFloat(bytes)) || !isFinite(bytes)) {
                return '-';
            }

            bytes = parseFloat(bytes);

            if (bytes === 0) {
                return '0 Byte';
            }
            if (typeof precision !== 'number') {
                precision = 1;
            }
            var k = 1000; // or 1024 for binary
            var dm = precision + 1 || 3;
            var sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
            var i = Math.floor(Math.log(bytes) / Math.log(k));
            return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
        };
    }

    angular.module('Fortscale.shared.filters')
        .filter('prettyBytes', prettyBytes);
}());
