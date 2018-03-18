(function () {
    'use strict';

    function fsMultiselectAutocomplete(assert, $http, Restangular) {

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
        function FsMultiselectAutocompleteController($scope, $element, $attrs, $timeout) {
            // Put dependencies on the controller instance
            this.$scope = $scope;
            this.$element = $element;
            this.$attrs = $attrs;
            this.$timeout = $timeout;

            this._ctrlInit();
        }

        _.merge(FsMultiselectAutocompleteController.prototype, {
            _errMsg: 'fsMultiselectAutocomplete.directive: ',

            /**
             * Control init validations. Validates directive's arguments
             * @param errMsg
             * @private
             */
            _ctrlValidations: function (errMsg) {
                assert.isString(this.multiselectAutocompleteId, 'multiselectAutocompleteId', errMsg);
                assert.isString(this.label, 'label', errMsg, true);
                assert.isString(this.entity, 'entity', errMsg);
                assert.isString(this.textField, 'textField', errMsg);
                assert.isString(this.valueField, 'valueField', errMsg);
                assert.isString(this.queryTextField, 'queryTextField', errMsg, true);
                assert.isString(this.queryValueField, 'queryValueField', errMsg, true);

                assert.isFunction(this.fetchStateDelegate, 'fetchStateDelegate', errMsg);
                assert.isFunction(this.updateStateDelegate, 'updateStateDelegate', errMsg);
            },

            /**
             * Watch state function.
             *
             * @returns {*}
             * @private
             */
            _watchStateFn: function () {
                return this.fetchStateDelegate(this.multiselectAutocompleteId);
            },

            /**
             * Watch state action function.
             * Handles digestion of incoming state.
             *
             * @param state
             * @private
             */
            _watchStateActionFn: function (state) {

                if (state === null) {
                    return;
                }

                var ctrl = this;

                // Create query
                var query = {};
                if (ctrl.queryTextField) {
                    query[ctrl.queryTextField] = state;
                }

                // Get list of entities
                Restangular.all(ctrl.entity).getList(query)
                    .then(function (entities) {

                        // Add and select list of entities to multi select
                        var el = ctrl._getElementData();
                        var data = el.dataSource.data();
                        _.each(entities, function (entity) {
                            data.push(entity);
                        });
                        el.dataSource.data(data);
                        el.value(state.split(','));

                        // Register the state for future diff examination
                        ctrl._lastMultiselectValue = el.value();
                    });

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
                // Set the data array to empty array
                this._multiselectValue([]);
                this._setState(null);
                this._initMultiselect();
            },

            /**
             * Calculates the total of the returned entities. Used by multiselect options.
             *
             * @param {object} data
             * @returns {number}
             * @private
             */
            _multiselectOptionsTotal: function (data) {
                return (data.data && data.data.length) || 0;
            },

            /**
             * Mapper function to create a filter. Used by multiselect options.
             *
             * @param data
             * @param type
             * @returns {{} | undefined}
             * @private
             */
            _multiselectOptionsParameterMap: function (data, type) {
                if (!this.queryValueField) {
                    return;
                }

                if (type === 'read') {
                    if (data && data.filter && data.filter.filters &&
                        data.filter.filters[0]) {
                        var value = data.filter.filters[0].value;

                        var paramMap = {};
                        paramMap[this.queryValueField] = value;

                        return paramMap;
                    }
                }
            },

            /**
             * Multi select select handler. Used by multiselect options. Calcs the desired state
             * and sets it.
             *
             * @param {Object} e
             * @private
             */
            _multiselectOptionsSelectHandler: function (e) {
                var dataItem = e.sender.dataSource.view()[e.item.index()];
                var ids = this._multiselectValue();

                //detect if id is in ids
                if (!_.some(ids, function (id, index) {
                        // If item's id is in ids, splice it out
                        if (id === dataItem.id) {

                            ids.splice(index, 1);
                            return true;
                        }

                        return false;
                    })) {
                    // If item's id is not in ids, put it in
                    ids.push(dataItem[this.valueField]);
                }

                this._setState(ids.join(','));
            },

            /**
             * Multi select change handler. Used by multiselect options. Finds if
             * there is a difference between the last registered value and the current.
             * If there's a difference then an item was removed, and state needs to be updated.
             *
             * @param e
             * @private
             */
            _multiselectOptionsChangeHandler: function (e) {
                var diff = _.difference(this._lastMultiselectValue || [], e.sender.value());
                if(diff.length > 0){
                    var ids = this._multiselectValue();
                    this._setState(ids.join(','));
                }
                this._lastMultiselectValue = e.sender.value();
            },

            /**
             * Creates the multiselect options
             *
             * @private
             */
            _initMultiselectOptions: function () {

                var ctrl = this;

                this.multiselectOptions = {
                    placeholder: this.label?this.label:'Enter Text',
                    dataTextField: ctrl.textField,
                    dataValueField: ctrl.valueField,
                    valuePrimitive: true,
                    autoBind: false,
                    minLength: 3,
                    dataSource: {
                        type: 'odata',
                        serverFiltering: true,
                        schema: {
                            data: 'data',
                            total: ctrl._multiselectOptionsTotal
                        },
                        transport: {
                            read: {
                                url: 'api/user/entities',
                                type: 'GET',
                                dataType: 'json'
                            },
                            parameterMap: ctrl._multiselectOptionsParameterMap.bind(ctrl)
                        }
                    },
                    select: ctrl._multiselectOptionsSelectHandler.bind(ctrl),
                    change: ctrl._multiselectOptionsChangeHandler.bind(ctrl),
                    filtering: function(e) {
                        // Make sure that backspace does not break the code
                        if (!e.filter || !e.filter.value) {
                            e.preventDefault();
                        }
                    }
                };
            },

            /**
             * Returns the multiselect api.
             *
             * @returns {*}
             * @private
             */
            _getElementData: function () {
                return this.multiselectAutocompleteSelectElement.data('kendoMultiSelect');

            },

            /**
             * Gets or sets the multiselect value.
             *
             * @param {Array=} val
             * @returns {Array}
             * @private
             */
            _multiselectValue: function (val) {
                // Get kendo element controller
                let kendoElementCtrl = this._getElementData();

                // Set value if value is provided
                if (!_.isNil(val)) {
                    kendoElementCtrl.value(val);
                    kendoElementCtrl.refresh();
                }

                return kendoElementCtrl.value();
            },

            _initMultiselect: function () {
                this.multiselectAutocompleteSelectElement = this.$element
                    .find('.multiselectAutocompleteSelect');
                this.multiselectAutocompleteSelectElement
                    .kendoMultiSelect(this.multiselectOptions);
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

                ctrl.$scope.$on('control:reset', ctrl._controlResetRequestHandler.bind(ctrl));
            },

            /**
             * Sets state to state container
             *
             * @param {string} state
             * @private
             */
            _setState: function (state) {
                this.updateStateDelegate({
                    id: this.multiselectAutocompleteId,
                    immediate: this._immediate,
                    type: 'data',
                    value: state
                });
            },

            /**
             * Controller init function
             *
             * @private
             */
            _ctrlInit: function _ctrlInit() {
                this._ctrlValidations(this._errMsg + '_ctrlInit: ');
                this._immediate=this._immediate?this._immediate:false; //this._immediate is false by default
            },

            /**
             * Link function init function
             *
             * @private
             */
            _linkInit: function () {

                // Init multiselect options
                this._initMultiselectOptions();

                // Init multiselect
                this._initMultiselect();


                // Init watches
                this._initWatches();
            }
        });

        FsMultiselectAutocompleteController.$inject = ['$scope', '$element', '$attrs', '$timeout'];

        return {
            restrict: 'E',
            templateUrl: 'app/shared/components/fs-multiselect-autocomplete/fs-multiselect-autocomplete.view.html',
            scope: {},
            controller: FsMultiselectAutocompleteController,
            controllerAs: 'multiselectAutocomplete',
            bindToController: {
                fetchStateDelegate: '=',
                updateStateDelegate: '=',
                multiselectAutocompleteId: '@',
                label: '@',
                entity: '@',
                textField: '@',
                valueField: '@',
                queryTextField: '@',
                queryValueField: '@',
                _immediate:'@?immediate',
            },
            link: linkFn
        };
    }

    fsMultiselectAutocomplete.$inject = ['assert', '$http', 'Restangular'];

    angular.module('Fortscale.shared.components.fsMultiselectAutocomplete', [
        'kendo.directives'
    ])
        .directive('fsMultiselectAutocomplete', fsMultiselectAutocomplete);
}());
