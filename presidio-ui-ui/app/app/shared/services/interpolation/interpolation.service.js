(function () {
    'use strict';

    /**
     * Service used by fs-state-container directive to manage the queries and data fetching
     *
     * @param $interpolate
     * @constructor
     */
    function Interpolation ($interpolate) {

        /**
         * PRIVATE PROPERTIES
         */
        this._errMessageStart = 'interpolation: ';

        /**
         * PRIVATE METHODS
         */

        /**
         * Takes a template, string or object. Returns a stringified string,
         * or throws SyntaxError if object can not be stringified.
         *
         * @param {string | object} template
         * @returns {string}
         * @private
         */
        this._convertTemplateToString = function _convertTemplateToString (template) {

            var self = this;

            if (angular.isString(template)) {
                return template;
            }

            try {
                return JSON.stringify(template);
            } catch (e) {
                var errMsg = self._errMessageStart +
                    '_convertTemplateToString: template provided is not a string, ' +
                    'and can not be stringified wite JSON.stringify.\n' + e.message;
                throw new SyntaxError(errMsg);
            }
        };

        /**
         * Takes a template string and tries to JSON.parse it into an object.
         * If JSON.parse fails, it throws a SyntaxError
         *
         * @param {string} templateString
         * @private
         */
        this._convertStringToObject = function _convertStringToObject (templateString) {
            var self = this;

            try {
                return JSON.parse(templateString);
            } catch (e) {
                var errMsg = self._errMessageStart +
                    '_convertStringToObject: templateString provided ' +
                    'could not be parsed to object.\nThe string:\n' + templateString + '\n' +
                    e.message;
                throw new SyntaxError(errMsg);
            }
        };

        /**
         * Takes a query value, a changeTo value (or function) and an object. It iterates through
         * all object values (also nested) and if a certain value matches the queryValue, the state
         * property will change to -or by if changeTo is a function - changeTo value.
         * If changeTo is a function, the new value will be the result of changeTo(value,key,obj)
         *
         *
         * @param {*} queryValue
         * @param {string|function} changeTo
         * @param {object|Array} obj
         * @returns {*}
         * @private
         */
        this._digestAdapterItem = function (queryValue, changeTo, obj) {


            // Duplicate object/array
            if (_.isArray(obj)) {
                obj = obj.slice(0);
            } else {
                obj = _.merge({}, obj);
            }

            // Iterate through object's keys and invoke _digestAdapterItem recursively or
            // apply changeTo
            _.each(obj, _.bind(function (value, key) {

                // If the value of the property is an object, invoke _digestAdapterItem recursively
                if (_.isObject(value)) {
                    obj[key] = this._digestAdapterItem(queryValue, changeTo, value);
                    return;
                }

                // the property value equals to the query value, this means that the adapter should
                // be used. When the changeTo value is a function, it is invoked with value,key,obj
                // otherwise if its not a function, then the value of the property should be
                // changeTo value
                if (value === queryValue) {
                    if (_.isFunction(changeTo)) {
                        obj[key] = changeTo(value, key, obj);
                    } else {
                        obj[key] = changeTo;
                    }
                }
            }, this));

            return obj;
        };

        /**
         * Takes a state adapter, and state, iterates through all values in the adapter, and for
         * each adapterItem, it invokes _digestAdapterItem which in turn will return a new state
         * object, that is potentially modified.
         *
         * @param {Array<{queryValue: *, changeTo: string|function}>=} stateAdapter
         * @param {object} state
         * @returns {Result|*}
         * @private
         */
        this._digestStateAdapter = function (stateAdapter, state) {
            _.each(stateAdapter, _.bind(function (adapterItem) {
                var queryValue = adapterItem.queryValue;
                var changeTo = adapterItem.changeTo;
                state = this._digestAdapterItem(queryValue, changeTo, state);
            }, this));

            return state;
        };

        /**
         *
         * @param {string} templateString
         * @param {object} state
         * @returns {object} The interpolated and objectified templateString
         * @private
         */
        this._interpolateTemplate = function _interpolateTemplate (templateString, state) {
            return this._convertStringToObject($interpolate(templateString)(state));
        };


        /**
         * Takes a template (string or object) and a state object, and interpolates the template
         * based on state. It returns an (post-interpolated) object.
         * If stateAdapter is provided, the state will be digested with the stateAdapter.
         *
         * @param {string|object} template
         * @param {object} state
         * @param {Array<{queryValue: *, changeTo: string|function}>=} stateAdapter
         * @returns {Object}
         */
        this.interpolate = function interpolate (template, state, stateAdapter) {

            if (stateAdapter) {
                state = this._digestStateAdapter(stateAdapter, state);
            }

            var templateString = this._convertTemplateToString(template);
            var interpolated = this._interpolateTemplate(templateString, state);

            // In the case of a string template, there is no need to do the elaborate merge,
            // because no data is dropped by converting to a string.
            if (_.isString(template)) {
                return interpolated;
            }

            // The reason a merge is returned and not the interpolated object, is that some data is
            // dumped on the convertion to string of the original object (_convertTemplateToString)
            // Functions for example are dumped in the stringfication process (this is the native
            // functionality of JSON.stringify). So in order to insert them back, the pre
            // interpolated template (if not a string) is merged with the interpolated one, so all
            // new values override old values, but missing data like functions remain.
            return _.merge({}, template, interpolated);

        };

    }

    Interpolation.$inject = ['$interpolate'];

    angular.module('Fortscale.shared.services.interpolation', [])
        .service('interpolation', Interpolation);
}());

