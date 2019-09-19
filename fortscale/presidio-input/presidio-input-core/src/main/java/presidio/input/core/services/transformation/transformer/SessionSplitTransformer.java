package presidio.input.core.services.transformation.transformer;

import com.fasterxml.jackson.annotation.*;
import fortscale.domain.core.entityattributes.Ja3;
import fortscale.domain.core.entityattributes.SslSubject;
import fortscale.domain.sessionsplit.records.SessionSplitTransformerKey;
import fortscale.domain.sessionsplit.records.SessionSplitTransformerValue;
import fortscale.domain.sessionsplit.cache.ISessionSplitStoreCache;
import fortscale.utils.json.JacksonUtils;
import fortscale.utils.logging.Logger;
import fortscale.utils.transform.AbstractJsonObjectTransformer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import presidio.sdk.api.domain.transformedevents.TlsTransformedEvent;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * SessionSplitTransformer enriched session events with sessionSplit > 0.
 *
 * Assumptions:
 * transform method get sorted events by date time.
 *
 * transform method:
 *  if the event has no SESSION_SPLIT field => return the event
 *  if the event has SESSION_SPLIT == 0  => save the enriched data in sessionSplitStoreCache.
 *  if the event has SESSION_SPLIT > 0  => get the data of sessionSplitStoreCache and enrich the event.
 *
 */
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

    @JsonIgnore
    @Autowired
    private ISessionSplitStoreCache sessionSplitStoreCache;

    @JsonCreator
    public SessionSplitTransformer(@JsonProperty("name") String name) {
        super(name);
    }


    @Override
    public JSONObject transform(JSONObject document) {
        if(document.isNull(TlsTransformedEvent.SESSION_SPLIT_FIELD_NAME)) {
            return document;
        }

        int eventSessionSplit = (int) document.get(TlsTransformedEvent.SESSION_SPLIT_FIELD_NAME);
        String eventSrcIp = (String) document.get(TlsTransformedEvent.SOURCE_IP_FIELD_NAME);
        String eventDstIp = (String) document.get(TlsTransformedEvent.DESTINATION_IP_FIELD_NAME);
        String eventSrcPort = (String) document.get(TlsTransformedEvent.SOURCE_PORT_FIELD_NAME);
        String eventDstPort = (String) jacksonUtils.getFieldValue(document, namePath(TlsTransformedEvent.DESTINATION_PORT_FIELD_NAME), null);
        SessionSplitTransformerKey key = new SessionSplitTransformerKey(eventSrcIp, eventDstIp, eventDstPort, eventSrcPort);

        if (eventSessionSplit == zeroSessionSplit) {
            String sslSubjectName = (String) jacksonUtils.getFieldValue(document, namePath(TlsTransformedEvent.SSL_SUBJECT_FIELD_NAME), null);
            String ja3Name = (String) jacksonUtils.getFieldValue(document, namePath(TlsTransformedEvent.JA3_FIELD_NAME), null);

            String ja3s = null;
            if(!document.isNull(TlsTransformedEvent.JA3S_FIELD_NAME)){
                ja3s = (String) document.get(TlsTransformedEvent.JA3S_FIELD_NAME);
            }

            List<String> sslCas = null;
            if(!document.isNull(TlsTransformedEvent.SSL_CAS_FIELD_NAME)){
                sslCas = JacksonUtils.jsonArrayToList((JSONArray) document.get(TlsTransformedEvent.SSL_CAS_FIELD_NAME));
            }

            SessionSplitTransformerValue value = new SessionSplitTransformerValue(zeroSessionSplit, sslSubjectName, sslCas, ja3Name, ja3s);
            sessionSplitStoreCache.write(key, value);
        }
        // enrich events with sessionSplit > 0
        else if (eventSessionSplit > zeroSessionSplit) {
            SessionSplitTransformerValue value = sessionSplitStoreCache.read(key);

            if (value != null) {
                if (value.getSessionSplit() == eventSessionSplit - 1) {
                    setEntityAttribute(document, TlsTransformedEvent.SSL_SUBJECT_FIELD_NAME,  value.getSslSubject(), SslSubject.class);
                    setEntityAttribute(document, TlsTransformedEvent.JA3_FIELD_NAME, value.getJa3(), Ja3.class);
                    put(document, TlsTransformedEvent.SSL_CAS_FIELD_NAME, value.getSslCas());
                    put(document, TlsTransformedEvent.JA3S_FIELD_NAME, value.getJa3s());
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

    private void put(JSONObject jsonObject, String fieldName, Object value){
        if( value != null){
            jsonObject.put(fieldName, value);
        }
    }

    private void setEntityAttribute(JSONObject jsonObject, String fieldName, String entityAttributeName, Class clzz) {
        if (entityAttributeName != null) {
            try {
                Constructor constructor = clzz.getDeclaredConstructor(String.class);
                Object entityAttributes = constructor.newInstance(entityAttributeName);
                JSONObject entityObject = new JSONObject(entityAttributes);
                jsonObject.put(fieldName, entityObject);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    private String namePath(String prefixPath) {
        return prefixPath + NAME_FIELD_SUFFIX;
    }
}