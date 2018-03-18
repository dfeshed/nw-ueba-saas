(function () {
    'use strict';

    var DEFAULT_VALUE = 50;
    var MAX_VALUE_DEFAULT = 100;
    var MIN_VALUE = 0;

    function fsMinScoreDirective (assert) {

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

            ctrl.setMinScoreValue(DEFAULT_VALUE);
        }

        /**
         * The directive's controller function
         *
         * @constructor
         */
        function FsMinScoreController ($element, $scope) {
            // Put dependencies on the instance
            var ctrl = this;
            ctrl.$element = $element;
            ctrl.$scope = $scope;

            // Invoke init
            ctrl.init();
        }

        angular.extend(FsMinScoreController.prototype, {

            /**
             * Validate fetchStateDelegate.
             * Throw TypeError if fetchStateDelegate is received and is not a function
             * @private
             */
            _validateGetStateFn: function () {
                if (this.fetchStateDelegate && !angular.isFunction(this.fetchStateDelegate)) {
                    throw new TypeError('fsMinScore.directive: FsMinScoreController: ' +
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
                    throw new TypeError('fsMinScore.directive: FsMinScoreController: ' +
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
             * Returns the value of the the state by the id
             * @returns {*}
             * @private
             */
            _stateWatchFn: function () {
                return this.fetchStateDelegate(this.minScoreId);
            },
            /**
             * Watch action function . Sets the value to the picker if state has changed.
             *
             * @param {string|number} value
             */
            _stateWatchActionFn: function (value) {
                if (_.isString(value)) {
                    value = parseInt(value, 10);
                }
                if (value !== undefined && value !== null && this.getMinScoreValue() !== value) {
                    this.setMinScoreValue(value);
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
             * @param {Event} evt
             * @private
             */
            _inputChangeHandler: function (evt) {

                this.setMinScoreValue(this.getMinScoreValue());

                this.updateStateDelegate({
                    id: this.minScoreId,
                    type: 'DATA',
                    value: this.getMinScoreValue(),
                    immediate: false
                });

                if (this.formCtrl) {
                    this.$scope.$apply(this.formCtrl.$setDirty.bind(this.formCtrl));
                }
            },

            /**
             * Sets an input change handler.
             *
             * @private
             */
            _initInputChangeWatch: function () {
                var ctrl = this;

                // Lock in context
                function listenerFn (evt) {
                    ctrl._inputChangeHandler(evt);
                }

                ctrl.minScoreElement.on('change', listenerFn);

                // Cleanup
                ctrl.$scope.$on('$destroy', function () {
                    ctrl.minScoreElement.off('change', listenerFn);
                });
            },

            _initDisableSubmitOnEnter: function () {
                var ctrl = this;

                function disableEnterKey (evt) {
                    var code = evt.keyCode || evt.which;
                    if (code === 13) {
                        evt.preventDefault();

                        ctrl.minScoreElement.trigger('blur');
                        return false;
                    }
                }

                ctrl.minScoreElement.on('keyup keypress', disableEnterKey);

                // Cleanup
                ctrl.$scope.$on('$destroy', function () {
                    ctrl.minScoreElement.off('keyup keypress', disableEnterKey);
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

            _checkMinMaxValues: function (value) {
                if (value < MIN_VALUE) {
                    return MIN_VALUE;
                }

                if (value > MAX_VALUE_DEFAULT) {
                    return MAX_VALUE_DEFAULT;
                }

                return value;
            },


            /**
             * Sets the picker value.
             *
             * @param {string|number} value
             * @returns {*}
             */
            setMinScoreValue: function (value) {
                assert((_.isString(value) || _.isNumber(value)), 'fsMinScore.directive: FsMinScoreController:' +
                    ' setMinScoreValue: value argument must be a number or a string representing a number.');

                if (_.isString(value)) {
                    value = parseInt(value, 10) || 0;
                }

                value = this._checkMinMaxValues(value);

                this.minScoreElement.val(value);
            },

            /**
             * Gets the picker value
             *
             * @returns {string}
             */
            getMinScoreValue: function () {
                return this.minScoreElement.val();
            },
            /**
             * Init
             */
            init: function init () {

                var ctrl = this;
                ctrl.minScoreElement = ctrl.$element.find('.min-score-input');

                ctrl._validations();
                ctrl._initWatches();

            }
        });

        FsMinScoreController.$inject = ['$element', '$scope'];

        return {
            restrict: 'E',
            template: '<input type="number" class="min-score-input">',
            scope: {},
            link: linkFn,
            controller: FsMinScoreController,
            controllerAs: 'ctrl',
            bindToController: {
                minScoreId: '@',
                maxScore: '<?',
                fetchStateDelegate: '=',
                updateStateDelegate: '='
            },
            require: ['fsMinScore', '?^^form']
        };
    }

    fsMinScoreDirective.$inject = ['assert'];

    angular.module('Fortscale.shared.components.fsMinScore')
        .directive('fsMinScore', fsMinScoreDirective);
}());
