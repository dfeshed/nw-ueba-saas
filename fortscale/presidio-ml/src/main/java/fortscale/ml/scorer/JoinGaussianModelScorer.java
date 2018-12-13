package fortscale.ml.scorer;

import fortscale.ml.model.*;
import fortscale.ml.model.builder.gaussian.PartitionedContinuousHistogramModelBuilder;
import fortscale.ml.model.cache.EventModelsCacheService;
import fortscale.ml.model.joiner.PartitionsDataModelJoiner;
import fortscale.ml.utils.MaxValuesResult;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.*;


public class JoinGaussianModelScorer extends GaussianModelScorer {
    private String secondaryModelName;
    private List<String> secondaryModelContextFieldNames;
    private long partitionsResolutionInSeconds;
    private PartitionsDataModelJoiner partitionsDataModelJoiner;
    private int numOfMaxValuesSamples;

    public JoinGaussianModelScorer(String scorerName,
                                   String modelName,
                                   String secondaryModelName,
                                   List<String> additionalModelNames,
                                   List<String> contextFieldNames,
                                   List<String> secondaryModelContextFieldNames,
                                   List<List<String>> additionalContextFieldNames,
                                   String featureName,
                                   int minNumOfPartitionsToInfluence,
                                   int enoughNumOfPartitionsToInfluence,
                                   boolean isUseCertaintyToCalculateScore,
                                   int globalInfluence,
                                   EventModelsCacheService eventModelsCacheService,
                                   long partitionsResolutionInSeconds,
                                   PartitionsDataModelJoiner partitionsDataModelJoiner,
                                   int numOfMaxValuesSamples) {

        super(scorerName, modelName, additionalModelNames, contextFieldNames, additionalContextFieldNames, featureName,
                minNumOfPartitionsToInfluence, enoughNumOfPartitionsToInfluence, isUseCertaintyToCalculateScore, globalInfluence, eventModelsCacheService);

        if (additionalModelNames.size() != 1) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + " expects to get one additional model name");
        }

        this.secondaryModelName = secondaryModelName;
        this.secondaryModelContextFieldNames = secondaryModelContextFieldNames;
        this.partitionsResolutionInSeconds = partitionsResolutionInSeconds;
        this.partitionsDataModelJoiner = partitionsDataModelJoiner;
        this.numOfMaxValuesSamples = numOfMaxValuesSamples;
    }

    @Override
    protected Model getMainModel(AdeRecordReader adeRecordReader) {
        Model model = getModel(adeRecordReader, getModelName(), getContextFieldNames());
        Model secondaryModel = getModel(adeRecordReader, secondaryModelName, secondaryModelContextFieldNames);
        if (secondaryModel == null) {
            return null;
        }
        if ((model != null && !(model instanceof PartitionsDataModel)) && !(secondaryModel instanceof PartitionsDataModel)) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() +
                    ".joinModels expects to get models of type " + PartitionsDataModel.class.getSimpleName());
        }
        MaxValuesResult maxValuesResult = partitionsDataModelJoiner.joinModels((PartitionsDataModel) model, (PartitionsDataModel) secondaryModel);
        long numOfPartitions = maxValuesResult.getMaxValues().keySet().stream().map(x -> (x / partitionsResolutionInSeconds) * partitionsResolutionInSeconds).distinct().count();

        return new PartitionedContinuousHistogramModelBuilder().build(maxValuesResult.getMaxValues().values(), numOfMaxValuesSamples, numOfPartitions);
    }

}
