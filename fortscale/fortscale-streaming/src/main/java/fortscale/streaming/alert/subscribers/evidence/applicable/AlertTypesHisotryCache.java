package fortscale.streaming.alert.subscribers.evidence.applicable;

import fortscale.domain.core.Alert;
import fortscale.domain.core.dao.AlertsRepository;
import fortscale.services.cache.CacheHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by shays on 28/03/2016.
 * This cache service hold counts of alert by alert type (name) and start end time.
 * For example: 3 brute forces alerts between 2 & 3 / 4 smarts between 28/Mar/2016 to 29/Mar/2016 / etc...
 */
public class AlertTypesHisotryCache {

    @Autowired
    private AlertsRepository alertsRepository;

    //Cache based on time (there is not use to have hold data more then 24 hours because
    //alerts length is up to 24 hours
    private CacheHandler<AlertContextKey, AtomicLong> counsPerAlertsContextKey;

    /**
     * Get number of Occurances by alert name in time unit
     * @param type
     * @param alertWindowStartDate
     * @param alertWindowEndTime
     * @return
     */
    public long getOccurances(String type,Long alertWindowStartDate, Long alertWindowEndTime){

        AlertContextKey alertContextKey = new AlertContextKey(type, alertWindowStartDate.longValue(), alertWindowEndTime.longValue());
        AtomicLong numberOfOccurances = counsPerAlertsContextKey.get(alertContextKey);
        if (numberOfOccurances==null){
            numberOfOccurances = countFromDatabaseAndUpdateCache(alertContextKey);
        }
        return numberOfOccurances.get();

    }

    //Update the cache with new alert
    public void updateCache(Alert alert){
        AlertContextKey alertContextKey = new AlertContextKey(alert.getName(), alert.getStartDate(), alert.getEndDate());
        AtomicLong countOnAlertContextKey = counsPerAlertsContextKey.get(alertContextKey);
        if (countOnAlertContextKey == null){
            countOnAlertContextKey = countFromDatabaseAndUpdateCache(alertContextKey);
        }
        countOnAlertContextKey.incrementAndGet();
    }

    /**
     * Count match alerts from DB, and save it to cache
     * @param alertContextKey
     * @return
     */
    private AtomicLong countFromDatabaseAndUpdateCache(AlertContextKey alertContextKey){

        PageRequest pageRequest=null;

        long countFromDB = alertsRepository.buildQueryForAlertByTimeAndName(alertContextKey.getAlertName(), alertContextKey.getStartTime(), alertContextKey.getEndTime());
        AtomicLong countOnAlertContextKey = new AtomicLong(countFromDB);
        counsPerAlertsContextKey.put(alertContextKey,countOnAlertContextKey);
        return  countOnAlertContextKey;
    }

    public CacheHandler<AlertContextKey, AtomicLong> getCounsPerAlertsContextKey() {
        return counsPerAlertsContextKey;
    }

    public void setCounsPerAlertsContextKey(CacheHandler<AlertContextKey, AtomicLong> counsPerAlertsContextKey) {
        this.counsPerAlertsContextKey = counsPerAlertsContextKey;
    }



    //Inner key class
    private class AlertContextKey {

        private long startTime;
        private long endTime;
        private String alertName;

        public AlertContextKey(String alertName, long startTime, long endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.alertName = alertName;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }

        public String getAlertName() {
            return alertName;
        }

        public void setAlertName(String alertName) {
            this.alertName = alertName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AlertContextKey)) return false;

            AlertContextKey that = (AlertContextKey) o;

            if (startTime != that.startTime) return false;
            if (endTime != that.endTime) return false;
            return alertName.equals(that.alertName);

        }

        @Override
        public int hashCode() {
            int result = (int) (startTime ^ (startTime >>> 32));
            result = 31 * result + (int) (endTime ^ (endTime >>> 32));
            result = 31 * result + alertName.hashCode();
            return result;
        }
    }
}
