package fortscale.ml.model.builder;

import fortscale.ml.model.*;
import fortscale.ml.utils.MaxValuesResult;
import fortscale.ml.utils.PartitionsReduction;
import fortscale.utils.logging.Logger;
import org.springframework.util.Assert;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static fortscale.utils.logging.Logger.getLogger;


public class PartitionsHistogramModelBuilder implements IModelBuilder {

    private static final Logger logger = getLogger(PartitionsHistogramModelBuilder.class);

    private int minNumOfMaxValuesSamples;
    private long partitionsResolutionInSeconds;
    private int resolutionStep;

    public PartitionsHistogramModelBuilder(PartitionsHistogramModelBuilderConf builderConf) {
        Assert.isTrue(builderConf.getMinNumOfMaxValuesSamples() > 0, "nimNumOfMaxValuesSamples should be bigger than zero");
        this.minNumOfMaxValuesSamples = builderConf.getMinNumOfMaxValuesSamples();
        this.partitionsResolutionInSeconds = builderConf.getPartitionsResolutionInSeconds();
        this.resolutionStep = builderConf.getResolutionStep();
    }


    @Override
    public Model build(Object modelBuilderData) {
        AggregatedFeatureValuesData aggregatedFeatureValuesData = (AggregatedFeatureValuesData) modelBuilderData;
        TreeMap<Instant, Double> instantToFeatureValue = aggregatedFeatureValuesData.getInstantToAggregatedFeatureValues();
        long numOfPartitions = instantToFeatureValue.keySet().stream().map(x -> (x.getEpochSecond() / partitionsResolutionInSeconds) * partitionsResolutionInSeconds).distinct().count();
        Duration instantStep = aggregatedFeatureValuesData.getInstantStep();

        Map<Long, Double> epochToFeatureValue = instantToFeatureValue.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().getEpochSecond(), Map.Entry::getValue));
        MaxValuesResult maxValuesResult = PartitionsReduction.reducePartitionsMapToMaxValues(epochToFeatureValue, instantStep, resolutionStep, partitionsResolutionInSeconds, minNumOfMaxValuesSamples);
        logger.debug("maxValuesResult={} for aggregatedFeatureValuesData={}", maxValuesResult, aggregatedFeatureValuesData);
        return new PartitionsDataModel(maxValuesResult.getMaxValues(), maxValuesResult.getResolutionInSeconds(), instantStep, numOfPartitions);
    }

}
