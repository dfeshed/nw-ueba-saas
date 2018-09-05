package fortscale.common.feature;


import fortscale.utils.data.Pair;
import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Document
public class MultiKeyHistogram implements Serializable, FeatureValue {

    private static final String HISTOGRAM_FIELD_NAME = "histogram";

    @Transient
    private Map<MultiKeyFeature, Double> histogram;
    private Long total;

    public MultiKeyHistogram() {
    }

    public MultiKeyHistogram(Map<MultiKeyFeature, Double> histogram, Long total) {
        this.histogram = histogram;
        this.total = total;
    }

    public void setMax(MultiKeyFeature multiKeyFeature, Double potentialMax) {
        Double max = histogram.get(multiKeyFeature);
        histogram.put(multiKeyFeature, max == null ? potentialMax : Math.max(max, potentialMax));
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getTotal() {
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
