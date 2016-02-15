package fortscale.entity.event;

import org.springframework.data.domain.PageRequest;

import java.util.Date;
import java.util.List;

public interface EntityEventDataStore {
	EntityEventData getEntityEventData(String entityEventName, String contextId, long startTime, long endTime);
	List<EntityEventData> getEntityEventDataWithModifiedAtEpochtimeLte(String entityEventName, long modifiedAtEpochtime);
	List<EntityEventMetaData> getEntityEventDataThatWereNotTransmittedOnlyIncludeIdentifyingData(String entityEventName, PageRequest pageRequest);
	List<EntityEventData> getEntityEventDataWithEndTimeInRange(String entityEventName, Date fromTime, Date toTime);
	void storeEntityEventData(EntityEventData entityEventData);
	void storeEntityEventDataList(List<EntityEventData> entityEventDataList);
}
