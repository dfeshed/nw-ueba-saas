module Fortscale.shared.components.fsAmChartComponent {

    import INanobarAutomationService = Fortscale.shared.services.fsNanobarAutomation.INanobarAutomationService;
    declare var AmCharts:any;

    class AmChartController {

        settings:any;
        indicator:any;
        chart:any;
        containerElement:ng.IAugmentedJQuery;
        _events:any[];

        _INDICATORS_DATA_PATH_NAME:string = 'evidences';
        _HISTORICAL_DATA_PATH_NAME:string = 'historical-data';
        _NANOBAR_ID:string = 'user-page';
        _ERR_MSG:string = 'AmChartComponent: ';
        _renderReady:boolean = false;


        /**
         * Finds the chart's container element and stores it. Returns the element.
         * @returns {ng.IAugmentedJQuery}
         * @private
         */
        _getContainerElement ():ng.IAugmentedJQuery {
            if (!this.containerElement) {
                this.containerElement = this.$element.find('.chart-container');
            }

            return this.containerElement;
        }

        /**
         * Returns a promise that resolves on fetched data from the server
         * @returns {IHttpPromise<T>}
         * @private
         */
        _fetchData ():ng.IPromise<any> {
            let params = this.interpolation.interpolate(this.settings.params, this.indicator);
            return this.$http.get(
                `${this.BASE_URL}/${this._INDICATORS_DATA_PATH_NAME}/${this.indicator.id}/${this._HISTORICAL_DATA_PATH_NAME}`,
                {params: params})
                .then((res:ng.IHttpPromiseCallbackArg<any>) => {
                    return res.data;
                });

        }

        /**
         * Takes received data and applies sorting, and adapter. Returns a list ready for use on amCharts
         * @param response
         * @private
         */
        _digestData (response: {data: any[]}) {

            if (this.settings.preProcessData) {
                response = this.settings.preProcessData(response, this.indicator);
            }

            // sort data if sorter provided by settings
            let list = this.settings.sortData ? this.settings.sortData(response.data) : response.data;

            // digest data if adapter provided by settings
            this.settings.chartSettings.dataProvider =
                this.settings.dataAdapter ? _.map(list, this.settings.dataAdapter.bind(this, this.indicator)) : list;

        }

        _augmentSettings () {
            this._addTitles();
            this._addListeners();
        }

        /**
         * Adds chart listeners
         * @private
         */
        _addListeners () {

            let ctrl = this;
            this.settings.chartSettings.listeners = [{
                "event": "clickGraphItem",
                "method": (graphItem:any) => {
                    if (ctrl.settings.handlers && ctrl.settings.handlers.clickGraphItem) {
                        ctrl.settings.handlers.clickGraphItem(ctrl.indicator, graphItem);
                    }
                }
            }];
        }

        /**
         * Adds a title to the chart if listed in this.settings.templates.titles
         * @private
         */
        _addTitles () {
            if (this.settings.templates && this.settings.templates.titles) {
                _.each(this.settings.templates.titles, (value:any, key:string) => {
                    let titleObj:any = _.find(this.settings.chartSettings.titles, {id: key});
                    titleObj.text = this.$interpolate(value)(this.indicator);
                });
            }
            if (this.settings.templates && this.settings.templates.valueAxes) {
                _.each(this.settings.templates.valueAxes, (value:any, key:string) => {
                    let titleObj:any = _.find(this.settings.chartSettings.valueAxes, {id: key});
                    titleObj.title = this.$interpolate(value)(this.indicator);
                });
            }
        }

        /**
         * Renders a chart
         * @private
         */
        _renderChart () {
            if (this._renderReady) {
                this.chart = AmCharts.makeChart(this._getContainerElement()[0], this.settings.chartSettings);
                this.chart.addListener("rendered", this._initLegenForPie(this.settings.chartSettings.type,this.chart));

            }
        }

        _initLegenForPie(type:string,chartObject:any){
            if (type === "pie"){

                let containerHeight: number=this._getContainerElement ().height();

                let legendContainer :ng.IAugmentedJQuery = this._getContainerElement ().find('.amcharts-legend-div');
                let ctrl=this;

                if (!_.isNil(legendContainer)) {
                    // legendContainer.css("height","250px");
                    let newContainerHeight = containerHeight-20;
                    ctrl._setLegendContainerHeight(legendContainer,newContainerHeight);
                    // alert(legendContainer.height());
                    ctrl.$timeout(
                        ()=> {
                            if (legendContainer.height() != newContainerHeight) {
                                ctrl._setLegendContainerHeight(legendContainer,newContainerHeight);
                            }
                        },700);
                    chartObject.legend.addListener('clickLabel', ()=>{ctrl._setLegendContainerHeight(legendContainer,newContainerHeight)});
                    chartObject.legend.addListener('clickMarker', ()=>{ctrl._setLegendContainerHeight(legendContainer,newContainerHeight)});
                    chartObject.addListener('drawn', ()=>{ctrl._setLegendContainerHeight(legendContainer,newContainerHeight)});
                    chartObject.addListener('rendered', ()=>{ctrl._setLegendContainerHeight(legendContainer,newContainerHeight)});

                }

            }
        }

        _setLegendContainerHeight(legendContainer:ng.IAugmentedJQuery, newContainerHeight:any){

            if (!_.isNil(legendContainer)) {
                legendContainer.height(newContainerHeight);
                legendContainer.css("overflow-x", "hidden");
                legendContainer.css("overflow-y", "auto");
            }

        }


         /** Sets watch on settings. When settings received, data is fetched and processed, and chart is rendered.
         * @private
         */
        _initSettingsWatch () {
            this.$scope.$watch(
                () => this.settings,
                () => {
                    if (this.settings) {
                        this._renderReady = false;
                        let promise = this._fetchData()
                            .then((data) => {
                                this._digestData(data);
                                this._augmentSettings();
                                this._renderReady = true;
                                this._renderChart();
                            })
                            .catch((err) => {
                                console.error(this._ERR_MSG + 'There was a problem loading data', err);
                            });

                        this.fsNanobarAutomationService.addPromise(this._NANOBAR_ID, promise);


                    }
                }
            );
        }


        $onInit () {
            this._initSettingsWatch();
        }

        static $inject = ['$scope', '$element', '$http', 'BASE_URL', 'interpolation', '$interpolate',
            'fsNanobarAutomationService','$timeout'];

        constructor (public $scope:ng.IScope, public $element:ng.IAugmentedJQuery,
            public $http:ng.IHttpService, public BASE_URL:string, public interpolation:any,
            public $interpolate:ng.IInterpolateService, public fsNanobarAutomationService:INanobarAutomationService,
                     public $timeout:ng.ITimeoutService) {
        }
    }

    let AmChartComponent:ng.IComponentOptions = {
        controller: AmChartController,
        bindings: {
            settings: '<',
            indicator: '<',
            _events: '<events'
        },
        template: '<div class="chart-container" style="position: absolute; width: 100%; height: 100%;"></div>'
    };

    angular.module('Fortscale.shared.components')
        .component('fsIndicatorAmChart', AmChartComponent);
}
