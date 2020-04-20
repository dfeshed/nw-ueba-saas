package time;

import org.junit.Assert;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static fortscale.utils.time.TimestampUtils.convertEpochInAnyUnitToSeconds;
import static fortscale.utils.time.TimestampUtils.epochToDateInLong;

/**
 * Created by gaashh on 5/29/16.
 */
public class EpochTimestampUtilsTest {


    @Test
    public void testEpochUnitConversion() {

        long epochSeconds =  LocalDateTime.of(2018,11,25,23,59,48,0).toEpochSecond(ZoneOffset.UTC);
        long value;

        // Check seconds
        value = convertEpochInAnyUnitToSeconds(epochSeconds);
        Assert.assertEquals(epochSeconds, value);

        // Check mSec
        value = convertEpochInAnyUnitToSeconds(epochSeconds * 1000L );
        Assert.assertEquals(epochSeconds, value);

        // Check uSec
        value = convertEpochInAnyUnitToSeconds(epochSeconds * 1000L * 1000);
        Assert.assertEquals(epochSeconds, value);

        // Check nSec
        value = convertEpochInAnyUnitToSeconds(epochSeconds * 1000L * 1000 * 1000);
        Assert.assertEquals(epochSeconds, value);


        // Check zero
        value = convertEpochInAnyUnitToSeconds(0);
        Assert.assertEquals(0, value);

        // Check negative
        value = convertEpochInAnyUnitToSeconds( -epochSeconds * 1000L);
        Assert.assertEquals( -epochSeconds * 1000L, value);

    }

    @Test
    public void testConvertToDateInLong() {


        // Check typical epoch
        long epochSeconds =  LocalDateTime.of(2018,11,25,23,59,48,0).toEpochSecond(ZoneOffset.UTC);
        long expected = 20181125235948L;

        long result = epochToDateInLong(epochSeconds);

        Assert.assertEquals(expected, result);

        // Check zero epoch
        result = epochToDateInLong(0);
        Assert.assertEquals(0L, result);
    }


}
