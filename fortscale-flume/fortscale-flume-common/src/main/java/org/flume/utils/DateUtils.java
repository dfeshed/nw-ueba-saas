package org.flume.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;

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
}
