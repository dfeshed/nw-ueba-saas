package org.flume.source.csv;

import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import fortscale.common.general.Schema;
import fortscale.domain.core.AbstractDocument;
import fortscale.utils.logging.Logger;
import org.apache.flume.Context;
import org.flume.source.AbstractPageablePresidioSource;
import org.flume.source.csv.domain.GenericADActivityRawEvent;
import org.flume.source.csv.domain.GenericAuthenticationRawEvent;
import org.flume.source.csv.domain.GenericFileRawEvent;
import org.flume.source.csv.domain.GenericRawEvent;
import presidio.sdk.api.domain.rawevents.AuthenticationRawEvent;

import java.io.Reader;
import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by idanp on 30/01/2018.
 */
public abstract class CsvFormatSource extends AbstractPageablePresidioSource {

    private static final Logger logger = Logger.getLogger(CsvFormatSource.class);

    protected static final String FIELD_MAPPING_CONF_NAME = "fieldMapping";
    protected static final String LINES_TO_SKIP = "linesToSkip";
    protected static final String RECORD_TYPE_CONF_NAME = "recordType";
    protected static final String WITH_IGNORE_LEADING_WHITE_SPACE_CONF_NAME = "withIgnoreLeadingWhiteSpace";
    protected static final String AD_INFO_MAPPING_DELIMITER = "adInfoMappingDelimiter";
    protected static final String AD_INFO_MAPPING = "adInfoMapping";
    protected static final String DELIMITER_CONF_NAME = "delimiter";
    protected static final String DEFAULT_DELIMITER_VALUE = ",";
    protected static final String DEFAULT_AD_INFO_MAPPING_VALUE = "";
    protected static final String DEFAULT_AD_INFO_MAPPING_DELIMITER_VALUE = ":";
    protected static final boolean DEFAULT_WITH_IGNORE_LEADING_WHITE_SPACE_VALUE = true;
    protected static final String DEFAULT_TIMESTAMP_FORMAT_VALUE = "ISO";
    protected static final String TIMESTAMP_FORMAT = "timestampFormat";



    protected int skipLines;
    protected Boolean withIgnoreLeadingWhiteSpace;
    protected String[] fieldMapping;
    protected String timestampFormat;

    protected char adInfoMappingDelimiter;
    protected Map<String, String> adInfoMapping = new HashMap<>();
    protected Class<GenericRawEvent> recordType;
    protected char delimiter;

    @SuppressWarnings("unchecked")
    public void doPresidioConfigure(Context context) {

        try {
            logger.debug("context is: {}", context);
            timestampFormat = context.getString(TIMESTAMP_FORMAT, DEFAULT_TIMESTAMP_FORMAT_VALUE);
            delimiter = context.getString(DELIMITER_CONF_NAME, DEFAULT_DELIMITER_VALUE).charAt(0);
            adInfoMappingDelimiter = context.getString(AD_INFO_MAPPING_DELIMITER, DEFAULT_AD_INFO_MAPPING_DELIMITER_VALUE).charAt(0);
            final String[] adInfoMappingAsStringArray = context.getString(AD_INFO_MAPPING, DEFAULT_AD_INFO_MAPPING_VALUE).split(String.valueOf(delimiter));
            for (String mapping : adInfoMappingAsStringArray) {
                final String[] split = mapping.split(String.valueOf(adInfoMappingDelimiter));
                adInfoMapping.put(split[0], split[1]);
            }
            recordType = (Class<GenericRawEvent>) Class.forName(context.getString(RECORD_TYPE_CONF_NAME));
            fieldMapping = context.getString(FIELD_MAPPING_CONF_NAME).split(String.valueOf(delimiter));
            skipLines = context.getInteger(LINES_TO_SKIP, 0);
            withIgnoreLeadingWhiteSpace = context.getBoolean(WITH_IGNORE_LEADING_WHITE_SPACE_CONF_NAME, DEFAULT_WITH_IGNORE_LEADING_WHITE_SPACE_VALUE);
        }

        catch (Exception e)
        {
            logger.error("Error configuring CsvFileSource!", e);
        }


    }

    @SuppressWarnings("unchecked")
    protected abstract List<AbstractDocument> doFetch(Schema schema,int pageNum);

    protected abstract String getNextFilePath();

    protected List<GenericRawEvent> getGenericRawEventsFromCsv(Reader reader) {
        List<GenericRawEvent> genericEvents;ColumnPositionMappingStrategy<GenericRawEvent> strategy = new ColumnPositionMappingStrategy<>();
        strategy.setType(recordType);
        strategy.setColumnMapping(fieldMapping);

        CsvToBean<GenericRawEvent> csvToBean = new CsvToBeanBuilder<GenericRawEvent>(reader)
                .withMappingStrategy(strategy)
                .withSkipLines(skipLines)
                .withIgnoreLeadingWhiteSpace(withIgnoreLeadingWhiteSpace)
                .build();

        genericEvents = csvToBean.parse();
        return genericEvents;
    }

    protected List<AbstractDocument> convertEvents(List<GenericRawEvent> genericEvents) throws IllegalAccessException {
        List<AbstractDocument> events = new ArrayList<>();
        for (GenericRawEvent genericEvent : genericEvents) {

            //In case its a Authentication event
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

                //Replace teh additional info fileds the name based on the additional info mapping
                if (!adInfoMapping.isEmpty()) {
                    final Field[] fields = genericAuthenticationRawEvent.getClass().getDeclaredFields();
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

            //TODO - File schema
            else if (recordType.equals(GenericFileRawEvent.class))
            {

            }

            //TODO - AD  schema
            else if (recordType.equals(GenericADActivityRawEvent.class))
            {

            }
        }

        return events;
    }







}
