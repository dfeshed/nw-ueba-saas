package fortscale.ml.model.builder.smart_weights;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

/**
 * Created by barak_schuster on 31/08/2017.
 */
public class WeightsModelBuilderConfTest {
    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenBadEntityEventConfName() {
        new WeightsModelBuilderConf("", 1, Collections.emptyList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenNegativeNumOfSimulations() {
        new WeightsModelBuilderConf("smartRecordConfName", -1, null);
    }

    @Test
    public void shouldHaveTheRightFactoryName() {
        Assert.assertEquals("weights_model_builder", new WeightsModelBuilderConf("smartRecordConfName", 1, null).getFactoryName());
    }

    @Test
    public void shouldGetEntityEventConfName() {
        String smartRecordConfName = "smartRecordConfName";
        int numOfSimulations = 1;
        WeightsModelBuilderConf model = new WeightsModelBuilderConf(smartRecordConfName, numOfSimulations, Collections.emptyList());

        Assert.assertEquals(smartRecordConfName, model.getSmartRecordConfName());
        Assert.assertEquals(numOfSimulations, model.getNumOfSimulations());
    }

    @Test
    public void shouldUseProperDefaults() {
        WeightsModelBuilderConf model = new WeightsModelBuilderConf("smartRecordConfName", null, Collections.emptyList());

        Assert.assertEquals(WeightsModelBuilderConf.DEFAULT_NUM_OF_SIMULATIONS, model.getNumOfSimulations());
    }
}