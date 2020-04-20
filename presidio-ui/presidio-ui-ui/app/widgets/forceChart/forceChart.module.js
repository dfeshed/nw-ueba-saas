(function () {
    'use strict';

    angular.module("ForceChartWidget", ["Utils", "Widgets", "Events"]).run(["utils", "widgetViews",
        function (utils, widgetViews) {

            function forceDataParser (view, data, params) {
                var settings = view.settings;

                var newData = {
                        nodes: [],
                        links: []
                    },
                    nodeIndexes = {};

                function getRowNodes (row) {
                    var nodeName,
                        nodeLabel,
                        nodeIndex,
                        rowNodeIndexes = [],
                        rowLinkKeys = {};

                    for (var i = 0, nodeSettings; !!(nodeSettings = settings.nodes[i]); i++) {
                        nodeName = row[nodeSettings.name];
                        nodeLabel =
                            nodeSettings.label ? utils.strings.parseValue(nodeSettings.label, row, params) : nodeName;

                        nodeIndex = nodeIndexes[nodeSettings.group + "_" + nodeLabel];
                        if (nodeIndex === undefined) {
                            nodeIndex = nodeIndexes[nodeSettings.group + "_" + nodeLabel] = newData.nodes.length;

                            newData.nodes.push({
                                name: nodeName,
                                count: parseInt(row[nodeSettings.size], 10),
                                group: nodeSettings.group,
                                weight: 0,
                                "userCount": true,
                                "label": nodeLabel,
                                unique: [nodeSettings.group, nodeLabel].join(":"),
                                isCurrentlyFocused: false,
                                field: nodeSettings.name,
                                custom: nodeSettings.custom
                            });

                        }

                        rowNodeIndexes.push(nodeIndex);
                    }

                    var currentLinkIndex,
                        currentSubLinkIndex,
                        linkKey,
                        sourceNodeIndex, targetNodeIndex;

                    for (currentLinkIndex = 0; currentLinkIndex < rowNodeIndexes.length - 1; currentLinkIndex++) {
                        for (currentSubLinkIndex = currentLinkIndex + 1; currentSubLinkIndex < rowNodeIndexes.length;
                             currentSubLinkIndex++) {
                            linkKey = [rowNodeIndexes[currentLinkIndex], rowNodeIndexes[currentSubLinkIndex]].join("_");
                            if (!rowLinkKeys[linkKey]) {
                                sourceNodeIndex = rowNodeIndexes[currentLinkIndex];
                                targetNodeIndex = rowNodeIndexes[currentSubLinkIndex];

                                newData.links.push({
                                    depth: 1,
                                    unique: [newData.nodes[sourceNodeIndex].unique,
                                        newData.nodes[targetNodeIndex].unique].join("_"),
                                    source: sourceNodeIndex,
                                    target: targetNodeIndex
                                });
                                rowLinkKeys[linkKey] = true;

                                newData.nodes[rowNodeIndexes[currentLinkIndex]].weight++;
                                newData.nodes[rowNodeIndexes[currentSubLinkIndex]].weight++;
                            }
                        }
                    }
                }

                angular.forEach(data, getRowNodes);
                return newData;
            }

            widgetViews.registerView("forceChart", {dataParser: forceDataParser});

        }]);
}());
