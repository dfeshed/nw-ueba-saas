package fortscale.entity.event;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

public class EntityEventDataReaderService {
	@Autowired
	private EntityEventDataMongoStore entityEventDataMongoStore;

	public List<String> findDistinctContextsByTimeRange(
			EntityEventConf entityEventConf, Date startTime, Date endTime) {

		return entityEventDataMongoStore.findDistinctContextsByTimeRange(
				entityEventConf, startTime, endTime);
	}

	public List<EntityEventData> findEntityEventsDataByContextIdAndTimeRange(
			EntityEventConf entityEventConf, String contextId, Date startTime, Date endTime) {

		return entityEventDataMongoStore.findEntityEventsDataByContextIdAndTimeRange(
				entityEventConf, contextId, startTime, endTime);
	}
}
