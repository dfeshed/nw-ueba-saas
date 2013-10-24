angular.module("PieChartWidget").factory("pieChartWidgetData", ["utils", "transforms", function(utils, transforms){
    var propertyCalcFunctions = {
        max: function(propertyValues, newValue){
            if (!propertyValues.length)
                propertyValues.push(newValue);
            else if (newValue > propertyValues[0])
                propertyValues[0] = newValue;
        }
    };

    return {
        getData: function(view, data, params){
            var viewData = { chartValues: [] },
                labelsIndex = {},
                itemLabelIndex,
                setProperties = view.settings.showInfo && view.settings.info && view.settings.info.properties,
                setPropertiesObj = {};

            angular.forEach(setProperties, function(propertySettings){
                setPropertiesObj[propertySettings.label] = propertySettings;
            });

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

            var propertyValues;
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
                    var itemProperties = {},
                        itemPropertyValues,
                        propertyValue;


                    for(var propertyIndex = 0, property, propertyValue; property = view.settings.info.properties[propertyIndex]; propertyIndex++){
                        itemProperties[property.label] = item[property.value];
                    }

                    for(var propertyName in itemProperties){
                        itemPropertyValues = itemLabelIndex.properties[propertyName];
                        if (!itemPropertyValues)
                            itemPropertyValues = itemLabelIndex.properties[propertyName] = [];

                        propertyValue = itemProperties[propertyName];

                        if (propertyValue && !~itemPropertyValues.indexOf(propertyValue) && propertyValue !== "-"){
                            var propertyCalc = setPropertiesObj[propertyName].calc;
                            if (!propertyCalc || !propertyCalcFunctions[propertyCalc])
                                itemPropertyValues.push(itemProperties[propertyName]);
                            else
                                propertyCalcFunctions[propertyCalc](itemPropertyValues, itemProperties[propertyName]);
                        }
                    }
                }
            }

            if (setProperties){
                for(var itemName in labelsIndex){
                    itemLabelIndex = labelsIndex[itemName];

                    for(propertyName in itemLabelIndex.properties){
                        propertyValues = itemLabelIndex.properties[propertyName];
                        angular.forEach(propertyValues, function(propertyValue, index){
                            if (setPropertiesObj[propertyName].transform){
                                propertyValues[index] = transforms[setPropertiesObj[propertyName].transform.method](propertyValue, setPropertiesObj[propertyName].transform.options);
                            }
                        });
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