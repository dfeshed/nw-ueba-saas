package fortscale.ml.model.builder.smart_weights;

import com.google.common.collect.Lists;
import fortscale.ml.model.retriever.smart_data.SmartAggregatedRecordDataContainer;
import fortscale.smart.record.conf.ClusterConf;

import java.time.Instant;
import java.util.*;

/**
 * Created by barak_schuster on 31/08/2017.
 */
public class SmartWeightModelTestUtils {

    public static ClusterConf createClusterConf(String... featureName) {
        return new ClusterConf(Arrays.asList(featureName), 0.01);
    }

    public static ClusterConf createClusterConf(double weight, String... featureName) {
        return new ClusterConf(Arrays.asList(featureName), weight);
    }
    public static List<ClusterConf> createClusterConfs(ClusterConf ...clusterConfs) {
        return Lists.newArrayList(clusterConfs);
    }
    public static class TestData {
        public List<SmartAggregatedRecordDataContainer> smartAggregatedRecordDataContainers;
        public List<ClusterConf> clusterConfs;

        public TestData() {
            String[] featureNames = new String[]{"F1", "F2", "F3", "F4", "F5", "P1", "P2"};
            Random r = new Random(42);
            smartAggregatedRecordDataContainers = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                Map<String, Double> featureNameToScore = new HashMap<>();
                for (String featureName : featureNames) {
                    if (r.nextBoolean()) {
                        double score = r.nextDouble() * (featureName.startsWith("F") ? 100 : 500);
                        featureNameToScore.put(
                                featureName,
                                score
                        );
                        if(featureName.equals("F1")){
                            featureNameToScore.put("F6",score);
                        }
                    }
                }
                smartAggregatedRecordDataContainers.add(new SmartAggregatedRecordDataContainer(Instant.ofEpochMilli(1234), featureNameToScore));
            }
            ClusterConf c1 = createClusterConf("F1");
            ClusterConf c2 = createClusterConf("F1", "F2");
            ClusterConf c3 = createClusterConf("F3");
            ClusterConf c4 = createClusterConf("F4", "F5");
            ClusterConf c5 = createClusterConf("P1");
            ClusterConf c6 = createClusterConf("P2");
            ClusterConf c7 = createClusterConf("F6");
            clusterConfs = createClusterConfs(c1, c2, c3, c4, c5, c6, c7);
        }
    }


}
