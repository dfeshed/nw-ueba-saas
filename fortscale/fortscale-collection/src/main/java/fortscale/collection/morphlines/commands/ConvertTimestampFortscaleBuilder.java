package fortscale.collection.morphlines.commands;

import com.typesafe.config.Config;
import fortscale.collection.monitoring.CollectionMessages;
import fortscale.collection.monitoring.MorphlineCommandMonitoringHelper;
import fortscale.collection.morphlines.metrics.MorphlineMetrics;
import fortscale.collection.services.time.FortscaleDateFormatService;
import fortscale.collection.services.time.FortscaleDateFormatterException;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.kitesdk.morphline.base.Fields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

/**
 * Command that converts the timestamps in a given field from one of a set of input date formats (in
 * an input timezone) to an output date format (in an output timezone), while respecting daylight
 * savings time rules. Provides reasonable defaults for common use cases.
 *
 * ******************************************************************************************************************************************************************************************
 * ******************************************************************************************************************************************************************************************
 * if not provided the default input/output time zone is UTC
 * The different between our internal implementation and the standard FormatDateTimestamp command is our command can get the timezone from a record value and not only as a constant input value
 * *******************************************************************************************************************************************************************************************
 * *******************************************************************************************************************************************************************************************
 */
@SuppressWarnings("unused")
@Configurable(preConstruction = true)
public final class ConvertTimestampFortscaleBuilder implements CommandBuilder {

  @Autowired
  private FortscaleDateFormatService fortscaleDateFormatService;

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
  private final class ConvertTimestampFortscale extends AbstractCommand {

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

	  //The specific Morphline metric
	  MorphlineMetrics morphlineMetrics = commandMonitoringHelper.getMorphlineMetrics(record);

      String tzInput = (String)record.getFirstValue(this.inputTimezoneField);

      String tzOutput = (String)record.getFirstValue(this.outputTimezoneField);

      ListIterator iter = record.get(fieldName).listIterator();
      while (iter.hasNext()) {
        String timestamp = (String) iter.next();

        String result;

        try {

          if (inputFormatsField != null && !inputFormatsField.isEmpty()) { // i.e. explicit input formats
            result = fortscaleDateFormatService.formatDateTimestamp(timestamp, inputFormatsField, tzInput, outputFormatField, tzOutput);
          } else {
            // date formatting will be based on pattern auto-detatcion
            result = fortscaleDateFormatService.formatDateTimestamp(timestamp, tzInput, outputFormatField, tzOutput);
          }

          if (result != null) {
            iter.set(result);
          }
          else {
			  if(morphlineMetrics != null )
				morphlineMetrics.unparseableTimeStamps++;
            LOG.debug("Could not parse timestamp '{}' ", timestamp);
            commandMonitoringHelper.addFilteredEventToMonitoring(record, CollectionMessages.CANNOT_PARSE_TIMESTAMP);

            return false;
          }
        } catch (FortscaleDateFormatterException e) {
			if(morphlineMetrics != null )
	      		morphlineMetrics.unparseableTimeStamps++;
          LOG.error("Exception while trying to parse timestamp '{}' :", timestamp, e);
          commandMonitoringHelper.addFilteredEventToMonitoring(record, CollectionMessages.CANNOT_PARSE_TIMESTAMP);

          return false;
        }
      }

      return super.doProcess(record);
    }
  }
}
