angular.module("TableWidget").factory("tableWidgetData", ["utils", "transforms", "styles", "icons", function(utils, transforms, styles, icons){
    var methods = {
        getData: function(view, data, params){
            var viewData = { rows: [] },
                fieldSpans = {};

            if (view.settings.useFirstRowAsMessage && data.length && data[0].message){
                viewData.message = {
                    type: view.settings.messageType || "info",
                    icon: view.settings.messageType === "warning" ? "warning-sign" : null,
                    value: data.splice(0, 1)[0].message
                };
            }

            function getField(row, rowIndex, field, item){
                var fieldData = {
                    display: field.value && utils.strings.parseValue(field.value, row, params, rowIndex) || "",
                    field: field
                };

                if (field.transform)
                    fieldData.display = transforms[field.transform.method](field.field ? row[field.field] : fieldData.display, field.transform.options);

                if (field.link)
                    fieldData.link = utils.strings.parseValue(field.link, row, params, rowIndex);

                if (field.style){
                    styles.getStyle(field, row).then(function(style){
                        fieldData.style = style;
                    }, function(error){
                        console.error("Can't set style to row: ", error);
                    });
                }

                if (field.map){
                    var mapValue = field.map[fieldData.display];
                    if (mapValue)
                        fieldData.display = mapValue;
                }

                if (field.icon){
                    icons.getIcon(field.icon, row).then(function(icon){
                        fieldData.icon = icon;
                    });
                }

                if (field.className)
                    fieldData.className = utils.strings.parseValue(field.className, row, params, rowIndex);

                if (field.valueTooltip){
                    if (angular.isString(field.valueTooltip))
                        fieldData.tooltip = utils.strings.parseValue(field.valueTooltip, row, params, rowIndex);
                    else if (field.valueTooltip.transform)
                        fieldData.tooltip = transforms[field.valueTooltip.transform.method](row[field.valueTooltip.field], field.valueTooltip.transform.options);
                }

                if (field.events)
                    fieldData.id = field.name.replace(/\s/g, "_");

                return fieldData;
            }

            function getRow(row, rowIndex){
                var rowData = { display: [] };

                if (view.settings.rows){
                    if (view.settings.rows.style){
                        styles.getStyle(view.settings.rows, row).then(function(style){
                            rowData.style = style;
                        });
                    }
                }

                angular.forEach(view.settings.fields, function(field, fieldIndex){
                    var fieldData;

                    if (field.collection){
                        fieldData = { items: [], type: "array" };

                        if (angular.isArray(row[field.collection])){
                            angular.forEach(row[field.collection], function(item){
                                fieldData.items.push(getField(row, rowIndex, field.item, item));
                            });
                        }
                        else{
                            fieldData.items.push(getField(row, rowIndex, field.item, row[field.collection]));
                        }
                    }
                    else{
                        fieldData = getField(row, rowIndex, field);
                    }

                    if (field.spanRowsIfEqual){
                        var fieldSpan = fieldSpans[String(fieldIndex)];
                        if (fieldSpan === undefined){
                            fieldData.rowSpan = 1;
                            fieldSpans[String(fieldIndex)] = fieldData;
                            rowData.display.push(fieldData);
                        }
                        else{
                            if (fieldSpan.display === fieldData.display){
                                fieldSpan.rowSpan++;
                            }
                            else{
                                fieldData.rowSpan = 1;
                                fieldSpans[String(fieldIndex)] = fieldData;
                                rowData.display.push(fieldData);
                            }
                        }
                    }
                    else
                        rowData.display.push(fieldData);
                });

                return rowData;
            }

            angular.forEach(data, function(row, rowIndex){
                viewData.rows.push(getRow(row, rowIndex + 1));
            });

            return viewData;
        }
    };
    
    return methods;
}]);