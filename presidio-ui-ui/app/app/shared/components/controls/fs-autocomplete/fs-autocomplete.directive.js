/**
 * @name: fsAutocomplete.directive
 * @description: This directive will use kendo to render an autocomplete element. The element is based on received
 * configurations and settings.
 * @param {string} autocompleteId The (should be) unique id within a state chain.
 * @param {function=} fetchStateDelegate Function that's used to get an external state by providing autocompleteId
 * @param {function=} updateStateDelegate Function that's used to update an external state by providing an object:
 * {id: string, type: string, value: *, immediate: boolean}
 * @param {object} autocompleteSettings The required configuration. The received object will override any property
 * on _settings.
 * @param {object} resourceSettings A configuration object, that if received will be used for server filtering.
 */


(function () {
    'use strict';

    function fsAutocompleteDirective (assert, interpolation, BASE_URL) {

        /**
         *
         * @param {object} scope
         * @param {object} element
         * @param {object} attrs
         * @param {array<object>|object} ctrls
         */
        function linkFn (scope, element, attrs, ctrls) {
            // Link function logic

            var ctrl = ctrls[0];
            ctrl.formCtrl = ctrls[1];

            ctrl._renderAutocompleteElement();
        }

        /**
         * The directive's controller function
         *
         * @constructor
         */
        function FsAutocompleteController ($element, $scope) {
            // Put dependencies on the instance
            var ctrl = this;
            ctrl.$element = $element;
            ctrl.$scope = $scope;

            // Invoke init
            ctrl.init();
        }

        angular.extend(FsAutocompleteController.prototype, {
            _errMsg: 'fsAutocomplete.directive: ',

            /**
             * Validates received id is a non empty string.
             *
             * @private
             */
            _validateId: function () {
                assert.isString(this.autocompleteId, 'autocompleteId',
                    this._errMsg + 'arguments: autocompleteId: ', false, false);
            },

            /**
             * Validate fetchStateDelegate.
             * Throw TypeError if fetchStateDelegate is received and is not a function
             * @private
             */
            _validateGetStateFn: function () {
                assert.isFunction(this.fetchStateDelegate, 'fetchStateDelegate', this._errMsg + 'arguments: ', true);
            },
            /**
             * Validate fetchStateDelegate.
             * Throw TypeError if fetchStateDelegate is received and is not a function
             * @private
             */
            _validateSetStateFn: function () {
                assert.isFunction(this.updateStateDelegate, 'updateStateDelegate', this._errMsg + 'arguments: ', true);
            },

            /**
             * Validate settings was received
             * @private
             */
            _validateSettingsReceived: function () {
                assert.isObject(this.autocompleteSettings, 'autocompleteSettings', this._errMsg + 'arguments: ', false);
            },

            /**
             * Validates settings has dataValueField or dataValueFn
             * Validates dataValueField is a non empty string if received.
             * Validates dataValueFn is a function if received.
             *
             * @private
             */
            _validateSettingsDataValue: function () {
                assert(this.autocompleteSettings.dataValueField || this.autocompleteSettings.dataValueFn,
                    this._errMsg + 'arguments: autocompleteSettings: dataValueField or dataValueFn must be provided.',
                    ReferenceError
                );

                assert.isString(this.autocompleteSettings.dataValueField, 'autocompleteSettings.dataValueField',
                    this._errMsg + 'arguments: autocompleteSettings: ', true, false);

                assert.isFunction(this.autocompleteSettings.dataValueFn, 'autocompleteSettings.dataValueFn',
                    this._errMsg + 'arguments: autocompleteSettings: ', true);
            },

            /**
             * Validates dataTextField is a non empty string.
             * Validates dataTextFn is a function if received.
             *
             * @private
             */
            _validateSettingsTextValue: function () {
                assert.isString(this.autocompleteSettings.dataTextField, 'autocompleteSettings.dataTextField',
                    this._errMsg + 'arguments: autocompleteSettings: ', false, false);

                assert.isFunction(this.autocompleteSettings.dataTextFn, 'autocompleteSettings.dataTextFn',
                    this._errMsg + 'arguments: autocompleteSettings: ', true);
            },

            /**
             * Validates resourceSettings is an object if received.
             *
             * @private
             */
            _validateResourceSettingsReceived: function () {
                assert.isObject(this.resourceSettings, 'resourceSettings', this._errMsg + 'arguments: ', true);
            },

            /**
             * Validates entity on resourceSettings is a non empty string
             *
             * @private
             */
            _validateResourceSettingsEntity: function () {
                  assert.isString(this.resourceSettings.entity, 'resourceSettings.entity',
                      this._errMsg + 'arguments: resourceSettings: ', false, false);
            },

            /**
             * Validates params on resourceSettings is an object if received.
             *
             * @private
             */
            _validateResourceSettingsParams: function () {
                assert.isObject(this.resourceSettings.params, 'resourceSettings.params',
                    this._errMsg + 'arguments:' + ' resourceSettings: ', true);
            },

            /**
             * Directive validation sequence
             *
             * @private
             */
            _validations: function () {
                this._validateId();
                this._validateGetStateFn();
                this._validateSetStateFn();
            },

            /**
             * Directive's settings validation sequence
             *
             * @private
             */
            _validateSettings: function () {
                this._validateSettingsReceived();
                this._validateSettingsDataValue();
                this._validateSettingsTextValue();
            },

            /**
             * Directive's resource settings validation sequence
             *
             * @private
             */
            _validateResourceSettings: function () {
                this._validateResourceSettingsReceived();
                if (this.resourceSettings) {
                    this._validateResourceSettingsEntity();
                    this._validateResourceSettingsParams();
                }
            },

            /**
             * Fires a fsAutocomplete:itemSelected event.
             *
             * @private
             */
            _fireSelectEvent: function (dataItem) {


                this.$scope.$applyAsync(function () {
                    this.$scope.$root.$broadcast('fsAutocomplete:itemSelected', this.autocompleteId, dataItem);
                }.bind(this));

            },

            /**
             * Fires when element value has changed, ie selection was made or focus was lost after change. It's
             * fired via Kendo autocomplete controller
             *
             * @private
             */
            _changeHandler: function () {

                // Get value
                var dataValue = this.getAutocompleteValue();
                var stateValue = this.fetchStateDelegate && this.fetchStateDelegate(this.autocompleteId);
                if (dataValue === stateValue) {
                    return;
                }


                // Positive updateOnNull will cause the element to update state even if the value is null
                if ((this._settings.updateOnNull || dataValue !== null) && this.updateStateDelegate) {

                    // Update state
                    this.updateStateDelegate({
                        id: this.autocompleteId,
                        type: 'DATA',
                        value: dataValue,
                        immediate: !!this._settings.isImmediate
                    });

                    // Update form controller if it exists and isImmediate is false
                    if (this.formCtrl && !this._settings.isImmediate) {
                        this.$scope.$apply(this.formCtrl.$setDirty.bind(this.formCtrl));
                    }
                }

                // If flag resetOnNull is positive, then reset value of element. (set to null)
                if (this._settings.resetOnNull && !dataValue) {
                    this.setAutocompleteValue(null);
                }

                // Focus out of element
                this.inputElement.blur();

                var dataItem = this.autocompleteCtrl.dataItem();
                this._fireSelectEvent(dataItem);

            },

            /**
             * Initiates settings. Digest received settings and creates settings that kendo will accept.
             *
             * @private
             */
            _initSettings: function () {

                this._validateSettings();

                var ctrl = this;

                this._settings = _.merge({},
                    {
                        // Directive  default settings
                        dataValueField: null,
                        dataValueFn: null,
                        mustBeInData: false,
                        updateOnNull: false,
                        resetOnNull: false,
                        isImmediate: false,
                        dataTextFn: null,


                        // Kendo default settings
                        delay: 300,
                        minLength: 1,
                        placeholder: "Service Account Username",
                        dataSource: (ctrl.resourceSettings && new kendo.data.DataSource({
                            transport: {
                                read: {
                                    url: BASE_URL + '/' + ctrl._resourceSettings.entity,
                                    dataType: "json",
                                    data: function () {
                                        var searchQuery = ctrl._getSearchQueryValue();
                                        var interpolatedSettings = interpolation.interpolate(ctrl._resourceSettings, {
                                            search: searchQuery || ctrl._initialValue
                                        });

                                        return interpolatedSettings.params;

                                    }
                                },
                                /**
                                 * Adapter function for query params
                                 *
                                 * @param data
                                 * @returns {string}
                                 */
                                parameterMap: function(data) {
                                    return _.map(data, function (queryParamValue, queryParamName) {
                                        if (_.isObject(queryParamValue)) {
                                            // var queryParamAsJsonString;
                                            // try {
                                            //     queryParamAsJsonString = JSON.stringify(queryParamValue);
                                            //     queryParamValue = queryParamAsJsonString;
                                            //
                                            // } catch (e) {
                                            //     console.warn(ctrl._errMsg + 'kendo:transport:parameterMap:' +
                                            //         ' translation of object to string failed.', queryParamValue);
                                            //     return '';
                                            // }
                                            return '';
                                        }

                                        return queryParamName + '=' + queryParamValue;
                                    }).join('&');
                                }
                            },
                            schema: {
                                data: "data"
                            },
                            serverFiltering: ctrl.resourceSettings && true,
                            change: function () {
                                if (ctrl.autocompleteSettings.dataTextFn) {
                                    ctrl.autocompleteSettings.dataTextFn(this.data());
                                }
                            }
                        })) || null,

                        //Kendo default event handlers
                        change: this._changeHandler.bind(this)
                    },
                    this.autocompleteSettings
                );

                // place the dataSource directly on the controller instance
                this._dataSource = this._settings.dataSource;
            },

            /**
             * Initiates resource settings.
             *
             * @private
             */
            _initResourceSettings: function () {
                this._validateResourceSettings();
                this._resourceSettings = _.merge({}, this.resourceSettings);
            },

            /**
             * Returns the value of the the state by the id
             * @returns {*}
             * @private
             */
            _stateWatchFn: function () {
                if (this.fetchStateDelegate) {
                    return this.fetchStateDelegate(this.autocompleteId);
                }
            },
            /**
             * Watch action function . Sets the value to the autocomplete if state has changed.
             *
             * @param {string|number} value
             */
            _stateWatchActionFn: function (value) {
                if (value !== null && value !== undefined && value !== this.getAutocompleteValue()) {
                    this._setAutocompleteInitialValue(value);
                }
            },

            /**
             * Initiates state watch
             *
             * @returns {*|function()}
             * @private
             */
            _initStateWatch: function () {
                this.$scope.$watch(this._stateWatchFn.bind(this), this._stateWatchActionFn.bind(this));
            },

            _initWatches: function () {
                this._initStateWatch();
            },

            /**
             * Processes the value of the input. Remove all whitespace from the string.
             *
             * @returns {string|null}
             * @private
             */
            _getSearchQueryValue: function () {
                // Get value
                var value = this.autocompleteCtrl.value(value);
                if (!value) {
                    return null;
                }

                //// Remove all white spaces
                //value = value.replace(/\s/g, '');

                // return value
                return value;
            },

            /**
             * Renders the autocomplete element. Places the rendered element kendo controller on the controller
             * instance.
             *
             * @private
             */
            _renderAutocompleteElement: function () {
                var ctrl = this;

                ctrl.inputElement.kendoAutoComplete(ctrl._settings);
                ctrl.autocompleteCtrl = ctrl.inputElement.data("kendoAutoComplete");

                // Make sure that when input value is deleted, and updateOnNull is on, update happens
                function emptyOnBackspaceHandler(evt) {
                    if (evt.keyCode === 8 && ctrl.inputElement.val() === '' && ctrl._settings.updateOnNull) {
                        ctrl._fireSelectEvent(null);
                        ctrl.autocompleteCtrl.trigger('change');
                    }
                }

                ctrl.inputElement.on('keyup', emptyOnBackspaceHandler);

                // Cleanup
                ctrl.$scope.$on('$destroy', function () {
                    ctrl.inputElement.off('keyup', emptyOnBackspaceHandler);
                });
            },

            /**
             * Processes received value from state, and selects the required choice.
             *
             * @param value
             * @private
             */
            _setAutocompleteInitialValue: function (value) {

                var ctrl = this;

                ctrl._initialValue = value;
                ctrl.autocompleteCtrl.enable(false);

                // fetch data if resourceSettings is provided
                if (ctrl.resourceSettings && ctrl._dataSource.fetch) {

                    // Fetch data and fire callback
                    ctrl._dataSource.fetch(function () {

                        // select the first one (should be only one)
                        if (ctrl.autocompleteCtrl.dataItems().length) {
                            ctrl.autocompleteCtrl.select(ctrl.autocompleteCtrl.ul.children().eq(0));
                            ctrl._fireSelectEvent(ctrl.autocompleteCtrl.dataItem());
                            ctrl.autocompleteCtrl.trigger('change');
                        }


                        // delete initial value
                        ctrl._initialValue = null;

                        // enable the element
                        ctrl.autocompleteCtrl.enable(true);



                    });
                } else {
                    assert(ctrl._settings.dataValueField !== null || ctrl._settings.dataValueField !== undefined,
                        ctrl._errMsg + 'When using autocomplete without server filtering, ie providing dataSource ' +
                        'in settings, dataValueField must be provided. Using dataValueFn will not allow the ' +
                        'selection of initial state.', RangeError);

                    var query = {};
                    query[ctrl._settings.dataValueField] = value;
                    var selectedItem = _.filter(ctrl._dataSource, query).shift();
                    if (selectedItem) {
                        ctrl.setAutocompleteValue(selectedItem[ctrl._settings.dataTextField]);
                        ctrl._fireSelectEvent(selectedItem);
                    } else {
                        ctrl.setAutocompleteValue(value);
                    }

                    ctrl.autocompleteCtrl.trigger('change');

                    // delete initial value
                    ctrl._initialValue = null;

                    // enable the element
                    ctrl.autocompleteCtrl.enable(true);

                }



            },

            /**
             * Sets the element value (without selection).
             *
             * @param {string|number} value
             * @returns {*}
             */
            setAutocompleteValue: function (value) {
                return this.autocompleteCtrl.value(value);
            },

            /**
             * Gets the element value. The extraction is based on the configuration.
             *
             * @returns {string|null}
             */
            getAutocompleteValue: function () {

                // Get data item from kendo controller
                var dataItem = this.autocompleteCtrl.dataItem();

                // If dataItem is not null:
                if (dataItem) {

                    // If settings has a dataValueField then dataItem should be dataItem[dataValueField] otherwise
                    // it should be the return value of dataValueFn(dataItem, dataItems)
                    if (this._settings.dataValueField) {
                        dataItem = dataItem[this._settings.dataValueField];
                    } else if (this._settings.dataValueFn) {
                        dataItem = this._settings.dataValueFn(dataItem, this.autocompleteCtrl.dataItems());
                    }

                    // If dataItem is null (no match is found in dataSource):
                } else {

                    // if flag mustBeInData is negative, then data item should be
                    // autocompleteCtrl.value() otherwise it should be null
                    if (!this._settings.mustBeInData) {
                        dataItem = this.autocompleteCtrl.value();
                    }

                }
                return dataItem;

            },
            /**
             * Init
             */
            init: function init () {

                var ctrl = this;
                ctrl.inputElement = ctrl.$element.find('input');

                ctrl._validations();

                ctrl._initResourceSettings();
                ctrl._initSettings();

                ctrl._initWatches();


            }

        });

        FsAutocompleteController.$inject = ['$element', '$scope'];

        return {
            restrict: 'E',
            template: '<input class="autocomplete-input">',
            scope: {},
            link: linkFn,
            controller: FsAutocompleteController,
            controllerAs: 'ctrl',
            bindToController: {
                autocompleteId: '@',
                fetchStateDelegate: '=',
                updateStateDelegate: '=',
                autocompleteSettings: '=',
                resourceSettings: '='
            },
            require: ['fsAutocomplete', '?^^form']
        };
    }

    fsAutocompleteDirective.$inject = ['assert', 'interpolation', 'BASE_URL'];

    angular.module('Fortscale.shared.components.fsAutocomplete')
        .directive('fsAutocomplete', fsAutocompleteDirective);
}());
