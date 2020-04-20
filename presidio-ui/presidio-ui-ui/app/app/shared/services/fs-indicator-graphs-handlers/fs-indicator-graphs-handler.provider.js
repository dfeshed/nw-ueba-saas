(function () {
    'use strict';
    var IS_NOT_OPTIONAL = false;
    var CAN_NOT_BE_EMPTY = false;
    var IS_OPTIONAL = true;

    function FsIndicatorGraphsHandlerProvider (assert) {

        var provider = this;

        provider._errMsg = "FsIndicatorGraphsHandlerProvider: ";

        provider._indicatorQueries = new Map();

        /**
         * Add an indicator query. This will add a query to match the indicator and a handler function to fire
         * when indicator is matched
         *
         * @param {object} queryObj Will be used as the query object to determine which handler function to use
         * @param handlerFn The function to invoke when indicator has matched the query
         */
        provider.addIndicatorQuery = function (queryObj, handlerFn) {
            var errMsg = provider._errMsg + "addIndicatorQuery: ";

            // Validations
            assert.isObject(queryObj, 'queryObj', errMsg, IS_NOT_OPTIONAL);
            assert.isFunction(handlerFn, 'handlerFn', errMsg, IS_NOT_OPTIONAL);

            var queryObjectKey;
            try {
                queryObjectKey = JSON.stringify(queryObj);
            } catch (e) {
                throw new RangeError(errMsg + "queryObj must be a parseble to string.", e);
            }

            assert(typeof provider._indicatorQueries.get(queryObjectKey) === 'undefined',
                errMsg + "queryObj must be a unique query.", RangeError);

            // Add query object and handler function
            provider._indicatorQueries.set(queryObjectKey, {query: queryObj, handlerFn: handlerFn});
        };

        /**
         * PROVIDER'S FACTORY
         */
        function fsIndicatorGraphsHandlerFactory ($location, $rootScope) {

            var errMsg = "fsIndicatorGraphsHandlerFactory: ";

            /**
             * Takes a selector and returns the indicator from the element's controller.
             *
             * @param {string} selector
             * @returns {object|null} Returns an indicator or null
             */
            function getIndicatorBySelector (selector) {
                assert.isString(selector, 'selector', errMsg + "getIndicatorBySelector: ", IS_NOT_OPTIONAL);

                var element = $(selector);
                if (!element.length) {
                    return null;
                }

                var controller = angular.element(element[0]).controller();

                if (!controller) {
                    return null;
                }

                return controller.indicator || null;

            }

            /**
             * Takes an indicator and iterates through queries. When a query matches, the handlerFn is returned.
             * If no match is made, null is returned.
             *
             * @param {object} indicator The indicator to be matched
             * @returns {function|null} Returns a handlerFn or null;
             */
            function getHandlerFnByIndicator (indicator) {
                assert.isObject(indicator, 'indicator', errMsg + "getHandlerFnByIndicator: ", IS_NOT_OPTIONAL);

                var queriesHandlers = Array.from(provider._indicatorQueries.values());
                var selectedQueryHandler = null;

                for (var queryHandler of queriesHandlers) {
                    if (_.find([indicator], queryHandler.query)) {
                        selectedQueryHandler = queryHandler;
                        break;
                    }

                }

                if (selectedQueryHandler) {
                    return selectedQueryHandler.handlerFn;
                }

                return null;

            }

            /**
             * Matches a handlerFn to an indicator, and if match is made, the handlerFn is invoked.
             *
             * @param {object} indicator
             * @param {*} value
             * @param {string=} identifier
             * @returns {null|*}
             */
            function invokeHandlerByIndicator (indicator, value, identifier) {
                assert.isObject(indicator, 'indicator', errMsg + "getHandlerFnByIndicator: ", IS_NOT_OPTIONAL);
                assert.isString(identifier, 'identifier', errMsg + "getHandlerFnByIndicator: ", IS_OPTIONAL);

                var handlerFn = api.getHandlerFnByIndicator(indicator);

                if (handlerFn) {
                    return handlerFn(indicator, value, identifier);
                }

                return null;
            }

            /**
             * Takes a data source id and a search object, and uses $location.path to transition to Explore page.
             *
             * @param {string} dataSourceId
             * @param {object=} search
             */
            function goToExplore (dataSourceId, search) {

                // Validations
                assert.isString(dataSourceId, 'dataSourceId', errMsg + "goToExplore: ",
                    IS_NOT_OPTIONAL, CAN_NOT_BE_EMPTY);
                assert.isObject(search, 'search', errMsg + "goToExplore: ", IS_OPTIONAL);

                // We use applyAsync because there is no guaranty that angular is not in a digest phase when the
                // function is called.
                $rootScope.$applyAsync(function () {
                    $location.path('/d/explore/' + dataSourceId).search(search);
                });
            }

            var api = {
                getIndicatorBySelector: getIndicatorBySelector,
                getHandlerFnByIndicator: getHandlerFnByIndicator,
                invokeHandlerByIndicator: invokeHandlerByIndicator,
                goToExplore: goToExplore
            };

            return api;
        }

        this.$get = ['$location', '$rootScope', fsIndicatorGraphsHandlerFactory];
    }

    FsIndicatorGraphsHandlerProvider.$inject = ['assertConstant'];

    angular.module('Fortscale.shared.services.fsIndicatorGraphsHandler', [])
        .provider('fsIndicatorGraphsHandler', FsIndicatorGraphsHandlerProvider);
}());
