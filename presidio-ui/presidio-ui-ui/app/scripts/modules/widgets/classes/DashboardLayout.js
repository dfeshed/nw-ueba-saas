(function () {
    'use strict';

    function DashboardLayout (Widget, conditions) {
        var SPAN_SIZE = 15;

        function DashboardRow (config, dashboard) {
            this.validate(config);

            if (config.className) {
                this.className = config.className;
            }

            if (config.show) {
                this.show = conditions.validateConditions(config.show.conditions, {}, null);
            }
            else {
                this.show = true;
            }

            if (this.show) {
                if (config.widgets) {
                    this.widgets = config.widgets.map(function (widgetConfig) {
                        return new Widget(widgetConfig, dashboard);
                    });
                }

                if (config.columns) {
                    this.columns = config.columns.map(function (rowConfig) {
                        return new DashboardColumn(rowConfig, dashboard);
                    });
                }
            }
        }

        DashboardRow.prototype.validate = function (config) {
            if (!config.widgets && !config.columns) {
                throw new Error("Can't create DashboardRow, it must have either widgets or columns.");
            }

            if (config.widgets && config.widgets.constructor !== Array) {
                throw new TypeError("Invalid 'widgets' property for DashboardRow, expected an array but got " +
                    config.widgets.constructor.name + ".");
            }

            if (config.columns && config.columns.constructor !== Array) {
                throw new TypeError("Invalid 'columns' property for DashboardColumn, expected an array but got " +
                    config.rows.constructor.name + ".");
            }

            if (config.weight) {
                if (typeof(config.weight) !== "number") {
                    throw new TypeError("Invalid weight for DashboardRow. Expected a number but got " + config.weight +
                        ".");
                }

                if (config.weight <= 0) {
                    throw new Error("Invalid weight for DashboardRow. It must be a positive number.");
                }
            }
        };

        /**
         * Returns all the widgets in the row: Columns in the row and the row itself
         */
        DashboardRow.prototype.getAllWidgets = function () {
            var widgets = this.widgets || [];

            if (this.columns) {
                for (var column of this.columns) {
                    widgets = widgets.concat(column.getAllWidgets());
                }
            }

            return widgets;
        };

        function DashboardColumn (config, dashboard) {
            this.validate(config);

            this.weight = config.weight || 1;
            this.span = "span" + SPAN_SIZE;

            if (config.show) {
                this.show = conditions.validateConditions(config.show.conditions, {}, null);
            }
            else {
                this.show = true;
            }

            if (config.className) {
                this.className = config.className;
            }

            if (this.show) {
                if (config.widgets) {
                    this.widgets = config.widgets.map(function (widgetConfig) {
                        return new Widget(widgetConfig, dashboard);
                    });
                }

                if (config.rows) {
                    this.rows = config.rows.map(function (rowConfig) {
                        return new DashboardRow(rowConfig, dashboard);
                    });
                }
            }
        }

        DashboardColumn.prototype.validate = function (config) {
            if (config.show) {
                if (!config.show.conditions) {
                    throw new Error("Can't create dashboardLayout - 'show' exists without conditions.");
                }

                // TODO: Validate the conditions
            }

            if (!config.widgets && !config.rows) {
                throw new Error("Can't create DashboardColumn, it must have either widgets or rows.");
            }

            if (config.widgets && config.widgets.constructor !== Array) {
                throw new TypeError("Invalid 'widgets' property for DashboardColumn, expected an array but got " +
                    config.widgets.constructor.name + ".");
            }

            if (config.rows && config.rows.constructor !== Array) {
                throw new TypeError("Invalid 'rows' property for DashboardColumn, expected an array but got " +
                    config.rows.constructor.name + ".");
            }

            if (config.weight) {
                if (typeof(config.weight) !== "number") {
                    throw new TypeError("Invalid weight for DashboardColumn. Expected a number but got " +
                        config.weight + ".");
                }

                if (config.weight <= 0) {
                    throw new Error("Invalid weight for DashboardColumn. It must be a positive number.");
                }
            }
        };

        /**
         * Returns all the widget in the column: Rows inside the column and the column itself
         */
        DashboardColumn.prototype.getAllWidgets = function () {
            var widgets = this.widgets || [];

            if (this.rows) {
                for (var row of this.rows) {
                    widgets = widgets.concat(row.getAllWidgets());
                }
            }

            return widgets;
        };

        /**
         * Given an array of DashboardColumns, calculates each column's span according to its requested weight and sets
         * the column's span accordingly.
         * @param {[DashboardColumn]} columns
         */
        function setColumnSpans (columns) {
            var totalWeight = columns.length === 1 ? columns[0].weight :
                columns.reduce(function (previousValue, currentValue) {
                    if (!currentValue.weight) {
                        throw new Error("Can't set column classes, weight not found.");
                    }

                    return (!isNaN(previousValue) ? previousValue : previousValue.weight || 0) + currentValue.weight;
                });

            columns.forEach(function (column) {
                column.span = "span" + column.weight * SPAN_SIZE / totalWeight;
            });
        }

        return {
            Column: DashboardColumn,
            Row: DashboardRow,
            setColumnSpans: setColumnSpans
        };
    }

    DashboardLayout.$inject = ["Widget", "conditions"];

    angular.module("Widgets").factory("DashboardLayout", DashboardLayout);
})();
