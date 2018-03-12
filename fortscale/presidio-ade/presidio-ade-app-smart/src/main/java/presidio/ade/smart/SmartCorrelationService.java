package presidio.ade.smart;

import fortscale.smart.correlation.conf.CorrelationNodeData;
import fortscale.smart.correlation.conf.FullCorrelation;
import fortscale.smart.record.conf.SmartRecordConf;
import fortscale.utils.Tree;
import fortscale.utils.TreeNode;
import org.springframework.util.Assert;
import presidio.ade.domain.record.aggregated.SmartRecord;

import java.util.*;
import java.util.stream.Collectors;


public class SmartCorrelationService {

    private SmartRecordConf smartRecordConf;
    private Map<String, TreeNode<CorrelationNodeData>> featureToTreeNode;
    private Map<String, FullCorrelation> featureToFullCorrelation;
    private List<String> features;
    private SmartCorrelationAlgorithm smartCorrelationAlgorithm;


    public SmartCorrelationService(SmartRecordConf smartRecordConf) {
        this.smartRecordConf = smartRecordConf;
        this.featureToTreeNode = new LinkedHashMap<>();
        this.featureToFullCorrelation = new LinkedHashMap<>();
        this.features = new ArrayList<>();

        List<Tree<CorrelationNodeData>> trees = smartRecordConf.getTrees();
        for (Tree<CorrelationNodeData> tree : trees) {
            TreeNode<CorrelationNodeData> root = tree.getRoot();
            fillFeaturesInTree(root, tree.getFeatures());
            fillParentsInTreeNode(root, null);
            fillAncestorsInTreeNode(root, new HashSet<>());
            fillTreeInTreeNode(tree.getRoot(), tree);
        }

        List<FullCorrelation> fullCorrelations = smartRecordConf.getFullCorrelations();
        buildFeatureToFullCorrelationMap(fullCorrelations);
        validateTreeCorrelationWithFullCorrelation();

        features.addAll(featureToTreeNode.keySet());
        features.addAll(featureToFullCorrelation.keySet());
        smartCorrelationAlgorithm = new SmartCorrelationAlgorithm(smartRecordConf, featureToTreeNode, featureToFullCorrelation, features);
    }

    /**
     * Validate TreeCorrelation with FullCorrelation:
     * if a ancestor of b, then a and b can not be in full correlation.
     */
    private void validateTreeCorrelationWithFullCorrelation() {
        featureToTreeNode.forEach((feature,treeNode) -> {
           Set<String> ancestors = treeNode.getAncestors().stream().map(ancestor -> ancestor.getData().getFeature()).collect(Collectors.toSet());
            FullCorrelation fullCorrelation = featureToFullCorrelation.get(feature);
            if(fullCorrelation != null) {
                ancestors.forEach(ancestor -> {
                    Assert.isTrue(!fullCorrelation.getFeatures().contains(ancestor), String.format(
                            "There should not be intersection between correlation trees and full correlation. The feature %s and %s can not be full correlated.", ancestor, feature));
                });
            }
        });
    }

    /**
     * Update scores of correlated aggregation records by tree correlation and full correlation.
     * @param smartRecords smartRecords
     */
    public void updateCorrelatedFeatures(Collection<SmartRecord> smartRecords) {
        smartCorrelationAlgorithm.updateCorrelatedFeatures(smartRecords);
    }


    /**
     * Fill set of all features in Tree
     * Build featureToTreeNode map
     *
     * @param treeNode treeNode
     * @param features features
     */
    private void fillFeaturesInTree(TreeNode<CorrelationNodeData> treeNode, Set<TreeNode<CorrelationNodeData>> features) {
        String feature = treeNode.getData().getFeature();
        Assert.isTrue(!featureToTreeNode.containsKey(feature), String.format(
                "There should not be any intersection between correlation trees. The feature %s already exist.", feature));

        featureToTreeNode.put(feature, treeNode);

        features.add(treeNode);
        List<TreeNode<CorrelationNodeData>> children = treeNode.getChildren();
        children.forEach(child -> {
            fillFeaturesInTree(child, features);
        });
    }


    /**
     * Fill parent for each treeNode recursively
     *
     * @param treeNode treeNode
     * @param parent   parent
     */
    private void fillParentsInTreeNode(TreeNode<CorrelationNodeData> treeNode, TreeNode<CorrelationNodeData> parent) {
        treeNode.setParent(parent);
        List<TreeNode<CorrelationNodeData>> children = treeNode.getChildren();

        children.forEach(child -> {
            fillParentsInTreeNode(child, treeNode);
        });
    }

    /**
     * Fill ancestore for each treeNode recursively
     * @param treeNode treeNode
     * @param parentAncestors ancestors
     */
    private void fillAncestorsInTreeNode(TreeNode<CorrelationNodeData> treeNode, Set<TreeNode<CorrelationNodeData>> parentAncestors) {

        List<TreeNode<CorrelationNodeData>> children = treeNode.getChildren();

        children.forEach(child -> {
            Set<TreeNode<CorrelationNodeData>> childAncestors = child.getAncestors();
            childAncestors.addAll(parentAncestors);
            childAncestors.add(treeNode);
            fillAncestorsInTreeNode(child, childAncestors);
        });
    }

    /**
     * fill tree of each treeNode
     * @param treeNode treeNode
     * @param tree tree
     */
    private void fillTreeInTreeNode(TreeNode<CorrelationNodeData> treeNode, Tree<CorrelationNodeData> tree) {
        treeNode.setTree(tree);
        List<TreeNode<CorrelationNodeData>> children = treeNode.getChildren();
        children.forEach(child -> fillTreeInTreeNode(child, tree));
    }

    /**
     * Build feature to fullCorrelation map
     * @param fullCorrelations fullCorrelations
     */
    private void buildFeatureToFullCorrelationMap(List<FullCorrelation> fullCorrelations) {
        for (FullCorrelation fullCorrelation : fullCorrelations) {
            fullCorrelation.getFeatures().forEach(feature -> {
                Assert.isTrue(!featureToFullCorrelation.containsKey(feature), String.format(
                        "There should not be any intersection between full correlation features. The feature %s already exist.", feature));
                featureToFullCorrelation.put(feature, fullCorrelation);
            });
        }
    }


    public Map<String, TreeNode<CorrelationNodeData>> getFeatureToTreeNode() {
        return featureToTreeNode;
    }

    public List<String> getFeatures() {
        return features;
    }

    public Map<String, FullCorrelation> getFeatureToFullCorrelation() {
        return featureToFullCorrelation;
    }

}
