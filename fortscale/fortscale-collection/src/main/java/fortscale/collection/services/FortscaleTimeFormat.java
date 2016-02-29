package fortscale.collection.services;

import java.text.SimpleDateFormat;

/**
 * @author gils
 * 29/02/2016
 */
public enum FortscaleTimeFormat {
    UNIX_TIME_IN_MILLIS("unixTimeInMillis", new SimpleDateFormat("'unixTimeInMillis'")),
    UNIX_TIME_IN_SECONDS("unixTimeInSeconds", new SimpleDateFormat("'unixTimeInSeconds'")),
    SIMPLE_DATE_FORMAT("fortscaleDateFormat", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    private String formatName;
    private SimpleDateFormat format;

    FortscaleTimeFormat(String formatName, SimpleDateFormat format) {
        this.formatName = formatName;
        this.format = format;
    }

    public String getFormatName() {
        return formatName;
    }

    public SimpleDateFormat getFormat() {
        return format;
    }
}
