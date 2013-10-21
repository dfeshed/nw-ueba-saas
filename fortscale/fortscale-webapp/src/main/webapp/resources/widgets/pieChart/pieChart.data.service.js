angular.module("PieChartWidget").factory("pieChartWidgetData", ["utils", "transforms", function(utils, transforms){
    return {
        getData: function(view, data, params){
            var viewData = { chartValues: [] },
                labelsIndex = {},
                itemLabelIndex,
                setProperties = view.settings.showInfo && view.settings.info && view.settings.info.properties;

            var getItemProperties = function(item){
                var itemProperties = {};

                for(var i= 0, property, propertyValue; property = view.settings.info.properties[i]; i++){
                    propertyValue = itemProperties[property.label] = utils.strings.parseValue(property.value, item, params);

                    if (property.transform){
                        itemProperties[property.label] = transforms[property.transform.method](propertyValue, property.transform.options);
                    }
                }

                return itemProperties;
            };

            for(var i= 0, item, label, itemValue; item = data[i]; i++){
                label = item[view.settings.chartLabel];
                itemValue = parseFloat(item[view.settings.chartValue]);

                itemLabelIndex = labelsIndex[label];
                if (!itemLabelIndex){
                    itemLabelIndex = labelsIndex[label] = { label: label, value: 0 };
                    if (setProperties)
                        itemLabelIndex.properties = {};
                }

                itemLabelIndex.value += itemValue;

                if (setProperties){
                    var itemProperties = getItemProperties(item),
                        itemPropertyValues,
                        propertyValue;

                    for(var propertyName in itemProperties){
                        itemPropertyValues = itemLabelIndex.properties[propertyName];
                        if (!itemPropertyValues)
                            itemPropertyValues = itemLabelIndex.properties[propertyName] = [];

                        propertyValue = itemProperties[propertyName];

                        if (propertyValue && !~itemPropertyValues.indexOf(propertyValue) && propertyValue !== "-")
                            itemPropertyValues.push(itemProperties[propertyName]);
                    }
                }
            }

            for(var label in labelsIndex){
                viewData.chartValues.push(labelsIndex[label]);
            }

            viewData.chartValues.sort(function(a, b){
                return a.value === b.value ? 0 :
                    a.value < b.value ? 1 : -1;
            });

            return viewData;
        }
    };
}]);