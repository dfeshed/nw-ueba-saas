angular.module("LineChartWidget").factory("lineChartWidgetData", ["transforms", "format", function(transforms, format){
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

    typeValueGenerators.datetime = typeValueGenerators.date;

    function roundFloat(num, decimals){
        return Math.round(num*Math.pow(10,decimals))/Math.pow(10,decimals);
    }

    return {
        getData: function(view, data, params, rawData){
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
}]);