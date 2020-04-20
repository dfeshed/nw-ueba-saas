(function () {
    'use strict';

    angular.module("LinksWidget", ["Utils", "Chart", "Widgets"]).run(["utils", "widgetViews",
        function (utils, widgetViews) {

            function linksDataParser (view, data, params) {
                var settings = view.settings;

                var newData = {
                        nodes: [],
                        links: []
                    },
                    nodeIndexes = {},
                    rowLinkKeys = {},
                    linksData = view.settings.getLinksData ? {} : null;

                function getRowNodes (row) {
                    var nodeName,
                        nodeLabel,
                        existingNode,
                        nodeUnique,
                        rowNodeIndexes = [],
                        node;

                    for (var i = 0, nodeSettings; !!(nodeSettings = settings.nodes[i]); i++) {
                        nodeName = row[nodeSettings.name];
                        nodeLabel =
                            nodeSettings.label ? utils.strings.parseValue(nodeSettings.label, row, params) : nodeName;
                        nodeUnique = [nodeSettings.group, nodeLabel].join(":");
                        existingNode = nodeIndexes[nodeUnique];

                        if (existingNode === undefined) {
                            node = {
                                name: nodeName,
                                group: nodeSettings.group,
                                weight: 0,
                                index: newData.nodes.length,
                                label: nodeLabel,
                                unique: nodeUnique,
                                isCurrentlyFocused: false,
                                field: nodeSettings.name,
                                custom: nodeSettings.custom
                            };
                            if (settings.centerNodeName && settings.centerNodeName === nodeName) {
                                node.centered = true;
                                node.fixed = true;
                                node.group = settings.centerMachineGroup || "Center";
                            }

                            newData.nodes.push(node);
                            existingNode = nodeIndexes[nodeUnique] = node;
                        }

                        rowNodeIndexes.push(existingNode.index);
                    }

                    var currentLinkIndex,
                        currentSubLinkIndex,
                        linkKey,
                        link,
                        sourceNodeIndex, targetNodeIndex,
                        currentSourceNode, currentTargetNode;

                    for (currentLinkIndex = 0; currentLinkIndex < rowNodeIndexes.length - 1; currentLinkIndex++) {
                        for (currentSubLinkIndex = currentLinkIndex + 1; currentSubLinkIndex < rowNodeIndexes.length;
                             currentSubLinkIndex++) {
                            linkKey = [rowNodeIndexes[currentLinkIndex], rowNodeIndexes[currentSubLinkIndex]].join("_");
                            if (!rowLinkKeys[linkKey]) {
                                sourceNodeIndex = rowNodeIndexes[currentLinkIndex];
                                targetNodeIndex = rowNodeIndexes[currentSubLinkIndex];

                                currentSourceNode = newData.nodes[rowNodeIndexes[currentLinkIndex]];
                                currentTargetNode = newData.nodes[rowNodeIndexes[currentSubLinkIndex]];

                                if (view.settings.connectSameGroup !== false ||
                                    currentSourceNode.group !== currentTargetNode.group) {
                                    link = {
                                        depth: 1,
                                        unique: [currentSourceNode.unique, currentTargetNode.unique].join("_"),
                                        source: currentSourceNode,
                                        target: currentTargetNode
                                    };

                                    if (linksData) {
                                        linksData[linkKey] = link.data = [row];
                                    }

                                    newData.links.push(link);
                                    rowLinkKeys[linkKey] = true;

                                    currentSourceNode.weight++;
                                    currentTargetNode.weight++;
                                }
                            }
                            else if (linksData) {
                                linksData[linkKey].push(row);
                            }
                        }
                    }
                }

                data.forEach(getRowNodes);

                return newData;
            }

            widgetViews.registerView("links", {dataParser: linksDataParser});

        }]);
}());
