package org.flume.utils;

import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;


public class DateUtilsTest {


    @Test
    public void ceiling() throws Exception {
        final Instant ceiling = DateUtils.ceiling(Instant.parse("2017-10-05T13:00:00Z"), ChronoUnit.HOURS);
        Assert.assertEquals(Instant.parse("2017-10-05T14:00:00Z"), ceiling);
    }

    @Test
    public void floor() throws Exception {
        final Instant ceiling = DateUtils.floor(Instant.parse("2017-10-05T13:00:00Z"), ChronoUnit.HOURS);
        Assert.assertEquals(Instant.parse("2017-10-05T13:00:00Z"), ceiling);
    }

}