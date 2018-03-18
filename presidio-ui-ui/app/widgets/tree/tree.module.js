(function () {
    'use strict';

    angular.module("TreeWidget", ["Utils", "Chart", "Widgets"]).run(["widgetViews", function (widgetViews) {

        function treeDataParser (view, data) {

            function createLinksIndex () {
                data.forEach(function (row) {
                    var node = linksIndex[row[linkParam]];
                    if (!node) {
                        node = linksIndex[row[linkParam]] = {};
                    }

                    var child = node[row[nameParam]];
                    if (!child) {
                        child = node[row[nameParam]] = [];
                    }

                    child.push(row);
                });
            }

            function getNodeChildren (node) {
                var nodeLinks = linksIndex[node.name],
                    newNode;

                if (nodeLinks) {
                    node.children = [];
                    for (var childName in nodeLinks) {
                        if (nodeLinks.hasOwnProperty(childName)) {
                            newNode = createNode(node.unique, childName, nodeLinks[childName]);
                            node.children.push(newNode);
                            getNodeChildren(newNode);
                        }
                    }
                }
            }

            function createNode (parentUnique, nodeName, nodeData) {
                var node = {name: nodeName, data: nodeData, unique: [parentUnique, nodeName].join(":"), size: 1};
                if (view.settings.expandNode) {
                    node = angular.extend(node, view.settings.expandNode(node));
                }

                return node;
            }

            var root = view.settings.root,
                linksIndex = {},
                linkParam = view.settings.link,
                nameParam = view.settings.nodeName;

            root.unique = root.unique || "__ROOT__";
            createLinksIndex();
            getNodeChildren(root);


            return root;
        }

        widgetViews.registerView("tree", {dataParser: treeDataParser});

    }]);
}());
