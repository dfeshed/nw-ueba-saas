package presidio.ade.smart.correlation;

import fortscale.smart.correlation.conf.CorrelationNodeData;
import fortscale.smart.correlation.conf.FullCorrelation;
import fortscale.utils.DescendantIterator;
import fortscale.utils.TreeNode;
import fortscale.utils.logging.Logger;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;


public class FeatureCorrelationAlgorithm {

    private Forest forest;
    private FullCorrelationSet fullCorrelationSet;
    private static final Double FULL_CORRELATION_FACTOR = 1.0;
    private static final Double NO_CORRELATION_FACTOR = 0.0;

    public FeatureCorrelationAlgorithm(Forest forest, FullCorrelationSet fullCorrelationSet) {
        this.forest = forest;
        this.fullCorrelationSet = fullCorrelationSet;
        validateTreeCorrelationWithFullCorrelation();
    }

    /**
     * Validate TreeCorrelation with FullCorrelation:
     * if a ancestor of b, then a and b can not be in full correlation.
     */
    private void validateTreeCorrelationWithFullCorrelation() {
        forest.getFeatureToTreeNode().forEach((feature, treeNode) -> {
            Set<String> ancestors = treeNode.getAncestors().stream().map(ancestor -> ancestor.getData().getFeature()).collect(Collectors.toSet());

            FullCorrelation fullCorrelation = fullCorrelationSet.getFullCorrelation(feature);
            if (fullCorrelation != null) {
                ancestors.forEach(ancestor -> {
                    Assert.isTrue(!fullCorrelation.getFeatures().contains(ancestor), String.format(
                            "There should not be intersection between correlation trees and full correlation. The feature %s and %s can not be full correlated.", ancestor, feature));
                });
            }
        });
    }

    /**
     * Update scores of correlated aggregation records by tree correlation and full correlation.
     *
     * @param descSortedFeatureCorrelations desc sorted featureCorrelations
     */
    public void updateCorrelatedFeatures(Map<String, FeatureCorrelation> descSortedFeatureCorrelations) {

        removeAncestors(descSortedFeatureCorrelations);
        List<String> features = descSortedFeatureCorrelations.values().stream().map(feature -> feature.getName()).collect(Collectors.toList());
        StopConditionFunction stopConditionFunction = new StopConditionFunction(features);

        for (FeatureCorrelation featureCorrelation : descSortedFeatureCorrelations.values()) {
            updateTreeCorrelation(featureCorrelation, descSortedFeatureCorrelations, stopConditionFunction);
        }

        updateFullCorrelation(descSortedFeatureCorrelations);
    }


    /**
     * Remove ancestors, which have lower score than each feature
     * Update ancestors correlationFactor and treeName
     * <p>
     * e.g:
     * sortedFeatures: dacyr
     * ancestors: b,a,r,c
     * tree:
     * a
     * /\
     * b c
     * \  \
     * d   r
     * \
     * y
     * result: dcy
     *
     * @param descSortedFeatureCorrelations <featureName, FeatureCorrelation> desc sorted map
     */
    private void removeAncestors(Map<String, FeatureCorrelation> descSortedFeatureCorrelations) {

        Set<String> ancestors = new HashSet<>();

        for (FeatureCorrelation featureCorrelation : descSortedFeatureCorrelations.values()) {
            TreeNode<CorrelationNodeData> treeNode = forest.getTreeNode(featureCorrelation.getName());

            if (treeNode != null) {
                Set<TreeNode<CorrelationNodeData>> treeNodeAncestors = treeNode.getAncestors();

                if (ancestors.contains(featureCorrelation.getName())) {
                    featureCorrelation.setCorrelationFactor(NO_CORRELATION_FACTOR);
                    featureCorrelation.setTreeName(treeNode.getTree().getName());
                } else {
                    ancestors.addAll(treeNodeAncestors.stream().map(m -> m.getData().getFeature()).collect(Collectors.toList()));

                }
            }

        }
    }


    /**
     * recursive method, which search for first descendant that exist in group and set them new correlation factor and tree name.
     * <p>
     * e.g: featureCorrelations: a, d, c
     * featureCorrelation: a
     * tree:
     * a
     * / \
     * b  c
     * /
     * d
     * /
     * k
     * result: d and c will get new correlation factor.
     *
     * @param featureCorrelation            featureCorrelation
     * @param descSortedFeatureCorrelations desc sorted FeatureCorrelations
     * @param stopConditionFunction         stopConditionFunction
     */
    private void updateTreeCorrelation(FeatureCorrelation featureCorrelation, Map<String, FeatureCorrelation> descSortedFeatureCorrelations, StopConditionFunction stopConditionFunction) {

        TreeNode<CorrelationNodeData> treeNode = forest.getTreeNode(featureCorrelation.getName());
        if (treeNode != null) {

            if (featureCorrelation.getCorrelationFactor() == null) {
                featureCorrelation.setCorrelationFactor(FULL_CORRELATION_FACTOR);
                featureCorrelation.setTreeName(treeNode.getTree().getName());
            }

            //skip ancestors that was removed
            if (!featureCorrelation.getCorrelationFactor().equals(NO_CORRELATION_FACTOR)) {
                DescendantIterator<CorrelationNodeData> iterator = treeNode.getDescendantIterator(stopConditionFunction);
                while (iterator.hasNext()) {
                    TreeNode<CorrelationNodeData> descendant = iterator.next();
                    String featureName = descendant.getData().getFeature();
                    FeatureCorrelation childFeatureCorrelation = descSortedFeatureCorrelations.get(featureName);
                    Double correlationFactor = featureCorrelation.getCorrelationFactor() * descendant.getData().getCorrelationFactor();
                    childFeatureCorrelation.setCorrelationFactor(correlationFactor);
                    childFeatureCorrelation.setTreeName(descendant.getTree().getName());
                }
            }
        }
    }


    /**
     * update correlation factor of full correlated records and fullCorrelation name
     *
     * @param descSortedFeatureCorrelations desc sorted FeatureCorrelations
     */
    private void updateFullCorrelation(Map<String, FeatureCorrelation> descSortedFeatureCorrelations) {

        List<String> featureCorrelationToRemove = new ArrayList<>();

        for (FeatureCorrelation featureCorrelation : descSortedFeatureCorrelations.values()) {

            String featureName = featureCorrelation.getName();
            FullCorrelation fullCorrelation = fullCorrelationSet.getFullCorrelation(featureName);
            if (fullCorrelation != null) {
                if (featureCorrelationToRemove.contains(featureName)) {
                    featureCorrelation.setCorrelationFactor(NO_CORRELATION_FACTOR);
                    featureCorrelation.setFullCorrelationName(fullCorrelation.getName());
                } else {
                    List<String> features = fullCorrelation.getFeatures();
                    featureCorrelationToRemove.addAll(features.stream().filter(feature -> !feature.equals(featureName)).collect(Collectors.toList()));
                    Double factor = featureCorrelation.getCorrelationFactor() != null ? featureCorrelation.getCorrelationFactor() : FULL_CORRELATION_FACTOR;
                    featureCorrelation.setCorrelationFactor(factor);
                }
            }
        }
    }

}
