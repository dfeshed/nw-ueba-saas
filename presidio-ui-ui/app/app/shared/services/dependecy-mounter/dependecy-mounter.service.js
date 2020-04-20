(function () {
	'use strict';

	/**
	 * The purpose of this service is to provide an alternative way for injecting dependencies.
	 *
	 *
	 * @param {object} $injector Angular service for injecting dependencies.
	 * @constructor
	 */
	function DependencyMounter ($injector) {

		/**
		 * PRIVATE PROPERTIES
		 */

		/**
		 * Holds the error message start.
		 *
		 * @type {string}
		 * @private
		 */
		this._errorMsgStart = 'Fortscale.shared.services.dependencyMounter: ';

		/**
		 * PRIVATE VALIDATIONS
		 */

		/**
		 * Validates a Constructor function
		 *
		 * @param {string} methodName The name of the method requesting validation
		 * @param {function} Constructor The constructor function being validated
		 * @private
		 */
		this._validateConstructor = function _validateConstructor (methodName, Constructor) {

			// error msg start
			var errMsgStart = this._errorMsgStart + methodName + ': ';

			// Validate Constructor is received.
			// If it's not received, throw ReferenceError
			if (!angular.isDefined(Constructor)) {
				throw new ReferenceError(errMsgStart + 'Constructor argument must be provided.');
			}

			// Validate Constructor is a function
			// If not throw Type error.
			if (!angular.isFunction(Constructor)) {
				throw new TypeError(errMsgStart + 'Constructor argument must be a function.');
			}
		};

		/**
		 * Validates a Constructor
		 *
		 * @param {string} methodName The name of the method requesting validation
		 * @param {object} instance The instance object being validated
		 * @private
		 */
		this._validateInstance = function _validateInstance (methodName, instance) {

			// error msg start
			var errMsgStart = this._errorMsgStart + methodName + ': ';

			// Validate instance is received.
			// If it's not received, throw ReferenceError
			if (!angular.isDefined(instance)) {
				throw new ReferenceError(errMsgStart + 'instance argument must be provided.');
			}

			// Validate instance is an object. If it isn't throw TypeError.
			if (!angular.isObject(instance)) {
				throw new TypeError(errMsgStart + 'instance argument must be an object.');
			}
		};

		/**
		 * Validates an array of strings
		 *
		 * @param {string} methodName The name of the method requesting validation
		 * @param {Array<string>} arrDependencies The instance object being validated
		 * @private
		 */
		this._validateArrDependencies = function _validateArrDependencies (methodName, arrDependencies) {

			// error msg start
			var errMsgStart = this._errorMsgStart + methodName + ': ';

			// Validate arrDependencies is received.
			// If not throw ReferenceError
			if (!angular.isDefined(arrDependencies)) {
				throw new ReferenceError(errMsgStart + 'arrDependencies argument must be provided.');
			}

			// validate arrDependencies is an array.
			// If not, throw TypeError
			if (!angular.isArray(arrDependencies)) {
				throw new TypeError(errMsgStart + 'arrDependencies argument must be an array.');
			}

			// Validate all members are strings.
			// If not throw TypeError
			angular.forEach(arrDependencies, function (dependecy, index) {
				if (!angular.isString(dependecy)) {
					throw new TypeError(errMsgStart + 'arrDependencies array member ' + index + ' is not a string.' +
						' All members must be strings.');
				}
			});
		};


		/**
		 * PRIVATE METHODS
		 */


		/**
		 * Iterate through arrDependencies and get the dependency for each, then mount
		 * that dependency on the object if that key is undefined.
		 *
		 * @param {object} obj The object that the dependencies are to be mounter on
		 * @param {Array<string>} arrDependencies The list of dependency names
		 * @private
		 */
		this._mountOnObject = function _mountOnObject (obj, arrDependencies) {
			// Iterate through dependencies and mount each dependency on prototype (if not already there)
			angular.forEach(arrDependencies, function (dependency) {
				if (!angular.isDefined(obj[dependency])) {
					obj[dependency] = $injector.get(dependency);
				}
			});
		};

		/**
		 * Iterate through arrDependencies, and for each dependency name, it gets the dependency,
		 * and uses angular.extend to extend the object.
		 *
		 * @param {object} obj The object that the dependencies are to be mounter on
		 * @param {Array<string>} arrDependencies The list of dependency names
		 * @private
		 */
		this._extendObject = function _extendObject (obj, arrDependencies) {
			// Iterate through dependencies and mount each dependency on prototype (if not already there)
			angular.forEach(arrDependencies, function (dependency) {
				angular.extend(obj, $injector.get(dependency));
			});
		};

		/**
		 * PUBLIC METHODS
		 */

		/**
		 * Takes a constructor function and a list of dependency names, gets the dependencies and mounts them on the
		 * constructor's prototype
		 *
		 * @param {function} Constructor
		 * @param {Array<string>} arrDependencies An array of string where each string is a name of a dependency
		 */
		this.mountOnConstructor = function mountOnConstructor (Constructor, arrDependencies) {

			// Validations
			this._validateConstructor('mountOnConstructor', Constructor);
			this._validateArrDependencies('mountOnConstructor', arrDependencies);

			// Start mounting process
			this._mountOnObject(Constructor.prototype, arrDependencies);

		};

		/**
		 * Takes an instance object and a list of dependency names, gets the dependencies and mounts them on the
		 * constructor's prototype
		 *
		 * @param {object} instance A controller's instance
		 * @param {Array<string>} arrDependencies An array of string where each string is a name of a dependency
		 */
		this.mountOnInstance = function mountOnInstance (instance, arrDependencies) {

			// Validations
			this._validateInstance('mountOnInstance', instance);
			this._validateArrDependencies('mountOnInstance', arrDependencies);

			// Start mounting process
			this._mountOnObject(instance, arrDependencies);
		};

		this.extendConstructor = function extendConstructor (Constructor, arrDependencies) {

			// Validations
			this._validateConstructor('mountOnConstructor', Constructor);
			this._validateArrDependencies('mountOnConstructor', arrDependencies);

			// Start mounting process
			this._extendObject(Constructor.prototype, arrDependencies);
		};

		this.extendInstance = function extendInstance (instance, arrDependencies) {

			// Validations
			this._validateInstance('mountOnInstance', instance);
			this._validateArrDependencies('mountOnInstance', arrDependencies);

			// Start mounting process
			this._extendObject(instance, arrDependencies);
		};
	}

	DependencyMounter.$inject = ['$injector'];

	angular.module('Fortscale.shared.services.dependencyMounter', [])
		.service('dependencyMounter', DependencyMounter);
}());
