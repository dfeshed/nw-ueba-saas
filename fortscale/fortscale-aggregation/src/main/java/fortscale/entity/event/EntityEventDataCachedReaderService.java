package fortscale.entity.event;

import fortscale.services.cache.MemoryBasedCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Configurable(preConstruction = true)
public class EntityEventDataCachedReaderService {

    private static final long DAY_IN_SECONDS = 60 * 60 * 24;

    @Autowired
    private EntityEventDataReaderService entityEventDataReaderService;

    @Value("{entity.event.data.cache.reader.service.max.cache.size:300000}")
    private int maxCacheSize;

    // Default 3 days: 3*24*60*60 = 259200
    @Value("{entity.event.data.cache.reader.service.time.to.expire.seconds:259200}")
    private int timeToExpire;

    // map of contextId to JokerEntityEventDataContainer that contains list of JokerEntityEventData of the last timeRangeInSeconds
    private MemoryBasedCache<String, JokerEntityEventDataContainer> cache;
    private final long timeRangeInSeconds;



    public EntityEventDataCachedReaderService(long timeRangeInSeconds) {
        this.timeRangeInSeconds = timeRangeInSeconds;
        cache = new MemoryBasedCache<String, JokerEntityEventDataContainer>(maxCacheSize, timeToExpire, JokerEntityEventDataContainer.class);
    }

    public List<JokerEntityEventData> findEntityEventsDataByContextIdAndTimeRange(EntityEventConf entityEventConf, String contextId, Date startTime, Date endTime) {
        long startTimeSec = startTime.getTime()/1000;
        long endTimeSec = endTime.getTime()/1000;
        JokerEntityEventDataContainer jokerEntityEventDataContainer = cache.get(contextId);
        if(jokerEntityEventDataContainer == null) {
            List<EntityEventData> entityEventDatas = entityEventDataReaderService.findEntityEventsDataByContextIdAndTimeRange(entityEventConf, contextId, startTime, endTime);
            jokerEntityEventDataContainer = new JokerEntityEventDataContainer(entityEventDatas, startTimeSec, endTimeSec);
            cache.put(contextId, jokerEntityEventDataContainer);
        } else if(startTimeSec != jokerEntityEventDataContainer.getStartTime() ) {
            jokerEntityEventDataContainer.removeEntriesWithStartTimeLt(startTimeSec);
            jokerEntityEventDataContainer.add(entityEventDataReaderService.findEntityEventsDataByContextIdAndTimeRange(
                    entityEventConf, contextId, jokerEntityEventDataContainer.getEndTime(), endTimeSec));
        }
        return jokerEntityEventDataContainer.getJokerEntityEventDatas();
    }

    class JokerEntityEventDataContainer {
        private long startTime;
        private long endTime;
        List<JokerEntityEventData> jokerEntityEventDatas;

        JokerEntityEventDataContainer(List<EntityEventData> entityEventDatas, long startTime, long endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
            jokerEntityEventDatas = new ArrayList<>(entityEventDatas.size());
            entityEventDatas.forEach(entityEventData -> jokerEntityEventDatas.add(new JokerEntityEventData(entityEventData)));
        }

        long getStartTime() {
            return startTime;
        }

        long getEndTime() {
            return endTime;
        }

        List<JokerEntityEventData> getJokerEntityEventDatas() {
            return jokerEntityEventDatas;
        }

        void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        void setEndTime(long endTime) {
            this.endTime = endTime;
        }

        void add(List<EntityEventData> entityEventDatas) {
            entityEventDatas.forEach(entityEventData -> jokerEntityEventDatas.add(new JokerEntityEventData(entityEventData)));
        }

        void removeEntriesWithStartTimeLt(long startTime) {
            List<JokerEntityEventData> entriesToRemove = new ArrayList<>();
            for(JokerEntityEventData jokerEntityEventData : jokerEntityEventDatas) {
                if(jokerEntityEventData.getStartTime() < startTime) {
                    entriesToRemove.add(jokerEntityEventData);
                }
            }
            jokerEntityEventDatas.removeAll(entriesToRemove);
        }

    }
}
