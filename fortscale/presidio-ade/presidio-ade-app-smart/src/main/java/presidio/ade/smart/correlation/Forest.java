package presidio.ade.smart.correlation;

import fortscale.smart.correlation.conf.CorrelationNodeData;
import fortscale.smart.record.conf.SmartRecordConf;
import fortscale.utils.DescendantIterator;
import fortscale.utils.Tree;
import fortscale.utils.TreeNode;
import fortscale.utils.logging.Logger;

import java.util.*;

public class Forest {

    private Map<String, TreeNode<CorrelationNodeData>> featureToTreeNode;
    private static final Logger logger = Logger.getLogger(Forest.class);

    public Forest(SmartRecordConf smartRecordConf) {

        featureToTreeNode = new HashMap<>();

        List<Tree<CorrelationNodeData>> trees = smartRecordConf.getTrees();
        for (Tree<CorrelationNodeData> tree : trees) {
            TreeNode<CorrelationNodeData> root = tree.getRoot();
            tree.fillTreeInTreeNodes();
            createFeatureToTreeNodeMap(root);
        }
    }

    /**
     * create featureToTreeNode map
     *
     * @param root root
     */
    private void createFeatureToTreeNodeMap(TreeNode<CorrelationNodeData> root) {
        featureToTreeNode.put(root.getData().getFeature(), root);

        DescendantIterator<CorrelationNodeData> descendantIterator = root.getDescendantIterator();
        while (descendantIterator.hasNext()) {
            TreeNode<CorrelationNodeData> child = descendantIterator.next();
            String featureName = child.getData().getFeature();

            TreeNode<CorrelationNodeData> featureTreeNode = featureToTreeNode.get(featureName);
            if (featureTreeNode != null) {
                logger.error(String.format(
                        "There should not be any intersection between correlation trees. " +
                                "The feature %s should not belong to %s, it already exist in %s.", featureName, child.getTree().getName(), featureToTreeNode.get(featureName).getTree().getName()));
            }
            featureToTreeNode.put(featureName, child);
        }
    }

    public Map<String, TreeNode<CorrelationNodeData>> getFeatureToTreeNode() {
        return featureToTreeNode;
    }

    public TreeNode<CorrelationNodeData> getTreeNode(String feature) {
        return featureToTreeNode.get(feature);
    }
}
