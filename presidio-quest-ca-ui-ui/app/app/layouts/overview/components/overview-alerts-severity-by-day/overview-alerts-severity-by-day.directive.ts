module Fortscale.layouts.overview.components.alertsSeverityByDay {

    import IAppConfigService = Fortscale.appConfigProvider.IAppConfigService;
    import IAlertStatsUtils = Fortscale.layouts.overview.services.alertStatsUtils.IAlertStatsUtils;
    import IAlertSeverityByDay = Fortscale.layouts.overview.services.alertStatsUtils.IAlertSeverityByDay;
    declare var AmCharts:any;

    function alertSeverityByDayDirectiveFn () {

        const CHART_CONTAINER_SELECTOR = '.alert-severity-by-day-chart-container';
        const STATUS_FILTER_KEY = 'ad.status_filter';
        const SEVERITY_FILTER_KEY = 'ad.severity_filter';
        const DATE_RANGE_FILTER_KEY = 'ad.date_range_filter';

        class AlertSeverityByDayController {
            containerElement:ng.IAugmentedJQuery;
            alertsSeverity:IAlertSeverityByDay[];
            timeFrame:string;

            /**
             * Receives an epch start of day and returns a range of that day to one day forward.
             *
             * @param {number} day
             * @returns {string}
             * @private
             */
            _getDateRangeByDay (day) {
                let startRange = Math.floor(day / 1000);
                let endRange = moment(day).utc().endOf('day').unix();
                return `${startRange},${endRange}`;
            }

            /**
             * Takes recieved response from the server, and converts it to an am-chart graph base list of objects.
             *
             * @returns {{day: number, dateRange: string, category: (any|string|Format|(function(number): string)|void),
             * Critical: any, High: any, Medium: any, Low: any}[]|boolean[]}
             * @private
             */
            _digestAlertsSeverity ():{
                category:string,
                day:number,
                Critical:number,
                High:number,
                Medium:number,
                Low:number
            }[] {

                return _.map(this.alertsSeverity, (alertSeverity:IAlertSeverityByDay) => {
                    let severityGroups = _.keyBy(alertSeverity.severities, 'severity');
                    return {
                        day: alertSeverity.day,
                        dateRange: this._getDateRangeByDay(alertSeverity.day),
                        category: moment(alertSeverity.day).format('DD-MM-YYYY'),
                        Critical: severityGroups["Critical"] ? severityGroups["Critical"].count : 0,
                        High: severityGroups["High"] ? severityGroups["High"].count : 0,
                        Medium: severityGroups["Medium"] ? severityGroups["Medium"].count : 0,
                        Low: severityGroups["Low"] ? severityGroups["Low"].count : 0
                    }
                });
            }

            /**
             * alertSeverity change handler. When alertsSeverity has an object, a graph will be rendered.
             * @param deregister
             * @private
             */
            _alertSeverityByDayChangeHandler (deregister:Function) {

                if (!this.alertsSeverity) {
                    return;
                }

                let dataProvider = this._digestAlertsSeverity();

                let chart = AmCharts.makeChart(this.containerElement[0],
                    {
                        "type": "serial",
                        "categoryField": "category",
                        "columnWidth": 0.48,
                        "dataDateFormat": "DD-MM-YYYY",
                        "autoMargins": false,
                        "marginBottom": 30,
                        "marginLeft": 20,
                        "marginRight": 30,
                        "marginTop": 40,
                        "colors": [
                            "#D32F2F",
                            "#FD7F1B",
                            "#69BDBD",
                            "#61aa02"
                        ],
                        "startDuration": 1,
                        "fontFamily": "'Open Sans', sans-serif",
                        "categoryAxis": {
                            "dateFormats": [
                                {
                                    "period": "fff",
                                    "format": "JJ:NN:SS"
                                },
                                {
                                    "period": "ss",
                                    "format": "JJ:NN:SS"
                                },
                                {
                                    "period": "mm",
                                    "format": "JJ:NN"
                                },
                                {
                                    "period": "hh",
                                    "format": "JJ:NN"
                                },
                                {
                                    "period": "DD",
                                    "format": "MMM DD"
                                },
                                {
                                    "period": "WW",
                                    "format": "MMM DD"
                                },
                                {
                                    "period": "MM",
                                    "format": "MMM"
                                },
                                {
                                    "period": "YYYY",
                                    "format": "YYYY"
                                }
                            ],
                            "gridPosition": "start",
                            "parseDates": true,
                            "gridColor": "#ffffff"
                        },
                        "graphs": [
                            {
                                "balloonText": "[[value]] [[title]] alerts on  [[category]]",
                                "fillAlphas": 1,
                                "id": "CriticalColumn",
                                "severity": "critical",
                                "title": "Critical",
                                "type": "column",
                                "valueField": "Critical",
                                "showHandOnHover": true
                            },
                            {
                                "balloonText": "[[value]] [[title]] alerts on  [[category]]",
                                "fillAlphas": 1,
                                "id": "HighColumn",
                                "severity": "high",
                                "title": "High",
                                "type": "column",
                                "valueField": "High",
                                "showHandOnHover": true
                            },
                            {
                                "balloonText": "[[value]] [[title]] alerts on  [[category]]",
                                "fillAlphas": 1,
                                "id": "MediumColumn",
                                "severity": "medium",
                                "title": "Medium",
                                "type": "column",
                                "valueField": "Medium",
                                "showHandOnHover": true
                            },
                            {
                                "balloonText": "[[value]] [[title]] alerts on  [[category]]",
                                "fillAlphas": 1,
                                "id": "LowColumn",
                                "severity": "low",
                                "title": "Low",
                                "type": "column",
                                "valueField": "Low",
                                "showHandOnHover": true
                            }
                        ],
                        "guides": [],
                        "valueAxes": [
                            {
                                "id": "ValueAxis-1",
                                "stackType": "regular",
                                "title": "",
                                "labelsEnabled": false,
                                "axisAlpha": 0
                            }
                        ],
                        "balloon": {},
                        "dataProvider": dataProvider
                    }
                );

                // Add click handler
                chart.addListener("clickGraphItem", (chartObj) => {
                    this.transitionToAlerts(chartObj.item.dataContext.dateRange, chartObj.graph.severity)
                });

                // Remove listener (so the chart will only be rendered once)
                deregister();

            }


            /**
             * Transition to alerts page.
             *
             * @param {string} duration
             * @param {string} severity
             */
            transitionToAlerts (duration:string, severity:string) {
                let url = `alerts?${STATUS_FILTER_KEY}=_ALL_&${SEVERITY_FILTER_KEY}=${severity}&${DATE_RANGE_FILTER_KEY}=${duration}`;
                this.$scope.$applyAsync(() => {
                    this.$location.url(url);
                });
            }

            /**
             * Init watcher on alertsSeverity
             * @param {IScope} scope
             */
            initAlertSeverityByDayWatch (scope:ng.IScope) {
                let deregister = scope.$watch(() => {
                    return this.alertsSeverity;
                }, () => {
                    this._alertSeverityByDayChangeHandler(deregister);
                });
            }

            static $inject = ['$location', 'appConfig', '$scope', 'alertStatsUtils', '$filter'];

            constructor (public $location:ng.ILocationService, public appConfig:IAppConfigService,
                public $scope:ng.IScope, public alertStatsUtils:IAlertStatsUtils, public $filter:ng.IFilterService) {
            }
        }

        function preLinkFn (scope:ng.IScope, instanceElement:ng.IAugmentedJQuery, instanceAttributes:ng.IAttributes,
            controller:AlertSeverityByDayController, transclude:ng.ITranscludeFunction) {

            // find container elements
            controller.containerElement = instanceElement.find(CHART_CONTAINER_SELECTOR);

            // init watches
            controller.initAlertSeverityByDayWatch(scope);

        }


        let directive:ng.IDirective = {
            controller: AlertSeverityByDayController,
            controllerAs: '$ctrl',
            bindToController: {
                alertsSeverity: '<',
                timeFrame: '@'
            },
            scope: true,
            link: {
                pre: preLinkFn,
            },
            templateUrl: 'app/layouts/overview/components/overview-alerts-severity-by-day/overview-alerts-severity-by-day.component.html'

        };

        return directive;
    }

    angular.module('Fortscale.layouts.overview')
        .directive('overviewAlertSeverityByDay', alertSeverityByDayDirectiveFn)

}
