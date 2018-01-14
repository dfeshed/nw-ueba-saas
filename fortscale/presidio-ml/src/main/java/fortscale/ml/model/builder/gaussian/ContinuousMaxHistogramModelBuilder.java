package fortscale.ml.model.builder.gaussian;

import com.google.common.collect.Lists;
import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.AggregatedFeatureValuesData;
import fortscale.ml.model.ContinuousDataModel;
import fortscale.ml.model.ContinuousMaxDataModel;
import fortscale.ml.model.Model;
import fortscale.ml.model.metrics.MaxContinuousModelBuilderMetricsContainer;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeService;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.util.Assert;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static fortscale.utils.ConversionUtils.convertToDouble;
import static fortscale.utils.logging.Logger.getLogger;


/**
 * Created by YaronDL on 9/24/2017.
 */
public class ContinuousMaxHistogramModelBuilder extends ContinuousHistogramModelBuilder {

    private static final Logger logger = getLogger(ContinuousMaxHistogramModelBuilder.class);

    private int numOfMaxValuesSamples;
    private int minNumOfMaxValuesSamples;
    private long partitionsResolutionInSeconds;
    private MaxContinuousModelBuilderMetricsContainer maxContinuousModelBuilderMetricsContainer;

    public ContinuousMaxHistogramModelBuilder(ContinuousMaxHistogramModelBuilderConf builderConf,
                                              MaxContinuousModelBuilderMetricsContainer maxContinuousModelBuilderMetricsContainer) {
        Assert.isTrue(builderConf.getNumOfMaxValuesSamples() > 0, "numOfMaxValuesSamples should be bigger than zero");
        Assert.isTrue(builderConf.getMinNumOfMaxValuesSamples() > 0, "nimNumOfMaxValuesSamples should be bigger than zero");
        this.numOfMaxValuesSamples = builderConf.getNumOfMaxValuesSamples();
        this.minNumOfMaxValuesSamples = builderConf.getMinNumOfMaxValuesSamples();
        this.partitionsResolutionInSeconds = builderConf.getPartitionsResolutionInSeconds();
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
        MaxValuesResult maxValuesResult = getMaxValues(instantToFeatureValue, instantStep);
        logger.debug("maxValuesResult={} for aggregatedFeatureValuesData={}",maxValuesResult,aggregatedFeatureValuesData);
        List<Double> maxValues = maxValuesResult.getMaxValues();
        ContinuousDataModel continuousDataModelOfMaxValues = buildContinuousDataModel(getMaxValuesHistogram(createGenericHistogram(maxValues).getHistogramMap()));

        maxContinuousModelBuilderMetricsContainer.updateMetric(numOfPartitions, continuousDataModel, continuousDataModelOfMaxValues);
        return new ContinuousMaxDataModel(continuousDataModel, continuousDataModelOfMaxValues,numOfPartitions);
    }

    /**
     * @param instantToFeatureValue start instant to featureValue treeMap
     * @return list of max feature values and final resolution
     */
    private MaxValuesResult getMaxValues(TreeMap<Instant, Double> instantToFeatureValue, Duration instantStep) {
        Instant instantCursor = TimeService.floorTime(instantToFeatureValue.firstKey(), partitionsResolutionInSeconds);
        Instant lastInstant = TimeService.floorTime(instantToFeatureValue.lastKey(), partitionsResolutionInSeconds).plus(Duration.ofSeconds(partitionsResolutionInSeconds));

        //add missed instants with Double.MIN_VALUE in order to be able get max values in various resolutions.
        while (instantCursor.isBefore(lastInstant)) {
            if (!instantToFeatureValue.containsKey(instantCursor)) {
                instantToFeatureValue.put(instantCursor, Double.MIN_VALUE);
            }
            instantCursor = instantCursor.plus(instantStep);
        }

        int resolution = (int) (partitionsResolutionInSeconds / instantStep.getSeconds());
        long resolutionInSeconds = partitionsResolutionInSeconds;
        List<Double> maxValues = new ArrayList<>();

        //Get max values
        while (maxValues.size() < minNumOfMaxValuesSamples && resolution > 0) {
            maxValues = getMaxValuesByResolution(instantToFeatureValue, resolution);
            resolutionInSeconds = resolution * instantStep.getSeconds();
            resolution--;
        }

        int metricResolution = resolution + 1;
        maxContinuousModelBuilderMetricsContainer.updateMaxValuesResult(metricResolution);

        return new MaxValuesResult(resolutionInSeconds,maxValues);
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


    /**
     * Split instantToFeatureValue by resolution, get max value of each part.
     *
     * @param instantToFeatureValue start instant to featureValue treeMap
     * @param resolution            resolution
     * @return list of max feature values
     */
    private List<Double> getMaxValuesByResolution(TreeMap<Instant, Double> instantToFeatureValue, int resolution) {

        List<Double> featureValues = new ArrayList<>(instantToFeatureValue.values());
        List<List<Double>> subLists = Lists.partition(featureValues, resolution);

        List<Double> maxValues = new ArrayList<>();
        subLists.forEach(sublist -> {
            Double maxValue = Collections.max(sublist);
            if (!maxValue.equals(Double.MIN_VALUE)) {
                maxValues.add(Collections.max(sublist));
            }
        });

        return maxValues;
    }


    private Map<String, Double> getMaxValuesHistogram(Map<String, Double> histogram) {
        Comparator<Map.Entry<String, Double>> histogramKeyComparator = Comparator.comparingDouble(e -> convertToDouble(e.getKey()));
        Map<String, Double> sortedHistogram = histogram.entrySet().stream().sorted(histogramKeyComparator.reversed()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        Map<String, Double> ret = new HashMap<>();
        double totalNumOfSamples = 0;
        for (Map.Entry<String, Double> entry : sortedHistogram.entrySet()) {
            double count = entry.getValue();
            if (totalNumOfSamples + count >= numOfMaxValuesSamples) {
                ret.put(entry.getKey(), numOfMaxValuesSamples - totalNumOfSamples);
                break;
            } else {
                totalNumOfSamples += count;
                ret.put(entry.getKey(), count);
            }
        }

        return ret;
    }

    private class MaxValuesResult
    {
        private long resolution;
        private List<Double> maxValues;

        public MaxValuesResult(long resolution, List<Double> maxValues) {
            this.resolution = resolution;
            this.maxValues = maxValues;
        }

        public long getResolution() {
            return resolution;
        }

        public void setResolution(int resolution) {
            this.resolution = resolution;
        }

        public List<Double> getMaxValues() {
            return maxValues;
        }

        public void setMaxValues(List<Double> maxValues) {
            this.maxValues = maxValues;
        }

        /**
         * @return ToString you know...
         */
        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }
}
