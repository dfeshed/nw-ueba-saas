(function () {
    'use strict';

    angular.module("Chart", ["Colors", "Utils"]).factory('Chart', [
        "$injector", "$parse", "colors", "utils", "debounce", "config",
        function ($injector, $parse, colors, utils, debounce, config) {

            var constructor;

            function Chart ($rootScope, defaultOptions, draw, $parse, colors, utils) {
                this.$rootScope = $rootScope;
                this.$parse = $parse;
                this.colors = colors;
                this.utils = utils;
                this.defaultOptions = defaultOptions || {};
                this._draw = draw;
            }

            Chart.prototype = {
                timeFormat: {
                    days: d3.time.format.multi([
                        ["%m/%d", function (d) {
                            return d.getFullYear() === utils.date.getMoment('now').year();
                        }],
                        ["%m/%d/%y", function () {
                            return true;
                        }]
                    ]),
                    "default": d3.time.format.multi([
                        [".%L", function (d) {
                            return d.getMilliseconds();
                        }],
                        [":%S", function (d) {
                            return d.getSeconds();
                        }],
                        ["%H:%M", function (d) {
                            return d.getMinutes();
                        }],
                        ["%H:00", function (d) {
                            return d.getHours();
                        }],
                        ["%a %d", function (d) {
                            return d.getDay() && d.getDate() !== 1;
                        }],
                        ["%b %d", function (d) {
                            return d.getDate() !== 1;
                        }],
                        ["%B", function (d) {
                            return d.getMonth();
                        }],
                        ["%Y", function () {
                            return true;
                        }]
                    ])
                },
                createBrush: function () {

                    function brushstart () {
                        self.svg.classed("selecting", true);
                    }

                    function brushmove () {
                        self.$rootScope.$apply(function () {
                            onBrush(self.scope, {extent: d3.event.target.extent()});
                        });
                    }

                    function brushend () {
                        self.svg.classed("selecting", !d3.event.target.empty());
                    }

                    var self = this,
                        onBrush = this.attrs.onBrush ? this.$parse(this.attrs.onBrush) : null;

                    this.dataSvg.append("g")
                        .attr("class", "brush")
                        .call(d3.svg.brush().x(this.scale.x)
                            .on("brushstart", brushstart)
                            .on("brush", onBrush ? brushmove : null)
                            .on("brushend", brushend))
                        .selectAll("rect")
                        .attr("height", self.dataHeight);

                },
                createScales: function () {
                    if (this.scale) {
                        return;
                    }

                    var self = this;
                    this.scale = {};

                    var d3timeScale = config.alwaysUtc ? d3.time.scale.utc : d3.time.scale;

                    if (this.settings.scales.x) {
                        this.scale.x = this.settings.scales.x.type === "time" ? d3timeScale() : d3.scale.linear();

                        this.scale.x.range([0, this.width - this.options.axisWidth]);
                        if (this.settings.scales.x.domain) {
                            this.scale.x.domain(this.settings.scales.x.domain);
                        }

                        if (this.attrs.graphDomainX) {
                            this.unwatchers.push(this.scope.$watch(this.attrs.graphDomainX, function (value) {
                                if (value) {
                                    if (self.scale.x) {
                                        self.scale.x.domain(value);
                                    }
                                    if (self.onUpdateDomain) {
                                        self.onUpdateDomain.call(this, {x: value});
                                    }

                                    if (self.axes.x) {
                                        self.axes.x._element.call(self.axes.x);
                                        self.axes.x._grid.call(self.getGridAxis("x"));
                                    }
                                }
                            }));
                        }
                    }

                    if (this.settings.scales.y) {
                        this.scale.y = this.settings.scales.y.type === "time" ? d3timeScale() : d3.scale.linear();
                        this.scale.y.range([this.height - this.options.axisWidth, 0]);

                        this.scale.y.reverseScale =
                            this.settings.scales.y.type === "time" ? d3timeScale() : d3.scale.linear();
                        this.scale.y.reverseScale.range([0, this.height - this.options.axisWidth]);

                        if (this.settings.scales.y.domain) {
                            this.scale.y.domain(this.settings.scales.y.domain);
                            this.scale.y.reverseScale.domain(this.settings.scales.y.domain);
                        }

                        if (this.attrs.graphDomainY) {
                            this.unwatchers.push(this.scope.$watch(this.attrs.graphDomainY, function (value) {
                                if (value) {
                                    if (self.scale.x) {
                                        self.scale.x.domain(value);
                                    }
                                    if (self.onUpdateDomain) {
                                        self.onUpdateDomain.call(this, {y: value});
                                    }
                                }
                            }));
                        }
                    }

                    this.setScaleRanges = function (ranges) {
                        if (!ranges) {
                            return;
                        }

                        if (ranges.x && this.scale.x) {
                            this.scale.x.range(ranges.x);
                        }

                        if (ranges.y && this.scale.y) {
                            this.scale.y.range(ranges.y);
                            this.scale.y.reverseScale.range([ranges.y[1], ranges.y[0]]);
                        }
                    };

                    this.setScaleDomains = function (domains) {
                        if (!domains) {
                            return;
                        }

                        if (domains.x && this.scale.x) {
                            this.scale.x.domain(domains.x);
                        }

                        if (domains.y && this.scale.y) {
                            this.scale.y.domain(domains.y);
                            this.scale.y.reverseScale.domain(domains.y);
                        }
                    };
                },
                createAxes: function () {
                    if (!this.settings.axes) {
                        return;
                    }

                    var self = this;
                    this.axes = {};

                    var d3timeFormat = config.alwaysUtc ? d3.time.format.utc : d3.time.format;

                    var axes = this.svg.append("g").attr("class", "axes"),
                        marginLeft = this.options.margins.left + (this.settings.axes.y ? this.options.axisWidth : 0),
                        grids = this.svg.insert("g", ".graph-data").attr("class", "grids");

                    if (this.settings.axes.x) {
                        this.axes.x = d3.svg.axis()
                            .scale(this.scale.x)
                            .orient("bottom");

                        if (this.settings.axes.x.ticks) {
                            this.axes.x.ticks(d3.time[this.settings.axes.x.ticks.unit],
                                this.settings.axes.x.ticks.interval);
                        } else {
                            //When letting D3 create the X-axis ticks automatically,
                            // we may get duplicate entries. so we remove them.
                            this.axes.x.tickValues(this.removeDuplicateTicks(this.scale.x.ticks(), this.dateFormatter));
                        }

                        if (this.settings.axes.x.tickFormat) {
                            var xScaleTickFormatter = this.settings.axes.x.type === "time" ? d3timeFormat : d3.format,
                                xScaleTickFormat = xScaleTickFormatter(this.settings.axes.x.tickFormat);

                            this.axes.x.tickFormat(xScaleTickFormat);
                        }
                        else if (this.settings.axes.x.type === "time") {
                            var timeFormat = this.timeFormat[this.settings.axes.x.timeFormat] ||
                                this.timeFormat.default;
                            this.axes.x.tickFormat(timeFormat);
                        }

                        this.axes.x._grid = grids.append("g")
                            .attr("class", "grid")
                            .attr("transform", "translate(" + (marginLeft + 1) + ", " + this.options.margins.top + ")")
                            .call(self.getGridAxis("x"));

                        this.axes.x._element = axes.append("g")
                            .attr("class", "x axis")
                            .attr("transform",
                            "translate(" + (marginLeft + 1) + "," + (this.height - this.options.margins.top) + ")")
                            .call(this.axes.x);

                        if (this.settings.axes.x.label) {
                            this.axes.x._label = this.axes.x._element.append("text")
                                .attr("class", "graph-axis-label")
                                .attr("x", this.dataWidth / 2)
                                .attr("dy", self.options.axisLabelsWidth.x + 10)
                                .style("text-anchor", "middle")
                                .text(this.settings.axes.x.label || "");
                        }
                    }

                    if (this.settings.axes.y) {
                        var yScaleTickFormatter, yScaleTickFormat;
                        this.axes.y = d3.svg.axis()
                            .scale(this.scale.y)
                            .orient("left")
                            .ticks(this.settings.axes.y.ticks);

                        if (this.settings.axes.y.tickFormat) {
                            if (~this.settings.axes.y.tickFormat.indexOf("{{")) {
                                var yTickFormat = this.settings.axes.y.tickFormat;
                                yScaleTickFormat = function (d) {
                                    return self.utils.strings.parseValue(yTickFormat, {value: d});
                                };
                            }
                            else {
                                yScaleTickFormatter = this.settings.axes.y.type === "time" ? d3timeFormat :
                                    d3.format;
                                yScaleTickFormat = yScaleTickFormatter(this.settings.axes.y.tickFormat);
                            }

                            this.axes.y.tickFormat(yScaleTickFormat);
                        }

                        this.axes.y._grid = grids.append("g")
                            .attr("class", "grid")
                            .attr("transform", "translate(" + marginLeft + ", " + this.options.margins.top + ")")
                            .call(self.getGridAxis("y"));

                        this.axes.y._element = axes.append("g")
                            .attr("class", "y axis")
                            .attr("transform", "translate(" + marginLeft + ", " + this.options.margins.top + ")")
                            .call(this.axes.y);

                        if (this.settings.axes.y.label) {
                            this.axes.y._label = this.axes.y._element.append("text")
                                .attr("class", "graph-axis-label")
                                .attr("transform", "rotate(-90)")
                                .attr("x", this.dataHeight / -2)
                                .attr("dy", self.options.axisLabelsWidth.y * -1 - 5)
                                .style("text-anchor", "middle")
                                .text(this.settings.axes.y.label || "");
                        }
                    }
                },
                _createLegend: function () {
                    var circleRadius = 8,
                        margin = 12,
                        textMargin = margin + 4;

                    this.elements.legend = this.svg.append("g")
                        .attr("class", "legend")
                        .attr("transform", "translate(0, " + this.options.margins.top + ")");

                    this.elements.legendItems = this.elements.legend.selectAll(".legend-item").data(this.legendData)
                        .enter().append("g")
                        .attr("transform", function (d, i) {
                            return "translate(0, " + (margin + circleRadius) * 2 * i + ")";
                        })
                        .attr("class", "legend-item");

                    var legendItems = this.elements.legendItems.append("g")
                        .attr("class", "legend-item-bullet")
                        .style("fill", function (d) {
                            return d.color;
                        });

                    legendItems.append("path").attr("d", function (d) {
                        return d3.svg.symbol().type(d.symbol).size(circleRadius * 10)();
                    })
                        .attr("fill", function (d) {
                            return d.color || "steelblue";
                        });

                    this.elements.legendItems.append("text")
                        .attr("class", "legend-item-text")
                        .text(function (d) {
                            return d.text;
                        })
                        .attr("transform", "translate(" + textMargin + ", 0)")
                        .attr("dy", ".3em");

                    if (this.settings.legend.position === "right") {
                        var legendWidth = this.elements.legend[0][0].getBoundingClientRect().width;
                        this.elements.legend._width = legendWidth + margin;
                        this.elements.legend.attr("transform",
                            "translate(" + (this.width - this.elements.legend._width) + ", " +
                            (this.options.margins.top + margin) + ")");
                    }
                },
                getGridAxis: function (xy) {
                    var oppositeScale = this.scale[xy === "x" ? "y" : "x"];
                    var oppositeRange = oppositeScale && oppositeScale.range() || [0],
                        axis = d3.svg.axis().scale(this.scale[xy]).orient(xy === "x" ? "top" : "left")
                            .tickSize(-(Math.max.apply(this, oppositeRange)), 0, 0)
                            .tickFormat("")
                            .ticks(this.settings.axes[xy].ticks);

                    if (this.settings.axes[xy] && this.settings.axes[xy].ticks) {
                        axis.ticks(d3.time[this.settings.axes[xy].ticks.unit], this.settings.scales[xy].interval);
                    }

                    return axis;
                },
                createTooltip: function () {
                    var self = this;

                    var tooltip = this.tooltip = this.svg.append("g")
                        .attr("class", "graph-tooltip")
                        .attr("style", "display: none");

                    var tooltipBackground = tooltip.append("rect")
                        .attr("width", 100)
                        .attr("height", 30)
                        .attr("fill", "rgba(0,0,0,.8")
                        .attr("rx", 4)
                        .attr("ry", 4);

                    var mouseOutTimeout,
                        elementWidth,
                        elementHeight,
                        elementBoundingRect;

                    if (this.getTooltipText) {
                        this.element.on("mouseover", "[data-tooltip]", function (e) {
                            var tooltipData = e.target.__data__,
                                tooltipText = self.getTooltipText(tooltipData,
                                    $(e.target).closest("[data-tooltip]").attr("data-tooltip"));

                            if (tooltipText) {
                                elementBoundingRect = self.svg[0][0].getBoundingClientRect();
                                elementWidth = elementBoundingRect.width;
                                elementHeight = elementBoundingRect.height;

                                setTooltipText(tooltipText);

                                showTooltip(elementBoundingRect);
                                window.addEventListener("mousemove", tooltipMoveHandler);
                            }
                        });

                        this.element.on("mouseout", "[data-tooltip]", function () {
                            hideTooltip();
                        });
                    }

                    function setTooltipText (text) {
                        var tspanTexts = text.split("|");

                        tooltip.selectAll("text").remove();
                        var textElement = tooltip.selectAll("text")
                            .data(tspanTexts)
                            .enter()
                            .append("text");

                        textElement.text(function (d) {
                            return d;
                        })
                            .attr("fill", "White")
                            .attr("font-size", "14px")
                            .attr("transform", function (d, i) {
                                return "translate(15, " + (20 * (i + 1)) + ")";
                            });

                        tooltipBackground.attr("width", textElement[0][0].getBoundingClientRect().width + 30);
                        tooltipBackground.attr("height", 20 * tspanTexts.length + 10);
                    }

                    function hideTooltip () {
                        tooltip[0][0].style.display = "none";
                    }

                    function showTooltip () {
                        clearTimeout(mouseOutTimeout);
                        tooltip[0][0].style.removeProperty("display");
                    }

                    function tooltipMoveHandler (e) {
                        setTooltipPosition({
                            x: (e.x || e.clientX) - elementBoundingRect.left,
                            y: (e.y || e.clientY) - elementBoundingRect.top
                        });
                    }

                    function setTooltipPosition (position) {
                        var tooltipPositionX = position.x + 5,
                            tooltipPositionY = position.y,
                            tooltipWidth = parseInt(tooltipBackground[0][0].getAttribute("width")),
                            tooltipHeight = parseInt(tooltipBackground[0][0].getAttribute("height"));

                        if (tooltipPositionX + tooltipWidth > elementWidth) {
                            tooltipPositionX = position.x - tooltipWidth - 5;
                            if (tooltipPositionX < 0) {
                                tooltipPositionX = 0;
                            }
                        }

                        if (tooltipPositionY + tooltipHeight > elementHeight - 20) {
                            tooltipPositionY = position.y - tooltipHeight - 5;
                            if (tooltipPositionY < 0) {
                                tooltipPositionY = 0;
                            }
                        }

                        tooltip.attr("transform", "translate(" + tooltipPositionX + "," + tooltipPositionY + ")");
                    }
                },
                draw: function () {
                    if (this._draw() !== false) {
                        if (this.settings.axes) {
                            this.createAxes();
                        }

                        if (this.createLegend) {
                            this.createLegend();
                        }

                        this.createTooltip();

                        if (this.postRender) {
                            this.postRender();
                        }
                    }
                },
                getColorScale: function (colorSettings) {
                    return this.colors.getScale(colorSettings || this.settings.color);
                },
                getData: function () {
                    if (this.graphFilter) {
                        if (this.filteredData) {
                            return this.filteredData;
                        }

                        this.filteredData = this.graphFilter(this.data);
                        return this.filteredData;
                    }

                    return this.data;
                },
                init: function (scope, element, attrs) {
                    var self = this,
                        defaults = {
                            axisWidth: 20,
                            axisLabelsWidth: {y: 25, x: 25},
                            margins: {top: 10, left: 20, right: 20, bottom: 10}
                        };

                    this.element = element;
                    this.scope = scope;
                    this.attrs = attrs;
                    this.unwatchers = [];

                    scope.$on("$destroy", function () {
                        if (self.dataSvg) {
                            self.dataSvg.empty();
                            self.dataSvg.remove();
                            self.dataSvg = null; // major memory performance improvement.
                        }

                        element.off();
                        element.empty();

                        self.unwatchers.forEach(function (unwatcher) {
                            unwatcher();
                        });
                        self.unwatchers = [];
                    });

                    this.unwatchers.push(scope.$on("resize", this.resize.bind(this)));

                    if (attrs.graphFilter) {
                        self.unwatchers.push(scope.$watch(attrs.graphFilter, function (value) {
                            var previousData = self.data;

                            self.graphFilter = value;
                            self.filteredData = null;
                            self.refresh(previousData);
                        }));
                    }

                    self.unwatchers.push(scope.$watch(attrs.ngModel, function (chartData) {
                        var previousData = self.data;

                        self.data = chartData;
                        if (self.formatData) {
                            self.formattedData = self.formatData(chartData);
                        }

                        self.refresh(previousData);
                    }));

                    self.unwatchers.push(scope.$watch(attrs.graphSettings, function (value) {
                        if (value) {
                            self.scale = null;
                            self.settings = value;
                            self.options = angular.extend({}, defaults, self.defaultOptions, self.settings.options);
                            self.render();
                        }
                    }));
                },
                refresh: function (previousData) {
                    if (this.loaded && this.update) {
                        this.update(this.getData(), previousData);
                    } else {
                        this.loaded = this.render();
                    }
                },
                render: function () {
                    var self = this;

                    this.element.empty();
                    this.element.off();
                    this.element.addClass("widget-graph");

                    if (!this.settings || !this.data || !this._draw) {
                        return false;
                    }

                    this.elements = {};

                    this.svg = d3.select(this.element[0])
                        .append("svg:svg")
                        .attr("class", "graph")
                        .attr("width", this.options.width || "100%")
                        .attr("height", this.options.height || "100%");

                    // The SVG has no height and width if it's hidden, which happens when transitioning widget state
                    // due to no data and then data. In this case, wait and try again:
                    if (!this.svg[0][0].clientHeight && !this.svg[0][0].clientWidth) {
                        if (this.renderRetry === 5) {
                            this.renderRetry = 0;
                            return;
                        }
                        else {
                            if (this.renderRetry === undefined) {
                                this.renderRetry = 0;
                            } else {
                                this.renderRetry++;
                            }

                            setTimeout(this.render.bind(this), 50);
                        }

                        return;
                    }
                    else if (this.renderRetry) {
                        this.renderRetry = 0;
                    }

                    if (this.preRender) {
                        this.preRender();
                    }

                    this.width = this.element.innerWidth();
                    this.height = this.element.innerHeight();

                    if (this.settings.legend && this.legendData) {
                        this._createLegend();

                        if (this.settings.legend.position === "right" || this.settings.legend.position === "left") {
                            var legendWidth = this.elements.legend[0][0].getBoundingClientRect().width;
                            this.width -= legendWidth;
                        }

                        // Still no support for top or bottom legend...
                    }

                    if (typeof(this.options.margins) === "number") {
                        this.options.margins = {
                            top: this.options.margins,
                            bottom: this.options.margins,
                            left: this.options.margins,
                            right: this.options.margins
                        };
                    }

                    this.width -= this.options.margins.left + this.options.margins.right;
                    this.height -= this.options.margins.top + this.options.margins.bottom;

                    if (this.settings.axes) {
                        if (this.settings.axes.y && this.settings.axes.y.label) {
                            this.width -= this.options.axisLabelsWidth.y;
                            if (!this.loaded) {
                                this.options.margins.left += this.options.axisLabelsWidth.y - 5;
                            }
                        }

                        if (this.settings.axes.x && this.settings.axes.x.label) {
                            this.height -= this.options.axisLabelsWidth.x;
                        }
                    }

                    this.dataWidth = this.width;
                    this.dataHeight = this.height;

                    this.dataSvg = this.svg.append("g").attr("class", "graph-data");

                    if (this.settings.axes) {
                        if (this.settings.axes.x) {
                            this.dataHeight -= this.options.axisWidth;
                        }
                        if (this.settings.axes.y) {
                            this.dataWidth -= this.options.axisWidth;
                        }

                        this.dataSvg.attr("transform", "translate(" +
                            ((this.settings.axes.y ? this.options.axisWidth + 1 : 0) + this.options.margins.left) +
                            ", " + this.options.margins.top + ")");
                    }
                    else {
                        this.dataSvg.attr("transform",
                            "translate(" + this.options.margins.left + ", " + this.options.margins.top + ")");
                    }

                    if (this.settings.shapes && this.settings.shapes.map) {
                        this.dataHeight -= 20;
                    }

                    if (this.settings.scales) {
                        this.createScales();
                    }

                    this.draw();

                    if (this.settings.onSelect) {
                        this.element.on("click", "[data-selectable]", function (e) {
                            self.scope.$apply(function () {
                                var event = angular.copy(self.settings.onSelect);
                                event.actionOptions.event = e;
                                event.actionOptions.position = {
                                    top: e.pageY,
                                    left: e.pageX
                                };

                                self.scope.$emit("dashboardEvent", {
                                    event: event,
                                    data: e.target.__data__,
                                    params: self.scope.widget.getState ? self.scope.widget.getState() :
                                        self.scope.getWidgetParams()
                                });
                            });
                        });
                    }

                    if (this.settings.brush) {
                        this.createBrush();
                    }

                    var selfResize = this.resize.bind(this);
                    // Debounce the handler to prevent overload
                    var resizeHandler = debounce(100, function () {
                        self.$rootScope.safeApply(selfResize);
                    });

                    window.addEventListener("resize", resizeHandler);

                    this.loaded = true;

                    return true;
                },
                resize: function () {
                    // Make sure the chart is visible
                    if (!angular.element(this.element).is(':visible')) {
                        return;
                    }

                    var legendWidth = this.elements.legend && this.elements.legend._width;

                    this.width = this.element.innerWidth();
                    this.height = this.element.innerHeight();

                    this.width -= this.options.margins.left + this.options.margins.right;
                    this.height -= this.options.margins.top + this.options.margins.bottom;

                    if (this.settings.axes) {
                        if (this.settings.axes.y && this.settings.axes.y.label) {
                            this.width -= this.options.axisLabelsWidth.y;
                        }

                        if (this.settings.axes.x && this.settings.axes.x.label) {
                            this.height -= this.options.axisLabelsWidth.x;
                        }
                    }

                    this.dataWidth = this.width;
                    this.dataHeight = this.height;

                    if (this.settings.axes) {
                        if (this.settings.axes.x) {
                            this.dataHeight -= this.options.axisWidth;
                        }
                        if (this.settings.axes.y) {
                            this.dataWidth -= this.options.axisWidth;
                        }
                    }

                    if (this.settings.scales) {
                        if (this.settings.scales.x) {
                            this.scale.x.range([0, this.width - this.options.axisWidth -
                            (legendWidth ? legendWidth - this.options.margins.right : 0)]);
                        }

                        if (this.settings.scales.y) {
                            this.scale.y.range([this.height - this.options.axisWidth, 0]);
                            this.scale.y.reverseScale.range([0, this.height - this.options.axisWidth]);
                        }
                    }

                    if (legendWidth) {
                        //this.dataWidth -= this.elements.legend._width;
                        this.elements.legend.attr("transform", "translate(" +
                            (this.svg[0][0].getBoundingClientRect().width - this.elements.legend._width) + ", " +
                            (this.options.margins.top + 6) + ")");
                        this.dataWidth -= this.elements.legend._width;
                    }

                    if (this.onResize) {
                        this.onResize();
                    }

                    if (this.settings.axes) {
                        var marginLeft = this.options.margins.left +
                            (this.settings.axes.y ? this.options.axisWidth : 0);

                        if (this.axes.x) {

                            this.axes.x._grid.attr("transform",
                                "translate(" + marginLeft + ", " + this.options.margins.top + ")")
                                .call(this.getGridAxis("x"));

                            this.axes.x._element.attr("transform",
                                "translate(" + marginLeft + "," + (this.height - this.options.margins.top) + ")")
                                .call(this.axes.x);

                            if (this.settings.axes.x.label) {
                                this.axes.x._label.attr("x", this.dataWidth / 2);
                            }
                        }

                        if (this.axes.y) {
                            this.axes.y._grid.attr("transform",
                                "translate(" + marginLeft + ", " + this.options.margins.top + ")")
                                .call(this.getGridAxis("y"));

                            this.axes.y._element.attr("transform",
                                "translate(" + marginLeft + ", " + this.options.margins.top + ")")
                                .call(this.axes.y);
                        }
                    }
                },
                get yAxisWidth () {
                    return 20;
                },
                get xAxisHeight () {
                    return 16;
                },

                /**
                 * remove duplicate entries from the X axis in graphs. this is when we want the ability to change time
                 * scale and to let the scale change accordingle without creating any duplicates.
                 * @param ticks
                 * @param formatter
                 * @returns array of non-duplicated ticks
                 */
                removeDuplicateTicks: function (ticks, formatter) {
                    if (ticks === undefined || ticks === null || !angular.isArray(ticks)) {
                        return ticks;
                    }
                    var nonDuplicateTickValues = [];
                    for (var i = 0; i < ticks.length; i++) {
                        var tickVal = ticks[i];
                        if (!this.tickAlreadyExists(nonDuplicateTickValues, tickVal, formatter)) {
                            nonDuplicateTickValues.push(tickVal);
                        }
                    }
                    return nonDuplicateTickValues;
                },
                /**
                 * Helper function that finds wheter an entry already entered into the list of unique entries
                 * @param nonDuplicateTickValues
                 * @param tickValIn
                 * @param formatter
                 * @returns {boolean}
                 */
                tickAlreadyExists: function (nonDuplicateTickValues, tickValIn, formatter) {
                    for (var i = 0; i < nonDuplicateTickValues.length; i++) {
                        var t = nonDuplicateTickValues[i];
                        var formattedTickValIn = formatter(tickValIn);
                        var formattedTickVal = formatter(t);
                        if (formattedTickValIn === formattedTickVal) {
                            return true;
                        }

                    }
                    return false;
                },

                dateFormatter: function (d) {
                    var d3timeFormat = config.alwaysUtc ? d3.time.format.utc : d3.time.format;
                    var format = d3timeFormat("%d-%b-%y");
                    return format(d);
                }

            };
            constructor = function (options, draw) {
                return $injector.instantiate(Chart, {
                    defaultOptions: options,
                    draw: draw,
                    $parse: $parse,
                    colors: colors,
                    utils: utils
                });
            };

            return constructor;
        }]);
}());
