package fortscale.ml.model.builder.gaussian;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.ContinuousDataModel;
import fortscale.ml.model.ContinuousMaxDataModel;
import fortscale.ml.model.Model;
import fortscale.ml.model.builder.IModelBuilder;
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

    public ContinuousMaxHistogramModelBuilder(ContinuousMaxHistogramModelBuilderConf builderConf) {
        Assert.isTrue(builderConf.getNumOfMaxValuesSamples() > 0, "numOfMaxValuesSamples should be bigger than zero");
        this.numOfMaxValuesSamples = builderConf.getNumOfMaxValuesSamples();
    }


    @Override
    public Model build(Object modelBuilderData) {
        TreeMap<Instant, Double> instantToFeatureValue = (TreeMap<Instant, Double>) modelBuilderData;

        //create ContinuousDataModel with all data
        Map<String, Double> histogram = createGenericHistogram(new ArrayList<>(instantToFeatureValue.values())).getHistogramMap();
        ContinuousDataModel continuousDataModel = buildContinuousDataModel(histogram);

        //create ContinuousDataModel with max values
        List<Double> maxValues = getMaxValues(instantToFeatureValue);
        ContinuousDataModel continuousDataModelOfMaxValues = buildContinuousDataModel(getMaxValuesHistogram(createGenericHistogram(maxValues).getHistogramMap()));

        long N = continuousDataModel.getN();
        double mean = continuousDataModelOfMaxValues.getMean();
        double continuousSd = continuousDataModel.getSd();
        double continuousMaxSd = continuousDataModelOfMaxValues.getSd();
        double maxValue = continuousDataModelOfMaxValues.getMaxValue();

        return new ContinuousMaxDataModel(N, mean, continuousMaxSd, continuousSd, maxValue);
    }

    /**
     * @param instantToFeatureValue start instant to featureValue treeMap
     * @return list of max feature values
     */
    private List<Double> getMaxValues(TreeMap<Instant, Double> instantToFeatureValue) {
        int resolution = 24;

        Instant instant = TimeService.floorTime(instantToFeatureValue.firstKey(), Duration.ofDays(1));
        Instant lastInstant = TimeService.floorTime(instantToFeatureValue.lastKey(), Duration.ofDays(1)).plus(Duration.ofDays(1));


        //add skipped instants with Double.MIN_VALUE in order to be able get nax values in various resolutions.
        while (instant.isBefore(lastInstant)) {
            if (!instantToFeatureValue.containsKey(instant)) {
                instantToFeatureValue.put(instant, Double.MIN_VALUE);
            } else {
                instantToFeatureValue.put(instant, instantToFeatureValue.get(instant));
            }
            instant = instant.plus(Duration.ofHours(1));
        }

        //Get max values
        List<Double> maxValues = new ArrayList<>();
        while (maxValues.size() < numOfMaxValuesSamples && resolution > 0) {
            maxValues = getMaxValuesByResolution(instantToFeatureValue, resolution);
            resolution--;
        }

        return maxValues;
    }

    /***
     * Create generic histogram
     * @param featureValue list of feature values
     * @return histogram of feature value to count
     */
    private GenericHistogram createGenericHistogram(List<Double> featureValue) {
        GenericHistogram reductionHistogram = new GenericHistogram();
        featureValue.forEach(aggregatedFeatureValue -> {
            reductionHistogram.add(aggregatedFeatureValue, 1d);
        });
        return reductionHistogram;
    }


    /**
     * Split instantToFeatureValue by resolution, get max value of each part.
     * @param instantToFeatureValue  start instant to featureValue treeMap
     * @param resolution resolution
     * @return list of max feature values
     */
    public List<Double> getMaxValuesByResolution(TreeMap<Instant, Double> instantToFeatureValue, int resolution) {

        List<Double> list = new ArrayList<>(instantToFeatureValue.values());

        List<List<Double>> subLists = Lists.partition(list, resolution);
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
