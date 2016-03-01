package fortscale.collection.morphlines.commands;

import com.typesafe.config.Config;
import fortscale.collection.monitoring.CollectionMessages;
import fortscale.collection.monitoring.MorphlineCommandMonitoringHelper;
import fortscale.collection.services.FortscaleTimeConverterService;
import org.kitesdk.morphline.api.*;
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
@SuppressWarnings("unused")
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
    private final List<String> inputFormatsField;

    private static final String DEFAULT_TIME_ZONE = "UTC";

    MorphlineCommandMonitoringHelper commandMonitoringHelper = new MorphlineCommandMonitoringHelper();

    private static final String DEFAULT_OUTPUT_FORTSCALE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    public ConvertTimestampFortscale(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
      super(builder, config, parent, child, context);

      this.fieldName = getConfigs().getString(config, "field", Fields.TIMESTAMP);
      this.inputTimezoneField = config.hasPath("inputTimezoneField") ?  getConfigs().getString(config, "inputTimezoneField") : DEFAULT_TIME_ZONE;
      this.inputLocaleField = getConfigs().getString(config, "inputLocale", "");
      this.outputTimezoneField= config.hasPath("outputTimezoneField") ? getConfigs().getString(config, "outputTimezoneField") : DEFAULT_TIME_ZONE;
      this.outputLocaleField=getConfigs().getString(config, "outputLocale", "");
      this.outputFormatField = getConfigs().getString(config, "outputFormat", DEFAULT_OUTPUT_FORTSCALE_FORMAT);
      this.inputFormatsField = getConfigs().getStringList(config, "inputFormats", Collections.EMPTY_LIST);

      validateArguments();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected boolean doProcess(Record record) {

      String tzInput = (String)record.getFirstValue(this.inputTimezoneField);
      TimeZone inputTimeZone = getTimeZone(tzInput == null ? DEFAULT_TIME_ZONE : tzInput);

      String tzOutput = (String)record.getFirstValue(this.outputTimezoneField);
      TimeZone outputTimeZone = getTimeZone(tzOutput == null ? DEFAULT_TIME_ZONE : tzOutput);

      ListIterator iter = record.get(fieldName).listIterator();
      while (iter.hasNext()) {
        String timestamp = (String) iter.next();

        String result;

        if (inputFormatsField != null && !inputFormatsField.isEmpty()) {
          result = FortscaleTimeConverterService.convertTimestampToFortscaleFormat(timestamp, inputFormatsField, inputTimeZone, outputFormatField, outputTimeZone);
        }
        else {
          result = FortscaleTimeConverterService.convertTimestampToFortscaleFormat(timestamp, inputTimeZone, outputFormatField, outputTimeZone);
        }

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

    private TimeZone getTimeZone(String timeZoneID) {
      TimeZone zone = TimeZone.getTimeZone(timeZoneID);
      // check if the zone is GMT and the timeZoneID is not GMT than it means that the
      // TimeZone.getTimeZone did not recieve a valid id
      if (!zone.getID().equalsIgnoreCase(timeZoneID)) {
        throw new MorphlineCompilationException("Unknown timezone: " + timeZoneID, getConfig());
      } else {
        return zone;
      }
    }
  }
}