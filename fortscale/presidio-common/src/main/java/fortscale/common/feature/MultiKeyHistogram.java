package fortscale.common.feature;


import fortscale.utils.data.Pair;
import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Document
public class MultiKeyHistogram implements Serializable, FeatureValue {

    private static final String HISTOGRAM_FIELD_NAME = "histogram";

    @Transient
    private Map<MultiKeyFeature, Double> histogram;
    private double total = 0.0; //todo: long instead double?
    private Object maxObject = null;

    public MultiKeyHistogram() {
        this.histogram = new HashMap<>();
    }

    public MultiKeyHistogram(Map<MultiKeyFeature, Double> histogram, double total) {
        this.histogram = histogram;
        this.total = total;
    }

    public void setMax(MultiKeyFeature multiKeyFeature, Double potentialMax) {
        Double max = histogram.get(multiKeyFeature);
        histogram.put(multiKeyFeature, max == null ? potentialMax : Math.max(max, potentialMax));
        this.total++;
    }

    public void add(MultiKeyFeature multiKeyFeature, Double count) {
        Double oldCount = histogram.get(multiKeyFeature);
        Double newValCount = oldCount != null ? count + oldCount : count;
        histogram.put(multiKeyFeature, newValCount);

        if (maxObject == null) {
            maxObject = multiKeyFeature;
        } else {
            Double maxCount = histogram.get(maxObject);
            if (maxCount == null) {
                maxObject = multiKeyFeature;
            } else if (maxCount < newValCount) {
                maxObject = multiKeyFeature;
            }
        }
        this.total += count;
    }

    public MultiKeyHistogram add(MultiKeyHistogram multiKeyHistogram) {
        for (MultiKeyFeature key : multiKeyHistogram.getHistogram().keySet()) {
            Double count = multiKeyHistogram.getHistogram().get(key);
            add(key, count);
        }
        return this;
    }

    public Object getMaxObject() {
        return maxObject;
    }

    public void remove(FeatureValue val) {
        if (val == null) {
            return;
        }

        histogram.entrySet().removeIf(e -> {
            boolean contains = e.getKey().containsValue(val);
            if (contains) {
                if (e.getValue() != null) {
                    this.total -= e.getValue();
                }
            }
            return contains;
        });

        if (histogram.size() > 0) {
            maxObject = histogram.entrySet().stream().max(Comparator.comparing(Map.Entry::getValue)).get().getKey();
        } else {
            maxObject = null;
        }
    }

    public long getN() {
        return histogram.size();
    }

    public double getTotal() {
        return total;
    }

    public Map<MultiKeyFeature, Double> getHistogram() {
        return histogram;
    }

    @AccessType(AccessType.Type.PROPERTY)
    @Field(HISTOGRAM_FIELD_NAME)
    public List<Pair<MultiKeyFeature, Double>> getHistogramList() {
        return histogram.entrySet()
                .stream()
                .map(e -> new Pair<>(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    @AccessType(AccessType.Type.PROPERTY)
    @Field(HISTOGRAM_FIELD_NAME)
    public void setHistogramList(List<Pair<MultiKeyFeature, Double>> histogramList) {
        this.histogram = histogramList.stream().collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

}
