package fortscale.ml.model.builder;

import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.ContinuousDataModel;
import fortscale.ml.model.Model;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ContinuousHistogramModelBuilderTest {

    ContinuousHistogramModelBuilder modelBuilder;

    private void assertModelEquals(Model actualModel,
                                   long expectedN,
                                   double expectedMean,
                                   double expectedSD,
                                   double expectedMaxValue) {
        ContinuousDataModel expectedModel = new ContinuousDataModel();
        expectedModel.setParameters(expectedN, expectedMean, expectedSD, expectedMaxValue);
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
        Model model = modelBuilder.build(new GenericHistogram());
        assertModelEquals(model, 0, 0, 0, 0);
    }

    @Test
    public void shouldBuildModelBasedOnOneSample() {
        long N = 10;
        double val = 5;
        GenericHistogram modelBuilderData = new GenericHistogram();
        modelBuilderData.add(val, (double)N);
        Model model = modelBuilder.build(modelBuilderData);
        assertModelEquals(model, N, val, 0, val);
    }

    @Test
    public void shouldBuildModelBasedOnTwoSamples() {
        long N1 = 10, N2 = 5;
        double val1 = 30, val2 = 3;
        GenericHistogram modelBuilderData = new GenericHistogram();
        modelBuilderData.add(val1, (double)N1);
        modelBuilderData.add(val2, (double)N2);
        Model model = modelBuilder.build(modelBuilderData);
        assertModelEquals(model, N1 + N2, 21, 12.728, Math.max(val1, val2));
    }
}
