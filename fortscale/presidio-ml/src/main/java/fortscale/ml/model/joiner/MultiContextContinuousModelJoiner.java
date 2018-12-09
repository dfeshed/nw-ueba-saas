package fortscale.ml.model.joiner;

import fortscale.ml.model.Model;
import fortscale.ml.model.store.ModelDAO;
import fortscale.ml.utils.MaxValuesResult;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MultiContextContinuousModelJoiner extends ContinuousModelJoiner {


    public MultiContextContinuousModelJoiner(int minNumOfMaxValuesSamples, long partitionsResolutionInSeconds, int resolutionStep, int numOfMaxValuesSamples) {
        super(minNumOfMaxValuesSamples, partitionsResolutionInSeconds, resolutionStep, numOfMaxValuesSamples);
    }

    /**
     * Merge multiContext model and single context model
     * @param multiContextModels multiContextModels
     * @param secondaryModels single context model
     * @return merged continuousModel
     */
    public List<Model> joinModels(List<ModelDAO> multiContextModels, List<ModelDAO> secondaryModels) {
        List<Model> models = new ArrayList<>();
        for (ModelDAO multiContextModel : multiContextModels) {
            String multiContextId = multiContextModel.getContextId();
            List<ModelDAO> filteredContextModel = secondaryModels.stream().filter(e -> multiContextId.contains(e.getContextId()))
                    .collect(Collectors.toList());

            Assert.isTrue(filteredContextModel.size() == 1, "Each multiContext model should contain 1 context model");
            MaxValuesResult maxValuesResult = joinModels(multiContextModel.getModel(), filteredContextModel.get(0).getModel());
            Model continuousModel = createContinuousModel(maxValuesResult);
            models.add(continuousModel);
        }
        return models;
    }
}
