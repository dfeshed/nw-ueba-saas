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

    @Value("${entity.event.data.cache.reader.service.max.cache.size:0}")
    private int maxCacheSize;

    // Default 3 days: 3*24*60*60 = 259200
    @Value("${entity.event.data.cache.reader.service.time.to.expire.seconds:259200}")
    private int timeToExpire;

    // map of contextId to JokerEntityEventDataContainer that contains list of JokerEntityEventData of the last timeRangeInSeconds
    private MemoryBasedCache<String, JokerEntityEventDataContainer> cache;


    public EntityEventDataCachedReaderService() {
        if(maxCacheSize>0) {
            cache = new MemoryBasedCache<String, JokerEntityEventDataContainer>(maxCacheSize, timeToExpire, JokerEntityEventDataContainer.class);
        }
    }

    public JokerEntityEventDataContainer getJokerEntityEventDataContainer(String contextId){
        return cache != null ? cache.get(contextId) : null;
    }

    public void putJokerEntityEventDataContainer (String contextId, JokerEntityEventDataContainer jokerEntityEventDataContainer){
        if(cache!=null) {
            cache.put(contextId, jokerEntityEventDataContainer);
        }
    }

    public List<JokerEntityEventData> findEntityEventsJokerDataByContextIdAndTimeRange(EntityEventConf entityEventConf, String contextId, Date startTime, Date endTime) {
        long startTimeSec = startTime.getTime()/1000;
        long endTimeSec = endTime.getTime()/1000;
        JokerEntityEventDataContainer jokerEntityEventDataContainer = getJokerEntityEventDataContainer(contextId);
        if(jokerEntityEventDataContainer == null) {
            List<JokerEntityEventData> entityEventDatas = entityEventDataReaderService.findEntityEventsJokerDataByContextIdAndTimeRange(entityEventConf, contextId, startTime, endTime);
            jokerEntityEventDataContainer = new JokerEntityEventDataContainer(entityEventDatas, startTimeSec, endTimeSec);
            putJokerEntityEventDataContainer(contextId, jokerEntityEventDataContainer);
        } else if(startTimeSec != jokerEntityEventDataContainer.getStartTime() ) {
            jokerEntityEventDataContainer.removeEntriesWithStartTimeLt(startTimeSec);
            jokerEntityEventDataContainer.add(entityEventDataReaderService.findEntityEventsJokerDataByContextIdAndTimeRange(
                    entityEventConf, contextId, jokerEntityEventDataContainer.getEndTime(), endTimeSec));
            jokerEntityEventDataContainer.setEndTime(endTimeSec);
            jokerEntityEventDataContainer.setStartTime(startTimeSec);
        }
        return jokerEntityEventDataContainer.getJokerEntityEventDatas();
    }

    class JokerEntityEventDataContainer {
        private long startTime;
        private long endTime;
        List<JokerEntityEventData> jokerEntityEventDatas;

        JokerEntityEventDataContainer(List<JokerEntityEventData> jokerEntityEventDatas, long startTime, long endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.jokerEntityEventDatas = jokerEntityEventDatas;
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

        void add(List<JokerEntityEventData> jokerEntityEventDatas) {
            jokerEntityEventDatas.forEach(entityEventData -> this.jokerEntityEventDatas.add(entityEventData));
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
