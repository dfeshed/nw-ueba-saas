(function () {
    'use strict';

    var _errMsg = 'Fortscale.shared.filters: pageToOffset: ';

    function pageToOffset (assert) {


        /**
         * Filter
         * takes a pageNum and pageAmount and converts it to offset
         *
         * @param {string | number} pageNumStr
         * @param {string | number} pageSizeStr
         * @returns {number | undefined}
         */
        function pageToOffsetFilter(pageNumStr, pageSizeStr) {

            // Return undefined if no pageNum is received
            if (!angular.isDefined(pageNumStr)) {
                return undefined;
            }

            // Convert values to integers if they are not integers
            var pageNum = parseInt(pageNumStr);
            var pageSize = parseInt(pageSizeStr);

            // Validate values
            assert(!_.isNaN(pageNum) && angular.isNumber(NaN), _errMsg +
                'pageNumStr argument must be a number.', TypeError);
            assert(angular.isDefined(pageSizeStr), _errMsg +
                'pageSize argument must be provided.', ReferenceError);
            assert(!_.isNaN(pageSize) && angular.isNumber(pageSize), _errMsg +
                'pageSize argument must be a number.', TypeError);
            assert(pageSize !== 0, _errMsg +
                'pageSize argument must not be equal to 0.', RangeError);

            // return the offset
            return (pageNum-1) * pageSize;
        }

        return pageToOffsetFilter;
    }

    pageToOffset.$inject = ['assert'];

    angular.module('Fortscale.shared.filters')
        .filter('pageToOffset', pageToOffset);
}());
