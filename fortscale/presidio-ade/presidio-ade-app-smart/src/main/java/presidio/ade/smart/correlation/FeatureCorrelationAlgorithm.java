package presidio.ade.smart.correlation;

import fortscale.smart.correlation.conf.CorrelationNodeData;
import fortscale.smart.correlation.conf.FullCorrelation;
import fortscale.utils.DescendantIterator;
import fortscale.utils.TreeNode;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;


public class FeatureCorrelationAlgorithm {

    private CorrelationForest correlationForest;
    private FullCorrelationSet fullCorrelationSet;
    private static final Double FULL_CORRELATION_FACTOR = 0.0;
    private static final Double NO_CORRELATION_FACTOR = 1.0;

    public FeatureCorrelationAlgorithm(CorrelationForest correlationForest, FullCorrelationSet fullCorrelationSet) {
        this.correlationForest = correlationForest;
        this.fullCorrelationSet = fullCorrelationSet;
        validateTreeCorrelationWithFullCorrelation();
    }

    /**
     * Validate TreeCorrelation with FullCorrelation:
     * if a ancestor of b, then a and b can not be in full correlation.
     */
    private void validateTreeCorrelationWithFullCorrelation() {
        correlationForest.getFeatureToTreeNode().forEach((feature, treeNode) -> {
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
     * Update FeatureCorrelations data (tree correlation name, full correlation name and old score)
     * by tree correlation and full correlation.
     *
     *
     * @param descSortedFeatureCorrelations desc sorted featureCorrelations
     */
    public void updateCorrelatedFeatures(Map<String, FeatureCorrelation> descSortedFeatureCorrelations) {
        markAncestorWithFullCorrelation(descSortedFeatureCorrelations);
        applyTreeCorrelation(descSortedFeatureCorrelations);
        applyFullCorrelation(descSortedFeatureCorrelations);
    }


    /**
     * mark ancestors, who have lower score than each feature with FullCorrelation.
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
    private void markAncestorWithFullCorrelation(Map<String, FeatureCorrelation> descSortedFeatureCorrelations) {

        Set<String> ancestors = new HashSet<>();

        for (FeatureCorrelation featureCorrelation : descSortedFeatureCorrelations.values()) {
            TreeNode<CorrelationNodeData> treeNode = correlationForest.getTreeNode(featureCorrelation.getName());

            if (treeNode != null) {
                if (ancestors.contains(featureCorrelation.getName())) {
                    featureCorrelation.setCorrelationFactor(FULL_CORRELATION_FACTOR);
                    featureCorrelation.setTreeName(treeNode.getTree().getName());
                } else {
                    Set<TreeNode<CorrelationNodeData>> treeNodeAncestors = treeNode.getAncestors();
                    ancestors.addAll(treeNodeAncestors.stream().map(m -> m.getData().getFeature()).collect(Collectors.toList()));

                }
            }

        }
    }


    /**
     * set correlation factor and tree name to given featureCorrelation.
     * iterate over descendants that exist in group and set them new correlation factor and tree name.
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
     * @param descSortedFeatureCorrelations desc sorted FeatureCorrelations
     */
    private void applyTreeCorrelation(Map<String, FeatureCorrelation> descSortedFeatureCorrelations) {

        StopIfFeatureExistFunction stopIfFeatureExistFunction = new StopIfFeatureExistFunction(descSortedFeatureCorrelations.values());

        for (FeatureCorrelation featureCorrelation : descSortedFeatureCorrelations.values()) {
            TreeNode<CorrelationNodeData> treeNode = correlationForest.getTreeNode(featureCorrelation.getName());
            if (treeNode != null) {

                //update correlation factor and tree name of given featureCorrelation
                if (featureCorrelation.getCorrelationFactor() == null) {
                    featureCorrelation.setCorrelationFactor(NO_CORRELATION_FACTOR);
                    featureCorrelation.setTreeName(treeNode.getTree().getName());
                }

                //skip ancestors that was removed
                if (!featureCorrelation.getCorrelationFactor().equals(FULL_CORRELATION_FACTOR)) {
                    DescendantIterator<CorrelationNodeData> iterator = treeNode.getDescendantIterator(stopIfFeatureExistFunction);
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
    }


    /**
     * update correlation factor of full correlated records and fullCorrelation name
     *
     * @param descSortedFeatureCorrelations desc sorted FeatureCorrelations
     */
    private void applyFullCorrelation(Map<String, FeatureCorrelation> descSortedFeatureCorrelations) {

        List<String> alreadySeenFullCorrelation = new ArrayList<>();

        for (FeatureCorrelation featureCorrelation : descSortedFeatureCorrelations.values()) {

            String featureName = featureCorrelation.getName();
            FullCorrelation fullCorrelation = fullCorrelationSet.getFullCorrelation(featureName);
            if (fullCorrelation != null) {
                if (alreadySeenFullCorrelation.contains(featureName)) {
                    featureCorrelation.setCorrelationFactor(FULL_CORRELATION_FACTOR);
                } else {
                    alreadySeenFullCorrelation.addAll(fullCorrelation.getFeatures());
                    Double factor = featureCorrelation.getCorrelationFactor() != null ? featureCorrelation.getCorrelationFactor() : NO_CORRELATION_FACTOR;
                    featureCorrelation.setCorrelationFactor(factor);
                }
                featureCorrelation.setFullCorrelationName(fullCorrelation.getName());
            }
        }
    }

}
