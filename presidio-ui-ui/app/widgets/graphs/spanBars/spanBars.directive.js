(function () {
    'use strict';

    angular.module('SpanBarsWidget')
        .directive('spanBars', ["Chart", function (Chart) {
            return {
                template: '<div class="chart widget-span-bars"></div>',
                restrict: 'E',
                replace: true,
                link: function postLink (scope, element, attrs) {

                    function draw () {
                        /* jshint validthis: true */
                        var self = this,
                            svg = this.dataSvg;

                        if (this.settings.setDomainByData) {
                            this.setScaleDomains({
                                x: self.settings.setDomainByData.x ? [
                                    d3.min(self.data, function (d) {
                                        return d.start;
                                    }),
                                    d3.max(self.data, function (d) {
                                        return d.end;
                                    })
                                ] : null,
                                y: self.settings.setDomainByData.y ? [0, d3.max(self.data, function (d) {
                                    return d.value;
                                })] : null
                            });
                        }

                        function createBars () {
                            var bars = svg.selectAll(".span").data(self.data, function (d) {
                                return [+d.start, +d.end].join(":");
                            });

                            graph.elements.bars = bars.enter().append("rect")
                                .attr("class", "span")
                                .attr("width", function (d) {
                                    return Math.max(0, self.scale.x(d.end) - self.scale.x(d.start));
                                })
                                .attr("height", function (d) {
                                    return Math.max(0, self.scale.y.reverseScale(d.value));
                                })
                                .attr("x", function (d) {
                                    return self.scale.x(d.start);
                                })
                                .attr("y", function (d) {
                                    return self.scale.y(d.value);
                                })
                                .attr("fill", "steelblue")
                                .attr("data-tooltip", graph.settings.tooltip ? "" : null);

                            bars.exit().remove();
                        }

                        createBars();

                    }

                    var defaultOptions = {
                            padding: 20
                        },
                        graph = new Chart(defaultOptions, draw);

                    element.css("height", "100%");

                    graph.init(scope, element, attrs);

                    graph.onResize = function () {
                        var self = this;

                        graph.elements.bars.attr("width", function (d) {
                            return Math.max(0, self.scale.x(d.end) - self.scale.x(d.start));
                        })
                            .attr("height", function (d) {
                                return Math.max(0, self.scale.y.reverseScale(d.value));
                            })
                            .attr("x", function (d) {
                                return self.scale.x(d.start);
                            })
                            .attr("y", function (d) {
                                return self.scale.y(d.value);
                            });
                    };

                }
            };
        }]);
}());
