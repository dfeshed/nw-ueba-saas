package presidio.ade.smart.correlation;

import fortscale.smart.correlation.conf.CorrelationNodeData;
import fortscale.smart.record.conf.SmartRecordConf;
import fortscale.utils.DescendantIterator;
import fortscale.utils.Tree;
import fortscale.utils.TreeNode;
import fortscale.utils.logging.Logger;
import org.springframework.util.Assert;

import java.util.*;

public class Forest {

    private Map<String, TreeNode<CorrelationNodeData>> featureToTreeNode;
    private static final Logger logger = Logger.getLogger(Forest.class);

    public Forest(SmartRecordConf smartRecordConf) {
        featureToTreeNode = new HashMap<>();

        List<Tree<CorrelationNodeData>> trees = smartRecordConf.getTrees();
        for (Tree<CorrelationNodeData> tree : trees) {
            tree.fillTreeInTreeNodes();
            createFeatureToTreeNodeMap(tree.getRoot());
        }
    }

    /**
     * create featureToTreeNode map
     *
     * @param root root
     */
    private void createFeatureToTreeNodeMap(TreeNode<CorrelationNodeData> root) {

        //add root to featureToTreeNode map
        String rootFeatureName = root.getData().getFeature();
        validateCorrelationTrees(rootFeatureName, root);
        featureToTreeNode.put(rootFeatureName, root);

        //add descendants to featureToTreeNode map
        DescendantIterator<CorrelationNodeData> descendantIterator = root.getDescendantIterator();
        while (descendantIterator.hasNext()) {
            TreeNode<CorrelationNodeData> child = descendantIterator.next();
            String featureName = child.getData().getFeature();
            validateCorrelationTrees(featureName, child);
            featureToTreeNode.put(featureName, child);
        }
    }

    /**
     * Validate intersection in correlation trees
     *
     * @param featureName
     */
    private void validateCorrelationTrees(String featureName, TreeNode<CorrelationNodeData> treeNode) {
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
