package fortscale.streaming.alert.subscribers.evidence.decider;

import fortscale.domain.core.AlertTimeframe;
import fortscale.streaming.alert.event.wrappers.EnrichedFortscaleEvent;

import java.util.List;

/**
 * Created by rans on 14/03/16.
 */

public interface AlertDeciderService {


    /**
     * Get list of evidences and return the name represent the alert
     * @param evidences
     * @return alert name or No Name Match to the alert if no one match
     */
    String decideName(List<EnrichedFortscaleEvent> evidences, AlertTimeframe alertTimeframe);

    /**
     * Get list of evidences and return the score of the alert or
     * Integer.MIN_VALUE if not evidence match
     * @param evidences
     * @return
     */
    int decideScore(List<EnrichedFortscaleEvent> evidences, AlertTimeframe alertTimeframe);


}
