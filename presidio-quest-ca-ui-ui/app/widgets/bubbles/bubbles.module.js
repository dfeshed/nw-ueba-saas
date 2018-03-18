(function () {
    'use strict';

    angular.module("BubblesWidget", ["Utils", "Chart", "Widgets"]).run(["utils", "widgetViews",
        function (utils, widgetViews) {

            /**
             *
             * @param {{settings: {noValueLabel, childIdField}}} view
             * @param data
             * @param params
             * @returns {{children: Array}}
             */
            function bubblesDataParser (view, data, params) {
                var parsedData = [],
                    itemsIndex = {},
                    membersIndex = {},
                    membersCount = 0;

                angular.forEach(data, function (item) {
                    var itemName = utils.strings.parseValue(view.settings.label, item, params) ||
                            view.settings.noValueLabel || "N/A",
                        indexedItem = itemsIndex[itemName];

                    if (!indexedItem) {
                        indexedItem = itemsIndex[itemName] = {name: itemName, membersIndex: {}, members: [], value: 0};

                        if (view.settings.highlight && indexedItem.name === view.settings.highlight) {
                            indexedItem.highlight = true;
                        }
                    }

                    if (view.settings.childIdField) {
                        var childId = item[view.settings.childIdField];

                        if (!indexedItem.membersIndex[childId]) {
                            indexedItem.membersIndex[childId] = item;

                            if (!membersIndex[childId]) {
                                membersIndex[childId] = true;
                                membersCount++;
                            }
                        }
                    } else {
                        membersCount += view.settings.value ? item[view.settings.value] : 0;
                    }

                    indexedItem.value += view.settings.value ? item[view.settings.value] : 1;
                });

                var item;
                for (var itemName in itemsIndex) {
                    if (itemsIndex.hasOwnProperty(itemName)) {
                        item = itemsIndex[itemName];
                        for (var childId in item.membersIndex) {
                            if (item.membersIndex.hasOwnProperty(childId)) {
                                item.members.push(item.membersIndex[childId]);
                            }
                        }

                        delete item.membersIndex;

                        parsedData.push(itemsIndex[itemName]);
                        item._percent = 100 * (item.members.length || item.value) / membersCount;
                    }
                }

                return {children: parsedData};
            }

            widgetViews.registerView("bubbles", {dataParser: bubblesDataParser});

        }]);
}());
