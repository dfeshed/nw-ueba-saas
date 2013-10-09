angular.module("PieChartWidget").factory("pieChartWidgetData", ["utils", "transforms", function(utils, transforms){
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

                    for(var i= 0, property, propertyData; property = view.settings.info.properties[i]; i++){
                        propertyData = {
                            label: property.label,
                            value: utils.strings.parseValue(property.value, item, params, itemIndex)
                        };

                        if (property.transform){
                            propertyData.value = transforms[property.transform.method](propertyData.value, property.transform.options);
                        }

                        itemInfo.properties.push(propertyData);
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