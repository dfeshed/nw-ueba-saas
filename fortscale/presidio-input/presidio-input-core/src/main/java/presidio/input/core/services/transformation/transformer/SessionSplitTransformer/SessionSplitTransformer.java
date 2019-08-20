package presidio.input.core.services.transformation.transformer.SessionSplitTransformer;

import fortscale.utils.logging.Logger;
import presidio.input.core.services.transformation.transformer.Transformer;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.transformedevents.TlsTransformedEvent;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class SessionSplitTransformer implements Transformer {

    private static final Logger logger = Logger.getLogger(SessionSplitTransformer.class);
    private int zeroSessionSplit;
    private Map<SessionSplitTransformerKey, TreeSet<SessionSplitTransformerValue>> splitTransformerMap;


    public SessionSplitTransformer(Map<SessionSplitTransformerKey, TreeSet<SessionSplitTransformerValue>> splitTransformerMap, int zeroSessionSplit) {
        this.splitTransformerMap = splitTransformerMap;
        this.zeroSessionSplit = zeroSessionSplit;
    }

    @Override
    public List<AbstractInputDocument> transform(List<AbstractInputDocument> documents) {
        documents.forEach((AbstractInputDocument document) -> {
            TlsTransformedEvent tlsTransformedEvent = (TlsTransformedEvent) document;

            // enrich events with sessionSplit > 0
            if (tlsTransformedEvent.getSessionSplit() != null && tlsTransformedEvent.getSessionSplit() > zeroSessionSplit) {
                SessionSplitTransformerKey key = new SessionSplitTransformerKey(tlsTransformedEvent.getSrcIp(), tlsTransformedEvent.getDstIp(), tlsTransformedEvent.getDstPort(), tlsTransformedEvent.getSrcPort());

                TreeSet<SessionSplitTransformerValue> treeSet = splitTransformerMap.get(key);
                if (treeSet != null) {

                    // loop on desc treeSet in order to find the right session by dateTime
                    for (SessionSplitTransformerValue value : treeSet) {
                        if (value.getDateTime().compareTo(tlsTransformedEvent.getDateTime()) <= 0) {
                            // handle missing events
                            if (value.getSessionSplit() == tlsTransformedEvent.getSessionSplit() - 1) {
                                tlsTransformedEvent.setSslSubject(value.getSslSubject());
                                tlsTransformedEvent.setSslCas(value.getSslCas());
                                tlsTransformedEvent.setJa3(value.getJa3());
                                tlsTransformedEvent.setJa3s(value.getJa3s());
                                value.setSessionSplit(tlsTransformedEvent.getSessionSplit());
                                logger.debug("Enrich {} tls event with missed fields.", tlsTransformedEvent.getId());
                                break;
                            } else {
                                //remove key of closed session
                                splitTransformerMap.remove(key);
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
