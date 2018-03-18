(function () {
    'use strict';
    function StringUtils(){}

    _.extend(StringUtils.prototype, {
        /**
         * Converts a string to an integer hash
         *
         * @param {string} str
         * @returns {number}
         */
        toIntHash: function(str) {
            var hash = 0, i, chr, len;
            if (str.length === 0) {
                return hash;
            }
            for (i = 0, len = str.length; i < len; i++) {
                chr   = str.charCodeAt(i);
                hash  = ((hash << 5) - hash) + chr;
                hash |= 0; // Convert to 32bit integer
            }

            return hash;
        },
        /**
         * Converts string to a hash in a certain base.
         *
         * @param {string} str
         * @param {integer=} base Will default to 35
         * @returns {string}
         */
        toBaseHash: function (str, base) {
            base = base || 35;
            return this.toIntHash(str).toString(base);
        },

        // Converts camel case to slug case. Example: convertThis  :  convert-this
        toSlugCase: function (str) {
            let rgx = /([A-Z])/g;
            return str.replace(rgx, (res) => '-' + res.toLowerCase());
        }
    });

    angular.module('Fortscale.shared.services.stringUtils', [])
        .service('stringUtils', StringUtils);
}());
