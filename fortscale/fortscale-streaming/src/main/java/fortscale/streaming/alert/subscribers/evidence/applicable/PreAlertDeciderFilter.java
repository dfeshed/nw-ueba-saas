package fortscale.streaming.alert.subscribers.evidence.applicable;

import fortscale.domain.core.EvidenceType;
import fortscale.streaming.alert.event.wrappers.EnrichedFortscaleEvent;

/**
 * Created by shays on 16/03/2016.
 */
public interface PreAlertDeciderFilter {

    boolean canCreateAlert(EnrichedFortscaleEvent evidencesOrEntityEvents);
    boolean filterMatch(String anomalyType, EvidenceType evidenceType);
}
