package fortscale.ml.model.builder.gaussian;

import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.AggregatedFeatureValuesData;
import fortscale.ml.model.ContinuousDataModel;
import fortscale.ml.model.ContinuousMaxDataModel;
import fortscale.ml.model.Model;
import fortscale.ml.model.metrics.MaxContinuousModelBuilderMetricsContainer;
import fortscale.ml.utils.MaxValuesResult;
import fortscale.ml.utils.PartitionsReduction;
import fortscale.utils.logging.Logger;
import org.springframework.util.Assert;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static fortscale.utils.logging.Logger.getLogger;


/**
 * Created by YaronDL on 9/24/2017.
 */
public class ContinuousMaxHistogramModelBuilder extends ContinuousHistogramModelBuilder {

    private static final Logger logger = getLogger(ContinuousMaxHistogramModelBuilder.class);

    private int numOfMaxValuesSamples;
    private int minNumOfMaxValuesSamples;
    private long partitionsResolutionInSeconds;
    private int resolutionStep;
    private int minResolution;
    private MaxContinuousModelBuilderMetricsContainer maxContinuousModelBuilderMetricsContainer;

    public ContinuousMaxHistogramModelBuilder(ContinuousMaxHistogramModelBuilderConf builderConf,
                                              MaxContinuousModelBuilderMetricsContainer maxContinuousModelBuilderMetricsContainer) {
        Assert.isTrue(builderConf.getNumOfMaxValuesSamples() > 0, "numOfMaxValuesSamples should be bigger than zero");
        Assert.isTrue(builderConf.getMinNumOfMaxValuesSamples() > 0, "nimNumOfMaxValuesSamples should be bigger than zero");
        this.numOfMaxValuesSamples = builderConf.getNumOfMaxValuesSamples();
        this.minNumOfMaxValuesSamples = builderConf.getMinNumOfMaxValuesSamples();
        this.partitionsResolutionInSeconds = builderConf.getPartitionsResolutionInSeconds();
        this.resolutionStep = builderConf.getResolutionStep();
        this.minResolution = builderConf.getMinResolution();
        this.maxContinuousModelBuilderMetricsContainer = maxContinuousModelBuilderMetricsContainer;
    }


    @Override
    public Model build(Object modelBuilderData) {

        AggregatedFeatureValuesData aggregatedFeatureValuesData = (AggregatedFeatureValuesData) modelBuilderData;

        Duration instantStep = aggregatedFeatureValuesData.getInstantStep();
        TreeMap<Instant, Double> instantToFeatureValue = aggregatedFeatureValuesData.getInstantToAggregatedFeatureValues();
        long numOfPartitions = instantToFeatureValue.keySet().stream().map(x -> (x.getEpochSecond() / partitionsResolutionInSeconds) * partitionsResolutionInSeconds).distinct().count();

        //create ContinuousDataModel with all data
        Map<String, Double> histogram = createGenericHistogram(instantToFeatureValue.values()).getHistogramMap();
        ContinuousDataModel continuousDataModel = buildContinuousDataModel(histogram);

        //create ContinuousDataModel with max values
        Map<Long, Double> epochToFeatureValue = instantToFeatureValue.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().getEpochSecond() , Map.Entry::getValue));
        MaxValuesResult maxValuesResult = PartitionsReduction.reducePartitionsMapToMaxValues(epochToFeatureValue, instantStep, resolutionStep, partitionsResolutionInSeconds, minNumOfMaxValuesSamples, minResolution);
        logger.debug("maxValuesResult={} for aggregatedFeatureValuesData={}",maxValuesResult,aggregatedFeatureValuesData);
        Collection<Double> maxValues = maxValuesResult.getMaxValues().values();
        ContinuousDataModel continuousDataModelOfMaxValues = (ContinuousDataModel) build(maxValues, numOfMaxValuesSamples);
        maxContinuousModelBuilderMetricsContainer.updateMetric(numOfPartitions, continuousDataModel, continuousDataModelOfMaxValues);
        return new ContinuousMaxDataModel(continuousDataModel, continuousDataModelOfMaxValues,numOfPartitions);
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
