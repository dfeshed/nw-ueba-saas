package fortscale.streaming.alert.subscribers.evidence.decider;

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
     *
     * @param pQueue array of EnrichedFortscaleEvent's, each holds event from Esper based on EnrichedFortscaleEvent, that are eligible for decider
     * @param deciderCommands List of <DeciderCommand> that can be chained for next decider iteration
     * @return decide and return anomalyTypeFieldName of the main evidence or entity event. The alert name dervied from this type (by configuration)
     */
  //  String getName(List<EnrichedFortscaleEvent> pQueue, List<DeciderCommand> deciderCommands);
    /**
     *
     * @param pQueue array of EnrichedFortscaleEvent's, each holds event from Esper based on EnrichedFortscaleEvent, that are eligible for decider
     * @param deciderCommands List of <DeciderCommand> that can be chained for next decider iteration
     * @return decide and return anomalyTypeFieldName of the main evidence or entity event. The alert score  dervied from this type (by configuration)
     */
    //Integer getScore(List<EnrichedFortscaleEvent> pQueue, List<DeciderCommand> deciderCommands);

    List<EnrichedFortscaleEvent> decide(List<EnrichedFortscaleEvent> enrichedFortscaleEvents);
}
