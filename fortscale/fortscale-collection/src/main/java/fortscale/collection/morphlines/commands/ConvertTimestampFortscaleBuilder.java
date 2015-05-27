package fortscale.collection.morphlines.commands;

import com.typesafe.config.Config;
import fortscale.utils.TimestampUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.kitesdk.morphline.api.*;
import org.kitesdk.morphline.base.AbstractCommand;
import org.kitesdk.morphline.base.Fields;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
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
    private final List<String> inputFormatsField;
    private final String outputTimezoneField;
    private final String outputLocaleField;
    private final String outputFormatField;
    

    private static final String DEFAULT_TIME_ZONE = "UTC";
    Locale inputLocale = null;
    Locale outputLocale = null;
    
    private static final String NATIVE_SOLR_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"; // e.g. 2007-04-26T08:05:04.789Z
    private static final SimpleDateFormat UNIX_TIME_IN_MILLIS = new SimpleDateFormat("'unixTimeInMillis'");
    private static final SimpleDateFormat UNIX_TIME_IN_SECONDS = new SimpleDateFormat("'unixTimeInSeconds'");
    
    static {
      DateUtil.DEFAULT_DATE_FORMATS.add(0, NATIVE_SOLR_FORMAT); 
    }    

    public ConvertTimestampFortscale(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
      super(builder, config, parent, child, context);
      
      this.fieldName = getConfigs().getString(config, "field", Fields.TIMESTAMP);
      this.inputTimezoneField = config.hasPath("inputTimezoneField") ?  getConfigs().getString(config, "inputTimezoneField") : DEFAULT_TIME_ZONE;
      this.inputLocaleField = getConfigs().getString(config, "inputLocale", "");
      this.inputFormatsField = getConfigs().getStringList(config, "inputFormats", DateUtil.DEFAULT_DATE_FORMATS);
      this.outputTimezoneField= config.hasPath("outputTimezoneField") ? getConfigs().getString(config, "outputTimezoneField") : DEFAULT_TIME_ZONE;
      this.outputLocaleField=getConfigs().getString(config, "outputLocale", "");  
      this.outputFormatField = getConfigs().getString(config, "outputFormat", NATIVE_SOLR_FORMAT);
      inputLocale = getLocale(this.inputLocaleField);
      outputLocale = getLocale(this.outputLocaleField);
     
      validateArguments();
    }
        
    @Override
    protected boolean doProcess(Record record) {
    	String tzInput = (String)record.getFirstValue(this.inputTimezoneField);
        TimeZone inputTimeZone = getTimeZone(tzInput == null ? DEFAULT_TIME_ZONE : tzInput);
        
        List<SimpleDateFormat> inputFormats = new ArrayList<SimpleDateFormat>();
        for (String inputFormat : this.inputFormatsField) {
          SimpleDateFormat dateFormat = getUnixTimeFormat(inputFormat, inputTimeZone);
          if (dateFormat == null) {
            dateFormat = new SimpleDateFormat(inputFormat, inputLocale);
            dateFormat.setTimeZone(inputTimeZone);
            dateFormat.set2DigitYearStart(DateUtil.DEFAULT_TWO_DIGIT_YEAR_START);
          }
          inputFormats.add(dateFormat);
        }
    	String tzOutput = (String)record.getFirstValue(this.outputTimezoneField);
        TimeZone outputTimeZone = getTimeZone(tzOutput == null ? DEFAULT_TIME_ZONE : tzOutput);
        
        String outputFormatStr = this.outputFormatField;
        SimpleDateFormat outputFormat = getUnixTimeFormat(outputFormatStr, outputTimeZone);
        if (outputFormat == null) {
        	outputFormat = new SimpleDateFormat(outputFormatStr, outputLocale);
        	outputFormat.setTimeZone(outputTimeZone);
        }

        
      ParsePosition pos = new ParsePosition(0);
      ListIterator iter = record.get(fieldName).listIterator();
      while (iter.hasNext()) {
        String timestamp = iter.next().toString();
        boolean foundMatchingFormat = false;
        for (SimpleDateFormat inputFormat : inputFormats) {
          DateTime date = null;
          boolean isUnixTime;
          if (inputFormat == UNIX_TIME_IN_MILLIS || inputFormat == UNIX_TIME_IN_SECONDS) {
            isUnixTime = true;
            date = parseUnixTime(timestamp, DateTimeZone.forTimeZone(inputTimeZone));
          } else {
            isUnixTime = false;
            pos.setIndex(0);
            Date parsed = inputFormat.parse(timestamp, pos);
            if (parsed!=null)
              date = new DateTime(parsed);
          }
          if (date != null) {
            // change the time zone to the output time zone
            date = date.withZone(DateTimeZone.forTimeZone(outputTimeZone));
            String result;
            if (outputFormat == UNIX_TIME_IN_MILLIS) {
              result = String.valueOf(date.getMillis());
            } else if (outputFormat == UNIX_TIME_IN_SECONDS) {
              result = String.valueOf(date.getMillis() / 1000);
            } else {
              result = outputFormat.format(date.toDate());
            }
            iter.set(result);
            foundMatchingFormat = true;
            break;
          }
        }
        if (!foundMatchingFormat) {
          LOG.debug("Cannot parse timestamp '{}' ", timestamp);
          return false;
        }
      }
      
      // pass record to next command in chain:
      return super.doProcess(record);
    }

    // work around the fact that SimpleDateFormat doesn't understand Unix time format
    private SimpleDateFormat getUnixTimeFormat(String format, TimeZone timeZone) {
      if (format.equals("unixTimeInMillis")) {
        if (!"UTC".equals(timeZone.getID())) {
          throw new MorphlineCompilationException("timeZone must be UTC for date format 'unixTimeInMillis'", getConfig());
        }
        return UNIX_TIME_IN_MILLIS;
      } else if (format.equals("unixTimeInSeconds")) {
        if (!"UTC".equals(timeZone.getID())) {
          throw new MorphlineCompilationException("timeZone must be UTC for date format 'unixTimeInSeconds'", getConfig());
        }
        return UNIX_TIME_IN_SECONDS;
      } else {
        return null;
      }
    }
    
    // work around the fact that SimpleDateFormat doesn't understand Unix time format
    private DateTime parseUnixTime(String timestamp, DateTimeZone tz) {
      try {
        return new DateTime(TimestampUtils.convertToMilliSeconds(Long.parseLong(timestamp)), tz);
      } catch (NumberFormatException e) {
        return null;
      }
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
    
    private Locale getLocale(String name) {
    	if (name.equals(Locale.ROOT.toString())) {
    		return Locale.ROOT;
        } else {
        	for (Locale locale : Locale.getAvailableLocales()) {
        		if (locale.toString().equals(name)) {
        			return locale;
        		}
        	}
        }
      throw new MorphlineCompilationException("Unknown locale: " + name, getConfig());
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
    private static final class DateUtil {
      //start HttpClient
      /**
       * Date format pattern used to parse HTTP date headers in RFC 1123 format.
       */
      public static final String PATTERN_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";

      /**
       * Date format pattern used to parse HTTP date headers in RFC 1036 format.
       */
      public static final String PATTERN_RFC1036 = "EEEE, dd-MMM-yy HH:mm:ss zzz";

      /**
       * Date format pattern used to parse HTTP date headers in ANSI C
       * <code>asctime()</code> format.
       */
      public static final String PATTERN_ASCTIME = "EEE MMM d HH:mm:ss yyyy";
      //These are included for back compat
      private static final Collection<String> DEFAULT_HTTP_CLIENT_PATTERNS = Arrays.asList(
              PATTERN_ASCTIME, PATTERN_RFC1036, PATTERN_RFC1123);

      private static final Date DEFAULT_TWO_DIGIT_YEAR_START;

      static {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.ROOT);
        calendar.set(2000, Calendar.JANUARY, 1, 0, 0);
        DEFAULT_TWO_DIGIT_YEAR_START = calendar.getTime();
      }

//      private static final TimeZone GMT = TimeZone.getTimeZone("GMT");

      //end HttpClient

      //---------------------------------------------------------------------------------------

      /**
       * A suite of default date formats that can be parsed, and thus transformed to the Solr specific format
       */
      public static final List<String> DEFAULT_DATE_FORMATS = new ArrayList<String>();

      static {
        DEFAULT_DATE_FORMATS.add("yyyy-MM-dd'T'HH:mm:ss'Z'");
        DEFAULT_DATE_FORMATS.add("yyyy-MM-dd'T'HH:mm:ss");
        DEFAULT_DATE_FORMATS.add("yyyy-MM-dd");
        DEFAULT_DATE_FORMATS.add("yyyy-MM-dd hh:mm:ss");
        DEFAULT_DATE_FORMATS.add("yyyy-MM-dd HH:mm:ss");
        DEFAULT_DATE_FORMATS.add("EEE MMM d hh:mm:ss z yyyy");
        DEFAULT_DATE_FORMATS.addAll(DateUtil.DEFAULT_HTTP_CLIENT_PATTERNS);
      }

    }
  }
  
}