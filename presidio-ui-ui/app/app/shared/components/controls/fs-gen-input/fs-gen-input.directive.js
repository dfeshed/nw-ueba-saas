(function () {
    'use strict';

    var IS_OPTIONAL = true;
    var IS_NOT_OPTIONAL = false;
    var CAN_NOT_BE_EMPTY = false;

    function fsGenInputDirective (assert, $compile) {

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

        }

        /**
         * The directive's controller function
         *
         * @constructor
         */
        function FsGenInputController ($element, $scope) {
            // Put dependencies on the instance
            var ctrl = this;
            ctrl.$element = $element;
            ctrl.$scope = $scope;

            // Invoke init
            ctrl.init();
        }

        angular.extend(FsGenInputController.prototype, {
            _errMsg: 'fsGenInput.directive: ',

            _validateGenInputId: function () {
                assert.isString(this.genInputId, 'genInputId', this._errMsg + 'arguments: ', IS_NOT_OPTIONAL,
                    CAN_NOT_BE_EMPTY);
            },
            /**
             * Validate fetchStateDelegate.
             * Throw TypeError if fetchStateDelegate is received and is not a function
             * @private
             */
            _validateGetStateFn: function () {
                assert.isFunction(this.fetchStateDelegate, 'fetchStateDelegate', this._errMsg + 'arguments: ',
                    IS_OPTIONAL);
            },
            /**
             * Validate updateStateDelegate.
             * Throw TypeError if fetchStateDelegate is received and is not a function
             * @private
             */
            _validateSetStateFn: function () {
                assert.isFunction(this.updateStateDelegate, 'updateStateDelegate', this._errMsg + 'arguments: ',
                    IS_OPTIONAL);
            },

            _validateAttributes: function () {
                assert.isObject(this.attributes, 'attributes', this._errMsg + 'arguments: ', IS_OPTIONAL);
            },

            _validateState: function () {
                assert.isObject(this.state, 'state', this._errMsg + 'arguments: ', IS_OPTIONAL);
            },

            /**
             * Directive validation sequence
             *
             * @private
             */
            _validations: function () {
                this._validateGenInputId();
                this._validateGetStateFn();
                this._validateSetStateFn();
                this._validateAttributes();
                this._validateState();
            },

            /**
             * Returns the value of the the state by the id
             * @returns {*|undefined}
             * @private
             */
            _stateWatchFn: function () {
                if (this.fetchStateDelegate) {
                    return this.fetchStateDelegate(this.genInputId);
                }
            },
            /**
             * Watch action function . Sets the value to the picker if state has changed.
             *
             * @param {string|number} value
             */
            _stateWatchActionFn: function (value) {
                if (value !== undefined && value !== null && this.getInputValue() !== value) {
                    this.setInputValue(value);
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
             * input change handler. When the input element changes, the
             *
             * @private
             */
            _inputChangeHandler: function () {

                if (!this.ngModelCtrl.$valid) {
                    return;
                }


                if (this.updateStateDelegate) {
                    this.updateStateDelegate({
                        id: this.genInputId,
                        type: 'DATA',
                        value: this.getInputValue(),
                        immediate: !!this.isImmediate
                    });
                }

                if (this.formCtrl) {
                    this.formCtrl.$setDirty.call(this.formCtrl);
                }
            },

            /**
             * Sets an input change handler.
             *
             * @private
             */
            _initInputChangeWatch: function () {
                var ctrl = this;
                ctrl.ngModelCtrl.$viewChangeListeners = [
                    this._inputChangeHandler.bind(this)
                ];
            },

            /**
             * Makes sure that using the 'Enter' key will not fire submit action on the form.
             *
             * @private
             */
            _initDisableSubmitOnEnter: function () {
                var ctrl = this;

                function disableEnterKey (evt) {
                    var code = evt.keyCode || evt.which;
                    if (code === 13) {
                        evt.preventDefault();

                        ctrl.inputElement.trigger('blur');
                        return false;
                    }
                }

                ctrl.inputElement.on('keyup keypress', disableEnterKey);

                // Cleanup
                ctrl.$scope.$on('$destroy', function () {
                    ctrl.inputElement.off('keyup keypress', disableEnterKey);
                });
            },

            /**
             * Init watches sequence
             *
             * @private
             */
            _initWatches: function () {
                this._initStateWatch();
                this._initInputChangeWatch();
                this._initDisableSubmitOnEnter();
            },

            /**
             * Takes an (angular/jquery) element, and applies attributes object with attr method. All properties on
             * attributes object will be assigned to element as attributes.
             *
             * @param {angular.element} element
             * @param {object=} attributes
             * @private
             */
            _assignAttributes: function (element, attributes) {
                if (attributes) {
                    // Assign attributes to element
                    angular.element(element).attr(attributes);
                }
            },

            /**
             * Renders and compiles the input element.
             *
             * @private
             */
            _renderElement: function () {
                // Create element
                this.inputElement = angular.element('<input ng-model="ctrl.model">');

                // Assign attributes to element
                this._assignAttributes(this.inputElement, _.merge({}, {
                    "ng-model-options": "{ updateOn: 'default blur', debounce: { 'default': " + (this.debounce || 400) +
                    ", 'blur': 0 } }"
                }, this.attributes));

                // Compile the element
                this.inputElement = $compile(this.inputElement)(this.$scope);

                // 'Capture' ngModel controller
                this.ngModelCtrl = this.inputElement.controller('ngModel');

                // Append input element to directive element
                this.$element.append(this.inputElement);

            },

            /**
             * Sets the input value.
             *
             * @param {string|number} value
             * @returns {*}
             */
            setInputValue: function (value) {
                return this.inputElement.val(value);
            },

            /**
             * Gets the input value
             *
             * @returns {string}
             */
            getInputValue: function () {
                return this.inputElement.val();
            },
            /**
             * Init
             */
            init: function init () {

                var ctrl = this;
                ctrl._validations();
                ctrl._renderElement();
                ctrl._initWatches();

            }
        });

        FsGenInputController.$inject = ['$element', '$scope'];

        return {
            restrict: 'E',
            template: '<span class="gen-input-wrapper"></span>',
            scope: {},
            link: linkFn,
            controller: FsGenInputController,
            controllerAs: 'ctrl',
            bindToController: {
                genInputId: '@',
                fetchStateDelegate: '=',
                updateStateDelegate: '=',
                attributes: '=',
                state: '=',
                isImmediate: '@',
                debounce: '@'
            },
            require: ['fsGenInput', '?^^form']
        };
    }

    fsGenInputDirective.$inject = ['assert', '$compile'];

    angular.module('Fortscale.shared.components.fsGenInput')
        .directive('fsGenInput', fsGenInputDirective);
}());
