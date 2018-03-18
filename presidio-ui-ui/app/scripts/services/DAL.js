(function () {
    'use strict';

    angular.module("DAL", ["Version", "Utils", "Config"])
        .factory("DAL", ["$http", "$q", "api", "version", "utils", "configFlags", "$timeout",
            function ($http, $q, api, version, utils, configFlags, $timeout) {
                var runningReports = {};

                var methods = {
                    dashboards: {
                        getDashboardById: function (dashboardId) {
                            if (!dashboardId) {
                                return $q.reject("No dashboard ID specified.");
                            }

                            return utils.http.wrappedHttpGet("data/dashboards/" + dashboardId.replace(/\./g, "/") +
                                ".json?v=" + version)
                                .catch(function (error) {
                                    var errorMessage = error.status === 404 ?
                                    "Dashboard '" + dashboardId + "' not found." :
                                    "Can't get dashboard '" + dashboardId + "'. Error: " + error.data;

                                    errorMessage += ' Redirecting to Overview.';

                                    var err = {
                                        message: errorMessage,
                                        code: 404,
                                        redirectToState: 'overview'
                                    };

                                    return $q.reject(err);
                                });
                        }
                    },
                    entities: {
                        getEntity: function (entityId) {
                            return utils.http.wrappedHttpGet("data/entities/" + entityId + ".json?v=" + version)
                                .catch(function (error) {
                                    var errorMessage = error.status === 404 ? "Entity '" + entityId + "' not found." :
                                    "Can't get entity '" + entityId + "'. Error: " + error.data;
                                    return $q.reject(errorMessage);
                                });
                        }
                    },
                    filters: {
                        getSavedSearch: function (searchId) {
                            var search = localStorage.getItem("search_" + searchId);
                            if (search) {
                                return $q.when(JSON.parse(search));
                            }

                            return $q.reject("Saved search with ID '" + searchId + "' not found.");
                        },
                        getSavedSearches: function () {
                            var searches = [],
                                keyMatch = /^search_/;

                            for (var key in localStorage) {
                                if (localStorage.hasOwnProperty(key)) {
                                    if (keyMatch.test(key)) {
                                        searches.push(JSON.parse(localStorage[key]));
                                    }
                                }
                            }

                            return $q.when(searches);
                        },
                        save: function (search) {
                            var searchId = search.name.toLowerCase().replace(/\W/g, "_"),
                                storageKey = "search_" + searchId;

                            if (localStorage[storageKey] && !confirm('A search named "' + search.name +
                                    '" already exists. Overwrite?')) {
                                return $q.reject("A search named \"" + search.name + "\" already exists.");
                            }

                            search.id = searchId;
                            localStorage.setItem("search_" + searchId, JSON.stringify(search));
                            return $q.when(search);
                            //return utils.http.wrappedHttpPost()
                        },
                        remove: function (search) {
                            var searchId = search.name.toLowerCase().replace(/\W/g, "_"),
                                storageKey = "search_" + searchId;
                            localStorage.removeItem(storageKey);
                            return $q.when(search);
                        }
                    },
                    reports: {
                        /**
                         * Gets a report config file's contents
                         * @param reportId The ID of the report to get. Dots (.) are replaced with slashes to form
                         * folder paths (user.top_events -> user/top_events.json)
                         * @returns {promise}
                         */
                        getReport: function (reportId) {
                            var deferred = $q.defer();

                            $http.get("data/reports/" + reportId.replace(/\./g, "/") + ".json?v=" + version)
                                .success(deferred.resolve)
                                .error(deferred.reject);

                            return deferred.promise;
                        },
                        /**
                         * Routes the report between mock data or server API. If any of the required params are not
                         * available, returns empty data.
                         * @param report The report object
                         * @param params The report params, each with the value ready to send
                         * @returns {promise}
                         */
                        runReport: function (report, params) {

                            function resolveSameReports (results, error) {
                                var runningReportDeferreds = runningReports[runningReportKey];
                                if (runningReportDeferreds) {
                                    runningReportDeferreds.forEach(function (deferred) {
                                        if (error) {
                                            deferred.reject(error);
                                        } else {
                                            deferred.resolve(results);
                                        }
                                    });
                                }

                                delete runningReports[runningReportKey];
                            }

                            function onReportSuccess (results) {
                                resolveSameReports(results);
                                return results;
                            }

                            function onReportError (error) {
                                var errorMessage = error.status === 404 ? "File '" + fileUrl + "' not found." :
                                    error.data;
                                resolveSameReports(null, errorMessage);
                                return $q.reject(errorMessage);
                            }

                            // report.delay is used for testing only, to simulate slow requests.
                            if (report.delay) {
                                var reportCopy = report.clone(),
                                    deferred = $q.defer();

                                delete reportCopy.delay;

                                $timeout(function () {
                                    methods.reports.runReport(reportCopy, params)
                                        .then(deferred.resolve, deferred.reject);
                                }, report.delay);

                                return deferred.promise;
                            }

                            var runningReportKey = JSON.stringify({report: report, params: params}),
                                runningReportDeferreds = runningReports[runningReportKey];

                            // If the same report is being queried at the moment, there's no need to query it again,
                            // just use the same results:
                            if (runningReportDeferreds) {
                                var reportDeferred = $q.defer();
                                runningReportDeferreds.push(reportDeferred);
                                return reportDeferred.promise;
                            } else {
                                runningReports[runningReportKey] = [];
                            }

                            // Transitioning from the old (mock_data) to the new (mockData)
                            var mockData = report.mockData || report.mock_data;

                            if (mockData && configFlags.mockData) {
                                var urlParams = utils.url.getQuery(params),
                                    fileUrl = "data/mock_data/" + utils.strings.parseValue(mockData, {}, params)
                                            .replace(/\./g, "/") + ".json?v=" + new Date().valueOf() +
                                        (urlParams ? "&" + urlParams : "");

                                return utils.http.wrappedHttpGet(fileUrl)
                                    .then(onReportSuccess.bind(report), onReportError);
                            }
                            else {
                                var paramValue;

                                if (report.requiredParams) {
                                    for (var i = 0; i < report.requiredParams.length; i++) {
                                        paramValue = params[report.requiredParams[i]];
                                        if (!paramValue && paramValue !== 0) {
                                            return $q.when({
                                                data: [],
                                                total: 0
                                            }).then(onReportSuccess.bind(report), onReportError);
                                        }
                                    }
                                }
                                return api.query(report, params, report.options)
                                    .then(onReportSuccess.bind(report), onReportError);
                            }

                        }
                    },
                    widgets: {
                        getWidget: function (widgetId) {
                            return utils.http.wrappedHttpGet("data/widgets/" + widgetId.replace(/\./g, "/") +
                                ".json?v=" + version).catch(function (error) {
                                var errorMessage = error.status === 404 ? "Widget '" + widgetId + "' not found." :
                                "Can't get widget '" + widgetId + "'. Error: " + error.data;
                                return $q.reject(errorMessage);
                            });
                        }
                    }
                };

                return methods;
            }]);
}());
