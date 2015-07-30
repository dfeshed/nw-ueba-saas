package fortscale.streaming.service.aggregation.entity.event;

import org.apache.commons.lang.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityEventDataTestStore implements EntityEventDataStore {
	private Map<String, EntityEventData> entityEventDataMap = new HashMap<>();

	@Override
	public EntityEventData getEntityEventData(String entityEventName, String contextId, long startTime, long endTime) {
		return entityEventDataMap.get(getEntityEventDataMapKey(entityEventName, contextId, startTime, endTime));
	}

	@Override
	public List<EntityEventData> getEntityEventDataWithFiringTimeLte(String entityEventName, long firingTimeInSeconds) {
		List<EntityEventData> listOfEntityEventData = new ArrayList<>();
		for (Map.Entry<String, EntityEventData> entry : entityEventDataMap.entrySet()) {
			String key = entry.getKey();
			EntityEventData value = entry.getValue();
			if (StringUtils.startsWith(key, entityEventName) && value.getFiringTimeInSeconds() <= firingTimeInSeconds) {
				listOfEntityEventData.add(value);
			}
		}
		return listOfEntityEventData;
	}

	@Override
	public List<EntityEventData> getEntityEventDataWithFiringTimeLteThatWereNotFired(String entityEventName, long firingTimeInSeconds) {
		List<EntityEventData> listOfEntityEventData = new ArrayList<>();
		for (EntityEventData entityEventData : getEntityEventDataWithFiringTimeLte(entityEventName, firingTimeInSeconds)) {
			if (!entityEventData.isFired()) {
				listOfEntityEventData.add(entityEventData);
			}
		}
		return listOfEntityEventData;
	}

	@Override
	public void storeEntityEventData(EntityEventData entityEventData) {
		if (entityEventData != null) {
			String key = getEntityEventDataMapKey(
					entityEventData.getEntityEventName(),
					entityEventData.getContextId(),
					entityEventData.getStartTime(),
					entityEventData.getEndTime());
			entityEventDataMap.put(key, entityEventData);
		}
	}

	private static String getEntityEventDataMapKey(String entityEventName, String contextId, long startTime, long endTime) {
		return String.format("%s.%s.%d.%d", entityEventName, contextId, startTime, endTime);
	}

	@Override
	public void emptyEntityEventDataStore() {
		entityEventDataMap.clear();
	}
}
