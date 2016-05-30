package fortscale.streaming.alert.subscribers.evidence.applicable;

import fortscale.domain.core.AlertTimeframe;
import fortscale.streaming.alert.event.wrappers.EnrichedFortscaleEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shays on 16/03/2016.
 */
public class AlertFilterApplicableEvidencesServiceImpl implements AlertFilterApplicableEvidencesService {

    List<AlertPreAlertDeciderFilter> alertCreatorCandidatesFilter;


    @Override
    public List<EnrichedFortscaleEvent> createIndicatorListApplicableForDecider(List<EnrichedFortscaleEvent> evidencesOrEntityEvents,
                                                                                Long startDate, Long endDate, AlertTimeframe alertTimeframe){

        List<EnrichedFortscaleEvent> applicableEvidencesOrEntityEvents = new ArrayList<>();
        for (EnrichedFortscaleEvent evidenceOrEntity : evidencesOrEntityEvents){
            if (canCreateAlert(evidenceOrEntity, startDate, endDate, alertTimeframe)){
                applicableEvidencesOrEntityEvents.add(evidenceOrEntity);
            }
        }
        return applicableEvidencesOrEntityEvents;
    }


    private boolean canCreateAlert(EnrichedFortscaleEvent evidencesOrEntityEvents, Long startDate, Long endDate, AlertTimeframe alertTimeframe){
        for (AlertPreAlertDeciderFilter alertFilter: alertCreatorCandidatesFilter){
            if (alertFilter.filterMatch(evidencesOrEntityEvents.getAnomalyTypeFieldName(), evidencesOrEntityEvents.getEvidenceType())){
                boolean isCandidate = alertFilter.canCreateAlert(evidencesOrEntityEvents,startDate,endDate, alertTimeframe);
                if (!isCandidate){
                    //This indicator failed, and need to be dumped
                    return  false;
                }
            }
        }
        return  true; //If all alertCandidates doesn't filter the candidate, it can create an alert
    }

    public List<AlertPreAlertDeciderFilter> getAlertCreatorCandidatesFilter() {
        return alertCreatorCandidatesFilter;
    }

    public void setAlertCreatorCandidatesFilter(List<AlertPreAlertDeciderFilter> alertCreatorCandidatesFilter) {
        this.alertCreatorCandidatesFilter = alertCreatorCandidatesFilter;
    }
}
