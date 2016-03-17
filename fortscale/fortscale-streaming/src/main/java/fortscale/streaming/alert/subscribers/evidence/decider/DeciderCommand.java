package fortscale.streaming.alert.subscribers.evidence.decider;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by rans on 14/03/16.
 * Command Design Pattern with chaining. Each command decides whether to return or to chain to next command
 */
public interface DeciderCommand {
    static final String ANOMALY_TYPE_FIELD_NAME = "anomalyTypeFieldName";
    static final String SCORE_FIELD_NAME = "score";
    static final String EVENT_TIME_FIELD_NAME = "eventTime";
    /**
     *
     * @param pQueue array of Map's, each holds event from Esper based on EnrichedFortscaleEvent, that are eligible for decider
     * @param deciderCommands List of <DeciderCommand> that can be chained for next decider iteration
     * @return
     */
    String getName(List<Map> pQueue, List<DeciderCommand> deciderCommands);
    /**
     *
     * @param pQueue array of Map's, each holds event from Esper based on EnrichedFortscaleEvent, that are eligible for decider
     * @param deciderCommands List of <DeciderCommand> that can be chained for next decider iteration
     * @return
     */
    Integer getScore(List<Map> pQueue, List<DeciderCommand> deciderCommands);
}
