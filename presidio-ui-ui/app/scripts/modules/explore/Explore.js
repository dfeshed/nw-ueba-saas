(function () {
    "use strict";

    function ExploreClass (appConfig, FilterCollection, DataEntity, TableDataView, state, queryOperators, Filter,
        graphs, dataViewTypes, Graph, utils) {

        /**
         * Gets the default filters collection for the Explore model
         * @param explore
         * @param existingFilters Existing filters collection, i.e from params, to use in the default filters. If
         *     something is missing from the existing filters, it's added here.
         * @returns {*}
         */
        function getDefaultFilters (explore, existingFilters) {
            if (!explore.dataEntity.performanceField) {
                return null;
            }

            var defaultFilters = existingFilters || new FilterCollection();

            // The first is for events, the second for sessions:
            var timeField = explore.dataEntity.fields.get("event_time_utc") ||
                explore.dataEntity.fields.get("session_time_utc");
            var eventScoreField = explore.dataEntity.performanceField.field;

            if (!defaultFilters.hasFilterForField(timeField)) {
                var timeFilter = new Filter(timeField);

                var daysAgo = appConfig.getConfigValue('ui.explore', 'daysRange');
                timeFilter.value = {
                    timeStart: utils.date.getMoment('-' + daysAgo + 'days').startOf("day").toDate(),
                    timeEnd: utils.date.getMoment('now').endOf("day").toDate()
                };

                timeFilter.operator = queryOperators.operators.get("dateRange");
                defaultFilters.addFilter(timeFilter, 0);
            }

            if (!defaultFilters.hasFilterForField(eventScoreField)) {
                var scoreFilter = new Filter(eventScoreField);
                scoreFilter.value = explore.dataEntity.performanceField.value;
                scoreFilter.operator = queryOperators.operators.get("greaterThanOrEquals");
                defaultFilters.addFilter(scoreFilter, 1);
            }
            //make sure the score filter value is formatted as a number
            defaultFilters.filters.forEach(function (filter) {
                if (filter.field.id.indexOf("score") > -1) {
                    filter.value = Number(filter.value);
                }
            });
            defaultFilters.unDirty();
            return defaultFilters;
        }

        /**
         * Returns the IDs of fields that are in default filters. To be used by the filters directive, which should
         * exclude them.
         * @returns {*|Array}
         */
        function getDefaultFilterFieldIds (explore) {
            var defaultFilterIds = explore.defaultFilters && explore.defaultFilters.filters.map(function (filter) {
                    return filter.field.id;
                });

            // TODO: This is a bit ugly, should be replaced to use only event_time.
            //(hack) The defaultFilterIds is used originally for the default filters, but it is used here for excluding
            // filters from being manually added.
            if (defaultFilterIds && ~defaultFilterIds.indexOf("event_time_utc")) {
                defaultFilterIds.push("event_time");
            }
            if (defaultFilterIds && ~defaultFilterIds.indexOf("session_time_utc")) {
                ["start_time", "start_time_utc", "end_time", "end_time_utc"].forEach(function (value) {
                    defaultFilterIds.push(value);
                });
            }

            return defaultFilterIds;
        }

        /**
         * Since filters are provided in the URL as a single string, there's a need to parse them into an object that
         * the FilterCollection can read. getFilterParams does just that.
         * @param filterParams
         * @returns {*}
         */
        function getFilterParams (filterParams) {

            // no filters
            if (filterParams === null) {
                filterParams = [];
            }

            // In case we got string instead of array, we need to parse it into an array
            if (filterParams && typeof(filterParams) === "string") {

                // split the string of the filters to array of filters. The format is:
                // entity.field=value(,entity.field=value)* Please note that the value might contains commas
                var filterParamsArr = filterParams.split(",");
                filterParams = [];
                for (var i = 0; i < filterParamsArr.length; i++) {
                    var rawFilter = filterParamsArr[i];

                    if (rawFilter) { // ignore empty parts

                        //each filter looks like this: <filter Name>=<filterValue> .the operator is part of filterValue
                        // (only if different than '=').
                        var rawFilterParts = rawFilter.match(/^(\w+\.\w+)=(.+)$/);

                        if (rawFilterParts) {
                            // add filter part to result
                            var obj = {};
                            obj[rawFilterParts[1]] = rawFilterParts[2] === "_null_" ? null : rawFilterParts[2];
                            filterParams[filterParams.length] = obj;
                        } else {
                            // the filter is part of previous filter
                            if (filterParams.length > 0) {
                                // add it as part of the value of the previous part
                                var prevObj = filterParams[filterParams.length - 1];
                                prevObj[Object.keys(prevObj)[0]] += "," + rawFilter;
                            } else {
                                console.warn("Invalid filter param, '" + rawFilter + "', ignoring.");
                            }
                        }
                    }
                }
            }

            return filterParams;
        }

        function validateSettings (settings) {
            for (var settingName in settings) {
                if (settings.hasOwnProperty(settingName)) {
                    if (!settingProperties.has(settingName)) {
                        throw new Error("Unknown setting for Explore, '" + settingName + "'.");
                    }
                }
            }
        }

        function setDataViews (explore) {
            if (explore.mode === dataViewTypes.types.table) {
                // table view
                explore.dataViews = [new TableDataView(explore)];
            } else {
                // graphs view
                if (explore.addedGraphs) {
                    // go over existing graph and refresh the data
                    explore.dataViews = explore.addedGraphs.map(function (dataView) {
                        dataView.updateReport(true);
                        return dataView;
                    });
                }
                else {
                    explore.dataViews = [];
                }
            }
        }

        function getGraphsParams (explore) {
            var graphIds = explore.addedGraphs && explore.addedGraphs.length ?
                explore.addedGraphs.map(function (dataView) {
                    return dataView.graph.id;
                }).join(",") :
                null;

            return {graphs: graphIds};
        }

        function getGraphsParamsId (explore) {
            var graphIds = explore.addedGraphs && explore.addedGraphs.length ?
                explore.addedGraphs.map(function (dataView) {
                    return dataView.graph.id;
                }).join(",") :
                null;

            return graphIds;
        }

        function setGraphsParam (explore) {
            state.setParams(getGraphsParams(explore));
        }

        // viewOnly for packages: to remove filtering and export
        var settingProperties = new Set(["viewOnly", "includeExport"]);

        /**
         * Constructor for Explore model objects
         * @param dataEntity
         * @param settings
         * @param params
         * @constructor
         */
        function Explore (dataEntity, settings, params) {

            var explore = this;

            if (dataEntity) {
                if (!(dataEntity instanceof DataEntity)) {
                    throw new TypeError("Invalid data entity for Explore object. Expected an instance of DataEntity.");
                }

                this.dataEntity = dataEntity;
                // Do not update URL (setToUrl = false)
                state.setParams({"eventsEntity": dataEntity.eventsEntity}, false);
                this._mode = dataViewTypes.types[params.mode || "table"];

                // setting properties
                this.includeExport = settings.includeExport;
                this.viewOnly = settings.viewOnly;

                this.addedGraphs = [];

                graphs.getGraphsForDataEntity(dataEntity).then(function (entityGraphs) {
                    explore.graphs = entityGraphs.map(function (graph) {
                        graph.added = false;
                        return graph;
                    });

                    //This is for adding the graph from the params into the graph list of the explore
                    if (params.graphs && typeof(params.graphs) === "string") {
                        // calculate if we should move the the graphs tab
                        var changeViewToGraphs = shouldChangeViewToGraphs(params);
                        // Add the graphs to the explore (might be hidden)
                        params.graphs.split(",").forEach(function (graphId) {
                            explore.addGraph(graphId, false, changeViewToGraphs);
                        });
                    }
                });
            }
            else {
                throw new Error("Can't create Explore object, missing dataEntityId.");
            }

            this.filters = new FilterCollection();

            if (settings) {
                validateSettings(settings);
            }

            this.settings = settings || {};
            this.defaultFilters = getDefaultFilters(this);
            this.defaultFilterFieldIds = getDefaultFilterFieldIds(this);
            this.addedAllGraphs = false;

            /*
             * First create the data views of the explore page,
             * then set the page's parameters using setParams
             * (which recursively sets the child data views' params).
             */
            setDataViews(this);
            if (params) {
                explore.setParams(params);
            }
        }

        Explore.prototype.__defineGetter__("mode", function () {
            return this._mode;
        });

        Explore.prototype.__defineSetter__("mode", function (mode) {
            if (mode !== this._mode) {
                this._mode = mode;
                setDataViews(this);
                state.setParams({mode: mode.id});
            }
        });

        Explore.prototype.getParams = function () {
            var params = {
                filters: this.filters.getParams(),
                graphs: getGraphsParamsId(this)
            };

            if (this.defaultFilters) {
                params.default_filters = this.defaultFilters.getParams();
            }

            return params;
        };

        Explore.prototype.getState = function () {
            return state.currentParams;
        };

        /**
         * calculate if we should move the the graphs tab
         * @param obj the container of the mode
         * @returns {*|boolean} true if we should use the graphs view
         */
        function shouldChangeViewToGraphs (obj) {
            return obj.mode && (obj.mode === "graphs" || obj.mode === dataViewTypes.types.graphs);
        }

        Explore.prototype.setParams = function (params) {
            var filterParams;
            var defaultFilterParams;

            if (!params) {
                return this;
            }

            if (params.filters !== undefined) {
                // Get new filters
                filterParams = getFilterParams(params.filters);
            } else if (params.initFilters) {
                // Reset flag
                params.initFilters = false;
                // Empty filters
                filterParams = [];
            }

            // If defined, set new filters
            if (filterParams) {
                this.filters.setParams(filterParams);
            }

            if (params.default_filters !== undefined) {
                // Get new default filters
                defaultFilterParams = getFilterParams(params.default_filters);
            } else if (params.initDefaultFilters) {
                // Reset flag
                params.initDefaultFilters = false;
                // Empty default filters
                defaultFilterParams = [];
            }

            // If defined, set new default filters
            if (defaultFilterParams && this.defaultFilters) {
                this.defaultFilters = getDefaultFilters(this, this.defaultFilters.setParams(defaultFilterParams));
            }

            if (this.dataViews) {
                this.dataViews.forEach(function (dataView) {
                    if (params.initParams) {
                        params.initParams = false;
                        // Override with default parameters
                        params = dataView.getDefaultParams();
                    }
                    var dataViewChanged = dataView.setParams(params);
                    if (dataViewChanged || filterParams || defaultFilterParams) {
                        dataView.update();
                    }
                });
            }

            if (params.graphs && typeof(params.graphs) === "string") {
                var explore = this;
                // calculate if we should move the the graphs tab
                var changeViewToGraphs = shouldChangeViewToGraphs(explore);
                // Add the graphs to the explore (might be hidden)
                params.graphs.split(",").forEach(function (graphId) {
                    explore.addGraph(graphId, false, changeViewToGraphs);
                });
            }
            return this;
        };

        Explore.prototype.unDirty = function () {
            this.filters.unDirty();
            if (this.defaultFilters) {
                this.defaultFilters.unDirty();
            }
        };

        Explore.prototype.updateView = function () {
            if (this.dataViews) {
                this.dataViews.forEach(function (dataView) {
                    dataView.update();
                });
            }

            return this;
        };

        /**
         * Gets the DataQuery config (conditions only) relevant to the filters and default filters of the Explore
         * object.
         * @returns {{conditions: (*|Array|string)}}
         */
        Explore.prototype.getFiltersDataQuery = function () {
            var filtersConditions = this.filters.getDataQuery().conditions,
                defaultFiltersConditions = this.defaultFilters && this.defaultFilters.getDataQuery().conditions;

            var dataQueryConfig = {
                conditions: filtersConditions
            };

            if (!filtersConditions) {
                dataQueryConfig.conditions = defaultFiltersConditions;
            } else if (defaultFiltersConditions) {
                dataQueryConfig.conditions.terms =
                    dataQueryConfig.conditions.terms.concat(defaultFiltersConditions.terms);
            }

            return dataQueryConfig;
        };

        /**
         * Adds a graph to the dataViews of the Explore object.
         * @param graph
         * @param updateParams
         * @param changeViewToGraphs
         * @changeViewToGraphs true if we want to change the view to graphs
         */
        Explore.prototype.addGraph = function (graph, updateParams, changeViewToGraphs) {
            if (typeof(graph) === "string") {
                graph = this.getGraphById(graph);
            }

            if (!graph) {
                return this;
            }

            if (!(graph instanceof Graph)) {
                throw new TypeError("Can't add graph to explore, expected an instance of Graph.");
            }

            if (graphExistsInExplore(this, graph.id)) {
                return this;
            }

            graph.dataView = graph.getDataView(this);
            // The default is to change the view (if the changeViewToGraphs is undefined)
            if (changeViewToGraphs === undefined || changeViewToGraphs) {
                this.dataViews.splice(0, 0, graph.dataView);
            }
            this.addedGraphs.splice(0, 0, graph.dataView);

            if (this.addedGraphs.length === this.graphs.length) {
                this.addedAllGraphs = true;
            }

            if (updateParams !== false) {
                setGraphsParam(this);
            }

            graph.added = true;

            return this;
        };

        function graphExistsInExplore (explore, graphId) {
            return explore.addedGraphs.some(function (graphDataView) {
                return graphDataView.graph.id === graphId;
            });
        }

        /**
         * Removes a graph that was added to the Explore object
         * @param graph
         * @returns {Explore}
         */
        Explore.prototype.removeGraph = function (graph) {
            if (!graph) {
                return this;
            }

            if (!(graph instanceof Graph)) {
                throw new TypeError("Can't add graph to explore, expected an instance of Graph.");
            }

            this.dataViews.splice(this.dataViews.indexOf(graph.dataView), 1);
            this.addedGraphs.splice(this.addedGraphs.indexOf(graph.dataView), 1);
            graph.added = false;
            this.addedAllGraphs = false;
            setGraphsParam(this);

            return this;
        };

        Explore.prototype.getGraphById = function (graphId) {
            if (!this.graphs) {
                return null;
            }

            for (var graphDataView of this.graphs) {
                if (graphDataView.id === graphId) {
                    return graphDataView;
                }
            }

            return null;
        };

        return Explore;

    }

    ExploreClass.$inject =
        ["appConfig", "FilterCollection", "DataEntity", "TableDataView", "state", "queryOperators", "Filter", "graphs",
            "dataViewTypes", "Graph", "utils"];

    angular.module("Explore").factory("Explore", ExploreClass);

})();
