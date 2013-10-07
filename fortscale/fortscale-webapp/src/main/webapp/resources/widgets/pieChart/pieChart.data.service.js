angular.module("PieChartWidget").factory("pieChartWidgetData", ["utils", function(utils){
    return {
        getData: function(view, data, params){
            var viewData = { chartValues: [] };

            for(var i= 0, item; item = data[i]; i++){
                viewData.chartValues.push({
                    value: parseFloat(item[view.settings.chartValue], 10),
                    label: item[view.settings.chartLabel]
                })
            }

            if (view.settings.showInfo && view.settings.info && view.settings.info.properties){
                var getItemInfo = function(item, itemIndex){
                    var itemInfo = { properties: [] };
                    if (view.settings.info.title)
                        itemInfo.title = utils.strings.parseValue(view.settings.info.title, item, params, itemIndex);

                    for(var i= 0, property; property = view.settings.info.properties[i]; i++){
                        itemInfo.properties.push({
                            label: property.label,
                            value: utils.strings.parseValue(property.value, item, params, itemIndex)
                        });
                    }

                    return itemInfo;
                };

                viewData.items = [];
                var infoItem;
                for(i= 0; item = data[i]; i++){
                    viewData.items.push(getItemInfo(item, i));
                }
            }
            else{
                viewData.items = [];
                angular.forEach(viewData.chartValues, function(chartValue){
                    viewData.items.push({
                        title: chartValue.label
                    })
                });
            }
            return viewData;
        }
    };
}]);