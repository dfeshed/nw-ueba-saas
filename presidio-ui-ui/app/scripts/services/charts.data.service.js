(function () {
    'use strict';

    angular.module("ChartWidgets", ["Utils", "Styles", "Transforms"])
        .factory("chartWidgetsData", ["$q", "utils", "styles", "transforms", function ($q, utils, styles, transforms) {
            function getPredefinedValues(predefinedValuesSettings) {
                if (predefinedValuesSettings.type === "date") {
                    var dates = utils.date.getDatesSpan(predefinedValuesSettings.first, predefinedValuesSettings.last),
                        timestamps = [];

                    angular.forEach(dates, function (date) {
                        timestamps.push(date.valueOf());
                    });

                    return timestamps;
                }

                return null;
            }

            function isSameValue(type, val1, val2) {
                if (type === "date") {
                    if (!val1 || !val2) {
                        return false;
                    }

                    var moment1 = utils.date.getMoment(val1),
                        moment2 = utils.date.getMoment(val2);

                    return moment1.year() === moment2.year() && moment1.month() === moment2.month() &&
                        moment1.date() === moment2.date();
                }

                return false;
            }

            function isSmallerValue(type, val1, val2) {
                if (type === "date") {
                    if (!val1 || !val2) {
                        return false;
                    }

                    var moment1 = utils.date.getMoment(val1),
                        moment2 = utils.date.getMoment(val2);

                    return !utils.date.compareDates(moment1.toDate(), moment2.toDate()) && moment1 < moment2;
                }

                return false;
            }

            function createDefaultValue(seriesSettings, labelSettings, labelValue) {
                var value = {};
                value[labelSettings.field] = labelValue;
                angular.forEach(seriesSettings, function (series) {
                    value[series.field] = series.default !== undefined ? series.default : null;
                });

                return value;
            }

            function setPredefinedValues(data, seriesSettings, labelSettings) {
                var predefinedValuesSettings = labelSettings.predefinedValues,
                    labelValues = getPredefinedValues(predefinedValuesSettings),
                    labelField = labelSettings.field;

                if (predefinedValuesSettings.type === "date") {
                    while (data.length && isSmallerValue("date", data[0][labelField], labelValues[0])) {
                        data.splice(0, 1);
                    }
                }

                for (var i = 0; i < labelValues.length; i++) {
                    if (!isSameValue(predefinedValuesSettings.type, data[i] && data[i][labelField], labelValues[i])) {
                        data.splice(i, 0, createDefaultValue(seriesSettings, labelSettings, labelValues[i]));
                    }
                }
            }

            function setPercent(data, countField) {
                var total = 0;
                data.forEach(function (item) {
                    var count = item[countField];
                    if (count && !isNaN(count)) {
                        total += count;
                    }
                });

                data.forEach(function (item) {
                    var count = item[countField];
                    if (count && !isNaN(count)) {
                        item._percent = 100 * count / total;
                    }
                });
            }

            return {
                getData: function (view, data, params) {
                    var deferred = $q.defer(),
                        styleDeferreds = [],
                        styleDeferredsMapping = {};

                    var viewData = {chartValues: data};
                    if (view.settings.labels.predefinedValues) {
                        setPredefinedValues(viewData.chartValues, view.settings.series, view.settings.labels);
                    }

                    angular.forEach(view.settings.series, function (series, i) {
                        series._label = utils.strings.parseValue(series.label, data, params, i);

                        if (series.style) {
                            styleDeferredsMapping[String(styleDeferreds.length)] = i;
                            styleDeferreds.push(styles.getParseStyleFunction(series));
                        }

                        if (view.settings.setPercent) {
                            setPercent(viewData.chartValues, series.field);
                        }

                        if (series.tooltip) {
                            viewData.chartValues.forEach(function (item) {
                                item.tooltip = utils.strings.parseValue(series.tooltip, item, params);
                            });
                        }
                    });

                    if (view.settings.legend) {
                        viewData.legend = view.settings.legend;
                    }
                    else if (view.settings.series.length === 1 && view.settings.series[0].legend) {
                        viewData.legend = {items: []};
                        angular.forEach(data, function (item) {
                            viewData.legend.items.push({value: utils.strings.parseValue(view.settings.series[0].legend,
                                item, params)});
                        });
                    }

                    angular.forEach(viewData.chartValues, function (item, itemIndex) {
                        if (view.settings.labels.transform) {
                            item._label =
                                transforms[view.settings.labels.transform.method](item[view.settings.labels.field],
                                    view.settings.labels.transform.options);
                        } else {
                            item._label = utils.strings.parseValue(view.settings.labels.value, item, params, itemIndex);
                        }
                    });

                    if (view.settings.selectedData) {
                        for (var property in view.settings.selectedData) {
                            if (view.settings.selectedData.hasOwnProperty(property)) {
                                view.settings.selectedData[property] =
                                    utils.strings.parseValue(view.settings.selectedData[property], data, params);
                            }
                        }
                    }

                    if (styleDeferreds.length) {
                        $q.all(styleDeferreds).then(function (styleParsers) {
                            var colorSeries = [];

                            for (var styleDeferredIndex in styleDeferredsMapping) {
                                if (styleDeferredsMapping.hasOwnProperty(styleDeferredIndex)) {
                                    colorSeries.push({
                                        series: view.settings.series[styleDeferredsMapping[styleDeferredIndex]],
                                        styleParser: styleParsers[parseInt(styleDeferredIndex, 10)]
                                    });
                                }
                            }

                            angular.forEach(viewData.chartValues, function (item, itemIndex) {
                                item._style = {};
                                angular.forEach(colorSeries, function (colorSeriesItem) {
                                    item._style[colorSeriesItem.series.field] = colorSeriesItem.styleParser(item);
                                    if (!view.settings.legend && viewData.legend) {
                                        viewData.legend.items[itemIndex].color =
                                            item._style[colorSeriesItem.series.field].color;
                                    }
                                });
                            });

                            deferred.resolve(viewData);
                        });
                    } else {
                        deferred.resolve(viewData);
                    }

                    return deferred.promise;
                }
            };
        }]);
}());
