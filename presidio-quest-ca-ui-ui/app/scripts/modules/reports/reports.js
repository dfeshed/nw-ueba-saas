(function () {
    'use strict';

    /**
     * Service for running reports - configurations of calls to the REST API
    **/
    function reports ($q, $timeout, DAL, Cache, reportsProcess, utils, configFlags, DataQuery, conditions, state) {
        var cache = new Cache({id: "reports"}), // this is for data
            cachedReports = {}, // This one is just for the report definitions
            runReportsTimeout;

        var globalSearchParams = ["page", "limit", "offset", "pageSize", "orderBy", "orderByDirection"];

        var queue = {
            clearRunReportQueue: function () {
                console.log("CLEAR: ");
                if (!queue.runReportQueue) {
                    return;
                }

                queue.runReportQueue.forEach(function (reportPriority) {
                    reportPriority.reports.forEach(function (report) {
                        if (report.deferred) {
                            report.deferred.reject({aborted: true});
                        }

                        if (configFlags.verbose) {
                            console.log("Aborted report ", report);
                        }
                    });
                });

                queue.runReportQueue = null;
                queue.currentRunningPriority = null;
            },
            findReportPosition: function (reportObj) {
                var priorityIndex, reportIndex, priority;
                for (priorityIndex = 0; priorityIndex < this.runReportQueue.length; priorityIndex++) {
                    priority = this.runReportQueue[priorityIndex];
                    for (reportIndex = 0; reportIndex < priority.reports.length; reportIndex++) {
                        if (reportObj === priority.reports[reportIndex]) {
                            return {priority: priority, reportIndex: reportIndex, priorityIndex: priorityIndex};
                        }
                    }
                }

                return null;
            },
            runReportQueue: null,
            currentRunningPriority: null,
            currentRunningReports: null,
            runNextQueuedPriority: function () {
                if (queue.currentRunningReports && queue.currentRunningReports.length) {
                    return;
                }

                if (!queue.runReportQueue || !queue.runReportQueue.length) {
                    return;
                }

                var nextQueuePriority;

                for (var priorityIndex = 0, priority; !!(priority = queue.runReportQueue[priorityIndex]);
                     priorityIndex++) {
                    if (priority.reports && priority.reports.length) {
                        nextQueuePriority = priority;
                        break;
                    }
                }

                if (!nextQueuePriority) {
                    return;
                }

                queue.currentRunningPriority = nextQueuePriority;
                queue.currentRunningReports = [];
                queue.currentRunningPriority.reports.forEach(function (reportObj) {
                    if (~queue.currentRunningReports.indexOf(reportObj)) {
                        return true;
                    }

                    queue.currentRunningReports.push(reportObj);
                    if (configFlags.verbose) {
                        console.log("Run report: ", reportObj);
                    }

                    runReport(reportObj).then(function (results) {
                        var deferred = reportObj.deferred;
                        delete reportObj.deferred;
                        deferred.resolve(results);
                    }, function (error) {
                        var deferred = reportObj.deferred;
                        delete reportObj.deferred;
                        deferred.reject(error);
                    }).finally(function () {
                        queue.currentRunningReports.splice(queue.currentRunningReports.indexOf(reportObj), 1);
                        queue.runNextQueuedPriority();
                    });
                });

                this.currentRunningPriority.reports = [];
            },
            queueReport: function (reportObj) {
                var deferred = $q.defer();
                $timeout.cancel(runReportsTimeout);

                reportObj.deferred = deferred;

                if (!queue.runReportQueue) {
                    queue.runReportQueue = [{priority: reportObj.priority, reports: [reportObj]}];
                } else {
                    var added;

                    for (var i = 0, reportPriority; i < queue.runReportQueue.length; i++) {
                        reportPriority = queue.runReportQueue[i];
                        if (reportPriority.priority === reportObj.priority) {
                            reportPriority.reports.push(reportObj);
                            added = true;
                            break;
                        }
                    }

                    if (!added) {
                        queue.runReportQueue.push({priority: reportObj.priority, reports: [reportObj]});
                    }

                    queue.runReportQueue.sort(function (a, b) {
                        return a.priority > b.priority ? 1 : -1;
                    });
                }

                runReportsTimeout = $timeout(function () {
                    queue.runNextQueuedPriority();
                }, 40);

                return deferred.promise;
            }
        };

        function parseParams (report, params) {
            var parsedParams = {};
            angular.forEach(report.params, function (param) {
                var paramValue = params[param.dashboardParam],
                    fieldName = param.field;

                if (param.value && paramValue !== undefined && paramValue !== null && paramValue !== "") {
                    parsedParams[fieldName] = utils.strings.parseValue(param.value, params, {});
                } else if (paramValue !== undefined && paramValue !== null && paramValue !== "") {
                    parsedParams[fieldName] = paramValue;
                } else if ((parsedParams[fieldName] === undefined || parsedParams[fieldName] === null ||
                    parsedParams[fieldName] === "") && param.default !== undefined && param.default !== null) {
                    parsedParams[fieldName] =
                        typeof(param.default) === "string" ? utils.strings.parseValue(param.default, {}, params) :
                            param.default;
                }
            });

            if (params) {
                for (var paramName in params) {
                    if (params.hasOwnProperty(paramName)) {
                        if (~globalSearchParams.indexOf(paramName) && params[paramName] !== undefined &&
                            params[paramName] !== null) {
                            parsedParams[paramName] = params[paramName];
                        }
                    }
                }
            }

            return parsedParams;
        }

        function getInSeconds (value) {
            var valueMatch = value.match(/^(\d+)(\w)$/);
            if (!valueMatch) {
                throw new Error("Invalid time period value: " + value);
            }

            var int = parseInt(valueMatch[1], 10),
                unit = valueMatch[2];

            if (unit === "s") {
                return int;
            }

            if (unit === "m") {
                return int * 60;
            }

            if (unit === "h") {
                return int * 3600;
            }

            if (unit === "d") {
                return int * 3600 * 24;
            }

            throw new Error("Invalid time period value: " + value);
        }

        function checkRequiredParams (report, params) {
            var result = {success: true, missingParams: []};

            if (report.params) {
                report.params.forEach(function (param) {
                    if (param.isRequired && !params[param.dashboardParam]) {
                        result.success = false;
                        result.missingParams.push(param.dashboardParam);
                    }
                });
            }

            return result;
        }

        function getCachedDataByKey (cacheItemKey, forceRefresh) {
            if (forceRefresh) {
                cache.removeItem(cacheItemKey);
                return null;
            }
            else {
                var cachedData = cache.getItem(cacheItemKey, {hold: true});
                if (cachedData) {
                    return cachedData;
                }
            }

            return null;
        }

        function getCacheKey (report, params) {
            var keyParams = utils.objects.copy(params),
                reportEndpoint = utils.objects.copy(report.endpoint) || "";

            if (reportEndpoint) {
                delete reportEndpoint.fields;
                delete reportEndpoint.sort;
                delete reportEndpoint.paging;

                if (reportEndpoint.entities) {
                    var entityIds = [];
                    reportEndpoint.entities.forEach(function (entity) {
                        entityIds.push(entity.id);
                    });
                    reportEndpoint.entities = entityIds.join("_");
                }
            }

            if (report.cacheIgnoredParams) {
                report.cacheIgnoredParams.forEach(function (ignoredParam) {
                    delete keyParams[ignoredParam];
                });
            }
            return JSON.stringify(reportEndpoint) + "_" + JSON.stringify(keyParams);
        }

        function getReportDataFromCache (reportObj, cacheKey) {
            if (!reportObj.report || !reportObj.report.cache) {
                return null;
            }

            var report = reportObj.report,
                params = reportObj.params;

            var parsedParams = parseParams(report, params),
                cacheItemKey = cacheKey || getCacheKey(report, parsedParams);

            var cachedData = getCachedDataByKey(cacheItemKey, reportObj.forceRefresh);
            if (cachedData) {
                return cachedData;
            }
        }

        function runReport (reportObj) {

            /**
             * This function resolves the reports.
             * It handle the process in sense that if there is few process like "groupBy" and "limit",
             * it will chain then one at the time while the processed data will pass from process to process
             * @param results
             * @param index
             * @returns {*}
             */
            function resolve (results, index) {
                if (report.process && results.data) {
                    var processesArr = [];
                    //if the value is not in array, we push it to the array
                    if (angular.isArray(report.process)) {
                        processesArr = report.process;
                    } else {
                        processesArr.push(report.process);
                    }
                    // we format the index if this is the first run
                    index = index ? index : 0;

                    return $q.when(reportsProcess.processData(processesArr[index].processId, results,
                        processesArr[index].params))
                        .then(function (processedResults) {
                            if (index < processesArr.length - 1) {
                                //if there is more then one process, we will call this function again and pass the new
                                // processed data to it
                                index++;
                                return resolve(processedResults, index);
                            } else {
                                // else we return the processed data
                                return finishResolve(processedResults);
                            }
                        }, onError);
                }
                else {
                    return finishResolve(results);
                }
            }

            function finishResolve (results) {
                if (report.cache && results.data.length) {
                    var saveData = utils.objects.copy(results);
                    delete saveData.$promise;
                    delete saveData.$resolved;
                    delete saveData.time;

                    cache.setItem(cacheItemKey, results, {expiresIn: getInSeconds(report.cache), hold: false});
                }

                results.time = utils.date.getMoment('now').toDate() - timeStart;
                return results.data ? results : {data: [], total: 0, time: results.time};
            }

            function onError (error) {
                if (report.cache) {
                    cache.removeItem(cacheItemKey);
                }

                return $q.reject(error);
            }

            function doRunReport () {
                return DAL.reports.runReport(report, parsedParams)
                    .then(function (results) {
                        return resolve(results);
                    }, onError);
            }
            var report = reportObj.report,
                params = reportObj.params,
                forceRefresh = reportObj.forceRefresh,
                timeStart = utils.date.getMoment('now').toDate();

            if (typeof(report) === "string") {
                return methods.runReportById(report, params, forceRefresh);
            }

            var requiredParamsCheck = checkRequiredParams(report, params);
            if (!requiredParamsCheck.success) {
                return $q.reject({
                    error: "requiredParams",
                    message: "Missing required parameters: " + requiredParamsCheck.missingParams.join(", ") + "."
                });
            }

            var parsedParams = parseParams(report, params),
                cacheItemKey = getCacheKey(report, parsedParams),
                cachedData = getReportDataFromCache(reportObj, cacheItemKey);

            //support addition of condition check on an report,
            //Uses for a case where there is a report on a general entity as User but with a condition on a special
            // data source. If the condition is not meet the report wont be execute.
            if (report.endpoint && report.endpoint.conditions &&
                !conditions.validateConditions(report.endpoint.conditions, null, state.currentParams)) {
                return resolve({data: [], total: 0});
            }

            if (cachedData) {
                return $q.when(cachedData);
            }

            if (report.joinReports) {
                return methods.runReports(report.joinReports, report, params, forceRefresh).then(function (results) {
                    var data = [],
                        total = 0,
                        totalTime = 0;

                    angular.forEach(results, function (result) {
                        data = data.concat(result.data);
                        total += result.total;
                        totalTime += result.time || 0;
                    });

                    return resolve({data: data, total: total});
                });
            }


            // Since we're replacing the dataQuery configuration with the data for the REST API,
            // stash the configuration for later use.
            var dataQueryConfig = report.dataQueryConfig || report.endpoint && report.endpoint.dataQuery;

            if (dataQueryConfig) {
                try {
                    try {
                        report.endpoint.dataQuery = dataQueryConfig instanceof DataQuery ? dataQueryConfig :
                            new DataQuery(dataQueryConfig, parsedParams);
                        if (!report.dataQueryConfig) {
                            report.dataQueryConfig = dataQueryConfig;
                        }

                    }
                    catch (error) {
                        return $q.reject({message: "Can't create DataQuery. Error: " + error.message, error: error});
                    }
                    if (configFlags.verbose) {
                        console.log("Running DataQuery: ", report.endpoint.dataQuery);
                    }

                    return doRunReport();
                }
                catch (error) {
                    return $q.reject(error);
                }
            }

            return doRunReport();

        }

        var methods = {
            abortCurrentReports: queue.clearRunReportQueue,
            getReport: function (reportId) {
                if (cachedReports[reportId]) {
                    return $q.when(cachedReports[reportId]);
                } else {
                    return DAL.reports.getReport(reportId).then(function (report) {
                        cachedReports[reportId] = report;
                        return report;
                    });
                }
            },
            runReport: function (report, params, forceRefresh, priority) {
                var reportObj = {
                    priority: priority || 0,
                    report: report,
                    params: params,
                    forceRefresh: forceRefresh
                };

                var cachedData = getReportDataFromCache(reportObj);
                if (cachedData) {
                    return $q.when(cachedData);
                }

                if (!angular.isNumber(priority)) {
                    return runReport(reportObj);
                }

                return queue.queueReport(reportObj);
            },
            runReportById: function (reportId, params, forceRefresh) {
                return methods.getReport(reportId).then(function (report) {
                    return methods.runReport(report, params, forceRefresh);
                });
            },
            runReports: function (reports, parentReport, params, forceRefresh) {
                var promises = [],
                    common;

                if (Object(reports) === reports) {
                    if (!!(common = reports.common)) {
                        reports = reports.reports.map(function (report) {
                            return utils.objects.extend({}, common, report);
                        });
                    }
                }

                angular.forEach(reports, function (report) {
                    var reportCopy = report.constructor.name === "Report" ? report.clone() : utils.objects.copy(report);
                    reportCopy.params =
                        reportCopy.params ? reportCopy.params.concat(parentReport.params || []) : parentReport.params;
                    promises.push(methods.runReport(reportCopy, params, forceRefresh));
                });

                return $q.all(promises);
            }
        };

        return methods;
    }

    reports.$inject =
        ["$q", "$timeout", "DAL", "Cache", "reportsProcess", "utils", "configFlags", "DataQuery", "conditions",
            "state"];

    angular.module("Reports").factory("reports", reports);
})();
