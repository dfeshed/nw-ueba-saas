package org.flume.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;

public class DateUtils {

    public static Instant getDateFromText(String text, String format) {
        try {
            new SimpleDateFormat(format).parse(text);
        } catch (ParseException e) {
            System.out.println("Invalid date format: " + format);
            throw new IllegalArgumentException("Could not parse date: " + e.getMessage());
        }

        return Instant.parse(text);
    }
}
