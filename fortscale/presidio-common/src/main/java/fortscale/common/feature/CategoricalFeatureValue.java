package fortscale.common.feature;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fortscale.common.util.GenericHistogram;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import javafx.util.Pair;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by barak_schuster on 10/22/17.
 */
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY, getterVisibility= JsonAutoDetect.Visibility.NONE, setterVisibility= JsonAutoDetect.Visibility.NONE)
public class CategoricalFeatureValue implements FeatureValue,Serializable {
    public static final String FEATURE_VALUE_TYPE = "categorical-feature-value";

    private Map<Pair<String,Instant>/*i.e. machine,date of activity*/, Double> histogram = new HashMap<>();

    private FixedDurationStrategy strategy;

    public CategoricalFeatureValue(FixedDurationStrategy strategy) {
        this.strategy = strategy;
    }

    public Map<Pair<String, Instant>, Double> getHistogram() {
        return histogram;
    }

    public void add(GenericHistogram genericHistogram, Instant startTime) {
        Map<String, Double> histogramMap = genericHistogram.getHistogramMap();
        histogramMap.forEach((name, value) -> {
            Pair<String, Instant> key = new Pair<>(name, startTime);
            if (histogram.get(key) != null) {
                histogram.put(key, histogram.get(key) + value);
            } else {
                histogram.put(key, value);
            }
        });
    }

    public long getN() {
        return histogram.size();
    }

    public FixedDurationStrategy getStrategy() {
        return strategy;
    }

    public void setHistogram(Map<Pair<String, Instant>, Double> histogram) {
        this.histogram = histogram;
    }
}
