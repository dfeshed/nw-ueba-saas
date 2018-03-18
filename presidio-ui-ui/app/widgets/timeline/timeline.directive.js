(function () {
    'use strict';

    angular.module("TimelineWidget").directive("timeline",
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
                            timespanMaxWidth = 0.5,
                            TIME_DISPLAY_HEIGHT = 15;

                        var svg,
                            margin = {top: 0, right: 20, bottom: 0, left: 0},
                            width,
                            height,
                            chartWidth,
                            svgHeight,
                            lastRefreshTime = new Date(),
                            lastRefreshTimeLine,
                            tooltip,
                            tooltipRect,
                            timeDisplay,
                            timeDisplayText,
                            timeDisplayVisible,
                            hoveredDot,
                            labelsWidth,
                            labelsContainer,
                            x, xAxis,
                            xAxisElement,
                            selectedTimespan,
                            dataContainer,
                            timeMinMax,
                            seriesWidth,
                            timeDiff,
                            firstTimespanPosition,
                            earliestTimespanDate,
                            xField1,
                            xField2,
                            colorField,
                            totalSeriesHeight,
                            zoomBehavior,
                            seriesBackground;

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

                                dataContainer.attr("transform",
                                    "translate(" + translateX + ", 0) scale(" + d3.event.scale + ", 1)");
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
                                dataContainer = svg.insert("g", ".axis")
                                    .attr("class", "timespans-container")
                                    .attr("transform", "translate(" + labelsWidth + ", 0)");

                                lastRefreshTimeLine = dataContainer.append("line")
                                    .attr("transform", "translate(" + x(lastRefreshTime) + ", 0)")
                                    .attr("x1", 0)
                                    .attr("x2", 0)
                                    .attr("y1", 0)
                                    .attr("y2", totalSeriesHeight)
                                    .attr("stroke", "#ddd")
                                    .attr("stroke-width", 2);

                                var lines = svg.append("g");

                                seriesBackground = dataContainer.append("g")
                                    .attr("clip-path", "url(#seriesClipPath)")
                                    .call(zoomBehavior = d3.behavior.zoom().scaleExtent([0.1, 60])
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

                                lines.selectAll("line").data(data)
                                    .enter().append("line")
                                    .attr("transform", function (d, i) {
                                        return "translate(0, " + (i * seriesHeight) + ")";
                                    })
                                    .attr("x1", 0)
                                    .attr("x2", "100%")
                                    .attr("y1", 0)
                                    .attr("y2", 0)
                                    .attr("stroke-width", "1")
                                    .attr("stroke", "#ddd");

                                addTimespans(data);
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

                                    seriesContainer.selectAll(".timespan")
                                        .data(timespansData)
                                        .enter().append("rect")
                                        .attr("class", "eventRect")
                                        .attr("height", rectHeight)
                                        .attr("width", function (d) {
                                            if (!d[xField2] || !d[xField1]) {
                                                return timespanMaxWidth;
                                            }

                                            return Math.max(timespanMaxWidth,
                                                x(utils.date.getMoment(d[xField2]).toDate()) -
                                                x(utils.date.getMoment(d[xField1]).toDate()));
                                        })
                                        .attr("transform", function (d) {
                                            var position = x(utils.date.getMoment(d[xField1]).toDate());
                                            if (firstTimespanPosition === undefined ||
                                                position < firstTimespanPosition) {
                                                firstTimespanPosition = position;
                                            }

                                            return "translate(" + position + ", " + seriesMargin + ")";
                                        })
                                        .attr("fill", function (d) {
                                            if (!d[xField2] && settings.colorMap._missingData) {
                                                return settings.colorMap._missingData;
                                            }

                                            return settings.colorMap && colorField &&
                                                settings.colorMap[d[colorField]] || settings.colorMap._default;
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
                                                    return utils.date.getMoment(d3[xField1]).toDate();
                                                });
                                            }
                                            else {
                                                return utils.date.getMoment(d2[xField1]).toDate();
                                            }
                                        });
                                    });
                                }
                                else {
                                    earliestTimespanDate = d3.min(data, function (d) {
                                        return utils.date.getMoment(d[xField1]).toDate();
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

                                // labelsWidth = d3.max(labelsContainer.selectAll(".seriesLabel")[0], function (node) {
                                //     return node.clientWidth;
                                // });
                                labelsWidth=100;
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
                                                        return utils.date.getMoment(d3[xField2] ||
                                                            d3[xField1]).toDate();
                                                    });
                                                }
                                                else {
                                                    return utils.date.getMoment(d2[xField2] || d2[xField1]).toDate();
                                                }
                                            });
                                        });
                                    }
                                }
                                else {
                                    xDomain2 = d3.max(data, function (d) {
                                        return utils.date.getMoment(d[xField2] || d[xField1]).toDate();
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

                            function createTooltip () {
                                tooltip = svg.append("g")
                                    .attr("style", "display: none");

                                tooltipRect = tooltip.append("rect")
                                    .attr("width", 100)
                                    .attr("height", 50)
                                    .attr("fill", "#333333")
                                    .attr("opacity", "0.8");

                                element.on("mouseover", ".eventRect", function (e) {
                                    var dot = e.target;
                                    if (dot !== hoveredDot) {
                                        if (hoveredDot) {
                                            hoveredDot.style.fill = hoveredDot.color;
                                        }

                                        hoveredDot = dot;
                                        hoveredDot.color = hoveredDot.style.fill;
                                        hoveredDot.style.fill = "#333333";
                                    }

                                    setTooltipText(getTooltipText(dot));
                                    showTooltip();
                                    window.addEventListener("mousemove", tooltipMoveHandler);
                                });

                                element.on("mouseout", ".eventRect", function (e) {
                                    cancelSelection(e.target.__data__);
                                });

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

                            function cancelSelection () {
                                hideTooltip();
                            }

                            function getTooltipText (dot) {
                                var dotData = dot.__data__;
                                return utils.strings.parseValue(settings.tooltip, dotData);
                            }

                            function setTooltipText (text) {
                                var tspanTexts = text.split("|");

                                tooltip.selectAll("text").remove();
                                tooltip.selectAll("text")
                                    .data(tspanTexts)
                                    .enter()
                                    .append("text")
                                    .text(function (d) {
                                        return d;
                                    })
                                    .attr("fill", "White")
                                    .attr("font-size", "14px")
                                    .attr("transform", function (d, i) {
                                        return "translate(15, " + (20 + options.tooltipLineHeight * i) + ")";
                                    });

                                tooltipRect.attr("width", d3.max(tooltip[0][0].childNodes, function (d) {
                                        return d.clientWidth;
                                    }) + 30);
                                tooltipRect.attr("height", options.tooltipLineHeight * tspanTexts.length + 15);
                            }

                            function hideTooltip () {
                                tooltip[0][0].style.display = "none";
                                hoveredDot.style.fill = hoveredDot.color;
                                hoveredDot = null;
                            }

                            function showTooltip () {
                                clearTimeout(mouseOutTimeout);
                                tooltip[0][0].style.removeProperty("display");
                            }

                            function tooltipMoveHandler (e) {
                                var boundingClientRect = element[0].getBoundingClientRect();
                                setTooltipPosition({
                                    x: (e.x || e.clientX) - boundingClientRect.left,
                                    y: (e.y || e.clientY) - boundingClientRect.top
                                });
                            }

                            function setTooltipPosition (position) {
                                var tooltipPositionX = position.x + 5,
                                    tooltipPositionY = position.y - TIME_DISPLAY_HEIGHT,
                                    tooltipWidth = parseInt(tooltipRect[0][0].getAttribute("width")),
                                    tooltipHeight = parseInt(tooltipRect[0][0].getAttribute("height"));

                                if (tooltipPositionX + tooltipWidth > element[0].clientWidth) {
                                    tooltipPositionX = position.x - tooltipWidth - 5;
                                    if (tooltipPositionX < 0) {
                                        tooltipPositionX = 0;
                                    }
                                }

                                if (tooltipPositionY + tooltipHeight > element[0].clientHeight - 20) {
                                    tooltipPositionY = position.y - tooltipHeight - 5;
                                    if (tooltipPositionY < 0) {
                                        tooltipPositionY = 0;
                                    }
                                }

                                tooltip.attr("transform",
                                    "translate(" + tooltipPositionX + "," + tooltipPositionY + ")");
                            }

                            var mouseOutTimeout;


                            if (!data || !settings) {
                                return;
                            }

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
                                zoomBehavior.event(seriesBackground);
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
                                width: "auto"
                            });

                            element.css({
                                width: element.width() - 20
                            });

                            totalSeriesHeight = seriesHeight * data.length;

                            if (!element.height()) {
                                element.css("height", totalSeriesHeight + axisHeight + TIME_DISPLAY_HEIGHT);
                            }

                            width = element.width();
                            height = element.height();
                            chartWidth = width - margin.left - margin.right;
                            svgHeight = height - margin.top - margin.bottom;

                            element.empty();
                            element.off();

                            svg = d3.select(element[0]).append("svg")
                                .attr("width", chartWidth + margin.left + margin.right)
                                .attr("height", svgHeight);

                            svg.append('svg:defs').append('svg:clipPath')
                                .attr("id", "seriesClipPath")
                                .append("rect")
                                .attr("width", width)
                                .attr("height", totalSeriesHeight);

                            svg = svg.append("g")
                                .attr("transform", "translate(" + margin.left + "," + TIME_DISPLAY_HEIGHT + ")");

                            xField1 = settings.series.timeStart;
                            xField2 = settings.series.timeEnd;
                            colorField = settings.color;

                            createTimeDisplay();

                            createAxes();
                            createSeries();
                            createTooltip();

                            svg[0][0].appendChild(labelsContainer[0][0]);

                            if (settings.onSelect) {
                                element.on("click", ".eventRect", function (e) {
                                    events.triggerDashboardEvent(settings.onSelect, e.target.__data__);

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

                            drawChart();
                        });

                        $window.addEventListener("resize", drawChart);
                        scope.$on("resize", drawChart);

                    }
                };
            }]);
}());
