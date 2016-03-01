package fortscale.collection.morphlines.commands;

import com.typesafe.config.Config;
import fortscale.collection.monitoring.CollectionMessages;
import fortscale.collection.monitoring.MorphlineCommandMonitoringHelper;
import fortscale.collection.services.FortscaleTimeConverterService;
import fortscale.collection.services.TimeConversionParamsWrapper;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.kitesdk.morphline.base.Fields;

import java.util.*;

/**
 * Command that converts the timestamps in a given field from one of a set of input date formats (in
 * an input timezone) to an output date format (in an output timezone), while respecting daylight
 * savings time rules. Provides reasonable defaults for common use cases.
 *
 * ******************************************************************************************************************************************************************************************
 * ******************************************************************************************************************************************************************************************
 * if not provided the default input/output time zone is UTC
 * The different between our internal implementation and the standard convertTimestamp command is our command can get the timezone from a record value and not only as a constant input value
 * *******************************************************************************************************************************************************************************************
 * *******************************************************************************************************************************************************************************************
 */
public final class ConvertTimestampFortscaleBuilder implements CommandBuilder {

  @Override
  public Collection<String> getNames() {
    return Collections.singletonList("convertTimestampFortscale");
  }

  @Override
  public Command build(Config config, Command parent, Command child, MorphlineContext context) {
    return new ConvertTimestampFortscale(this, config, parent, child, context);
  }


  ///////////////////////////////////////////////////////////////////////////////
  // Nested classes:
  ///////////////////////////////////////////////////////////////////////////////
  private static final class ConvertTimestampFortscale extends AbstractCommand {

    private final String fieldName;
    private final String inputTimezoneField;
    private final String inputLocaleField;
    private final String outputTimezoneField;
    private final String outputLocaleField;
    private final String outputFormatField;


    private static final String DEFAULT_TIME_ZONE = "UTC";

    private TimeConversionParamsWrapper timeConversionParamsWrapper;

    MorphlineCommandMonitoringHelper commandMonitoringHelper = new MorphlineCommandMonitoringHelper();

    private static final String NATIVE_SOLR_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"; // e.g. 2007-04-26T08:05:04.789Z

//    static {
//      DateUtil.DEFAULT_DATE_FORMATS.add(0, NATIVE_SOLR_FORMAT);
//    }

    public ConvertTimestampFortscale(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
      super(builder, config, parent, child, context);

      this.fieldName = getConfigs().getString(config, "field", Fields.TIMESTAMP);
      this.inputTimezoneField = config.hasPath("inputTimezoneField") ?  getConfigs().getString(config, "inputTimezoneField") : DEFAULT_TIME_ZONE;
      this.inputLocaleField = getConfigs().getString(config, "inputLocale", "");
      this.outputTimezoneField= config.hasPath("outputTimezoneField") ? getConfigs().getString(config, "outputTimezoneField") : DEFAULT_TIME_ZONE;
      this.outputLocaleField=getConfigs().getString(config, "outputLocale", "");
      this.outputFormatField = getConfigs().getString(config, "outputFormat", NATIVE_SOLR_FORMAT);

      validateArguments();
    }

    private static Locale getLocale(String name) {
      if (name.equals(Locale.ROOT.toString())) {
        return Locale.ROOT;
      } else {
        for (Locale locale : Locale.getAvailableLocales()) {
          if (locale.toString().equals(name)) {
            return locale;
          }
        }
      }
      return null;
      //throw new MorphlineCompilationException("Unknown locale: " + name, getConfig());
    }


    @SuppressWarnings("unchecked")
    @Override
    protected boolean doProcess(Record record) {

      String tzInput = (String)record.getFirstValue(this.inputTimezoneField);
      TimeZone inputTimeZone = getTimeZone(tzInput == null ? DEFAULT_TIME_ZONE : tzInput);

      String tzOutput = (String)record.getFirstValue(this.outputTimezoneField);
      TimeZone outputTimeZone = getTimeZone(tzOutput == null ? DEFAULT_TIME_ZONE : tzOutput);

      this.timeConversionParamsWrapper = new TimeConversionParamsWrapper(inputTimeZone,
              getLocale(inputLocaleField), outputTimeZone, getLocale(outputLocaleField), outputFormatField);
//      String outputFormatStr = this.outputFormatField;

//      String inputTimeZoneStr = (String)record.getFirstValue(this.inputTimezoneField);
//      String outputTimeZoneStr = (String)record.getFirstValue(this.outputTimezoneField);

      ListIterator iter = record.get(fieldName).listIterator();
      while (iter.hasNext()) {
        String timestamp = (String) iter.next();
        String result = FortscaleTimeConverterService.convertTimestampToFortscaleFormat(timestamp, timeConversionParamsWrapper);

        if (result != null) {
          iter.set(result);
        }
        else {
          LOG.debug("Cannot parse timestamp '{}' ", timestamp);
          commandMonitoringHelper.addFilteredEventToMonitoring(record, CollectionMessages.CANNOT_PARSE_TIMESTAMP);
          return false;
        }
      }

      // pass record to next command in chain:
      return super.doProcess(record);
    }

    private static TimeZone getTimeZone(String timeZoneID) {
      TimeZone zone = TimeZone.getTimeZone(timeZoneID);
      // check if the zone is GMT and the timeZoneID is not GMT than it means that the
      // TimeZone.getTimeZone did not recieve a valid id
      if (!zone.getID().equalsIgnoreCase(timeZoneID)) {
        //throw new MorphlineCompilationException("Unknown timezone: " + timeZoneID, getConfig());
      } else {
        return zone;
      }

      return null;
    }
  }
}