module Fortscale.layouts.overview.components.alertStatus {

    import IAlertStatus = Fortscale.layouts.overview.services.alertStatsUtils.IAlertStatus;
    import IAppConfigService = Fortscale.appConfigProvider.IAppConfigService;
    import IAlertStatsUtils = Fortscale.layouts.overview.services.alertStatsUtils.IAlertStatsUtils;

    declare var AmCharts:any;

    function alertStatusDirectiveFn () {

        const CHART_SELECTOR_SHORT = '.alert-status-short-chart-container';
        const CHART_SELECTOR_LONG = '.alert-status-long-chart-container';

        class AlertStatusController {
            alertStatusShort:IAlertStatus;
            alertStatusLong:IAlertStatus;
            containerElementShort:ng.IAugmentedJQuery;
            containerElementLong:ng.IAugmentedJQuery;

            _alertStatusChangeHandler (alertStatus:IAlertStatus, dateRange:string, containerElement:ng.IAugmentedJQuery,
                deregister:Function) {
                if (!alertStatus) {
                    return;
                }

                let chart = AmCharts.makeChart(containerElement[0],
                    {
                        "type": "serial",
                        "categoryField": "category",
                        "columnWidth": 0.47,
                        "colors": [],
                        "startDuration": 1,
                        "fontFamily": "Open Sans",
                        "color": "#01294A",
                        "categoryAxis": {
                            "autoRotateAngle": 1.8,
                            "gridPosition": "start",
                            "gridColor": "#ffffff"
                        },
                        "trendLines": [],
                        "autoMargins": false,
                        "marginBottom": 30,
                        "marginLeft": 0,
                        marginTop: 5,
                        marginRight: 0,
                        "graphs": [
                            {
                                "colorField": "color",
                                "fillAlphas": 1,
                                "id": "AmGraph-1",
                                "lineColorField": "color",
                                "title": "graph 1",
                                "type": "column",
                                "valueField": 'count',
                                "lineThickness": 2,
                                showHandOnHover: true
                            }
                        ],
                        "guides": [],
                        "valueAxes": [
                            {
                                "id": "ValueAxis-1",
                                "integersOnly": true,
                                "minimum": 0,
                                "title": null,
                                "labelsEnabled": false,
                                "axisAlpha": 0
                            }
                        ],
                        "allLabels": [],
                        "balloon": {},
                        "titles": [
                            {
                                "alpha": 0,
                                "id": "Title-1",
                                "size": 15,
                                "text": "Chart Title"
                            }
                        ],
                        "dataProvider": [
                            {
                                "category": "Reviewed",
                                "count": alertStatus.alert_status.Closed || 0,
                                "dateRange": dateRange,
                                "color": "#35a6da",
                                "feedbackFilterValues": "approved,rejected"
                            },
                            {
                                "category": "Unreviewed",
                                "count": alertStatus.alert_status.Open || 0,
                                "dateRange": dateRange,
                                "color": "#024d89",
                                "feedbackFilterValues":"none"

                            }
                        ]
                    }
                );

                chart.addListener("clickGraphItem", (chartObj) => {
                    this.transitionToAlerts(chartObj.item.dataContext.dateRange,
                        chartObj.item.dataContext.feedbackFilterValues);
                });

                deregister();
            }

            initAlertStatusShortWatch ($scope:ng.IScope):void {
                let deregister = $scope.$watch<IAlertStatus>(
                    () => {
                        return this.alertStatusShort
                    },
                    (alertStatus:IAlertStatus) => {
                        this._alertStatusChangeHandler(alertStatus, this.alertStatsUtils.shortDateRange,
                            this.containerElementShort, deregister);
                    });
            }

            initAlertStatusLongWatch ($scope:ng.IScope):void {
                let deregister = $scope.$watch<IAlertStatus>(
                    () => {
                        return this.alertStatusLong
                    },
                    (alertStatus:IAlertStatus) => {
                        this._alertStatusChangeHandler(alertStatus, this.alertStatsUtils.longDateRange,
                            this.containerElementLong, deregister);
                    });
            }

            transitionToAlerts (duration:string, feedbackFilterValues:string) {

                let feedbackFilterKey= 'ad.feedback_filter';
                let dateRangeFilterKey = 'ad.date_range_filter';
                let url = `alerts?${feedbackFilterKey}=${feedbackFilterValues}&${dateRangeFilterKey}=${duration}`;
                this.$scope.$applyAsync(() => {
                    this.$location.url(url);
                });
            }

            static $inject = ['$location', '$scope', 'appConfig', 'alertStatsUtils'];

            constructor (public $location:ng.ILocationService, public $scope:ng.IScope,
                public appConfig:IAppConfigService,
                public alertStatsUtils:IAlertStatsUtils) {
            }
        }

        function preLinkFn (scope:ng.IScope, instanceElement:ng.IAugmentedJQuery, instanceAttributes:ng.IAttributes,
            controller:AlertStatusController, transclude:ng.ITranscludeFunction) {

            // find container elements
            controller.containerElementShort = instanceElement.find(CHART_SELECTOR_SHORT);
            controller.containerElementLong = instanceElement.find(CHART_SELECTOR_LONG);

            // init watches
            controller.initAlertStatusShortWatch(scope);
            controller.initAlertStatusLongWatch(scope);

        }


        let directive:ng.IDirective = {
            controller: AlertStatusController,
            controllerAs: '$ctrl',
            bindToController: {
                alertStatusShort: '<',
                alertStatusLong: '<'
            },
            scope: true,
            link: {
                pre: preLinkFn,
            },
            templateUrl: 'app/layouts/overview/components/overview-alerts-status/overview-alert-status.component.html'

        };

        return directive;
    }

    angular.module('Fortscale.layouts.overview')
        .directive('overviewAlertStatus', alertStatusDirectiveFn)

}
