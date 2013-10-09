angular.module("PropertiesWidget").factory("propertiesWidgetData", ["utils", "format", "icons", function(utils, format, icons){
    return {
        getData: function(view, data, params){
            var viewData = [];

            angular.forEach(data, function(item, itemIndex){
                var itemData = [];
                angular.forEach(view.settings.properties, function(property){
                    var itemValue = utils.strings.parseValue(property.value, item, params, itemIndex);
                    if (itemValue){
                        itemData.push({
                            icon: icons.getIcon(property.icon),
                            tooltip: utils.strings.parseValue(property.tooltip, item, params, itemIndex),
                            value: format.formatItem(property, itemValue)
                        });
                    }
                });

                viewData.push(itemData);
            });

            return viewData;
        }
    };
}]);