package presidio.ade.test.utils.generators.models;

import fortscale.common.feature.CategoricalFeatureValue;
import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.CategoryRarityModel;
import fortscale.ml.model.builder.CategoryRarityModelBuilder;
import fortscale.ml.model.builder.CategoryRarityModelBuilderConf;
import fortscale.ml.model.metrics.CategoryRarityModelBuilderMetricsContainer;
import fortscale.utils.fixedduration.FixedDurationStrategy;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static org.mockito.Mockito.mock;

/**
 * Created by barak_schuster on 9/10/17.
 */
public class CategoryRarityModelGenerator implements IModelGenerator<CategoryRarityModel> {
    private CategoryRarityModelBuilder categoryRarityModelBuilder;
    private IGenericHistogramGenerator genericHistogramGenerator;
    private int numOfBuckets;

    public CategoryRarityModelGenerator(CategoryRarityModelBuilderMetricsContainer categoryRarityModelBuilderMetricsContainer) {
        genericHistogramGenerator = new FixedGenericHistogramGenerator();
        numOfBuckets = 30;
        categoryRarityModelBuilder = new CategoryRarityModelBuilder(new CategoryRarityModelBuilderConf(numOfBuckets), categoryRarityModelBuilderMetricsContainer);
    }

    public CategoryRarityModelGenerator(CategoryRarityModelBuilderConf conf, CategoryRarityModelBuilderMetricsContainer categoryRarityModelBuilderMetricsContainer) {
        genericHistogramGenerator = new FixedGenericHistogramGenerator();
        numOfBuckets = conf.getNumOfBuckets();
        categoryRarityModelBuilder = new CategoryRarityModelBuilder(conf, categoryRarityModelBuilderMetricsContainer);
    }

    @Override
    public CategoryRarityModel getNext() {
        GenericHistogram genericHistogram = genericHistogramGenerator.getNext();
        CategoricalFeatureValue categoricalFeatureValue = genericHistogram2CategoricalFeatureValue(genericHistogram);

        return (CategoryRarityModel)categoryRarityModelBuilder.build(categoricalFeatureValue);
    }

    private CategoricalFeatureValue genericHistogram2CategoricalFeatureValue(GenericHistogram genericHistogram) {
        CategoricalFeatureValue categoricalFeatureValue = new CategoricalFeatureValue(FixedDurationStrategy.HOURLY);
        for (Map.Entry<String, Double> entry : genericHistogram.getHistogramMap().entrySet()) {
            Instant startTime = Instant.parse("2007-12-03T10:00:00.00Z");
            Double numOfOccurences = entry.getValue();
            while (numOfOccurences >0)
            {
                GenericHistogram histogram = new GenericHistogram();
                histogram.add(entry.getKey(),entry.getValue());
                categoricalFeatureValue.add(histogram,startTime);
                startTime = startTime.plus(1, ChronoUnit.DAYS);
                numOfOccurences--;
            }

        }
        return categoricalFeatureValue;
    }

    private CategoryRarityModelBuilder getCategoryRarityModelBuilder() {
        return categoryRarityModelBuilder;
    }

    public void setNumOfBuckets(int numOfBuckets) {
        this.numOfBuckets = numOfBuckets;
    }

    public void setGenericHistogramGenerator(IGenericHistogramGenerator genericHistogramGenerator) {
        this.genericHistogramGenerator = genericHistogramGenerator;
    }
}
