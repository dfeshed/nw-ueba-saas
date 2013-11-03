angular.module("PropertiesWidget").factory("propertiesWidgetData", ["utils", "format", "icons", function(utils, format, icons){
    return {
        getData: function(view, data, params){
            var viewData = [];

            angular.forEach(data, function(item, itemIndex){
                var itemData = [];
                angular.forEach(view.settings.properties, function(property){
                    if (property.array){
                        var arrayValues = [];
                        angular.forEach(item[property.array], function(member, memberIndex){
                            var memberValue = utils.strings.parseValue(property.value, member, params, memberIndex);
                            if (memberValue)
                                arrayValues.push(memberValue);
                        });
                    }

                    if (property.value){
                        var itemValue = arrayValues && arrayValues.length ? arrayValues.join(", ") : utils.strings.parseValue(property.value, item, params, itemIndex);
                        if (itemValue){
                            var itemDataObj = {
                                icon: icons.getIcon(property.icon),
                                tooltip: utils.strings.parseValue(property.tooltip, item, params, itemIndex),
                                value: format.formatItem(property, itemValue)
                            };

                            if (property.link)
                                itemDataObj.link = utils.strings.parseValue(property.link, item, params, itemIndex);

                            itemData.push(itemDataObj);
                        }
                    }
                    else if (property.booleanList){
                        var listItems = [];

                        angular.forEach(property.booleanList, function(member){
                            if (item[member.show])
                                listItems.push({ value: member.label });
                        });

                        if (listItems.length){
                            itemData.push({
                                icon: icons.getIcon(property.icon),
                                tooltip: utils.strings.parseValue(property.tooltip, item, params, itemIndex),
                                list: listItems
                            });
                        }
                    }
                });

                viewData.push(itemData);
            });

            return viewData;
        }
    };
}]);