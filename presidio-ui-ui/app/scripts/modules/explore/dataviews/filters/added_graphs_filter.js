(function () {
    'use strict';

    angular.module("Explore.DataViews")
        .filter('unAddedGraphs', function () {
            return function (graphs) {
                if (!graphs) {
                    return graphs;
                }

                return graphs.filter(function (graph) {
                    return !graph.added;
                });
            };
        })
        //for graphs that need to been shown only on packages but not in the Explore
        .filter('hideGraphs', function () {
            return function (graphs) {
                if (!graphs) {
                    return graphs;
                }

                return graphs.filter(function (graph) {
                    return !graph.hide;
                });
            };
        })
        .filter('addedGraphs', function () {
            return function (graphs) {
                if (!graphs) {
                    return graphs;
                }

                return graphs.filter(function (graph) {
                    return graph.added;
                });
            };
        });
}());
