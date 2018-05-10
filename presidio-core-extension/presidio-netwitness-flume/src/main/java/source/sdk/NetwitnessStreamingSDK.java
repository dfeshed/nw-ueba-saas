package source.sdk;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rsa.asoc.streams.RecordSource;
import com.rsa.asoc.streams.RecordStream;
import com.rsa.asoc.streams.base.Configuration;
import com.rsa.asoc.streams.base.DefaultRecordStream;
import com.rsa.asoc.streams.policy.RecordStreamPolicy;
import com.rsa.asoc.streams.policy.RecordStreamPolicyDecorator;
import com.rsa.asoc.streams.policy.RecordStreamPolicyParameter;
import com.rsa.asoc.streams.policy.TimeOrderedStreamPolicy;
import com.rsa.asoc.streams.source.netwitness.NwParameter;
import domain.NetwitnessAuthenticationMessage;
import fortscale.common.general.Schema;
import fortscale.domain.core.AbstractDocument;
import org.apache.http.client.utils.URIBuilder;
import org.flume.source.sdk.StreamingSDK;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class NetwitnessStreamingSDK implements StreamingSDK {

    //private static final String BROKER_END_POINT = "nw://admin:netwitness@localhost:50003";
    private static final String BROKER_END_POINT = "nw://admin:netwitness@10.25.67.33:50005";
    private static final String UEBA = "ueba";

    private AtomicBoolean isRunning = new AtomicBoolean(true);

    private static Logger logger = LoggerFactory.getLogger(NetwitnessStreamingSDK.class);

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    RecordStream stream;
    int count = 0;

    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        OBJECT_MAPPER.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
    }


    public NetwitnessStreamingSDK() {

    }

    public void startStreaming(Schema schema, Instant startDate, Instant endDate) {
        try {
            stream = buildStream(UEBA);
            isRunning.set(true);
            addSource(startDate, endDate, stream);
        } catch (Exception ex) {
            logger.error("start streaming failed", ex);
        }
    }

    public boolean hasNext(){
        if (!isRunning.get()) {
            Map<String, Object> streamStatus = (Map<String, Object>)stream.getStatus(false).get("stream");
            long recordsBuffered = (Long) streamStatus.get("recordsBuffered");
            if (recordsBuffered == 0) {
                return false;
            }
        }
        return true;
    }

    public AbstractDocument next(){
        NetwitnessAuthenticationMessage netwitnessMessage = null;
        Map<String, Object> nextMessage = null;

        try {
            do {
                nextMessage = stream.poll(1000, TimeUnit.MILLISECONDS);
            } while (nextMessage == null && hasNext());

            if (nextMessage != null) {
                count++;
                netwitnessMessage = OBJECT_MAPPER.readValue(getJsonFromMap(nextMessage).toString(), NetwitnessAuthenticationMessage.class);
                logger.info(""+netwitnessMessage);
            }

        } catch (Exception ex) {
            logger.error("next record failed", ex);
        }
        return netwitnessMessage;
    }

    public void stopStreaming(){
        stream.close();
    }

    private RecordStream buildStream(String name) {
        Configuration configuration = new Configuration(URI.create(name), RecordStreamPolicyParameter.SUPPORTED);
        RecordStreamPolicy policy = new TimeOrderedStreamPolicy(configuration);
        RecordStreamPolicyDecorator recordStreamPolicyDecorator = new RecordStreamPolicyDecorator(policy) {
            public void handleSourceComplete(RecordSource source) {
                super.handleSourceComplete(source);
                isRunning.set(false);
            }
        };
        RecordStream stream = new DefaultRecordStream(recordStreamPolicyDecorator);
        return  stream;
    }

    private void addSource(Instant startTime, Instant endTime, RecordStream stream) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(BROKER_END_POINT);

        uriBuilder.addParameter(NwParameter.Mechanism.name(), "query");
        //uriBuilder.addParameter(NwParameter.TimeMeta.name(), "event.time");

        // start streaming parameter
        long minutesBack = TimeUnit.MINUTES.convert(Duration.between(startTime, Instant.now()).get(ChronoUnit.SECONDS),TimeUnit.SECONDS) ;
        uriBuilder.addParameter(NwParameter.MinutesBack.name(), Long.valueOf(minutesBack).toString());

        // end streaming parameter
        long collectionDurationInMinutes =TimeUnit.MINUTES.convert(Duration.between(startTime, endTime).get(ChronoUnit.SECONDS),TimeUnit.SECONDS) ;
        uriBuilder.addParameter(NwParameter.CollectionDurationInMinutes.name(), Long.valueOf(collectionDurationInMinutes).toString());

        // event source
        uriBuilder.addParameter(NwParameter.Query.name(), "select *");
       // uriBuilder.addParameter(NwParameter.Query.name(), "select * where alias.host exists && reference.id = '4624'");
        //uriBuilder.addParameter(NwParameter.Query.name(), "select * where event.source="+eventSource);

        stream.addSource(uriBuilder.build());
    }

    private JSONObject getJsonFromMap(Map<String, Object> map) throws JSONException {
        JSONObject jsonData = new JSONObject();
        for (String key : map.keySet()) {
            Object value = map.get(key);
            if (value instanceof Map<?, ?>) {
                value = getJsonFromMap((Map<String, Object>) value);
            } else if(value instanceof Object[]) {
                Object[] arr = (Object[]) value;
                value = arr.length > 0? arr[0]: null;
            }
            if (value!=null) {
                jsonData.put(key, value);
            }
        }
        return jsonData;
    }
}

