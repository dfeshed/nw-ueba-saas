(function () {
    'use strict';

    angular.module('ForceChartWidget')
        .directive('forceChart', ["events", function (eventsService) {
            return {
                template: '<div class="widget-force-chart"></div>',
                restrict: 'E',
                require: "?ngModel",
                replace: true,
                link: function postLink (scope, element, attrs, ngModel) {
                    var defaultOptions = {
                            "radius": 2.5,
                            "fontSize": 9,
                            "labelFontSize": 9,
                            "gravity": 0,
                            "height": 800,
                            "nodeFocusColor": "black",
                            "nodeFocusRadius": 25,
                            "nodeFocus": true,
                            "linkDistance": 1250,
                            "charge": 0,
                            "nodeResize": "weight",
                            "linkName": "tag",
                            "minRadius": 3,
                            "maxRadius": 15,
                            "minScale": 1,
                            "maxScale": 3

                        },
                        data,
                        dataChanged,
                        events,
                        settings,
                        graphOptions,
                        nodeClickInProgress,
                        nodeClickTimeout,
                        nodesData,
                        linksData,
                        selectedNodes = [];

                    // Load the Visualization API and the piechart package.
                    //google.load('visualization', '1.0', {'packages':['corechart']});

                    scope.$on("$destroy", function () {
                        element.empty();
                        element.off();
                    });

                    scope.$watch(attrs.ngModel, function (chartData) {
                        data = chartData;
                        dataChanged = true;
                        drawChart();
                    });

                    scope.$watch(attrs.graphSettings, function (value) {
                        events = value.events;
                        settings = value;
                        graphOptions = angular.extend({}, defaultOptions, settings.graphOptions);

                        drawChart();
                    });

                    function drawChart () {
                        element[0].innerHTML = "";

                        if (!settings || !data || !data.nodes || !data.nodes.length || !dataChanged) {
                            return;
                        }

                        function addLinks () {
                            linksData = data.links.filter(function (d) {
                                return d.source.group !== d.target.group;
                            });

                            if (selectedNodes.length) {
                                linksData = linksData.filter(function (d) {
                                    return d.source.selected || d.target.selected;
                                });
                            }

                            force.links(linksData);

                            link = svg.selectAll("line.link")
                                .data(linksData, function (d) {
                                    return d.unique;
                                });

                            link.enter().insert("svg:line", ".node").attr("class", "link");
                            link.exit().remove();
                        }

                        function addNodes () {
                            nodesData = [];
                            if (selectedNodes.length) {
                                selectedNodes.forEach(function (node) {
                                    nodesData.push(node);
                                });

                                linksData.forEach(function (d) {
                                    if (!~nodesData.indexOf(d.source)) {
                                        nodesData.push(d.source);
                                    }

                                    if (!~nodesData.indexOf(d.target)) {
                                        nodesData.push(d.target);
                                    }
                                });
                            }
                            else {
                                nodesData = data.nodes;
                            }

                            force.nodes(nodesData);

                            node = svg.selectAll(".node")
                                .data(nodesData, function (d) {
                                    return d.unique;
                                });

                            node.enter().append("svg:g")
                                .attr("class", "node")

                                .on("click", function (d) {
                                    if (d3.event.defaultPrevented) {
                                        return;
                                    }

                                    if (nodeClickInProgress) {
                                        nodeClickInProgress = false;
                                        clearTimeout(nodeClickTimeout);
                                        onDblClick(d);
                                    }
                                    else {
                                        nodeClickInProgress = true;
                                        nodeClickTimeout = setTimeout(function () {
                                            onClick(d);
                                            nodeClickInProgress = false;
                                        }, 200);
                                    }
                                })
                                .call(drag);

                            if (settings.icons) {
                                node.append("svg:image")
                                    .attr("xlink:href", function (d) {
                                        return settings.icons[d.group];
                                    })
                                    .attr("width", 25)
                                    .attr("height", 25)
                                    .attr("x", -8)
                                    .attr("y", -10)
                                    .style("fill", function (d) {
                                        return color(d.group);
                                    });
                            }
                            else {
                                node.append("svg:circle")
                                    .attr("class", "node")
                                    .style("fill", function (d) {
                                        return color(d.group);
                                    })
                                    .attr("r", function (d) {
                                        return Math.min(scale(d.weight), graphOptions.maxRadius);
                                    });
                            }

                            node.append("text")
                                .attr("dx", 12)
                                .attr("dy", ".35em")
                                .text(function (d) {
                                    return d.label;
                                });

                            node.append("title")
                                .text(function (d) {
                                    return d.label;
                                });

                            node.exit().remove();
                        }

                        function onClick (d) {
                            if (d.selected) {
                                selectedNodes.splice(selectedNodes.indexOf(d), 1);
                            } else {
                                selectedNodes.push(d);
                            }

                            d.selected = !d.selected;
                            addLinks();
                            addNodes();
                            force.resume();
                        }

                        function onDblClick (d) {
                            if (graphOptions.onDblClick) {
                                eventsService.triggerDashboardEvent(graphOptions.onDblClick, d);
                            }
                        }

                        function dragstart (d) {
                            /* jshint validthis: true */
                            d3.select(this).classed("fixed", d.fixed = true);
                        }

                        function tick () {
                            link.attr("x1", function (d) {
                                return d.source.x;
                            })
                                .attr("y1", function (d) {
                                    return Math.min(height - 10, Math.max(10, d.source.y));
                                })
                                .attr("x2", function (d) {
                                    return d.target.x;
                                })
                                .attr("y2", function (d) {
                                    return Math.max(10, d.target.y);
                                });

                            node.attr("transform", function (d) {
                                return "translate(" + d.x + "," + Math.min(height - 10, Math.max(10, d.y)) + ")";
                            });
                        }
                        var width = element.innerWidth() - 30,
                            height = graphOptions.height;

                        var link, node;
                        var scale = d3.scale.linear()
                            .range(settings.icons ? [graphOptions.minScale, graphOptions.maxScale] :
                                [graphOptions.minRadius, graphOptions.maxRadius])
                            .domain([1, 30]);

                        var color = d3.scale.category10();

                        var svg = d3.select(element[0])
                            .append("svg:svg")
                            .attr("width", width)
                            .attr("height", height);

                        d3.scale.linear().range([-120, 0]).domain([0, 10]);
                        d3.scale.linear().range([40, 300]).domain([0, 100]);

                        var force = d3.layout.force()
                            .linkDistance(60)
                            .charge(-220)
                            .gravity(0.02)
                            .size([width, height])
                            .nodes(data.nodes)
                            .links(data.links)
                            .on("tick", tick)
                            .start();

                        var drag = force.drag()
                            .on("dragstart", dragstart);

                        addLinks();
                        addNodes();


                        force.on("tick", tick);
                        force.tick();

                    }
                }
            };
        }]);
}());
