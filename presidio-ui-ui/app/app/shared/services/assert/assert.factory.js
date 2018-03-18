(function () {
    'use strict';

    function assertFactory () {

        /**
         *
         * @param {boolean | *} condition
         * @param {string=} message
         * @param {function=} ErrorType The object should be
         */
        function assert (condition, message, ErrorType) {

            // Create an early return when condition is fulfilled to prevent execution of futile code.
            if (condition) {
                return;
            }

            // Set defaults
            message = message || '';

            var error;

            // Verify error type is valid. If it's not, then Error type should be error
            if (typeof ErrorType === 'function') {

                // Create new error from Error type
                error = new ErrorType(message);

            }

            // If ErrorType is not a function or (newly populated) error is not an instance of Error
            // Then error should be new Error
            if (!(error instanceof Error)) {
                error = new Error(message);
            }

            throw error;

        }

        /**
         * Validates that a variable is string and not an empty string
         *
         * @param {string} str
         * @param {string} strName
         * @param {string=} errMsg Defaults to empty string
         * @param {boolean=} isOptional Defaults to false
         * @param {boolean=} canBeEmpty Defaults to false
         */
        assert.isString = function (str, strName, errMsg, isOptional, canBeEmpty) {

            errMsg = errMsg || '';
            isOptional = !!isOptional;
            canBeEmpty = !!canBeEmpty;

            if (!(str === undefined && isOptional)) {
                assert(!_.isUndefined(str),
                    errMsg + strName + ' must be provided.',
                    ReferenceError
                );
                assert(_.isString(str),
                    errMsg + strName + ' must be a string.',
                    TypeError
                );
                assert(str !== '' || canBeEmpty,
                    errMsg + strName + ' must not be an empty string.',
                    RangeError
                );
            }
        };

        /**
         * Validates that a variable is string and not an empty string
         *
         * @param {number} num
         * @param {string} numName
         * @param {string=} errMsg Defaults to empty string
         * @param {boolean=} isOptional Defaults to false
         */
        assert.isNumber = function (num, numName, errMsg, isOptional) {

            errMsg = errMsg || '';
            isOptional = !!isOptional;

            if (!(num === undefined && isOptional)) {
                assert(!_.isUndefined(num),
                    errMsg + numName + ' must be provided.',
                    ReferenceError
                );
                assert(_.isNumber(num),
                    errMsg + numName + ' must be a number.',
                    TypeError
                );
            }
        };

        /**
         * Validates that a variable is an array
         *
         * @param {string} arr
         * @param {string} arrName
         * @param {string=} errMsg Defaults to empty string
         * @param {boolean=} isOptional Defaults to false
         */
        assert.isArray = function (arr, arrName, errMsg, isOptional) {

            errMsg = errMsg || '';
            isOptional = !!isOptional;

            if (!(arr === undefined && isOptional)) {
                assert(!_.isUndefined(arr),
                    errMsg + arrName + ' must be provided.',
                    ReferenceError
                );
                assert(_.isArray(arr),
                    errMsg + arrName + ' must be an array.',
                    TypeError
                );
            }
        };

        /**
         * Validates that a variable is a function
         *
         * @param {string} fn
         * @param {string} fnName
         * @param {string=} errMsg Defaults to empty string
         * @param {boolean=} isOptional Defaults to false
         */
        assert.isFunction = function (fn, fnName, errMsg, isOptional) {

            errMsg = errMsg || '';
            isOptional = !!isOptional;

            if (!(fn === undefined && isOptional)) {
                assert(!_.isUndefined(fn),
                    errMsg + fnName + ' must be provided.',
                    ReferenceError
                );
                assert(_.isFunction(fn),
                    errMsg + fnName + ' must be a function.',
                    TypeError
                );
            }
        };

        /**
         * Validates that a variable is a function
         *
         * @param {string} obj
         * @param {string} objName
         * @param {string=} errMsg Defaults to empty string
         * @param {boolean=} isOptional Defaults to false
         */
        assert.isObject = function (obj, objName, errMsg, isOptional) {

            errMsg = errMsg || '';
            isOptional = !!isOptional;

            if (!(obj === undefined && isOptional)) {
                assert(!_.isUndefined(obj),
                    errMsg + objName + ' must be provided.',
                    ReferenceError
                );
                assert(_.isObject(obj),
                    errMsg + objName + ' must be an object.',
                    TypeError
                );
            }
        };

        return assert;
    }

    angular.module('Fortscale.shared.services.assert', [])
        .factory('assert', assertFactory)
        // This constant declaration is a hack made to expose this service to config phase functions
        .constant('assertConstant', assertFactory());
}());
