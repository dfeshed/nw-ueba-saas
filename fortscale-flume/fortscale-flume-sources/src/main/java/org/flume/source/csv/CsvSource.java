package org.flume.source.csv;

import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import fortscale.domain.core.AbstractDocument;
import fortscale.utils.logging.Logger;
import org.apache.flume.Context;
import org.flume.source.AbstractPageablePresidioSource;
import org.flume.source.AbstractPresidioSource;
import org.flume.source.csv.domain.GenericAuthenticationRawEvent;
import org.flume.source.csv.domain.GenericRawEvent;
import presidio.sdk.api.domain.rawevents.AuthenticationRawEvent;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CsvSourceAgile extends AbstractPageablePresidioSource {


    private static final Logger logger = Logger.getLogger(CsvSourceAgile.class);

    protected static final String FILE_PATH_CONF_NAME = "filePath";
    protected static final String FIELD_MAPPING_CONF_NAME = "fieldMapping";
    protected static final String SKIP_LINES_CONF_NAME = "skipLines";
    protected static final String WITH_IGNORE_LEADING_WHITE_SPACE_CONF_NAME = "withIgnoreLeadingWhiteSpace";
    protected static final String TIMESTAMP_FORMAT = "timestampFormat";
    protected static final String AD_INFO_MAPPING_DELIMITER = "adInfoMappingDelimiter";
    protected static final String AD_INFO_MAPPING = "adInfoMapping";
    protected static final String FILE_TIMESTAMP_FORMAT = "fileTimestampFormat";
    protected static final String FILE_PREFIX = "filePrefix";
    protected static final String FILE_DATE_SEPARATOR = "fileDateSeparator";
    protected static final String FILE_SUFFIX = "fileSuffix";
    private static final String DELIMITER_CONF_NAME = "delimiter";
    private static final String RECORD_TYPE_CONF_NAME = "recordType";
    private static final String DEFAULT_DELIMITER_VALUE = ",";
    private static final String DEFAULT_TIMESTAMP_FORMAT_VALUE = "ISO";
    private static final String DEFAULT_AD_INFO_MAPPING_VALUE = "";
    private static final String DEFAULT_AD_INFO_MAPPING_DELIMITER_VALUE = ">";
    private static final String DEFAULT_FILE_TIMESTAMP_FORMAT_VALUE = "ISO";
    private static final boolean DEFAULT_WITH_IGNORE_LEADING_WHITE_SPACE_VALUE = true;

    private Path filePath;
    private int skipLines;
    private Boolean withIgnoreLeadingWhiteSpace;
    private String[] fieldMapping;
    private String timestampFormat;
    private String fileTimestampFormat;
    private char adInfoMappingDelimiter;
    private Map<String, String> adInfoMapping = new HashMap<>();
    private Class<GenericRawEvent> recordType;
    private char delimiter;
    private String filePrefix;
    private String fileDateSeparator;
    private String fileSuffix;

    @SuppressWarnings("unchecked")
    public void doPresidioConfigure(Context context) {
        try {
            logger.debug("context is: {}", context);
            setName("presidio-flume-csv-source");
            delimiter = context.getString(DELIMITER_CONF_NAME, DEFAULT_DELIMITER_VALUE).charAt(0);
            filePrefix = context.getString(FILE_PREFIX, "");
            fileDateSeparator = context.getString(FILE_DATE_SEPARATOR, "");
            fileSuffix = context.getString(FILE_SUFFIX, "");
            timestampFormat = context.getString(TIMESTAMP_FORMAT, DEFAULT_TIMESTAMP_FORMAT_VALUE);
            fileTimestampFormat = context.getString(FILE_TIMESTAMP_FORMAT, DEFAULT_FILE_TIMESTAMP_FORMAT_VALUE);
            fileTimestampFormat = context.getString(FILE_TIMESTAMP_FORMAT, DEFAULT_FILE_TIMESTAMP_FORMAT_VALUE);
            adInfoMappingDelimiter = context.getString(AD_INFO_MAPPING_DELIMITER, DEFAULT_AD_INFO_MAPPING_DELIMITER_VALUE).charAt(0);
            final String[] adInfoMappingAsStringArray = context.getString(AD_INFO_MAPPING, DEFAULT_AD_INFO_MAPPING_VALUE).split(String.valueOf(delimiter));
            for (String mapping : adInfoMappingAsStringArray) {
                final String[] split = mapping.split(String.valueOf(adInfoMappingDelimiter));
                adInfoMapping.put(split[0], split[1]);
            }
            getNextFile(context);

            timestampFormat = context.getString(TIMESTAMP_FORMAT, "ISO");

            recordType = (Class<GenericRawEvent>) Class.forName(context.getString(RECORD_TYPE_CONF_NAME));
            fieldMapping = context.getString(FIELD_MAPPING_CONF_NAME).split(String.valueOf(delimiter));
            skipLines = context.getInteger(SKIP_LINES_CONF_NAME, 0);
            withIgnoreLeadingWhiteSpace = context.getBoolean(WITH_IGNORE_LEADING_WHITE_SPACE_CONF_NAME, DEFAULT_WITH_IGNORE_LEADING_WHITE_SPACE_VALUE);
        } catch (Exception e) {
            logger.error("Error configuring CsvSource!", e);
        }

    }


    /**
     * The method that populated the filePath - basically responsible to get the path of the next file
     * In this implementation the method will generate the file name based on the current startDate and endDate
     * @param context
     */
    protected void getNextFile(Context context) {
        switch (fileTimestampFormat) {
            case "ISO":
                filePath = Paths.get(context.getString(FILE_PATH_CONF_NAME) + filePrefix + startDate + fileDateSeparator + endDate + fileSuffix);
                break;
            case "EPOCHSECONDS":
                filePath = Paths.get(context.getString(FILE_PATH_CONF_NAME) + filePrefix + startDate.getEpochSecond() + fileDateSeparator + endDate.getEpochSecond() + fileSuffix);
                break;
            case "EPOCHMILLI":
                filePath = Paths.get(context.getString(FILE_PATH_CONF_NAME) + filePrefix + (1000L * startDate.getEpochSecond()) + fileDateSeparator + (1000L * endDate.getEpochSecond()) + fileSuffix);
                break;
            default: //some format
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern((timestampFormat));
                String startDateInFormat = dtf.format(startDate);
                String dateTimeInFormat = dtf.format(endDate);
                filePath = Paths.get(context.getString(FILE_PATH_CONF_NAME) + filePrefix + startDateInFormat + fileDateSeparator + dateTimeInFormat + fileSuffix);
                break;
        }
    }

    @Override
    protected List<AbstractDocument> doFetch(int i){
        List<GenericRawEvent> genericEvents;
        try (Reader reader = Files.newBufferedReader(filePath)) {
            ColumnPositionMappingStrategy<GenericRawEvent> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(recordType);
            strategy.setColumnMapping(fieldMapping);

            CsvToBean<GenericRawEvent> csvToBean = new CsvToBeanBuilder<GenericRawEvent>(reader)
                    .withMappingStrategy(strategy)
                    .withSkipLines(skipLines)
                    .withIgnoreLeadingWhiteSpace(withIgnoreLeadingWhiteSpace)
                    .build();

            genericEvents = csvToBean.parse();
            return convertEvents(genericEvents);

        } catch (Exception e) {
            logger.error("CSV source {} Failed to fetch from csv file", this, e);
            return null;
        }



    }

    private List<AbstractDocument> convertEvents(List<GenericRawEvent> genericEvents) throws IllegalAccessException {
        List<AbstractDocument> events = new ArrayList<>();
        for (GenericRawEvent genericEvent : genericEvents) {
            if (recordType.equals(GenericAuthenticationRawEvent.class)) {
                final AuthenticationRawEvent event = new AuthenticationRawEvent();
                final GenericAuthenticationRawEvent genericAuthenticationRawEvent = (GenericAuthenticationRawEvent) genericEvent;
                event.setDataSource(genericAuthenticationRawEvent.getDataSource());
                event.setDstMachineDomain(genericAuthenticationRawEvent.getDstMachineDomain());
                event.setDstMachineId(genericAuthenticationRawEvent.getDstMachineId());
                event.setDstMachineName(genericAuthenticationRawEvent.getDstMachineName());
                event.setEventId(genericAuthenticationRawEvent.getEventId());
                event.setOperationType(genericAuthenticationRawEvent.getOperationType());
                event.setSrcMachineId(genericAuthenticationRawEvent.getSrcMachineId());
                event.setSrcMachineName(genericAuthenticationRawEvent.getSrcMachineName());
                event.setUserId(genericAuthenticationRawEvent.getUserId());

                final String dateTimeAsString = genericAuthenticationRawEvent.getDateTime();
                Instant dateTime;
                switch (timestampFormat) {
                    case "ISO":
                        dateTime = Instant.parse(dateTimeAsString);
                        break;
                    case "EPOCHSECONDS":
                        dateTime = Instant.ofEpochSecond(Long.parseLong(dateTimeAsString));
                        break;
                    case "EPOCHMILLI":
                        dateTime = Instant.ofEpochMilli(Long.parseLong(dateTimeAsString));
                        break;
                    default: //a format
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern((timestampFormat));
                        dateTime = LocalDateTime.parse(dateTimeAsString, dtf).toInstant(ZoneOffset.UTC);
                        break;
                }

                event.setDateTime(dateTime);
                if (!adInfoMapping.isEmpty()) {
                    final Field[] fields = genericEvent.getClass().getDeclaredFields();
                    for (Map.Entry<String, String> stringStringEntry : adInfoMapping.entrySet()) {
                        for (Field field : fields) {
                            final String fieldName = field.getName();
                            final String oldName = stringStringEntry.getKey();
                            if (oldName.equals(fieldName)) {
                                final String value = (String) field.get(genericEvent);
                                final String newName = stringStringEntry.getValue();
                                event.getAdditionalInfo().put(newName, value);
                            }
                        }
                    }
                }
                events.add(event);
            }
        }

        return events;
    }


}






