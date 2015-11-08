package fortscale.ml.model;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class ModelBuilderManagerTest {
    @Test
    public void testCalcNextRunTime() {
        ModelConf modelConf = Mockito.mock(ModelConf.class);
        long buildIntervalInSeconds = 123;
        Mockito.when(modelConf.getBuildIntervalInSeconds()).thenReturn(buildIntervalInSeconds);

        ModelBuilderManager modelManager = new ModelBuilderManager(modelConf);
        long currentTimeInSeconds = 100;
        modelManager.calcNextRunTime(currentTimeInSeconds);

        Assert.assertEquals(currentTimeInSeconds + buildIntervalInSeconds, modelManager.getNextRunTimeInSeconds());
    }
}
