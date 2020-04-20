(function () {
    'use strict';

    function GraphDataViewClass (utils, DataView, Widget) {

        function createReport (graph, explore) {
            var dataQueryConfig = utils.objects.extend({
                entity: explore.dataEntity.id
            }, graph.dataQuery, explore.getFiltersDataQuery());

            if (!graph.dataView) {
                graph.dataView = graph.getDataView(explore);
            }
            dataQueryConfig.entitiesJoin = graph.dataView.getDataQueryJoin(dataQueryConfig, explore);

            var report = DataView.getDataQueryReport(dataQueryConfig, "graphs." + graph.id);

            return {report: report};
        }

        function GraphDataView (graph, explore) {
            var graphDataView = this;
            this.explore = explore;

            this.graph = utils.objects.copy(graph);

            Widget.loadWidget(this.graph.widget).then(function (widget) {
                graphDataView.widget = Widget.copy(widget);
                graphDataView.widget.setParent(explore);
                graphDataView.widget.setReport(createReport(graph, explore)).getData();
                //the includeExport is used to identify that we are coming from the Explore page - in the future if we
                // need different condition we can add another flag
                if (graphDataView.explore.includeExport) {
                    graphDataView.widget.buttons = [
                        {
                            icon: "#icon-remove",
                            title: "Remove graph",
                            onClick: function () {
                                explore.removeGraph(graph);
                            }
                        }
                    ];
                }
            });
        }

        // This next code is scary! We should definitely look into it when we have time, and kick the use of __proto__.
        // TODO!!
        /* jshint ignore:start */
        GraphDataView.prototype.__proto__ = DataView;
        /* jshint ignore:end */

        /*
         * Returns the default parameters for a Graph Data View.
         */
        GraphDataView.prototype.getDefaultParams = function () {
            return {};
        };

        GraphDataView.prototype.setParams = function (params, updateOnChange) {
            var needUpdate = false;

            if (params[this.getParamName("filters")] !== undefined ||
                params[this.getParamName("default_filters")] !== undefined) {
                needUpdate = this.updateReport();
            }

            if (updateOnChange && needUpdate) {
                this.update();
            }

            return needUpdate;
        };

        /**
         * Updates the dataView's report according to the current graph and explore states.
         * @param updateOnChange
         * @returns {boolean}
         */
        GraphDataView.prototype.updateReport = function (updateOnChange) {
            var needUpdate = false;

            var report = createReport(this.graph, this.explore);
            if (!utils.objects.areEqual(report, this.lastReport)) {
                this.widget.setReport(report);
                this.lastReport = report;
                needUpdate = true;
            }

            if (updateOnChange) {
                this.update();
            }

            return needUpdate;
        };

        GraphDataView.prototype.update = function () {
            this.widget.setParent(this.explore);
            this.widget.refresh();
        };

        return GraphDataView;

    }

    GraphDataViewClass.$inject = ["utils", "DataView", "Widget"];

    angular.module("Explore.DataViews").factory("GraphDataView", GraphDataViewClass);
})();
