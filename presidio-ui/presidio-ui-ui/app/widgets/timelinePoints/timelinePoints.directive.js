(function () {
    'use strict';

    angular.module("TimelineWidget").directive("timelinePoints",
        ["$rootScope", "$window", "utils", "$parse", "events", "config",
            function ($rootScope, $window, utils, $parse, events, config) {

                return {
                    template: "<div class='timeline' style='width: 100%; height: 100%'></div>",
                    restrict: 'E',
                    scope: true,
                    replace: true,
                    link: function postLink (scope, element, attrs) {
                        var data, settings, options,
                            defaultOptions = {
                                height: 300,
                                tooltipLineHeight: 20
                            },
                            isInit,
                            axisHeight = 23,
                            rectHeight = 18,
                            seriesMargin = 4,
                            seriesHeight = rectHeight + seriesMargin * 2,
                            currentPage = 1,
                            pageFunc = attrs.page ? $parse(attrs.page) : null,
                            isPaging,
                            TIME_DISPLAY_HEIGHT = 15,
                            LEGEND_MARGIN = 10,
                            LEGEND_HEIGHT = 20,
                            LEGEND_ITEM_PADDING = 20;

                        var svg,
                            mask,
                            margin = {top: 0, right: 20, bottom: 30, left: 0},
                            width,
                            height,
                            chartWidth,
                            svgHeight,
                            lastRefreshTime = new Date(),
                            lastRefreshTimeLine,
                            timeDisplay,
                            timeDisplayText,
                            timeDisplayVisible,
                            labelsWidth,
                            labelsContainer,
                            x, xAxis,
                            xAxisElement,
                            lineElements,
                            selectedTimespan,
                            dataContainer,
                            timeMinMax,
                            seriesWidth,
                            timeDiff,
                            firstTimespanPosition,
                            earliestTimespanDate,
                            xField,
                            colorField,
                            totalSeriesHeight,
                            points,
                            color,
                            typesCount = {};


                        function onResize () {
                            svg.attr("width", "100%");
                            //element.css("width", "100%");

                            width = element.width();
                            height =
                                totalSeriesHeight + axisHeight + TIME_DISPLAY_HEIGHT + margin.bottom + LEGEND_MARGIN +
                                LEGEND_HEIGHT;
                            chartWidth = width - margin.left - margin.right;
                            x.range([0, seriesWidth = chartWidth - labelsWidth + margin.left + margin.right]);
                            svg.attr("width", width);
                            mask.attr("width", width);

                            points.attr("transform", function (d) {
                                return "translate(" + x(utils.date.getMoment(d[xField]).toDate()) + ", " +
                                    seriesHeight / 2 + ")";
                            });

                            xAxisElement.call(xAxis);
                            xAxisElement.attr("x", chartWidth);

                            lineElements.attr("x2", width);
                        }

                        function getColorScale (scaleName) {
                            if (scaleName && angular.isObject(scaleName) && scaleName.map) {
                                var defaultColor = scaleName.map._default || "#000000",
                                    field = scaleName.field,
                                    map = scaleName.map;

                                return function (d) {
                                    var value = d[field],
                                        mapValue = map[value];

                                    if (!value || !mapValue) {
                                        return defaultColor;
                                    }

                                    return mapValue;
                                };
                            }

                            if (scaleName) {
                                if (scaleName === "score") {
                                    return d3.scale.linear().domain([0, 50, 51, 80, 90, 100]).range(['#80BFF0',
                                        '#80BFF0', '#F1CC37', '#F59925', '#D77576', '#D77576']);
                                }

                                var d3Scale = d3.scale[scaleName];
                                if (d3Scale) {
                                    return d3Scale();
                                }
                            }

                            return d3.scale.category20c();
                        }

                        function drawChart () {

                            function onMouseLeave () {
                                element.off("mousemove.timeDisplay", onMouseMove);
                                element.off("mouseleave.timeDisplay", onMouseLeave);
                                timeDisplay[0][0].style.display = "none";
                                timeDisplayVisible = false;
                            }

                            function onMouseMove (e) {
                                if (e.offsetX < labelsWidth) {
                                    if (timeDisplayVisible) {
                                        timeDisplayVisible = false;
                                        timeDisplay[0][0].style.display = "none";
                                    }
                                }
                                else if (e.target.nodeName !== "text") {
                                    if (!timeDisplayVisible) {
                                        timeDisplay[0][0].style.removeProperty("display");
                                        timeDisplayVisible = true;
                                    }
                                    timeDisplay.attr("transform", "translate(" + e.offsetX + ", 0)");
                                    timeDisplayText.text(utils.date.getMoment(x.invert(e.offsetX -
                                        labelsWidth)).format("MMM DD, HH:mm:ss"));
                                }
                            }

                            function zoom () {
                                var translateX = d3.event.translate[0];

                                points.attr("transform", function (d) {
                                    return "translate(" + x(utils.date.getMoment(d[xField]).toDate()) + ", " +
                                        seriesHeight / 2 + ")";
                                });

                                var timeDiffScaled = timeDiff / d3.event.scale,
                                    timeStart = +timeMinMax[0] - timeDiff * (translateX / seriesWidth / d3.event.scale);

                                x.domain([
                                    utils.date.getMoment(timeStart).toDate(),
                                    utils.date.getMoment(timeStart + timeDiffScaled).toDate()
                                ]);
                                xAxisElement.call(xAxis);

                                lastRefreshTimeLine.attr("transform", "translate(" + x(lastRefreshTime) + ", 0)");

                                if (!isPaging && translateX / d3.event.scale * -1 < firstTimespanPosition) {
                                    loadPage();
                                }
                            }

                            function setLastRefreshTime () {
                                lastRefreshTime = new Date();
                                lastRefreshTimeLine.attr("transform", "translate(" + x(lastRefreshTime) + ", 0)");
                            }

                            function loadPage () {
                                isPaging = true;
                                pageFunc(scope, {$firstTime: earliestTimespanDate});
                            }

                            function createSeries () {
                                dataContainer = svg.append("g")
                                    .attr("transform", "translate(" + labelsWidth + ", 0)");

                                var lines = svg.append("g");

                                var seriesBackground = dataContainer.append("g")
                                    .attr("clip-path", "url(#seriesClipPath)")
                                    .call(d3.behavior.zoom().x(x).scaleExtent([0.1, 60])
                                        .on("zoom", zoom)
                                );

                                seriesBackground.append("rect")
                                    .attr("height", totalSeriesHeight)
                                    .attr("width", "100%")
                                    .attr("fill", "Transparent")
                                    .attr("class", "zoom-area");

                                dataContainer = seriesBackground.append("g");

                                dataContainer.selectAll("g")
                                    .data(data).enter()
                                    .append("g")
                                    .attr("class", function (d, i) {
                                        return "series_" + i;
                                    })
                                    .attr("transform", function (d, i) {
                                        return "translate(0, " + (i * seriesHeight) + ")";
                                    });

                                lineElements = lines.selectAll("line").data(data)
                                    .enter().append("line")
                                    .attr("transform", function (d, i) {
                                        return "translate(0, " + (i * seriesHeight) + ")";
                                    })
                                    .attr("x1", 0)
                                    .attr("x2", width)
                                    .attr("y1", 0)
                                    .attr("y2", 0)
                                    .attr("stroke-width", "1")
                                    .attr("stroke", "#ddd");

                                lastRefreshTimeLine = lines.append("line")
                                    .attr("transform", "translate(" + x(lastRefreshTime) + ", 0)")
                                    .attr("x1", 0)
                                    .attr("x2", 0)
                                    .attr("y1", 0)
                                    .attr("y2", totalSeriesHeight)
                                    .attr("stroke", "#ddd")
                                    .attr("stroke-width", 2);

                                addTimespans(data);
                            }

                            function getTypeSymbol (type) {
                                var symbolType = settings.symbolMap[type] || "circle";
                                return d3.svg.symbol().type(symbolType).size(rectHeight * 5)();
                            }

                            function addTimespans (data) {
                                angular.forEach(data, function (series, i) {
                                    var seriesContainer = dataContainer.selectAll(".series_" + i);
                                    seriesContainer[0][0].innerHTML = "";

                                    var timespansData;
                                    if (settings.series.timeSpansSeries) {
                                        timespansData = [];
                                        angular.forEach(series[settings.series.timeSpansSeries],
                                            function (timeSpansArrayObject) {
                                                angular.forEach(timeSpansArrayObject[settings.series.timeSpans],
                                                    function (timespan) {
                                                        timespan._groupName =
                                                            timeSpansArrayObject[settings.series
                                                                .timeSpansSeriesGroupName];
                                                        timespansData.push(timespan);
                                                    });
                                            });
                                    }
                                    else if (settings.series.timeSpans) {
                                        timespansData = series[settings.series.timeSpans];
                                    } else if (settings.series.isSingleSeries) {
                                        timespansData = series._timespans;
                                    } else {
                                        timespansData = [series];
                                    }

                                    points = seriesContainer.selectAll(".timespan")
                                        .data(timespansData)
                                        .enter().append("path")
                                        .attr("class", "timespan")
                                        .attr("d", function (d) {
                                            var typeCount = typesCount[d[typeField]];
                                            if (typeCount === undefined) {
                                                typesCount[d[typeField]] = 1;
                                            } else {
                                                typesCount[d[typeField]]++;
                                            }

                                            return getTypeSymbol(d[typeField]);
                                        })
                                        .attr("transform", function (d) {
                                            var position = x(utils.date.getMoment(d[xField]).toDate());
                                            if (firstTimespanPosition === undefined ||
                                                position < firstTimespanPosition) {
                                                firstTimespanPosition = position;
                                            }

                                            return "translate(" + position + ", " + seriesHeight / 2 + ")";
                                        })
                                        .attr("fill", "White")
                                        .attr("stroke-width", 1.5)
                                        .attr("stroke", function (d) {
                                            return color(d);
                                        });
                                });

                                currentPage++;
                            }

                            // Get the relevant timeSpans
                            function getTimeSpanSeriesName (settings) {
                                if (settings.series.timeSpansSeries) {
                                    return settings.series.timeSpansSeries;
                                }

                                return settings.series.timeSpans || "_timespans";
                            }

                            function getEarliestTimespanDate () {
                                if (settings.series.timeSpansSeries || settings.series.timeSpans ||
                                    settings.series.isSingleSeries) {
                                    var timeSpanSeriesName = getTimeSpanSeriesName(settings);

                                    earliestTimespanDate = d3.min(data, function (d) {
                                        var timeSpansData = d[timeSpanSeriesName];

                                        return d3.min(timeSpansData, function (d2) {
                                            if (settings.series.timeSpansSeries) {
                                                return d3.min(d2[settings.series.timeSpans], function (d3) {
                                                    return utils.date.getMoment(d3[xField]).toDate();
                                                });
                                            }
                                            else {
                                                return utils.date.getMoment(d2[xField]).toDate();
                                            }
                                        });
                                    });
                                }
                                else {
                                    earliestTimespanDate = d3.min(data, function (d) {
                                        return utils.date.getMoment(d[xField]).toDate();
                                    });
                                }

                                return earliestTimespanDate;
                            }

                            function createAxes () {
                                labelsContainer = svg.append("g");

                                labelsContainer.selectAll(".seriesLabel")
                                    .data(data)
                                    .enter().append("text")
                                    .attr("class", "seriesLabel")
                                    .text(function (d) {
                                        return d[settings.series.name];
                                    })
                                    .attr("text-anchor", "end")
                                    .attr("height", seriesHeight)
                                    .attr("transform", function (d, i) {
                                        return "translate(0, " + ((0.5 + i) * seriesHeight + 4) + ")";
                                    });

                                labelsWidth = d3.max(labelsContainer.selectAll(".seriesLabel")[0], function (node) {
                                    return node.clientWidth;
                                });

                                labelsContainer.attr("transform", "translate(" + labelsWidth + ", 0)");

                                labelsWidth += 10; // Padding for labels

                                var d3timeScale = config.alwaysUtc ? d3.time.scale.utc : d3.time.scale;

                                x = d3timeScale()
                                    .range([0, seriesWidth = chartWidth - labelsWidth + margin.left + margin.right]);

                                xAxis = d3.svg.axis()
                                    .scale(x)
                                    .orient("bottom")
                                    .tickSize(6, 0);

                                var xDomain1 = getEarliestTimespanDate(),
                                    xDomain2;

                                if (settings.series.timeSpansSeries || settings.series.timeSpans ||
                                    settings.series.isSingleSeries) {
                                    var timeSpanSeriesName = getTimeSpanSeriesName(settings);

                                    if (settings.useLastRefreshTimeForX) {
                                        xDomain2 = utils.date.getMoment('1hours').toDate();
                                    }
                                    else {
                                        xDomain2 = d3.max(data, function (d) {
                                            return d3.max(d[timeSpanSeriesName], function (d2) {
                                                if (settings.series.timeSpansSeries) {
                                                    return d3.max(d2[settings.series.timeSpans], function (d3) {
                                                        return utils.date.getMoment(d3[xField]).toDate();
                                                    });
                                                }
                                                else {
                                                    return utils.date.getMoment(d2[xField]).toDate();
                                                }
                                            });
                                        });
                                    }
                                }
                                else {
                                    xDomain2 = d3.max(data, function (d) {
                                        return utils.date.getMoment(d[xField]).toDate();
                                    });
                                }

                                x.domain(timeMinMax = [xDomain1, xDomain2]);
                                timeDiff = xDomain2 - xDomain1;

                                xAxisElement = svg.append("g")
                                    .attr("class", "x axis")
                                    .attr("transform",
                                    "translate(" + labelsWidth + "," + (totalSeriesHeight + TIME_DISPLAY_HEIGHT - 15) +
                                    ")")
                                    .call(xAxis);

                                xAxisElement
                                    .append("text")
                                    .attr("class", "label")
                                    .attr("x", chartWidth)
                                    .attr("y", -6)
                                    .style("text-anchor", "end");
                            }

                            function createTimeDisplay () {
                                timeDisplay = svg.append("g").attr("style", "display: none");
                                timeDisplayText = timeDisplay.append("text")
                                    .attr("fill", "#333")
                                    .attr("text-anchor", "middle")
                                    .attr("transform", "translate(0, -5)");

                                timeDisplay.append("line")
                                    .attr("x1", "0")
                                    .attr("x2", "0")
                                    .attr("y1", "0")
                                    .attr("y2", totalSeriesHeight)
                                    .attr("stroke", "#ccc")
                                    .attr("stroke-width", 1);
                            }

                            function createLegend () {
                                var legend = svg.append("g"),
                                    symbols = [],
                                    widths = [],
                                    totalWidth = 0;

                                for (var type in settings.symbolMap) {
                                    if (settings.symbolMap.hasOwnProperty(type)) {
                                        if (typesCount[type]) {
                                            symbols.push({
                                                value: type,
                                                symbol: settings.symbolMap[type],
                                                color: color(type)
                                            });
                                        }
                                    }
                                }

                                var items = legend.selectAll(".legendItem").data(symbols)
                                    .enter().append("g")
                                    .attr("class", "legendItem")
                                    .attr("data-type", function (d) {
                                        return d.symbol;
                                    });

                                items.append("path").attr("d", function (d) {
                                    return d3.svg.symbol().type(d.symbol).size(rectHeight * 5)();
                                })
                                    .attr("fill", "White")
                                    .attr("stroke-width", 1.5)
                                    .attr("stroke", function (d) {
                                        return d.color;
                                    });
                                items.append("text")
                                    .attr("transform", "translate(10, 4)")
                                    .text(function (d) {
                                        return d.value + " (" + (typesCount[d.value] || 0) + ")";
                                    });

                                legend.selectAll(".legendItem")[0].forEach(function (item) {
                                    var itemWidth = item.getBBox().width;
                                    widths.push(itemWidth);
                                    totalWidth += itemWidth;
                                });

                                items.attr("transform", function (d, i) {
                                    var translateX = 0;

                                    for (var widthIndex = 0; widthIndex < i; widthIndex++) {
                                        translateX += widths[widthIndex] + LEGEND_ITEM_PADDING;
                                    }

                                    return "translate(" + translateX + ", 0)";
                                });

                                legend.attr("transform", "translate(" + (width - totalWidth) / 2 + ", " +
                                    (totalSeriesHeight + axisHeight + TIME_DISPLAY_HEIGHT + LEGEND_MARGIN) + ")");
                            }

                            if (!data || !settings) {
                                return;
                            }

                            var typeField = settings.series.type || "type";

                            if (settings.series.isSingleSeries) {
                                data = [{_timespans: data}];
                            }

                            if (isInit) { // For when the data is updated only
                                var previousEarliestTimespanDate = earliestTimespanDate;

                                x.domain(timeMinMax);
                                addTimespans(data);
                                getEarliestTimespanDate();

                                if (earliestTimespanDate.valueOf() !== previousEarliestTimespanDate.valueOf()) {
                                    isPaging = false;
                                }

                                setLastRefreshTime();
                                return;
                            }

                            if (settings.data) {
                                if (angular.isArray(data)) {
                                    data = data[0][settings.data];
                                } else {
                                    data = data[settings.data];
                                }
                            }

                            element.css({
                                width: "100%"
                            });

                            totalSeriesHeight = seriesHeight * data.length;

                            //if (!element.height())
                            //element.css("height", totalSeriesHeight + axisHeight + TIME_DISPLAY_HEIGHT);

                            width = element.width();
                            height =
                                totalSeriesHeight + axisHeight + TIME_DISPLAY_HEIGHT + margin.bottom + LEGEND_MARGIN +
                                LEGEND_HEIGHT;
                            chartWidth = width - margin.left - margin.right;
                            svgHeight = height;

                            element.empty();
                            element.off();

                            svg = d3.select(element[0]).append("svg")
                                .attr("width", chartWidth + margin.left + margin.right)
                                .attr("height", svgHeight);

                            mask = svg.append('svg:defs').append('svg:clipPath')
                                .attr("id", "seriesClipPath")
                                .append("rect")
                                .attr("width", width)
                                .attr("height", totalSeriesHeight);

                            svg = svg.append("g")
                                .attr("transform", "translate(" + margin.left + "," + TIME_DISPLAY_HEIGHT + ")");

                            xField = settings.series.time;
                            colorField = settings.color;

                            createTimeDisplay();

                            createAxes();
                            createSeries();
                            createLegend();
                            //createTooltip();

                            svg[0][0].appendChild(labelsContainer[0][0]);

                            if (settings.onSelect) {
                                element.on("click", ".timespan", function (e) {
                                    scope.$apply(function () {
                                        var eventCopy = angular.copy(settings.onSelect);
                                        eventCopy.actionOptions.position = {top: e.clientY, left: e.clientX};
                                        events.triggerDashboardEvent(eventCopy,
                                            angular.extend({}, e.target.__data__.properties, e.target.__data__));
                                    });

                                    if (selectedTimespan) {
                                        selectedTimespan.classList.remove("selected");
                                    }

                                    selectedTimespan = e.target;
                                    selectedTimespan.classList.add("selected");
                                });
                            }

                            element.on("mouseenter.timeDisplay", function (e) {
                                if (e.offsetX < labelsWidth) {
                                    timeDisplay[0][0].style.removeProperty("display");
                                    timeDisplayVisible = true;
                                }
                                element.on("mouseleave.timeDisplay", onMouseLeave)
                                    .on("mousemove.timeDisplay", onMouseMove);
                            });

                            if (!isInit) {
                                isInit = true;
                            }
                        }

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

                            color = getColorScale(settings.colorMap);
                            drawChart();
                        });

                        $window.addEventListener("resize", onResize);
                        //scope.$on("resize", drawChart);

                    }
                };
            }]);
}());
