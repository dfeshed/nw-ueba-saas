(function () {
    'use strict';

    function fsSplitter(assert, utils) {

        function linkFn(scope, element, attrs, ctrl, transclude) {

            // Init splitter
            var settings = _.merge({}, ctrl._settings, {
                collapse: function () {
                    ctrl._splitterCollapseHandler();
                },
                expand: function () {
                    ctrl._splitterExpandHandler();
                }
            });
            element.kendoSplitter(settings);
            ctrl.splitterElement = element.data('kendoSplitter');

            // Init watches

            if (ctrl.fetchStateDelegate) {
                scope.$watch(ctrl._watchState.bind(ctrl), ctrl._watchStateAction.bind(ctrl));
            }
        }

        /**
         * The directive's controller function
         * Added instead of using a Link function
         * Properties are bound on the Controller instance and available in the view
         *
         * @constructor
         */
        function FsSplitterController($scope, $element, $attrs) {
            // Put dependencies on the controller instance
            this.$scope = $scope;
            this.$element = $element;
            this.$attrs = $attrs;

            this.init();
        }

        _.merge(FsSplitterController.prototype, {

            /**
             * Action for state.collapsed.
             *
             * @param {string} collapsed When 'true' its the same as true
             * @private
             */
            _watchStateCollapseAction: function (collapsed) {
                if (collapsed !== undefined &&
                    collapsed !== this.state.collapsed) {
                    if (collapsed === 'true') {
                        this._collapsePane();
                    } else {
                        this._expandPane();
                    }

                    this.state.collapsed = collapsed;
                }
            },

            /**
             * A watch state function (registered with $watch)
             *
             * @returns {*}
             * @private
             */
            _watchState: function () {
                return this.fetchStateDelegate(this._id);
            },

            /**
             * A watch state action function. Fires when state changes.
             *
             * @param {object} newVal
             * @private
             */
            _watchStateAction: function (newVal) {
                if (newVal) {

                    if (!this.state) {
                        this.state = {};
                    }

                    this._watchStateCollapseAction(newVal.collapsed);

                }
            },

            /**
             * Updates the state's control.
             *
             * @private
             */
            _updateControl: function () {
                this.updateStateDelegate({
                    id: this._id,
                    type: 'ui',
                    value: this.state,
                    immediate: 'true'
                });
            },

            /**
             * Collapses the first pane.
             *
             * @private
             */
            _collapsePane: function () {
                this.splitterElement.collapse(".k-pane");
            },

            /**
             * Expands the first pane.
             *
             * @private
             */
            _expandPane: function () {
                this.splitterElement.expand(".k-pane");
            },

            /**
             * Splitter collapse event handler. Sets internal state (for dirty checking)
             * and updates the control.
             *
             * @private
             */
            _splitterCollapseHandler: function () {
                this.state = this.state || {};
                this.state.collapsed = 'true';

                // Trigger apply because the event is not an angular event.
                this.$scope.$apply(function () {
                    this._updateControl();
                }.bind(this));

            },

            /**
             * Splitter expand event handler. Sets internal state (for dirty checking)
             * and updates the control.
             *
             * @private
             */
            _splitterExpandHandler: function () {
                this.state = this.state || {};
                this.state.collapsed = 'false';

                // Trigger apply because the event is not an angular event.
                this.$scope.$apply(function () {
                    this._updateControl();
                }.bind(this));

            },

            /**
             * Init
             */
            init: function init() {
            }
        });

        FsSplitterController.$inject = ['$scope', '$element', '$attrs'];

        return {
            restrict: 'A',
            scope: true,
            controller: FsSplitterController,
            controllerAs: 'splitter',
            bindToController: {
                _settings: '=splitterSettings',
                _id: '@splitterId',
                fetchStateDelegate: '=',
                updateStateDelegate: '='
            },
            link: linkFn
        };
    }

    fsSplitter.$inject = ['assert', 'utils'];

    angular.module('Fortscale.shared.components.fsSplitter', [])
        .directive('fsSplitter', fsSplitter);
}());
