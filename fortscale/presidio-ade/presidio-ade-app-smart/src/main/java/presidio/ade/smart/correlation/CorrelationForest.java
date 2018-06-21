package presidio.ade.smart.correlation;

import fortscale.smart.correlation.conf.CorrelationNodeData;
import fortscale.utils.Tree;
import fortscale.utils.TreeNode;
import fortscale.utils.logging.Logger;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class CorrelationForest {

    private Map<String, TreeNode<CorrelationNodeData>> featureToTreeNode;
    private static final Logger logger = Logger.getLogger(CorrelationForest.class);

    public CorrelationForest(List<Tree<CorrelationNodeData>> trees ) {
        featureToTreeNode = new HashMap<>();

        for (Tree<CorrelationNodeData> tree : trees) {
            fillFeatureToTreeNodeMap(tree.getRoot());
        }
    }

    /**
     * create featureToTreeNode map
     *
     * @param root root
     */
    private void fillFeatureToTreeNodeMap(TreeNode<CorrelationNodeData> root) {
        root.getDescendantStreamIncludingCurrentNode().forEach(node -> {
            String featureName = node.getData().getFeature();
            assertIfNodeAlreadyExist(featureName, node);
            featureToTreeNode.put(featureName, node);
        });
    }

    /**
     * Validate intersection in correlation trees
     *
     * @param featureName
     */
    private void assertIfNodeAlreadyExist(String featureName, TreeNode<CorrelationNodeData> treeNode) {
        TreeNode<CorrelationNodeData> featureTreeNode = featureToTreeNode.get(featureName);

        if (featureTreeNode != null) {
            String message = String.format(
                    "There should not be any intersection between correlation trees. " +
                            "The feature %s can not belong to %s, it already exist in %s.", featureName, treeNode.getTree().getName(), featureToTreeNode.get(featureName).getTree().getName());
            throw new IllegalArgumentException(message);
        }
    }

    public Map<String, TreeNode<CorrelationNodeData>> getFeatureToTreeNode() {
        return featureToTreeNode;
    }

    public TreeNode<CorrelationNodeData> getTreeNode(String feature) {
        return featureToTreeNode.get(feature);
    }
}
