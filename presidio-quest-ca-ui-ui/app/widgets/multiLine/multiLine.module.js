(function () {
    'use strict';

    angular.module("MultiLineWidget", ["Utils", "Chart", "Widgets"])
        .run(["utils", "widgetViews", "config",
            function (utils, widgetViews, config) {

                function multiLineDataParser (view, data) {

                    function getParser (parser) {
                        if (parser.type === "date") {
                            if (parser.format) {
                                var d3timeFormat = config.alwaysUtc ? d3.time.format.utc : d3.time.format;
                                var parseDate = d3timeFormat(parser.format).parse;

                                if (parser.startOfDay) {
                                    return function (str) {
                                        var date = parseDate(str);
                                        date.setSeconds(0);
                                        date.setMinutes(0);
                                        date.setHours(0);
                                    };
                                }
                                else {
                                    return function (str) {
                                        if (!str) {
                                            return null;
                                        }

                                        if (typeof str === "number") {
                                            return utils.date.getMoment(str).toDate();
                                        }

                                        return parseDate(str);
                                    };
                                }
                            }
                            else {
                                return function (value) {
                                    var m = utils.date.getMoment(value);

                                    if (m.isValid()) {
                                        if (parser.startOfDay) {
                                            m.startOf("day");
                                        }

                                        return m.toDate();
                                    }

                                    return null;
                                };
                            }
                        }

                        return null;
                    }

                    var seriesIndex = {},
                        xField = view.settings.x;

                    if (view.settings.xType) {
                        var xParser = getParser(view.settings.xType);
                    }

                    data.forEach(function (item) {
                        if (xParser) {
                            item["_" + xField] = item[xField];
                            item[xField] = xParser(item[xField]);
                        }
                        var seriesName = item[view.settings.series],
                            series = seriesIndex[seriesName];

                        if (!series) {
                            series = seriesIndex[seriesName] = {name: seriesName, values: []};
                        }

                        series.values.push(item);
                    });

                    function sortIndex (a, b) {
                        var aVal = a[xField], bVal = b[xField];

                        if (aVal === bVal) {
                            return 0;
                        }

                        return aVal > bVal ? 1 : -1;
                    }

                    for (var seriesName in seriesIndex) {
                        if (seriesIndex.hasOwnProperty(seriesName)) {
                            seriesIndex[seriesName].values.sort(sortIndex);
                        }
                    }


                    return utils.objects.toArray(seriesIndex);
                }

                widgetViews.registerView("multiLine", {dataParser: multiLineDataParser});

            }]);
}());
