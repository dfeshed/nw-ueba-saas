(function () {
    'use strict';

    function fsChartDirective(assert, chartSettings, chartModelMapping) {


        var errorMsg = 'fsChart.directive: ';


        /**
         * Controller constructor
         *
         * @constructor
         */
        function FsChartController($scope, $element, $attrs) {

            // Put dependencies on the controller instance
            this.$scope = $scope;
            this.$element = $element;
            this.$attrs = $attrs;

            this.init();
        }

        angular.extend(FsChartController.prototype, {

            /**
             * Validations
             */

            /**
             * @param {string=} errorMsg
             * @private
             */
            _validateModel: function (errorMsg) {
                errorMsg = errorMsg || '';
                // model should be an array
                assert(
                    _.isArray(this._model),
                    errorMsg + 'provided model must be an array.',
                    TypeError
                );
            },
            /**
             * @param {string=} errorMsg
             * @private
             */
            _validateSettings: function (errorMsg) {
                errorMsg = errorMsg || '';

                // Settings - if provided - should be an object.
                assert(
                    _.isObject(this.settings),
                    errorMsg + 'provided settings must be an object.',
                    TypeError
                );
            },

            _validateMapSettings: function (errorMsg) {
                errorMsg = errorMsg || '';

                // Settings - if provided - should be an object.
                if (this._mapSettings) {
                    assert(
                        _.isObject(this._mapSettings),
                        errorMsg + 'provided map settings must be an object.',
                        TypeError
                    );
                }
            },

            _validateStyleSettings: function (errorMsg) {
                errorMsg = errorMsg || '';

                // Settings - if provided - should be an object.
                if (this._styleSettings) {
                    assert(
                        _.isObject(this._styleSettings),
                        errorMsg + 'provided style settings must be an object.',
                        TypeError
                    );
                }
            },

            /**
             * Validates a required string type attribute
             *
             * @param {string} name
             * @param {string=} errorMsg
             * @private
             */
            _validateString: function (name, errorMsg) {
                errorMsg = errorMsg || '';

                // should be provided
                assert(
                    !_.isUndefined(this[name]),
                    errorMsg + name + ' must be provided in the directive html declaration.',
                    ReferenceError
                );
                // should be a string
                assert(
                    _.isString(this[name]),
                    errorMsg + 'provided ' + name + ' must be a string.',
                    TypeError
                );

                // should not be an empty string
                assert(
                    this[name] !== '',
                    errorMsg + 'provided ' + name + ' must not be an empty string.',
                    RangeError
                );
            },
            /**
             * Runs through required directive validations
             *
             * @param {string=} errorMsg
             * @private
             */
            _validations: function (errorMsg) {
                errorMsg = errorMsg || '';
                this._validateSettings(errorMsg);
                this._validateMapSettings(errorMsg);
                this._validateStyleSettings(errorMsg);
                this._validateString('_chartType', errorMsg);
            },

            /**
             * Init functions
             */

            /**
             * Initiates settings. These are common pie chart settings and can be overridden.
             *
             * @private
             */
            _initSettings: function () {
                this._settings = chartSettings.getSettings(this._chartType, this.settings);
            },

            /**
             * Sets style object to be used for styling the graphs container
             *
             * @private
             */
            _initStyling: function () {
                this._containerStyle = _.merge({
                    'minWidth': '25%', 'width': 'auto', 'maxWidth': '100%', margin: '0 auto', height: '20vw'
                }, this._styleSettings);
            },

            /**
             * Watch functions
             */

            /**
             * watch function. returns _model. Will cause _watchModelAction (implicitly) to fire
             * when _model changes.
             *
             * @returns {*}
             * @private
             */
            _watchModel: function () {
                return this._model;
            },

            /**
             * watch model action. When _model changes, the directive will render the graph.
             *
             * @param {*} newVal
             * @private
             */
            _watchModelAction: function (newVal) {

                // Only render if newVal exists
                if (newVal !== undefined) {
                    this._validateModel(errorMsg + '_watchModelAction: ');
                    this._render();
                }
            },

            /**
             * Set a $watch for _model
             *
             * @private
             */
            _initModelWatch: function () {
                this.$scope.$watch(
                    this._watchModel.bind(this),
                    this._watchModelAction.bind(this)
                );

            },
            /**
             * Creates a timeout that is cleared on the next invocation, which creates a throttle.
             * After 500ms fires _resizeHandler
             *
             * @private
             */
            _watchResizeAction: function () {
                clearTimeout(this._preResizeTimeout);
                this._preResizeTimeout = setTimeout(this._resizeHandler.bind(this), 200);
            },

            /**
             * Redraws the chart.
             *
             * @private
             */
            _resizeHandler: function () {
                // Remove animation from settings
                var settings = _.merge({}, this._settings, {
                    plotOptions: {
                        series: {
                            animation: false
                        }
                    }
                });
                // Redraw table
                this._chartContainer.highcharts(settings);
            },

            /**
             * Should rerender the chart when resize has finished
             *
             * @private
             */
            _initResizeWatch: function () {
                var ctrl = this;

                function resizeAction() {
                    ctrl._watchResizeAction();
                }

                window.addEventListener('resize', resizeAction, false);

                // Cleanup
                ctrl.$scope.$on('$destroy', function () {
                    window.removeEventListener('resize', resizeAction, false);
                });
            },

            /**
             * Initiates watches
             *
             * @private
             */
            _initWatches: function () {
                this._initModelWatch();
                this._initResizeWatch();
            },


            /**
             * Takes generic data list, and by using _mapName and _mapY it creates a new list of
             * objects that is given as data to the chart.
             *
             * @param {array} dataList
             * @returns {array}
             * @private
             */
            _mapData: function (dataList) {

                if (this._mapSettings) {
                    return chartModelMapping.mapData(this._mapSettings, dataList);
                }

                return dataList;

            },

            /**
             * Processes the data and renders the chart.
             *
             * @private
             */
            _render: function () {
                // Sometimes highcharts does not render the chart. We use $applyAsync to delay
                // render till the end of the current digest cycle, giving highchart the time
                // it needs.
                this.$scope.$applyAsync(function () {

                    // If no series, create an empty array
                    this._settings.series = this._settings.series || [];

                    if (this._settings.isMultiSeries) {
                        this._settings.series = _.map(this._model, _.bind(function (serie) {
                            var obj = {};
                            if (serie.name) {
                                obj.name = serie.name;
                            }
                            if (serie.color) {
                                obj.color = serie.color;
                            }
                            if (serie.data) {
                                obj.data = this._mapData(serie.data);
                            }
                            return obj;

                        }, this));
                    } else {
                        this._settings.series[0].data = this._mapData(this._model);
                    }

                    this._chartContainer.highcharts(this._settings);
                }.bind(this));
            },

            /**
             * A deep merge of received settings with default settings.
             * The received settings take precedence.
             *
             * @private
             */
            _mergeExternalSettings: function () {
                _.merge(this._settings, this.settings);
            },

            /**
             * The init function.
             */
            init: function init() {
                this._chartContainer = this.$element.find('.container');

                this._validations(errorMsg + 'init: ');
                this._initSettings();
                this._initStyling();
                this._mergeExternalSettings();
                this._initWatches();
            }
        });

        FsChartController.$inject = ['$scope', '$element', '$attrs'];

        return {
            templateUrl: 'app/shared/components/fs-chart/fs-chart.view.html',
            restrict: 'E',
            scope: true,
            controller: FsChartController,
            controllerAs: 'chart',
            bindToController: {
                _chartType: '@chartType',
                _mapSettings: '=mapSettings',
                settings: '=',
                _styleSettings: '=styleSettings',
                _model: '=model'
            }
        };
    }

    fsChartDirective.$inject = ['assert', 'chartSettings', 'chartModelMapping'];

    angular.module('Fortscale.shared.components.fsChart')
        .directive('fsChart', fsChartDirective);

}());
