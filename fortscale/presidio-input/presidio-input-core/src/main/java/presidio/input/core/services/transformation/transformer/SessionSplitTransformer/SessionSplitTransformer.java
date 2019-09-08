package presidio.input.core.services.transformation.transformer.SessionSplitTransformer;

import com.fasterxml.jackson.annotation.*;
import fortscale.common.general.Schema;
import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.domain.core.entityattributes.EntityAttributes;
import fortscale.domain.core.entityattributes.Ja3;
import fortscale.domain.core.entityattributes.SslSubject;
import fortscale.utils.json.JacksonUtils;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeUtils;
import fortscale.utils.transform.AbstractJsonObjectTransformer;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import presidio.sdk.api.domain.RawEventsPageIterator;
import presidio.sdk.api.domain.rawevents.TlsRawEvent;
import presidio.sdk.api.domain.transformedevents.TlsTransformedEvent;
import presidio.sdk.api.services.PresidioInputPersistencyService;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;


@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.ANY,
        isGetterVisibility = JsonAutoDetect.Visibility.ANY,
        setterVisibility = JsonAutoDetect.Visibility.ANY
)
@JsonTypeName("session-split-transformer")
public class SessionSplitTransformer extends AbstractJsonObjectTransformer {

    private static final Logger logger = Logger.getLogger(SessionSplitTransformer.class);
    private static final String DOCUMENT_ID_FIELD = "id";
    private static final String NAME_FIELD_SUFFIX = ".name";
    private static final int zeroSessionSplit = 0;
    private static final JacksonUtils jacksonUtils = new JacksonUtils();
    private List<String> projectionFields;
    private Schema schema;

    @Value("#{T(java.time.Duration).parse('${split.transformer.intervel:PT12H}')}")
    private Duration interval;

    @Value("${split.transformer.page.size:100000}")
    private Integer pageSize;

    @JacksonInject("endDate")
    private Instant endDate;

    @JsonIgnore
    private Map<SessionSplitTransformerKey, TreeSet<SessionSplitTransformerValue>> splitTransformerMap;

    @JsonIgnore
    @Autowired
    private PresidioInputPersistencyService inputPersistencyService;


    @JsonCreator
    public SessionSplitTransformer(@JsonProperty("name") String name,
                                   @JsonProperty("schema") String schema,
                                   @JsonProperty("projectionFields") List<String> projectionFields) {
        super(name);
        this.schema = Schema.valueOf(schema.toUpperCase());
        this.projectionFields = projectionFields;
    }

    @PostConstruct
    public void postAutowireProcessor() {
        this.splitTransformerMap = createSplitTransformerMap(inputPersistencyService, interval, endDate, schema, pageSize, zeroSessionSplit, projectionFields);
    }

    @Override
    public JSONObject transform(JSONObject document) {
        int eventSessionSplit = (int)document.get(TlsTransformedEvent.SESSION_SPLIT_FIELD_NAME);

        // enrich events with sessionSplit > 0
        if (eventSessionSplit > zeroSessionSplit) {
            String eventSrcIp = (String)document.get(TlsTransformedEvent.SOURCE_IP_FIELD_NAME);
            String eventDstIp = (String)document.get(TlsTransformedEvent.DESTINATION_IP_FIELD_NAME);
            String eventSrcPort = (String)document.get(TlsTransformedEvent.SOURCE_PORT_FIELD_NAME);
            String eventDstPort = (String) jacksonUtils.getFieldValue(document, namePath(TlsTransformedEvent.DESTINATION_PORT_FIELD_NAME), null);
            SessionSplitTransformerKey key = new SessionSplitTransformerKey(eventSrcIp, eventDstIp, eventDstPort, eventSrcPort);

            TreeSet<SessionSplitTransformerValue> treeSet = splitTransformerMap.get(key);
            if (treeSet != null) {

                // loop on desc treeSet in order to find the right session by dateTime
                for (SessionSplitTransformerValue value : treeSet) {
                    Instant eventDateTime = TimeUtils.parseInstant((String)document.get(AbstractAuditableDocument.DATE_TIME_FIELD_NAME));
                    if (value.getDateTime().compareTo(eventDateTime) <= 0) {
                        // handle missing events
                        if (value.getSessionSplit() == eventSessionSplit - 1) {
                            String sslSubjectName = (String) jacksonUtils.getFieldValue(document, namePath(TlsTransformedEvent.SSL_SUBJECT_FIELD_NAME), null);
                            String ja3Name = (String) jacksonUtils.getFieldValue(document, namePath(TlsTransformedEvent.JA3_FIELD_NAME), null);
                            setEntityAttribute(document, TlsTransformedEvent.SSL_SUBJECT_FIELD_NAME, new SslSubject(sslSubjectName));
                            setEntityAttribute(document, TlsTransformedEvent.JA3_FIELD_NAME, new Ja3(ja3Name));
                            document.put(TlsTransformedEvent.SSL_CAS_FIELD_NAME, document.get(TlsTransformedEvent.SSL_CAS_FIELD_NAME));
                            document.put(TlsTransformedEvent.JA3S_FIELD_NAME, document.get(TlsTransformedEvent.JA3S_FIELD_NAME));
                            value.setSessionSplit(eventSessionSplit);
                            logger.debug("Enrich {} tls event with missed fields.", document.get(DOCUMENT_ID_FIELD));
                        } else {
                            //remove key of closed session
                            splitTransformerMap.remove(key);
                            logger.info("The {} tls event can't be enriched due to missing zero session split event.", document.get(DOCUMENT_ID_FIELD));
                        }
                        break;
                    }
                }
            } else {
                logger.info("There is no appropriate tls event with zero session split for {} event.", document.get(DOCUMENT_ID_FIELD));
            }
        }
        return document;
    }

    private Map<SessionSplitTransformerKey, TreeSet<SessionSplitTransformerValue>> createSplitTransformerMap(PresidioInputPersistencyService inputPersistencyService, Duration interval,
                                                                                                           Instant endDate, Schema schema, Integer pageSize, int zeroSessionSplit, List<String> projectionFields) {
        Map<SessionSplitTransformerKey, TreeSet<SessionSplitTransformerValue>> splitTransformerMap = new HashMap<>();

        // Read records with sessionSplit=0
        Map<String, Object> sessionSplitFilter = new HashMap<>();
        sessionSplitFilter.put(TlsRawEvent.SESSION_SPLIT_FIELD_NAME, zeroSessionSplit);

        RawEventsPageIterator rawEventsPageIterator = new RawEventsPageIterator(endDate.minus(interval), endDate, inputPersistencyService, schema, pageSize, sessionSplitFilter, projectionFields);
        while (rawEventsPageIterator.hasNext()) {
            try {
                List<TlsRawEvent> tlsRawEvents = rawEventsPageIterator.next();
                logger.debug("read {} zero session split events", tlsRawEvents.size());

                for (TlsRawEvent tlsRawEvent : tlsRawEvents) {
                    SessionSplitTransformerKey key = new SessionSplitTransformerKey(tlsRawEvent.getSrcIp(), tlsRawEvent.getDstIp(), tlsRawEvent.getDstPort().getName(), tlsRawEvent.getSrcPort());

                    splitTransformerMap.compute(key, (k, treeSet) -> {
                        if (treeSet == null) {
                            treeSet = new TreeSet<>();
                        }
                        SessionSplitTransformerValue value = new SessionSplitTransformerValue(tlsRawEvent.getDateTime(), tlsRawEvent.getSessionSplit(), tlsRawEvent.getSslSubject(), tlsRawEvent.getSslCas(), tlsRawEvent.getJa3(), tlsRawEvent.getJa3s());
                        treeSet.add(value);
                        return treeSet;
                    });
                }
            } catch (IllegalArgumentException ex) {
                logger.error("Error reading events from repository.", ex);
            }
        }

        return splitTransformerMap;
    }

    private void setEntityAttribute(JSONObject jsonObject, String fieldName, EntityAttributes entityAttributes) {
        JSONObject entityObject = new JSONObject(entityAttributes);
        jsonObject.put(fieldName, entityObject);
    }

    private String namePath(String prefixPath) {
        return prefixPath + NAME_FIELD_SUFFIX;
    }
}