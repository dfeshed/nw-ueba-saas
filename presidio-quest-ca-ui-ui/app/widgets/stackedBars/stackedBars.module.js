(function () {
    'use strict';

    angular.module("StackedBarsWidget", ["Utils", "Chart", "Widgets"]).run(["utils", "widgetViews",
        function (utils, widgetViews) {

            function stackedBarsDataParser (view, data) {
                var seriesIndex = {},
                    series = [],
                    labels = {},
                    labelTotals;

                if (view.settings.calculatePercentage) {
                    labelTotals = {};
                    data.forEach(function (row) {
                        var label = row[view.settings.label];

                        if (labelTotals[label] === undefined) {
                            labelTotals[label] = row[view.settings.value];
                        } else {
                            labelTotals[label] += row[view.settings.value];
                        }
                    });

                    data.forEach(function (row) {
                        var label = row[view.settings.label];
                        row._percent = 100 * row[view.settings.value] / labelTotals[label];
                    });
                }

                data.forEach(function (row) {
                    var seriesName = row[view.settings.series],
                        seriesValues = seriesIndex[seriesName],
                        rowLabel = row[view.settings.label];

                    if (!seriesValues) {
                        seriesValues = seriesIndex[seriesName] = {dataIndex: {}, name: seriesName};
                    }

                    seriesValues.dataIndex[rowLabel] = {
                        label: rowLabel,
                        value: view.settings.calculatePercentage ? row._percent : row[view.settings.value],
                        rawData: row
                    };
                    labels[rowLabel] = true;
                });

                var seriesValues,
                    label;
                var seriesName;

                // Add missing labels, with value=0:
                for (label in labels) {
                    if (labels.hasOwnProperty(label)) {
                        for (seriesName in seriesIndex) {
                            if (seriesIndex.hasOwnProperty(seriesName)) {
                                seriesValues = seriesIndex[seriesName];
                                if (!seriesValues.dataIndex[label]) {
                                    seriesValues.dataIndex[label] = {label: label, value: 0, rawData: null};
                                }
                            }
                        }
                    }
                }

                for (seriesName in seriesIndex) {
                    if (seriesIndex.hasOwnProperty(seriesName)) {
                        seriesValues = seriesIndex[seriesName];
                        seriesValues.data = utils.objects.toArray(seriesValues.dataIndex);
                        delete seriesValues.dataIndex;
                        seriesValues.data.sort(function (a, b) {
                            return a.label > b.label ? 1 : -1;
                        });
                    }
                }

                if (view.settings.seriesOrder) {
                    view.settings.seriesOrder.forEach(function (seriesName) {
                        var s = seriesIndex[seriesName];
                        if (s) {
                            s.name = seriesName;
                            series.push(s);
                        }
                    });
                }
                else {
                    series = utils.objects.toArray(seriesIndex, function (seriesName, d) {
                        d.name = seriesName;
                        return d;
                    });
                }

                return series;
            }

            widgetViews.registerView("stackedBars", {dataParser: stackedBarsDataParser});

        }]);
}());
