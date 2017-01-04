package fortscale.entity.event;

import fortscale.accumulator.entityEvent.store.AccumulatedEntityEventStore;
import fortscale.utils.time.TimestampUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EntityEventDataReaderService {
	@Autowired
	private EntityEventDataMongoStore entityEventDataMongoStore;
	@Autowired
	private AccumulatedEntityEventStore accumulatedEntityEventStore;

	public List<String> findDistinctContextsByTimeRange(
			EntityEventConf entityEventConf, Date startTime, Date endTime) {

		return entityEventDataMongoStore.findDistinctContextsByTimeRange(
				entityEventConf, startTime, endTime);
	}

	public List<JokerEntityEventData> findEntityEventsJokerDataByContextIdAndTimeRange(
			EntityEventConf entityEventConf, String contextId, Date startTime, Date endTime) {
		long startTimeSeconds = TimestampUtils.convertToSeconds(startTime.getTime());
		long endTimeSeconds = TimestampUtils.convertToSeconds(endTime.getTime());
		return findEntityEventsJokerDataByContextIdAndTimeRange(entityEventConf, contextId, startTimeSeconds, endTimeSeconds);
	}

	public List<JokerEntityEventData> findEntityEventsJokerDataByContextIdAndTimeRange(
			EntityEventConf entityEventConf, String contextId, long startTime, long endTime) {
		List<EntityEventData> entityEventDatas = entityEventDataMongoStore.findEntityEventsJokerDataByContextIdAndTimeRange(
				entityEventConf, contextId, startTime, endTime);

		List<JokerEntityEventData> jokerEntityEventDatas= new ArrayList<>(entityEventDatas.size());
		entityEventDatas.forEach(entityEventData -> jokerEntityEventDatas.add(new JokerEntityEventData(entityEventData)));

		return jokerEntityEventDatas;
	}

	public List<String> findDistinctAcmContextsByTimeRange(EntityEventConf entityEventConf, Date startTime, Date endTime) {

		return accumulatedEntityEventStore.findDistinctContextsByTimeRange(
				entityEventConf, startTime, endTime);
	}
}
