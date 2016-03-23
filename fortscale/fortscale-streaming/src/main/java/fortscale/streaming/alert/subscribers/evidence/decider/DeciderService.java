package fortscale.streaming.alert.subscribers.evidence.decider;

import fortscale.streaming.alert.event.wrappers.EnrichedFortscaleEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by rans on 14/03/16.
 */

public interface  DeciderService {


    /**
     * Get list of evidences and return the name represent the alert
     * @param evidences
     * @return alert name or No Name Match to the alert if no one match
     */
    String decideName(List<EnrichedFortscaleEvent> evidences);

    /**
     * Get list of evidences and return the score of the alert or
     * Integer.MIN_VALUE if not evidence match
     * @param evidences
     * @return
     */
    int decideScore(List<EnrichedFortscaleEvent> evidences);


}
