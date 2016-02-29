package fortscale.collection.services;

import java.util.Locale;
import java.util.TimeZone;

/**
 * @author gils
 *         29/02/2016
 */
public class TimeConversionParamsWrapper {
    private TimeZone inputTimezone;
    private Locale inputLocale;
    private TimeZone outputTimezone;
    private Locale outputLocale;
    private String outputFormat;

    public TimeConversionParamsWrapper(TimeZone inputTimezone, Locale inputLocale, TimeZone outputTimezone, Locale outputLocale, String outputFormat) {
        this.inputTimezone = inputTimezone;
        this.inputLocale = inputLocale;
        this.outputTimezone = outputTimezone;
        this.outputLocale = outputLocale;
        this.outputFormat = outputFormat;
    }

    public TimeZone getInputTimezone() {
        return inputTimezone;
    }

    public Locale getInputLocale() {
        return inputLocale;
    }

    public TimeZone getOutputTimezone() {
        return outputTimezone;
    }

    public Locale getOutputLocale() {
        return outputLocale;
    }

    public String getOutputFormat() {
        return outputFormat;
    }
}