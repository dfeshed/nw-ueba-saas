package presidio.ade.smart;

import fortscale.smart.correlation.conf.CorrelationNodeData;
import fortscale.smart.correlation.conf.FullCorrelation;
import fortscale.smart.record.conf.SmartRecordConf;
import fortscale.utils.Tree;
import fortscale.utils.TreeNode;
import fortscale.utils.logging.Logger;
import javafx.util.Pair;
import presidio.ade.domain.record.aggregated.*;
import scala.util.parsing.combinator.testing.Str;

import java.util.*;
import java.util.stream.Collectors;


public class SmartCorrelationAlgorithm {

    private SmartRecordConf smartRecordConf;
    private Map<String, TreeNode<CorrelationNodeData>> featureToTreeNode;
    private Map<String, FullCorrelation> featureToFullCorrelation;
    private Set<String> correlationFeatures;
    private static final Double CORRELATION_FACTOR = 1.0;
    private static final Double ZERO_CORRELATION_FACTOR = 0.0;
    private static final Double INITIAL_CORRELATION_FACTOR = Double.NEGATIVE_INFINITY;
    private static final Logger logger = Logger.getLogger(SmartCorrelationAlgorithm.class);

    public SmartCorrelationAlgorithm(SmartRecordConf smartRecordConf,
                                     Map<String, TreeNode<CorrelationNodeData>> featureToTreeNode,
                                     Map<String, FullCorrelation> featureToFullCorrelation,
                                     Set<String> correlationFeatures) {
        this.smartRecordConf = smartRecordConf;
        this.featureToTreeNode = featureToTreeNode;
        this.featureToFullCorrelation = featureToFullCorrelation;
        this.correlationFeatures = correlationFeatures;
    }

    /**
     * Update scores of correlated aggregation records by tree correlation and full correlation.
     *
     * @param smartRecords smartRecords
     */
    public void updateCorrelatedFeatures(Collection<SmartRecord> smartRecords) {

        for (SmartRecord smartRecord : smartRecords) {
            Map<String, Double> featureToCorrelationFactor = correlationFeatures.stream().collect(Collectors.toMap(e -> e, e -> INITIAL_CORRELATION_FACTOR));

            List<SmartAggregationRecord> smartAggregationRecords = smartRecord.getSmartAggregationRecords();
            List<Map<String, SmartAggregationRecord>> groups = getGroups(smartAggregationRecords);

            for (Map<String, SmartAggregationRecord> group : groups) {
                Map<String, SmartAggregationRecord> sortedFeatures = sortSmartAggregationRecordGroup(group);
                sortedFeatures = removeAncestors(sortedFeatures, featureToCorrelationFactor);

                for (Map.Entry<String, SmartAggregationRecord> feature : sortedFeatures.entrySet()) {
                    String featureName = feature.getKey();
                    if(featureToCorrelationFactor.get(featureName).equals(INITIAL_CORRELATION_FACTOR)) {
                        featureToCorrelationFactor.put(featureName, CORRELATION_FACTOR);
                    }
                    dfs(featureToTreeNode.get(featureName), featureToCorrelationFactor.get(featureName), sortedFeatures, featureToCorrelationFactor);
                }
            }

            updateFullCorrelationRecords(smartAggregationRecords, featureToCorrelationFactor);
            updateSmartRecordScore(smartRecord, featureToCorrelationFactor);
        }
    }


    /**
     * Remove ancestors, which have lower score than each feature
     *
     * e.g:
     * sortedFeatures: dacyr
     * ancestors: b,a,r,c
     * tree:
     *           a
     *          /\
     *         b c
     *         \  \
     *         d   r
     *              \
     *              y
     * result: dcy
     *
     * @param sortedFeatures <featureName, SmartAggregationRecord> sorted map
     * @return filtered <featureName, SmartAggregationRecord> sorted map
     */
    private Map<String, SmartAggregationRecord> removeAncestors(Map<String, SmartAggregationRecord> sortedFeatures,  Map<String, Double> featureToCorrelationFactor) {

        Set<String> featuresToRemove = new HashSet<>();
        Set<String> ancestors = new HashSet<>();
        for (Map.Entry<String, SmartAggregationRecord> feature : sortedFeatures.entrySet()) {
            String featureName = feature.getKey();
            Set<String> featureAncestors = featureToTreeNode.get(featureName).getAncestors().stream().map(ancestor -> ancestor.getData().getFeature()).collect(Collectors.toSet());
            ancestors.addAll(featureAncestors);

            if (ancestors.contains(featureName)) {
                featuresToRemove.add(featureName);
                featureToCorrelationFactor.put(featureName, ZERO_CORRELATION_FACTOR);
            }
        }

        sortedFeatures = sortedFeatures.entrySet().stream().filter(feature -> !featuresToRemove.contains(feature.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return sortSmartAggregationRecordGroup(sortedFeatures);
    }

    /**
     * Sort group by features score in reverse order (high score to low score)
     * @param group smart aggregation records
     * @return sorted map of <featureName, SmartAggregationRecord>
     */
    private Map<String, SmartAggregationRecord> sortSmartAggregationRecordGroup(Map<String, SmartAggregationRecord> group) {
        return group.entrySet().stream().sorted(
                            Comparator.comparing(feature -> (-1)* getAggregationFeatureScore(feature.getValue()))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                            (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }


    /**
     *
     * @param feature smart aggregation record
     * @return score of smart aggregation record
     */
    private Double getAggregationFeatureScore(SmartAggregationRecord feature) {
        AdeAggregationRecord adeAggregationRecord = feature.getAggregationRecord();
        AggregatedFeatureType aggregatedFeatureType = adeAggregationRecord.getAggregatedFeatureType();
        Double score = null;

        if (aggregatedFeatureType.equals(AggregatedFeatureType.SCORE_AGGREGATION)) {
            score = adeAggregationRecord.getFeatureValue();
        }
        else if (aggregatedFeatureType.equals(AggregatedFeatureType.FEATURE_AGGREGATION)) {
            score = ((ScoredFeatureAggregationRecord) adeAggregationRecord).getScore();
        }
        return score;
    }


    /**
     * calculate new score to SmartAggregationRecord by correlationFactor
     * @param feature smart aggregation record
     * @param correlationFactor correlation factor
     * @return old score
     */
    private Double setAggregationFeatureScore(SmartAggregationRecord feature, Double correlationFactor) {
        AdeAggregationRecord adeAggregationRecord = feature.getAggregationRecord();
        AggregatedFeatureType aggregatedFeatureType = adeAggregationRecord.getAggregatedFeatureType();
        Double score = null;

        if (aggregatedFeatureType.equals(AggregatedFeatureType.SCORE_AGGREGATION)) {
            score = adeAggregationRecord.getFeatureValue();
            adeAggregationRecord.setFeatureValue(score * correlationFactor);
        } else if (aggregatedFeatureType.equals(AggregatedFeatureType.FEATURE_AGGREGATION)) {
            score = ((ScoredFeatureAggregationRecord) adeAggregationRecord).getScore();
            ((ScoredFeatureAggregationRecord) adeAggregationRecord).setScore(score * correlationFactor);
        }

        return score;
    }

    /**
     *
     * recursive method, which search for first children that exist in group and set them new correlation factor.
     *
     * e.g: group: a, d, c
     *      treeNode: a
     *      tree:
     *              a
     *             / \
     *            b  c
     *           /
     *          d
     *         /
     *        k
     *      result: d and c will get new correlation factor.
     *
     *
     * @param treeNode treeNode
     * @param correlationFactor correlationFactor
     * @param group smart aggregation records
     * @param featureToCorrelationFactor featureToCorrelationFactor map
     */
    private void dfs(TreeNode<CorrelationNodeData> treeNode, Double correlationFactor, Map<String, SmartAggregationRecord> group, Map<String, Double> featureToCorrelationFactor) {
        List<TreeNode<CorrelationNodeData>> children = treeNode.getChildren();
        for (TreeNode<CorrelationNodeData> child : children) {
            String childFeature = child.getData().getFeature();
            if (!group.containsKey(childFeature)) {
                dfs(child, correlationFactor, group, featureToCorrelationFactor);
            } else {
                Double childCorrelationFactor = child.getData().getCorrelationFactor();
                featureToCorrelationFactor.put(childFeature, childCorrelationFactor * correlationFactor);
            }
        }
    }

    /**
     * Divide smart aggregation records into groups by trees, the records belongs to.
     * @param smartAggregationRecords smartAggregationRecords
     * @return lists of <featureName,SmartAggregationRecords> map, where each list contain features that belong to the same tree.
     */
    private List<Map<String, SmartAggregationRecord>> getGroups(List<SmartAggregationRecord> smartAggregationRecords) {

        //filter records that does not exist in trees
        List<SmartAggregationRecord> filteredAggregationRecords = smartAggregationRecords.stream().filter(aggr ->
                featureToTreeNode.containsKey(aggr.getAggregationRecord().getFeatureName())).collect(Collectors.toList());

        Map<String, Map<String, SmartAggregationRecord>> groups =
                filteredAggregationRecords.stream().collect(Collectors.groupingBy(e ->  featureToTreeNode.get(e.getAggregationRecord().getFeatureName()).getTree().getName(),
                        Collectors.toMap(e -> e.getAggregationRecord().getFeatureName(), e -> e)));

        return new ArrayList<>(groups.values());
    }


    /**
     * update correlation factor of full correlated records
     * @param smartAggregationRecords smartAggregationRecords
     * @param featureToCorrelationFactor featureToCorrelationFactor map
     */
    private void updateFullCorrelationRecords(List<SmartAggregationRecord> smartAggregationRecords, Map<String, Double> featureToCorrelationFactor) {
        List<FullCorrelation> fullCorrelations = smartRecordConf.getFullCorrelations();

        fullCorrelations.forEach(fullCorrelation -> {
            List<String> features = fullCorrelation.getFeatures();

            //filter records that does not exist in the fullCorrelation.
            //create <featureName, score> map
            Map<String, Double> filteredAggregationRecords = smartAggregationRecords.stream().filter(feature -> features.contains(feature.getAggregationRecord().getFeatureName())).collect(Collectors
                    .toMap(record -> record.getAggregationRecord().getFeatureName(), feature -> getAggregationFeatureScore(feature)));


            Optional<Map.Entry<String, Double>> optional = filteredAggregationRecords.entrySet().stream().max(Map.Entry.comparingByValue());
            if (optional.isPresent()) {
                String featureWithMaxScore = optional.get().getKey();
                featureToCorrelationFactor.putIfAbsent(featureWithMaxScore, CORRELATION_FACTOR);

                filteredAggregationRecords.entrySet().stream().filter(map -> !map.getKey().equals(featureWithMaxScore)).forEach(map ->
                        featureToCorrelationFactor.put(map.getKey(), ZERO_CORRELATION_FACTOR));
            }
        });
    }


    /**
     * Update smart aggregation records that belong to tree or full correlation,
     * with new scores, tree name, full correlation name and old score.
     * @param smartRecord smartRecord
     * @param featureToCorrelationFactor featureToCorrelationFactor map
     */
    private void updateSmartRecordScore(SmartRecord smartRecord, Map<String, Double> featureToCorrelationFactor) {
        List<SmartAggregationRecord> smartAggregationRecords = smartRecord.getSmartAggregationRecords();

        smartAggregationRecords.forEach(smartAggregationRecord -> {
            String feature = smartAggregationRecord.getAggregationRecord().getFeatureName();
            Double correlationFactor = featureToCorrelationFactor.get(feature);

            // if record belong to tree or full correlation
            if(correlationFactor != null) {
                if(correlationFactor.equals(INITIAL_CORRELATION_FACTOR)){
                    logger.error("All the smart aggregation records should contain correlation factor");
                }

                Double oldScore = setAggregationFeatureScore(smartAggregationRecord, correlationFactor);
                smartAggregationRecord.setOldScore(oldScore);
                smartAggregationRecord.setCorrelationFactor(correlationFactor);

                //fill correlationTree name in aggr feature
                TreeNode<CorrelationNodeData> treeNode = featureToTreeNode.get(feature);
                if (treeNode != null) {
                    smartAggregationRecord.setCorrelationTreeName(treeNode.getTree().getName());
                }

                //fill full correlation name in aggr feature
                FullCorrelation fullCorrelation = featureToFullCorrelation.get(feature);
                if (fullCorrelation != null) {
                    smartAggregationRecord.setFullCorrelationName(fullCorrelation.getName());
                }
            }
        });

    }

}
