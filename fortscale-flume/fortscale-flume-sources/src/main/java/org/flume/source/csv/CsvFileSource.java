package org.flume.source.csv;


import fortscale.common.general.Schema;
import fortscale.domain.core.AbstractDocument;
import fortscale.utils.logging.Logger;
import org.apache.flume.Context;
import org.flume.source.csv.domain.GenericRawEvent;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CsvFileSource extends CsvFormatSource {


    private static final Logger logger = Logger.getLogger(CsvFileSource.class);

    protected static final String FOLDER_PATH_CONF_NAME = "filePath";
    protected static final String FILE_TIMESTAMP_FORMAT = "fileTimestampFormat";
    protected static final String FILE_PREFIX = "filePrefix";
    protected static final String FILE_DATE_SEPARATOR = "fileDateSeparator";
    protected static final String FILE_SUFFIX = "fileSuffix";



    protected static final String DEFAULT_FILE_TIMESTAMP_FORMAT_VALUE = "ISO";

    protected String folderPath;
    protected Path filePath;
    protected String filePrefix;
    protected String fileDateSeparator;
    protected String fileSuffix;
    protected String timestampFormat;
    protected String fileTimestampFormat;

    @SuppressWarnings("unchecked")
    public void doPresidioConfigure(Context context) {
        try {

            super.doPresidioConfigure(context);
            logger.debug("context is: {}", context);
            setName("presidio-flume-csv-source");
            filePrefix = context.getString(FILE_PREFIX, "");
            fileDateSeparator = context.getString(FILE_DATE_SEPARATOR, "");
            fileSuffix = context.getString(FILE_SUFFIX, "");
            folderPath = context.getString(FOLDER_PATH_CONF_NAME,"");
            fileTimestampFormat = context.getString(FILE_TIMESTAMP_FORMAT, DEFAULT_FILE_TIMESTAMP_FORMAT_VALUE);
            timestampFormat = context.getString(TIMESTAMP_FORMAT, "ISO");
            filePath = Paths.get(getNextFilePath());

        } catch (Exception e) {
            logger.error("Error configuring CsvFileSource!", e);
        }

    }


    /**
     * The method that populated the filePath - basically responsible to get the path of the next file
     * In this implementation the method will generate the file name based on the current startDate and endDate
     *
     */
    @SuppressWarnings("unchecked")
    protected String getNextFilePath() {
        switch (fileTimestampFormat) {
            case "ISO":
                return  folderPath + filePrefix + startDate + fileDateSeparator + endDate + fileSuffix;

            case "EPOCHSECONDS":
                return folderPath + filePrefix + startDate.getEpochSecond() + fileDateSeparator + endDate.getEpochSecond() + fileSuffix;

            case "EPOCHMILLI":
                return folderPath + filePrefix + (1000L * startDate.getEpochSecond()) + fileDateSeparator + (1000L * endDate.getEpochSecond()) + fileSuffix;

            default: //some format
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern((timestampFormat));
                String startDateInFormat = dtf.format(startDate);
                String dateTimeInFormat = dtf.format(endDate);
                return folderPath + filePrefix + startDateInFormat + fileDateSeparator + dateTimeInFormat + fileSuffix;

        }
    }

    @Override
    protected List<AbstractDocument> doFetch(Schema schema,int i){
        List<GenericRawEvent> genericEvents;
        try (Reader reader = Files.newBufferedReader(filePath)) {
            genericEvents = getGenericRawEventsFromCsv(reader);
            return convertEvents(genericEvents);

        } catch (Exception e) {
            logger.error("CSV source {} Failed to fetch from csv file", this, e);
            return null;
        }



    }



}






