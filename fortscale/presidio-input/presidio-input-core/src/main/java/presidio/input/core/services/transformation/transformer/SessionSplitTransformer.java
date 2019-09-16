package presidio.input.core.services.transformation.transformer;

import com.fasterxml.jackson.annotation.*;
import fortscale.common.general.Schema;
import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.domain.core.entityattributes.EntityAttributes;
import fortscale.domain.core.entityattributes.Ja3;
import fortscale.domain.core.entityattributes.SslSubject;
import fortscale.domain.sessionsplit.records.SessionSplitTransformerKey;
import fortscale.domain.sessionsplit.records.SessionSplitTransformerValue;
import fortscale.domain.sessionsplit.cache.ISessionSplitStoreCache;
import fortscale.utils.json.JacksonUtils;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeUtils;
import fortscale.utils.transform.AbstractJsonObjectTransformer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import presidio.sdk.api.domain.transformedevents.TlsTransformedEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.*;


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
    @Autowired
    private ISessionSplitStoreCache sessionSplitStoreCache;

    @JsonCreator
    public SessionSplitTransformer(@JsonProperty("name") String name,
                                   @JsonProperty("schema") String schema,
                                   @JsonProperty("projectionFields") List<String> projectionFields) {
        super(name);
        this.schema = Schema.valueOf(schema.toUpperCase());
        this.projectionFields = projectionFields;
    }


    @Override
    public JSONObject transform(JSONObject document) {
        int eventSessionSplit = (int) document.get(TlsTransformedEvent.SESSION_SPLIT_FIELD_NAME);

        String eventSrcIp = (String) document.get(TlsTransformedEvent.SOURCE_IP_FIELD_NAME);
        String eventDstIp = (String) document.get(TlsTransformedEvent.DESTINATION_IP_FIELD_NAME);
        String eventSrcPort = (String) document.get(TlsTransformedEvent.SOURCE_PORT_FIELD_NAME);
        String eventDstPort = (String) jacksonUtils.getFieldValue(document, namePath(TlsTransformedEvent.DESTINATION_PORT_FIELD_NAME), null);
        Instant eventDateTime = TimeUtils.parseInstant((String) document.get(AbstractAuditableDocument.DATE_TIME_FIELD_NAME));

        SessionSplitTransformerKey key = new SessionSplitTransformerKey(eventSrcIp, eventDstIp, eventDstPort, eventSrcPort);

        if (eventSessionSplit == zeroSessionSplit) {
            String sslSubjectName = (String) jacksonUtils.getFieldValue(document, namePath(TlsTransformedEvent.SSL_SUBJECT_FIELD_NAME), null);
            String ja3Name = (String) jacksonUtils.getFieldValue(document, namePath(TlsTransformedEvent.JA3_FIELD_NAME), null);
            String ja3s = (String) document.get(TlsTransformedEvent.JA3S_FIELD_NAME);
            List<String> sslCas = JacksonUtils.jsonArrayToList((JSONArray) document.get(TlsTransformedEvent.SSL_CAS_FIELD_NAME));

            SessionSplitTransformerValue value = new SessionSplitTransformerValue(eventDateTime, zeroSessionSplit,
                    new SslSubject(sslSubjectName), sslCas, new Ja3(ja3Name), ja3s);
            sessionSplitStoreCache.write(key, value);
        }
        // enrich events with sessionSplit > 0
        else if (eventSessionSplit > zeroSessionSplit) {
            SessionSplitTransformerValue value = sessionSplitStoreCache.read(key);

            if (value != null && value.getDateTime().compareTo(eventDateTime) <= 0) {
                if (value.getSessionSplit() == eventSessionSplit - 1) {
                    setEntityAttribute(document, TlsTransformedEvent.SSL_SUBJECT_FIELD_NAME, value.getSslSubject());
                    setEntityAttribute(document, TlsTransformedEvent.JA3_FIELD_NAME, value.getJa3());
                    document.put(TlsTransformedEvent.SSL_CAS_FIELD_NAME, value.getSslCas());
                    document.put(TlsTransformedEvent.JA3S_FIELD_NAME, value.getJa3s());

                    value.setSessionSplit(eventSessionSplit);
                    sessionSplitStoreCache.write(key, value);
                    logger.debug("Enrich {} tls event with missed fields.", document.get(DOCUMENT_ID_FIELD));
                } else {
                    logger.info("The {} tls event can't be enriched due to missing zero session split event.", document.get(DOCUMENT_ID_FIELD));
                }
            } else {
                logger.info("There is no appropriate tls event with zero session split for {} event.", document.get(DOCUMENT_ID_FIELD));
            }
        }
        return document;
    }

    private void setEntityAttribute(JSONObject jsonObject, String fieldName, EntityAttributes entityAttributes) {
        if (entityAttributes != null) {
            JSONObject entityObject = new JSONObject(entityAttributes);
            jsonObject.put(fieldName, entityObject);
        }
    }

    private String namePath(String prefixPath) {
        return prefixPath + NAME_FIELD_SUFFIX;
    }
}