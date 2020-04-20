(function () {
    'use strict';

    angular.module('TableWidget')
        .directive('tableData', ["widgets", "$compile", function (widgets, $compile) {
            return {
                restrict: 'A',
                require: "?ngModel",
                link: function postLink (scope, element, attrs) {
                    var data,
                        settings,
                        table = d3.select(element[0]),
                        classes = {
                            headerSortEnabled: "widget-table-sort-visible"
                        },
                        dataUnWatcher,
                        settingsUnWatcher;

                    scope.$on("$destroy", function () {
                        element.find("*").addBack().off();
                        element.off();
                        dataUnWatcher();
                        settingsUnWatcher();
                        element.empty();
                    });

                    dataUnWatcher = scope.$watch(attrs.tableData, function (value) {
                        if (!value) {
                            element.empty();
                            element.off();
                        }
                        else {
                            data = value;
                            render();
                        }
                    });

                    settingsUnWatcher = scope.$watch(attrs.tableSettings, function (value) {
                        if (value) {
                            settings = value;
                            render();
                        }
                    });

                    function render () {
                        element.empty();

                        if (!data || !settings) {
                            return;
                        }

                        if (data.caption) {
                            element.append($("<caption></caption>").text(data.caption));
                        }

                        if (data.rows && data.rows.length) {
                            createHeader();
                            createBody();
                        }

                        $compile(element.contents())(scope);

                    }

                    function createHeader () {
                        var headerRow = table.append("thead").append("tr");
                        headerRow.selectAll("th").data(settings.fields).enter().append("th")
                            .attr("class", function (field) {
                                return field.sortBy ? 'widget-table-header-sortable' : '';
                            })
                            .attr("colspan", function (field) {
                                return field.headerColspan;
                            })
                            .style("width", function (field) {
                                return field.width;
                            });

                        headerRow.selectAll("th").each(function (field, fieldIndex) {
                            var th = d3.select(this);
                            if (field.sortBy) {
                                th.append("a")
                                    .attr("class", "widget-table-sort-link" +
                                    (field.sortDirection && !field.sortDisabled ? " widget-table-sort-enabled" : "") +
                                    (field.sortDisabled ? " sort-disabled" : ""))
                                    .attr("ng-click", "sortTable(view.settings.fields[" + fieldIndex +
                                    "], view, getWidgetParams(widget))")
                                    .text(field.name)
                                    .append(getFieldHeaderSort);
                            }
                            else {
                                if (field.headerIcon) {
                                    th.append("i")
                                        .attr("class", "table-widget-header-icon icon-" + field.headerIcon)
                                        .attr("title", field.name);
                                }
                                else if (field.name) {
                                    th.text(field.name);
                                }

                                if (field.tooltip) {
                                    th.append("i")
                                        .attr("class", "icon-question-sign tooltip-icon")
                                        .attr("title", field.tooltip);
                                }
                            }
                        });
                    }

                    function createBody () {
                        if (scope.widget.isLoading) {
                            return;
                        }

                        var rows = table.append("tbody").selectAll("tr").data(data.rows).enter().append("tr");
                        rows.each(function (rowData, rowIndex) {
                            var cells = d3.select(this).selectAll("td").data(function (row) {
                                return row.display;
                            }).enter().append("td")
                                .attr("class", function (d, i) {
                                    try {
                                        return d.noValue ? 'widget-table-cell-no-value' :
                                        settings.fields[i].className || null;
                                    } catch (e) {
                                        //console.error("Can't set class for field #%d", i, e);
                                    }
                                })
                                .attr("rowspan", function (d) {
                                    return d.rowSpan || null;
                                })
                                .attr("colspan", function (d) {
                                    return d.colspan;
                                });

                            cells.each(function (cellData, cellIndex) {
                                var cell = d3.select(this);

                                cell.append(cellData.link || cellData.field.events && cellData.field.events.click ?
                                    function (d) {
                                        return getCellLink(d, rowIndex, cellIndex);
                                    } : getCellText);

                                if (cellData.externalLinks) {
                                    cell.append(function (d) {
                                        return getCellExternalLinks(d, rowIndex, cellIndex);
                                    });
                                }

                                if (cellData.tags) {
                                    cell.append(function (d) {
                                        return getCellTags(d);
                                    });
                                }

                                if (cellData.menu && cellData.menu.items) {
                                    cell.append(function (d) {
                                        return getCellMenu(d, rowIndex, cellIndex);
                                    });
                                }
                            });
                        });
                    }

                    function getFieldHeaderSort (field) {
                        var sort = document.createElement("span"),
                            caretUp = document.createElement("i"),
                            caretDown = document.createElement("i");

                        sort.className = "widget-table-sort";
                        if (field.sortDirection) {
                            if (field.sortDirection === 1) {
                                sort.classList.add("widget-table-sort-up");
                            } else if (field.sortDirection === -1) {
                                sort.classList.add("widget-table-sort-down");
                            }
                        }

                        caretUp.className = "fa fa-caret-up";
                        caretDown.className = "fa fa-caret-down";

                        if (!field.sortDirection) {
                            caretUp.classList.add(classes.headerSortEnabled);
                            caretDown.classList.add(classes.headerSortEnabled);
                        }
                        else if (field.sortDirection === 1) {
                            caretUp.classList.add(classes.headerSortEnabled);
                        } else if (field.sortDirection === -1) {
                            caretDown.classList.add(classes.headerSortEnabled);
                        }

                        sort.appendChild(caretUp);
                        sort.appendChild(caretDown);

                        return sort;
                    }

                    function getCellIcon (d) {
                        var icon = d3.select(document.createElement("span"));
                        icon.style(d.icon.style)
                            .attr("title", d.icon.tooltip || d.tooltip);

                        icon.append("i").attr("class", "fa fa-" + d.icon.type);
                        return icon[0][0];
                    }

                    function getCellTags (field) {
                        var tags = d3.select(document.createElement("div"));
                        tags.attr("class", "widget-table-tags");

                        tags.selectAll(".tag").data(field.tags).enter().append("span")
                            .attr("class", function (d) {
                                return "tag " + (d.className || "widget-table-tag");
                            })
                            .attr("title", function (d) {
                                return d.name;
                            })
                            .text(function (d) {
                                return d.text;
                            });

                        return tags[0][0];
                    }

                    function getCellText (d) {
                        var text = document.createElement("span");
                        if (d.tooltip) {
                            text.setAttribute("title", d.tooltip);
                        }

                        if (d.icon && d.icon.type) {
                            text.appendChild(getCellIcon(d));
                        }

                        text.innerHTML = text.innerHTML + d.display;
                        return text;
                    }

                    function getCellLink (d, rowIndex, cellIndex) {
                        var link = document.createElement("a");
                        if (d.style && d.style.color) {
                            link.style.color = d.style.color;
                        }

                        if (settings.dragContents) {
                            link.classList.add("draggable");
                        }

                        if (d.field.events && d.field.events.click) {
                            link.setAttribute("ng-click",
                                "tableFieldClick($event, view.data.rows[" + rowIndex + "].display[" + cellIndex +
                                "], " + cellIndex + ", view.data.rows[" + rowIndex + "], " + rowIndex + ")");
                        } else {
                            link.setAttribute("href", d.link);
                        }

                        if (d.tooltip) {
                            link.setAttribute("title", d.tooltip);
                        }

                        if (d.icon && d.icon.type) {
                            link.appendChild(getCellIcon(d));
                        }

                        var linkSpan = document.createElement("span");
                        linkSpan.innerHTML = d.display;
                        link.appendChild(linkSpan);

                        return link;
                    }

                    function getCellExternalLinks (d, rowIndex, cellIndex) {
                        var externalLinksContainer = d3.select(document.createElement("span")),
                            externalLinks = externalLinksContainer.selectAll("a").data(d.externalLinks).enter()
                                .append("a")
                                .attr("href", function (externalLink) {
                                    return externalLink.href || externalLink.link;
                                })
                                .attr("ng-click", function (externalLink, i) {
                                    return externalLink.href || externalLink.link ? null :
                                    "externalLinkClick($event, view.data.rows[" + rowIndex + "].display[" + cellIndex +
                                    "].externalLinks[" + i + "], widget.rawData[" + rowIndex + "])";
                                })
                                .attr("class", function (externalLink) {
                                    return "table-external-link" +
                                        (externalLink.pinned ? " table-external-link-pinned" : "");
                                })
                                .attr("title", function (externalLink) {
                                    return externalLink.currentTooltip || externalLink.tooltip;
                                });

                        externalLinks.each(function (d) {
                            var linkElement = d3.select(this);
                            if (d.icon) {
                                linkElement.append("i")
                                    .attr("class", "icon-" + d.icon);
                            }
                            else if (d.text) {
                                linkElement.append("span").text(d.text);
                            }
                        });

                        return externalLinksContainer[0][0];
                    }

                    function getCellMenu (d, rowIndex) {

                        var menu = $('<menu class="dropdown dropdown-directive"><a class="clickable dropdown-toggle ' +
                            'dropdown-delegate-toggle hidden-phone"><b class="caret"></a></menu>')[0];
                        menu.menu = d.menu;
                        menu.data = scope.widget.rawData[rowIndex];
                        return menu;
                    }
                }
            };
        }]);
}());
