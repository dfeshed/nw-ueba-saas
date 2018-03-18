(function () {
    'use strict';

    function CSVConverter(assert) {
        this.assert = assert;
    }

    angular.merge(CSVConverter.prototype, {

        /**
         * The start of the error message
         * @private
         */
        _errMsg: 'CSVConverter.service: ',

        /**
         * An array holding allowed values for CSVSchemaValue.type
         * @private
         */
        _validSchemaTypes: ['boolean', 'integer', 'number'],

        /**
         * Validate a single CSVSchema member
         * @methodOf CSVConverter
         *
         * @param {{type: string, name: string} | string} CSVSchemaValue
         * @param {string} errMsg
         * @private
         */
        _validateCSVSchemaValue: function (CSVSchemaValue, errMsg) {
            var self = this;
            errMsg = errMsg || '';

            // Validation in case the CSVSchemaValue is a string
            if (_.isString(CSVSchemaValue)) {
                self.assert(CSVSchemaValue !== '',
                    errMsg + 'All CSVSchema members that are strings ' +
                    'must not be empty strings.', RangeError);

                // Validation in case the CSVSchemaValue is an object
            } else if (_.isObject(CSVSchemaValue)) {

                // Validate CSVSchemaValue.name
                self.assert(!_.isUndefined(CSVSchemaValue.name),
                    errMsg + 'All CSVSchema members that are objects ' +
                    'must have a "name" property.', ReferenceError);
                self.assert(_.isString(CSVSchemaValue.name),
                    errMsg + 'All CSVSchema members that are objects ' +
                    'must have a "name" property that is a string.', TypeError);
                self.assert(CSVSchemaValue.name !== '',
                    errMsg + 'All CSVSchema members that are objects ' +
                    'must have a "name" property this is not an empty string.', RangeError);

                // Validate CSVSchemaValue.type
                self.assert(!_.isUndefined(CSVSchemaValue.type),
                    errMsg + 'All CSVSchema members that are objects ' +
                    'must have a "type" property.', ReferenceError);
                self.assert(_.isString(CSVSchemaValue.type),
                    errMsg + 'All CSVSchema members that are objects ' +
                    'must have a "type" property that is a string.', TypeError);
                self.assert(self._validSchemaTypes
                        .indexOf(CSVSchemaValue.type.toLowerCase()) !== -1,
                    'All CSVSchema members that are objects ' +
                    'must have a "type" property that is one of these values: ' +
                    self._validSchemaTypes.join(', ') + ' .', RangeError);
            } else {

                // Default assertion that always fires if this branch is executed.
                // This branch executing means that CSVSchemaValue was not a string or an object
                self.assert(false,
                    errMsg + 'All CSVSchema members must be either a string ' +
                    'or an object.', TypeError);
            }
        },
        /**
         * Validate a CSVSchema array
         *
         * @param {Array<{type: string, name: string} | string>} CSVSchema
         * @param {string} errMsg
         * @private
         */
        _validateCSVSchema: function (CSVSchema, errMsg) {

            var self = this;
            errMsg = errMsg || '';

            self.assert(!_.isUndefined(CSVSchema),
                errMsg + 'CSVSchema argument must be provided.', ReferenceError);
            self.assert(_.isArray(CSVSchema),
                errMsg + 'CSVSchema argument must be an array.', TypeError);

            _.each(CSVSchema, function (CSVSchemaValue) {
                self._validateCSVSchemaValue(CSVSchemaValue, errMsg);
            });
        },

        /**
         * Validates CSVString
         *
         * @param {string} CSVString
         * @param {string} errMsg
         * @private
         */
        _validateCSVString: function (CSVString, errMsg) {
            var self = this;
            errMsg = errMsg || '';

            // Validate CSVSchemaValue.name
            self.assert(!_.isUndefined(CSVString),
                errMsg + 'CSVString argument must be provided.', ReferenceError);
            self.assert(_.isString(CSVString),
                errMsg + 'CSVString argument must be a string.', TypeError);
        },
        /**
         * Validates CSVString
         *
         * @param {string} CSVString
         * @param {string} errMsg
         * @private
         */
        _validateModel: function (model, errMsg) {
            var self = this;
            errMsg = errMsg || '';

            // Validate CSVSchemaValue.name
            self.assert(!_.isUndefined(model),
                errMsg + 'model argument must be provided.', ReferenceError);
            self.assert(_.isObject(model),
                errMsg + 'model argument must be an object.', TypeError);
        },
        /**
         * Holds parser methods -to- and -from-
         * @private
         */
        _parsers: {
            /**
             * Holds -to- parsers. converts from string to a specific type.
             */
            to: {
                /**
                 * returns false if value is undefined, null, '0', 0, or 'false',
                 * otherwise it returns true.
                 *
                 * @param {*} value
                 * @returns {boolean}
                 */
                boolean: function (value) {
                    return (
                    value !== undefined &&
                    value !== null &&
                    value !== '0' &&
                    value !== 0 &&
                    value !== 'false');
                },
                /**
                 * Parses from string to integer
                 *
                 * @param {string} value
                 * @returns {Number}
                 */
                integer: function (value) {
                    return parseInt(value);
                },
                /**
                 * Parses from string to number
                 *
                 * @param {string} value
                 * @returns {Number}
                 */
                number: function (value) {
                    return parseFloat(value);
                }
            },
            /**
             * Holds -from- parsers. converts from primitives to string.
             */
            from: {
                /**
                 * Returns '1' if true and '0' if false
                 *
                 * @param {boolean} value
                 * @returns {string}
                 */
                boolean: function (value) {
                    return value ? '1' : '0';
                },
                /**
                 * Returns a string from a number.
                 *
                 * @param {number} value
                 * @returns {string}
                 */
                integer: function (value) {
                    return value.toString();
                },
                /**
                 * Returns a string from a number.
                 *
                 * @param {number} value
                 * @returns {string}
                 */
                number: function (value) {
                    return value.toString();
                }
            }
        },

        /**
         * Takes a CSVString and a SCVSchema and returns an array of objects based on the schema
         *
         * @param {String} CSVString
         * @param {Array<{type: string, name: string} | string>} CSVSchema
         * @returns {Array<{}>}
         */
        toModel: function (CSVString, CSVSchema) {

            var self = this;

            // Validate CSVSchema
            self._validateCSVSchema(CSVSchema, this._errMsg + 'toModel: ');
            // Validate CSVString
            self._validateCSVString(CSVString, this._errMsg + 'toModel: ');


            // Extract values array from the string
            var values = CSVString.split(',');

            // Create a model array
            var model = [];

            // Ascertain the length
            var schemaLength = CSVSchema.length;

            // Iterate through values, and place each value in the correct position and type
            // on the model array
            _.each(values, function (value, index) {

                // Position in the model array
                var pos = Math.floor(index / schemaLength);

                // CSVSchema array position is determined by
                // modding the index of values by schemaLength
                var CSVSchemaValue = CSVSchema[index % schemaLength];

                // Create a new object member on the model array if on does not exist.
                if (!model[pos]) {
                    model[pos] = {};
                }

                // When CSVSchemaValue is an object, parsing is required,
                // otherwise the value is set directly.
                if (_.isObject(CSVSchemaValue)) {

                    // Set the value to model after parsing it.
                    model[pos][CSVSchemaValue.name] = self._parsers.to[CSVSchemaValue.type](value);
                } else {
                    model[pos][CSVSchemaValue] = value;
                }
            });

            // return the model array
            return model;
        },
        /**
         * Takes an array of object and a CSVSchema, and converts the model into a SCV string.
         *
         * @param {Array<{}>} model An array model that correlates to the schema
         * @param {Array<{type: string, name: string} | string>} CSVSchema
         * @returns {string}
         */
        toCSVString: function (model, CSVSchema) {

            var self = this;

            // Validate CSVSchema
            self._validateCSVSchema(CSVSchema, this._errMsg + 'toCSVString: ');
            // Validate model
            self._validateModel(model, this._errMsg + 'toCSVString: ');


            // Iterate through model members and return a string from joining array by map
            return _.map(model, function (tabObj) {
                // Iterate through CSVSchema members and return string from joining array by map
                return _.map(CSVSchema, function (CSVSchemaValue) {

                    // If CSVSchemaValue is an object, then parsing is required
                    // otherwise return the direct value.
                    if (_.isObject(CSVSchemaValue)) {
                        // parse from primitive type to string and return the value
                        return self._parsers.from[CSVSchemaValue.type](tabObj[CSVSchemaValue.name]);
                    }
                    // return a direct value (if CSVSchemaValue is not an object)
                    return tabObj[CSVSchemaValue];
                }).join(',');
            }).join(',');
        }
    });


    CSVConverter.$inject = ['assert'];
    angular.module('Fortscale.shared.services.CSVConverter', [
        'Fortscale.shared.services.assert'
    ])
        .service('CSVConverter', CSVConverter);

}());
