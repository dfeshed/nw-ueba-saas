package presidio.ade.smart.correlation;

import fortscale.smart.correlation.conf.CorrelationNodeData;
import fortscale.smart.record.conf.SmartRecordConf;
import fortscale.utils.DescendantIterator;
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
        //todo: choose option
        //========================option 1====================================
        Supplier<Stream<TreeNode<CorrelationNodeData>>> streamSupplier = () -> root.getDescendantStream();

        streamSupplier.get().forEach(node -> {
            String featureName = node.getData().getFeature();
            assertIfNodeAlreadyExist(featureName, node);
            featureToTreeNode.put(featureName, node);


        });

        //========================option 2====================================
//        //todo:maybe you can enable the iterator to include the root node.
//        //add root to featureToTreeNode map
//        String rootFeatureName = root.getData().getFeature();
//        assertIfNodeAlreadyExist(rootFeatureName, root);
//        featureToTreeNode.put(rootFeatureName, root);
//
//        //add descendants to featureToTreeNode map
//        DescendantIterator<CorrelationNodeData> descendantIterator = root.getDescendantIterator();
//        while (descendantIterator.hasNext()) {
//            TreeNode<CorrelationNodeData> child = descendantIterator.next();
//            String featureName = child.getData().getFeature();
//            assertIfNodeAlreadyExist(featureName, child);
//            featureToTreeNode.put(featureName, child);
//        }
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
