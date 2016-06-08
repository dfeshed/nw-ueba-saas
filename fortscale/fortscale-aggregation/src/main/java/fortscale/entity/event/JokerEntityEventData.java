package fortscale.entity.event;

import fortscale.aggregation.feature.event.AggrEvent;

import java.util.HashSet;
import java.util.Set;

public class JokerEntityEventData {
    private long startTime;
    private Set<JokerAggrEventData> jokerAggrEventDatas;

    public JokerEntityEventData(EntityEventData entityEventData) {
        this.startTime = entityEventData.getStartTime();
        jokerAggrEventDatas = new HashSet<>(entityEventData.getIncludedAggrFeatureEvents().size());
        for (AggrEvent aggrEvent : entityEventData.getIncludedAggrFeatureEvents()) {
            jokerAggrEventDatas.add(new JokerAggrEventData(aggrEvent));
        }
        for (AggrEvent aggrEvent : entityEventData.getNotIncludedAggrFeatureEvents()) {
            jokerAggrEventDatas.add(new JokerAggrEventData(aggrEvent));
        }
    }

    public long getStartTime() {
        return startTime;
    }

    public Set<JokerAggrEventData> getJokerAggrEventDatas() {
        return jokerAggrEventDatas;
    }
}
