(function () {
    'use strict';

    angular.module('LinksWidget')
        .directive('links', ["Chart", "$parse", "utils", function (Chart, $parse, utils) {
            return {
                template: '<div class="chart widget-links"></div>',
                restrict: 'E',
                replace: true,
                link: function postLink (scope, element, attrs) {
                    function draw () {
                        /* jshint validthis:true */
                        var data = this.getData();

                        if (!data.nodes || !data.links) {
                            return false;
                        }

                        graphDataWidth = this.width;

                        graph.params = {
                            colors: colorbrewer.Set2[Math.max(3,
                                graph.settings.legend ? graph.settings.legend.length : graph.settings.nodes.length)],
                            categories: {}
                        };

                        if (graph.settings.legend) {
                            graph.settings.legend.forEach(function (d) {
                                graph.params.categories[d] = {group: d};
                            });
                        }
                        else {
                            data.nodes.forEach(function (d) {
                                graph.params.categories[d.group] = d;
                            });
                        }

                        graph.params.categoryKeys = d3.keys(graph.params.categories);

                        angular.extend(graph.params, {
                            strokeColor: getColorScale(0.7),
                            fillColor: getColorScale(-0.1)
                        });

                        addDefsAndFilters();
                        createLegend();

                        graph.force = d3.layout.force()
                            .nodes(data.nodes)
                            .links(data.links)
                            .linkStrength(1)
                            .size([graphDataWidth, graph.height])
                            .linkDistance(this.options.linkDistance)
                            .charge(this.options.charge)
                            .gravity(this.options.gravity)
                            .on('tick', tick);

                        graphDataWidth -= legendWidth + 10;
                        this.dataSvg.attr("transform",
                            "translate(" + (legendWidth + 10) + ", " + this.options.margins.top + ")");

                        setBoundaries();
                        setDrag();
                        createLinks();
                        createNodes();

                        initForce();

                        if (graph.settings.linkTooltip) {
                            graph.getTooltipText = function (d) {
                                return utils.strings.parseValue(graph.settings.linkTooltip, d);
                            };
                        }

                        drawed = true;
                    }

                    var defaultOptions = {
                            "linkDistance": 130,
                            "charge": -400,
                            "gravity": 0.01,
                            "height": 800,
                            "numColors": 12,
                            "labelPadding": {
                                "left": 3,
                                "right": 3,
                                "top": 2,
                                "bottom": 2
                            },
                            "labelMargin": {"left": 3, "right": 3, "top": 2, "bottom": 2},
                            "ticksWithoutCollisions": 150,
                            "renderArrows": false,
                            "legendMargin": 20,
                            "keepNodePositionsOnUpdate": false, // If true, when there's new data, existing nodes will
                                                                // remain fixed in their current position,
                            "fixNodesOnDrag": false
                        },
                        graph = new Chart(defaultOptions, draw),
                        mouseoutTimeout,
                        highlighted,
                        legendWidth = 0,
                        graphDataWidth,
                        boundaries,
                        drawed,
                        onSelect = attrs.onSelect ? $parse(attrs.onSelect) : null,
                        onSelectLink = attrs.onSelectLink ? $parse(attrs.onSelectLink) : null,
                        getHighlights = attrs.getHighlights ? $parse(attrs.getHighlights) : null,
                        maxLineChars = 26,
                        wrapChars = ' /_-.'.split('');

                    element.css("height", "100%");

                    graph.init(scope, element, attrs);
                    graph.update = function (newData, oldData) {
                        if (!newData.nodes) {
                            drawed = false;
                            return;
                        }

                        if (!drawed) {
                            draw.call(this);
                        } else {
                            if (this.options.keepNodePositionsOnUpdate) {
                                graph.force.stop();

                                if (newData.nodes && oldData && oldData.nodes) {
                                    newData.nodes.forEach(function (newNode) {
                                        var isNew = true;
                                        for (var i = 0, oldNode; !!(oldNode = oldData.nodes[i]); i++) {
                                            if (oldNode.unique === newNode.unique) {
                                                newNode.x = oldNode.x;
                                                newNode.y = oldNode.y;
                                                newNode.fixed = true;
                                                isNew = false;
                                                break;
                                            }
                                        }
                                        newNode.isNewNode = isNew;
                                    });
                                }
                            }

                            graph
                                .force.nodes(newData.nodes || [])
                                .links(newData.links || []);

                            createNodes();
                            createLinks();

                            graph.force.on("end", unFixNodes);
                            initForce();
                        }
                    };
                    graph.onResize = function () {
                        graphDataWidth = this.width;
                        graph.force.size([graphDataWidth, graph.height]);
                        graphDataWidth -= legendWidth + 10;
                        this.dataSvg.attr("transform",
                            "translate(" + (legendWidth + 10) + ", " + this.options.margins.top + ")");
                        setBoundaries();
                        this.force.resume();
                    };

                    graph.highlightNodes = function (selectedNode) {
                        if (highlighted !== selectedNode) {
                            var highlightedNodes = getHighlights ?
                                getHighlights(scope, {links: graph.getData().links, selectedNode: selectedNode}) :
                                getConnections(selectedNode);
                            highlightSelectedNodes(highlightedNodes);
                            highlighted = selectedNode;
                        }
                    };


                    function unFixNodes () {
                        graph.getData().nodes.forEach(function (d) {
                            if (!d.centered) {
                                d.fixed = false;
                            }
                        });

                        graph.force.on("end", null);
                    }

                    function initForce () {
                        if (!graph.force.nodes().length) {
                            return;
                        }

                        setTimeout(function () {
                            graph.elements.nodes.each(function (d) {
                                var node = d3.select(this),
                                    text = node.selectAll('text'),
                                    bounds = {},
                                    first = true;

                                text.each(function () {
                                    var box = this.getBBox();
                                    if (first || box.x < bounds.x1) {
                                        bounds.x1 = box.x;
                                    }
                                    if (first || box.y < bounds.y1) {
                                        bounds.y1 = box.y;
                                    }
                                    if (first || box.x + box.width > bounds.x2) {
                                        bounds.x2 = box.x + box.width;
                                    }
                                    if (first || box.y + box.height > bounds.y2) {
                                        bounds.y2 = box.y + box.height;
                                    }
                                    first = false;
                                }).attr('text-anchor', 'middle');

                                var padding = graph.options.labelPadding,
                                    margin = graph.options.labelMargin,
                                    oldWidth = bounds.x2 - bounds.x1;

                                if (d.isNewNode === undefined || d.isNewNode) {
                                    bounds.x1 -= oldWidth / 2;
                                    bounds.x2 -= oldWidth / 2;
                                }
                                bounds.x1 -= padding.left;
                                bounds.y1 -= padding.top;
                                bounds.x2 += padding.left + padding.right;
                                bounds.y2 += padding.top + padding.bottom;

                                node.select('rect')
                                    .attr('x', bounds.x1)
                                    .attr('y', bounds.y1)
                                    .attr('width', bounds.x2 - bounds.x1)
                                    .attr('height', bounds.y2 - bounds.y1);

                                d.extent = {
                                    left: bounds.x1 - margin.left,
                                    right: bounds.x2 + margin.left + margin.right,
                                    top: bounds.y1 - margin.top,
                                    bottom: bounds.y2 + margin.top + margin.bottom
                                };

                                d.edge = {
                                    left: new geo.LineSegment(bounds.x1, bounds.y1, bounds.x1, bounds.y2),
                                    right: new geo.LineSegment(bounds.x2, bounds.y1, bounds.x2, bounds.y2),
                                    top: new geo.LineSegment(bounds.x1, bounds.y1, bounds.x2, bounds.y1),
                                    bottom: new geo.LineSegment(bounds.x1, bounds.y2, bounds.x2, bounds.y2)
                                };
                            });

                            graph.params.numTicks = 0;
                            graph.params.preventCollisions = true;
                            graph.force.start();
                            for (var i = 0; i < graph.options.ticksWithoutCollisions; i++) {
                                graph.force.tick();
                            }
                            graph.dataSvg.style("visibility", "visible");
                        }, 1);
                    }

                    function createNodes () {

                        function getNodeFillColor (d) {
                            return graph.params.fillColor(d.group);
                        }

                        function getNodeStrokeColor (d) {
                            return graph.params.strokeColor(d.group);
                        }

                        var nodesData = graph.dataSvg.selectAll('.node')
                            .data(graph.force.nodes(), function (d) {
                                return d.unique;
                            });

                        graph.elements.nodes = nodesData.enter().append('g')
                            .attr('class', "node")
                            .call(graph.drag)
                            .on('mouseover', function (d) {
                                if (mouseoutTimeout) {
                                    clearTimeout(mouseoutTimeout);
                                    mouseoutTimeout = null;
                                }

                                graph.highlightNodes(d);
                            })
                            .on('mouseout', function () {
                                if (mouseoutTimeout) {
                                    clearTimeout(mouseoutTimeout);
                                    mouseoutTimeout = null;
                                }
                                mouseoutTimeout = setTimeout(clearHighlight, 300);
                            });

                        if (onSelect) {
                            graph.elements.nodes.on("click", function (d) {
                                if (d3.event.defaultPrevented) {
                                    return;
                                } // ignore drag
                                var nodeElement = d3.event.currentTarget;

                                onSelect(scope, {node: d, element: d3.event.currentTarget}).then(function (response) {
                                    if (angular.isObject(response)) {
                                        angular.extend(d, response);
                                        if (response.group) {
                                            d3.select(nodeElement).selectAll("rect")
                                                .attr('stroke', getNodeStrokeColor)
                                                .attr('fill', getNodeFillColor);
                                        }
                                    }
                                });
                            });
                        }

                        graph.elements.nodeRect = graph.elements.nodes.append('rect')
                            .attr('rx', 5)
                            .attr('ry', 5)
                            .attr('stroke', getNodeStrokeColor)
                            .attr('fill', getNodeFillColor)
                            .attr('width', 120)
                            .attr('height', 30);

                        graph.elements.nodes.each(function (d) {
                            var node = d3.select(this),
                                lines = wrap(d.label),
                                ddy = 1.1,
                                dy = -ddy * lines.length / 2 + 0.5;

                            lines.forEach(function (line) {
                                node.append('text')
                                    .text(line)
                                    .attr('dy', dy + 'em');
                                dy += ddy;
                            });
                        });

                        nodesData.exit().remove();
                        graph.elements.nodes = graph.dataSvg.selectAll('.node');

                    }

                    function createLinks () {
                        var linksData = graph.dataSvg.selectAll('.link')
                            .data(graph.force.links(), function (d) {
                                return d.unique;
                            });

                        graph.elements.links = linksData
                            .enter().insert('line', ".node")
                            .attr('class', 'link' + (graph.options.renderArrows ? " arrow" : "") +
                            (onSelectLink ? " selectable" : ""));

                        if (onSelectLink) {
                            graph.elements.links.on("click", function (d) {
                                if (d3.event.defaultPrevented) {
                                    return;
                                } // ignore drag
                                onSelectLink(scope, {$event: d3.event, link: d});
                            });
                        }

                        if (graph.settings.linkTooltip) {
                            graph.elements.links.attr("data-tooltip", "");
                        }

                        linksData.exit().remove();
                        graph.elements.links = graph.dataSvg.selectAll('.link');
                    }

                    function createLegend () {
                        if (!graph.elements.legend) {
                            graph.elements.legend = graph.svg.append('g')
                                .attr('class', 'legend')
                                .attr("transform",
                                "translate(" + graph.options.margins.left + ", " + graph.options.margins.top + ")");

                            graph.params.legendConfig = {
                                rectWidth: 12,
                                rectHeight: 12,
                                xOffset: 0,
                                yOffset: 30,
                                xOffsetText: 20,
                                yOffsetText: 10,
                                lineHeight: 20
                            };
                            graph.params.legendConfig.xOffsetText += graph.params.legendConfig.xOffset;
                            graph.params.legendConfig.yOffsetText += graph.params.legendConfig.yOffset;
                        }

                        var legendCategories = graph.elements.legend.selectAll('.legend-category')
                            .data(d3.values(graph.params.categories))
                            .enter().append('g')
                            .attr('class', 'legend-category');

                        legendCategories.append('rect')
                            .attr('x', graph.params.legendConfig.xOffset)
                            .attr('y', function (d, i) {
                                return graph.params.legendConfig.yOffset + i * graph.params.legendConfig.lineHeight;
                            })
                            .attr('height', graph.params.legendConfig.rectHeight)
                            .attr('width', graph.params.legendConfig.rectWidth)
                            .attr('fill', function (d) {
                                return graph.params.fillColor(d.group);
                            })
                            .attr('stroke', function (d) {
                                return graph.params.strokeColor(d.group);
                            });

                        legendCategories.append('text')
                            .attr('x', graph.params.legendConfig.xOffsetText)
                            .attr('y', function (d, i) {
                                return graph.params.legendConfig.yOffsetText + i * graph.params.legendConfig.lineHeight;
                            })
                            .text(function (d) {
                                return d.group;
                            });

                        element.on('scroll', function () {
                            graph.elements.legend.attr('transform', 'translate(' + graph.options.margins.left + "," +
                                (graph.options.margins.top + $(this).scrollTop()) + ')');
                        });

                        legendWidth = graph.elements.legend[0][0].getBoundingClientRect().width;
                    }

                    function setBoundaries () {
                        var margin = 5;
                        boundaries = {
                            top: graph.options.labelPadding.top + margin,
                            bottom: graph.height - graph.options.labelMargin.bottom -
                            graph.options.labelPadding.bottom - margin,
                            left: graph.options.labelMargin.left + graph.options.labelPadding.left + margin,
                            right: graphDataWidth - graph.options.labelMargin.right - graph.options.labelPadding.right -
                            margin
                        };

                        boundaries.center = {
                            x: (boundaries.right - boundaries.left) / 2 + boundaries.left,
                            y: (boundaries.bottom - boundaries.top) / 2 + boundaries.top
                        };
                    }

                    function setDrag () {
                        graph.params.draggedThreshold = d3.scale.linear()
                            .domain([0, 0.1])
                            .range([5, 20])
                            .clamp(true);

                        function dragged (d) {
                            var threshold = graph.params.draggedThreshold(graph.force.alpha()),
                                dx = d.oldX - d.px,
                                dy = d.oldY - d.py;
                            if (Math.abs(dx) >= threshold || Math.abs(dy) >= threshold) {
                                d.dragged = true;
                            }
                            return d.dragged;
                        }

                        graph.drag = d3.behavior.drag()
                            .origin(function (d) {
                                return d;
                            })
                            .on('dragstart', function (d) {
                                d.oldX = d.x;
                                d.oldY = d.y;
                                d.dragged = false;
                                d.fixed = true;
                            })
                            .on('drag', function (d) {
                                d.px = d3.event.x;
                                d.py = d3.event.y;
                                if (dragged(d)) {
                                    graph.params.numTicks = 0;
                                    if (!graph.force.alpha()) {
                                        graph.force.alpha(0.025);
                                    }
                                }
                            })
                            .on('dragend', function (d) {
                                d.fixed = graph.options.fixNodesOnDrag;
                            });
                    }

                    function addDefsAndFilters () {
                        graph.svg.insert('defs', ".graph-data").selectAll('marker')
                            .data(['end'])
                            .enter().append('marker')
                            .attr('id', String)
                            .attr('viewBox', '0 -5 10 10')
                            .attr('refX', 10)
                            .attr('refY', 0)
                            .attr('markerWidth', 6)
                            .attr('markerHeight', 6)
                            .attr('orient', 'auto')
                            .append('path')
                            .attr('d', 'M0,-5L10,0L0,5');

                        // adapted from http://stackoverflow.com/questions/9630008
                        // and http://stackoverflow.com/questions/17883655

                        var glow = graph.svg.insert('filter', ".graph-data")
                            .attr('x', '-50%')
                            .attr('y', '-50%')
                            .attr('width', '200%')
                            .attr('height', '200%')
                            .attr('id', 'blue-glow');

                        glow.append('feColorMatrix')
                            .attr('type', 'matrix')
                            .attr('values', '0 0 0 0  0 ' +
                            '0 0 0 0  0 ' +
                            '0 0 0 0  .7 ' +
                            '0 0 0 1  0 ');

                        glow.append('feGaussianBlur')
                            .attr('stdDeviation', 3)
                            .attr('result', 'coloredBlur');

                        glow.append('feMerge').selectAll('feMergeNode')
                            .data(['coloredBlur', 'SourceGraphic'])
                            .enter().append('feMergeNode')
                            .attr('in', String);
                    }

                    function getColorScale (darkness) {
                        return d3.scale.ordinal()
                            .domain(graph.params.categoryKeys)
                            .range(graph.params.colors.map(function (c) {
                                return d3.hsl(c).darker(darkness).toString();
                            }));
                    }

                    function tick (e) {
                        graph.params.numTicks++;

                        if (graph.params.preventCollisions) {
                            preventCollisions(e.alpha);
                        }

                        graph.elements.links
                            .attr('x1', function (d) {
                                return d.source.x;
                            })
                            .attr('y1', function (d) {
                                return d.source.y;
                            })
                            .each(function (d) {
                                var x = d.target.x,
                                    y = d.target.y;

                                if (d.source.x === x && d.source.y === y) {
                                    return true;
                                }

                                var line = new geo.LineSegment(d.source.x, d.source.y, x, y);

                                for (var e in d.target.edge) {
                                    if (d.target.edge.hasOwnProperty(e)) {
                                        var ix = line.intersect(d.target.edge[e].offset(x, y));
                                        if (ix.in1 && ix.in2) {
                                            x = ix.x;
                                            y = ix.y;
                                            break;
                                        }
                                    }
                                }

                                d3.select(this)
                                    .attr('x2', x)
                                    .attr('y2', y);
                            });

                        graph.elements.nodes
                            .attr('transform', function (d) {
                                return 'translate(' + d.x + ',' + d.y + ')';
                            });
                    }

                    function preventCollisions () {
                        var data = graph.getData(),
                            quadtree = d3.geom.quadtree(data.nodes);

                        data.nodes.forEach(function (obj) {
                            if (obj.centered) {
                                obj.y = boundaries.center.y;
                                obj.x = boundaries.center.x;
                                return true;
                            }

                            var ox1 = obj.x + obj.extent.left,
                                ox2 = obj.x + obj.extent.right,
                                oy1 = obj.y + obj.extent.top,
                                oy2 = obj.y + obj.extent.bottom;

                            quadtree.visit(function (quad) {
                                if (quad.point && quad.point !== obj) {
                                    // Check if the rectangles intersect
                                    var p = quad.point,
                                        px1 = p.x + p.extent.left,
                                        px2 = p.x + p.extent.right,
                                        py1 = p.y + p.extent.top,
                                        py2 = p.y + p.extent.bottom,
                                        ix = (px1 <= ox2 && ox1 <= px2 && py1 <= oy2 && oy1 <= py2);
                                    if (ix) {
                                        var xa1 = ox2 - px1, // shift obj left , p right
                                            xa2 = px2 - ox1, // shift obj right, p left
                                            ya1 = oy2 - py1, // shift obj up   , p down
                                            ya2 = py2 - oy1, // shift obj down , p up
                                            adj = Math.min(xa1, xa2, ya1, ya2);

                                        if (adj === xa1) {
                                            obj.x -= adj / 2;
                                            p.x += adj / 2;
                                        } else if (adj === xa2) {
                                            obj.x += adj / 2;
                                            p.x -= adj / 2;
                                        } else if (adj === ya1) {
                                            obj.y -= adj / 2;
                                            p.y += adj / 2;
                                        } else if (adj === ya2) {
                                            obj.y += adj / 2;
                                            p.y -= adj / 2;
                                        }
                                    }
                                    return ix;
                                }
                            });

                            if (oy1 < boundaries.top) {
                                obj.y += 1;
                            } else if (oy2 > boundaries.bottom - obj.extent.bottom + obj.extent.top) {
                                obj.y -= 1;
                            }

                            if (ox1 < boundaries.left) {
                                obj.x += 1;
                            } else if (ox2 > boundaries.right - obj.extent.right + obj.extent.left) {
                                obj.x -= 1;
                            }
                        });
                    }

                    function wrap (text) {
                        if (text.length <= maxLineChars) {
                            return [text];
                        } else {
                            for (var k = 0; k < wrapChars.length; k++) {
                                var c = wrapChars[k];
                                for (var i = maxLineChars; i >= 0; i--) {
                                    if (text.charAt(i) === c) {
                                        var line = text.substring(0, i + 1);
                                        return [line].concat(wrap(text.substring(i + 1)));
                                    }
                                }
                            }
                            return [text.substring(0, maxLineChars)]
                                .concat(wrap(text.substring(maxLineChars)));
                        }
                    }

                    function clearHighlight () {
                        graph.elements.nodes.classed('inactive', false);
                        graph.elements.links.classed('inactive', false);
                        highlighted = null;
                    }

                    function highlightSelectedNodes (selectedNodes) {
                        graph.elements.nodes.classed('inactive', function (d) {
                            return !selectedNodes[d.unique];
                        });
                        graph.elements.links.classed('inactive', function (d) {
                            return (!selectedNodes[d.source.unique] || !selectedNodes[d.target.unique]);
                        });
                    }

                    function getConnections (node) {
                        var connections = {};
                        connections[node.unique] = true;

                        graph.getData().links.forEach(function (link) {
                            if (link.source === node) {
                                connections[link.target.unique] = true;
                            } else if (link.target === node) {
                                connections[link.source.unique] = true;
                            }
                        });
                        return connections;
                    }
                }
            };
        }]);
}());
