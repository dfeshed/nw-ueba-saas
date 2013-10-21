angular.module("Fortscale").factory("widgets", [
    "$q", "DAL", "conditions", "format", "transforms", "styles", "icons", "reports", "widgetsData",
    function($q, DAL, conditions, format, transforms, styles, icons, reports, widgetsData){

    var cachedWidgets = {};

    function parseFieldValue(field, value, data, index, params, item){
        var parsedValue = value.replace(/\{\{([^\}]+)\}\}/g, function(match, variable){
            if (/^@/.test(variable)){
                var param = variable.replace("@", "");
                if (param === "index")
                    return index;
                else if (param === "item")
                    return item;

                return params[param] || "";
            }
            else{
                var dataValue = data[variable];
                if (dataValue !== undefined && dataValue !== null){
                    if (field.format)
                        return format[field.format](dataValue, field.formatOptions);

                    return dataValue;
                }

                return "";
            }
        });

        return parsedValue;
    }

    function getValueFromObject(path, object){
        var pathParts = angular.isString(path) ? path.split(" -> ") : path,
            value = object[pathParts[0]];

        if (pathParts.length){
            pathParts.splice(0, 1);
            return getValueFromObject(pathParts, value);
        }

        return value;
    }

    /*
    function getIcon(iconSettings, data){
        var deferred = $q.defer(),
            icon = {
                type: iconSettings
            };

        if (Object(iconSettings) === iconSettings){
            if (iconSettings.style){
                styles.getStyle(iconSettings, data).then(function(iconStyle){
                    icon.style = iconStyle;
                    deferred.resolve(icon);
                });
            }
            else
                deferred.resolve(icon);

            icon.type = parseFieldValue(iconSettings, iconSettings.type, data, 0, {});
            if (iconSettings.typeMap && iconSettings.typeMap[icon.type])
                icon.type = iconSettings.typeMap[icon.type];
        }
        else
            deferred.resolve(icon);

        return deferred.promise;
    }
*/
    function chartSetData(view, data, params){
        var deferred = $q.defer(),
            styleDeferreds = [],
            styleDeferredsMapping = {};

        var viewData = { chartValues: data };
        angular.forEach(view.settings.series, function(series, i){
            series.label = parseFieldValue(series, series.label, data, i, params);

            if (series.style){
                styleDeferredsMapping[String(styleDeferreds.length)] = i;
                styleDeferreds.push(styles.getParseStyleFunction(series));
            }
        });

        angular.forEach(viewData.chartValues, function(item, itemIndex){
            item._label = parseFieldValue(view.settings.labels, view.settings.labels.value, item, itemIndex, params);
        });

        if (styleDeferreds.length){
            $q.all(styleDeferreds).then(function(styleParsers){
                var colorSeries = [];

                for(var styleDeferredIndex in styleDeferredsMapping){
                    colorSeries.push({
                        series: view.settings.series[styleDeferredsMapping[styleDeferredIndex]],
                        styleParser: styleParsers[parseInt(styleDeferredIndex, 10)]
                    });
                }

                angular.forEach(viewData.chartValues, function(item, itemIndex){
                    item._style = {};
                    angular.forEach(colorSeries, function(colorSeriesItem){
                        item._style[colorSeriesItem.series.field] = colorSeriesItem.styleParser(item);
                    })
                });

                deferred.resolve(viewData);
            });
        }
        else
            deferred.resolve(viewData);

        return deferred.promise;
    }

    var viewTypeSetData = {
        button: function(view, data, params){
            return {
                text: parseFieldValue(view.settings, view.settings.text, data, 0, params)
            };
        },
        buttonsBar: function(view, data, params){
            var viewData = [];

            function getButton(button, buttonIndex){
                var buttonData = angular.copy(button);

                buttonData.text = parseFieldValue(button, button.text, data, buttonIndex, params);

                if (button.link)
                    buttonData.link = parseFieldValue(button, button.link, data, buttonIndex, params);

                if (button.style){
                    styles.getStyle(button, button).then(function(buttonStyle){
                        buttonData.style = buttonStyle;
                    })
                }
                if (button.tooltip)
                    buttonData.tooltip = parseFieldValue(button, button.tooltip, data, buttonIndex, params);

                return buttonData;
            }

            angular.forEach(view.settings.buttons, function(button, buttonIndex){
                viewData.push(getButton(button, buttonIndex + 1));
            });

            return viewData;
        },
        checkbox: function(view, data, params){
            var viewData = false;

            console.log("check: ", view, data, params);

            return viewData;
        },
        list: function(view, data, params){
            var viewData = [];

            angular.forEach(data, function(item, itemIndex){
                var itemData = {
                    value: parseFieldValue(view.settings.item, view.settings.item.value, item, itemIndex, params)
                };
                viewData.push(itemData);

                if (view.settings.item.icon){
                    icons.getIcon(view.settings.item.icon, item).then(function(icon){
                        itemData.icon = icon;
                    });
                }

                if (view.settings.item.sideNote)
                    itemData.sideNote = parseFieldValue(view.settings.item, view.settings.item.sideNote, item, itemIndex, params);

                if (view.settings.item.link)
                    itemData.link = parseFieldValue(view.settings.item, view.settings.item.link, item, itemIndex, params);
            });

            return viewData;
        },
        numeric: function(view, data, params){
            var viewData = [];

            angular.forEach(data, function(item, itemIndex){
                var itemData = {};

                if (view.settings.title)
                    itemData.title = parseFieldValue(view.settings, view.settings.title, item, itemIndex, params);

                if (view.settings.description)
                    itemData.description = parseFieldValue(view.settings, view.settings.description, item, itemIndex, params);

                if (view.settings.tooltip)
                    itemData.tooltip = parseFieldValue(view.settings, view.settings.tooltip, item, itemIndex, params);

                itemData.value = {
                    display: parseFieldValue(view.settings.value, view.settings.value.display, item, itemIndex, params)
                };

                if (view.settings.value.style){
                    styles.getStyle(view.settings.value, item).then(function(valueStyle){
                        itemData.value.style = valueStyle;
                    });
                }

                if (view.settings.secondaryValue){
                    itemData.secondaryValue = {
                        value: parseFieldValue(view.settings.secondaryValue, view.settings.secondaryValue.display, item, itemIndex, params)
                    };

                    if (view.settings.secondaryValue.icon){
                        icons.getIcon(view.settings.secondaryValue.icon, item).then(function(icon){
                            itemData.secondaryValue.icon = icon;
                        });
                    }

                    if (view.settings.secondaryValue.tagLine)
                        itemData.secondaryValue.tagLine = parseFieldValue(view.settings.secondaryValue, view.settings.secondaryValue.tagLine, item, itemIndex, params);

                    if (view.settings.secondaryValue.style){
                        styles.getStyle(view.settings.secondaryValue, item).then(function(secondaryStyle){
                            itemData.secondaryValue.style = secondaryStyle;
                        });
                    }
                }

                viewData.push(itemData);
            });

            return viewData;
        },
        text: function(view, data, params){
            var viewData = [];

            angular.forEach(data, function(row){
                var item = {
                    label: parseFieldValue(view.settings, view.settings.label, row, 0, params),
                    display: parseFieldValue(view.settings, view.settings.value, row, 0, params)
                };

                viewData.push(item);

                if (view.settings.style){
                    styles.getStyle(view.settings, data).then(function(textStyle){
                        item.style = textStyle;
                    })
                }
            });

            return viewData;
        }
    };

    angular.extend(viewTypeSetData, widgetsData);

    var methods = {
        checkRequiredParams: function(widget, params){
            if (angular.isArray(widget.requiredParams))
                widget.requiredParams = { all: widget.requiredParams };

            if (widget.requiredParams.all){
                for(var i= 0, paramName; paramName = widget.requiredParams.all[i]; i++){
                    if (params[paramName] === undefined || params[paramName] === null)
                        return false;
                }
            }

            if (widget.requiredParams.any){
                for(var i= 0; paramName = widget.requiredParams.any[i]; i++){
                    if (params[paramName] !== undefined && params[paramName] !== null)
                        return true;
                }

                return false;
            }

            return true;
        },
        getDashboardWidgets: function(dashboardId){
            var deferred = $q.defer();

            DAL.widgets.getDashboardWidgets(dashboardId)
                .success(function(data){
                    deferred.resolve(data);
                })
                .error(deferred.reject);

            return deferred.promise;
        },
        getWidget: function(widgetId){
            var deferred = $q.defer();

            if (cachedWidgets[widgetId])
                deferred.resolve(cachedWidgets[widgetId]);
            else{
                DAL.widgets.getWidget(widgetId).then(function(widget){
                    if (widget.reportId){
                        reports.getReport(widget.reportId).then(function(report){
                            widget.report = { query: report };
                            deferred.resolve(widget);
                        }, function(error){
                            console.error("Can't get report with ID '%s' for widget: ", widget.reportId, widget, ", original error: ", error);
                            deferred.reject(error);
                        })
                    }
                    else
                        deferred.resolve(widget);

                    cachedWidgets[widgetId] = widget;
                }, deferred.reject);
            }

            return deferred.promise;
        },
        parseFieldValue: parseFieldValue,
        setViewValues: function(view, data, params, rawData){
            var setData = viewTypeSetData[view.type];
            if (!setData)
                return data;

            return setData(view, data, params, rawData);
        },
        /**
         * Returns true whether a widget uses any of the specified params in its report.
         * @param widget
         * @param params [Object] an object with params
         */
        widgetContainsParams: function(widget, params){
            if (widget.updateFor){
                for(var i = 0, param; param = widget.updateFor[i]; i++){
                    if (params[param] !== undefined)
                        return true;
                }
            }

            var widgetParams = widget.report && widget.report.query && widget.report.query.params;
            if (!widgetParams)
                return false;

            for(i= 0; param = widgetParams[i]; i++){
                if (params[param.dashboardParam] !== undefined)
                    return true;
            }

            return false;
        }
    };

    return methods;
}]);