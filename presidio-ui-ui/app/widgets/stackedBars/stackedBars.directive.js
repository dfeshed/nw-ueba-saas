(function () {
    'use strict';

    angular.module('StackedBarsWidget')
        .directive('stackedBars', ["Chart", "utils", function (Chart, utils) {
            return {
                template: '<div class="chart widget-bars"></div>',
                restrict: 'E',
                replace: true,
                link: function postLink (scope, element, attrs) {

                    function draw () {
                        /* jshint validthis: true */
                        var self = this,
                            labelsContainer,
                            labels,
                            labelsWidth,
                            color = this.getColorScale(this.settings.color);

                        function createLabels () {
                            //var series = self.data.map(function (d) {
                            //        return d.name;
                            //    }),
                            var labelsData = labelsContainer.selectAll(".label").data(labels);

                            labelsData.enter().append("svg:text")
                                .text(function (d) {
                                    var text = d;
                                    if (text.length > 50) {
                                        text = text.substr(0, 50) + "...";
                                    }

                                    return text;
                                })
                                .attr("text-anchor", "end")
                                .attr("y", function (d, i) {
                                    return yScale(d) + self.options.barHeight / 2;
                                })
                                .attr("dy", ".3em")
                                .attr("transform", isHorizontal ? "rotate(-90)" : null);

                            labelsData.exit().remove();

                            labelsWidth = labelsContainer[0][0].getBoundingClientRect().width;
                            labelsContainer.attr("transform", "translate(" + (isHorizontal ?
                                "0, " + (self.height - labelsWidth) : labelsWidth + ", 0") + ")");
                        }

                        function createScales () {
                            var xMax = d3.max(data, function (group) {
                                    return d3.max(group, function (d) {
                                        return d.x + d.x0;
                                    });
                                }),
                                domain = [0, xMax];

                            xScale = d3.scale.linear()
                                .domain([0, xMax])
                                .rangeRound([0, self.width - labelsWidth - labelsBarsDistance]);

                            var seriesBarWidths = {};

                            graph.data.forEach(function (series) {
                                series.data.forEach(function (bar) {
                                    if (!bar.value) {
                                        return true;
                                    }

                                    var scaledValue = xScale(bar.value);

                                    if (graph.options.minBarWidth > scaledValue) {
                                        scaledValue -= graph.options.minBarWidth - scaledValue;
                                    }

                                    if (seriesBarWidths[bar.label] === undefined) {
                                        seriesBarWidths[bar.label] = scaledValue;
                                    } else {
                                        seriesBarWidths[bar.label] += scaledValue;
                                    }
                                });
                            });

                            for (var label in seriesBarWidths) {
                                if (seriesBarWidths.hasOwnProperty(label)) {
                                    xScales[label] = d3.scale.linear()
                                        .domain(domain)
                                        .rangeRound([0, seriesBarWidths[label]]);
                                }
                            }
                        }

                        function createBars () {
                            barsMargin = labelsWidth + labelsBarsDistance;
                            barsWidth = (isHorizontal ? self.height : self.width) - barsMargin;

                            barsContainer.attr("transform", "translate(" + barsMargin + ", 0)");

                            d3.scale.category10();
                            var groups = barsContainer.selectAll('g')
                                .data(data)
                                .enter()
                                .append('g');

                            createScales();

                            graph.elements.rects = groups.selectAll('rect')
                                .data(function (d) {
                                    return d;
                                })
                                .enter()
                                .append('rect')
                                .attr("class", "stacked-bar")
                                .attr('x', function (d, i) {
                                    return xScales[d.y](d.x0);
                                })
                                .attr("data-tooltip", graph.settings.tooltipText ? "" : null)
                                .attr('y', function (d, i) {
                                    return yScale(d.y);
                                })
                                .style("fill", function (d) {
                                    return color(d.series);
                                })
                                .attr('height', self.options.barHeight)
                                .attr('width', function (d) {
                                    var value = d.x;
                                    if (!value) {
                                        return 0;
                                    }

                                    var width = Math.max(xScales[d.y](value) - graph.options.rectMargin,
                                        graph.options.minBarWidth);

                                    /*
                                     if (width < self.options.minBarWidth){
                                     var y = this.y.baseVal.valueAsString;
                                     if (overflowWidth[y] === undefined)
                                     overflowWidth[y] = 0;

                                     overflowWidth[y] += self.options.minBarWidth - width;
                                     width = self.options.minBarWidth;
                                     }
                                     */
                                    return width;
                                });

                            /*
                             graph.elements.rects.each(function(){
                             var currentWidth = this.width.baseVal.value,
                             y = this.y.baseVal.valueAsString;

                             if (currentWidth > overflowWidth[y]){
                             this.setAttribute("width", (currentWidth - overflowWidth[y]) + "px");
                             }
                             });

                             graph.elements.rects.each(function(){
                             var currentX = this.x.baseVal.value,
                             y = this.y.baseVal.valueAsString;

                             if (currentWidth > overflowWidth[y]){
                             this.setAttribute("width", (currentWidth - overflowWidth[y]) + "px");
                             }
                             });
                             */
                        }

                        graph.barsAndLabelsContainer = this.dataSvg.append("svg:g");
                        labelsContainer = graph.barsAndLabelsContainer.append("g").attr("class", "labels");
                        barsContainer = graph.barsAndLabelsContainer.append("g").attr("class", "bars");

                        data = self.data.map(function (d) {
                            return d.data.map(function (o, i) {
                                // Structure it so that your numeric
                                // axis (the stacked amount) is y
                                return {
                                    y: o.value,
                                    x: o.label,
                                    series: d.name,
                                    rawData: o.rawData
                                };
                            });
                        });

                        stack(data);

                        data = data.map(function (group) {
                            return group.map(function (d) {
                                // Invert the x and y values, and y0 becomes x0
                                return {
                                    x: d.y,
                                    y: d.x,
                                    x0: d.y0,
                                    series: d.series,
                                    rawData: d.rawData
                                };
                            });
                        });

                        labels = data[0].map(function (d) {
                            return d.y;
                        });

                        yScale = function (d) {
                            return labels.indexOf(d) * (self.options.barHeight + self.options.barMargin);
                        };

                        createLabels();
                        createBars();
                        setSvgHeight();

                    }

                    function setSvgHeight () {
                        graph.svg.attr("height",
                            (barsContainer[0][0].getBoundingClientRect().height + graph.options.margins.top +
                            graph.options.margins.bottom) + "px");
                    }

                    var defaultOptions = {
                            barHeight: 30,
                            barMargin: 10,
                            barsPadding: 20,
                            scrollBarWidth: 8,
                            rectMargin: 2,
                            minBarWidth: 3
                        },
                        labelsBarsDistance = 7,
                        barsMargin,
                        barsWidth,
                        graph = new Chart(defaultOptions, draw),
                        isHorizontal,
                        yScale,
                        barsContainer,
                        stack = d3.layout.stack(),
                        data,
                        xScale,
                        xScales = {};

                    graph.getTooltipText = function (d) {
                        if (this.settings.tooltipText) {
                            return utils.strings.parseValue(this.settings.tooltipText, d.rawData);
                        }

                        return d.rawData[this.settings.label] + " (" + d.rawData[this.settings.value] + ")";
                    };

                    graph.init(scope, element, attrs);

                }
            };
        }]);
}());
