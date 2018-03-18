(function () {
    'use strict';

    var START_TIME_FIELD_NAME = 'start_time';
    var USERNAME_FIELD_NAME = 'username';
    var END_TIME_FIELD_NAME = 'end_time';
    var DURATION_FIELD_NAME = 'duration';
    var DATA_USAGE_FIELD_NAME = 'data_bucket';
    var DATA_USAGE_SCORE_FIELD_NAME = 'data_bucket_score';

    function SuspiciousVPNDataAmountController (appConfig, $state, $scope, dateRanges, SEVERITIES, utils, $filter,
        userControlSettings, userControlResource, tableResource, tableSettings, chartResource,
        fsNanobarAutomationService) {

        var ctrl = this;

        // Put injections on instance
        ctrl.state = _.merge({}, $state.current.data);
        ctrl.$scope = $scope;
        ctrl.SEVERITIES = SEVERITIES;
        ctrl.utils = utils;
        ctrl.$filters = $filter;
        ctrl.dateRanges = dateRanges;

        ctrl._defaultDaysRange = appConfig.getConfigValue('ui.' + $state.current.name, 'daysRange');

        ctrl.userControlSettings = _.merge({}, userControlSettings);
        ctrl.userControlResource = _.merge({}, userControlResource);
        ctrl.tableResource = tableResource;
        ctrl.tableSettings = _.merge({}, tableSettings);
        ctrl.chartResource = chartResource;

        // Lock in controller as activation context
        ctrl.chartResourceAdapter = function (dataList) {
            return ctrl._chartResourceAdapter(dataList);
        };

        this.NANOBAR_ID = 'reports';
        this.dataFetchDelegate = (promise) => {
            fsNanobarAutomationService.addPromise(this.NANOBAR_ID, promise);
        };

        // Init
        this.init();
    }

    _.merge(SuspiciousVPNDataAmountController.prototype, {

        /**
         * Initiates report main state
         *
         * @private
         */
        _initMainState: function () {

            var ctrl = this;
            ctrl.mainState = _.merge({}, {
                events_time: {
                    value: ctrl.dateRanges.getByDaysRange(ctrl._defaultDaysRange, 'short')
                },
                min_score: {
                    value: 50
                },
                suspicious_vpn_data_amount_table: {
                    value: {
                        pageSize: 20,
                        page: 1,
                        sortDirection: 'DESC',
                        sortBy: 'data_bucket_score'
                    }
                }
            });
        },

        /**
         * Initiates chart settings
         *
         * @private
         */
        _initChartSettings: function () {
            var ctrl = this;

            ctrl.chartSettings = {
                "chart": {
                    "type": "scatter"
                },
                "title": {
                    "text": null
                },
                "xAxis": {
                    "type": "datetime",
                    "dateTimeLabelFormats": {
                        "month": "%e. %b",
                        "year": "%b"
                    },
                    "title": {
                        "text": "Date"
                    },
                    "tickInterval": 1000 * 60 * 60 * 24
                },
                "yAxis": {
                    "type": 'datetime',
                    "tickInterval": 3600 * 1000,
                    min: 0,
                    max: (1000 * 60 * 60 * 24) - 1,
                    labels: {
                        formatter: function () {
                            var hour = this.value / 1000 / 60 / 60;
                            return (hour < 10 ? '0' : '') + hour + ':00';
                        }
                    },
                    "title": {
                        "text": "Hours"
                    }
                },
                "isMultiSeries": true,
                plotOptions: {
                    series: {
                        marker: {
                            radius: 5,
                            symbol: 'circle'
                        }
                    }
                },
                tooltip: {
                    formatter: function () {
                        var s = '';
                        s += '<b>Username: </b>' + this.point.username + '<br>';
                        s += '<b>Start Time: </b>' + moment(this.point.startTime).format("MMM Do YY HH:mm:ss") + '<br>';
                        s += '<b>End Time: </b>' + moment(this.point.endTime).format("MMM Do YY HH:mm:ss") + '<br>';
                        s += '<b>Duration: </b>' + moment(this.point.endTime).format("HH:mm:ss") + '<br>';
                        s += '<b>Data Usage: </b>' + ctrl.$filters('prettyBytes')(this.point.dataUsage) + '/s<br>';
                        s += '<b>Data Usage Score: </b>' + this.point.dataUsageScore + '<br>';
                        return s;
                    }
                },
                legend: {
                    "enabled": true,
                    layout: 'vertical',
                    align: 'right',
                    verticalAlign: 'top',
                    x: 0,
                    y: 40
                }
            };
        },

        /**
         * Initiates chart map settings
         *
         * @private
         */
        _initChartMapSettings: function () {
            var ctrl = this;
            ctrl.chartMapSettings = {
                x: {
                    key: START_TIME_FIELD_NAME,
                    /**
                     * In effect always returns the start of day, so all markers fall on the same line on the x axis.
                     *
                     * @param {number} startDate
                     * @returns {number}
                     */
                    fn: function (startDate) {
                        return ctrl.utils.date.getMoment(startDate).startOf('day').valueOf();
                    }
                },
                y: {
                    key: START_TIME_FIELD_NAME,
                    /**
                     * Returns only the delta between start of day and date, thus producing only the time in day
                     * for the y axis.
                     *
                     * @param {number} startDate
                     * @returns {number}
                     */
                    fn: function (startDate) {
                        var endTime = ctrl.utils.date.getMoment(startDate).valueOf();
                        var startTime = ctrl.utils.date.getMoment(startDate).startOf('day').valueOf();
                        return (endTime - startTime);
                    }
                },
                username: USERNAME_FIELD_NAME,
                startTime: START_TIME_FIELD_NAME,
                endTime: END_TIME_FIELD_NAME,
                duration: DURATION_FIELD_NAME,
                dataUsage: DATA_USAGE_FIELD_NAME,
                dataUsageScore: DATA_USAGE_SCORE_FIELD_NAME
            };
        },

        /**
         * Converts returned data list to series list by severity
         *
         * @param {Array<{data_bucket_score: number}>} dataList
         * @returns {Array<{name: string, color: string, scoreRange: Array<number,number>, data: Array<{}>}>}
         * @private
         */
        _convertToSeries: function (dataList) {

            // Group by severity
            var dataListGrouped = _.groupBy(dataList, _.bind(function (dataItem) {
                return this.SEVERITIES.getByScore(dataItem[DATA_USAGE_SCORE_FIELD_NAME]).id;
            }, this));

            // Create series list
            var series = _.map(dataListGrouped, _.bind(function (group, groupName) {
                var severity = this.SEVERITIES.getById(groupName);
                return {
                    name: severity.displayName,
                    color: severity.color,
                    data: group,
                    scoreRange: severity.scoreRange
                };
            }, this));

            // Sort series list by score range bottom end (should produce low, medium, high, critical)
            return _.orderBy(series, 'scoreRange[0]', 'asc');

        },

        /**
         * Chart resource adapter. Invokes and returns _convertToSeries, which converts data list to list of series.
         *
         * @param {Array<{data_bucket_score: number}>} dataList
         * @returns {*|Array.<{name: string, color: string, scoreRange: Array.<number, number>, data: Array.<{}>}>}
         * @private
         */
        _chartResourceAdapter: function (dataList) {
            return this._convertToSeries(dataList);
        },

        /**
         * Controller's init function
         */
        init: function () {
            this._initMainState();
            this._initChartMapSettings();
            this._initChartSettings();
        }
    });

    SuspiciousVPNDataAmountController.$inject =
        ['appConfig', '$state', '$scope', 'dateRanges', 'SEVERITIES', 'utils', '$filter',
            'userControlSettings', 'userControlResource', 'tableResource', 'tableSettings', 'chartResource',
            'fsNanobarAutomationService'];
    angular.module('Fortscale.layouts.reports')
        .controller('suspiciousVPNDataAmountController', SuspiciousVPNDataAmountController);
}());
