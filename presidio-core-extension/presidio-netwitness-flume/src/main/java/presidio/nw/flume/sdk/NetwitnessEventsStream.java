package presidio.nw.flume.sdk;

import com.rsa.asoc.streams.RecordSourcePosition;
import com.rsa.asoc.streams.RecordStream;
import com.rsa.asoc.streams.RecordStreamException;
import com.rsa.asoc.streams.RecordStreams;
import com.rsa.asoc.streams.source.netwitness.NwParameter;
import com.rsa.asoc.streams.source.netwitness.NwPosition;
import fortscale.common.general.Schema;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.CloseableIterator;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class NetwitnessEventsStream extends AbstractNetwitnessEventsStream {

    protected static final String QUERY = "query";
    protected static final String TIME_FIELD = "timeField";
    protected static final String CONNECTION_TIMEOUT = "connectionTimeout";
    protected static final String SOCKET_TIMEOUT = "socketTimeout";

    private NetwitnessEventsSources nwSources;

    private static Logger logger = LoggerFactory.getLogger(NetwitnessEventsStream.class);

    public NetwitnessEventsStream() {
        this.nwSources = new NetwitnessEventsSources();
    }

    @Override
    public CloseableIterator<Map<String, Object>> iterator(Schema schema, Instant startDate, Instant endDate, Map<String, String> config) {
        List<String> sources = nwSources.getSourcesURI();
        return new EventsStreamIterator(schema, startDate, endDate, sources, config);
    }


    private class EventsStreamIterator implements CloseableIterator<Map<String, Object>>{

        private static final String UEBA = "ueba";

        private RecordStream stream;
        private Instant startTime;
        private Instant endTime;
        private String query;
        private String timeField;
        private String timeFieldMetaKey;
        private String connectionTimeout;
        private String socketTimeout;
      
        public EventsStreamIterator(Schema schema, Instant startTime, Instant endTime, List<String> sources, Map<String, String> configurations ) {
            try {
                this.startTime = startTime;
                this.endTime = endTime;
                this.query = configurations.get(QUERY);
                this.timeField = configurations.get(TIME_FIELD);
                this.connectionTimeout = configurations.get(CONNECTION_TIMEOUT);
                this.socketTimeout = configurations.get(SOCKET_TIMEOUT);
                this.stream = initializeStream(sources);
                this.timeFieldMetaKey = timeField.replace('.','_');
            } catch (Exception ex) {
                logger.error("start streaming failed", ex);
                throw new RuntimeException("start streaming failed", ex);
            }
        }


        @Override
        public void close() {
            if (stream != null) {
                stream.close();
            }
        }

        @Override
        public boolean hasNext() {
            boolean hasNext;
            try {
                hasNext = stream.hasNext();
            } catch (RecordStreamException ex) {
                logger.error("Stream hasNext failure", ex);
                throw new RuntimeException("Stream hasNext failure", ex);
            }
            return hasNext;
        }

        @Override
        public Map<String, Object> next() {
            Map<String, Object> event;

            try {
                event = stream.poll(10, TimeUnit.SECONDS);

                if (endOfBatch(event)) {
                    event = null;

                }
            } catch (Exception ex) {
                logger.error("failed to fetch next record", ex);
                throw new RuntimeException("failed to fetch next record", ex);
            }
            return event;
        }


        private URI initializeSource(String source) {

            logger.debug("adding source {}", source);

            try {
                URIBuilder uriBuilder = new URIBuilder(source);

                uriBuilder.addParameter(NwParameter.Mechanism.name(), "query");
                uriBuilder.addParameter(NwParameter.TimeMeta.name(), timeField);
                uriBuilder.addParameter(NwParameter.SkipQueryValidation.name(),Boolean.TRUE.toString());

                // timeout parameters
                if (StringUtils.isNotEmpty(connectionTimeout)){
                    uriBuilder.addParameter(NwParameter.ConnectionTimeout.name(), connectionTimeout);
                }

                if (StringUtils.isNotEmpty(socketTimeout)){
                    uriBuilder.addParameter(NwParameter.SocketTimeout.name(), socketTimeout);
                }

                // end streaming parameter
                long collectionDurationInMinutes = TimeUnit.MINUTES.convert(Duration.between(startTime, endTime).get(ChronoUnit.SECONDS), TimeUnit.SECONDS);
                uriBuilder.addParameter(NwParameter.CollectionDurationInMinutes.name(), Long.valueOf(collectionDurationInMinutes).toString());

                // query
                uriBuilder.addParameter(NwParameter.Query.name(), query);

                return uriBuilder.build();
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException(source);
            }
        }

        /**
         * Initialize the stream
         *
         * @see com.rsa.asoc.streams.RecordStreamBuilder for other stream parameters
         */
        private RecordStream initializeStream(List<String> sources) throws Exception{

            try {
                logger.debug("about to initializing stream for source {}", sources);

                // Construct a new stream
                RecordStream stream = RecordStreams.streamBuilder(UEBA)
                        .positionTracking(startOfBatch(startTime.getEpochSecond()))
                        .build();

                logger.debug("adding sources to stream{}", sources);

                // Apply source configuration and add them
                sources.stream().map(this::initializeSource).forEach(stream::addSource);

                logger.info("stream is ready");

                return stream;

            } catch (Throwable ex) {
                logger.error("failed to init NW stream: {}", ex.getMessage(), ex);
                throw  ex;
            }

        }

        /**
         * A position repository that starts source pulls at the chosen time.
         */
        private RecordSourcePosition.Repository startOfBatch(long epoch) {
            return new RecordSourcePosition.Repository() {
                String start = new NwPosition(0, epoch - 1).toJson();

                @Override
                public String getStreamPositionInSource(String ignored, String ignored2) {
                    return start;
                }

                @Override
                public void setStreamPositionInSource(String stream, String positionJson, String source) {
                    // Do nothing, we don't care for position tracking.
                }
            };
        }

        /**
         * Decide when we've had enough
         */
        private boolean endOfBatch(Map<String, Object> event) {
            if (event != null) {
                Object recordTime = event.get(timeFieldMetaKey);

                if (recordTime instanceof Long) {
                    return endTime.toEpochMilli() <= (long) recordTime;
                }
            }

            return false;
        }

    }

}
