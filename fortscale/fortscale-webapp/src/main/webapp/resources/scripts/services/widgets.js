angular.module("Fortscale").factory("widgets", [
    "$q", "DAL", "conditions", "format", "transforms", "styles", "icons", "reports", "widgetsData",
    function($q, DAL, conditions, format, transforms, styles, icons, reports, widgetsData){
    var typeValueGenerators = {
        date: function(options, params){
            var values = [],
                startValue = /^params\./.test(options.startAt) ? params[options.startAt.replace(/^params\./, "")] : options.startAt,
                endValue = /^params\./.test(options.endtAt) ? params[options.endAt.replace(/^params\./, "")] : options.endAt,
                currentDate = moment(startValue),
                lastDate = moment(endValue);

            while(currentDate <= lastDate){
                values.push(options.format ? format.date(currentDate, { format: options.format }) : currentDate.toString());
                currentDate.add(options.interval.unit, options.interval.value);
            }

            return values;
        }
    };

    var cachedWidgets = {};

    typeValueGenerators.datetime = typeValueGenerators.date;

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
        barChart: widgetsData.barsChart,
        percentChart: widgetsData.percentChart,
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
        pieChart: widgetsData.pieChart,
        properties: function(view, data, params){
            var viewData = [];

            angular.forEach(data, function(item, itemIndex){
                var itemData = [];
                angular.forEach(view.settings.properties, function(property){
                    var itemValue = parseFieldValue(property, property.value, item, itemIndex, params);
                    if (itemValue){
                        itemData.push({
                            icon: icons.getIcon(property.icon),
                            tooltip: parseFieldValue(property, property.tooltip, item, itemIndex, params),
                            value: format.formatItem(property, itemValue)
                        });
                    }
                });

                viewData.push(itemData);
            });

            return viewData;
        },
        table: widgetsData.table,
        tabs: function(view, data, params){
            var viewData = [];

            angular.forEach(data, function(item, itemIndex){
                var itemData = {};
                itemData.display = parseFieldValue(view.settings.tab, view.settings.tab.display, item, itemIndex, params);
                itemData.id = parseFieldValue(view.settings.tab, view.settings.tab.id, item, itemIndex, params);

                if (view.settings.tab.style){
                    styles.getStyle(view.settings.tab, item).then(function(style){
                        itemData.style = style;
                    });
                }

                if (view.settings.label){
                    itemData.label = {
                        value: parseFieldValue(view.settings.label, view.settings.label.value, item, itemIndex, params)
                    };

                    if (view.settings.label.style){
                        styles.getStyle(view.settings.label, item).then(function(style){
                            itemData.label.style = style;
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
        },
        lineGraph: function(view, data, params, rawData){
            var xFieldParser = angular.isObject(view.settings.axes.x) && ~["date", "datetime"].indexOf(view.settings.axes.x.values.type)
                ? function(value){  return new Date(value.replace(/-/g, "/")); }
                : function(value){ return value; };

            if (view.settings.useRawData){
                angular.forEach(rawData.rows, function(row){
                    angular.forEach(row, function(field, fieldIndex){
                        if (fieldIndex){
                            if (field !== null)
                                row[fieldIndex] = parseFloat(field, 10);
                        }
                        else{
                            row[fieldIndex] = xFieldParser(field);
                        }
                    })
                });

                var graphData = rawData.rows;
                graphData.splice(0, 0, rawData.fields);

                if (view.settings.labelsMap){
                    angular.forEach(graphData[0], function(field, fieldIndex){
                        for(var label in view.settings.labelsMap){
                            if (field === label)
                                graphData[0][fieldIndex] = view.settings.labelsMap[label];
                        }
                    });
                }

                setDateExtremeties();
                return graphData;
            }

            var graphData = [[]],
                labelsAreSet = angular.isArray(view.settings.labels),
                labels = labelsAreSet ? view.settings.labels : undefined,
                xValueIndexes = {},
                labelIndexes = {},
                lineCount = 0,
                floatDecimals = view.settings.decimals,
                xField,
                xValues,
                xTransform,
                typeValueGenerator,
                xFormat,
                defaultValue = view.settings.defaultValue || null;

            function addXValue(value){
                var valueIndex = xValueIndexes[value] = graphData.length;
                graphData.push([value]);
                return valueIndex;
            }

            // Google charts can't handle nulls for the first and last items in a graph.
            // Therefore, I'm adding the first and last times, all with zeroes.
            function setDateExtremeties(){
                if (!params.timeStart || !params.timeEnd)
                    return;

                var xFirstValue = transforms.getDate(params.timeStart).toDate(),
                    xLastValue = transforms.getDate(params.timeEnd).toDate(),
                    columnCount = graphData[0].length;

                if (xLastValue > new Date())
                    xLastValue = new Date();

                if (graphData[1][0] > xFirstValue){
                    var firstRow = [xFirstValue],
                        xSecondValue = graphData[1][0];

                    xSecondValue.setSeconds(xSecondValue.getSeconds() - 1);

                    var secondRow = [xSecondValue];

                    for(var i=1; i < columnCount; i++){
                        firstRow[i] = 0;
                        secondRow[i] = 0;
                    }

                    graphData.splice(1,0,firstRow);
                    graphData.splice(2, 0, secondRow);
                }

                var currentLastValue = graphData[graphData.length - 1][0];
                if (currentLastValue < xLastValue){
                    var lastRow = [xLastValue],
                        xSecondToLastValue = currentLastValue;

                    xSecondToLastValue.setSeconds(xSecondToLastValue.getSeconds() + 1);

                    var secondToLastRow = [xSecondToLastValue];

                    for(var i=1; i < columnCount; i++){
                        lastRow[i] = 0;
                        secondToLastRow[i] = 0;
                    }

                    graphData.push(secondToLastRow);
                    graphData.push(lastRow);
                }
            }

            if (angular.isObject(view.settings.axes.x)){
                xField = view.settings.axes.x.field;
                xValues = view.settings.axes.x.values;
                xFormat = function(value){ return format[xValues.type](value, { format: xValues.format }) };
                xTransform = xValues.transform && xValues.transform.regExp
                    ? function(value){ return transforms.string(value, xValues.transform); }
                    : null;
            }
            else
                xField = view.settings.axes.x;

            if (labelsAreSet)
                graphData[0] = view.settings.labels;
            else
                graphData[0][0] = xField;

            if (xValues){
                if (angular.isObject(xValues)){
                    typeValueGenerator = typeValueGenerators[xValues.type];
                    if (!typeValueGenerator)
                        throw new Error("Invalid type for graph values.");

                    var values = typeValueGenerator(xValues, params);
                    for(var i=0; i < values.length; i++){
                        addXValue(values[i]);
                    }
                }
            }

            angular.forEach(data, function(row){
                var xValue = row[xField],
                    labelIndex;

                if (xTransform)
                    xValue = xTransform(xValue);

                if (xFormat)
                    xValue = xFormat(xValue);

                var xValueIndex = xValueIndexes[xValue];
                if (!labelsAreSet){
                    var labelValue = String(row[view.settings.labels]);
                    labelIndex = labelIndexes[labelValue];

                    if (!labelIndex){
                        labelIndex = labelIndexes[labelValue] = graphData[0].length;
                        if (!labelsAreSet)
                            graphData[0].push(labelValue);
                        lineCount++;

                        var currentRowPosition;
                        for(var graphDataIndex = 1, graphDataRow; graphDataRow = graphData[graphDataIndex]; graphDataIndex++){
                            for(currentRowPosition = graphDataRow.length; currentRowPosition <= lineCount; currentRowPosition++){
                                graphDataRow[currentRowPosition] = defaultValue;
                            }
                        }
                    }
                }
                else
                    labelIndex = 1;

                if (!xValueIndex){
                    xValueIndex = addXValue(xFieldParser(xValue));
                }

                var yValue = parseFloat(row[view.settings.axes.y], 10);
                if (floatDecimals)
                    yValue = roundFloat(yValue, floatDecimals);

                graphData[xValueIndex][labelIndex] = yValue;
            });

            setDateExtremeties();
            return graphData;
        }
    };

    function roundFloat(num, decimals){
        return Math.round(num*Math.pow(10,decimals))/Math.pow(10,decimals);
    }

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