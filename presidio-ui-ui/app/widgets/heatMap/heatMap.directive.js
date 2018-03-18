(function () {
    'use strict';

    angular.module('HeatMapWidget')
        .directive('heatMap', ["utils", "colors", "Chart", function (utils, colors, Chart) {
            return {
                template: '<div class="widget-heat-map chart"></div>',
                restrict: 'E',
                require: "?ngModel",
                replace: true,
                link: function postLink (scope, element, attrs, ngModel) {


                    /**
                     * This function draw the heat map table
                     */
                    function draw () {


                        //Start scrolling handling
                        /**
                         * Add scrolling SVG item
                         * graphHeight - The  height of the graph - include the labels and all cells
                         */
                        function setScrolling (graphHeight) {
                            //Add rect item to SVG for scrolling, and set the location to be marings.top
                            //and other parameters
                            graph.scroll = {position: 0, scrollBarPosition: graph.options.margins.top};
                            graph.scroll.container = graph.svg.append("svg:g")
                                .attr("class", "scrollbar scrollbar-v")
                                .attr("transform",
                                "translate(" + (element.width() - graph.options.margins.right) + ", " +
                                graph.options.margins.top + ")");

                            graph.scroll.scrollBar = graph.scroll.container.append("rect")
                                .attr("class", "scrollbar")
                                .attr("x", "-20px")
                                .attr("y", graph.options.margins.top)
                                .attr("rx", "4px")
                                .attr("width", graph.options.scrollBarWidth = 8)
                                .attr("height", graph.scroll.scrollBarHeight = 60);

                            //Event listener to dragging the rect item which represent the scroller
                            var scrollDrag = d3.behavior.drag().on("drag", function () {
                                scrollDragMove(d3.event.dy);
                            });

                            //Event listener of scrolling with mouse wheel
                            graph.svg.on("mousewheel", function () {
                                var deltaY = Math.ceil(d3.event.wheelDelta / -80);
                                d3.event.preventDefault();
                                graph.scroll.scrollBar.attr("y", graph.scroll.scrollBarPosition += deltaY);
                                scrollDragMove(deltaY);
                            });

                            graph.scroll.scrollBar.call(scrollDrag);

                            //The scroll length is function of the ration and minScrollBarPosition
                            //Scroll bar possition should be equals to the chart height
                            graph.scroll.ratio = -0.66;
                            graph.scroll.minScrollBarPosition =
                                // (graph.height  - graph.scroll.scrollBarHeight - graph.options.margins.top -
                                // graph.options.margins.bottom/2 )*10;
                                graphHeight - graph.scroll.scrollBarHeight - graph.options.margins.bottom;

                            graph.scroll.scrollBarPosition = graph.options.margins.top;

                            //This function move the location of scroller rect and change the visible part of the
                            // SVG component
                            function scrollDragMove (deltaY) {
                                graph.scroll.scrollBarPosition = Math.max(0,
                                    Math.min(graph.scroll.minScrollBarPosition,
                                        graph.scroll.scrollBarPosition + deltaY));
                                graph.scroll.scrollBar.attr("y", graph.scroll.scrollBarPosition);
                                graph.scroll.position = graph.scroll.scrollBarPosition * graph.scroll.ratio;
                                rootScrollableContainer.attr("transform",
                                    "translate(0," + graph.scroll.position + ")");
                            }

                        }

                        function removeScrolling () {
                            //Currently don't do nothing
                        }

                        element.css("width", "100%");
                        element.css("height", "100%");

                        //Set max page size
                        //The internal height of window with 'px'
                        var maxHeight = element.closest('.widget-view-wrap').css('height');
                        //Convert to number
                        maxHeight = Number(maxHeight.substring(0, maxHeight.length - 2));

                        /* jshint validthis: true */
                        if (this.height < 100) {
                            element.empty();
                            element.off();

                            graph.init(scope, element, attrs);
                        } else {
                            var self = this,
                                maxCellSize = element.width() / 35,
                                data = angular.copy(graph.data),
                                colorSettings = scope.view.settings.behavior.color || {domain: getValuesExtent(data)},
                                maxLabelSize = 25,
                                gapBetweenCells = 1,
                                animationDuration = 1500,
                                columnLabelPosition = scope.view.settings.behavior.columns.position || "top";

                            var margin = getMargin(data, self, maxLabelSize, gapBetweenCells, columnLabelPosition);

                            self.scaleX = d3.scale.linear()
                                .domain([0, data.columns.length])
                                .range([0, self.width - (margin.right + margin.left)]);

                            var cellSize = self.scaleX(1);

                            cellSize = (cellSize < maxCellSize) ? cellSize : maxCellSize;
                            var fontScale = d3.scale.linear()
                                .domain([0, maxCellSize])
                                .range([0.9, 0.9]);

                            self.heatMapProperties = {
                                margin: margin,
                                cellSize: cellSize,
                                colorScale: colors.getScale(colorSettings),
                                fontSize: fontScale(cellSize),
                                gapBeweenCells: gapBetweenCells,
                                columnLabelPosition: columnLabelPosition,
                                animationDuration: animationDuration
                            };

                            var realGraphHeight = graph.heatMapProperties.margin.top +
                                graph.heatMapProperties.margin.bottom + (cellSize * (graph.data.rows.length + 1));


                            //End scrolling handling
                            graph.data = createJsonArr();
                            buildHeatMapForTheFirstTime(graph.data);

                            var newHeight = Math.min(maxHeight, realGraphHeight);
                            //If the graph size + the mergin bigger then maxHeight (the white space) we should add
                            // scrolling
                            var graphHeight = (cellSize * (graph.data.rows.length + 1)) + maxLabelSize +
                                graph.heatMapProperties.margin.top;
                            if (graphHeight > maxHeight) {
                                setScrolling(maxHeight - graph.options.margins.bottom);
                            }
                            else {
                                removeScrolling();
                            }
                            element.css("height", newHeight + "px");
                            $(element).parents(".widget-view-container").css("height", "inherit");
                            var legendX = calculateCenter() +
                                (cellSize + graph.heatMapProperties.gapBeweenCells) * (graph.data.columns.length) +
                                graph.heatMapProperties.margin.left / 2;
                            var legendHeight = (cellSize + graph.heatMapProperties.gapBeweenCells) *
                                graph.data.rows.length;
                            legendHeight = (legendHeight < 200) ? legendHeight : 150;
                            addLegend(legendX, graph.heatMapProperties.margin.top, 10, legendHeight,
                                getValuesExtent(data), scope.view.settings.legend,
                                scope.view.settings.behavior.color || "range");
                        }
                    }

                    var defaultOptions = {
                            direction: "vertical"
                        },
                        graph = new Chart(defaultOptions, draw);

                    element.css("height", "100%");
                    element.css("width", "100%");

                    scope.$watch(element, function () {
                        graph.init(scope, element, attrs);
                    });

                    element.parents(".dataView").addClass("heatMapView");

                    var lastItemNameClicked;
                    var rootScrollableContainer;

                    /**
                     * This function sort the rows of the heatmap when the user click on the head of a column
                     * @param column
                     */
                    function sortByColumn (column) {
                        if (column.name !== lastItemNameClicked) {
                            lastItemNameClicked = column.name;
                            var myData = d3.values(angular.copy(graph.data));
                            myData[1].sort(function (a, b) {

                                var aVal = a.columns.filter(function (col) {
                                    if (col.name === column.name) {
                                        col.lastSortedItem = true;
                                    }
                                    return col.name === column.name;
                                });

                                var bVal = b.columns.filter(function (col) {
                                    return col.name === column.name;
                                });

                                if (aVal[0].value < bVal[0].value) {
                                    return 1;
                                } else {
                                    return -1;
                                }
                            });

                            graph.data.rows = (graph.heatMapProperties.columnLabelPosition === "top") ? myData[1] :
                                myData[1].reverse();
                            animateHeatmap();
                        }

                    }

                    /**
                     * This function sorts the columns of the heatmap when the user click on a row
                     * It basically sorts all columns objects that belong to the rows so there will be similarity
                     * Process:
                     * 1. sort the columns of the row object that was clicked
                     * 2. according to the this creates new array of columns object to each one of the rows
                     * 3. sort the rows by similarity to create the best heatmap that the user can learn something from
                     *
                     * @param row
                     */
                    function sortByRow (row) {
                        if (row.name !== lastItemNameClicked) {
                            lastItemNameClicked = row.name;
                            var myData = d3.values(angular.copy(graph.data)),
                            //first we are sorting the selected row;
                                selectedRowArr = row.columns.sort(function (a, b)
                                {
                                    if (a.value < b.value) {
                                        return 1;
                                    } else {
                                        return -1;
                                    }
                                    //return d3.descending(b.value);
                                });

                            // then we create rowObjects for each existing row and we sort it
                            angular.forEach(myData[1], function (r) {
                                var newArr = [];

                                angular.forEach(selectedRowArr, function (sra) {

                                    var myColumn = r.columns.filter(
                                        function (obj) {
                                            return obj.name === sra.name;
                                        })[0];

                                    var newRow = {
                                        name: sra.name,
                                        value: myColumn.value,
                                        id: myColumn.id,
                                        x: myColumn.x,
                                        y: myColumn.y
                                    };

                                    newArr.push(newRow);
                                });

                                r.columns = angular.copy(newArr);
                            });

                            row.lastSortedItem = true; // adding true will show the selected row

                            row.columns = setColumnsValue(row.columns);
                            graph.data.columns = angular.copy(row.columns);
                            graph.data.rows = myData[1];
                            animateHeatmap();
                        }
                    }

                    /**
                     * Run over all columns of a row in order to return the value
                     * @param columns
                     * @returns {*}
                     */
                    function setColumnsValue (columns) {
                        angular.forEach(graph.data.columns, function (col) {
                            columns.map(function (c) {
                                if (c.name === col.name) {
                                    c.valueSum = col.valueSum;
                                    c.count = col.count;
                                }
                            });
                        });
                        return columns;
                    }

                    graph.getTooltipText = function (d, attrValue) {
                        return attrValue ? utils.strings.parseValue(attrValue, d) : "";
                    };

                    /**
                     * This function related to the colors' gradient of the heat map.
                     * since we should define the gradient according to the lowest and highest minimum value
                     * we check the poles of the data and send it to the ColorService
                     * @param data
                     * @returns {*[]}
                     */
                    function getValuesExtent (data) {
                        var myExt = [null, 0];
                        angular.forEach(data.rows, function (d) {
                            var ext = d3.extent(d.columns, function (e) {
                                return e.value;
                            });
                            myExt[0] = Math.min(ext[0], myExt[0] || ext[0]);
                            myExt[1] = Math.max(ext[1], myExt[1]);
                        });

                        // in case that the min and max are equals keep the min lower
                        if (myExt[0] === myExt[1]) {
                            myExt[0] = 0;
                        }

                        return myExt;
                    }

                    function calculateCenter () {
                        var mapWidth = (graph.heatMapProperties.cellSize + graph.heatMapProperties.gapBeweenCells) *
                                graph.data.columns.length + graph.heatMapProperties.margin.left,
                            left = (graph.width - mapWidth) / 2;
                        return (left < graph.heatMapProperties.margin.left) ? graph.heatMapProperties.margin.left :
                            left;
                    }

                    function buildHeatMapForTheFirstTime (data) {
                        var container = graph.dataSvg.append("g")
                            .attr("transform",
                            "translate(" + calculateCenter() + "," + graph.heatMapProperties.margin.top + ")");
                        //This is the area that we wish to scroll, if scroll required
                        rootScrollableContainer = container.append("g");
                        rootScrollableContainer.append("g")
                            .selectAll(".rowLabel")
                            .data(data.rows)
                            .enter()
                            .append("text")
                            .text(function (d) {
                                return d.name;
                            })
                            .style("font-size", graph.heatMapProperties.fontSize + "em")
                            .attr("x", function (d) {
                                return d.x;
                            })
                            .attr("y", function (d) {
                                return d.y;
                            })
                            .style("text-anchor", "end")
                            .attr("data-tooltip", scope.view.settings.behavior.rows.tooltip)
                            .attr("transform", function () {
                                return " translate(-10,0)";
                            })
                            .attr("rowIndex", function (d, i) {
                                return i;
                            })
                            .attr("class", function (d) {
                                return "rowLabel mono " + d.headerId;
                            })
                            .on("mouseover", function () {
                                d3.select(this).classed("text-hover", true);
                            })
                            .on("mouseout", function () {
                                d3.select(this).classed("text-hover", false);
                            })
                            .on("click", function (row) {
                                if (scope.view.settings.behavior.rows.isSortingEnabled === null ||
                                    scope.view.settings.behavior.rows.isSortingEnabled !== false) {
                                    sortByRow(row);
                                }
                            });

                        rootScrollableContainer.append("g")
                            .selectAll(".colLabel")
                            .data(data.columns)
                            .enter()
                            .append("text")
                            .text(function (d) {
                                return d.name;
                            })
                            .attr("x", 0)
                            .attr("y", function (d) {
                                return d.x;
                            })
                            .style("font-size", graph.heatMapProperties.fontSize + "em")
                            .style("text-anchor", "left")
                            .attr("transform", function (d) {
                                return " translate(0," + d.translateY + ") rotate (-90)";
                            })
                            .attr("text-anchor", function (d) {
                                return d.textAnchor;
                            })
                            .attr("data-tooltip", scope.view.settings.behavior.columns.tooltip)
                            .attr("class", function (d, i) {
                                return "colLabel mono c" + i + " " + d.headerId;
                            })
                            .on("click", function (column) {
                                sortByColumn(column);
                            })
                            .on("mouseover", function () {
                                d3.select(this).classed("text-hover", true);
                            })
                            .on("mouseout", function () {
                                d3.select(this).classed("text-hover", false);
                            });

                        var row = rootScrollableContainer.append("g").attr("class", "g3")
                            .selectAll(".cellsGroup")
                            .data(data.rows)
                            .enter()
                            .append("g")
                            .attr("class", "row")
                            .attr("index", function (d, i) {
                                return i.toString();
                            });

                        row
                            .selectAll("rect")
                            .data(function (d) {
                                return d.columns;
                            })
                            .enter()
                            .append("rect")
                            .attr("x", function (d) {
                                return d.x;
                            })
                            .attr("y", function (d) {
                                return d.y;
                            })
                            .attr("rowIndedx", function () {
                                return this.parentNode.attributes.index.value;
                            })
                            .attr("class", function (d) {
                                return d.id;
                            })
                            .attr("width", graph.heatMapProperties.cellSize)
                            .attr("height", graph.heatMapProperties.cellSize)
                            .attr("data-tooltip",
                            function (d) {
                                if (d.value > 0) {
                                    return scope.view.settings.behavior.cell.tooltip;
                                }
                            }
                        )
                            .style("fill", function (d) {
                                return graph.heatMapProperties.colorScale(d.value);
                            })
                            .on("mouseover", function (d) {
                                //highlight text
                                d3.select(this).classed("cell-hover", true);
                                var _this = this;
                                d3.selectAll(".rowLabel").classed("text-highlight", function () {
                                    return this.attributes.rowIndex.value === _this.parentNode.attributes.index.value;
                                });
                                d3.selectAll(".colLabel").classed("text-highlight", function (c) {
                                    return c.name === (d.name);
                                });
                            })
                            .on("mouseout", function () {
                                d3.select(this).classed("cell-hover", false);
                                d3.selectAll(".rowLabel").classed("text-highlight", false);
                                d3.selectAll(".colLabel").classed("text-highlight", false);
                            });
                    }

                    function createJsonArr () {
                        var myData = angular.copy(graph.data);
                        angular.forEach(myData.columns, function (column, i) {
                            if (!column.headerId) {
                                column.headerId = "header-" + i;
                            }
                            delete column.x;
                            delete column.y;
                            delete column.translateY;

                            column.translateY = (graph.heatMapProperties.columnLabelPosition === "top") ? -10 :
                            (graph.heatMapProperties.cellSize + graph.heatMapProperties.gapBeweenCells) *
                            graph.data.rows.length + 10;

                            column.textAnchor =
                                (graph.heatMapProperties.columnLabelPosition === "top") ? "start" : "end";

                            column.y = 0;
                            column.x = (graph.heatMapProperties.cellSize + graph.heatMapProperties.gapBeweenCells) * i;

                            //fix to middle
                            column.x += graph.heatMapProperties.cellSize / 1.8;
                        });

                        angular.forEach(myData.rows, function (row, i) {
                            row.x = 0;
                            row.y = graph.heatMapProperties.cellSize * i + (i * graph.heatMapProperties.gapBeweenCells);

                            //fixiToMiddle
                            row.y += graph.heatMapProperties.cellSize / 1.5;

                            if (!row.headerId) {
                                row.headerId = "row-" + i;
                            }

                            angular.forEach(row.columns, function (column, j) {
                                if (!column.id) {
                                    column.id = "heatmap" + i + "-" + j;
                                    column.headerId = "header-" + j;
                                }
                                column.x =
                                    graph.heatMapProperties.cellSize * j + (j * graph.heatMapProperties.gapBeweenCells);
                                column.y =
                                    graph.heatMapProperties.cellSize * i + (i * graph.heatMapProperties.gapBeweenCells);
                            });
                        });
                        return myData;
                    }

                    function animateHeatmap () {
                        var myData = createJsonArr();
                        var t = graph.dataSvg.transition().duration(graph.heatMapProperties.animationDuration);

                        angular.forEach(myData.columns, function (column) {
                            t.selectAll("." + column.headerId)
                                .attr("y", function () {
                                    return column.x;
                                })
                                .attr("x", 0);
                        });

                        angular.forEach(myData.rows, function (row) {
                            angular.forEach(row.columns, function (column) {
                                t.selectAll("." + column.id)
                                    .attr("y", function () {
                                        return column.y;
                                    })
                                    .attr("x", function () {
                                        return column.x;
                                    });
                            });
                            t.selectAll("." + row.headerId)
                                .attr("y", function () {
                                    return row.y;
                                })
                                .attr("x", 0);
                        });
                    }

                    function getMargin (data, self, maxLebelSize, gapBetweenCels, columnLabelPosition) {
                        var rowsMaxLabelSize = 0;
                        var columnsMaxLabelSize = 0;
                        var margin = {top: 0, right: 0, bottom: 100, left: 0};

                        angular.forEach(data.rows, function (row) {
                            if (!rowsMaxLabelSize) {
                                rowsMaxLabelSize = row.name.length;
                            } else {
                                rowsMaxLabelSize = Math.max(row.name.length, rowsMaxLabelSize);
                            }
                        });

                        angular.forEach(data.columns, function (column) {
                            if (!columnsMaxLabelSize) {
                                columnsMaxLabelSize = column.name.length;
                            } else {
                                columnsMaxLabelSize = Math.max(column.name.length, columnsMaxLabelSize);
                            }
                        });

                        columnsMaxLabelSize = (columnsMaxLabelSize > maxLebelSize) ? maxLebelSize : columnsMaxLabelSize;
                        rowsMaxLabelSize = (rowsMaxLabelSize > maxLebelSize) ? maxLebelSize : rowsMaxLabelSize;

                        var labelWidthScale = d3.scale.linear()
                            .domain([0, self.width])
                            .range([0, maxLebelSize]);

                        margin.left = labelWidthScale(rowsMaxLabelSize) * self.width / 3;

                        var labelHeightScale = d3.scale.linear()
                            .domain([0, self.height])
                            .range([0, maxLebelSize]);

                        var marginForColumnsLabel = labelHeightScale(columnsMaxLabelSize) * self.height / 2;

                        margin.top = (columnLabelPosition === "top") ? marginForColumnsLabel : 30;
                        margin.bottom = (columnLabelPosition === "top") ? 100 : marginForColumnsLabel;
                        margin.right = (scope.view.settings.legend) ?
                        (graph.width + gapBetweenCels * graph.data.columns.length) / 10 : 0;

                        return margin;
                    }

                    function createColorDef (legend) {
                        var colorStopCount = legend.items.length;
                        var legendColors = colors.getColors("scale");
                        var gradient = graph.dataSvg.append("defs").append("linearGradient")
                            .attr("id", "legendGradientHeatMap")
                            .attr("x1", 0).attr("x2", 0).attr("y1", 1).attr("y2", 0);

                        gradient.selectAll("stop")
                            .data(legendColors)
                            .enter().append("stop")
                            .attr("stop-color", function (d) {
                                return d;
                            })
                            .attr("offset", function (d, i) {
                                return (100 * i / colorStopCount) + "%";
                            });
                    }

                    function addLegend (x, y, width, height, extent, legendObject, colorsType) {
                        var dataExtent = extent,
                            legendWidth = width,
                            rectHeight = height,
                            legend = graph.dataSvg.append("g")
                                .attr("class", "legend")
                                .attr("transform", "translate(" + (x + legendWidth / 2) + ", " + y + ")");

                        function addGradientLegend () {
                            createColorDef(legendObject);

                            legend.append("rect")
                                .attr("fill", "url(#legendGradientHeatMap)")
                                .attr("width", legendWidth)
                                .attr("height", rectHeight);

                            legend.append("text")
                                .text(dataExtent[1])
                                .attr("data-tooltip", legendObject.items[1].value)
                                .attr("transform", "translate(" + (width / 2) + ", " + (-10) + ")")
                                .attr("text-anchor", "middle");

                            if (dataExtent[0] !== dataExtent[1]) {
                                legend.append("text")
                                    .attr("data-tooltip", legendObject.items[0].value)
                                    .text(dataExtent[0])
                                    .attr("transform", "translate(" + (width / 2) + ", " + (rectHeight + 20) + ")")
                                    .attr("text-anchor", "middle");
                            }
                        }

                        function addTopicsLegend () {
                            var legendColors = colors.getColors(colorsType);
                            var itemGroup = legend
                                .selectAll("g")
                                .data(legendObject.items)
                                .enter()
                                .append("g")
                                .attr("class", "legend")
                                .attr("transform", function (d, i) {
                                    return "translate(0, " + ((width + 10) * i) + ")";
                                });

                            itemGroup.append("rect")
                                .attr("width", width)
                                .attr("height", width)
                                .style("fill", function (d) {
                                    return legendColors[d.color] || legendColors[1]; // if binary we return the colored
                                                                                     // value
                                });

                            itemGroup.append("text")
                                .text(function (d) {
                                    return d.value;
                                })
                                .attr("text-anchor", "left")
                                .attr("transform", function () {
                                    return "translate(" + (width + 5) + ", " + (width) + ")";
                                });
                        }

                        switch (legendObject.items.length) {
                            case 2:
                                addGradientLegend();
                                break;
                            default:
                                addTopicsLegend();
                                break;
                        }
                    }


                }
            };

        }]);
}());
