package presidio.monitoring.flush;

import java.util.List;

/**
 * temporal solution to flush metrics in resultion of name,tag and logical time
 * TODO: replace with final solution once saving resolution changes
 * Created by barak_schuster on 12/10/17.
 */
public class MetricContainerFlusher {

    private List<FlushableMetricContainer> containers;

    public MetricContainerFlusher(List<FlushableMetricContainer> containers) {
        this.containers = containers;
    }

    public void flush()
    {
        containers.forEach(FlushableMetricContainer::flush);
    }
}
