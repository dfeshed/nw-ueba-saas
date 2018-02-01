package presidio.ade.test.utils.generators.models;

import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.TimeModel;
import fortscale.ml.model.builder.TimeModelBuilder;
import fortscale.ml.model.builder.TimeModelBuilderConf;
import fortscale.ml.model.metrics.CategoryRarityModelBuilderMetricsContainer;
import fortscale.ml.model.metrics.TimeModelBuilderMetricsContainer;
import fortscale.ml.model.metrics.TimeModelBuilderPartitionsMetricsContainer;

import static org.mockito.Mockito.mock;

/**
 * Created by barak_schuster on 9/11/17.
 */
public class TimeModelGenerator implements IModelGenerator<TimeModel> {
    private TimeModelBuilder timeModelBuilder;
    private TimeModelBuilderConf timeModelBuilderConf;
    private IGenericHistogramGenerator genericHistogramGenerator;
    private TimeModelBuilderMetricsContainer timeModelBuilderMetricsContainer = mock(TimeModelBuilderMetricsContainer.class);
    private TimeModelBuilderPartitionsMetricsContainer timeModelBuilderPartitionsMetricsContainer = mock(TimeModelBuilderPartitionsMetricsContainer.class);
    private CategoryRarityModelBuilderMetricsContainer categoryRarityModelBuilderMetricsContainer = mock(CategoryRarityModelBuilderMetricsContainer.class);

    public TimeModelGenerator(TimeModelBuilderConf conf) {
        timeModelBuilderConf = conf;
        timeModelBuilder = new TimeModelBuilder(conf, timeModelBuilderMetricsContainer, timeModelBuilderPartitionsMetricsContainer, categoryRarityModelBuilderMetricsContainer);
        GenericHistogram genericHistogram = new GenericHistogram();
        for (int i = 40; i < 50; i++) {
            genericHistogram.add(i*86400,2D);
        }
        genericHistogramGenerator = new FixedGenericHistogramGenerator(genericHistogram);
    }

    @Override
    public TimeModel getNext() {
        return (TimeModel) timeModelBuilder.build(genericHistogramGenerator.getNext());
    }

    public void setTimeModelBuilder(TimeModelBuilder timeModelBuilder) {
        this.timeModelBuilder = timeModelBuilder;
    }

    public void setTimeModelBuilderConf(TimeModelBuilderConf timeModelBuilderConf) {
        this.timeModelBuilderConf = timeModelBuilderConf;
    }

    public void setGenericHistogramGenerator(IGenericHistogramGenerator genericHistogramGenerator) {
        this.genericHistogramGenerator = genericHistogramGenerator;
    }
}
