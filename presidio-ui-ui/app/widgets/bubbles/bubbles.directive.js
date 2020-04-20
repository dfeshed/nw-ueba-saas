(function () {
    'use strict';

    angular.module('BubblesWidget')
        .directive('bubbles', ["Chart", "utils", function (Chart, utils) {
            return {
                template: '<div class="widget-bubbles"></div>',
                restrict: 'E',
                require: "?ngModel",
                replace: true,
                link: function postLink (scope, element, attrs) {
                    /* jshint validthis: true */
                    function draw () {
                        var self = this,
                            svg = this.dataSvg,
                            diameter = Math.min(this.dataWidth, this.dataHeight);

                        this.svg.attr("width", diameter);

                        var bubble = d3.layout.pack()
                            .sort(null)
                            .size([diameter - this.options.margins.left - this.options.margins.right,
                                diameter - this.options.margins.top - this.options.margins.bottom])
                            .padding(1.5);

                        this.elements.nodes = svg.selectAll(".node")
                            .data(bubble.nodes(self.data).filter(function (d) {
                                return !d.children;
                            }))
                            .enter()
                            .append("g")
                            .attr("class", "node")
                            .attr("data-tooltip", "")
                            .attr("transform", function (d) {
                                return "translate(" + d.x + "," + d.y + ")";
                            })
                            .attr("data-selectable", this.settings.onSelect ? "" : null);

                        this.elements.circles = this.elements.nodes.append("circle")
                            .attr("r", function (d) {
                                return d.r;
                            });

                        this.elements.circles = this.elements.nodes.append("text")
                            .attr("dy", ".3em")
                            .style("text-anchor", "middle")
                            .attr("font-family", "Roboto, sans-serif")
                            .attr("font-size", "14px")
                            .attr("font-weight", "bold")
                            .text(function (d) {
                                return d.name.substring(0, d.r / 4.5);
                            });
                    }

                    var defaultOptions = {
                            circleStrokeWidth: 2
                        },
                        graph = new Chart(defaultOptions, draw);

                    element.css("height", "100%");

                    graph.getTooltipText = function (d) {
                        return graph.settings.tooltipText ? utils.strings.parseValue(graph.settings.tooltipText, d) :
                        d.data[graph.settings.label] + ": " + d.data[graph.settings.value] + " (" +
                        Math.round(d.data._percent) + "%)";
                    };

                    graph.init(scope, element, attrs);

                    scope.$on("$destroy", function () {
                        element.off();
                        element.empty();
                    });

                }
            };
        }]);
}());
