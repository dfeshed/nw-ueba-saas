(function () {
    'use strict';

    function fsDatePickerDirective (dateRanges) {

        /**
         *
         * @param {object} scope
         * @param {object} element
         * @param {object} attrs
         * @param {array<object>|object} ctrl
         */
        function linkFn (scope, element, attrs, ctrls) {
            var ctrl = ctrls[0];
            ctrl.formCtrl = ctrls[1];

            ctrl._renderDatePicker();
            ctrl._setPickerDefaultValue();
            ctrl._setInputChangeHandler();


        }

        /**
         * The directive's controller function
         *
         * @constructor
         */
        function FsDatePickerController ($element, $scope) {
            // Put dependencies on the instance
            var ctrl = this;
            ctrl.$element = $element;
            ctrl.$scope = $scope;


            // Invoke init
            ctrl.init();
        }

        angular.extend(FsDatePickerController.prototype, {
            /**
             * Validate fetchStateDelegate.
             * Throw TypeError if fetchStateDelegate is received and is not a function
             * @private
             */
            _validateGetStateFn: function () {
                if (this.fetchStateDelegate && !angular.isFunction(this.fetchStateDelegate)) {
                    throw new TypeError('fsDatePicker.directive: FsDatePickerController: ' +
                        'If fetchStateDelegate is provided, it must be a function.');
                }
            },
            /**
             * Validate fetchStateDelegate.
             * Throw TypeError if fetchStateDelegate is received and is not a function
             * @private
             */
            _validateSetStateFn: function () {
                if (this.updateStateDelegate && !angular.isFunction(this.updateStateDelegate)) {
                    throw new TypeError('fsDatePicker.directive: FsDatePickerController: ' +
                        'If updateStateDelegate is provided, it must be a function.');
                }
            },

            /**
             * Directive validation sequence
             *
             * @private
             */
            _validations: function () {
                this._validateGetStateFn();
                this._validateSetStateFn();
            },

            /**
             * Tests for all digits string. Returns true if all digits
             *
             * @param value
             * @returns {boolean}
             * @private
             */
            _isStringInteger: function (value) {
                return /^\d*$/.test(value);
            },

            /**
             */

            /**
             * Returns a date value (timestamp) from a Date, a string, a string representing an int, or an int.
             *
             * @param {string|number|Date} value
             * @returns {*}
             * @private
             */
            _getDateValueFromGenericValue: function (value) {
                if (_.isString(value) && this._isStringInteger(value)) {
                    value = parseInt(value, 10);
                }

                return new Date(value).valueOf();
            },

            /**
             * Returns the value of the the state by the id
             * @returns {*}
             * @private
             */
            _stateWatchFn: function () {
                return this.fetchStateDelegate(this.datePickerId);
            },
            /**
             * Watch action function . Sets the value to the picker if state has changed.
             *
             * @param newState
             */
            stateWatchActionFn: function (newState) {
                if (newState && this._getDateValueFromGenericValue(newState) !== this.getPickerValue().valueOf()) {
                    this.setPickerValue(newState);
                }
            },

            /**
             * Initiates state watch
             *
             * @returns {*|function()}
             * @private
             */
            _initStateWatch: function () {
                return this.$scope.$watch(this._stateWatchFn.bind(this), this.stateWatchActionFn.bind(this));
            },

            /**
             * Init watches sequence
             *
             * @private
             */
            _initWatches: function () {
                this._watchStateDeregister = this._initStateWatch();
            },

            /**
             * Handler function for picker value change. Updates state (via updateStateDelegate), and sets the form
             * dirty, if form was recognized.
             *
             * @param {Event} evt
             * @private
             */
            _pickerChangeHandler: function (evt) {
                var date = this.getPickerValue().valueOf();

                this.updateStateDelegate({
                    id: this.datePickerId,
                    type: 'DATA',
                    value: date,
                    immediate: false
                });

                if (this.formCtrl) {
                    this.$scope.$apply(this.formCtrl.$setDirty.bind(this.formCtrl));
                }
            },

            /**
             * Finds the date-picker element, and renders the date range picker with kendoDatePicker
             *
             * @private
             */
            _renderDatePicker: function () {
                this.datePickerElement = this.$element.find('.date-picker').kendoDatePicker({
                    change: this._pickerChangeHandler.bind(this)
                });
                this.datePickerKendoController = this.datePickerElement.data("kendoDatePicker");
            },

            /**
             * Sets the default value to the picker. In no default value is provided, the default value will be today.
             *
             * @returns {*}
             * @private
             */
            _setPickerDefaultValue: function () {
                var initVal = this.initialValue || dateRanges.getStartOfDayByDaysAgo(0);
                return this.setPickerValue(initVal);
            },

            /**
             * input change handler. When the input element changes, will change the value of the picker and fire
             * 'change' event.
             *
             * @param {Event} evt
             * @private
             */
            _inputChangeHandler: function (evt) {
                var newDate = new Date(evt.target.value);
                if (newDate.valueOf() !== this.getPickerValue().valueOf()) {
                    this.setPickerValue(newDate);
                    this.datePickerKendoController.trigger('change');
                }
            },

            /**
             * Sets an input change handler.
             *
             * @private
             */
            _setInputChangeHandler: function () {
                this.$element.find('input').on('change', this._inputChangeHandler.bind(this));
            },

            /**
             * Sets the picker value.
             *
             * @param {string|number|Date} value
             * @returns {*}
             */
            setPickerValue: function (value) {
                value = this._getDateValueFromGenericValue(value);
                return this.datePickerKendoController.value(new Date(value));
            },

            /**
             * Gets the picker value
             *
             * @returns {Date}
             */
            getPickerValue: function () {
                return this.datePickerKendoController.value();
            },
            /**
             * Init
             */
            init: function init () {

                var ctrl = this;

                ctrl._validations();
                ctrl._initWatches();

            }
        });

        FsDatePickerController.$inject = ['$element', '$scope'];

        return {
            restrict: 'E',
            template: '<input class="date-picker" />',
            scope: {},
            link: linkFn,
            controller: FsDatePickerController,
            controllerAs: 'ctrl',
            bindToController: {
                datePickerId: '@',
                fetchStateDelegate: '=',
                updateStateDelegate: '=',
                initialValue: '@'
            },
            require: ['fsDatePicker', '?^^form']
        };
    }

    fsDatePickerDirective.$inject = ['dateRanges'];

    angular.module('Fortscale.shared.components.fsDatePicker')
        .directive('fsDatePicker', fsDatePickerDirective);
}());
