package fortscale.accumulator.manager;

import java.time.Instant;
import java.util.Set;

/**
 * Created by barak_schuster on 10/9/16.
 */
public class AccumulatorManagerParams {
    private Instant from;
    private Instant to;
    private Set<String> features;

    public Instant getFrom() {
        return from;
    }

    public void setFrom(Instant from) {
        this.from = from;
    }

    public Instant getTo() {
        return to;
    }

    public void setTo(Instant to) {
        this.to = to;
    }

    public Set<String> getFeatures() {
        return features;
    }

    public void setFeatures(Set<String> features) {
        this.features = features;
    }
}
