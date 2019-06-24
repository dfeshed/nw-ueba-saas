package presidio.input.core.services.transformation.factory;


import fortscale.common.general.Schema;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import presidio.input.core.RawEventsPageIterator;
import presidio.input.core.services.transformation.transformer.SessionSplitTransformer.SessionSplitTransformer;
import presidio.input.core.services.transformation.transformer.SessionSplitTransformer.SessionSplitTransformerKey;
import presidio.input.core.services.transformation.transformer.SessionSplitTransformer.SessionSplitTransformerValue;
import presidio.input.core.services.transformation.transformer.Transformer;
import presidio.sdk.api.domain.rawevents.TlsRawEvent;
import presidio.sdk.api.services.PresidioInputPersistencyService;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@SuppressWarnings("unused")
@Component
public class SessionSplitTransformerFactory extends AbstractServiceAutowiringFactory<Transformer> {

    private static final Logger logger = Logger.getLogger(SessionSplitTransformerFactory.class);

    @Autowired
    private PresidioInputPersistencyService inputPersistencyService;
    @Value("#{T(java.time.Duration).parse('${split.transformer.intervel:PT12H}')}")
    private Duration interval;
    @Value("${split.transformer.page.size:100000}")
    private Integer pageSize;

    @Override
    public String getFactoryName() {
        return SessionSplitTransformerConf.SESSION_SPLIT_TRANSFORMER_FACTORY_NAME;
    }

    @Override
    public SessionSplitTransformer getProduct(FactoryConfig factoryConfig) {
        SessionSplitTransformerConf config = (SessionSplitTransformerConf) factoryConfig;

        Map<SessionSplitTransformerKey, TreeSet<SessionSplitTransformerValue>> splitTransformerMap =
                createSplitTransformerMap(inputPersistencyService, interval, config.getEndDate(), config.getSchema(), pageSize, config.getZeroSessionSplit(), config.getProjectionFields());

        return new SessionSplitTransformer(splitTransformerMap, config.getZeroSessionSplit());
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
                    SessionSplitTransformerKey key = new SessionSplitTransformerKey(tlsRawEvent.getSrcIp(), tlsRawEvent.getDstIp(), tlsRawEvent.getDstPort(), tlsRawEvent.getSrcPort());

                    splitTransformerMap.compute(key, (k, treeSet) -> {
                        if (treeSet == null) {
                            treeSet = new TreeSet<>();
                        }
                        SessionSplitTransformerValue value = new SessionSplitTransformerValue(tlsRawEvent.getDateTime(), tlsRawEvent.getSessionSplit(), tlsRawEvent.getSslSubject(), tlsRawEvent.getSslCa(), tlsRawEvent.getJa3(), tlsRawEvent.getJa3s());
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


}
