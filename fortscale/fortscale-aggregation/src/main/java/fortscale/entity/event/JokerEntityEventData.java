package fortscale.entity.event;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
		jokerAggrEventDatas = Stream
				.concat(
						entityEventData.getIncludedAggrFeatureEvents().stream(),
						entityEventData.getNotIncludedAggrFeatureEvents().stream()
				)
				.map(JokerAggrEventData::new)
				.collect(Collectors.toList());
    }

    public long getStartTime() {
        return startTime;
    }

    public List<JokerAggrEventData> getJokerAggrEventDatas() {
        return jokerAggrEventDatas;
    }
}
