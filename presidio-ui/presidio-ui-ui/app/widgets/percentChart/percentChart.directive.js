(function () {
    'use strict';

    angular.module("PercentChartWidget").directive("yoxigenPercentChart", ["$rootScope", function ($rootScope) {
        return {
            template: "<div class='chart yoxigen-percent-chart' style='width: 100%; height: 100%'></div>",
            restrict: 'E',
            scope: true,
            replace: true,
            link: function postLink (scope, element, attrs) {
                var data, settings;
                var resizeEventListenerEnabled;
                var selectedBarGroup = null;
                var labelTexts, labelBoxes;
                var selectItem;
                var rects;
                var selectedOnLoad;

                var defaultOptions = {
                        selectable: false,
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
                        borderWidth: 1,
                        gridSize: 13,
                        minGridSize: 8,
                        maxGridSize: 16,
                        barsHandleEvents: true,
                        colors: ["#0b62a4", "#7a92a3"],
                        createLabels: true,
                        showSelectionBar: false,
                        refreshOnResize: true
                    },
                    options;

                scope.$on("$destroy", function () {
                    element.empty();
                    element.off();
                });

                scope.$watch(attrs.ngModel, function (chartData) {
                    data = chartData;
                    drawChart();
                });

                scope.$watch(attrs.settings, function (value) {
                    settings = value;
                    options = angular.extend({}, defaultOptions, settings.options);

                    if (settings.selectedIndex !== undefined) {
                        selectedBarGroup = settings.selectedIndex;
                    }

                    drawChart();

                    if (options.refreshOnResize && !resizeEventListenerEnabled) {
                        window.addEventListener("resize", drawChart);
                    } else if (!options.refreshOnResize && resizeEventListenerEnabled) {
                        window.removeEventListener("resize", drawChart);
                    }

                    if (settings.events) {
                        angular.forEach(settings.events, function (eventSettings) {
                            element.on(eventSettings.eventName, ".handle-events", function (e) {
                                scope.safeApply(function () {
                                    scope.$emit("widgetEvent",
                                        {event: eventSettings, data: e.currentTarget.__data__, widget: scope.widget});
                                });
                            });
                        });
                    }

                    if (options.selectable) {
                        element.on("click", ".handle-events", function (e) {
                            var targetData = e.currentTarget.__data__,
                                targetIndex = data.indexOf(targetData);

                            if ((targetIndex !== selectedBarGroup) && selectItem) {
                                selectItem(targetData, targetIndex);
                            }
                        });
                    }
                });

                function removeEmptyData (data) {
                    var copiedData = angular.copy(data);
                    for (var i = copiedData.length - 1; i >= 0; i--) {
                        if (!copiedData[i][settings.series[0].field]) {
                            copiedData.splice(i, 1);
                        }
                    }
                    return copiedData;
                }

                function setLabelFill (labelData, labelIndex) {
                    if (labelIndex === selectedBarGroup) {
                        return "White";
                    }
                    var color = labelData._style && labelData._style[settings.labels.styleField] &&
                        labelData._style[settings.labels.styleField].color;
                    return color || options.labelsFont.color;
                }

                function getSelectionBarColor (itemData) {
                    var color = itemData._style && itemData._style[settings.selectionBar.styleField] &&
                        itemData._style[settings.selectionBar.styleField].color;
                    return color || options.selectable.defaultColor;
                }


                function drawChart () {

                    function getBarWidth (barIndex) {
                        var barWidth = barsWidth[barIndex];
                        if (barWidth !== undefined) {
                            return barWidth;
                        }

                        barWidth = fitToGrid(scale(data[barIndex][settings.series[0].field]));
                        barsWidth[barIndex] = barWidth;
                        return barWidth;
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
                            .attr("class", "crisp")
                            .attr("width", barsArea.width + 2 * gridSize - 2 * options.borderWidth)
                            .attr("height", barsArea.height + 2 * gridSize)
                            .attr("x", 0)
                            .attr("y", 0)
                            .attr("stroke", "rgb(204,204,204)")
                            .attr("fill", getPatternFill("#ffffff"));
                    }

                    function moveSelectionBarArrowTo (itemIndex) {
                        var arrowPosition = leftPadding,
                            itemWidth;

                        for (var i = 0; i < itemIndex; i++) {
                            itemWidth = getBarWidth(i);
                            arrowPosition += itemWidth + gridSize;
                        }

                        arrowPosition += getBarWidth(itemIndex) / 2;

                        selectionBarArrow.attr("d", [
                            "M" + arrowPosition,
                            height - options.selectionBar.barHeight - options.selectionBar.arrowHeight,
                            "L" + (arrowPosition - options.selectionBar.arrowWidth / 2),
                            height - options.selectionBar.barHeight,
                            "L" + (arrowPosition + options.selectionBar.arrowWidth / 2),
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
                        if (size < gridSize) {
                            return gridSize;
                        }

                        var gridRemainder = size % gridSize;
                        if (gridRemainder) {
                            return size - gridRemainder;
                        }

                        return size;
                    }

                    function createBars (series) {
                        rects = svg.selectAll("rect.bars")
                            .data(data)
                            .enter()
                            .append("rect")
                            .attr("class",
                            "crisp bars percent-bars" + (options.barsHandleEvents ? " handle-events" : ""));

                        var totalWidth = leftPadding;

                        function getDataColor (d) {
                            return d._style && d._style[series.field] && d._style[series.field].color;
                        }

                        rects
                            .attr("x", function (d, i) {
                                var currentTotalWidth = totalWidth;
                                totalWidth += getBarWidth(i) + gridSize;
                                return currentTotalWidth;
                            })
                            .attr("width", function (d, i) {
                                return getBarWidth(i);
                            })
                            .attr("height", barsArea.height + "px")
                            .attr("y", gridSize)
                            .attr("fill", function (d, i) {
                                return getPatternFill(getDataColor(d) || series.color || options.colors[i]);
                            })
                            .append("title").text(function (d) {
                                return d.tooltip;
                            });
                    }

                    function createLabels () {
                        var handleEventsClass = options.labels.handleEvents ? " handle-events" : "",
                            totalWidth = leftPadding;

                        labelBoxes = svg.selectAll("rect.label-box" + handleEventsClass)
                            .data(data)
                            .enter()
                            .append("rect")
                            .attr("class", "label-box" + handleEventsClass)
                            .attr("height", options.labels.height)
                            .attr("rx", "16px")
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
                                return (d._percent < 1 ? "< 1" : Math.round(d._percent)) + "%";
                            })
                            .attr("x", function (d, i) {
                                var currentItemWidth = getBarWidth(i),
                                    currentTotalWidth = totalWidth;

                                totalWidth += currentItemWidth + gridSize;
                                return currentTotalWidth + currentItemWidth / 2;
                            })
                            .attr("y", height - 10 - selectionBarTotalHeight)
                            .attr("font-family", options.labelsFont.family)
                            .attr("font-size", options.labelsFont.size)
                            .attr("font-weight", options.labelsFont.weight)
                            .attr("fill", setLabelFill)
                            .attr("text-anchor", "middle");

                        totalWidth = leftPadding;

                        labelBoxes
                            .attr("width", function (d, i) {
                                return labelTexts[0][i].clientWidth + 10;
                            })
                            .attr("x", function (d, i) {
                                var currentTotalWidth = totalWidth,
                                    itemWidth = getBarWidth(i);

                                totalWidth += getBarWidth(i) + gridSize;
                                return Math.max(0,
                                    currentTotalWidth + (itemWidth - this.getBoundingClientRect().width) / 2);
                            });
                    }

                    if (!data || !settings) {
                        return;
                    }

                    element.css({
                        height: options.height,
                        width: "100%"
                    });

                    element[0].innerHTML = "";

                    data = removeEmptyData(data);

                    var valueField = settings.series[0].field,
                        scale = d3.scale.linear()
                            .domain([0, d3.sum(data, function (d) {
                                return d[valueField];
                            })]);

                    var patterns = {};

                    var width = element.width(),
                        height = element.height(),
                        gridSize = options.gridSize,
                        leftPadding = gridSize,
                        leftMargin = 0,
                        selectionBarTotalHeight = options.showSelectionBar ?
                        options.selectionBar.barHeight + options.selectionBar.arrowHeight +
                        options.selectionBar.margin : 0,
                        bottomReservedSpace = (options.createLabels ? options.labels.height + options.labels.margin :
                                0) + selectionBarTotalHeight,
                        barsArea = {width: width - 2 * gridSize, height: height - gridSize * 2 - bottomReservedSpace},
                        heightGridRemainder = barsArea.height % gridSize,
                        widthGridRemainder = barsArea.width % gridSize,
                        selectionBar,
                        selectionBarArrow,
                        barsWidth = [];

                    if (heightGridRemainder) {
                        barsArea.height += (gridSize - heightGridRemainder);
                        bottomReservedSpace -= heightGridRemainder;
                    }
                    if (widthGridRemainder) {
                        leftMargin = widthGridRemainder / 2;
                        barsArea.width -= widthGridRemainder;
                    }

                    scale.range([0, barsArea.width - gridSize]);

                    var svg = d3.select(element[0])
                        .append("svg")
                        .attr("height", "100%")
                        .attr("width", "100%");

                    var defs = svg.append('svg:defs');

                    selectItem = function (itemData, itemIndex) {
                        var color = getSelectionBarColor(itemData),
                            previousSelectedIndex = selectedBarGroup;

                        d3.select(labelBoxes[0][selectedBarGroup]).classed("selected", false);
                        selectedBarGroup = itemIndex;
                        d3.select(labelTexts[0][selectedBarGroup]).attr("fill",
                            setLabelFill(itemData, selectedBarGroup));
                        if (previousSelectedIndex !== null) {
                            d3.select(labelTexts[0][previousSelectedIndex]).attr("fill",
                                setLabelFill(data[previousSelectedIndex], previousSelectedIndex));
                        }

                        d3.select(labelBoxes[0][selectedBarGroup]).classed("selected", true);

                        selectionBar.attr("fill", color);
                        selectionBarArrow.attr("fill", color);
                        moveSelectionBarArrowTo(itemIndex);
                    };

                    createBackground();

                    createBars(settings.series[0]);

                    if (options.createLabels) {
                        createLabels();
                    }

                    if (options.showSelectionBar) {
                        createSelectionBar();
                    }

                    if (selectedBarGroup !== null && selectedBarGroup !== undefined && selectItem) {
                        selectItem(data[selectedBarGroup], selectedBarGroup);
                    }

                    if (!selectedOnLoad && options.selectFirstOnLoad && selectItem) {
                        setTimeout(function () {
                            $rootScope.$apply(function () {
                                jQuery(rects[0][0]).click();
                            });
                        });

                        selectedOnLoad = true;
                    }

                }
            }
        };
    }]);
}());
