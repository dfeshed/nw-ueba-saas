(function () {
    'use strict';


    function tableWidget ($q, utils, transforms, icons, conditions, menus, tags, widgetViews,
                          TableWidgetConfig) {

        function tableConfigValidate (settings) {
            var tableConfig = new TableWidgetConfig(settings);
            return !!tableConfig;
        }

        function tableDataParser (view, data, params) {
            function getTableData () {
                var viewData = {rows: []},
                    fieldSpans = {},
                    loadPromises = [],
                    iconParsers = {},
                    menuIds = {};

                angular.forEach(tableConfig.fields, function (field, fieldIndex) {
                    field.__index = fieldIndex;
                    if (field.icon) {
                        loadPromises.push(icons.getParseIconFunction(field.icon).then(function (iconParser) {
                            iconParsers[fieldIndex] = iconParser;
                        }));
                    }

                    if (field.menu && field.menu.id) {
                        menuIds[field.menu.id] = true;
                        loadPromises.push(menus.getMenuParser(field.menu).then(function (menuParser) {
                            field.getMenu = menuParser;
                        }));
                    }

                    loadPromises.push(menus.initMenus(Object.keys(menuIds)));
                });

                if (tableConfig.caption) {
                    viewData.caption = utils.strings.parseValue(tableConfig.caption, data, params);
                }

                function getField (row, rowIndex, field) {
                    var fieldData = {
                        display: field.value && utils.strings.parseValue(field.value, row, params, rowIndex) || "",
                        field: field
                    };

                    if (!fieldData.display && field.field) {
                        fieldData.display = row[field.field];
                    }
                    if (field.transform && field.transform.method) {
                        fieldData.display =
                            transforms[field.transform.method](field.field ? row[field.field] : fieldData.display,
                                field.transform.options);
                    }

                    if (field.externalLinks) {
                        fieldData.externalLinks = field.externalLinks;
                    }
                    if (fieldData.display !== null && typeof(fieldData.display) !== "string") {
                        fieldData.display = String(fieldData.display);
                    }

                    if (field.link) {
                        fieldData.link = utils.strings.parseValue(field.link, row, params, rowIndex);
                    }

                    if (field.valueTooltip) {
                        if (angular.isString(field.valueTooltip)) {
                            fieldData.tooltip = utils.strings.parseValue(field.valueTooltip, row, params, rowIndex);
                        } else if (field.valueTooltip.transform) {
                            fieldData.tooltip =
                                transforms[field.valueTooltip.transform.method](row[field.valueTooltip.field],
                                    field.valueTooltip.transform.options);
                        }
                    }

                    if (fieldData.display) {
                        if (field.map) {
                            var mapValue = field.map[fieldData.display] || field.map._default;
                            if (mapValue) {
                                fieldData.display = utils.strings.parseValue(mapValue, row, params, rowIndex);
                            }
                        }
                    }
                    else if (field.noValueDisplay) {
                        fieldData.display = field.noValueDisplay;
                        fieldData.noValue = true;
                    }
                    else if (fieldData.display === "") {
                        fieldData.display = "N/A";
                        fieldData.noValue = true;
                    }

                    if (field.icon) {
                        fieldData.icon = iconParsers[field.__index](row);
                    }

                    if (field.renderHeader === false) {
                        fieldData.renderHeader = false;
                    }

                    if (field.events) {
                        fieldData.id = field.name.replace(/\s/g, "_");
                    }

                    if (field.switch) {
                        for (var i = 0, switchItem; !!(switchItem = field.switch[i]); i++) {
                            if (!switchItem.conditions ||
                                conditions.validateConditions(switchItem.conditions, row, params)) {
                                angular.extend(fieldData, getField(row, rowIndex, switchItem.field));
                                break;
                            }
                        }
                    }

                    if (field.tags) {
                        fieldData.tags = tags.getTagsSync(field.tags, row);
                    }

                    if (field.menu) {
                        if (field.getMenu && (!fieldData.noValue || field.menu.renderForEmptyCell)) {
                            fieldData.menu = field.getMenu(row, params);
                        }
                        else if (!fieldData.noValue) {
                            fieldData.menu = menus.getMenu(field.menu, row, params);
                        }
                    }

                    if (field.externalLinks) {
                        fieldData.externalLinks = angular.copy(field.externalLinks);
                        angular.forEach(fieldData.externalLinks, function (externalLink) {
                            if (externalLink.pinConditions) {
                                if (conditions.validateConditions(externalLink.pinConditions, row, params)) {
                                    externalLink.pinned = true;
                                    if (externalLink.pinnedTooltip) {
                                        externalLink.currentTooltip = externalLink.pinnedTooltip;
                                    }
                                }
                            }

                            if (externalLink.url) {
                                externalLink.href = utils.strings.parseValue(externalLink.url, row, params);
                            }

                            if (externalLink.tooltip) {
                                externalLink.tooltip = utils.strings.parseValue(externalLink.tooltip, row, params);
                            }
                        });
                    }
                    return fieldData;
                }

                function getRow (row, rowIndex) {
                    var rowData = {display: []};

                    angular.forEach(tableConfig.fields, function (field, fieldIndex) {
                        var fieldData = getField(row, rowIndex, field);

                        if (field.spanRowsIfEqual) {
                            var fieldSpan = fieldSpans[String(fieldIndex)];
                            if (fieldSpan === undefined) {
                                fieldData.rowSpan = 1;
                                fieldSpans[String(fieldIndex)] = fieldData;
                                rowData.display.push(fieldData);
                            }
                            else {
                                if (fieldSpan.display === fieldData.display) {
                                    fieldSpan.rowSpan++;
                                }
                                else {
                                    fieldData.rowSpan = 1;
                                    fieldSpans[String(fieldIndex)] = fieldData;
                                    rowData.display.push(fieldData);
                                }
                            }
                        }
                        else {
                            rowData.display.push(fieldData);
                        }
                    });

                    return rowData;
                }

                function doGetData () {
                    if (tableConfig.dataField) {
                        if (angular.isArray(data)) {
                            angular.forEach(data, function (item, itemIndex) {
                                angular.forEach(item[tableConfig.dataField], function (row, rowIndex) {
                                    viewData.rows.push(getRow(row, itemIndex + rowIndex + 1));
                                });
                            });
                        }
                        else {
                            angular.forEach(data[tableConfig.dataField], function (item, itemIndex) {
                                viewData.rows.push(getRow(item, itemIndex + 1));
                            });
                        }
                    }
                    else {
                        angular.forEach(data, function (item, itemIndex) {
                            viewData.rows.push(getRow(item, itemIndex + 1));
                        });
                    }

                    return viewData;
                }

                return $q.all(loadPromises).then(doGetData);
            }

            var tableConfig = new TableWidgetConfig(view.settings);

            return tags.initTags().then(getTableData);

        }

        widgetViews.registerView("table", {dataParser: tableDataParser, validate: tableConfigValidate});

    }

    tableWidget.$inject = ["$q", "utils", "transforms", "icons", "conditions", "menus", "tags", "widgetViews",
        "TableWidgetConfig"];

    angular.module("TableWidget", ["Utils", "Transforms", "Styles", "Icons", "Conditions", "Widgets"]).run(tableWidget);

})();
