(function () {
    "use strict";

    function graphs ($q, utils, Graph) {

        /**
         * Returns the configuration for all graphs that are available to the specified entity
         * @param dataEntity
         */
        function getGraphsForDataEntity (dataEntity) {
            return getGraphsConfig()
                .then(function () {
                    var entityGraphs = [],
                        currentEntity = dataEntity;

                    do {
                        entityGraphs = (entitiesGraphs[currentEntity.id] || []).concat(entityGraphs);
                    }
                    while (!!(currentEntity = currentEntity.baseEntity));

                    return entityGraphs;
                });
        }

        function getGraphsConfig () {
            if (!graphsConfig) {
                return utils.http.wrappedHttpGet("data/explore/graphs.json").then(function (graphsConfigData) {
                    graphsConfig = {};
                    entitiesGraphs = {};

                    var graph,
                        allEntitiesGraphs = [];

                    function populateEntityGraphs (entity) {
                        var entityGraphs = entitiesGraphs[entity.id];
                        if (!entityGraphs) {
                            entityGraphs = entitiesGraphs[entity.id] = [];
                        }

                        entityGraphs.push(graph);
                    }

                    for (var graphId in graphsConfigData) {
                        if (graphsConfigData.hasOwnProperty(graphId)) {
                            graph = graphsConfig[graphId] = new Graph(graphsConfigData[graphId]);

                            if (graph.entities) {
                                graph.entities.forEach(populateEntityGraphs);
                            }
                            else {
                                allEntitiesGraphs.push(graph);
                            }
                        }
                    }

                    if (allEntitiesGraphs.length) {
                        for (var entityId in entitiesGraphs) {
                            if (entitiesGraphs.hasOwnProperty(entityId)) {
                                entitiesGraphs[entityId] = allEntitiesGraphs.concat(entitiesGraphs[entityId]);
                            }
                        }
                    }

                    return graphsConfig;
                });
            }

            return $q.when(graphsConfig);
        }

        var graphsConfig,
            entitiesGraphs;

        return {
            getGraphsForDataEntity: getGraphsForDataEntity
        };

    }

    graphs.$inject = ["$q", "utils", "Graph"];

    angular.module("Explore.DataViews").factory("graphs", graphs);
})();
