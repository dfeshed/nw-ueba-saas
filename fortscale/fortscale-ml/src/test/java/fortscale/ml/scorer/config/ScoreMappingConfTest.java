package fortscale.ml.scorer.config;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashMap;
import java.util.Map;


@RunWith(JUnit4.class)
public class ScoreMappingConfTest {
    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfSettingNull() {
        ScoreMappingConf conf = new ScoreMappingConf();
        conf.setMapping(null);
    }

    @Test
    public void shouldAdd0And100MappingPointsWhenCreated() {
        ScoreMappingConf conf = new ScoreMappingConf();
        Map<Double, Double> mapping = conf.getMapping();
        Assert.assertEquals(2, mapping.size());
        Assert.assertEquals(0, mapping.get(0D), 0.0001);
        Assert.assertEquals(100, mapping.get(100D), 0.0001);
    }

    @Test
    public void shouldAdd0And100MappingPointsWhenSettingMapping() {
        ScoreMappingConf conf = new ScoreMappingConf();
        Map<Double, Double> mapping = new HashMap<>();
        conf.setMapping(mapping);
        mapping = conf.getMapping();
        Assert.assertEquals(2, mapping.size());
        Assert.assertEquals(0, mapping.get(0D), 0.0001);
        Assert.assertEquals(100, mapping.get(100D), 0.0001);
    }

    @Test
    public void shouldNotAdd0And100MappingPointsWhenSettingMappingWith0And100() {
        ScoreMappingConf conf = new ScoreMappingConf();
        Map<Double, Double> mapping = new HashMap<>();
        double mappingOf0 = 10;
        double mappingOf100 = 90;
        mapping.put(0D, mappingOf0);
        mapping.put(100D, mappingOf100);
        conf.setMapping(mapping);
        mapping = conf.getMapping();
        Assert.assertEquals(mappingOf0, mapping.get(0D), 0.00001);
        Assert.assertEquals(mappingOf100, mapping.get(100D), 0.00001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenNonMonotonicMapping() {
        ScoreMappingConf conf = new ScoreMappingConf();
        Map<Double, Double> mapping = new HashMap<>();
        mapping.put(0D, 10D);
        mapping.put(100D, 9D);
        conf.setMapping(mapping);
    }
}
