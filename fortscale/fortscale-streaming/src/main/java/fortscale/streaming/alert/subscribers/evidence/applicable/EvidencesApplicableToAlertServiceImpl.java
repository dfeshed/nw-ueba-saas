package fortscale.streaming.alert.subscribers.evidence.applicable;

import fortscale.streaming.alert.event.wrappers.EnrichedFortscaleEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shays on 16/03/2016.
 */
public class EvidencesApplicableToAlertServiceImpl implements  EvidencesApplicableToAlertService{

    List<PreAlertDeciderFilter> alertCreatorCandidatesFilter;


    @Override
    public List<EnrichedFortscaleEvent> createIndicatorListApplicableForDecider(List<EnrichedFortscaleEvent> evidencesOrEntityEvents,
                                                                                Long startDate, Long endDate){

        List<EnrichedFortscaleEvent> applicableEvidencesOrEntityEvents = new ArrayList<>();
        for (EnrichedFortscaleEvent evidenceOrEntity : evidencesOrEntityEvents){
            if (canCreateAlert(evidenceOrEntity, startDate, endDate)){
                applicableEvidencesOrEntityEvents.add(evidenceOrEntity);
            }
        }
        return applicableEvidencesOrEntityEvents;
    }


    private boolean canCreateAlert(EnrichedFortscaleEvent evidencesOrEntityEvents, Long startDate, Long endDate){
        for (PreAlertDeciderFilter alertFilter: alertCreatorCandidatesFilter){
            if (alertFilter.filterMatch(evidencesOrEntityEvents.getAnomalyTypeFieldName(), evidencesOrEntityEvents.getEvidenceType())){
                boolean isCandidate = alertFilter.canCreateAlert(evidencesOrEntityEvents,startDate,endDate);
                if (!isCandidate){
                    //This indicator failed, and need to be dumped
                    return  false;
                }
            }
        }
        return  true; //If all alertCandidates doesn't filter the candidate, it can create an alert
    }

    public List<PreAlertDeciderFilter> getAlertCreatorCandidatesFilter() {
        return alertCreatorCandidatesFilter;
    }

    public void setAlertCreatorCandidatesFilter(List<PreAlertDeciderFilter> alertCreatorCandidatesFilter) {
        this.alertCreatorCandidatesFilter = alertCreatorCandidatesFilter;
    }
}
