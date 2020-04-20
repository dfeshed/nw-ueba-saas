(function () {
    'use strict';

    angular.module('PieWidget')
        .directive('pie', ["Chart", "utils", function (Chart, utils) {
            return {
                template: '<div class="widget-pie"></div>',
                restrict: 'E',
                require: "?ngModel",
                replace: true,
                link: function postLink (scope, element, attrs, ngModel) {

                    function draw () {
                        /* jshint validthis: true */
                        var self = this,
                            color = this.settings.color ? this.getColorScale(this.settings.color) :
                                d3.scale.ordinal().range(["#98abc5", "#8a89a6", "#7b6888", "#6b486b", "#a05d56",
                                    "#d0743c", "#ff8c00"]),
                            svg = this.dataSvg,
                            r = this.options.radius,
                            arc = d3.svg.arc().outerRadius(r).innerRadius(0),
                            pie = d3.layout.pie()
                                .sort(function (a, b) {
                                    var aVal = a[self.settings.value],
                                        bVal = b[self.settings.value];

                                    if (aVal === bVal) {
                                        return 0;
                                    }

                                    return aVal < bVal ? 1 : -1;
                                })
                                .value(function (d) {
                                    return d[self.settings.value];
                                }
                            ),
                            lines,
                            pieData = pie(this.data),
                            largeItemsData = pieData.filter(function (item) {
                                return item.data._percent > 4;
                            }); // Only items with value > 4%

                        var vis = svg.append("svg:g")
                            .attr("transform", "translate(" + this.width / 2 + ", " + this.height / 2 + ")");

                        //draw lines to connect label to pie triangle
                        lines = vis.selectAll("line").data(largeItemsData);
                        lines.enter().append("svg:line")
                            .attr("x1", 0)
                            .attr("x2", 0)
                            .attr("y1", -r - 1)
                            .attr("y2", -r - 8)
                            .attr("stroke", "gray")
                            .attr("transform", function (d) {
                                return "rotate(" + (d.startAngle + d.endAngle) / 2 * (180 / Math.PI) + ")";
                            });
                        lines.exit().remove();

                        //draw slices
                        var arcs = vis.selectAll("g.slice")
                            .data(pieData).enter()
                            .append("svg:g")
                            .attr("class", "slice")
                            .attr("data-tooltip", "");

                        //set the color for each slice
                        arcs.append("svg:path")
                            .attr("fill", function (d, i) {
                                return color(self.settings.color ? d.data : i);
                            })
                            .attr("d", arc);

                        var flipPoint = 1.9 * Math.PI;

                        //draw labels
                        var labels = arcs.append("svg:text")
                            .attr("class", function (d) {
                                return d.data._percent <= 4 ? "hidden" : "";
                            })
                            .attr("transform", function (d) {
                                var offset = self.options.labelsOffset;
                                return "translate(" +
                                    Math.cos(((d.startAngle + d.endAngle - Math.PI) / 2)) * (r + offset) + "," +
                                    Math.sin((d.startAngle + d.endAngle - Math.PI) / 2) * (r + offset) + ")";
                            })
                            .attr("dy", function (d) {
                                if ((d.startAngle + d.endAngle) / 2 > Math.PI / 2 &&
                                    (d.startAngle + d.endAngle) / 2 < Math.PI * 1.5) {
                                    return 5;
                                } else {
                                    return -7;
                                }
                            })
                            .attr("text-anchor", function (d) {
                                var middleAngle = (d.startAngle + d.endAngle) / 2;
                                if (middleAngle < Math.PI || (middleAngle > flipPoint && middleAngle < 2 * Math.PI)) {
                                    return "beginning";
                                } else {
                                    return "end";
                                }
                            })
                            .text(function (d) {
                                return d.data[self.settings.label] + ": " + d.data[self.settings.value] + " (" +
                                    Math.round(d.data._percent) + "%)";
                            });

                        var prevbb;

                        labels.each(function (d, i) {
                            var thisbb = this.getBoundingClientRect();

                            if (i > 0) {
                                // move if they overlap
                                if (!(thisbb.right < prevbb.left ||
                                    thisbb.left > prevbb.right ||
                                    thisbb.bottom < prevbb.top ||
                                    thisbb.top > prevbb.bottom)) {
                                    this.classList.add("hidden");
                                }
                            }
                            prevbb = thisbb;
                        });
                    }


                    var defaultOptions = {
                            radius: 100,
                            labelsOffset: 10
                        },
                        graph = new Chart(defaultOptions, draw);

                    element.css("height", "100%");

                    graph.getTooltipText = function (d) {
                        if (graph.settings.tooltipText) {
                            return utils.strings.parseValue(graph.settings.tooltipText, d.data);
                        }

                        return d.data[graph.settings.label] + ": " + d.data[graph.settings.value] + " (" +
                            Math.round(d.data._percent) + "%)";
                    };

                    graph.init(scope, element, attrs);

                    scope.$on("$destroy", function () {
                        element.off();
                        element.empty();
                    });
                    //this is called from Charts.js where the svg is appended to the HTML
                }
            };
        }]);
}());
