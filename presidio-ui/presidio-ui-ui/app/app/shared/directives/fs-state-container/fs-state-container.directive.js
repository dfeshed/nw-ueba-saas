(function () {
    'use strict';

    /**
     * Holds a default state adapter. Used when fetching data. Causes any _NONE_ or _ALL_ values
     * to be null
     *
     * @type {Array}
     */
    var DEFAULT_STATE_ADAPTER = [
        {
            queryValue: '_NONE_',
            changeTo: null
        },
        {
            queryValue: '_ALL_',
            changeTo: null
        }
    ];

    /**
     * StateContainerController constructor
     *
     * @param {angular.$scope} $scope
     * @param {angular.$element} $element
     * @param {object} dependencyMounter
     * @constructor
     */
    function StateContainerController ($scope, $element, dependencyMounter) {
        var ctrl = this;

        ctrl._isStateInitialized = false;

        ctrl._init($scope, $element, dependencyMounter);

        /**
         * Used as a delegate for anything that wants to notify pre-state to state change.
         * It's an instance property so it would be possible to preserve binding to the instance.
         *
         */
        ctrl.applyPreState = function () {
            return ctrl._applyPreState();
        };

        /**
         * Used as a delegate for anything that wants to update a specific control state
         * It's an instance property so it would be possible to preserve binding to the instance.
         *
         * @param {{id: string, type: string, value: *, immediate: boolean=}} ctrlState
         */
        ctrl.updateCtrlState = function (ctrlState) {
            return ctrl._updateCtrlState(ctrlState);
        };

        /**
         * Used as a delegate for anything that needs to get its own state from stateModel
         * Returns the value of a specific property on the state object.
         * It's an instance property so it would be possible to preserve binding to the instance.
         *
         * @param {string} controlId
         * @returns {*}
         */
        ctrl.fetchStateById = function (controlId) {
            return ctrl._fetchStateById(controlId);
        };

        /**
         * Start data fetching service, and places the result on the control instance as dataModel
         * Used as a delegate by anything that needs to refresh the data.
         * It's an instance property so it would be possible to preserve binding to the instance.
         *
         * @returns {HttpPromise}
         */
        ctrl.refreshData = function () {
            return ctrl._fetchData();
        };
    }

    // Controller prototype
    angular.extend(StateContainerController.prototype, {

        /**
         * PRIVATE METHODS
         */

        /**
         * Initiates state object by duplicating provided stateModel
         * and placing it on instance as stateModel
         *
         * @private
         */
        _initState: function () {
            this.stateModel = null;
        },

        /**
         * Initiates the child state controllers reference list on instance
         * as _childStateContainerCtrls
         *
         * @private
         */
        _initChildStateRefList: function () {
            this._childStateContainerCtrls = [];
        },

        /**
         * Initiates the parent state controllers reference on instance
         * as _parentStateContainerCtrl
         *
         * @private
         */
        _initParenStateRef: function () {
            this._parentStateContainerCtrl = null;
        },

        /**
         * Inits a resource, using resourceFactory
         *
         * @private
         */
        _initResource: function () {

            if (this._resourceSettings) {

                // Validate resource settings if not string
                if (!_.isString(this._resourceSettings)) {
                    this._validateResourceSettings('_initResource', this._resourceSettings);
                }

                // interpolate the settings
                var interpolated = this.interpolation.interpolate(this._resourceSettings,
                    this.stateModel);

                // create the resource and store it as _resource
                this._resource = this.resourceFactory.create(interpolated.entity);

            }
        },

        /**
         * Registers a (potentially) parent StateContainerController controller
         * by placing it on instance as _parentStateContainerCtrl
         *
         * @param  {StateContainerController} stateContainerCtrl
         * @private
         */
        _registerParentController: function (stateContainerCtrl) {
            var errMsgStart = 'StateContainerController: _registerParentController: ';
            // Validate stateContainerCtrl is an instance of StateContainerController
            if (!(stateContainerCtrl instanceof StateContainerController)) {
                throw new TypeError(errMsgStart +
                    'stateContainerCtrl argument must be an instance of StateContainerController.');
            }

            this._parentStateContainerCtrl = stateContainerCtrl;
        },

        /**
         * Iterates through _childStateContainerCtrls,
         * and for each child-control invokes updateState(this.stateModel, doNotOverride)
         *
         * @param {boolean=} doNotOverride If true, the new state will not override existing values.
         * @param {boolean=} doNotFetchData If true, _fetchData will not be invoked.
         * @param {boolean=} doGetFromUrl If true, merge with state from url
         * @param {object=} alterState An object that should be used instead of the instance's
         * stateModel
         * @private
         */
        _updateChildStates: function (doNotOverride, doNotFetchData, doGetFromUrl, alterState) {
            var ctrl = this;

            angular.forEach(this._childStateContainerCtrls, function (childCtrl) {

                // This is used to determine which state to pass
                var targetState = alterState || ctrl.stateModel;

                // if true will tell updateState to permeate only the received state,
                // and not the entire state
                var doPermeateGottenState = angular.isDefined(alterState);

                childCtrl.updateState(targetState, doNotOverride, doNotFetchData, doGetFromUrl,
                    doPermeateGottenState);
            });
        },

        /**
         * Iterates through _childStateContainerCtrls,
         * and for each ctrl it invokes its updateCtrlsState.
         *
         * @param {{id: string, type: string, value: *, immediate: boolean=}} ctrlState
         * @private
         */
        _ctrlUpdateChildStates: function (ctrlState) {
            angular.forEach(this._childStateContainerCtrls, function (ctrl) {
                ctrl._updateCtrlState(ctrlState, true);
            });
        },

        /**
         * Checks if the instance has a parent controller
         *
         * @returns {boolean} true if instance has no parent controller
         * @private
         */
        _isRootState: function () {
            return !this._parentStateContainerCtrl;
        },

        /**
         * For each data fetching type, fetch data and return an http promise
         *
         * @returns {Promise}
         * @private
         */
        _initDataFetchingService: function () {
            // Validate
            this.assert(angular.isDefined(this._resourceSettings), this._validateStartMessage +
                ' _initDataFetchingService: _resourceSettings must be defined.', ReferenceError);

            // interpolate the settings

            var interpolated = this.interpolation.interpolate(this._resourceSettings,
                this.stateModel, this._stateAdapter);

            this.objectUtils.removeNulls(interpolated.params, '');

            // If it has an id then we need to get a single resource
            if (interpolated.id) {
                return this._resource.get(interpolated.id, interpolated.params);
                // Get list
            } else {
                return this._resource.getList(interpolated.params);
            }
        },
        /**
         * Checks if the state container needs to get data.
         * It checks if it has any _queryTemplate
         *
         * @returns {boolean}
         * @private
         */
        _isDataRequired: function () {
            return !!this._resource;
        },

        /**
         * Start data fetching service, and places the result on the control instance as dataModel
         *
         * @private
         */
        _fetchData: function () {
            var ctrl = this;

            ctrl.errorModel = null;
            ctrl.isLoading = true;

            let promise = ctrl._initDataFetchingService()
                .then(function (data) {
                    if (ctrl._resourceAdapter) {
                        return ctrl._resourceAdapter(data);
                    }

                    return data;
                })
                .then(function (data) {
                    ctrl.isLoading = false;
                    ctrl.dataModel = data;
                })
                .catch(function (res) {
                    ctrl.isLoading = false;
                    ctrl.dataModel = null;

                    ctrl.errorModel = res || {
                            message: 'Connection error'
                        };

                    ctrl.$log.error(ctrl.errorModel);
                });

            // Give back promise to dataFetchDelegate
            ctrl.onDataFetch({promise: promise});

            return promise;
        },

        /**
         * Should be invoked when the controller is root controller
         * and data permeation process needs to begin.
         * Updates all child states.
         *
         * @private
         */
        _startInitialStateUpdate: function () {

            // Get state from url if state has not yet initialized
            if (!this._isStateInitialized) {
                var urlState = this.urlStateManager.getStateByContainerId(this.containerId);
                var convertedUrlState;

                if (urlState) {
                    var urlStateKeys = Object.keys(urlState);
                    convertedUrlState = {};

                    urlStateKeys.forEach(function (urlStateKey) {
                        convertedUrlState[urlStateKey] = {
                            value: urlState[urlStateKey]
                        };
                    });
                }

                this.stateModel = angular.merge(this.stateModel, convertedUrlState);
            }

            // Update all children states
            this._updateChildStates(true, false, true);

            // Get data if required and _isStateInitialized is false
            // We use '_isStateInitialized is false' because there can be several calls to
            // _startInitialStateUpdate in the initial render of the page.
            // So !this._isStateInitialized will make sure no unwanted calls to the server are made
            if (this._isDataRequired() && !this._isStateInitialized) {
                this._fetchData();
            }

            // Set flag to let everyone in contact with this container know that it's state has
            // been initialized.
            this._isStateInitialized = true;
        },
        /**
         * Validation error message start
         * @type string
         * @private
         */
        _validateStartMessage: 'fs-state-container: ',

        /**
         * Validates ctrlState object. It verifies id, and type.
         *
         * @param {string} caller
         * @param {{id: string, type: string, value: *, immediate: boolean=}} ctrlState
         * @private
         */
        _validateCtrlState: function (caller, ctrlState) {

            var message = this._validateStartMessage + caller + ': ';

            // Validate ctrlState object
            this.assert(angular.isDefined(ctrlState),
                message + 'ctrlState argument must be provided.', ReferenceError);
            this.assert(angular.isObject(ctrlState),
                message + 'ctrlState argument must be an object.', TypeError);

            // Validate ctrlState.id
            this.assert(angular.isDefined(ctrlState.id), message +
                'ctrlState argument must have an "id" property.', ReferenceError);
            this.assert(angular.isString(ctrlState.id), message +
                'ctrlState.id must be a string.', TypeError);
            this.assert(ctrlState.id !== '', message +
                'ctrlState.id must not be an empty string.', RangeError);

            // Validate ctrlState.type
            this.assert(angular.isDefined(ctrlState.type), message +
                'ctrlState argument must have an "type" property.', ReferenceError);
            this.assert(angular.isString(ctrlState.type), message +
                'ctrlState.type must be a string.', TypeError);
            // Validate against known types which are listed on control-types.const.js
            var controlTypes = Object.keys(this.controlTypes);
            this.assert(controlTypes.indexOf(ctrlState.type.toUpperCase()) > -1,
                message + 'ctrlState.type must be a valid type: ' + controlTypes.join(', ') +
                ', and it is "' + ctrlState.type.toUpperCase() + '"', RangeError);

        },

        _validateResourceSettings: function (caller, resourceSettings) {
            var message = this._validateStartMessage + caller + ': ';

            // resource settings must have an entity string
            this.assert(angular.isDefined(resourceSettings.entity), message +
                'resourceSettings must have an "entity" property.', ReferenceError);
            this.assert(angular.isString(resourceSettings.entity), message +
                'resourceSettings.entity must be a string.', TypeError);
            this.assert(resourceSettings.entity !== '', message +
                'resourceSettings.entity must not be an empty string.', RangeError);

            // If resourceSettings.id is defined, it must be a non empty string
            if (angular.isDefined(resourceSettings.id)) {
                this.assert(angular.isString(resourceSettings.id), message +
                    'resourceSettings.id must be a string.', TypeError);
                this.assert(resourceSettings.id !== '', message +
                    'resourceSettings.id must not be an empty string.', RangeError);
            }

        },

        /**
         * Gets the specific state object for this ctrl (by id).
         * Iterates through the url state keys, and sets the values on the stateModel.
         *
         * @private
         */
        _mergeUrlState: function () {

            var ctrl = this;

            // Get this container's state from the url
            var urlState = ctrl.urlStateManager.getStateByContainerId(ctrl.containerId);

            // If this container has no state then early return
            if (!urlState) {
                return;
            }

            // Get the url state's keys
            var urlStateKeys = Object.keys(urlState);

            // Iterate through the keys
            urlStateKeys.forEach(function (urlStateKey) {

                // Set the object's value property to the value of urlState[urlStateKey]
                // Example: If ctrl.stateModel is {},
                // and urlState is {control: 'someValue'} then
                // ctrl.stateModel will hold {control1: {value: 'someValue'}}

                // For each key create an object if one does not exist
                ctrl.stateModel[urlStateKey] = ctrl.stateModel[urlStateKey] ?
                    ctrl.stateModel[urlStateKey] : {};
                // For each key set the value
                ctrl.stateModel[urlStateKey].value = urlState[urlStateKey];
            });

        },

        /**
         * Takes an object that is the target state, a state object, and a property name.
         * The method will set the targetState with the preValue value of the state's object
         * property. For example targetState = {}, state = {control1: {preValue: 'someValue'}},
         * stateProperty = 'control1'. After this function targetState will look like this:
         * targetState = {control1: {value: 'someValue'}}
         *
         * @param {object} targetState
         * @param {object} state
         * @param {string} stateProperty
         * @private
         */
        _populateActionStateObject: function (targetState, state, stateProperty) {

            // Create container object if one does not exist
            targetState[stateProperty] = targetState[stateProperty] ?
                targetState[stateProperty] : {};

            // Put preValue into stateObject that should be permeated
            targetState[stateProperty].value = state[stateProperty].preValue;

        },

        /**
         * Takes a name of a property (on stateModel) and deletes its property's preValue property.
         *
         * @param {string} stateProperty
         * @private
         */
        _deletePreValue: function (stateProperty) {
            delete this.stateModel[stateProperty].preValue;
        },

        /**
         * Takes a targetState object, iterates through it to create a hashMap and updates the url
         *
         * @param {object} targetState
         * @private
         */
        _applyTargetStateToUrl: function (targetState) {

            // Convert targetState format to urlState format
            var urlTargetState = {};
            var targetStateKeys = Object.keys(targetState);
            targetStateKeys.forEach(function (targetStateKey) {
                if (targetState[targetStateKey].value !== undefined) {
                    urlTargetState[targetStateKey] = targetState[targetStateKey].value;
                }
            });

            // Update url if urlTargetState has keys
            if (Object.keys(urlTargetState).length) {
                this.urlStateManager
                    .updateUrlStateParameters(this.containerId, urlTargetState);
            }
        },
        /**
         * Applies the pre-state.
         * Iterates through stateModel and applies the pre-value state to the state.
         *
         * @private
         */
        _applyPreState: function () {

            var ctrl = this;

            var actionState = {};
            var isActionStateUpdated = false;

            //iterate through stateModel
            var stateProperties = Object.keys(ctrl.stateModel);

            //forEach property, check if it has preValue defined
            stateProperties.forEach(function (stateProperty) {

                if (angular.isDefined(ctrl.stateModel[stateProperty].preValue)) {

                    // Set flag that state was updated for future reference
                    if (!isActionStateUpdated) {
                        isActionStateUpdated = true;
                    }

                    // Put preValue into stateObject that should be permeated
                    ctrl._populateActionStateObject(actionState, ctrl.stateModel, stateProperty);

                    // Delete preValue
                    ctrl._deletePreValue(stateProperty);

                }
            });

            // Only update state if any pre-state were digested
            if (isActionStateUpdated) {

                // update Url

                ctrl._applyTargetStateToUrl(actionState);
                // Update state
                ctrl.updateState(actionState, false, false, false, true);
            }

        },

        /**
         * Takes a ctrlState object, and updates the state based on the id of the control.
         * If update is flagged as immediate, state permeation will begin.
         * If update is flagged as not DATA (ctrlState.type), then permeation process will be
         * flagged with doNotFetchData, which would mean not data will be fetched.
         *
         *
         * @param {{id: string, type: string, value: *, immediate: boolean=}} ctrlState
         * @param {boolean=} invokedByParent If true no validation and url update will happen
         */
        _updateCtrlState: function (ctrlState, invokedByParent) {

            // Validate (if invokedByIterator is not true) ctrlState
            if (!invokedByParent) {
                this._validateCtrlState('updateCtrlState', ctrlState);
            }

            // Make sure control object exists
            if (!this.stateModel) {
                this.stateModel = {};
            }
            if (!angular.isDefined(this.stateModel[ctrlState.id])) {
                this.stateModel[ctrlState.id] = {};
            }

            // Create path to be updated.
            var path = ctrlState.id + (ctrlState.immediate ? '.value' : '.preValue');

            // Update the state
            this.updateStateByPath(path, ctrlState.value);

            // If update is flagged as immediate,
            // and this function was not invoked by updateCtrlsState.
            if (ctrlState.immediate) {

                // If not invoked by a parent controller then update the url
                if (!invokedByParent) {
                    this.urlStateManager
                        .updateUrlStateParameter(this.containerId, ctrlState.id, ctrlState.value);
                }

                // Update children
                this._ctrlUpdateChildStates(ctrlState);

                // If isDataRequired and is of type data, fetch data
                if (this._isDataRequired() &&
                    ctrlState.type.toUpperCase() === this.controlTypes.DATA) {
                    this._fetchData();
                }
            }

        },
        /**
         * Returns the value of a specific property on the state object.
         * The value returned is stateModel[controlId].value or null if
         * stateModel[controlId] is undefined.
         *
         * @param {string} controlId
         * @returns {*|null}
         */
        _fetchStateById: function (controlId) {
            if (this.stateModel && this.stateModel[controlId]) {

                if (this.stateModel[controlId].value !== undefined) {
                    return this.stateModel[controlId].value;
                }

            }

            return null;

        },

        /**
         * PUBLIC METHODS
         */

        /**
         * Updates the state model on the instance, and all its subsequent children
         * If it needs to get data (_isDataRequired), _fetchData is invoked.
         *
         * @param {object} state The state to be digested
         * @param {boolean=} doNotOverride If true, the new state will not override existing values.
         * @param {boolean=} doNotFetchData If true, _fetchData will not be invoked.
         * @param {boolean=} doGetFromUrl If true, merge with state from url
         * @param {boolean=} doPermeateGottenState If true, permeates only the recieved state and
         * not its entire instance's state. Used when permeating a cluster of pre-values.
         */
        updateState: function (state, doNotOverride, doNotFetchData, doGetFromUrl,
            doPermeateGottenState) {
            if (doNotOverride) {
                this.stateModel = angular.merge({}, state, this.stateModel);
            } else {
                this.stateModel = angular.merge({}, this.stateModel, state);
            }

            if (doGetFromUrl) {
                // Get state from url
                this._mergeUrlState();
            }

            this._initResource();

            // update children states
            if (doPermeateGottenState) {
                this._updateChildStates(doNotOverride, doNotFetchData, doGetFromUrl, state);
            } else {
                this._updateChildStates(doNotOverride, doNotFetchData, doGetFromUrl);
            }

            // Get data if required
            if (this._isDataRequired() && !doNotFetchData) {
                this._fetchData();
            }

            this._isStateInitialized = true;

        },

        /**
         * Returns a value from state model based on a received path
         *
         * @returns {object}
         */
        fetchStateByPath: function (path) {

            // $parse returns a link function.
            // The link function is invoked with stateModel as context.
            return this.$parse(path)(this.stateModel);
        },

        /**
         * Provides a precision tool for setting a value in a specific path in the state
         *
         * Please do not delete this function for now even if its not in use.
         * If you do, angles will loose their wings.
         * Puppies will die. Rivers will dry. Mothers will cry. Grandmas will sigh.
         * Delete after september 2015 (only if not in use)
         *
         * @param {string} path
         * @param {*} value
         */
        updateStateByPath: function (path, value) {

            // Puts temporarily the value to assign on __assignValue
            this.stateModel.__assignValue = value;

            // Create the assignment expression to be parsed
            var assignmentExpression = path + '=__assignValue';

            // $parse returns a link function.
            // The link function is invoked with stateModel as context.
            this.$scope.$eval(assignmentExpression, this.stateModel);

            // Remove temporary property
            delete this.stateModel.__assignValue;
        },

        /**
         * Registers a (potentially) child StateContainerController controller
         * by adding it to _childStateContainerCtrls
         *
         * @param {StateContainerController} childStateContainerCtrl
         */
        registerChildController: function (childStateContainerCtrl) {
            var errMsgStart = 'StateContainerController: _registerChildController: ';
            // Validate childStateContainerCtrl is an instance of StateContainerController
            if (!(childStateContainerCtrl instanceof StateContainerController)) {
                throw new TypeError(errMsgStart +
                    'stateContainerCtrl argument must be an instance of StateContainerController.');
            }

            // Verify it is not already on the array
            if (this._childStateContainerCtrls.indexOf(childStateContainerCtrl) > -1) {
                return;
            }

            // Add stateContainerCtrl to list
            this._childStateContainerCtrls.push(childStateContainerCtrl);
        },

        /**
         * Removes child state container controller from _childStateContainerCtrls if its there.
         * Returns true if successfully spliced, and false if not spliced.
         *
         * @param {StateContainerController} childStateContainerCtrl
         * @returns {boolean}
         */
        unregisterChildController: function (childStateContainerCtrl) {
            var pos = this._childStateContainerCtrls.indexOf(childStateContainerCtrl);

            if (pos === -1) {
                return false;
            }

            this._childStateContainerCtrls.splice(pos, 1);
            return true;
        },

        /**
         * Init function
         *
         * @private
         */
        _init: function _init ($scope, $element, dependencyMounter) {
            this._initState();
            this._initChildStateRefList();
            this._initParenStateRef();
            this.$scope = $scope;
            this.$element = $element;

            // This will place on the prototype the following dependencies.
            // Dependencies can be accessed via the instance's 'this'
            dependencyMounter.mountOnConstructor(StateContainerController, [
                '$parse', 'assert', 'controlTypes', 'urlStateManager', '$log',
                'resourceFactory', 'interpolation', 'objectUtils'
            ]);

        }
    });

    function fsStateContainer (assert) {

        function linkFn (scope, element, attr, parentCtrl) {

            /**
             * VARIABLES
             */

            // Reference the controller
            var ctrl = scope.stateContainer;

            /**
             * METHODS
             */

            /**
             * Register parent and child controllers if parent controller exists.
             */
            function registerCtrls () {
                if (parentCtrl) {
                    ctrl._registerParentController(parentCtrl);
                    parentCtrl.registerChildController(ctrl);
                }
            }

            /**
             * Stores the value of queryTemplate attribute onto _queryTemplate
             */
            function initStateValues () {

                // We use this method to create a bind-value-once-one-way :)
                //ctrl.stateModel = scope.$eval(attr.stateModel);
                ctrl.stateModel = angular.merge({}, ctrl._stateModel);

            }

            /**
             * Stores the value of queryTemplate attribute onto _queryTemplate
             */
            function initQueryValues () {

                // We use this method to create a bind-value-once-one-way :)
                ctrl._queryTemplate = scope.$eval(attr.queryTemplate);
            }

            /**
             * Creates a local state adapter from DEFAULT_STATE_ADAPTER and a received state adapter
             */
            function initStateAdapter () {
                ctrl._stateAdapter = _.merge({}, DEFAULT_STATE_ADAPTER, ctrl.stateAdapter);
            }

            /**
             * WATCHERS
             */
            /**
             * Init watch on destroy
             */
            function initWatchDestroy () {

                // When invoked will remove the current controller from the parent
                function unregisterChildFromParent () {
                    if (parentCtrl) {
                        parentCtrl.unregisterChildController(ctrl);
                    }
                }

                // When invoked will cause a $destroy on the current scope
                function destroyScope () {
                    scope.$destroy();
                }

                scope.$on('$destroy', unregisterChildFromParent);

                // Make sure that if element is removed (with element.remove())
                // its scope will be destroyed
                element.on('$destroy', destroyScope);
            }

            /**
             * Inits all watchers
             */
            function initWatchers () {
                initWatchDestroy();
            }

            /**
             * INIT
             */
            function init () {
                assert(angular.isDefined(attr.containerId),
                    'fsStateContainer: linkFn: ' +
                    'directive must be provided with a containerId', ReferenceError);

                // Register the parent controller if one exists.
                registerCtrls();

                // Init values
                initStateValues();
                initQueryValues();
                initStateAdapter();

                // Init resource
                ctrl._initResource();

                // Init watchers
                initWatchers();

                // Start initial state update if state is root
                if (ctrl._isRootState()) {
                    ctrl._startInitialStateUpdate();
                }

                // If the parent has an _isStateInitialized flag set to true,
                // this means that state permeation has already occurred for the parent.
                // In this case, this ctrl must start its own initialization process.
                if (ctrl._parentStateContainerCtrl &&
                    ctrl._parentStateContainerCtrl._isStateInitialized) {
                    // Get the parent's state
                    ctrl.stateModel = angular.merge({}, ctrl._parentStateContainerCtrl.stateModel,
                        ctrl.stateModel);
                    ctrl._startInitialStateUpdate();
                }

            }

            init();
        }

        StateContainerController.$inject = ['$scope', '$element', 'dependencyMounter'];

        return {
            restrict: 'E',
            scope: true,
            require: '?^^fsStateContainer',
            controller: StateContainerController,
            controllerAs: 'stateContainer',
            bindToController: {
                _stateModel: '=stateModel',
                _resourceSettings: '=resourceSettings',
                _resourceAdapter: '=resourceAdapter',
                stateAdapter: '=stateAdapter',
                containerId: '@containerId',
                onDataFetch: '&'
            },
            link: linkFn
        };
    }

    angular.module('Fortscale.shared.directives.fsStateContainer', [
        'Fortscale.shared.services.dependencyMounter',
        'Fortscale.shared.services.assert',
        'Fortscale.shared.fsStateContainer.controlTypes',
        'Fortscale.shared.fsStateContainer.urlStateManager',
        'Fortscale.shared.services.objectUtils',
        'Fortscale.shared.services',
        'restangular'
    ])
        .directive('fsStateContainer', ['assert', fsStateContainer]);
}());
