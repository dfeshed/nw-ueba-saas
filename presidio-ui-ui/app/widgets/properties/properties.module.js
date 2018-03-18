(function () {
    'use strict';

    angular.module("PropertiesWidget", ["Utils", "Format", "Icons", "Transforms", "Widgets"]).run(["$q", "utils",
        "format", "icons", "transforms", "menus", "widgetViews",
        function ($q, utils, format, icons, transforms, menus, widgetViews) {
            var defaultListItemDisplayCount = 5; // The default number of items to display initially in a list.

            function propertiesDataParser (view, data, params) {
                var viewData = [],
                    promises = [],
                    iconParserPromises = [],
                    iconParsers = {};

                angular.forEach(view.settings.properties, function (property, propertyIndex) {
                    if (property.icon) {
                        iconParserPromises.push(icons.getParseIconFunction(property.icon).then(function (iconParser) {
                            iconParsers[propertyIndex] = iconParser;
                        }));
                    }
                });

                function doGetData () {
                    angular.forEach(data, function (item, itemIndex) {
                        var itemData = [];
                        var arrayValues;
                        angular.forEach(view.settings.properties, function (property, propertyIndex) {
                            var itemDataObj = {},
                                i;

                            if (property.array) {
                                arrayValues = [];
                                angular.forEach(item[property.array], function (member, memberIndex) {
                                    var memberValue = {
                                        value: utils.strings.parseValue(property.value, member, params, memberIndex)
                                    };
                                    if (memberValue.value) {
                                        if (property.link) {
                                            memberValue.link =
                                                utils.strings.parseValue(property.link, member, params, itemIndex);
                                        }

                                        arrayValues.push(memberValue);
                                    }
                                });
                            }

                            if (property.menu) {
                                if (property.menu.id) {
                                    promises.push(menus.getMenu(property.menu, item, params).then(function (menu) {
                                        if (menu) {
                                            itemDataObj.menu = angular.extend(menu, property.menu);
                                        }
                                    }));
                                }
                                else {
                                    itemDataObj.menu = {
                                        items: []
                                    };
                                    property.menu.items.forEach(function (menuItem, i) {
                                        var itemCopy = angular.copy(menuItem);
                                        itemCopy.text = utils.strings.parseValue(menuItem.text, item, params, i);
                                        itemDataObj.menu.items.push(itemCopy);
                                    });
                                }
                            }

                            var icon = iconParsers[propertyIndex](property.icon);
                            if (property.value) {
                                var itemValue = property.value && !arrayValues &&
                                    utils.strings.parseValue(property.value, item, params, itemIndex);
                                if (itemValue || (arrayValues && arrayValues.length)) {
                                    angular.extend(itemDataObj, {
                                        icon: icon,
                                        tooltip: utils.strings.parseValue(property.tooltip, item, params, itemIndex)
                                    });

                                    if (arrayValues) {
                                        itemDataObj.list = arrayValues;
                                        if (arrayValues.length > defaultListItemDisplayCount) {
                                            itemDataObj.enableShowAll = true;

                                            for (i = 0; i < defaultListItemDisplayCount; i++) {
                                                itemDataObj.list[i].enabled = true;
                                            }
                                        }
                                    }
                                    else {
                                        itemDataObj.value = format.formatItem(property, itemValue);
                                        if (property.transform) {
                                            itemDataObj.value =
                                                transforms.transformValue(itemDataObj.value, property.transform);
                                        }
                                        if (property.link) {
                                            itemDataObj.link =
                                                utils.strings.parseValue(property.link, item, params, itemIndex);
                                        }
                                    }
                                    itemData.push(itemDataObj);
                                }
                            }
                            else if (property.list) {
                                angular.extend(itemDataObj, {
                                    icon: icon,
                                    tooltip: utils.strings.parseValue(property.tooltip, item, params, itemIndex),
                                    list: []
                                });

                                angular.forEach(property.list, function (listItem, listItemIndex) {
                                    var listItemValue = utils.strings.parseValue(listItem.value, item, params,
                                            listItemIndex),
                                        listItemObj;

                                    if (listItemValue) {
                                        if (listItem.transform) {
                                            listItemValue =
                                                transforms.transformValue(listItemValue, listItem.transform);
                                        }

                                        if (listItem.link) {
                                            listItemValue = "<a href='" +
                                                utils.strings.parseValue(listItem.link, item, params, listItemIndex) +
                                                "'>" + listItemValue + "</a>";
                                        }

                                        listItemObj = {
                                            value: (listItem.label ? "<strong>" +
                                            utils.strings.parseValue(listItem.label, item, params, listItemIndex) +
                                            "</strong> " : "") + listItemValue
                                        };

                                        if (listItem.className) {
                                            listItemObj.className = listItem.className;
                                        }

                                        itemDataObj.list.push(listItemObj);
                                    }
                                });

                                if (itemDataObj.list.length > defaultListItemDisplayCount) {
                                    itemDataObj.enableShowAll = true;
                                    for (i = 0; i < defaultListItemDisplayCount; i++) {
                                        itemDataObj.list[i].enabled = true;
                                    }
                                }

                                itemData.push(itemDataObj);
                            }
                            /*
                             boolean list - items in this list exist only if the appear in item(see below)
                             item - the data returned from REST.
                             reference: see user_detail.json

                             */
                            else if (property.booleanList) {
                                var listItems = [];

                                angular.forEach(property.booleanList, function (member) {
                                    if (item[member.show]) {
                                        var boolItem = {value: member.label};
                                        if (member.link) {
                                            boolItem.link =
                                                utils.strings.parseValue(member.link, item, params, itemIndex);
                                        }
                                        listItems.push(boolItem);
                                    }
                                });

                                if (listItems.length) {
                                    itemData.push({
                                        icon: icon,
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

                return $q.all(iconParserPromises).then(doGetData);
            }

            widgetViews.registerView("properties", {dataParser: propertiesDataParser});

        }]);
}());
