(function () {
    "use strict";

    function GraphClass (dataEntities, GraphDataView) {
        var graphTypesArr = [
            {id: "scatterPlot", icon: "scatterPlot.svg", name: "Scatter Plot"},
            {id: "horizontalBars", icon: "vertical_bars.svg", name: "Horizontal Bar Chart"},
            {id: "verticalBars", icon: "horizontal_bars.svg", name: "Vertical Bar Chart"},
            {id: "lines", icon: "lines.svg", name: "Line Chart"},
            {id: "geo", icon: "geo.png", name: "Geolocation"},
            {id: "bubbles", icon: "bubbles.svg", name: "Bubbles"},
            {id: "heatmap", icon: "grid.png", name: "Heat Map"}
        ];

        var graphTypes = new Map();
        graphTypesArr.forEach(function (graphType) {
            graphTypes.set(graphType.id, new GraphType(graphType));
        });

        function Graph (config) {
            this.validate(config);

            this.id = config.id;
            this.name = config.name;
            this.type = graphTypes.get(config.type);

            // Graph.widget is just the widget's configuration, NOT the widget itself!
            this.widget = angular.extend(config.widget, {title: this.name, description: config.description});

            this.dataQuery = config.dataQuery;

            if (config.hide) {
                this.hide = config.hide;
            }
            //since each graph is defined for a list of entities
            //make sure each entity the graph is related to is a known entity in the current configuration brought from
            // the server
            if (config.entities) {
                config.entities = config.entities.filter(function (entity) {
                    return dataEntities.getEntityById(entity);
                });
                this.entities = config.entities.map(function (entityId) {
                    return dataEntities.getEntityById(entityId);
                });
            }
        }

        Graph.prototype.validate = function (config) {
            if (!config.id || typeof(config.id) !== "string") {
                throw new Error("Can't create Graph, a string 'id' property is required.");
            }

            if (!config.name || typeof(config.name) !== "string") {
                throw new Error("Can't create Graph, a string 'name' property is required.");
            }

            if (!config.type || typeof(config.type) !== "string") {
                throw new Error("Can't create Graph, a string 'type' property is required.");
            }

            if (!graphTypes.has(config.type)) {
                throw new Error("Unknown graph type, '" + config.type + "'.");
            }

            if (!config.widget || Object(config.widget) !== config.widget) {
                throw new Error("Can't create Graph, a 'widget' object is required.");
            }

            if (!config.dataQuery || !angular.isObject(config.dataQuery)) {
                throw new Error("Can't create graph, an object 'dataQuery' property is required.");
            }

            if (config.entities) {
                if (config.entities.constructor !== Array) {
                    throw new TypeError("Invalid 'entities' property for graph, expected an array but got " +
                        config.entities);
                }
            }
        };

        /**
         * Returns a new GraphDataView object for the specified Explore object
         * @param explore
         * @returns {GraphDataView}
         */
        Graph.prototype.getDataView = function (explore) {
            return new GraphDataView(this, explore);
        };

        function GraphType (config) {
            this.id = config.id;
            this.icon = "images/icons/charts/" + config.icon;
            this.name = config.name;
        }

        return Graph;
    }

    GraphClass.$inject = ["dataEntities", "GraphDataView"];

    angular.module("Explore.DataViews").factory("Graph", GraphClass);
})();
