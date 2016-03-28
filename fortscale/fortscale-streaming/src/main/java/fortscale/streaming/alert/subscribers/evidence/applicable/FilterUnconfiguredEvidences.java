package fortscale.streaming.alert.subscribers.evidence.applicable;

import fortscale.domain.core.EvidenceType;
import fortscale.streaming.alert.event.wrappers.EnrichedFortscaleEvent;
import fortscale.streaming.alert.subscribers.evidence.decider.DeciderConfiguration;
import org.springframework.beans.factory.annotation.Autowired;

;

/**
 * Created by shays on 23/03/2016.
 * This class check if the evidence of entity event have configuration for alert creation.
 * If no configuration - the the evidence or entity event will be filtered.
 */
public class FilterUnconfiguredEvidences implements  PreAlertDeciderFilter{

    @Autowired
    private DeciderConfiguration conf;

    @Override
    public boolean canCreateAlert(EnrichedFortscaleEvent evidencesOrEntityEvents, Long startTime, Long endTime) {
        return conf.getNamePriority().get(evidencesOrEntityEvents.getAnomalyTypeFieldName()) != null &&
                conf.getAlertName().get(evidencesOrEntityEvents.getAnomalyTypeFieldName()) != null &&
                conf.getScorePriority().get(evidencesOrEntityEvents.getAnomalyTypeFieldName()) != null;


    }

    @Override
    public boolean filterMatch(String anomalyType, EvidenceType evidenceType) {
        return true; //Work for all entity events and evidneces
    }
}
