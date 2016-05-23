package fortscale.streaming.alert.subscribers.evidence.decider;

import fortscale.domain.core.AlertTimeframe;
import fortscale.streaming.alert.event.wrappers.EnrichedFortscaleEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by rans on 14/03/16.
 * Command Design Pattern with chaining. Each command decides whether to return or to chain to next command
 */
public interface DeciderCommand {

    /**
     * This method get list of EnrichedFortscaleEvents and return subset of that list,
     * of all the EnrichedFortscaleEvents who match to internal criterion.
     *
     * @param enrichedFortscaleEvents
     * @return
     */
    List<EnrichedFortscaleEvent> decide(List<EnrichedFortscaleEvent> enrichedFortscaleEvents, AlertTimeframe alertTimeframe);
}
