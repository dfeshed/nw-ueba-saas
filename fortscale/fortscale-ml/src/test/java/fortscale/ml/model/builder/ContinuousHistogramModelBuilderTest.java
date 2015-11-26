package fortscale.ml.model.builder;

import fortscale.ml.model.Model;
import fortscale.ml.model.prevalance.field.ContinuousDataModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ContinuousHistogramModelBuilderTest {

    ContinuousHistogramModelBuilder modelBuilder;

    private void assertModelEquals(Model actualModel, long expectedN, double expectedMean, double expectedSD) {
        ContinuousDataModel expectedModel = new ContinuousDataModel();
        expectedModel.setParameters(expectedN, expectedMean, expectedSD);
        Assert.assertEquals(expectedModel, actualModel);
    }

    @Before
    public void setUp() {
        modelBuilder = new ContinuousHistogramModelBuilder();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenNullAsInput() {
        modelBuilder.build(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenIllegalInputType() {
        modelBuilder.build("");
    }

    @Test
    public void shouldBuildEmptyModelIfGivenZeroSamples() {
        Model model = modelBuilder.build(new HashMap<Double, Long>());
        assertModelEquals(model, 0, 0, 0);
    }

    @Test
    public void shouldBuildModelBasedOnOneSample() {
        long N = 10;
        double val = 5;
        Map<Double, Double> modelBuilderData = new HashMap<>();
        modelBuilderData.put(val, (double) N);
        Model model = modelBuilder.build(modelBuilderData);
        assertModelEquals(model, N, val, 0);
    }

    @Test
    public void shouldBuildModelBasedOnTwoSamples() {
        long N1 = 10, N2 = 5;
        double val1 = 30, val2 = 3;
        Map<Double, Double> modelBuilderData = new HashMap<>();
        modelBuilderData.put(val1, (double) N1);
        modelBuilderData.put(val2, (double) N2);
        Model model = modelBuilder.build(modelBuilderData);
        assertModelEquals(model, N1 + N2, 21, 12.727922061357855);
    }
}
