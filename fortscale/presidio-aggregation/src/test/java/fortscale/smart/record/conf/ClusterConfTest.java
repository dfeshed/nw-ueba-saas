package fortscale.smart.record.conf;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Collections;
import java.util.List;

@RunWith(JUnit4.class)
public class ClusterConfTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenEmptyAggregatedFeatureEventNames() {
        new ClusterConf(Collections.emptyList(), 0.1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenEmptyAggregatedFeatureEventName() {
        new ClusterConf(Collections.singletonList(""), 0.1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenNegativeWeight() {
        new ClusterConf(Collections.singletonList("F"), -0.1);
    }

    @Test
    public void shouldNotFailGivenZeroWeight() {
        new ClusterConf(Collections.singletonList("F"), 0D);
    }

    @Test
    public void shouldBuildClusterSpecsAccordingToData() {
        List<String> aggregatedFeatureEventNames = Collections.singletonList("F");
        double weight = 0.1;
        ClusterConf clusterConf = new ClusterConf(aggregatedFeatureEventNames, weight);

        Assert.assertEquals(aggregatedFeatureEventNames, clusterConf.getAggregationRecordNames());
        Assert.assertEquals(weight, clusterConf.getWeight(), 0.0000001);
    }
}
