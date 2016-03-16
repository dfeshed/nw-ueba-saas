package fortscale.streaming.alert.subscribers.alert.creator.candidate;

import fortscale.domain.core.EvidenceType;

import java.util.List;
import java.util.Map;

/**
 * Created by shays on 16/03/2016.
 */
public class AlertCreatorCandidateService {

    List<AlertCreatorCandidate> alertCandiates;

    boolean canCreateAlert(String anomalyType, EvidenceType evidenceType, Map<String,String> evidence){
        for (AlertCreatorCandidate alertFilter: alertCandiates){
            if (alertFilter.filterMatch(anomalyType, evidenceType)){
                boolean isCandidate = alertFilter.canCreateAlert(evidence);
                if (!isCandidate){
                    //This indicator failed, and need to be dumped
                    return  false;
                }
            }
        }
        return  true; //If all alertCandidates doesn't filter the candidate, it can create an alert
    }

}
