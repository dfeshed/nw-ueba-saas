package fortscale.ml.model.builder;

import fortscale.ml.model.CategoryRarityGlobalModel;
import fortscale.ml.model.CategoryRarityModel;
import fortscale.ml.model.Model;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryRarityGlobalModelBuilder implements IModelBuilder{
    private static final String NULL_MODEL_BUILDER_DATA_ERROR_MSG = "Model builder data cannot be null.";
    private static final String MODEL_BUILDER_DATA_TYPE_ERROR_MSG = String.format(
            "Model builder data must be of type %s of %s",
            List.class.getSimpleName(),
            CategoryRarityModel.class.getSimpleName()
    );

    private int minNumOfPartitionsToLearnFrom;


    public CategoryRarityGlobalModelBuilder(int minNumOfPartitionsToLearnFrom){
        Assert.isTrue(minNumOfPartitionsToLearnFrom > 0, "minNumOfPartitionsToLearnFrom should be >= 0");

        this.minNumOfPartitionsToLearnFrom = minNumOfPartitionsToLearnFrom;
    }


    @Override
    public Model build(Object modelBuilderData) {
        List<CategoryRarityModel> models = castModelBuilderData(modelBuilderData);
        models = getModelsWithEnoughPartitions(models);
        if(models.isEmpty()){
            return null;
        }
        int numOfBuckets = models.get(0).getOccurrencesToNumOfPartitionsList().size();
        List<Double> occurrencesToNumOfDistinctFeatureValuesList = new ArrayList<>();

        for(int i = 0; i < numOfBuckets; i++){
            double numOfUsers = 0;
            for(CategoryRarityModel categoryRarityModel: models){
                if(categoryRarityModel.getOccurrencesToNumOfPartitionsList().get(i) > 0){
                    numOfUsers++;
                }
            }
            occurrencesToNumOfDistinctFeatureValuesList.add(numOfUsers);
        }

        long maxNumOfPartitions = 0;
        for(CategoryRarityModel categoryRarityModel: models){
            if(categoryRarityModel.getNumOfPartitions() > maxNumOfPartitions){
                maxNumOfPartitions = categoryRarityModel.getNumOfPartitions();
            }
        }

        return new CategoryRarityGlobalModel(occurrencesToNumOfDistinctFeatureValuesList, maxNumOfPartitions, (long) models.size());
    }



    private List<CategoryRarityModel> castModelBuilderData(Object modelBuilderData) {
        Assert.notNull(modelBuilderData, NULL_MODEL_BUILDER_DATA_ERROR_MSG);
        Assert.isInstanceOf(List.class, modelBuilderData, MODEL_BUILDER_DATA_TYPE_ERROR_MSG);
        return (List<CategoryRarityModel>) modelBuilderData;
    }

    private List<CategoryRarityModel> getModelsWithEnoughPartitions(List<CategoryRarityModel> models) {
        return models.stream()
                .filter(model -> model.getNumOfPartitions() >= minNumOfPartitionsToLearnFrom)
                .collect(Collectors.toList());
    }
}
