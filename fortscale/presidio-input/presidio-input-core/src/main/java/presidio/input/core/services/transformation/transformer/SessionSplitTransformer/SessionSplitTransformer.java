package presidio.input.core.services.transformation.transformer.SessionSplitTransformer;

import fortscale.common.general.Schema;
import fortscale.utils.logging.Logger;
import presidio.input.core.RawEventsPageIterator;
import presidio.input.core.services.transformation.transformer.Transformer;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.rawevents.TlsRawEvent;
import presidio.sdk.api.domain.transformedevents.TlsTransformedEvent;
import presidio.sdk.api.services.PresidioInputPersistencyService;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class SessionSplitTransformer implements Transformer {


    private static final Logger logger = Logger.getLogger(SessionSplitTransformer.class);
    private static final int ZERO_SESSION_SPLIT = 0;
    private Map<SessionSplitTransformerKey, TreeSet<SessionSplitTransformerValue>> splitTransformerMap = new HashMap<>();


    public SessionSplitTransformer(PresidioInputPersistencyService inputPersistencyService, Duration interval,
                                   Instant endDate, Schema schema, Integer pageSize) {

        // Read records with sessionSplit=0
        Map<String, Object> sessionSplitFilter = new HashMap<>();
        sessionSplitFilter.put(TlsRawEvent.SESSION_SPLIT_FIELD_NAME, ZERO_SESSION_SPLIT);

        RawEventsPageIterator rawEventsPageIterator = new RawEventsPageIterator(endDate.minus(interval), endDate, inputPersistencyService, schema, pageSize, sessionSplitFilter);
        while (rawEventsPageIterator.hasNext()) {
            try {
                List<TlsRawEvent> tlsRawEvents = rawEventsPageIterator.next();
                logger.debug("read {} zero session split events", tlsRawEvents.size());

                for (TlsRawEvent tlsRawEvent : tlsRawEvents) {
                    SessionSplitTransformerKey key = new SessionSplitTransformerKey(tlsRawEvent.getSrcIp(), tlsRawEvent.getDstIp(), tlsRawEvent.getDstPort(), tlsRawEvent.getSrcPort());
                    TreeSet<SessionSplitTransformerValue> treeSet = new TreeSet<>();
                    if (splitTransformerMap.containsKey(key)) {
                        treeSet = splitTransformerMap.get(key);
                    }
                    SessionSplitTransformerValue value = new SessionSplitTransformerValue(tlsRawEvent.getDateTime(), tlsRawEvent.getSessionSplit(), tlsRawEvent.getSslSubject(), tlsRawEvent.getSslCa(), tlsRawEvent.getJa3());
                    treeSet.add(value);
                    splitTransformerMap.put(key, treeSet);
                }
            } catch (IllegalArgumentException ex) {
                logger.error("Error reading events from repository.", ex);
            }
        }
    }


    @Override
    public List<AbstractInputDocument> transform(List<AbstractInputDocument> documents) {
        documents.forEach((AbstractInputDocument document) -> {
            TlsTransformedEvent tlsTransformedEvent = (TlsTransformedEvent) document;

            // enrich events with sessionSplit > 0
            if (tlsTransformedEvent.getSessionSplit() > ZERO_SESSION_SPLIT) {
                SessionSplitTransformerKey key = new SessionSplitTransformerKey(tlsTransformedEvent.getSrcIp(), tlsTransformedEvent.getDstIp(), tlsTransformedEvent.getDstPort(), tlsTransformedEvent.getSrcPort());


                if (splitTransformerMap.containsKey(key)) {

                    TreeSet<SessionSplitTransformerValue> treeSet = splitTransformerMap.get(key);

                    // loop on desc treeSet in order to find the right session by dateTime
                    for (SessionSplitTransformerValue value : treeSet) {
                        if (value.getDateTime().compareTo(tlsTransformedEvent.getDateTime()) <= 0) {
                            // handle missing events
                            if (value.getSessionSplit() == tlsTransformedEvent.getSessionSplit() - 1) {
                                tlsTransformedEvent.setSslSubject(value.getSslSubject());
                                tlsTransformedEvent.setSslCa(value.getSslCa());
                                tlsTransformedEvent.setJa3(value.getJa3());
                                value.setSessionSplit(tlsTransformedEvent.getSessionSplit());
                                logger.debug("Enrich {} tls event with missed fields.", tlsTransformedEvent.getId());
                                break;
                            } else {
                                logger.info("The {} tls event can't be enriched due to missing zero session split event.", tlsTransformedEvent.getId());
                            }
                        }
                    }
                } else {
                    logger.info("There is no appropriate tls event with zero session split for {} event.", tlsTransformedEvent.getId());
                }
            }
        });

        return documents;
    }

}
