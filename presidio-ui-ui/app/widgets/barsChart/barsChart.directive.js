(function () {
    'use strict';

    angular.module("BarsChartWidget").directive("yoxigenBarChart", ["$parse", "$timeout", function ($parse, $timeout) {
        return {
            template: "<div class='chart yoxigen-bar-chart' style='width: 100%; height: 100%'></div>",
            restrict: 'E',
            scope: true,
            replace: true,
            link: function postLink (scope, element, attrs) {
                var data, settings;
                var resizeEventListenerEnabled;
                var selectedBarGroup = null;
                var labelTexts, labelBoxes;
                var selectItem;
                var tooltip, tooltipText, tooltipRect;

                var defaultOptions = {
                        height: "200px",
                        width: "100%",
                        spacing: {min: 0, max: 40},
                        padding: {top: 10, right: 50, left: 50, bottom: 0},
                        selectable: false,
                        highlightSelectedItem: true,
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
                        colors: ["#98A3AC", "#7a92a3"],
                        minHeight: 100,
                        maxWidth: 200,
                        createAxes: false,
                        createLabels: true,
                        selectLabels: true,
                        showSelectionBar: false,
                        refreshOnResize: true,
                        tooltip: "{{seriesLabel}}'s {{barLabel}} score: {{barValue}}"
                    },
                    options;

                var renderTimeoutPromise;
                scope.$watch(attrs.ngModel, function (chartData) {
                    if (chartData) {
                        $timeout.cancel(renderTimeoutPromise);
                        renderTimeoutPromise = $timeout(function () {
                            data = chartData;
                            drawChart();
                        }, 40);
                    }
                });

                scope.$watch(attrs.settings, function (value) {
                    settings = value;
                    options = angular.extend({}, defaultOptions, settings.options);

                    if (settings.selectedIndex !== undefined) {
                        selectedBarGroup = settings.selectedIndex;
                    }

                    drawChart();

                    if (options.refreshOnResize && !resizeEventListenerEnabled) {
                        window.addEventListener("resize", function () {
                            drawChart(false);
                        });
                    } else if (!options.refreshOnResize && resizeEventListenerEnabled) {
                        window.removeEventListener("resize", drawChart);
                    }

                    if (settings.events) {
                        angular.forEach(settings.events, function (eventSettings) {
                            element.on(eventSettings.eventName, ".handle-events", function (e) {
                                scope.$apply(function () {
                                    scope.$emit("widgetEvent",
                                        {event: eventSettings, data: e.currentTarget.__data__, widget: scope.widget});
                                });
                            });
                        });
                    }

                    element.on("mouseover", ".vertical-bar", function (e) {
                        setTooltipText(getTooltipText(e.target));
                        showTooltip();
                        window.addEventListener("mousemove", tooltipMoveHandler);
                    });

                    element.on("mouseout", ".vertical-bar", function () {
                        hideTooltip();
                        window.removeEventListener("mousemove", tooltipMoveHandler);
                    });

                    if (options.selectable && !element.clickHandlerAdded) {
                        element.on("click", ".handle-events", function (e) {
                            var targetData = e.currentTarget.__data__,
                                targetIndex = data.indexOf(targetData);

                            if ((targetIndex !== selectedBarGroup) && selectItem) {
                                selectItem(targetData, targetIndex);
                            }
                        });

                        element.clickHandlerAdded = true;
                    }
                });

                scope.$on("$destroy", function () {
                    $timeout.cancel(renderTimeoutPromise);
                    $timeout.cancel(showTimeoutPromise);
                    element.empty();
                    element.off();
                });

                function getBarIndexForSelection (dataSelection) {
                    for (var i = 0; i < data.length; i++) {
                        if (dataMemberMatchesSelection(dataSelection, data[i])) {
                            return i;
                        }
                    }

                    if (settings.selectedIndex !== undefined) {
                        return settings.selectedIndex;
                    }

                    return null;
                }

                function dataMemberMatchesSelection (dataSelection, dataMember) {
                    for (var property in dataSelection) {
                        if (dataSelection.hasOwnProperty(property)) {
                            if (String(dataMember[property]) !== dataSelection[property]) {
                                return false;
                            }
                        }
                    }

                    return true;
                }

                function getTooltipText (barElement) {
                    var series = settings.series[parseInt(barElement.getAttribute("data-seriesIndex"))],
                        barLabel = data[parseInt(barElement.getAttribute("data-index"))]._label,
                        barValue = barElement.__data__[series.field];

                    return options.tooltip
                        .replace("{{seriesLabel}}", series._label)
                        .replace("{{barLabel}}", barLabel)
                        .replace("{{barValue}}", barValue);
                }

                function getMinValue () {
                    return d3.min(data, function (d) {
                        var fields = [];
                        angular.forEach(settings.series, function (series) {
                            fields.push(d[series.field]);
                        });

                        return d3.min(fields);
                    });
                }

                function getMaxValue () {
                    return d3.max(data, function (d) {
                        var fields = [];
                        angular.forEach(settings.series, function (series) {
                            fields.push(d[series.field]);
                        });

                        return d3.max(fields);
                    });
                }

                function tooltipMoveHandler (e) {
                    var boundingClientRect = element[0].getBoundingClientRect();
                    setTooltipPosition({
                        x: (e.x || e.clientX) - boundingClientRect.left,
                        y: (e.y || e.clientY) - boundingClientRect.top
                    });
                }

                function setTooltipText (text) {
                    tooltipText.text(text);
                    var textClientRect = tooltipText[0][0].getBoundingClientRect();

                    tooltipRect.attr("width", textClientRect.width + 20);
                    tooltipRect.attr("height", textClientRect.height + 10);
                }

                var hideTooltipTimeout;

                function hideTooltip () {
                    clearTimeout(hideTooltipTimeout);
                    hideTooltipTimeout = setTimeout(function () {
                        tooltip[0][0].style.display = "none";
                    }, 50);
                }

                function showTooltip () {
                    clearTimeout(hideTooltipTimeout);
                    tooltip[0][0].style.removeProperty("display");
                }

                function setTooltipPosition (position) {
                    var tooltipPositionX = position.x + 10,
                        tooltipWidth = parseInt(tooltipRect[0][0].getAttribute("width"));

                    if (tooltipPositionX + tooltipWidth > element[0].clientWidth) {
                        tooltipPositionX = position.x - 10 - tooltipWidth;
                    }

                    tooltipRect
                        .attr("x", tooltipPositionX)
                        .attr("y", position.y);

                    tooltipText
                        .attr("x", tooltipPositionX + 10)
                        .attr("y", position.y + 20);
                }

                function setLabelFill (labelData, labelIndex) {
                    if (labelIndex === selectedBarGroup) {
                        return "White";
                    }
                    if (!labelData) {
                        return options.labelsFont.color;
                    }

                    var color = labelData._style && labelData._style[settings.labels.styleField] &&
                        labelData._style[settings.labels.styleField].color;
                    return color || options.labelsFont.color;
                }

                function getSelectionBarColor (itemData) {
                    var color = itemData && itemData._style && itemData._style[settings.selectionBar.styleField] &&
                        itemData._style[settings.selectionBar.styleField].color;
                    return color || options.selectable.defaultColor;
                }

                var showTimeoutPromise;
                scope.$on("show", function () {
                    $timeout.cancel(showTimeoutPromise);
                    showTimeoutPromise = $timeout(function () {
                        drawChart(false);
                    });
                });

                function drawChart (selectBar) {
                    if (!data || !settings) {
                        return;
                    }

                    element.css({
                        height: options.height,
                        width: "100%"
                    });

                    element[0].innerHTML = "";

                    var domainMinMax = options.values || {};
                    if (domainMinMax.min === undefined) {
                        domainMinMax.min = getMinValue();
                    }
                    if (domainMinMax.max === undefined) {
                        domainMinMax.max = getMaxValue();
                    }

                    var scale = d3.scale.linear()
                        .domain([domainMinMax.min, domainMinMax.max]);

                    var patterns = {};

                    if (settings.selectedData) {
                        selectedBarGroup = getBarIndexForSelection(settings.selectedData);
                    }

                    if (selectedBarGroup === "last") {
                        selectedBarGroup = data.length - 1;
                    }

                    var width = element.width(),
                        height = element.height(),
                        gridSize = options.gridSize,
                        leftPadding = options.padding.left,
                        rightPadding = options.padding.right,
                        selectionBarTotalHeight = options.showSelectionBar ?
                        options.selectionBar.barHeight + options.selectionBar.arrowHeight +
                        options.selectionBar.margin : 0,
                        bottomReservedSpace = options.padding.bottom +
                            (options.createLabels ? options.labels.height + options.labels.margin : 0) +
                            selectionBarTotalHeight,
                        barsSpacing = (width / data.length) / (data.length - 1),
                        barsArea = {
                            width: width - 2 * options.borderWidth,
                            height: height - bottomReservedSpace - options.borderWidth * 2
                        },
                        heightGridRemainder = barsArea.height % gridSize,
                        widthGridRemainder = barsArea.width % gridSize,
                        selectionBar,
                        selectionBarArrow;

                    if (heightGridRemainder) {
                        barsArea.height += (gridSize - heightGridRemainder);
                        bottomReservedSpace -= heightGridRemainder;
                    }
                    if (widthGridRemainder) {
                        barsArea.width -= widthGridRemainder;
                        leftPadding += Math.floor(widthGridRemainder / 2);
                        rightPadding += Math.floor(widthGridRemainder / 2);
                    }

                    barsSpacing = Math.max(barsSpacing, options.spacing.min);
                    barsSpacing = Math.min(barsSpacing, options.spacing.max);

                    scale.range([10, barsArea.height - options.barLabels.height - options.barLabels.margin]);

                    var svg = d3.select(element[0])
                        .append("svg")
                        .attr("height", "100%")
                        .attr("width", "100%");

                    var defs = svg.append('svg:defs');

                    var totalRectWidth = Math.min((width - leftPadding - rightPadding + barsSpacing) / data.length,
                            options.maxWidth * settings.series.length),
                        rectWidth = (totalRectWidth - barsSpacing) / settings.series.length,
                        barRemainder = rectWidth % gridSize;

                    if (options.highlightSelectedItem) {
                        selectItem = function (itemData, itemIndex) {
                            var color = getSelectionBarColor(itemData),
                                previousSelectedIndex = selectedBarGroup;

                            if (labelBoxes) {
                                d3.select(labelBoxes[0][selectedBarGroup]).classed("selected", false);
                            }

                            selectedBarGroup = itemIndex;

                            if (labelBoxes && options.selectLabels) {
                                d3.select(labelTexts[0][selectedBarGroup]).attr("fill",
                                    setLabelFill(itemData, selectedBarGroup));
                                d3.select(labelTexts[0][previousSelectedIndex]).attr("fill",
                                    setLabelFill(data[previousSelectedIndex], previousSelectedIndex));
                                d3.select(labelBoxes[0][selectedBarGroup]).classed("selected", true);
                            }

                            if (selectionBar) {
                                selectionBar.attr("fill", color);
                                selectionBarArrow.attr("fill", color);
                                moveSelectionBarArrowTo(itemIndex * totalRectWidth + leftPadding +
                                    (totalRectWidth - barsSpacing) / 2);
                            }
                        };
                    }
                    if (barRemainder) {
                        var halfGrid = Math.floor(gridSize / 2);
                        if (barRemainder < halfGrid) {
                            rectWidth -= barRemainder;
                        } else {
                            rectWidth += gridSize - barRemainder;
                        }
                    }

                    var xScale = d3.scale.linear()
                        .domain([0, data.length])
                        .range([0, totalRectWidth * data.length]);

                    function createAxes () {
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

                    function createTooltip () {
                        tooltip = svg.append("g")
                            .attr("style", "display: none");

                        tooltipRect = tooltip.append("rect")
                            .attr("width", 100)
                            .attr("height", 50)
                            .attr("fill", "#333333")
                            .attr("x", 0).attr("y", 0);

                        tooltipText = tooltip.append("text")
                            .attr("fill", "White")
                            .attr("font-size", "16px")
                            .attr("x", 0).attr("y", 0);

                    }

                    function getPatternFill (backgroundColor) {
                        var patternId = "pattern-" + backgroundColor.match(/[^\(\)\.#]/g).join("").replace(/\,/g, "_");

                        if (!patterns[patternId]) {
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

                    function createBackground () {
                        svg.append("svg:rect")
                            .attr("width", barsArea.width)
                            .attr("height", barsArea.height)
                            .attr("x", 0)
                            .attr("y", 0)
                            .attr("fill", "rgba(0,0,0,0)")
                            .attr("stroke", "#ddd")
                            .attr("class", "crisp")
                            .attr("stroke-width", "1px");

                        svg.append("svg:rect")
                            .attr("width", barsArea.width - options.borderWidth * 2)
                            .attr("height", barsArea.height)
                            .attr("x", options.borderWidth)
                            .attr("y", options.borderWidth)
                            .attr("fill", getPatternFill("rgba(0,0,0,0)"));
                    }

                    function moveSelectionBarArrowTo (horizontalPosition) {
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

                    function createSelectionBar () {
                        selectionBar = svg.append("svg:rect")
                            .attr("width", width)
                            .attr("height", options.selectionBar.barHeight)
                            .attr("x", 0)
                            .attr("y", height - options.selectionBar.barHeight);

                        selectionBarArrow = svg.append("svg:path");
                    }

                    function fitToGrid (size) {
                        var gridRemainder = size % gridSize;
                        if (gridRemainder) {
                            return size - gridRemainder;
                        }

                        return size;
                    }

                    function createSeries (series, seriesIndex) {
                        var rects = svg.selectAll("rect.series_" + seriesIndex)
                            .data(data)
                            .enter()
                            .append("rect")
                            .attr("class", "crisp bars vertical-bar series_" + seriesIndex +
                            (options.barsHandleEvents ? " handle-events" : ""));

                        function getDataColor (d) {
                            return d._style && d._style[series.field] && d._style[series.field].color;
                        }

                        rects
                            .attr("width", Math.max(0, rectWidth))
                            .attr("height", function (d) {
                                var value = d[series.field];
                                if (value === null) {
                                    return 0;
                                }

                                return Math.max(gridSize, fitToGrid(scale(value)));
                            })
                            .attr("x", function (d, i) {
                                return fitToGrid(i * totalRectWidth + leftPadding + seriesIndex * rectWidth +
                                    options.borderWidth);
                            })
                            .attr("y", function (d) {
                                var value = d[series.field],
                                    height;

                                height = value === null ? 0 : Math.max(gridSize, fitToGrid(scale(value)));
                                return barsArea.height + options.borderWidth - height;
                            })
                            .attr("fill", function (d) {
                                return getPatternFill(getDataColor(d) || series.color || options.colors[seriesIndex]);
                            })
                            .attr("data-seriesIndex", seriesIndex)
                            .attr("data-index", function (d, i) {
                                return i;
                            });

                        if (series.nullDisplay) {
                            angular.forEach(data, function (item, itemIndex) {
                                if (item[series.field] === null) {
                                    svg.append("text")
                                        .text(series.nullDisplay)
                                        .attr("x", fitToGrid(itemIndex * totalRectWidth + leftPadding) +
                                        (totalRectWidth - barsSpacing) / 2)
                                        .attr("y", barsArea.height + options.borderWidth - gridSize - 2)
                                        .attr("font-family", options.barLabelsFont.family)
                                        .attr("font-size", "14px")
                                        .attr("font-weight", options.barLabelsFont.weight)
                                        .attr("fill", "#666666")
                                        .attr("text-anchor", "middle");
                                }
                            });
                        }

                        var textMargins = 4,
                            fontSize = Math.max(10, Math.min(rectWidth - textMargins * 2, options.barLabelsFont.size)) +
                                "px";

                        svg.selectAll("text.series_" + seriesIndex)
                            .data(data)
                            .enter()
                            .append("text")
                            .attr("class", "series_" + seriesIndex + (options.barsHandleEvents ? " handle-events" : ""))
                            .text(function (d) {
                                return d[series.field];
                            })
                            .attr("x", function (d, i) {
                                return fitToGrid(i * totalRectWidth + leftPadding) + rectWidth / 2 +
                                    seriesIndex * rectWidth;
                            })
                            .attr("y", function (d) {
                                return barsArea.height + options.borderWidth -
                                    Math.max(gridSize, fitToGrid(scale(d[series.field]))) - gridSize;
                            })
                            .attr("font-family", options.barLabelsFont.family)
                            .attr("font-size", fontSize)
                            .attr("font-weight", options.barLabelsFont.weight)
                            .attr("fill", function (d) {
                                return getDataColor(d) || series.barLabelsColor || options.barLabelsFont.color;
                            })
                            .attr("text-anchor", "middle");
                    }

                    function createLabels () {
                        var handleEventsClass = options.labels.handleEvents ? " handle-events" : "",
                            labelWidth = totalRectWidth - barsSpacing;

                        if (labelWidth <= 0) {
                            return;
                        }

                        labelBoxes = svg.selectAll("rect.label-box" + handleEventsClass)
                            .data(data)
                            .enter()
                            .append("rect")
                            .attr("class", "label-box" + handleEventsClass)
                            .attr("width", totalRectWidth - barsSpacing)
                            .attr("height", options.labels.height)
                            .attr("x", function (d, i) {
                                return i * totalRectWidth + leftPadding;
                            })
                            .attr("rx", Math.floor(options.labels.height / 2) + "px")
                            .attr("y", height - options.labels.height - selectionBarTotalHeight)
                            .attr("fill", function (d) {
                                var color = d._style && d._style[settings.labels.styleField] &&
                                    d._style[settings.labels.styleField].color;
                                return color || options.labelsFont.color;
                            });

                        labelTexts = svg.selectAll("text.labels" + handleEventsClass)
                            .data(data)
                            .enter()
                            .append("text")
                            .attr("class", "labels" + handleEventsClass)
                            .text(function (d) {
                                return d._label;
                            })
                            .attr("x", function (d, i) {
                                return (i + 0.5) * totalRectWidth + leftPadding - barsSpacing / 2;
                            })
                            .attr("y", height - options.labels.height - selectionBarTotalHeight +
                            parseInt(options.labelsFont.size, 10) + 1)
                            .attr("font-family", options.labelsFont.family)
                            .attr("font-size", options.labelsFont.size)
                            .attr("font-weight", options.labelsFont.weight)
                            .attr("fill", setLabelFill)
                            .attr("text-anchor", "middle");
                    }

                    createBackground();

                    angular.forEach(settings.series, createSeries);

                    if (options.createAxes) {
                        createAxes();
                    }

                    if (options.createLabels) {
                        createLabels();
                    }

                    if (options.showSelectionBar) {
                        createSelectionBar();
                    }

                    if (selectedBarGroup !== undefined && selectItem) {
                        selectItem(data[selectedBarGroup], selectedBarGroup);

                        if (settings.selectedIndex && selectBar !== false) {
                            angular.forEach(settings.events, function (eventSettings) {
                                var eventSettingsCopy = angular.copy(eventSettings);
                                if (eventSettingsCopy.actionOptions) {
                                    eventSettingsCopy.actionOptions.updateUrl = false;
                                }

                                if (eventSettings.eventName === "click") {
                                    scope.$emit("widgetEvent",
                                        {event: eventSettingsCopy, data: data[selectedBarGroup], widget: scope.widget});
                                }
                            });
                        }
                    }

                    createTooltip();

                    if (options.selectFirstOnLoad && selectItem) {
                        selectItem(data[0], 0);
                    }

                }
            }
        };
    }]);
}());
