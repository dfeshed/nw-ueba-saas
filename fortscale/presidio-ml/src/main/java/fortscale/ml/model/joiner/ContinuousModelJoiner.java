package fortscale.ml.model.joiner;

import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.Model;
import fortscale.ml.model.PartitionsDataModel;
import fortscale.ml.model.builder.gaussian.ContinuousHistogramModelBuilder;
import fortscale.ml.utils.MaxValuesResult;
import fortscale.ml.utils.PartitionsReduction;

import java.util.Collection;
import java.util.Map;

public class ContinuousModelJoiner {

    private int minNumOfMaxValuesSamples;
    private long partitionsResolutionInSeconds;
    private int resolutionStep;
    private int numOfMaxValuesSamples;

    public ContinuousModelJoiner(int minNumOfMaxValuesSamples, long partitionsResolutionInSeconds, int resolutionStep,  int numOfMaxValuesSamples) {
        this.minNumOfMaxValuesSamples = minNumOfMaxValuesSamples;
        this.partitionsResolutionInSeconds = partitionsResolutionInSeconds;
        this.resolutionStep = resolutionStep;
        this.numOfMaxValuesSamples = numOfMaxValuesSamples;
    }

    /**
     * Merge models:
     * secondaryModel represent events that counted as zero-values if do not exist in main model.
     *
     * calculate max values of the main model with secondaryModel resolution (higher resolution),
     * merge the models by adding secondaryModel instants and zero values to the main model, if do not exist.
     * Reduce merged model to max values.
     * (notice: we do not lose max values as a result that the resolution decreased by 2 in model builder step util we get enough values.)
     *
     * @param model        main model
     * @param secondaryModel secondary model
     * @return maxValuesResult
     */
    public MaxValuesResult joinModels(Model model, Model secondaryModel) {
        if (!(model instanceof PartitionsDataModel && secondaryModel instanceof PartitionsDataModel)) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() +
                    ".calculateScore expects to get a models of type " + PartitionsDataModel.class.getSimpleName());
        }

        PartitionsDataModel partitionsDataModel = (PartitionsDataModel) model;
        PartitionsDataModel partitionsDataSecondaryModel = (PartitionsDataModel) secondaryModel;
        Map<Long, Double> instantToValueMap = partitionsDataModel.getInstantToValue();

        if (partitionsDataModel.getResolutionInSeconds() < partitionsDataSecondaryModel.getResolutionInSeconds()) {
            instantToValueMap = PartitionsReduction.getMaxValuesByResolution(instantToValueMap, partitionsDataSecondaryModel.getResolutionInSeconds());
        }

        for (Map.Entry<Long, Double> entry : partitionsDataSecondaryModel.getInstantToValue().entrySet()) {
            instantToValueMap.putIfAbsent(entry.getKey(), 0D);
        }

        return PartitionsReduction.reducePartitionsMapToMaxValues(instantToValueMap, partitionsDataModel.getInstantStep(), resolutionStep, partitionsResolutionInSeconds, minNumOfMaxValuesSamples);
    }

    /**
     * create continuous model
     *
     * @param maxValuesResult maxValuesResult
     * @return continuous model
     */
    public Model createContinuousModel(MaxValuesResult maxValuesResult) {
        Map<String, Double> histogram = createGenericHistogram(maxValuesResult.getMaxValues().values()).getHistogramMap();
        return new ContinuousHistogramModelBuilder().buildContinuousDataModel(PartitionsReduction.getMaxValuesHistogram(histogram, numOfMaxValuesSamples));
    }


    /***
     * Create generic histogram
     * @param featureValue Collection of feature values
     * @return histogram of feature value to count
     */
    private GenericHistogram createGenericHistogram(Collection<Double> featureValue) {
        GenericHistogram reductionHistogram = new GenericHistogram();
        featureValue.forEach(aggregatedFeatureValue -> {
            reductionHistogram.add(aggregatedFeatureValue, 1d);
        });
        return reductionHistogram;
    }
}
