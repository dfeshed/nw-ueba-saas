(function () {
    'use strict';

    function ObjectUtils (assert) {
        this._errMsg = 'Fortscale.shared.services.objectUtils: ';

        /**
         *
         * @param caller
         * @param obj
         * @private
         */
        this._validateObject = function (caller, obj) {
            var errMsg = this._errMsg + caller + ': ';

            assert(angular.isDefined(obj),
                errMsg + 'obj argument must be provided.',
                ReferenceError);

            assert(angular.isObject(obj),
                errMsg + 'obj argument must be an object.',
                TypeError);

        };

        this._validateHashObject = function (caller, obj) {
            var errMsg = this._errMsg + caller + ': ';

            assert(angular.isDefined(obj),
                errMsg + 'hashMap argument must be provided.',
                ReferenceError);

            assert(angular.isObject(obj),
                errMsg + 'hashMap argument must be an object.',
                TypeError);

        };

        this._validateObjectName = function (caller, objName) {
            var errMsg = this._errMsg + caller + ': ';

            assert(angular.isDefined(objName),
                errMsg + 'objName argument must be provided.',
                ReferenceError);

            assert(angular.isString(objName),
                errMsg + 'objName argument must be a string.',
                TypeError);

            assert(objName !== '',
                errMsg + 'objName argument must not be an empty string.',
                RangeError);
        };

        /**
         * Takes an object and returns a deep flattened array of key-value pairs,
         * where the odd member is the namespace of the property and the even member is the value.
         * This function runs recursively so there is a danger of stack overflowing.
         *
         * @param {object} obj
         * @param {string} objName
         */
        this._flattenObject = function (obj, objName) {

            var self = this;

            // iterate through keys of object to flatten. If the value is not an object
            // then a key-value array is returned. If value is an object, the function is invoked
            // recursively with the object value and the key name.
            // The array is flattened for each recursion resulting in a single array of key-value
            // pairs where the odd is key and the even is value
            return _.flattenDeep(_.map(_.keys(obj), function (key) {
                var nameSpace = objName + '.' + key;
                if (_.isObject(obj[key])) {
                    return self._flattenObject(obj[key], nameSpace);
                } else {
                    return [nameSpace, obj[key]];
                }
            }));

        };

        /**
         * Takes an array of key-value pairs where the odd is the key and the even is the value
         * Returns a hash-map object
         *
         * @param {Array} flattenedArray An array of key value pairs
         * @returns {object} Hash map object
         * @private
         */
        this._createPairsObject = function (flattenedArray) {
            var length = flattenedArray.length;
            var pairsObject = {};

            for (var i = 0; i < length; i += 2) {
                pairsObject[flattenedArray[i]] = flattenedArray[i + 1];
            }

            return pairsObject;
        };

        /**
         * Takes an object and a dot delimited namespace.
         * Recursively build (or use if exists) an object for each namespace but the last.
         * The value will be set to the last namespace which is the property's name.
         *
         * @param {object} obj
         * @param {string} nameSpace
         * @param {*} value
         * @returns {object}
         * @private
         */
        this._inflateNameSpace = function (obj, nameSpace, value) {
            var nsList = nameSpace.split('.');

            if (nsList.length === 1) {
                obj[nsList] = value;
                return obj;
            }

            var nsCurrentPos = nsList.shift();
            obj[nsCurrentPos] = obj[nsCurrentPos] || {};

            this._inflateNameSpace(obj[nsCurrentPos], nsList.join('.'), value);
        };

        /**
         * Takes an object (possibly with nesting) and returns a flattened object
         * where each property is the namespace of the original property from the original object
         * and each value is the original value
         *
         * @param {object} obj
         * @param {string} objName
         * @returns {Object}
         */
        this.flattenToNamespace = function (obj, objName) {

            // Validations
            this._validateObject('flattenToNamespace', obj);
            this._validateObjectName('flattenToNamespace', objName);

            // Create a flattened object
            return this._createPairsObject(this._flattenObject(obj, objName));
        };

        /**
         * Takes a Key-value object where each key represents a namespace.
         * The namespace should be inflated into the provided object
         * (or new object if no object was provided) and the value set to the namespace.
         *
         * @param {object} hashMap
         * @param {object=} obj
         * @returns {object}
         */
        this.createFromFlattened = function (hashMap, obj) {
            var self = this;

            // Validate hashMap
            self._validateHashObject('createFromFlattened', hashMap);
            // Validate object
            self._validateObject('createFromFlattened', obj);

            // Default the object
            obj = obj || {};

            // Iterate through keys and inflate each namespace into the object
            _.each(hashMap, function (value, key) {
                self._inflateNameSpace(obj, key, value);
            });

            return obj;
        };

        /**
         * Removes all properties that have null values.
         * This is useful for example to pass query params and remove all that have null values.
         * It is possible to remove other values if alternativeValue is provided.
         *
         * @param {object} obj
         * @param {*=} alternativeValue
         */
        this.removeNulls = function (obj, alternativeValue) {
            // Validate object
            this._validateObject('removeNulls', obj);

            alternativeValue = alternativeValue === undefined ? null : alternativeValue;

            _.each(obj, function (value, propertyName) {
                if (value === alternativeValue) {
                    delete obj[propertyName];
                }
            });
        };
    }

    ObjectUtils.$inject = ['assert'];
    angular.module('Fortscale.shared.services.objectUtils', [
        'Fortscale.shared.services.assert'
    ])
        .service('objectUtils', ObjectUtils);
}());
