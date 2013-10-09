angular.module("ForceChartWidget").factory("forceChartWidgetData", function(){
    var methods = {
        getData: function(view, data, params){
            var settings = view.settings;

            var newData = {
                    nodes: [],
                    links: []
                },
                nodeIndexes = {};

            function getRowNodes(row){
                var nodeName,
                    nodeIndex,
                    rowNodeIndexes = [],
                    rowLinkKeys = {};

                for(var i= 0, nodeSettings; nodeSettings = settings.nodes[i]; i++){
                    nodeName = row[nodeSettings.name]
                    nodeIndex = nodeIndexes[nodeSettings.group + "_" + nodeName];
                    if (nodeIndex === undefined){
                        nodeIndex = nodeIndexes[nodeSettings.group + "_" + nodeName] = newData.nodes.length;

                        newData.nodes.push({
                            name: nodeName,
                            count:parseInt(row[nodeSettings.size], 10),
                            group: nodeSettings.group,
                            linkCount: 0,
                            "userCount": true,
                            "label": nodeName,
                            unique: nodeIndex,
                            isCurrentlyFocused: false
                        });

                    }

                    rowNodeIndexes.push(nodeIndex);
                }

                var currentLinkIndex,
                    currentSubLinkIndex,
                    linkKey;

                for(currentLinkIndex = 0; currentLinkIndex < rowNodeIndexes.length - 1; currentLinkIndex++){
                    for(currentSubLinkIndex = currentLinkIndex + 1; currentSubLinkIndex < rowNodeIndexes.length; currentSubLinkIndex++){
                        linkKey = [rowNodeIndexes[currentLinkIndex], rowNodeIndexes[currentSubLinkIndex]].join("_");
                        if (!rowLinkKeys[linkKey]){
                            newData.links.push({
                                depth: 1,
                                unique: newData.links.length,
                                source: rowNodeIndexes[currentLinkIndex],
                                target: rowNodeIndexes[currentSubLinkIndex]
                            });
                            rowLinkKeys[linkKey] = true;

                            newData.nodes[rowNodeIndexes[currentLinkIndex]].linkCount++;
                            newData.nodes[rowNodeIndexes[currentSubLinkIndex]].linkCount++;
                        }
                    }
                }
            }

            angular.forEach(data, getRowNodes);
            return newData;
        }
    };

    return methods;
});
