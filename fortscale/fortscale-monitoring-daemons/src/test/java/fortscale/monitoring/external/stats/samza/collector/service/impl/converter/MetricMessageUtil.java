package fortscale.monitoring.external.stats.samza.collector.service.impl.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.samza.metricMessageModels.MetricMessage;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * reads metricmessage POJO from json file
 */
public class MetricMessageUtil {
    private MetricMessage metricMessage;
    public MetricMessageUtil() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        metricMessage = mapper.readValue(Paths.get("src/test/resources/samza/collector.service.impl.converter/metricMessage.json").toFile(),MetricMessage.class);
    }
    public MetricMessage getMetricMessage() {
        return metricMessage;
    }


}
