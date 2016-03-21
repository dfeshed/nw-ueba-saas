package fortscale.streaming.alert.subscribers.evidence.applicable;

import fortscale.streaming.alert.event.wrappers.EnrichedFortscaleEvent;

import java.util.List;

/**
 * Created by shays on 16/03/2016.
 */
public interface EvidencesApplicableToAlertService {


        List<EnrichedFortscaleEvent> createIndicatorListApplicableForDecider(List<EnrichedFortscaleEvent> evidencesOrEntityEvents);


}
