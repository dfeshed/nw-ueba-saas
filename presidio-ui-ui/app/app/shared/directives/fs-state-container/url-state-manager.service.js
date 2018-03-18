(function () {
    'use strict';

	function UrlStateManager ($location, objectUtils) {

		/**
		 * The object returned by $location.search
		 *
		 * @type {object|null}
		 * @private
		 */
		this._locationSearchObject = null;

		/**
		 * The object built for the app's use.
		 * Where the _locationSearchObject might hold something like {'state1.value1': 'someValue'}
		 * this object will hole {state1: {value1: 'someValue'}};
		 *
		 * @type {object|null}
		 * @private
		 */
		this._searchObject = null;


		/**
		 * This method recursively builds _searchObject.
		 * It will turn {'state1.value1': 'someValue'} into
		 * {state1: {value1: 'someValue'}}
		 *
		 * @param {object} obj The object that will host the values
		 * @param {string} path Dot delimited namespace
		 * @param {*} value The value to be hosted
		 * @returns {object}
		 * @private
		 */
		this._setValueByPath = function (obj, path, value) {

            function parseValue (value) {
                try {
                    return JSON.parse(value);
                } catch (e) {
                    return value;
                }
            }

			// Split path into path nodes
			var pathNodes = path.split('.');

			// If there is only one node, then the object will hold the node as
			// key, and set value to it. This ends the recursion.
			if (pathNodes.length === 1) {
				obj[pathNodes] = parseValue(value);
				return obj;
			}

			// Take the first node out of the array
			var currentPathNode = pathNodes.shift();
			// Set a new object to the path (or use existing object)
			// If for any reason the previous value is not an object,
			// The value will be overridden.
			obj[currentPathNode] = angular.isObject(obj[currentPathNode]) ?
				obj[currentPathNode] : {};

			// Rejoin the path into dot delimited string, and run the recursion with
			// The relevant object, the relevant path, and the value.
			this._setValueByPath(obj[currentPathNode], pathNodes.join('.'), value);
		};

		/**
		 * Returns the interpreted search object
		 *
		 * @returns {Object|null}
		 * @private
		 */
		this._getSearchObject  = function () {
			var self = this;

			// Get angular's search object
			var searchObject = $location.search();

			// Check if the angular's search object and the stored search object
			// are the same. If they are, this means that the interpreted search
			// object is valid and can be returned without reinterpreting the
			// search object
			if (angular.equals(self._locationSearchObject, searchObject)) {
				return self._searchObject;
			}

			// This branch happens if the stored locationSearchObject and angular's
			// Search object are not the same. This means that _searchObject is no
			// longer valid, and needs to be reinterpreted.

			// Create a new object for _searchObject
			self._searchObject = {};

			// Set the new searchObject to _locationSearchObject for future comparison
			self._locationSearchObject = searchObject;


            objectUtils.createFromFlattened(searchObject, self._searchObject);

            return self._searchObject;

		};

		/**
		 * Takes a containerId and returns the state object of that container.
		 *
		 * @param {string} containerId The name (id) of the stateContainer.
		 * @returns {*|null}
		 */
		this.getStateByContainerId = function (containerId) {

			// Get the entire state
			var state = this._getSearchObject();

			// Return the relevant state object
			return angular.isDefined(state[containerId]) ? state[containerId] : null;
		};

		/**
		 * Takes stateId (state container name) and a paramId (param name)
		 * and sets the value to the search string in the url.
		 * A new history will be added.
		 *
		 * @param {string} containerId The name of the state
		 * @param {string} paramId The name of the paramater
		 * @param {*} value Any value to be stored
		 */
		this.updateUrlStateParameter = function (containerId, paramId, value) {
			// Create the key string
            var hashMap = {};
            hashMap[paramId] = value;

            var futureState = objectUtils.flattenToNamespace(hashMap, containerId);

            // Merge futureState into current state (so current state will not be lost)

            futureState = angular.merge({}, $location.search(), futureState);

			// Set the value to the search string
			$location.search(futureState);
		};

		/**
		 * Takes stateId (state container name) and a hashMap object
		 * and sets the values of the hashMap to the url search string.
		 *
		 * @param containerId The name of the state
		 * @param {object} hashMap A key-value object, where the key is the paramId
		 */
		this.updateUrlStateParameters = function (containerId, hashMap, rebuildStateCompletly) {

			// Create a new object for the future state
			var futureState = objectUtils.flattenToNamespace(hashMap, containerId);

            if (rebuildStateCompletly) {
                var locationWithoutState = _.pickBy($location.search(), function (value, key) {
                    return !_.startsWith(key, containerId);

                });
                futureState = angular.merge({}, locationWithoutState, futureState);

            } else {
                // Merge futureState into current state (so current state will not be lost)
                futureState = angular.merge({}, $location.search(), futureState);
            }
			// Set new search string
			$location.search(futureState);
		};


	}
    UrlStateManager.$inject = [
        '$location',
        'objectUtils'
    ];
	angular.module('Fortscale.shared.fsStateContainer.urlStateManager', [
        'Fortscale.shared.services.objectUtils'
    ])
		.service('urlStateManager', UrlStateManager);
}());
