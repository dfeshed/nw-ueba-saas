(function () {
    'use strict';

    var setOfProcess = new Set(["add", "combine", "extend", "getUsersDetails", "groupBy", "groupByField", "map", "sort",
        "limit"]);

    function ReportClass ($q, utils, reports) {
        var allReports = {};

        function Report (config) {
            this.validate(config);

            function getReportById (reportId) {
                /* jshint validthis: true */
                this._initializing = true;
                onInit = [];
                reports.getReport(reportId).then(function (reportConfig) {
                    delete config.reportId;
                    utils.objects.extend(reportConfig, config);
                    setReportProperties(reportConfig);

                    if (onInit.length) {
                        for (var onInitDeferred of onInit) {
                            onInitDeferred.resolve(self);
                        }
                    }
                }, function (error) {
                    if (onInit.length) {
                        for (var onInitDeferred of onInit) {
                            onInitDeferred.reject(error);
                        }
                    }
                }).finally(function () {
                    onInit = null;
                    delete self._initializing;
                    delete self.onInitSubscribe;
                });
            }

            function setReportProperties (reportConfig) {
                self.validate(reportConfig);

                self.endpoint = reportConfig.endpoint;
                self.params = reportConfig.params ? reportConfig.params.map(function (paramConfig) {
                    return new ReportParam(paramConfig);
                }) : [];
                self.allowCache = reportConfig.allowCache !== false;
                self.mockData = reportConfig.mock_data;
                self.requiredParams = reportConfig.requiredParams;
                self.process = reportConfig.process;

                if (reportConfig.joinReports) {
                    self.joinReports = reportConfig.joinReports.reports.map(function (childReport) {
                        var childReportConfig = reportConfig.joinReports.common ?
                            utils.objects.extend({}, reportConfig.joinReports.common, childReport) : childReport;
                        return new Report(childReportConfig);
                    });
                }
            }

            var self = this,
                onInit;

            if (config.reportId) {
                getReportById(config.reportId);
            } else {
                setReportProperties(config);
            }

            this._config = config;


            self.onInitSubscribe = function () {
                var deferred = $q.defer();
                onInit.push(deferred);
                return deferred.promise;
            };
        }

        Report.prototype.validate = function (config) {
            var param = null;

            if (!config) {
                throw new Error("No report configuration to validate.");
            }

            if (Object(config) !== config) {
                throw new TypeError("Invalid report configuration, expected an object but got " + config);
            }

            if (config.reportId && typeof(config.reportId) !== "string") {
                throw new TypeError("Can't create report, the 'reportId' property must be a string.");
            }

            if (!config.endpoint && !config.joinReports) {
                throw new Error("Can't create report, missing the 'endpoint' property.");
            }

            if (config.endpoint && Object(config.endpoint) !== config.endpoint) {
                throw new TypeError("Can't create report, endpoint is not an object.");
            }

            if (config.joinReports) {
                if (!angular.isObject(config.joinReports)) {
                    throw new TypeError("Invalid joinReports, expected an object but got " + config.joinReports);
                }

                if (!config.joinReports.reports) {
                    throw new Error("Report.joinReports is mising the 'reports' property.");
                }

                if (!angular.isArray(config.joinReports.reports)) {
                    throw new TypeError("Invalid 'reports' for joinReports, expected an Arrya but got " +
                        config.joinReports.reports);
                }
            }

            if (config.mock_data && typeof(config.mock_data) !== "string") {
                throw new TypeError("Can't create report, expected a string for mock_data but got " +
                    typeof(config.mock_data) + ".");
            }

            if (config.params) {
                if (config.params.constructor !== Array) {
                    throw new TypeError("Can't create report, expected params to be an array but got " + config.params);
                }

                for (param of config.params) {
                    if (param.dashboardParam === undefined && param.value === undefined &&
                        param.default === undefined) {
                        throw new Error("Can't create report, param doesn't have either 'dashboardParam', " +
                            "'value' or 'default' properties.");
                    }
                }
            }

            if (config.requiredParams) {
                if (config.requiredParams.constructor !== Array) {
                    throw new TypeError("Cant' create report, requiredParams must be an Array.");
                }

                for (param of config.requiredParams) {
                    if (typeof(param) !== "string") {
                        throw new TypeError("Can't create report, invalid required param, expected a string but got " +
                            param + ".");
                    }
                }
            }

            if (config.process) {
                if (config.process.constructor !== Array) {
                    config.process = [config.process];
                }
                for (var process of config.process) {
                    if (!process.processId || typeof(process.processId) !== "string") {
                        throw new TypeError("Cant' create report, processId must be a string.");
                    }
                    if (!setOfProcess.has(process.processId)) {
                        throw new TypeError("Cant' create report, unknown processId " + process.processId);
                    }
                }
            }

        };

        /**
         * Creates a deep copy of the specified Report
         * @param report
         * @returns {ReportClass.Report}
         */
        Report.copy = function (report) {
            var newReport = new Report(report._config);
            for (var p in report) {
                if (report.hasOwnProperty(p) && p !== "joinReports") {
                    newReport[p] = report[p];
                }
            }

            return newReport;
        };

        /**
         * Returns a copy of this Report
         */
        Report.prototype.clone = function () {
            return Report.copy(this);
        };

        /**
         * Runs the report. Returns a promise which is resolved when data is ready or an error occurs.
         * @param {object} state Params to use when running the report
         * @param {number} priority The priority on which to run the report - lower number is higher priority
         * @param {boolean} noCache Whether to allow data to be retrieved from cache
         * @returns {Promise}
         */
        Report.prototype.run = function (state, priority, noCache) {
            var report = this;
            this.isLoading = true;

            if (this._initializing) {
                return this.onInitSubscribe().then(function () {
                    return report.run(state);
                }, function (error) {
                    report.isLoading = false;
                    return $q.reject(error);
                });
            }
            else {
                var forceRefresh = noCache !== undefined ? !!noCache : !report.allowCache;

                return reports.runReport(this, state, forceRefresh, priority).finally(function () {
                    report.isLoading = false;
                });
            }
        };

        Report.loadReport = function (config) {
            if (config.reportId) {
                var existingReport = allReports[config.reportId];
                if (existingReport) {
                    return $q.when(existingReport);
                }

                return utils.http.wrappedHttpGet("data/reports/" + config.reportId.replace(/\./g, "/") +
                    ".json").then(function (reportConfig) {
                    var fullReportConfig = utils.objects.extend({}, reportConfig, config),
                        reportId = fullReportConfig.reportId;

                    delete fullReportConfig.reportId;
                    var report = new Report(fullReportConfig);
                    allReports[reportId] = report;
                    return report;
                }, function (error) {
                    var errorMessage = error.status === 404 ? "Report '" + config.reportId + "' not found." :
                    "Can't get report '" + config.reportId + "'. Error: " + error.data;
                    return $q.reject(errorMessage);
                });
            }

            return new Report(config);
        };

        function ReportParam (config) {
            /**
             * The name of the param in the state params object
             * @type string
             */
            this.dashboardParam = config.dashboardParam;

            /**
             * The value of the param - will be parsed using the state params object
             */
            this.value = config.value;

            /**
             * Default value for the param, if it's not present in the state params object (Optional)
             */
            this["default"] = config.default;

            /**
             * The name of the param inside the report
             */
            this.field = config.field;

            this.isRequired = !!config.isRequired;

            return this;
        }

        return Report;
    }

    ReportClass.$inject = ["$q", "utils", "reports"];

    angular.module("Reports").factory("Report", ReportClass);
})();
