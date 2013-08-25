'use strict';

if(typeof(yoxigen) === "undefined")
    yoxigen = angular.module("Yoxigen", []);

yoxigen.directive("yoxigenBarChart", ["$parse", function($parse){
    return {
        template: "<div class='yoxigen-chart yoxigen-bar-chart' style='width: 100%; height: 100%'></div>",
        restrict: 'E',
        scope: true,
        replace: true,
        require: "?ngModel",
        link: function postLink(scope, element, attrs, ngModel) {
            var data, settings;
            var resizeEventListenerEnabled;
            var selectedBarGroup = null;
            var labelTexts, labelBoxes;
            var selectItem;

            var defaultOptions = {
                    spacing: { min: 0, max: 100 },
                    padding: { top: 10, right: 50, left: 50, bottom: 0 },
                    selectable: false,
                    values: {
                        min: 0,
                        max: 100
                    },
                    barLabels: {
                        height: 30,
                        margin: 11
                    },
                    barLabelsFont: {
                        color: "#000000",
                        size: 14,
                        weight: "bold",
                        family: "sans-serif"
                    },
                    borderWidth: 1,
                    labels: {
                        height: 32,
                        margin: 12,
                        handleEvents: true
                    },
                    labelsFont: {
                        color: "#000000",
                        size: "14px",
                        weight: "bold",
                        family: "sans-serif"
                    },
                    selectionBar: {
                        barHeight: 5,
                        arrowHeight: 10,
                        arrowWidth: 21,
                        margin: 8,
                        defaultColor: "#333"
                    },
                    gridSize: 13,
                    barsHandleEvents: true,
                    colors: ["#0b62a4", "#7a92a3"],
                    minHeight: 100,
                    createAxes: true,
                    createLabels: true,
                    showSelectionBar: false,
                    refreshOnResize: true
                },
                options;

            scope.$watch(attrs.ngModel, function(chartData){
                data = chartData;
                drawChart();
            });

            scope.$watch(attrs.settings, function(value){
                settings = value;
                options = angular.extend({}, defaultOptions, settings.options);

                if (settings.selectedIndex !== undefined)
                    selectedBarGroup = settings.selectedIndex;

                drawChart();

                if (options.refreshOnResize && !resizeEventListenerEnabled)
                    window.addEventListener("resize", drawChart);
                else if (!options.refreshOnResize && resizeEventListenerEnabled)
                    window.removeEventListener("resize", drawChart);

                if (settings.events){
                    angular.forEach(settings.events, function(eventSettings){
                        element.on(eventSettings.eventName, ".handle-events", function(e){
                            scope.$apply(function(){
                                scope.$emit("widgetEvent", { event: eventSettings, data: e.currentTarget.__data__, widget: scope.widget });
                            });
                        })
                    });
                }

                if(options.selectable){
                    element.on("click", ".handle-events", function(e){
                        var targetData = e.currentTarget.__data__,
                            targetIndex = data.indexOf(targetData);

                        if (targetIndex !== selectedBarGroup)
                            selectItem && selectItem(targetData, targetIndex);
                    });
                }
            });

            function getMinValue(){
                return d3.min(data, function(d){
                    var fields = [];
                    angular.forEach(settings.series, function(series){
                        fields.push(d[series.field]);
                    });

                    return d3.min(fields);
                })
            }
            function getMaxValue(){
                return d3.max(data, function(d){
                    var fields = [];
                    angular.forEach(settings.series, function(series){
                        fields.push(d[series.field]);
                    });

                    return d3.max(fields);
                })
            }

            function setLabelFill(labelData, labelIndex){
                if (labelIndex === selectedBarGroup) return "White";
                var color = labelData._style && labelData._style[settings.labels.styleField] && labelData._style[settings.labels.styleField].color;
                return color || options.labelsFont.color;
            }

            function getSelectionBarColor(itemData){
                var color = itemData._style && itemData._style[settings.selectionBar.styleField] && itemData._style[settings.selectionBar.styleField].color;
                return color || options.selectable.defaultColor;
            }

            function drawChart(){
                if (!data || !settings)
                    return;

                element[0].innerHTML = "";
                element[0].style.width = options.width;
                element[0].style.height = options.height;

                var scale = d3.scale.linear()
                    .domain([options.values.min, options.values.max]);

                var patterns = {};

                var width = element.width(),
                    height = element.height(),
                    gridSize = options.gridSize,
                    leftPadding = options.padding.left,
                    rightPadding = options.padding.right,
                    selectionBarTotalHeight = options.showSelectionBar ? options.selectionBar.barHeight + options.selectionBar.arrowHeight + options.selectionBar.margin : 0,
                    bottomReservedSpace = options.padding.bottom + (options.createLabels ? options.labels.height + options.labels.margin : 0) + selectionBarTotalHeight,
                    barsSpacing = (width  / data.length) / (data.length - 1),
                    barsArea = { width: width - 2 * options.borderWidth, height: height - bottomReservedSpace - options.borderWidth * 2 },
                    heightGridRemainder = barsArea.height % gridSize,
                    widthGridRemainder = barsArea.width % gridSize,
                    selectionBar,
                    selectionBarArrow;

                if (heightGridRemainder){
                    barsArea.height += (gridSize - heightGridRemainder);
                    bottomReservedSpace -= heightGridRemainder;
                }
                if (widthGridRemainder){
                    barsArea.width -= widthGridRemainder;
                    leftPadding += Math.floor(widthGridRemainder / 2);
                    rightPadding += Math.floor(widthGridRemainder / 2);
                }

                barsSpacing = Math.max(barsSpacing, options.spacing.min);
                barsSpacing = Math.min(barsSpacing, options.spacing.max);

                scale.range([10, barsArea.height - options.barLabels.height - options.barLabels.margin]);

                var svg = d3.select(element[0])
                    .append("svg")
                    .attr("height", "100%");

                var defs = svg.append('svg:defs');

                var totalRectWidth = (width - leftPadding - rightPadding + barsSpacing) / data.length,
                    rectWidth = (totalRectWidth - barsSpacing) / settings.series.length,
                    barRemainder = rectWidth % gridSize;

                selectItem = function(itemData, itemIndex){
                    var color = getSelectionBarColor(itemData),
                        previousSelectedIndex = selectedBarGroup;

                    d3.select(labelBoxes[0][selectedBarGroup]).classed("selected", false);
                    selectedBarGroup = itemIndex;
                    d3.select(labelTexts[0][selectedBarGroup]).attr("fill", setLabelFill(itemData, selectedBarGroup));
                    d3.select(labelTexts[0][previousSelectedIndex]).attr("fill", setLabelFill(data[previousSelectedIndex], previousSelectedIndex));
                    d3.select(labelBoxes[0][selectedBarGroup]).classed("selected", true);

                    selectionBar.attr("fill", color);
                    selectionBarArrow.attr("fill", color);
                    moveSelectionBarArrowTo(itemIndex * totalRectWidth + leftPadding + (totalRectWidth - barsSpacing) / 2)
                };

                if (barRemainder){
                    var halfGrid = Math.floor(gridSize / 2);
                    if (barRemainder < halfGrid)
                        rectWidth -= barRemainder;
                    else
                        rectWidth += gridSize - barRemainder;
                }

                var xScale = d3.scale.linear()
                    .domain([0, data.length])
                    .range([0, totalRectWidth * data.length]);

                createBackground();

                angular.forEach(settings.series, createSeries);

                if (options.createAxes)
                    createAxes();

                if (options.createLabels)
                    createLabels();

                if (options.showSelectionBar)
                    createSelectionBar();

                if (selectedBarGroup !== undefined && selectItem)
                    selectItem(data[selectedBarGroup], selectedBarGroup);

                function createAxes(){
                    var xAxis = d3.svg.axis()
                        .scale(xScale)
                        .orient("bottom");

                    svg.append("g")
                        .attr("class", "axis")
                        .attr("transform", "translate(" + leftPadding + "," + (height - options.padding.top) + ")")
                        .call(xAxis);

                    var yAxis = d3.svg.axis()
                        .scale(scale)
                        .orient("left");

                    //Create Y axis
                    svg.append("g")
                        .attr("class", "axis")
                        .attr("transform", "translate(" + options.padding.top + ",0)")
                        .call(yAxis);
                }

                function getPatternFill(backgroundColor){
                    var patternId = "pattern-" + backgroundColor.match(/[^\(\)\.#]/g).join("").replace(/\,/g, "_");

                    if (!patterns[patternId]){
                        var pattern = defs.append('svg:pattern')
                            .attr('id', patternId)
                            .attr('width', gridSize)
                            .attr('height', gridSize)
                            .attr('patternUnits', 'userSpaceOnUse');

                        pattern
                            .append('svg:rect')
                            .attr('width', gridSize)
                            .attr('height', gridSize)
                            .attr('fill', backgroundColor)
                            .attr("stroke", "rgba(0,0,0,0.2)")
                            .attr("stroke-dasharray",
                                (gridSize * 2) + "px, " + (gridSize * 2) + "px");

                        patterns[patternId] = "url(#" + patternId + ")";
                    }
                    return patterns[patternId];
                }

                function createBackground(){
                    var backgroundBorder = svg.append("svg:rect")
                        .attr("width", barsArea.width)
                        .attr("height", barsArea.height)
                        .attr("x", 0)
                        .attr("y", 0)
                        .attr("fill", "rgba(0,0,0,0)")
                        .attr("stroke", "#ddd")
                        .attr("class", "crisp")
                        .attr("stroke-width", "1px");

                    var background = svg.append("svg:rect")
                        .attr("width", barsArea.width - options.borderWidth * 2)
                        .attr("height", barsArea.height)
                        .attr("x", options.borderWidth)
                        .attr("y", options.borderWidth)
                        .attr("fill", getPatternFill("rgba(0,0,0,0)"));
                }

                function moveSelectionBarArrowTo(horizontalPosition){
                    selectionBarArrow.attr("d", [
                        "M" + horizontalPosition,
                        height - options.selectionBar.barHeight - options.selectionBar.arrowHeight,
                        "L" + (horizontalPosition - options.selectionBar.arrowWidth / 2),
                        height - options.selectionBar.barHeight,
                        "L" + (horizontalPosition + options.selectionBar.arrowWidth / 2),
                        height - options.selectionBar.barHeight,
                        "Z"
                    ].join(" "));
                }

                function createSelectionBar(){
                    selectionBar = svg.append("svg:rect")
                        .attr("width", width)
                        .attr("height", options.selectionBar.barHeight)
                        .attr("x", 0)
                        .attr("y", height - options.selectionBar.barHeight);

                    selectionBarArrow = svg.append("svg:path");
                }

                function fitToGrid(size){
                    var gridRemainder = size % gridSize;
                    if (gridRemainder)
                        return size - gridRemainder;

                    return size;
                }

                function createSeries(series, seriesIndex){
                    var rects = svg.selectAll("rect.series_" + seriesIndex)
                        .data(data)
                        .enter()
                        .append("rect")
                        .attr("class", "crisp bars vertical-bar series_" + seriesIndex + (options.barsHandleEvents ? " handle-events" : ""));

                    function getDataColor(d){
                        return d._style && d._style[series.field] && d._style[series.field].color;
                    }

                    rects
                        .attr("width", rectWidth)
                        .attr("height", function(d){
                            return fitToGrid(scale(d[series.field]));
                        })
                        .attr("x", function(d, i) {
                            return fitToGrid(i * totalRectWidth + leftPadding + seriesIndex * rectWidth + options.borderWidth);
                        })
                        .attr("y", function(d, i){
                            return barsArea.height + options.borderWidth - fitToGrid(scale(d[series.field]));
                        })
                        .attr("fill", function(d){
                            return getPatternFill(getDataColor(d) || series.color || options.colors[seriesIndex]);
                        });

                    var textMargins = 4,
                        fontSize = Math.max(10, Math.min(rectWidth - textMargins * 2, options.barLabelsFont.size)) + "px";

                    svg.selectAll("text.series_" + seriesIndex)
                        .data(data)
                        .enter()
                        .append("text")
                        .attr("class", "series_" + seriesIndex + (options.barsHandleEvents ? " handle-events" : ""))
                        .text(function(d, i) {
                            return d[series.field];
                        })
                        .attr("x", function(d, i) {
                            return fitToGrid(i * totalRectWidth  + leftPadding) + rectWidth / 2 + seriesIndex * rectWidth;
                        })
                        .attr("y", function(d) {
                            return barsArea.height + options.borderWidth - fitToGrid(scale(d[series.field])) - gridSize;
                        })
                        .attr("font-family", options.barLabelsFont.family)
                        .attr("font-size", fontSize)
                        .attr("font-weight", options.barLabelsFont.weight)
                        .attr("fill", function(d){
                            return getDataColor(d) || series.barLabelsColor || options.barLabelsFont.color;
                        })
                        .attr("text-anchor", "middle");
                }

                function createLabels(){
                    var handleEventsClass =  options.labels.handleEvents ? " handle-events" : "";

                    labelBoxes = svg.selectAll("rect.label-box" + handleEventsClass)
                        .data(data)
                        .enter()
                        .append("rect")
                        .attr("class", "label-box" + handleEventsClass)
                        .attr("width", totalRectWidth - barsSpacing)
                        .attr("height", options.labels.height)
                        .attr("x", function(d, i){
                            return i * totalRectWidth + leftPadding;
                        })
                        .attr("rx", "16px")
                        .attr("y", height - options.labels.height - selectionBarTotalHeight)
                        .attr("fill", function(d, i){
                            var color = d._style && d._style[settings.labels.styleField] && d._style[settings.labels.styleField].color;
                            return color || options.labelsFont.color;
                        });

                    labelTexts = svg.selectAll("text.labels" + handleEventsClass)
                        .data(data)
                        .enter()
                        .append("text")
                        .attr("class", "labels" + handleEventsClass)
                        .text(function(d, i) {
                            return d[settings.labels.field];
                        })
                        .attr("x", function(d, i) {
                            return (i + 0.5) * totalRectWidth + leftPadding  - barsSpacing / 2;
                        })
                        .attr("y", height - 10 - selectionBarTotalHeight)
                        .attr("font-family", options.labelsFont.family)
                        .attr("font-size", options.labelsFont.size)
                        .attr("font-weight", options.labelsFont.weight)
                        .attr("fill", setLabelFill)
                        .attr("text-anchor", "middle");
                }
            }
        }
    };
}]);