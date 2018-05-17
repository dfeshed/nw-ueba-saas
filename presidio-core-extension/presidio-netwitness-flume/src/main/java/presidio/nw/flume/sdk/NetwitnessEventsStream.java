package presidio.nw.flume.sdk;

import com.rsa.asoc.streams.RecordSource;
import com.rsa.asoc.streams.RecordStream;
import com.rsa.asoc.streams.base.DefaultRecordStream;
import com.rsa.asoc.streams.policy.BufferedSourceStreamPolicy;
import com.rsa.asoc.streams.policy.RecordStreamPolicy;
import com.rsa.asoc.streams.policy.RecordStreamPolicyDecorator;
import com.rsa.asoc.streams.policy.RecordStreamPolicyParameter;
import com.rsa.asoc.streams.source.netwitness.NwParameter;
import fortscale.common.general.Schema;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.CloseableIterator;
import presidio.config.server.client.ConfigurationServerClientService;
import presidio.config.server.factory.ConfigurationServerClientServiceFactory;
import presidio.nw.flume.model.Configuration;
import presidio.nw.flume.model.DataPullingConfiguration;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class NetwitnessEventsStream extends AbstractNetwitnessEventsStream {

//    private static final String BROKER_END_POINT = "nw://admin:netwitness@10.25.67.33:50005";
    protected static final String UEBA = "ueba";
    protected static final String QUERY = "query";
    protected static final String TIME_FIELD = "timeField";
    private ConfigurationServerClientService configurationServerClientService;

    private static Logger logger = LoggerFactory.getLogger(NetwitnessEventsStream.class);

    public NetwitnessEventsStream() {
        final ConfigurationServerClientServiceFactory configurationServerClientServiceFactory = new ConfigurationServerClientServiceFactory();
        try {
            this.configurationServerClientService = configurationServerClientServiceFactory.createConfigurationServerClientService();
        } catch (Exception e) {
            logger.error("failed to start netwitness event stream", e);
        }
    }

    @Override
    public CloseableIterator<Map<String, Object>> iterator(Schema schema, Instant startDate, Instant endDate, Map<String, String> config) {
        return new EventsStreamIterator(schema, startDate, endDate, config);
    }

    private String readDataPullingSourceConfiguration() throws Exception {
        DataPullingConfiguration dataPullingConfiguration = null;
        try {
            dataPullingConfiguration = configurationServerClientService.readConfigurationAsJson("application-presidio", "default", Configuration.class).getDataPulling();
        } catch (Exception e) {
            String errorMessage = String.format("Failed to fetch. Failed to get configuration from config server %s", configurationServerClientService);
            logger.error(errorMessage, e);
            throw new Exception(errorMessage, e);
        }

        if(dataPullingConfiguration == null) {
            logger.error("Failed to read data pulling source from configuration server");
            throw new Exception("Failed to read data pulling source from configuration server");
        }
        return dataPullingConfiguration.getSource();
    }

    private class EventsStreamIterator implements CloseableIterator<Map<String, Object>>{

        private AtomicBoolean isRunning = new AtomicBoolean(true);
        private RecordStream stream;

        public EventsStreamIterator(Schema schema, Instant startDate, Instant endDate, Map<String, String> configurations) {
            try {
                String query = configurations.get(QUERY);
                String timeField = configurations.get(TIME_FIELD);
                stream = buildStream(UEBA);
                isRunning.set(true);
                addSource(startDate, endDate, stream, query, timeField);
            } catch (Exception ex) {
                logger.error("start streaming failed", ex);
            }
        }


        @Override
        public void close() {
            stream.close();
        }

        @Override
        public boolean hasNext() {
            if (!isRunning.get()) {
                Map<String, Object> streamStatus = (Map<String, Object>)stream.getStatus(false).get("stream");
                long recordsBuffered = (Long) streamStatus.get("recordsBuffered");
                if (recordsBuffered == 0) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public Map<String, Object> next() {
            Map<String, Object> event = null;

            try {
                do {
                    event = stream.poll(1000, TimeUnit.MILLISECONDS);
                } while (event == null && hasNext());


            } catch (Exception ex) {
                logger.error("failed to fetch next record", ex);
            }
            return event;
        }

        private RecordStream buildStream(String name) {
            com.rsa.asoc.streams.base.Configuration configuration = new com.rsa.asoc.streams.base.Configuration(URI.create(name), RecordStreamPolicyParameter.SUPPORTED);
            RecordStreamPolicy policy = new BufferedSourceStreamPolicy(configuration);
            RecordStreamPolicyDecorator recordStreamPolicyDecorator = new RecordStreamPolicyDecorator(policy) {
                public void handleSourceComplete(RecordSource source) {
                    super.handleSourceComplete(source);
                    isRunning.set(false);
                }
            };
            RecordStream stream = new DefaultRecordStream(recordStreamPolicyDecorator);
            return  stream;
        }

        private void addSource(Instant startTime, Instant endTime, RecordStream stream, String query, String timeField) throws Exception {
            String source = readDataPullingSourceConfiguration();
            URIBuilder uriBuilder = new URIBuilder(source);

            uriBuilder.addParameter(NwParameter.Mechanism.name(), "query");
            uriBuilder.addParameter(NwParameter.TimeMeta.name(), timeField);

            // start streaming parameter
            long minutesBack = TimeUnit.MINUTES.convert(Duration.between(startTime, Instant.now()).get(ChronoUnit.SECONDS),TimeUnit.SECONDS) ;
            uriBuilder.addParameter(NwParameter.MinutesBack.name(), Long.valueOf(minutesBack).toString());

            // end streaming parameter
            long collectionDurationInMinutes =TimeUnit.MINUTES.convert(Duration.between(startTime, endTime).get(ChronoUnit.SECONDS),TimeUnit.SECONDS) ;
            uriBuilder.addParameter(NwParameter.CollectionDurationInMinutes.name(), Long.valueOf(collectionDurationInMinutes).toString());

            // event source
            uriBuilder.addParameter(NwParameter.Query.name(), query);

            stream.addSource(uriBuilder.build());
        }
    }

}
