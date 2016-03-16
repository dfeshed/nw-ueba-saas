package fortscale.streaming.alert.subscribers.alert.creator.candidate;

import fortscale.domain.core.EvidenceType;

import java.util.Map;

/**
 * Created by shays on 16/03/2016.
 */
public interface AlertCreatorCandidate {

    boolean canCreateAlert(String anomalyType, EvidenceType evidenceType,Map<String, String> evidence);
    boolean filterMatch(String anomalyType, EvidenceType evidenceType);
}
