(function () {
    'use strict';

    angular.module('MultiLineWidget')
        .directive('multiLine', ["Chart", "utils", function (Chart, utils) {

            return {
                template: '<div class="chart multiline"></div>',
                restrict: 'E',
                require: "?ngModel",
                replace: true,
                link: function postLink (scope, element, attrs, ngModel) {
                    function draw () {
                        /* jshint validthis: true */
                        var self = this,
                            svg = this.dataSvg;

                        line = d3.svg.line()
                            .interpolate(graph.settings.interpolate || "linear")
                            .x(function (d) {
                                return graph.scale.x(d[graph.settings.x]);
                            })
                            .y(function (d) {
                                return graph.scale.y(d[graph.settings.y]);
                            });

                        var settingsMinXValue = graph.settings.minXValue || graph.settings.minDefaultXValue;
                        var settingsMaxXValue = graph.settings.maxXValue || graph.settings.maxDefaultXValue;

                        var minXValue = getXAxisValue(settingsMinXValue, graph.settings.xType.type);
                        var maxXValue = getXAxisValue(settingsMaxXValue, graph.settings.xType.type);

                        graph.scale.x.domain(getDomain("x", minXValue, maxXValue));
                        graph.scale.y.domain(getDomain("y", graph.settings.minYValue, graph.settings.maxYValue));

                        graph.elements.series = svg.selectAll(".series")
                            .data(self.data)
                            .enter().append("g")
                            .attr("class", "series pointsGroup")
                            // on mouseover we remove the g element and append it again, by that we create
                            // z-index effect
                            .on("mouseover", function () {
                                var thisGroup = d3.select(this),
                                    parent = $(thisGroup[0]).parent();
                                putChildOnTopOfSvgItem(thisGroup, parent);
                                thisGroup.classed("onLine", true);
                            })
                            .on("mouseout", function () {
                                d3.select(this).classed("onLine", false);
                            });

                        graph.elements.series.append("path")
                            .attr("class", "line")
                            .attr("d", function (d) {
                                return line(d.values);
                            })
                            .style("stroke", function (d) {
                                return color(d.name);
                            });

                        graph.elements.series.each(function (series, i) {
                            d3.select(graph.elements.series[0][i]).selectAll(".point").data(series.values)
                                .enter().append("circle")
                                .attr("class", "point")
                                .attr("r", graph.options.circleRadius)
                                .attr("cx", function (d) {
                                    return graph.options.circleRadius / 4 + graph.scale.x(d[graph.settings.x]);
                                })
                                .attr("cy", function (d) {
                                    return graph.scale.y(d[graph.settings.y]);
                                })
                                .attr("data-tooltip", graph.settings.tooltipText ? "" : null)
                                .attr("data-selectable", graph.settings.onSelect ? "" : null)
                                .style("fill", function () {
                                    return color(series.name);
                                });
                        });

                        //When the user will mouseover the legend item the path will be outlined and move to top
                        $(element).find(".legend").on("mouseover", ".legend-item", function () {
                            var pointsGroup = getThePointsGroupRelatedToTheLegend(this);
                            var thisClass = $(pointsGroup).attr("class");
                            (pointsGroup).attr("class", thisClass + " onLine");
                            putChildOnTopOfSvgItem(pointsGroup, pointsGroup.parent());
                        });

                        $(element).find(".legend").on("mouseout", ".legend-item", function () {
                            var pointsGroup = getThePointsGroupRelatedToTheLegend(this);
                            var thisClass = $(pointsGroup).attr("class");
                            $(pointsGroup).attr("class", thisClass.replace("onLine", ""));
                        });

                    }

                    var defaultOptions = {
                            circleRadius: 4
                        },
                        graph = new Chart(defaultOptions, draw),
                        line,
                        color;

                    element.css("height", "100%");

                    graph.getTooltipText = function (d) {
                        return utils.strings.parseValue(graph.settings.tooltipText, d);
                    };

                    graph.preRender = function () {
                        color = graph.getColorScale(graph.settings.color ||
                            (graph.data.length <= 10 ? "category10" : "category20"));
                        graph.legendData = graph.data.map(function (d) {
                            return {text: d.name, color: color(d.name)};
                        });
                    };

                    graph.init(scope, element, attrs);

                    scope.$on("$destroy", function () {
                        element.off();
                        element.empty();
                    });


                    function getXAxisValue (val, type) {
                        var returnVal;
                        var params;

                        if (val && angular.isString(val)) {
                            params = scope.widget.getState ? scope.widget.getState() : scope.getWidgetParams();
                            returnVal = Number(utils.strings.parseValue(val, {}, params));

                            if (type === "date") {
                                return utils.date.getMoment(returnVal).startOf('day');
                            }

                            return returnVal;
                        }

                        return val;
                    }

                    function putChildOnTopOfSvgItem (child, parent) {
                        child.remove();
                        $(parent).append(child[0]);
                    }

                    function getThePointsGroupRelatedToTheLegend (legendItem) {
                        var fillStyleColor = $(legendItem).find(".legend-item-bullet").attr("style");
                        fillStyleColor =
                            fillStyleColor.substring(fillStyleColor.indexOf(":") + 1, fillStyleColor.length - 1);
                        fillStyleColor = $.trim(fillStyleColor);
                        return $(element).find("g.pointsGroup>path[style*='" + fillStyleColor + "']").parent();
                    }

                    function getDomain (axis, minValue, maxValue) {
                        var min, max,
                            property = graph.settings[axis];

                        graph.data.forEach(function (series) {
                            series.values.forEach(function (item) {
                                var value = item[property];
                                if (minValue === undefined) {
                                    if (min === undefined || value < min) {
                                        min = value;
                                    }
                                }

                                if (maxValue === undefined) {
                                    if (max === undefined || value > max) {
                                        max = value;
                                    }
                                }
                            });
                        });

                        return [min || minValue, max || maxValue];
                    }
                }
            };
        }]);
}());
