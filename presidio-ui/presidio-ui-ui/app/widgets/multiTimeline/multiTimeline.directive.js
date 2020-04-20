(function () {
    'use strict';

    angular.module('MultiTimelineWidget')
        .directive('multiTimeline',
        ["Chart", "$parse", "utils", "$timeout",
            function (Chart, $parse, utils, $timeout) {

                return {
                    template: '<div class="chart widget-multiTimeline"></div>',
                    restrict: 'E',
                    replace: true,
                    link: function postLink (scope, element, attrs) {

                        function draw () {
                            /* jshint validthis: true */
                            var self = this,
                                svg = this.dataSvg,
                                seriesContainer = svg.append("g").attr("class", "series-container"),
                                labelsContainer = svg.append("g").attr("class", "labels-container");


                            function createLabels () {
                                graph.elements.labels =
                                    labelsContainer.selectAll(".label").data(self.data, function (d) {
                                        return d[graph.settings.unique];
                                    });

                                graph.elements.labels.enter().append("svg:text")
                                    .text(function (d) {
                                        return d[self.settings.label];
                                    })
                                    .attr("text-anchor", "end")
                                    .attr("y", function (d, i) {
                                        return (i + 0.5) * graph.options.seriesHeight;
                                    })
                                    .attr("dy", ".3em");

                                graph.elements.labels.exit().remove();

                                labelsWidth = labelsContainer[0][0].getBoundingClientRect().width;
                                labelsContainer.attr("transform", "translate(" + labelsWidth + ", 0)");
                            }

                            function createSeries () {
                                graph.elements.series =
                                    seriesContainer.selectAll(".series").data(self.data, function (d) {
                                        return d[graph.settings.unique];
                                    });

                                var series = graph.elements.series.enter().append("svg:g")
                                    .attr("class", "series" + (onSeriesClick ? " selectable" : ""))
                                    .attr("transform", function (d, i) {
                                        return "translate(0, " + i * graph.options.seriesHeight + ")";
                                    });

                                series.append("svg:rect")
                                    .attr("class", "series-background")
                                    .attr("width", "100%")
                                    .attr("height", graph.options.seriesHeight);

                                series.append("svg:line")
                                    .attr("class", "series-divider")
                                    .attr("x1", 0)
                                    .attr("x2", "100%")
                                    .attr("y1", 0)
                                    .attr("y2", 0);

                                series.append("svg:rect")
                                    .attr("class", "series-session")
                                    .attr("x", function (series) {
                                        return d3.min(series.events, function (d) {
                                            return graph.scale.x(d[graph.settings.value]);
                                        });
                                    })
                                    .attr("y", (graph.options.seriesHeight - graph.options.sessionHeight) / 2)
                                    .attr("width", function (series) {
                                        return d3.max(series.events, function (d) {
                                                return graph.scale.x(d[graph.settings.value]);
                                            }) - d3.min(series.events, function (d) {
                                                return graph.scale.x(d[graph.settings.value]);
                                            });
                                    })
                                    .attr("height", graph.options.sessionHeight);

                                series.selectAll(".series-event")
                                    .data(function (series) {
                                        return series.events;
                                    }, function (d) {
                                        return d.id;
                                    })
                                    .enter().append("svg:circle")
                                    .attr("class", "series-event")
                                    .attr("r", graph.options.eventRadius)
                                    .attr("cx", function (d) {
                                        return graph.scale.x(d[graph.settings.value]);
                                    })
                                    .attr("cy", graph.options.seriesHeight / 2)
                                    .attr("data-tooltip", graph.settings.eventTooltip ? "eventTooltip" : null);

                                graph.elements.series.exit().remove();
                            }

                            function addEvents () {
                                if (onSeriesHover) {
                                    element.on("mouseenter", ".series", function (e) {
                                        $timeout.cancel(seriesMouseOutTimeout);
                                        scope.$apply(function () {
                                            onSeriesHover(scope, {series: e.currentTarget.__data__});
                                        });
                                    });
                                }

                                if (onSeriesMouseOut) {
                                    element.on("mouseleave", ".series", function () {
                                        seriesMouseOutTimeout = $timeout(function () {
                                            scope.$apply(function () {
                                                onSeriesMouseOut(scope,
                                                    {series: selectedSeries && selectedSeries.__data__});
                                            });
                                        }, 40);
                                    });
                                }

                                if (onSeriesClick) {
                                    element.on("click", ".series", function (e) {
                                        if (selectedSeries) {
                                            selectedSeries.classList.remove("selected");
                                            if (selectedSeries === e.currentTarget) {
                                                scope.$apply(function () {
                                                    onSeriesClick(scope, {series: selectedSeries = null});
                                                });
                                            } else {
                                                selectSeries(e.currentTarget);
                                            }
                                        } else {
                                            selectSeries(e.currentTarget);
                                        }
                                    });
                                }

                                function selectSeries (seriesElement) {
                                    scope.$apply(function () {
                                        selectedSeries = seriesElement;
                                        seriesElement.classList.add("selected");
                                        onSeriesClick(scope, {series: seriesElement.__data__});
                                    });
                                }
                            }

                            createLabels();

                            this.setScaleRanges({
                                x: [labelsWidth + this.options.seriesMargin + this.options.margins.left, this.width]
                            });

                            var domainExtent = {
                                x: [
                                    d3.min(self.data, function (series) {
                                        return d3.min(series.events, function (d) {
                                            return d[graph.settings.value];
                                        });
                                    }),
                                    d3.max(self.data, function (series) {
                                        return d3.max(series.events, function (d) {
                                            return d[graph.settings.value];
                                        });
                                    })
                                ]
                            };

                            if (domainExtent.x[0] === domainExtent.x[1]) {
                                var extentDate = domainExtent.x[0];

                                domainExtent.x[0] = utils.date.getMoment(extentDate).subtract(1, "days").toDate();
                                domainExtent.x[1] = utils.date.getMoment(extentDate).add(1, "days").toDate();
                            }

                            this.setScaleDomains(domainExtent);

                            this.color = this.getColorScale();
                            createSeries();
                            addEvents();

                        }

                        var defaultOptions = {
                                seriesHeight: 35,
                                sessionHeight: 3,
                                seriesMargin: 10,
                                eventRadius: 3
                            },
                            graph = new Chart(defaultOptions, draw),
                            labelsWidth,
                            onSeriesHover = attrs.onSeriesHover ? $parse(attrs.onSeriesHover) : null,
                            onSeriesMouseOut = attrs.onSeriesMouseOut ? $parse(attrs.onSeriesMouseOut) : null,
                            onSeriesClick = attrs.onSeriesClick ? $parse(attrs.onSeriesClick) : null,
                            selectedSeries,
                            seriesMouseOutTimeout;

                        graph.init(scope, element, attrs);
                        graph.onResize = function () {
                        };

                        graph.postRender = function () {
                            this.axes.x._element.attr("transform", "translate(" + this.options.margins.left + ", " +
                                (this.data.length * this.options.seriesHeight + this.options.margins.top) + ")");
                            this.axes.x._grid.attr("transform", "translate(" + this.options.margins.left + ", " +
                                (this.data.length * this.options.seriesHeight + this.options.margins.top) + ")");
                            this.axes.x._label.attr("transform", "translate(" + labelsWidth / 2 + ", 0)");
                        };

                        graph.preRender = function () {
                            element.css("height", this.data.length * this.options.seriesHeight + this.xAxisHeight +
                                this.options.margins.top + this.options.margins.bottom +
                                this.options.axisLabelsWidth.x);
                        };

                        graph.getTooltipText = function (d, type) {
                            return utils.strings.parseValue(graph.settings[type], d);
                        };

                    }
                };
            }]);
}());
