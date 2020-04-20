(function () {
    'use strict';

    angular.module('TreeWidget')
        .directive('tree', ["Chart", "$parse", "utils", function (Chart, $parse, utils) {
            return {
                restrict: 'E',
                template: "<div class='chart widget-tree'></div>",
                replace: true,
                link: function postLink (scope, element, attrs) {

                    function draw () {
                        /* jshint validthis: true */
                        var data = this.getData();
                        if (!data) {
                            return false;
                        }

                        graph.tree = d3.layout.tree()
                            .size([this.dataHeight, this.dataWidth]);

                        root = data;
                        initNodes(data);

                        return true;
                    }

                    function initNodes () {
                        if (!root) {
                            return;
                        }

                        root.x0 = graph.dataHeight / 2;
                        root.y0 = graph.options.rtl ? graph.dataWidth : 0;
                        root[graph.settings.xPositionParam] = graph.scale.x.domain()[graph.options.rtl ? 1 : 0];

                        function toggleAll (d) {
                            if (d.children) {
                                d.children.forEach(toggleAll);
                                toggle(d);
                            }
                        }

                        if (root.children) {
                            root.children.forEach(toggleAll);
                        }

                        createGraph(root);
                    }

                    function selectNode (node) {
                        if (onSelect && !node.children) {
                            onSelect(scope,
                                {node: node, element: d3.event.currentTarget, graph: graph}).then(function (response) {
                                    if (angular.isObject(response)) {
                                        if (response.children) {
                                            var newnodes = graph.tree.nodes(response.children).reverse();
                                            node.children = newnodes[0];
                                            createGraph(node);
                                        }
                                        else {
                                            node.children = [];
                                        }
                                    }
                                    else {
                                        toggle(node);
                                        createGraph(node);
                                    }
                                });
                        }
                        else {
                            toggle(node);
                            createGraph(node);
                        }
                    }

                    function setNodePosition (d) {
                        d.y = graph.scale.x(d[graph.settings.xPositionParam]);
                    }

                    function createGraph (source) {

                        function createNodes () {
                            if (graph.scale.x && graph.settings.xPositionParam) {
                                nodes.forEach(function (d) {
                                    if (d.unique === "__ROOT__") {
                                        d.y = d.y0;
                                    } else {
                                        d.y = graph.scale.x(d[graph.settings.xPositionParam]);
                                    }
                                });
                            }

                            var node = graph.dataSvg.selectAll("g.node")
                                .data(nodes, function (d) {
                                    return d.unique;
                                });

                            // Enter any new nodes at the parent's previous position.
                            var nodeEnter = node.enter().append("svg:g")
                                .attr("class", "node")
                                .attr("data-tooltip", graph.settings.nodeTooltip ? function (d) {
                                    return d.unique === "__ROOT__" ? null : "nodeTooltip";
                                } : null)
                                .attr("transform", function () {
                                    return "translate(" + source.y0 + "," + source.x0 + ")";
                                })
                                .on("click", selectNode)
                                .on('mouseover', function (d) {
                                    highlightPath(d);
                                })
                                .on('mouseout', function () {
                                    clearHighlight();
                                });

                            graph.elements.nodes = nodeEnter;

                            nodeEnter.append("svg:circle")
                                .attr("r", 1e-6)
                                .style("fill", function (d) {
                                    return d._children ? "lightsteelblue" : "#fff";
                                });

                            nodeEnter.append("svg:text")
                                .attr("y", "-.75em")
                                .attr("text-anchor", "middle")
                                .text(function (d) {
                                    return d.name;
                                })
                                .style("fill-opacity", 1e-6);

                            // Transition nodes to their new position.
                            var nodeUpdate = node.transition()
                                .duration(duration)
                                .attr("transform", getNodeTransform);

                            nodeUpdate.select("circle")
                                .attr("r", 4.5)
                                .style("fill", function (d) {
                                    return d._children ? "lightsteelblue" : "#fff";
                                });

                            nodeUpdate.select("text")
                                .style("fill-opacity", 1);

                            // Transition exiting nodes to the parent's new position.
                            var nodeExit = node.exit().transition()
                                .duration(duration)
                                .attr("transform", function () {
                                    return "translate(" + source.y + "," + source.x + ")";
                                })
                                .remove();

                            nodeExit.select("circle")
                                .attr("r", 1e-6);

                            nodeExit.select("text")
                                .style("fill-opacity", 1e-6);

                            graph.elements.nodes = graph.dataSvg.selectAll('.node');
                        }

                        function createLinks () {
                            var link = graph.dataSvg.selectAll("path.link")
                                .data(tree.links(nodes), function (d) {
                                    return d.target.unique;
                                });

                            // Enter any new links at the parent's previous position.
                            graph.elements.links = link.enter().insert("svg:path", ".node")
                                .attr("class", "link" + (onSelectLink ? " selectable" : ""))
                                .attr("d", function () {
                                    var o = {x: source.x0, y: source.y0};
                                    return diagonal({source: o, target: o});
                                });

                            if (onSelectLink) {
                                graph.elements.links.on("click", function (d) {
                                    onSelectLink(scope, {$event: d3.event, link: d});
                                });
                            }

                            graph.elements.links.transition()
                                .duration(duration)
                                .attr("d", diagonal);

                            // Transition links to their new position.
                            link.transition()
                                .duration(duration)
                                .attr("d", diagonal);

                            // Transition exiting nodes to the parent's new position.
                            link.exit().transition()
                                .duration(duration)
                                .attr("d", function () {
                                    var o = {x: source.x, y: source.y};
                                    return diagonal({source: o, target: o});
                                })
                                .remove();

                            graph.elements.links = graph.dataSvg.selectAll('.link');
                        }

                        function createLinkTexts () {
                            var linksText = graph.dataSvg.selectAll(".linkText").data(tree.links(nodes), function (d) {
                                    return d.target.unique;
                                }),
                                params = scope.getWidgetParams();

                            graph.elements.linkTexts = linksText.enter().insert("svg:text", ".node")
                                .attr("class", "linkText" + (onSelectLink ? " selectable" : ""))
                                .text(function (d) {
                                    return utils.strings.parseValue(graph.settings.linkText, d, params);
                                })
                                .attr("text-anchor", "middle")
                                .style("fill-opacity", 1e-6)
                                .attr("dy", "-.35em")
                                .attr("transform", function (d) {
                                    return "translate(" + d.source.y + ", " + d.source.x + ")";
                                })
                                .attr("data-tooltip", graph.settings.linkTextTooltip ? "linkTextTooltip" : null);

                            if (onSelectLink) {
                                graph.elements.linkTexts.on("click", function (d) {
                                    onSelectLink(scope, {$event: d3.event, link: d});
                                });
                            }

                            graph.elements.linkTexts.transition()
                                .duration(duration)
                                .style("fill-opacity", 1)
                                .attr("transform", function (d) {
                                    return "translate(" + (d.source.y + (d.target.y - d.source.y) / 2) + ", " +
                                        (d.source.x + (d.target.x - d.source.x) / 2) + ")";
                                });

                            linksText.exit().transition()
                                .duration(duration)
                                .attr("transform", function (d) {
                                    return "translate(" + d.source.y + ", " + d.source.x + ")";
                                })
                                .style("fill-opacity", 1e-6)
                                .remove();

                            graph.elements.linkTexts = graph.dataSvg.selectAll('.linkText');
                        }

                        var tree = graph.tree,
                            duration = graph.options.expandDuration;

                        nodes = tree.nodes(root).reverse();

                        createNodes();
                        createLinks();

                        if (graph.settings.linkText) {
                            createLinkTexts();
                        }

                        // Stash the old positions for transition.
                        nodes.forEach(function (d) {
                            d.x0 = d.x;
                            d.y0 = d.y;
                        });

                    }

                    function highlightPath (node) {
                        var highlighted = getHighlightedNodes(node);
                        graph.elements.nodes.classed('inactive', function (d) {
                            return !highlighted[d.unique];
                        });
                        graph.elements.links.classed('inactive', function (d) {
                            return (!highlighted[d.source.unique] || !highlighted[d.target.unique]);
                        });
                        element.addClass("highlighted");
                    }

                    function getHighlightedNodes (node) {
                        var highlighted = {},
                            currentNode = node;

                        while (currentNode) {
                            highlighted[currentNode.unique] = true;
                            currentNode = currentNode.parent;
                        }

                        return highlighted;
                    }

                    function toggle (d) {
                        if (d.children) {
                            d._children = d.children;
                            d.children = null;
                        } else {
                            d.children = d._children;
                            d._children = null;
                        }
                    }

                    function clearHighlight () {
                        graph.elements.nodes.classed('inactive', false);
                        graph.elements.links.classed('inactive', false);
                        highlighted = null;
                        element.removeClass("highlighted");
                    }

                    function getNodeTransform (d) {
                        return "translate(" + d.y + "," + d.x + ")";
                    }

                    function onUpdateDomain () {
                        nodes.forEach(setNodePosition);
                        graph.elements.nodes.attr("transform", getNodeTransform);

                        graph.elements.links.attr("d", function (d) {
                            return diagonal({source: d.source, target: d.target});
                        });

                        graph.elements.linkTexts.attr("transform", function (d) {
                            return "translate(" + (d.source.y + (d.target.y - d.source.y) / 2) + ", " +
                                (d.source.x + (d.target.x - d.source.x) / 2) + ")";
                        });
                    }

                    var defaultOptions = {
                            expandDuration: 500,
                            rtl: false,
                            margins: {
                                top: 10,
                                bottom: 10,
                                left: 10,
                                right: 10
                            }
                        },
                        graph = new Chart(defaultOptions, draw),
                        root,
                        onSelect = attrs.onSelect ? $parse(attrs.onSelect) : null,
                        onSelectLink = attrs.onSelectLink ? $parse(attrs.onSelectLink) : null,
                        diagonal = d3.svg.diagonal().projection(function (d) {
                            return [d.y, d.x];
                        }),
                        highlighted,
                        nodes;



                    element.css("height", "100%");
                    graph.onUpdateDomain = onUpdateDomain;
                    graph.init(scope, element, attrs);

                    graph.getTooltipText = function (d, type) {
                        return utils.strings.parseValue(graph.settings[type], d);
                    };

                    graph.onResize = function () {
                        graph.tree.size([this.dataHeight, this.dataWidth]);
                        root.x0 = graph.dataHeight / 2;
                        root.y0 = graph.options.rtl ? graph.dataWidth : 0;

                        onUpdateDomain();
                    };
                }
            };
        }]);
}());
