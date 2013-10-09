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

            angular.forEach(viewData.chartValues, function(item, itemIndex){
                if (view.settings.labels.transform)
                    item._label = transforms[view.settings.labels.transform.method](item[view.settings.labels.field], view.settings.labels.transform.options);
                else
                    item._label = utils.strings.parseValue(view.settings.labels.value, item, params, itemIndex);
            });

            if (styleDeferreds.length){
                $q.all(styleDeferreds).then(function(styleParsers){
                    var colorSeries = [];

                    for(var styleDeferredIndex in styleDeferredsMapping){
                        colorSeries.push({
                            series: view.settings.series[styleDeferredsMapping[styleDeferredIndex]],
                            styleParser: styleParsers[parseInt(styleDeferredIndex, 10)]
                        });
                    }

                    angular.forEach(viewData.chartValues, function(item){
                        item._style = {};
                        angular.forEach(colorSeries, function(colorSeriesItem){
                            item._style[colorSeriesItem.series.field] = colorSeriesItem.styleParser(item);
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