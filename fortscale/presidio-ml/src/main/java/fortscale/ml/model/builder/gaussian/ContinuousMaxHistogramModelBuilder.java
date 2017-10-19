package fortscale.ml.model.builder.gaussian;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.AggregatedFeatureValuesData;
import fortscale.ml.model.ContinuousDataModel;
import fortscale.ml.model.ContinuousMaxDataModel;
import fortscale.ml.model.Model;
import fortscale.ml.model.builder.IModelBuilder;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.time.TimeService;
import org.springframework.util.Assert;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static fortscale.utils.ConversionUtils.convertToDouble;

/**
 * Created by YaronDL on 9/24/2017.
 */
public class ContinuousMaxHistogramModelBuilder extends ContinuousHistogramModelBuilder {

    private int numOfMaxValuesSamples;
    private int minNumOfMaxValuesSamples;
    private long partitionsResolutionInSeconds;

    public ContinuousMaxHistogramModelBuilder(ContinuousMaxHistogramModelBuilderConf builderConf) {
        Assert.isTrue(builderConf.getNumOfMaxValuesSamples() > 0, "numOfMaxValuesSamples should be bigger than zero");
        Assert.isTrue(builderConf.getMinNumOfMaxValuesSamples() > 0, "nimNumOfMaxValuesSamples should be bigger than zero");
        this.numOfMaxValuesSamples = builderConf.getNumOfMaxValuesSamples();
        this.minNumOfMaxValuesSamples = builderConf.getMinNumOfMaxValuesSamples();
        this.partitionsResolutionInSeconds = builderConf.getPartitionsResolutionInSeconds();
    }


    @Override
    public Model build(Object modelBuilderData) {


        AggregatedFeatureValuesData aggregatedFeatureValuesData = (AggregatedFeatureValuesData) modelBuilderData;

        FixedDurationStrategy fixedDurationStrategy = aggregatedFeatureValuesData.getFixedDurationStrategy();
        TreeMap<Instant, Double> instantToFeatureValue = aggregatedFeatureValuesData.getInstantToAggregatedFeatureValues();

        //create ContinuousDataModel with all data
        Map<String, Double> histogram = createGenericHistogram(instantToFeatureValue.values()).getHistogramMap();
        ContinuousDataModel continuousDataModel = buildContinuousDataModel(histogram);

        //create ContinuousDataModel with max values
        List<Double> maxValues = getMaxValues(instantToFeatureValue, fixedDurationStrategy);
        ContinuousDataModel continuousDataModelOfMaxValues = buildContinuousDataModel(getMaxValuesHistogram(createGenericHistogram(maxValues).getHistogramMap()));

        return new ContinuousMaxDataModel(continuousDataModel, continuousDataModelOfMaxValues);
    }

    /**
     * @param instantToFeatureValue start instant to featureValue treeMap
     * @return list of max feature values
     */
    private List<Double> getMaxValues(TreeMap<Instant, Double> instantToFeatureValue, FixedDurationStrategy fixedDurationStrategy) {
        Instant instant = TimeService.floorTime(instantToFeatureValue.firstKey(), Duration.ofDays(1));
        Instant lastInstant = TimeService.floorTime(instantToFeatureValue.lastKey(), Duration.ofDays(1)).plus(Duration.ofDays(1));

        //add missed instants with Double.MIN_VALUE in order to be able get max values in various resolutions.
        while (instant.isBefore(lastInstant)) {
            if (!instantToFeatureValue.containsKey(instant)) {
                instantToFeatureValue.put(instant, Double.MIN_VALUE);
            }
            instant = instant.plus(fixedDurationStrategy.toDuration());
        }

        long resolution = partitionsResolutionInSeconds / fixedDurationStrategy.toDuration().getSeconds();
        List<Double> maxValues = new ArrayList<>();
        //Get max values
        while (maxValues.size() < minNumOfMaxValuesSamples && resolution > 0) {
            maxValues = getMaxValuesByResolution(instantToFeatureValue, resolution);
            resolution = (partitionsResolutionInSeconds - fixedDurationStrategy.toDuration().getSeconds()) / fixedDurationStrategy.toDuration().getSeconds();
        }

        return maxValues;
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
    private List<Double> getMaxValuesByResolution(TreeMap<Instant, Double> instantToFeatureValue, long resolution) {

        int sizeOfPartition = (int) resolution;
        List<Double> featureValues = new ArrayList<>(instantToFeatureValue.values());
        List<List<Double>> subLists = Lists.partition(featureValues, sizeOfPartition);

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


}
