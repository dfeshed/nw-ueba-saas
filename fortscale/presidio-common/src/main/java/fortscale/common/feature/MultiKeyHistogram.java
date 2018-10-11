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

    public MultiKeyHistogram() {
        this.histogram = new HashMap<>();
    }

    public void set(MultiKeyFeature multiKeyFeature, Double value) {
        histogram.put(multiKeyFeature, value);
    }

    public MultiKeyHistogram add(MultiKeyHistogram multiKeyHistogram, Set<String> filter) {
        for (Map.Entry<MultiKeyFeature, Double> entry : multiKeyHistogram.getHistogram().entrySet()) {
            if(!entry.getKey().containsAtLeastOneValue(filter)){
                Double oldCount = histogram.get(entry.getKey());
                Double newValCount = oldCount != null ?  entry.getValue() + oldCount :  entry.getValue();
                set(entry.getKey(), newValCount);
            }
        }
        return this;
    }

    public Double getCount(MultiKeyFeature multiKeyFeature) {
        return histogram.get(multiKeyFeature);
    }

    public boolean isEmpty() {
        return histogram.isEmpty();
    }

    public long getN() {
        return histogram.size();
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
