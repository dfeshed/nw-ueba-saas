package presidio.monitoring.flush;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Created by barak_schuster on 12/10/17.
 */
@Configuration
public class MetricContainerFlusherConfig {

    @Autowired
    private List<FlushableMetricContainer> containerList;

    @Bean
    public MetricContainerFlusher metricContainerFlusher() {
        return new MetricContainerFlusher(containerList);
    }

}
