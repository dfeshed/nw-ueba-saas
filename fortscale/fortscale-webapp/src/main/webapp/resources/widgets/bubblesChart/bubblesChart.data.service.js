angular.module("BubblesChartWidget").factory("bubblesChartWidgetData", ["utils", function(utils){
    return {
        getData: function(view, data, params){
            var parsedData = [],
                itemsIndex = {};

            angular.forEach(data, function(item){
                var indexedItem = itemsIndex[item[view.settings.itemField]];
                if (!indexedItem)
                    indexedItem = itemsIndex[item[view.settings.itemField]] = { name: item[view.settings.itemField], members: [], value: 0 };

                var itemChildren = item[view.settings.childrenField];
                if (!~indexedItem.members.indexOf(itemChildren))
                    indexedItem.members.push(itemChildren);

                indexedItem.value += view.settings.valueIsCount ? 1 : item[view.settings.valueField];
            });

            var item;
            for(var itemName in itemsIndex){
                item = itemsIndex[itemName];
                item.valueNames = item.members.join("\n");
                parsedData.push(itemsIndex[itemName]);
            }

            return { children: parsedData };
        }
    }
}]);