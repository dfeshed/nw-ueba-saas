package presidio.ade.test.utils.generators.models;

import fortscale.ml.model.CategoryRarityModel;
import fortscale.ml.model.builder.CategoryRarityModelBuilder;
import fortscale.ml.model.builder.CategoryRarityModelBuilderConf;

/**
 * Created by barak_schuster on 9/10/17.
 */
public class CategoryRarityModelGenerator implements IModelGenerator<CategoryRarityModel> {
    private CategoryRarityModelBuilder categoryRarityModelBuilder;
    private IGenericHistogramGenerator genericHistogramGenerator;
    private int numOfBuckets;

    public CategoryRarityModelGenerator() {
        genericHistogramGenerator = new FixedGenericHistogramGenerator();
        numOfBuckets = 30;
        categoryRarityModelBuilder = new CategoryRarityModelBuilder(new CategoryRarityModelBuilderConf(numOfBuckets));
    }

    public CategoryRarityModelGenerator(CategoryRarityModelBuilderConf conf) {
        genericHistogramGenerator = new FixedGenericHistogramGenerator();
        numOfBuckets = conf.getNumOfBuckets();
        categoryRarityModelBuilder = new CategoryRarityModelBuilder(conf);
    }

    @Override
    public CategoryRarityModel getNext() {
        return (CategoryRarityModel)categoryRarityModelBuilder.build(genericHistogramGenerator.getNext());
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
