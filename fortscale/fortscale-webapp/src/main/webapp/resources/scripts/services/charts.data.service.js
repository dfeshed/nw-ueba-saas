angular.module("ChartWidgets", ["Utils", "Styles", "Transforms"]).factory("chartWidgetsData", ["$q", "utils", "styles", "transforms", function($q, utils, styles, transforms){
    return {
        getData: function(view, data, params){
            var deferred = $q.defer(),
                styleDeferreds = [],
                styleDeferredsMapping = {};

            var viewData = { chartValues: data };
            angular.forEach(view.settings.series, function(series, i){
                series.label = utils.strings.parseValue(series.label, data, params, i);

                if (series.style){
                    styleDeferredsMapping[String(styleDeferreds.length)] = i;
                    styleDeferreds.push(styles.getParseStyleFunction(series));
                }
            });

            if (view.settings.legend){
                viewData.legend = view.settings.legend;
            }
            else if (view.settings.series.length === 1 && view.settings.series[0].legend){
                viewData.legend = { items: [] };
                angular.forEach(data, function(item){
                    viewData.legend.items.push({ value: utils.strings.parseValue(view.settings.series[0].legend, item, params) });
                });
            }

            angular.forEach(viewData.chartValues, function(item, itemIndex){
                if (view.settings.labels.transform)
                    item._label = transforms[view.settings.labels.transform.method](item[view.settings.labels.field], view.settings.labels.transform.options);
                else
                    item._label = utils.strings.parseValue(view.settings.labels.value, item, params, itemIndex);

            });

            if (view.settings.selectedData){
                for(var property in view.settings.selectedData){
                    view.settings.selectedData[property] = utils.strings.parseValue(view.settings.selectedData[property], data, params);
                }
            }

            if (styleDeferreds.length){
                $q.all(styleDeferreds).then(function(styleParsers){
                    var colorSeries = [];

                    for(var styleDeferredIndex in styleDeferredsMapping){
                        colorSeries.push({
                            series: view.settings.series[styleDeferredsMapping[styleDeferredIndex]],
                            styleParser: styleParsers[parseInt(styleDeferredIndex, 10)]
                        });
                    }

                    angular.forEach(viewData.chartValues, function(item, itemIndex){
                        item._style = {};
                        angular.forEach(colorSeries, function(colorSeriesItem){
                            item._style[colorSeriesItem.series.field] = colorSeriesItem.styleParser(item);
                            if (!view.settings.legend && viewData.legend)
                                viewData.legend.items[itemIndex].color = item._style[colorSeriesItem.series.field].color;
                        })
                    });

                    deferred.resolve(viewData);
                });
            }
            else
                deferred.resolve(viewData);

            return deferred.promise;
        }
    }
}]);