package fortscale.entity.event;

import fortscale.aggregation.feature.event.AggrEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JokerEntityEventData {
    private long startTime;
    private List<JokerAggrEventData> jokerAggrEventDatas;

    public JokerEntityEventData(long startTime, Map<String, Double> fullAggregatedFeatureEventNameToScore) {
        this.startTime = startTime;
        jokerAggrEventDatas = fullAggregatedFeatureEventNameToScore.entrySet().stream()
                .map(fullAggregatedFeatureEventNameAndScore -> new JokerAggrEventData(
                        fullAggregatedFeatureEventNameAndScore.getKey(),
                        fullAggregatedFeatureEventNameAndScore.getValue()
                ))
                .collect(Collectors.toList());
    }

    public JokerEntityEventData(EntityEventData entityEventData) {
        this.startTime = entityEventData.getStartTime();
        jokerAggrEventDatas = new ArrayList<>(entityEventData.getIncludedAggrFeatureEvents().size() +entityEventData.getNotIncludedAggrFeatureEvents().size());
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

    public List<JokerAggrEventData> getJokerAggrEventDatas() {
        return jokerAggrEventDatas;
    }
}
