package fortscale.services.ipresolving;

import fortscale.domain.events.IseEvent;
import fortscale.domain.events.dao.IseEventRepository;
import fortscale.services.cache.CacheHandler;
import fortscale.utils.time.TimestampUtils;
import org.apache.commons.lang3.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import java.util.List;

/**
 * Created by tomerd on 12/05/2015.
 */

public class IseResolver extends GeneralIpResolver<IseEvent> {

    private static Logger logger = LoggerFactory.getLogger(IseResolver.class);
    @Autowired
    private IseEventRepository iseEventRepository;

    @Value("${ise.resolver.leaseTimeInSec:1}")
    private int graceTimeInSec;

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
     */

    /**
     * Handle ISE events
     * @param event
     */
    public void addIseEvent(IseEvent event) {
        if (event == null) {
            return;
        }

        switch (event.getEventType()) {
        case ipAllocation:
            handleIpAllocationEvent(event);
            break;
        case ipRelease:
            handleIpReleaseEvent(event);
            break;
        case unknown:
            break;
		default:
			break;
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

        long upperTsLimit = (graceTimeInSec > 0) ? ts + TimestampUtils.convertToMilliSeconds(graceTimeInSec) : ts;

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

    /**
     * Handle ISE IP allocation event
     * Note: this implementation assumes all events are received in chronological order for each ip address, there
     * are some cases where we will not handle events as expected if they are received out of order
     * @param event
     */
    private void handleIpAllocationEvent(IseEvent event) {
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
                // We will update the cache and the repository in case that:
                // 1. IP was allocated
                // 2. We have record in repository but not in cache
                // 3. The ips of the new event and the event from the repository are the same
                // 4. The time of the new event is more recent
                // 5. The hostnames are different
                if (cached.getIpaddress().equals(event.getIpaddress()) &&
                        !cached.getHostname().equals(event.getHostname()) &&
                        cached.getTimestampepoch().compareTo(event.getTimestampepoch()) <= 0) {
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

    /**
     * Handle ISE IP release event
     * @param event
     */
    private  void handleIpReleaseEvent(IseEvent event) {

        // Get event from cache
        IseEvent cached = cache.get(event.getIpaddress());

        // If we have in cache event with different host name, ignore
        if (cached != null && !cached.getHostname().equals(event.getHostname()))
            return;

        // Cache hit
        if (cached != null) {
            if (cached.getHostname().compareTo(event.getHostname()) == 0) {
                cached.setExpiration(event.getTimestampepoch());
                cache.put((cached.getIpaddress()), cached);
            }
        }

        // Update the repository
        List<IseEvent> iseEvents = iseEventRepository.findByIpaddress(event.getIpaddress(),
                new PageRequest(0, 1, Direction.DESC, IseEvent.TIMESTAMP_EPOCH_FIELD_NAME));
        if (!iseEvents.isEmpty()) {
            cached = iseEvents.get(0);
            // Update if this event is newer than the event from cache \ repository
            if (event.getTimestampepoch().compareTo(cached.getTimestampepoch()) > 0 &&
                    cached.getHostname().compareTo(event.getHostname()) == 0) {
                cached.setExpiration(event.getTimestampepoch());
                iseEventRepository.save(cached);
            }
        }
    }

}
