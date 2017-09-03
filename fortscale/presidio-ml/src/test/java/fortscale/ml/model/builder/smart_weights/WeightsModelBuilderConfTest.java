package fortscale.ml.model.builder.smart_weights;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by barak_schuster on 31/08/2017.
 */
public class WeightsModelBuilderConfTest {
    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenBadEntityEventConfName() {
        new WeightsModelBuilderConf("", 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenNegativeNumOfSimulations() {
        new WeightsModelBuilderConf("smartRecordConfName", -1);
    }

    @Test
    public void shouldHaveTheRightFactoryName() {
        Assert.assertEquals("weights_model_builder", new WeightsModelBuilderConf("smartRecordConfName", 1).getFactoryName());
    }

    @Test
    public void shouldGetEntityEventConfName() {
        String smartRecordConfName = "smartRecordConfName";
        int numOfSimulations = 1;
        WeightsModelBuilderConf model = new WeightsModelBuilderConf(smartRecordConfName, numOfSimulations);

        Assert.assertEquals(smartRecordConfName, model.getSmartRecordConfName());
        Assert.assertEquals(numOfSimulations, model.getNumOfSimulations());
    }

    @Test
    public void shouldUseProperDefaults() {
        WeightsModelBuilderConf model = new WeightsModelBuilderConf("smartRecordConfName", null);

        Assert.assertEquals(100, model.getNumOfSimulations());
    }
}