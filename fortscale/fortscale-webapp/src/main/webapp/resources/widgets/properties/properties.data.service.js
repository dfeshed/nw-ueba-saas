angular.module("PropertiesWidget").factory("propertiesWidgetData", ["utils", "format", "icons", "transforms", function(utils, format, icons, transforms){
    return {
        getData: function(view, data, params){
            var viewData = [];

            angular.forEach(data, function(item, itemIndex){
                var itemData = [];
                angular.forEach(view.settings.properties, function(property){
                    var itemDataObj;

                    if (property.array){
                        var arrayValues = [];
                        angular.forEach(item[property.array], function(member, memberIndex){
                            var memberValue = { value: utils.strings.parseValue(property.value, member, params, memberIndex) };
                            if (memberValue.value){
                                if (property.link)
                                    memberValue.link = utils.strings.parseValue(property.link, member, params, itemIndex);

                                arrayValues.push(memberValue);
                            }
                        });
                    }

                    if (property.value){
                        var itemValue = property.value && !arrayValues && utils.strings.parseValue(property.value, item, params, itemIndex);
                        if (itemValue || (arrayValues && arrayValues.length)){
                            itemDataObj = {
                                icon: icons.getIcon(property.icon),
                                tooltip: utils.strings.parseValue(property.tooltip, item, params, itemIndex)
                            };

                            if (arrayValues)
                                itemDataObj.list = arrayValues;
                            else{
                                itemDataObj.value = format.formatItem(property, itemValue);
                                if (property.transform){
                                    itemDataObj.value = transforms.transformValue(itemDataObj.value, property.transform);
                                }
                                if (property.link)
                                    itemDataObj.link = utils.strings.parseValue(property.link, item, params, itemIndex);
                            }
                            itemData.push(itemDataObj);
                        }
                    }
                    else if (property.list){
                        itemDataObj = {
                            icon: icons.getIcon(property.icon),
                            tooltip: utils.strings.parseValue(property.tooltip, item, params, itemIndex),
                            list: []
                        };

                        angular.forEach(property.list, function(listItem, listItemIndex){
                            var listItemValue = utils.strings.parseValue(listItem.value, item, params, listItemIndex),
                                listItemObj;

                            if (listItemValue){
                                if (listItem.transform)
                                    listItemValue = transforms.transformValue(listItemValue, listItem.transform);

                                if (listItem.link)
                                    listItemValue = "<a href='" + utils.strings.parseValue(listItem.link, item, params, listItemIndex) + "'>" + listItemValue + "</a>";

                                listItemObj = {
                                    value: (listItem.label ? "<strong>" + utils.strings.parseValue(listItem.label, item, params, listItemIndex) + "</strong> " : "") + listItemValue
                                };

                                if (listItem.className)
                                    listItemObj.className = listItem.className;

                                itemDataObj.list.push(listItemObj);
                            }
                        });

                        itemData.push(itemDataObj);
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