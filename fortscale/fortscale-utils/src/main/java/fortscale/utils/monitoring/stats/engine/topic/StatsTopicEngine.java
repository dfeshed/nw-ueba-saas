package fortscale.utils.monitoring.stats.engine.topic;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.kafka.KafkaEventsWriter;
import fortscale.utils.kafka.metricMessageModels.Header;
import fortscale.utils.kafka.metricMessageModels.MetricMessage;
import fortscale.utils.kafka.metricMessageModels.Metrics;
import fortscale.utils.monitoring.stats.engine.StatsEngineBase;
import fortscale.utils.monitoring.stats.engine.StatsEngineMetricsGroupData;
import fortscale.utils.monitoring.stats.models.engine.EngineData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 *
 * The stats topic engine get write metrics group data directly to a Kafka ("metrics") topic.
 *
 * It should be used when Samaza is not available.
 *
 * Created by gaashh on 4/26/16.
 */
public class StatsTopicEngine extends StatsEngineBase {

    final protected String ENGINE_DATA_METRICS_HEADER_VERSION = "0.0.1"; // Like Samza version
    // Must match metric adapter
    // DO not change even is class is relocated
    final protected String ENGINE_DATA_METRIC_FIELD_NAME = "fortscale.utils.monitoring.stats.models.engine";
    final protected String ENGINE_DATA_METRIC_METRIC_NAME = "EngineData";

    protected String topicName;

    protected long firstEpochTime = 0;

    protected KafkaEventsWriter kafkaEventsWriter;

    public StatsTopicEngine(KafkaEventsWriter kafkaEventsWriter) {

        super();

        this.kafkaEventsWriter = kafkaEventsWriter;

    }


    @Override
    public void writeMetricsGroupData(StatsEngineMetricsGroupData metricsGroupData) {

    }

    @Override
    public void flushMetricsGroupData(StatsEngineMetricsGroupData metricsGroupData) {

    }

    // TODO: Change to protected when test will be fixed
    // TODO: Limit the msg size somehow
    public String engineDataToMetricsTopicMessageString(EngineData engineData, long epochTime) {

        // On the first time, update first epoch time
        if (firstEpochTime == 0) {
            firstEpochTime = epochTime;
        }


        // Build the metrics topic header
        Header header = new Header();
        header.setHost("TODO-hostname"); // TODO
        header.setJobName("TODO-jobname"); // TODO
        header.setVersion(ENGINE_DATA_METRICS_HEADER_VERSION);
        header.setTime(epochTime * 1000);  // in mSec
        header.setResetTime(firstEpochTime * 1000); // in mSec

        // Convert the engine data into a JSON string
        String engineDataInJsonString = modelMetricGroupToJsonInString(engineData);

        // Creates a metrics entry
        HashMap<String, Object> engineDataMetricsEntry = new HashMap<>();
        engineDataMetricsEntry.put(ENGINE_DATA_METRIC_METRIC_NAME, engineDataInJsonString);

        // Create the metrics object and populate it with the entry
        Metrics metrics = new Metrics();
        metrics.setAdditionalProperty(ENGINE_DATA_METRIC_FIELD_NAME, engineDataMetricsEntry);

        // Build the metric message
        MetricMessage metricMessage = new MetricMessage();
        metricMessage.setHeader(header);
        metricMessage.setMetrics(metrics);



        ObjectMapper mapper = new ObjectMapper();
      //  mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String jsonInString = null;
        try {
            jsonInString = mapper.writeValueAsString(metricMessage);
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return jsonInString;

    }

    // TODO: limit msg size
    // TODO: Change to protected when test will be fixed
    public void writeEngineDataToMetricsTopic(EngineData engineData, long epochTime) {

        String metricMessageString = engineDataToMetricsTopicMessageString(engineData, epochTime);

        writeMessageStringToMetricsTopic(metricMessageString);

    }

    public void writeMessageStringToMetricsTopic(String message) {

        kafkaEventsWriter.send("", message);
    }


}