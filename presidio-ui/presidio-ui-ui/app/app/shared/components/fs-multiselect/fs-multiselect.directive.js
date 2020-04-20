(function () {
    'use strict';

    /**
     * Holds the global state of all multiselect drop downs. Used to determine if any are opened.
     *
     * @type {boolean}
     * @private
     */
    var _anyDropdownOpened = false;

    function fsMultiselect(assert) {


        /**
         * The link function
         *
         * @param scope
         * @param element
         * @param attrs
         * @param ctrl
         */
        function linkFn(scope, element, attrs, ctrl/**, transclude**/) {

            ctrl._linkInit();

        }

        /**
         * The directive's controller function
         * Added instead of using a Link function
         * Properties are bound on the Controller instance and available in the view
         *
         * @constructor
         */
        function FsMultiselectController($scope, $element, $attrs, $timeout) {
            var ctrl = this;

            // Put dependencies on the controller instance
            this.$scope = $scope;
            this.$element = $element;
            this.$attrs = $attrs;
            this.$timeout = $timeout;

            // Bind to controller instance
            this.windowClickHandler  = function (evt) {
                ctrl._windowClickHandler(evt);
            };

            this._ctrlInit();
        }

        _.merge(FsMultiselectController.prototype, {
            _errMsg: 'fsMultiselect.directive: ',

            /**
             * Validates listData. Validates each item for value id and unique.
             *
             * @param {array<{id: string, value: string}>} listData
             * @param errMsg
             * @private
             */
            _validateListData: function (listData, errMsg) {

                // Validate values and ids
                _.each(listData, function (dataItem) {
                    // validate value
                    assert.isString(dataItem.value, 'dataItem.value', errMsg);

                    // Validate ids
                    assert.isString(dataItem.id, 'dataItem.id', errMsg);

                });

                // Validate uniques
                var values = _.map(listData, 'value');
                var ids = _.map(listData, 'id');

                assert(values.length === _.uniq(values).length,
                    errMsg + 'value property\'s values must be unique.',
                    RangeError
                );

                assert(ids.length === _.uniq(ids).length,
                    errMsg + 'id property\'s values must be unique.',
                    RangeError
                );
            },

            /**
             * Control init validations. Validates directive's arguments
             * @param errMsg
             * @private
             */
            _ctrlValidations: function (errMsg) {
                assert.isString(this.multiselectId, 'multiselectId', errMsg);
                assert.isString(this._initialState, '_initialState', errMsg, true);
                assert.isString(this.label, 'label', errMsg, true);
                assert.isArray(this._listData, 'listData', errMsg);
                this._validateListData(this._listData, errMsg + 'listData argument: ');

                assert.isFunction(this.fetchStateDelegate, 'fetchStateDelegate', errMsg);
                assert.isFunction(this.updateStateDelegate, 'updateStateDelegate', errMsg);
            },

            /**
             * A handler for any window click. Should close the dropdown if the click is outside the dropdown.
             *
             * @param evt
             * @private
             */
            _windowClickHandler: function (evt) {

                // If the click is outside the dropdown - fire cancelClickHandler
                if (!this.$element.find('.fs-multiselect-dropdown-list')[0].contains(evt.target)) {
                    this.cancelClickHandler();
                }

                // If the click is on the drop-down openener, stop propagation so drop down will not reopen
                if (this.$element.find('.fs-multiselect-value')[0].contains(evt.target)) {
                    evt.stopImmediatePropagation();
                }
            },
            /**
             * Initates window click listener
             * @private
             */
            _initWindowClickListener: function () {
                window.addEventListener('click', this.windowClickHandler, true);
            },
            /**
             * Removes window click listener
             * @private
             */
            _removeWindowClickListener: function () {
                window.removeEventListener('click', this.windowClickHandler, true);
            },
            /**
             * Watch state function.
             *
             * @returns {*}
             * @private
             */
            _watchStateFn: function () {
                return this.fetchStateDelegate(this.multiselectId);
            },

            /**
             * Watch state action function.
             * Handles digestion of incoming state (which items are checked), and select all state.
             *
             * @param state
             * @private
             */
            _watchStateActionFn: function (state) {
                if (state) {
                    this._setAllUnchecked();
                    this._digestIncomingState(state);
                    this._setAllSelected();
                } else {
                    this._initAllSelectedState();

                }
            },

            /**
             * Watch list data
             *
             * @returns {*}
             * @private
             */
            _watchListDataFn: function () {
                return this._listData;
            },

            /**
             * Watch list data action function.
             * Handles digestion of incoming state (which items are checked), and select all state.
             *
             * @param state
             * @private
             */
            _watchListDataActionFn: function (listData) {
                if (listData && listData.length > 0) {
                    //this._ctrlInit();
                    this.listData = _.cloneDeep(this._listData);
                    this._watchStateActionFn(this.fetchStateDelegate(this.multiselectId));

                }
            },

            /**
             * Fires when 'control:reset' event is broadcasted.
             * It resets the control to its initial state and updates state.
             *
             * @param event
             * @param eventData
             * @private
             */
            _controlResetRequestHandler: function (event, eventData) {
                var ctrl = this;

                if (eventData.controlId && eventData.controlId === ctrl.multiselectId) {

                    // Since this is like receiving external state,
                    // _watchStateActionFn is the perfect method to use
                    ctrl._watchStateActionFn(ctrl._initialState);

                    // Clear the form
                    ctrl.$scope.dropdownForm.$setPristine();

                    // Get the value to set it to the pre-state
                    var state = ctrl._getMultiselectState();

                    // Set pre-state
                    ctrl.updateStateDelegate({
                        id: ctrl.multiselectId,
                        immediate: this._immediate,
                        type: 'data',
                        value: state
                    });
                }
            },

            /**
             * Handler for scope destroy.
             * Sets _anyDropdownOpened to false.
             *
             * @private
             */
            _scopeDestroyHandler: function () {
                _anyDropdownOpened = false;
                this._removeWindowClickListener();
            },

            /**
             * Initates all relevan watches.
             *
             * @private
             */
            _initWatches: function () {

                var ctrl = this;

                // Init state watch
                ctrl.$scope.$watch(
                    ctrl._watchStateFn.bind(ctrl),
                    ctrl._watchStateActionFn.bind(ctrl)
                );

                //Init list options change watcher
                ctrl.$scope.$watch(
                    ctrl._watchListDataFn.bind(ctrl),
                    ctrl._watchListDataActionFn.bind(ctrl)
                );

                ctrl.$scope.$on('$destroy', ctrl._scopeDestroyHandler.bind(ctrl));

                ctrl.$scope.$on('control:reset', ctrl._controlResetRequestHandler.bind(ctrl));
            },

            /**
             * Before receiving external state, set the default to all selected.
             *initToSelectAll - if true - all be selected, if false, nothing will be selected
             * @private
             */
            _initAllSelectedState: function () {
                var dataItems = this._listData.concat(this.listData);

                var shouldSelectAll = this.lastSelected==='All'; //If selectAll is false, none will be selected
                // Iterate through concatinated array and turn on checked for each item.
                _.each(dataItems, function (dataItem) {
                    dataItem.checked = shouldSelectAll;
                });

                // Put the view value in value property on the controller instance.
                this.value = this._getMultiselectViewValue();

                this.allSelected = shouldSelectAll;
            },

            /**
             * Sel all items as unchecked.
             *
             * @private
             */
            _setAllUnchecked: function () {
                var dataItems = this._listData.concat(this.listData);

                // Iterate through concatinated array and turn on checked for each item.
                _.each(dataItems, function (dataItem) {
                    dataItem.checked = false;
                });
            },
            /**
             * Returns the multiselect state. Multiselect state is a csv string of
             * checked items ids. If all or none are selected, the return value is null.
             *
             * @returns {string|null}
             * @private
             */
            _getMultiselectState: function () {

                // Get all checked items from model state
                var checked = _.filter(this.listData, {checked: true});

                // init value variable
                // If value has an initial state, the value needs to be explicitly '_NONE_'
                // or '_ALL_'
                var value = (this._initialState === undefined) ? null : '_NONE_';

                // If there are any checked and not all are checked in :
                if (checked.length > 0 && checked.length < this.listData.length) {

                    // The value is a csv string of all checked items ids.
                    value = _.map(checked, function (item) {
                        return item.id;
                    }).join(',');
                } else if (checked.length === this.listData.length) {
                    value = (this._initialState === undefined) ? null : '_ALL_';
                }

                // Return a csv or null
                return value;
            },

            /**
             * Receives a list of checked view items. Returns a csv of item.value.
             * Used in 'short' lists.
             *
             * @param {Array<{value: string}>} checkedItems
             * @returns {string}
             * @private
             */
            _getShortViewValue: function (checkedItems) {
                return _.map(checkedItems, function (item) {
                    return item.value;
                }).join('; ');
            },

            keyPress: function(e){
                if (e.keyCode===27){
                    this.okClickHandler();
                }
            },
            /**
             * Receives a list of checked view items. Returns  '1 item' if there is only one item,
             * or it returns '<n> items' if there are more then one item. Used in 'long' lists.
             *
             * @param {Array<{value: string}>} checkedItems
             * @returns {string}
             * @private
             */
            _getLongViewValue: function (checkedItems) {
                if (checkedItems.length > 1) {
                    return checkedItems.length + ' items';
                } else{
                    return '1 item';
                }
            },

            /**
             * Returns the view value to be displayed in the drop down button.
             * If all are selected, 'All' is returned.
             * If none are selected, 'None' is returned.
             * If list is short, a ';' delimited string of item.value is returnd.
             * if list is long, '<n> items' (or '1 item') is returned.
             *
             * @returns {string}
             * @private
             */
            _getMultiselectViewValue: function () {

                // Gets a list of all checked items
                var checked = _.filter(this._listData, {checked: true});

                // If there are any checked items and non all items are checked:
                if (checked.length > 0 && checked.length < this._listData.length) {
                    //Per Uri's request - always show the values, seperated
                    return this._getShortViewValue(checked);
                    //// If list is short: return short view value
                    //if (this._listData.length < this.longListThreshold) {
                    //    return this._getShortViewValue(checked);
                    //
                    //    // If list is long: return long view value
                    //} else {
                    //    return this._getLongViewValue(checked);
                    //}

                    // If all are checked: return 'All'
                } else if (checked.length === this._listData.length) {
                    return 'All';

                    // If none are checked (default) return 'None'
                } else {
                    return 'None';
                }
            },

            /**
             * Takes a csv string, an creates an id list. Turns on 'checked' for each of the
             * received items in both model state and view state.
             *
             * @param {string} state a csv string
             * @private
             */
            _digestIncomingState: function (state) {

                var ids;

                // Get ids from csv
                if (state === '_NONE_') {
                    ids = '';
                } else if (state === '_ALL_') {
                    ids = _.map(this._listData, 'id');
                } else {
                    ids = state.split(',');
                }


                // Iterate through ids
                _.each(ids, _.bind(function (id) {

                    // Create one array that holds both model state objects and view state objects,
                    // that have a received id.
                    // This works because the lists hold objects and changes are made by reference.
                    var dataItems = [];
                    dataItems = dataItems.concat(_.filter(this._listData, {id: id}));
                    dataItems = dataItems.concat(_.filter(this.listData, {id: id}));

                    // Iterate through concatinated array and turn on checked for each item.
                    _.each(dataItems, function (dataItem) {
                        dataItem.checked = true;
                    });
                }, this));

                // Put the view value in value property on the controller instance.
                this.value = this._getMultiselectViewValue();
            },

            /**
             * Checks if all checked are true in a received list.
             *
             * @param {Array<{checked: boolean}>} listData
             * @returns {boolean}
             * @private
             */
            _isAllSelected: function (listData) {
                return _.every(listData, function (dataItem) {
                    return dataItem.checked;
                });
            },

            /**
             * Set allSelected property with true or false based on the result of isAllSelected.
             *
             * @private
             */
            _setAllSelected: function () {
                this.allSelected = this._isAllSelected(this.listData);
            },

            /**
             * Sets all checked to true/false (based on the received 'checked' argument)
             * in view model.
             *
             * @param {boolean} checked
             * @private
             */
            _checkAllInView: function (checked) {
                _.each(this.listData, function (dataItem) {
                    dataItem.checked = checked;
                });
            },

            /**
             * Finds the drop down element and returns it.
             *
             * @returns {*}
             * @private
             */
            _getDropdownElement: function () {
                return this.$element.find('.fs-multiselect-dropdown-list');
            },


            /**
             * This method is exposed for testing purposes only!
             * Sets the value of _anyDropdownOpened
             *
             * @param {*} val
             * @private
             */
            __setAnyDropdownOpened: function (val) {
                _anyDropdownOpened = val;
            },

            /**
             * This method is exposed for testing purposes only!
             * Gets the value of _anyDropdownOpened
             *
             * @returns {boolean}
             * @private
             */
            __getAnyDropdownOpened: function () {
                return _anyDropdownOpened;
            },

            /**
             * This variable holds threshold number between 'long' list and 'short' list
             */
            longListThreshold: 10,

            /**
             * Hides the drop-down menu, and sets the global _anyDropdownOpened to false.
             */
            hideDropdown: function () {
                this._dropDownElement.removeClass('show');
                _anyDropdownOpened = false;
                this._removeWindowClickListener();
            },

            /**
             * Shows the drop down menu if no drop downs are opened globally.
             * Sets the global _anyDropdownOpened to true so no other drop-downs may be opened.
             */
            showDropdown: function () {
                if (!(this._dropDownElement.hasClass('show') ||
                    _anyDropdownOpened)) {
                    this._dropDownElement.addClass('show');
                    _anyDropdownOpened = true;

                    this.setLocation();

                    // Init document click watch
                    this._initWindowClickListener();
                }
            },

            /**
             * Handler for 'Select all' item click. If sets all view items to checked/unchecked.
             */
            selectAllHandler: function () {

                // if all view items are selected: deselect all
                if (this._isAllSelected(this.listData)) {
                    this._checkAllInView(false);
                    this.allSelected = false;

                    // If not all are selected: select all.
                } else {
                    this._checkAllInView(true);
                    this.allSelected = true;
                }

                // Set the form to dirty so 'Ok' button will be activated.
                this.$scope.dropdownForm.$setDirty();
            },

            /**
             * Single item select handler. Fires _setAllSelected to determine the state of
             * 'Select all'.
             */
            selectSingleHandler: function (evt) {
                this._setAllSelected();

                // If original top was registered then use it to set the scroll to the original position
                // This takes care of a bug where clicking on the list caused the scroll of the list to
                // drop to the bottom
                if (this._originalScrollTop) {
                    this._originalScrollTop.element.scrollTop = this._originalScrollTop.scrollTop;
                    this._originalScrollTop = null;
                }
            },

            /**
             * A precursor to selectSingleHandler method. This registers the original scroll top so
             * selectSingleHandler can then use it to set the scroll back to when it was started before
             * clicking had changed it
             * @param element
             * @param evt
             */
            setListContainerScroll: function (element, evt) {
                var container = element.parentElement;
                this._originalScrollTop = {element: container, scrollTop: container.scrollTop};
            },

            /**
             * OK Click handler.
             * Hides the drop-down, merges the view state into the model state, sets the view
             * value, sets the form pristine, and updates the state in the state container.
             */
            okClickHandler: function () {
                this.hideDropdown();
                this._listData = _.cloneDeep(this.listData);
                this.value = this._getMultiselectViewValue();
                this.lastSelected=this.value;
                this.$scope.dropdownForm.$setPristine();
                var state = this._getMultiselectState();

                this.updateStateDelegate({
                    id: this.multiselectId,
                    immediate: this._immediate,
                    type: 'data',
                    value: state
                });
            },

            /**
             * Cancel click handler.
             * Hides the drop-down, sets the form pristine to disable OK button, resets view
             * state to the model state, and determine allSelected state.
             */
            cancelClickHandler: function () {
                this.hideDropdown();
                this.$scope.dropdownForm.$setPristine();
                this.listData = _.cloneDeep(this._listData);
                this._setAllSelected();
            },


            setLocation: function (setTransition) {
                var _setLocation = () => {
                    var windowHeight = window.innerHeight;
                    var dropdownBottom = this._dropDownElement[0].getBoundingClientRect().bottom;
                    var transform = this._dropDownElement[0].style.transform.split(',');
                    var translateY = transform.length > 1 ? parseFloat(transform[1]) : 0;

                    if (windowHeight - 120 < dropdownBottom) {
                        var delta = (dropdownBottom + translateY) - windowHeight + 120;
                        this._dropDownElement.css('transform', `translate3d(0, -${delta}px, 0)`);
                    } if (windowHeight - 120 > dropdownBottom) {
                        this._dropDownElement.css('transform', `translate3d(0, 0, 0)`);
                    }
                };


                if (setTransition) {
                    this._dropDownElement.css('transition', 'transform 0.5s ease-in-out');
                    this.$timeout(_setLocation, 200);
                } else {
                    this._dropDownElement.css('transition', '');
                    _setLocation();
                }

            },

            /**
             * Inits
             */

            /**
             * Controller init function
             *
             * @private
             */
            _ctrlInit: function _ctrlInit() {

                this._ctrlValidations(this._errMsg + '_ctrlInit: ');

                // Clone states
                // _listData is the model state
                // listData is the view state
                this._listData = _.cloneDeep(this._listData);
                this.listData = _.cloneDeep(this._listData);
                this._immediate=this._immediate?this._immediate:false; //this._immediate is false by default

            },


            /**
             * Link function init function
             *
             * @private
             */
            _linkInit: function () {

                // Get dropdown element
                this._dropDownElement = this._getDropdownElement();

                // Init watches
                this.lastSelected='None'; //Init with none
                this._initWatches();
            }
        });

        FsMultiselectController.$inject = ['$scope', '$element', '$attrs', '$timeout'];

        return {
            restrict: 'E',
            templateUrl: 'app/shared/components/fs-multiselect/fs-multiselect.view.html',
            scope: {},
            controller: FsMultiselectController,
            controllerAs: 'multiselect',
            bindToController: {
                _listData: '=listData',
                fetchStateDelegate: '=',
                updateStateDelegate: '=',
                multiselectId: '@multiselectId',
                label: '@label',
                _initialState: '@initialState',
                _immediate:'@?immediate',
                //Count labels should be array of two strings, the first one is the label for count one item (I.E. User)
                //The second string should be the label for count of more then one item (I.E Users)
                //To display the count, each item in listData must have attribute of 'count' on it.
                countLabels: '=countLabels'
            },
            link: linkFn
        };
    }

    fsMultiselect.$inject = ['assert'];

    angular.module('Fortscale.shared.components.fsMultiselect', [
        'kendo.directives'
    ])
        .directive('fsMultiselect', fsMultiselect);
}());
