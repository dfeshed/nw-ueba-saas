/**
 * This service is a mapper service. It returns a type from mapObject if any of the queries
 * meet the condition set in the query.
 * queries list is an 'or' list, meaning that if any of the query objects meet the condition,
 * that type will return.
 * Each query object is an 'And' operator, meaning that all conditions must be fulfilled to match.
 *
 */
(function () {
    'use strict';

    // Dependencies
    var indicatorTypeMap, assert, URLUtils;

    /**
     *
     * @constructor
     */
    function IndicatorTypeMapper (_indicatorTypeMap_, _assert_, _URLUtils_) {

        // Mount dependencies
        if (!indicatorTypeMap) {
            indicatorTypeMap = _indicatorTypeMap_;
        }
        if (!assert) {
            assert = _assert_;
        }
        if (!URLUtils) {
            URLUtils = _URLUtils_;
        }
    }

    _.extend(IndicatorTypeMapper.prototype, {

        _errMsg: 'Fortscale.shared.services.indicatorTypeMapper: indicatorTypeMapper.service: ',

        /**
         * Takes an indicator and returns an indicator type by matching against the queries
         * in the mapObject.
         * It returns null if no type was found.
         *
         * @param {object} indicator
         * @returns {string | null}
         */
        getType: function (indicator) {

            // Init indicatorType variable to return
            var indicatorTypeObject = null;

            // Using 'every' instead of 'each' allows the process to stop by returning false.
            // false is returned when a match is made.
            _.every(indicatorTypeMap, function (mapProperty, indicatorType) {

                // If any of the queries match, the indicatorType is returned.
                if (_.some(mapProperty.queries, function (query) {

                        // Iterate through query properties and all must match.
                        return _.every(query, function (checkValue, checkName) {
                            return _.isEqualWith(indicator[checkName], checkValue,
                                // The customizer is needed because when Restangular holds an array
                                // it treats it as a list and it adds function and properties to the
                                // array. Worse still, the array is not recognized as array but as
                                // an object. This causes the _.isEqual to return false even though
                                // the arrays are equal. So the arrays need to be converted to
                                // actual arrays.
                                function (valA, valB) {
                                    // Convert array-like objects to actual arrays.
                                    if (valA.slice && valA.map) {
                                        valA = valA.slice(0);
                                    }
                                    if (valB.slice && valB.map) {
                                        valB = valB.slice(0);
                                    }
                                    return _.isEqual(valA, valB);
                                });
                        });

                    })) {

                    // If _.some has returned true, then a match was made, and indicatorType can be returned.
                    indicatorTypeObject = indicatorTypeMap[indicatorType];
                    return false;
                }

                return true; // So as not to stop iteration with undefined
            });

            return indicatorTypeObject;
        },

        /**
         * Calculates the desired href of an indicator in alerts page.
         *
         * @param {string} alertId
         * @param {object=} indicator
         * @param {boolean=} shouldPassSearchParams
         * @param {string=} subRoute
         * @returns {string}
         */
        getTargetUrl: function (alertId, indicator, shouldPassSearchParams, subRoute) {
            var errMsg = this._errMsg + 'getTargetUrl: ';
            // Validations
            assert.isString(alertId, 'alertId', errMsg);
            assert.isObject(indicator, 'indicator', errMsg, true);

            // Set defaults
            if (shouldPassSearchParams === undefined) {
                shouldPassSearchParams = true;
            }

            var targetUrl = '#/alerts/' + alertId;

            // Get an indicator type
            var indicatorType = this.getType(indicator);

            // Build the target url
            if (indicatorType !== null && !subRoute) {

                targetUrl += '/' + indicator.id;

                // Route to 'gen' for general indicators
                if (indicatorType.indicatorClass === 'gen') {
                    targetUrl += '/gen/overview';

                } else if (indicatorType.indicatorClass === 'tag') {
                    targetUrl += '/tag';
                }

            } else if (subRoute) {
                targetUrl += '/' + subRoute;
            }

            if (shouldPassSearchParams) {
                targetUrl += URLUtils.getSearchQueryString();
            }

            return targetUrl;
        }
    });

    IndicatorTypeMapper.$inject = ['indicatorTypeMap', 'assert', 'URLUtils'];

    angular.module('Fortscale.shared.services.indicatorTypeMapper')
        .service('indicatorTypeMapper', IndicatorTypeMapper);
}());
