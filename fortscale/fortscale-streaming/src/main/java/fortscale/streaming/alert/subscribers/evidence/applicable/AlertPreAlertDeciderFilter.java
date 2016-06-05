package fortscale.streaming.alert.subscribers.evidence.applicable;

import fortscale.domain.core.AlertTimeframe;
import fortscale.domain.core.EvidenceType;
import fortscale.streaming.alert.event.wrappers.EnrichedFortscaleEvent;

/**
 * Created by shays on 16/03/2016.
 */
public interface AlertPreAlertDeciderFilter {

    boolean canCreateAlert(EnrichedFortscaleEvent evidencesOrEntityEvents, Long startDate, Long endDate, AlertTimeframe timeframe);
    boolean filterMatch(String anomalyType, EvidenceType evidenceType);
}
