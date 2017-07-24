package fortscale.entity.event;

import fortscale.accumulator.entityEvent.store.AccumulatedEntityEventStore;
import fortscale.utils.time.TimestampUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class EntityEventDataReaderService {

	private EntityEventDataMongoStore entityEventDataMongoStore;
	private AccumulatedEntityEventStore accumulatedEntityEventStore;

	public EntityEventDataReaderService(EntityEventDataMongoStore entityEventDataMongoStore, AccumulatedEntityEventStore accumulatedEntityEventStore) {
		this.entityEventDataMongoStore = entityEventDataMongoStore;
		this.accumulatedEntityEventStore = accumulatedEntityEventStore;
	}

	public Set<String> findDistinctContextsByTimeRange(
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

	public Set<String> findDistinctAcmContextsByTimeRange(EntityEventConf entityEventConf, Date startTime, Date endTime) {

		return accumulatedEntityEventStore.findDistinctContextsByTimeRange(
				entityEventConf, startTime, endTime);
	}
}
