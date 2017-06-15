package fortscale.utils.time;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

/**
 *
   A service to get the system's time (Epoch, Date, Instant)
 *
 * Normally, the service returns the system (true) time. However, the time can be forced for specific value (for testing)
 *
 * Created by gaashh on 7/10/16.
 */
public interface SystemDateService {


    /**
     * @return system epoch time in seconds (unless time is forced)
     */
    long getEpoch();

    /**
     * @return system epoch time in Date format
     */
    Date getDate();

    /**
     * @return system epoch time in Instant format
     */
    Instant getInstant();

    /**
     * @return system epoch time in milli seconds (unless time is forced)
     */
    long getEpochMilli();

    /**
     *
     * Force the service system time to the specified value in Instant. Set to null to revert to normal operation (un-force)
     *
     * @param forcedDate - see above
     */
    void forceInstant(Instant forcedDate);

    /**
     *
     * Force the service system time to the specified value (in seconds). Set to null to revert to normal operation (un-force)
     *
     * @param forcedEpoch - see above
     */
    void forceEpoch(Long forcedEpoch);

    /**
     *
     * Force the service system time to the specified value (in milli seconds). Set to null to revert to normal operation (un-force)
     *
     * @param forcedEpochMilli - see above
     */
    void forceEpochMilli(Long forcedEpochMilli);


    /**
     * Force the service system time to advance specific value (in milli seconds)
     *
     * Valid only if service date is already forced
     *
     * @param forcedEpochMilli
     * @return new forced time in milli seconds
     */
    Long forceAdvanceMilli(Long forcedEpochMilli);

    /**
     * Force the service system time to advance specific value (in Duration)
     *
     * Valid only if service date is already forced
     *
     * @param duration
     * @return new forced time as Instant
     */
    Instant forceDurationAdvance(Duration duration);

}
