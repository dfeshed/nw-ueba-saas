package fortscale.streaming.alert.subscribers.evidence.applicable;

import fortscale.domain.core.AlertTimeframe;
import fortscale.domain.core.EvidenceType;
import fortscale.streaming.alert.event.wrappers.EnrichedFortscaleEvent;
import fortscale.streaming.alert.subscribers.evidence.decider.AlertTypeConfigurationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

;

/**
 * Created by shays on 23/03/2016.
 * This class check if the evidence of entity event have configuration for alert creation.
 * If no configuration - the the evidence or entity event will be filtered.
 */
public class FilterUnconfiguredEvidences implements AlertPreAlertDeciderFilter {

    @Autowired
    private AlertTypeConfigurationServiceImpl alertTypeConfigurationService;

    @Override
    public boolean canCreateAlert(EnrichedFortscaleEvent evidencesOrEntityEvents, Long startTime, Long endTime, AlertTimeframe timeframe) {
        return alertTypeConfigurationService.configurationExists(evidencesOrEntityEvents.getAnomalyTypeFieldName(),timeframe);


    }

    @Override
    public boolean filterMatch(String anomalyType, EvidenceType evidenceType) {
        return true; //Work for all entity events and evidneces
    }
}
