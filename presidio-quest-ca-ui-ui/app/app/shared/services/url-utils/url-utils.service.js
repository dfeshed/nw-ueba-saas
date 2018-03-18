(function () {
    'use strict';

    function URLUtils ($location) {
        this.$location = $location;
    }

    angular.extend(URLUtils.prototype, {
        /**
         * Gets the (angular) url, and uses regex to get the query string.
         * if withoutQuestionMark is set to true, the returned value will be without it;
         *
         * @param {boolean=} withoutQuestionMark
         * @returns {string}
         */
        getSearchQueryString: function (withoutQuestionMark) {

            // Get the (angular) url
            var url = this.$location.url();

            // Match the search query
            var match = url.match(/(\?)([^#]+)/);

            // If match was made
            if (match) {

                // if withoutQuestionMark return the match without the question mark
                if (withoutQuestionMark) {
                    return match[2];
                }

                // return the match string
                return match[0];
            }

            // If no match then return an empty string
            return '';
        },
        /**
         * Takes an (angular) url and sets it to transition to a new state.
         * If shouldPassQuery is set to true, the search query will be added to the url.
         *
         * @param {string} url
         * @param {boolean=} shouldPassQuery
         */
        setUrl: function (url, shouldPassQuery) {
            var transitionTo = url + (shouldPassQuery ? this.getSearchQueryString() : '');
            this.$location.url(transitionTo);
        }
    });

    URLUtils.$inject = ['$location'];

    angular.module('Fortscale.shared.services.URLUtils', [])
        .service('URLUtils', URLUtils);
}());
