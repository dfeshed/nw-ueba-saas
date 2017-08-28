package org.flume.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalUnit;

public class DateUtils {

    private static Logger logger = LoggerFactory.getLogger(DateUtils.class);


    public static Instant getDateFromText(String text, String format) {
        try {
            new SimpleDateFormat(format).parse(text);
        } catch (ParseException e) {
            final String errorMessage = "Invalid date format: " + format;
            logger.error(errorMessage);
            throw new IllegalArgumentException("Could not parse date: " + errorMessage, e);
        }

        return Instant.parse(text);
    }


    /**
     * This method will round 'UP' the given {@code time} by the given {@code temporalUnit} using UTC timezone or in other words it's a 'ceiling' function.
     * i.e - if the given time is '2016-07-26T13:32:53Z' and the {@code temporalUnit} is 'HOUR', the return value will be an {@link Instant} of time '2016-07-26T14:00:00Z'
     *
     * @param time         the time
     * @param temporalUnit the time unit to round 'UP'
     * @return the given {@code time} rounded 'UP' by the given {@code temporalUnit} using the given {@code zoneOffset}
     */
    public static Instant ceiling(Instant time, TemporalUnit temporalUnit) {
        return ceiling(time, temporalUnit, ZoneOffset.UTC);
    }


    /**
     * This method will round 'UP' the given {@code time} by the given {@code temporalUnit} using the given {@code zoneOffset} or in other words it's a 'ceiling' function.
     * i.e - if the given time is '2016-07-26T13:32:53+02:00'(=2016-07-26T11:32:53Z), the given {@code zoneOffset} is '+02:00' and the {@code temporalUnit} is 'HOUR', the return value will be an {@link Instant} of time '2016-07-26T12:00:00Z'
     *
     * @param time         the time
     * @param temporalUnit the time unit to round 'UP'
     * @param zoneOffset   the timezone
     * @return the given {@code time} rounded 'UP' by the given {@code temporalUnit} using the given {@code zoneOffset}
     */
    public static Instant ceiling(Instant time, TemporalUnit temporalUnit, ZoneOffset zoneOffset) {
        /* Convert to LocalDateTime. Use time zone offset */
        LocalDateTime localDateTime = LocalDateTime.ofInstant(time, zoneOffset);

        /* Add 1 'temporalUnit' time and then use 'floor' function (java 8 date doesn't have a 'ceiling' function). */
        localDateTime = localDateTime.plus(1, temporalUnit).truncatedTo(temporalUnit);

        /* Convert back to instant, again, with time zone offset. */
        return localDateTime.atZone(zoneOffset).toInstant();
    }

    /**
     * This method will round 'DOWN' the given {@code time} by the given {@code temporalUnit} using UTC timezone or in other words it's a 'floor' function.
     * i.e - if the given time is '2016-07-26T13:32:53Z' and the {@code temporalUnit} is 'HOUR', the return value will be an {@link Instant} of time '2016-07-26T13:00:00Z'
     *
     * @param time         the time
     * @param temporalUnit the time unit to round 'DOWN'
     * @return the given {@code time} rounded 'DOWN' by the given {@code temporalUnit} using the given {@code zoneOffset}
     */
    public static Instant floor(Instant time, TemporalUnit temporalUnit) {
        return floor(time, temporalUnit, ZoneOffset.UTC);
    }

    /**
     * This method will round 'DOWN' the given {@code time} by the given {@code temporalUnit} using the given {@code zoneOffset} or in other words it's a 'floor' function.
     * i.e - if the given time is '2016-07-26T13:32:53+02:00'(=2016-07-26T11:32:53Z), the given {@code zoneOffset} is '+02:00' and the {@code temporalUnit} is 'HOUR', the return value will be an {@link Instant} of time '2016-07-26T11:00:00Z'
     *
     * @param time         the time
     * @param temporalUnit the time unit to round 'DOWN'
     * @param zoneOffset   the timezone
     * @return the given {@code time} rounded 'DOWN' by the given {@code temporalUnit} using the given {@code zoneOffset}
     */
    public static Instant floor(Instant time, TemporalUnit temporalUnit, ZoneOffset zoneOffset) {
        /* Convert to LocalDateTime. Use time zone offset */
        LocalDateTime localDateTime = LocalDateTime.ofInstant(time, zoneOffset);

        /*  use 'floor' function. */
        localDateTime = localDateTime.truncatedTo(temporalUnit);

        /* Convert back to instant, again, with time zone offset. */
        return localDateTime.atZone(zoneOffset).toInstant();
    }


}
