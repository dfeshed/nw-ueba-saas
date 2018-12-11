package fortscale.ml.model.joiner;

import fortscale.ml.model.PartitionsDataModel;
import fortscale.ml.utils.MaxValuesResult;
import fortscale.ml.utils.PartitionsReduction;

import java.util.Map;

public class PartitionsDataModelJoiner {

    private int minNumOfMaxValuesSamples;
    private long partitionsResolutionInSeconds;
    private int resolutionStep;
    private int minResolution;

    public PartitionsDataModelJoiner(int minNumOfMaxValuesSamples, long partitionsResolutionInSeconds, int resolutionStep, int minResolution) {
        this.minNumOfMaxValuesSamples = minNumOfMaxValuesSamples;
        this.partitionsResolutionInSeconds = partitionsResolutionInSeconds;
        this.resolutionStep = resolutionStep;
        this.minResolution = minResolution;
    }

    /**
     * Merge models:
     * secondaryModel represent events that counted as zero-values if do not exist in main model.
     *
     * merge the models by adding secondaryModel instants as zero values to the main model, if do not exist.
     * Reduce merged model to max values.
     * (notice: we do not lose max values as a result that the resolution decreased by 2 in modelBuilder step util we get enough values.)
     *
     * @param model        main model
     * @param secondaryModel secondary model
     * @return maxValuesResult
     */
    public MaxValuesResult joinModels(PartitionsDataModel model, PartitionsDataModel secondaryModel) {
        Map<Long, Double> instantToValueMap = model.getInstantToValue();
        for (Map.Entry<Long, Double> entry : secondaryModel.getInstantToValue().entrySet()) {
            instantToValueMap.putIfAbsent(entry.getKey(), 0D);
        }
        return PartitionsReduction.reducePartitionsMapToMaxValues(instantToValueMap, model.getInstantStep(), resolutionStep, partitionsResolutionInSeconds, minNumOfMaxValuesSamples, minResolution);
    }
}
