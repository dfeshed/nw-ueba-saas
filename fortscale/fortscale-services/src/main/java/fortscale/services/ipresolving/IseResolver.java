package fortscale.services.ipresolving;

/**
 * Created by tomerd on 12/05/2015.
 */

import fortscale.domain.events.IseEvent;
import fortscale.domain.events.dao.IseEventRepository;
import fortscale.services.cache.CacheHandler;
import fortscale.utils.TimestampUtils;
import org.apache.commons.lang3.Range;
import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import java.util.List;

public class IseResolver extends GeneralIpResolver<IseEvent> {

    private static Logger logger = LoggerFactory.getLogger(IseResolver.class);
    @Autowired
    private IseEventRepository iseEventRepository;

    @Value("${ise.resolver.leaseTimeInMins:1}")
    private int graceTimeInMins;

    @Autowired
    @Qualifier("iseResolverCache")
    private CacheHandler<String, IseEvent> cache;


    public IseResolver(boolean shouldUseBlackList, CacheHandler<String, Range<Long>> ipBlackListCache) {
        super(shouldUseBlackList, ipBlackListCache, IseEvent.class);
    }

    public IseResolver() {
        super();
    }

    @Override
    public CacheHandler<String, IseEvent> getCache() {
        return cache;
    }

    public void setCache(CacheHandler<String, IseEvent> cache) {
        this.cache = cache;
    }


    /**
     * Handle the ise event and update repository when required.
     * Note: this implementation assumes all events are received in chronological order for each ip address, there
     * are some cases where we will not handle events as expected if they are received out of order
     */
    public void addIseEvent(IseEvent event) {
        // add assigned events to repository
        // see that we don't already have such an event in cache with the same
        // expiration time and hostname
        IseEvent cached = cache.get(event.getIpaddress());

        // If have already have in cache an event with same host name and a more recent lease time, return
        if (cached != null && cached.getHostname().equals(event.getHostname()) && cached.getTimestampepoch().compareTo(event.getTimestampepoch()) >= 0)
            return;

        // Cache miss
        if (cached == null) {
            List<IseEvent> iseEvents = iseEventRepository.findByIpaddress(event.getIpaddress(),
                    new PageRequest(0, 1, Direction.DESC, IseEvent.TIMESTAMP_EPOCH_FIELD_NAME));
            // New IP - add it to repository and cache
            if (iseEvents.isEmpty()) {
                cache.put(event.getIpaddress(), event);
                iseEventRepository.save(event);
            }
            // The IP was used before;
            // Update the cache
            // TODO: make sure that the first event is the latest date
            else {
                cached = iseEvents.get(0);
                if (!cached.getHostname().equals(event.getHostname()) && cached.getTimestampepoch().compareTo(event.getTimestampepoch()) <= 0) {
                    iseEventRepository.save(event);
                    cache.put((event.getIpaddress()), event);
                }
                else {
                    cache.put(cached.getIpaddress(), cached);
                }

            }
        }
        // Cache hit
        else {
            if (cached.getTimestampepoch().compareTo(event.getTimestampepoch()) < 0) {
                cache.put(event.getIpaddress(), event);
                removeFromBlackList(event);
                if (!cached.getHostname().equals(event.getHostname())) {
                    iseEventRepository.save(event);
                }
            }
        }
    }

    public IseEvent getLatestIseEventBeforeTimestamp(String ip, long ts) {
        if (iseEventRepository == null) {
            return null;
        }

        ts = TimestampUtils.convertToMilliSeconds(ts);

        // if
        // 1. Need to use the blacklist and
        // 2. The IP is in the blacklist
        // 3. The ts is included in the time range.
        //	Than the ip is not in the cache or MongoDB and we should skip it.
        Range<Long> timeRange = ipBlackListCache.get(ip);
        if (shouldUseBlackList && timeRange != null && timeRange.contains(ts)) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("IP %s is in the black list and the ts %s is between time range %s - %s. Skipping it.", ip, ts, timeRange.getMinimum(), timeRange.getMaximum()));
            }
            return null;
        }

        long upperTsLimit = (graceTimeInMins > 0) ? ts + graceTimeInMins * 60 * 1000 : ts;

        // see if we have a matching event in cache
        IseEvent iseEvent = cache.get(ip);
        if (iseEvent != null && iseEvent.getTimestampepoch() <= upperTsLimit && iseEvent.getExpiration() >= ts) {
            // return cached event
            return iseEvent;
        }

        // if the event was not in cache than look for it in the repository
        List<IseEvent> iseEvents = iseEventRepository.findByIpaddressAndTimestampepochLessThan(ip, upperTsLimit,
                new PageRequest(0, 1, Direction.DESC, IseEvent.TIMESTAMP_EPOCH_FIELD_NAME));
        if (!iseEvents.isEmpty()) {
            // check if the ip assignment is not expired
            iseEvent = iseEvents.get(0);
            // also add the event to the cache for next time
            cache.put(ip, iseEvent);
            return iseEvent;
        }

        //take the expiration date as the lower limit of the black list period only if the given event is previous to the given event ts
        long lowerLimitTs = iseEvent != null && (iseEvent.getExpiration() < ts) ? iseEvent.getExpiration() : 0;
        addToBlackList(ip, lowerLimitTs, upperTsLimit);
        return null;
    }

    public String getHostname(String ip, long ts) {
        IseEvent event = getLatestIseEventBeforeTimestamp(ip, ts);
        return (event != null) ? event.getHostname() : null;
    }

    protected List<IseEvent> getNextEvents(String ip, Long upperTsLimit) {
        return iseEventRepository.findByIpaddressAndTimestampepochGreaterThanEqual(ip, upperTsLimit, new PageRequest(0, 1, Sort.Direction.ASC, IseEvent.TIMESTAMP_EPOCH_FIELD_NAME));
    }

    @Override
    protected void removeFromBlackList(IseEvent event) {
        removeFromBlackList(event.getIpaddress(), event.getTimestampepoch(), event.getExpiration());
    }

}
