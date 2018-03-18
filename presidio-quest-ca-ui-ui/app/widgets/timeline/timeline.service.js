(function () {
    'use strict';

    angular.module("TimelineWidget").factory("timelineService", ["$q", "reports", function ($q, reports) {
        return {
            getEarlierData: function (settings, params, currentData) {
                var deferred = $q.defer(),
                    newData = angular.copy(currentData);

                reports.runReport(settings.getPageReport, params, true).then(function (results) {
                    angular.forEach(results.data, function (series) {
                        var existingSeries = getSeriesByName(series[settings.series.name]);
                        if (!existingSeries) {
                            newData.push(series);
                        } else {
                            var timeSpansArray = existingSeries[settings.series.timeSpansSeries];
                            angular.forEach(series[settings.series.timeSpansSeries], function (timeSpanSeriesGroup) {
                                if (settings.series.timeSpansSeriesGroupName) {
                                    var existingGroup = getGroupByName(timeSpansArray,
                                        timeSpanSeriesGroup[settings.series.timeSpansSeriesGroupName]);
                                    if (!existingGroup) {
                                        timeSpansArray.push(timeSpanSeriesGroup);
                                    } else {
                                        existingGroup[settings.series.timeSpans] =
                                            timeSpanSeriesGroup[settings.series.timeSpans]
                                                .concat(existingGroup[settings.series.timeSpans]);
                                    }
                                }
                            });
                        }
                    });

                    deferred.resolve(newData);
                }, deferred.reject);

                function getSeriesByName (seriesName) {
                    for (var i = 0, series; !!(series = newData[i]); i++) {
                        if (series[settings.series.name] === seriesName) {
                            return series;
                        }
                    }

                    return null;
                }

                function getGroupByName (groupsArray, groupName) {
                    for (var i = 0, group; !!(group = groupsArray[i]); i++) {
                        if (group[settings.series.timeSpansSeriesGroupName] === groupName) {
                            return group;
                        }
                    }

                    return null;
                }

                return deferred.promise;
            }
        };
    }]);
}());
