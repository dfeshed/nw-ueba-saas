package fortscale.entity.event;

import fortscale.utils.time.TimestampUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.PageRequest;

import java.util.*;

public class EntityEventDataTestStore implements EntityEventDataStore {
	private Map<String, EntityEventData> entityEventDataMap = new HashMap<>();

	@Override
	public EntityEventData getEntityEventData(String entityEventName, String contextId, long startTime, long endTime) {
		return entityEventDataMap.get(getEntityEventDataMapKey(entityEventName, contextId, startTime, endTime));
	}

	@Override
	public List<EntityEventData> getEntityEventDataWithModifiedAtEpochtimeLte(String entityEventName, long modifiedAtEpochtime) {
		List<EntityEventData> listOfEntityEventData = new ArrayList<>();
		for (Map.Entry<String, EntityEventData> entry : entityEventDataMap.entrySet()) {
			String key = entry.getKey();
			EntityEventData value = entry.getValue();
			if (StringUtils.startsWith(key, entityEventName) && value.getModifiedAtEpochtime() <= modifiedAtEpochtime) {
				listOfEntityEventData.add(value);
			}
		}

		Collections.sort(listOfEntityEventData, new EntityEventDataStartTimeComparator());
		return listOfEntityEventData;
	}

	@Override
	public List<EntityEventData> getEntityEventDataThatWereNotTransmittedOnlyIncludeIdentifyingData(String entityEventName, PageRequest pageRequest){
		List<EntityEventData> listOfEntityEventData = new ArrayList<>();
		for (Map.Entry<String, EntityEventData> entry : entityEventDataMap.entrySet()) {
			String key = entry.getKey();
			EntityEventData value = entry.getValue();
			if (StringUtils.startsWith(key, entityEventName) && !value.isTransmitted()) {
				listOfEntityEventData.add(value);
			}
		}

		Collections.sort(listOfEntityEventData, new EntityEventDataEndTimeComparator());
		if(pageRequest != null) {
			int fromIndex = pageRequest.getPageNumber() * pageRequest.getPageSize();
			int toIndex = Math.min(fromIndex + pageRequest.getPageSize(), listOfEntityEventData.size());
			listOfEntityEventData = listOfEntityEventData.subList(fromIndex, toIndex);
		}
		return listOfEntityEventData;
	}

	@Override
	public List<EntityEventData> getEntityEventDataWithEndTimeInRange(String entityEventName, Date fromTime, Date toTime) {
		long fromTimeSeconds = TimestampUtils.convertToSeconds(fromTime.getTime());
		long toTimeSeconds = TimestampUtils.convertToSeconds(toTime.getTime());

		List<EntityEventData> listOfEntityEventData = new ArrayList<>();
		for (Map.Entry<String, EntityEventData> entry : entityEventDataMap.entrySet()) {
			String key = entry.getKey();
			EntityEventData value = entry.getValue();
			if (StringUtils.startsWith(key, entityEventName) && fromTimeSeconds <= value.getEndTime() && value.getEndTime() <= toTimeSeconds) {
				listOfEntityEventData.add(value);
			}
		}

		Collections.sort(listOfEntityEventData, new EntityEventDataEndTimeComparator());
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

	private static final class EntityEventDataStartTimeComparator implements Comparator<EntityEventData> {
		@Override
		public int compare(EntityEventData entityEventData1, EntityEventData entityEventData2) {
			return Long.compare(entityEventData1.getStartTime(), entityEventData2.getStartTime());
		}
	}

	private static final class EntityEventDataEndTimeComparator implements Comparator<EntityEventData> {
		@Override
		public int compare(EntityEventData entityEventData1, EntityEventData entityEventData2) {
			return Long.compare(entityEventData1.getEndTime(), entityEventData2.getEndTime());
		}
	}

	public void emptyEntityEventDataStore() {
		entityEventDataMap.clear();
	}
}
