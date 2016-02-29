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
    Locale inputLocale = null;
    Locale outputLocale = null;
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

      this.timeConversionParamsWrapper = new TimeConversionParamsWrapper(getTimeZone(inputTimezoneField),
              getLocale(inputLocaleField), getTimeZone(outputTimezoneField), getLocale(outputLocaleField), outputFormatField);

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
    ///////////////////////////////////////////////////////////////////////////////
    // Nested classes:
    ///////////////////////////////////////////////////////////////////////////////
    /*
     * Licensed to the Apache Software Foundation (ASF) under one or more
     * contributor license agreements.  See the NOTICE file distributed with
     * this work for additional information regarding copyright ownership.
     * The ASF licenses this file to You under the Apache License, Version 2.0
     * (the "License"); you may not use this file except in compliance with
     * the License.  You may obtain a copy of the License at
     *
     *     http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     */
    /**
     * This class has some code from HttpClient DateUtil and Solrj DateUtil.
     */
//    private static final class DateUtil {
//      //start HttpClient
//      /**
//       * Date format pattern used to parse HTTP date headers in RFC 1123 format.
//       */
//      public static final String PATTERN_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";
//
//      /**
//       * Date format pattern used to parse HTTP date headers in RFC 1036 format.
//       */
//      public static final String PATTERN_RFC1036 = "EEEE, dd-MMM-yy HH:mm:ss zzz";
//
//      /**
//       * Date format pattern used to parse HTTP date headers in ANSI C
//       * <code>asctime()</code> format.
//       */
//      public static final String PATTERN_ASCTIME = "EEE MMM d HH:mm:ss yyyy";
//      //These are included for back compat
//      private static final Collection<String> DEFAULT_HTTP_CLIENT_PATTERNS = Arrays.asList(
//              PATTERN_ASCTIME, PATTERN_RFC1036, PATTERN_RFC1123);
//
//      private static final Date DEFAULT_TWO_DIGIT_YEAR_START;
//
//      static {
//        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.ROOT);
//        calendar.set(2000, Calendar.JANUARY, 1, 0, 0);
//        DEFAULT_TWO_DIGIT_YEAR_START = calendar.getTime();
//      }
//
////      private static final TimeZone GMT = TimeZone.getTimeZone("GMT");
//
//      //end HttpClient
//
//      //---------------------------------------------------------------------------------------
//
//      /**
//       * A suite of default date formats that can be parsed, and thus transformed to the Solr specific format
//       */
//      public static final List<String> DEFAULT_DATE_FORMATS = new ArrayList<String>();
//
//      static {
//        DEFAULT_DATE_FORMATS.add("yyyy-MM-dd'T'HH:mm:ss'Z'");
//        DEFAULT_DATE_FORMATS.add("yyyy-MM-dd'T'HH:mm:ss");
//        DEFAULT_DATE_FORMATS.add("yyyy-MM-dd");
//        DEFAULT_DATE_FORMATS.add("yyyy-MM-dd hh:mm:ss");
//        DEFAULT_DATE_FORMATS.add("yyyy-MM-dd HH:mm:ss");
//        DEFAULT_DATE_FORMATS.add("EEE MMM d hh:mm:ss z yyyy");
//        DEFAULT_DATE_FORMATS.addAll(DateUtil.DEFAULT_HTTP_CLIENT_PATTERNS);
//      }
//
//    }
  }

}