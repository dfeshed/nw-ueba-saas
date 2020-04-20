package fortscale.utils.time.impl;

import fortscale.utils.time.SystemDateService;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

/**
 *
 * The standard implementation for SystemDateService
 *
 * Created by gaashh on 7/10/16.
 */
public class SystemDateServiceImpl implements SystemDateService {


    // Forced epoch value in milli. Null indicated normal operation (not-forced)
    private Long forcedEpochMilli;


    @Override
    public long getEpochMilli() {

        // Epoch is forced
        if (forcedEpochMilli != null) {
            return forcedEpochMilli;
        }

        // Normal operation, get system epoch in Milli
        long epochMilli = System.currentTimeMillis();

        return epochMilli;

    }

    @Override
    public long getEpoch() {

        // Get epoch in mSec and convert it to seconds
        long epoch = getEpochMilli() / 1000;

        return epoch;
    }

    @Override
    public Date getDate() {
        Date date;
        // Epoch is forced
        if (forcedEpochMilli != null) {
            date= new Date(forcedEpochMilli);
        }
        else
        {
            date = new Date();
        }
        return date;
    }

    @Override
    public Instant getInstant() {
        Instant instant;

        // Epoch is forced
        if(forcedEpochMilli!=null)
        {
            instant = Instant.ofEpochMilli(forcedEpochMilli);
        }
        else
        {
            instant = Instant.now();
        }
        return instant;
    }


    @Override
    public void forceEpoch(Long forcedEpoch) {

        // Check setting to null
        if (forcedEpoch == null) {
            this.forcedEpochMilli = null;
            return;
        }

        // Convert epoch to millis and set it
        forceEpochMilli( forcedEpoch * 1000) ;
    }

    @Override
    public void forceInstant(Instant forcedDate) {

        // Unforce?
        if (forcedDate == null) {
            forceEpochMilli(null);
            return;
        }

        // Force
        forceEpochMilli(forcedDate.toEpochMilli());

    }

    @Override
    public void forceEpochMilli(Long forcedEpochMilli) {

        this.forcedEpochMilli  = forcedEpochMilli;

    }

    @Override
    public Long forceAdvanceMilli(Long forcedEpochMilli) {
        // Will throw if no forced
        this.forcedEpochMilli += forcedEpochMilli;
        return this.forcedEpochMilli;
    }

    @Override
    public Instant forceDurationAdvance(Duration duration) {

        forceAdvanceMilli(duration.toMillis());
        return getInstant();
    }
}
