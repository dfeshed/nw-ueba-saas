(function () {
    'use strict';

    function fsSelectDirective (assert) {

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

            ctrl._renderSelectElement();
        }

        /**
         * The directive's controller function
         *
         * @constructor
         */
        function FsSelectController ($element, $scope) {
            // Put dependencies on the instance
            var ctrl = this;
            ctrl.$element = $element;
            ctrl.$scope = $scope;

            // Invoke init
            ctrl.init();
        }

        angular.extend(FsSelectController.prototype, {

            _errMsg: 'fsSelect.directive: ',

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
             * Validates selectMap
             *
             * @private
             */
            _validateSelectMap: function () {
                var errMsg = this._errMsg + 'arguments: ';
                assert.isObject(this.selectMap, 'selectMap', errMsg);
                if (this.selectMap) {
                    _.each(this.selectMap, function (selectValue, key) {
                        assert.isString(selectValue, 'selectMap[' + key + ']', errMsg, false, true);
                    });
                }
            },

            /**
             * Validates that provided selectedId is an actual id on selectMap
             *
             * @param {string=} errMsg
             * @param {string=} selectedId
             * @private
             */
            _validateSelectedId: function (errMsg, selectedId) {

                if (this.selectedId !== undefined && this.selectedId !== null) {
                    errMsg = errMsg || this._errMsg + '_validateSelectedId: id must be one of the id\'s provided in' +
                        ' selectMap';

                    selectedId = selectedId || this.selectedId;

                    var exists = Object.keys(this.selectMap).some(function (id) {
                        return id === selectedId;
                    }, this);

                    assert(exists, errMsg, RangeError);
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
                this._validateSelectMap();
            },

            /**
             * Returns the value of the the state by the id
             * @returns {*}
             * @private
             */
            _stateWatchFn: function () {
                if (this.fetchStateDelegate) {
                    return this.fetchStateDelegate(this.selectId);
                }
            },
            /**
             * Watch action function . Sets the value to the picker if state has changed.
             *
             * @param {string|number} value
             */
            _stateWatchActionFn: function (value) {
                if (value !== null && value !== undefined) {
                    this.setSelectValue(value);
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

            /**
             * input change handler. When the input element changes, will change the value of the picker and fire
             * 'change' event.
             *
             * @private
             */
            _SelectChangeHandler: function () {

                this.updateStateDelegate({
                    id: this.selectId,
                    type: 'DATA',
                    value: this.getSelectValue(),
                    immediate: !this.isNotImmediate
                });

                if (this.formCtrl && !!this.isNotImmediate) {
                    this.$scope.$apply(this.formCtrl.$setDirty.bind(this.formCtrl));
                }

                this.selectElement.blur();
            },

            /**
             * Sets a Select change handler.
             *
             * @private
             */
            _initSelectChangeWatch: function () {
                var ctrl = this;

                // Lock in context
                function listenerFn (evt) {
                    ctrl._SelectChangeHandler(evt);
                }

                ctrl.selectElement.on('change', listenerFn);

                // Cleanup
                ctrl.$scope.$on('$destroy', function () {
                    ctrl.selectElement.off('change', listenerFn);
                });
            },

            /**
             * Init watches sequence
             *
             * @private
             */
            _initWatches: function () {
                this._initStateWatch();
                this._initSelectChangeWatch();
            },

            /**
             * Adds a select option to select element
             *
             * @param {string} id
             * @param {string} value
             * @private
             */
            _addSelectOption: function (id, value) {
                var option = document.createElement("option");
                option.text = value;
                option.value = id;
                this.selectElement[0].add(option);
            },

            /**
             * Renders all options received on selectMap
             *
             * @private
             */
            _renderOptions: function () {
                _.each(this.selectMap, _.bind(function (value, key) {
                    this._addSelectOption(key, value);
                }, this));
            },

            /**
             * Sets the initial value of the select element. If selectedId is provided, it's assigned to the element
             * via setSelectValue. if selectedId is not provided, the first option is selected.
             * @private
             */
            _setInitialValue: function () {

                var selectedId = this.selectedId ||
                    (this.selectElement[0].options &&
                    this.selectElement[0].options[0] &&
                    this.selectElement[0].options[0].value);

                if (selectedId) {
                    this.setSelectValue(selectedId);
                }
            },

            /**
             * Renders the select element
             *
             * @private
             */
            _renderSelectElement: function () {
                this._renderOptions();
                this._setInitialValue();
            },

            /**
             * Sets the element value.
             *
             * @param {string|number} value
             * @returns {*}
             */
            setSelectValue: function (value) {
                this._validateSelectedId(this._errMsg + 'setSelectValue: ', value);
                this.selectElement[0].value = value;
            },

            /**
             * Gets the element value
             *
             * @returns {string}
             */
            getSelectValue: function () {
                return this.selectElement[0].value;
            },
            /**
             * Init
             */
            init: function init () {

                var ctrl = this;
                ctrl.selectElement = ctrl.$element.find('select');

                ctrl._validations();
                ctrl._initWatches();

            }
        });

        FsSelectController.$inject = ['$element', '$scope'];

        return {
            restrict: 'E',
            template: '<div class="fs-select"><select></select><svg class="dropdown-icon"><use xlink:href="#dropdown-icon"></use></svg></div>',
            scope: {},
            link: linkFn,
            controller: FsSelectController,
            controllerAs: 'ctrl',
            bindToController: {
                selectId: '@',
                selectMap: '=',
                selectedId: '@',
                isNotImmediate: '@',
                fetchStateDelegate: '=',
                updateStateDelegate: '='
            },
            require: ['fsSelect', '?^^form']
        };
    }

    fsSelectDirective.$inject = ['assert'];

    angular.module('Fortscale.shared.components.fsSelect')
        .directive('fsSelect', fsSelectDirective);
}());
