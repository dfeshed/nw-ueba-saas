package fortscale.collection.jobs.event.process;

import fortscale.collection.io.BufferedLineReader;
import fortscale.collection.monitoring.ItemContext;
import fortscale.collection.morphlines.RecordExtensions;
import fortscale.collection.morphlines.commands.DlpMailEventsCache;
import fortscale.collection.morphlines.commands.EventsJoinerCache;
import fortscale.utils.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.kitesdk.morphline.api.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fortscale.collection.morphlines.commands.DlpMailEventsCache.EVENT_ID_FIELD_NAME;

public class DlpMailEventProcessJob extends EventProcessJob {

    private static final Logger logger = Logger.getLogger(DlpMailEventProcessJob.class);

    public static final String EVENT_TYPE_FIELD_NAME = "event_type";
    public static final String IS_EXTERNAL_FIELD_NAME = "is_external";
    public static final String NUM_OF_RECIPIENTS_FIELD_NAME = "num_of_recipients";
    public static final String EVENT_TYPE_RECIPIENT = "recipient";
    public static final String FORTSCALE_CONTROL_RECORD_EVENT_ID = "Fortscale Control";

    @Autowired
    private DlpMailEventsCache dlpMailEventsCache;

    private EventsJoinerCache eventsJoinerCache = EventsJoinerCache.getInstance("dlpmail_reducer", "1");




    /**
     * Iterate each line of the file and process each line.
     *
     * Pay attention - if override the method, make sure to set updateItemContext
     * @param file
     * @return
     * @throws IOException
     */
    protected boolean processFile(File file) throws IOException {


        long totalLines=0;

        BufferedLineReader reader = new BufferedLineReader();
        reader.open(file);
        ItemContext itemContext = new ItemContext(file.getName(),taskMonitoringHelper,morphlineMetrics);

        //read the line number only in case that the linesPrintEnabled flag is turned on
        if (linesPrintEnabled) {
            LineNumberReader lnr = new LineNumberReader(new FileReader(file));
            lnr.skip(Long.MAX_VALUE);
            totalLines = lnr.getLineNumber() + 1; //Add 1 because line index starts at 0
            lnr.close();
        }

        String line = null;
        try {
            int numOfLines = 0;
            int numOfSuccessfullyProcessedLines = 0;
            while ((line = reader.readLine()) != null) {
                if (StringUtils.isNotBlank(line)) {
                    numOfLines++;
                    //count that new event trying to processed from specific file
                    taskMonitoringHelper.handleNewEvent(file.getName());
                    jobMetrics.lines++;
                    List<Record> records = processLineWithAggregation(line, itemContext);
                    if (CollectionUtils.isEmpty(records)) {
//                        if (records == null && !eventsJoinerCache.)
                        logger.debug("Current line was cached. Moving to process the next line.");
                        continue;
                    }
                    for (Record record : records) {
                        if (record != null){
                            numOfSuccessfullyProcessedLines++;
                            //If success - write the event to monitoring. filed event monitoing handled by monitoring
                            Long timestamp = RecordExtensions.getLongValue(record, timestampField);
                            if (timestamp!=null){
                                jobMetrics.lastEventTime = timestamp;
                                taskMonitoringHelper.handleUnFilteredEvents(itemContext.getSourceName(),timestamp);
                            }
                            jobMetrics.linesSuccessfully++;
                        }
                        if (linesPrintEnabled && numOfLines % linesPrintSkip == 0) {
                            logger.info("{}/{} lines processed - {}% done", numOfLines, totalLines,
                                    Math.round(((float)numOfLines / (float)totalLines) * 100));
                            jobMetrics.linesTotalFailures++;
                        }
                    }
                }
            }

            logger.info("Successfully processed {} out of {} lines in file {}", numOfSuccessfullyProcessedLines, numOfLines, file.getName());

            // flush hadoop
            flushOutputAppender();
            if (numOfLines != numOfSuccessfullyProcessedLines){
                jobMetrics.processFilesSuccessfullyWithFailedLines++;
            }
        } catch (Exception e) {
            logger.error("error processing file {}. line: {}.", file.getName(), line, e);
            taskMonitoringHelper.error("Process Files", e.toString());
            return false;
        } finally {
            reader.close();
        }


        if (reader.HasErrors()) {
            logger.error("error processing file " + file.getName(), reader.getException());
            taskMonitoringHelper.error("Process Files", reader.getException().toString());
            return false;
        } else {
            if (reader.hasWarnings()) {
                logger.warn("error processing file " + file.getName(), reader.getException());
                taskMonitoringHelper.error("Process Files warning", reader.getException().toString());
            }

            return true;
        }

    }

    protected List<Record> processLineWithAggregation(String line, ItemContext itemContext) throws Exception {
        // process each line

        //I assume that this.itemContext updated once for each file.
        Record rec = morphline.process(line, itemContext);
        Record record;
        if(rec == null){
            jobMetrics.linesFailuresInMorphline++;
            return null;
        }
        if (morphlineEnrichment != null) {
            record = morphlineEnrichment.process(rec, itemContext);
            if (record == null) {
                jobMetrics.linesFailuresInMorphlineEnrichment++;
                return null;
            }
        } else {
            record = rec;
        }


        final boolean isNewEvent = dlpMailEventsCache.getCacheSize() == 2;
        if (isDummyRecord(record) || isNewEvent) {
            logger.debug("Got to {} event, updating previous records", isDummyRecord(record)? "dummy" : "new");
            return updatePreviousRecords(record);
        }
        else  {
            return Collections.emptyList(); //return nothing so the processing will continue to the next line
        }
    }








    private List<Record> updatePreviousRecords(Record record) throws Exception {
        final Map.Entry<String, List<Record>> previousRecordsEntry;
        final String newRecordEventId = (String) record.getFirstValue(EVENT_ID_FIELD_NAME);
        previousRecordsEntry = dlpMailEventsCache.popPreviousEventRecords(newRecordEventId);
        updateAggregatedEventFields(previousRecordsEntry);

        final List<Record> previousRecords = previousRecordsEntry.getValue();
        for (Record previousRecord : previousRecords) {
            //divide to two outputs:
            //1. longer one - including data_source and last_state
            //2. shorter one - without them - to hadoop

            String outputToHadoop = recordToHadoopString.process(previousRecord);


            // append to hadoop, if there is data to be written
            if (outputToHadoop!=null) {
                // append to hadoop
                Long timestamp = RecordExtensions.getLongValue(previousRecord, timestampField);
                appender.writeLine(outputToHadoop, timestamp);

                // output event to streaming platform
                streamMessage(recordKeyExtractor.process(previousRecord),recordToMessageString.toJSON(previousRecord));
            } else {
                jobMetrics.linesFailuresInTecordToHadoopString++;
                return null;
            }
        }
        return previousRecords;
    }

    private boolean isDummyRecord(Record record) {
        final String eventId = (String) record.getFirstValue(EVENT_ID_FIELD_NAME);
        return eventId.equals(FORTSCALE_CONTROL_RECORD_EVENT_ID);
    }

    private void updateAggregatedEventFields(Map.Entry<String, List<Record>> cachedRecordsEntry) {
        final String eventId = cachedRecordsEntry.getKey();
        final List<Record> cachedRecords = cachedRecordsEntry.getValue();

        logger.debug("Handling cached records for eventId {}", eventId);

        final List<Record> recipients = cachedRecords.stream().filter(currRecord -> currRecord.getFirstValue(EVENT_TYPE_FIELD_NAME).equals(EVENT_TYPE_RECIPIENT)).collect(Collectors.toList());
        updateNumOfRecipients(cachedRecords, recipients);
        updateIsExternal(cachedRecords, recipients);
    }

    private void updateNumOfRecipients(List<Record> records, List<Record> recipients) {
        int numOfRecipients = recipients.size();
        for (Record record : records) {
            record.replaceValues(NUM_OF_RECIPIENTS_FIELD_NAME, numOfRecipients);
        }
    }

    private void updateIsExternal(List<Record> records, List<Record> recipients) {
        boolean atLeastOneRecipientIsExternal = false;
        for (Record recipient : recipients) {
            atLeastOneRecipientIsExternal = Boolean.valueOf((String) recipient.getFirstValue(IS_EXTERNAL_FIELD_NAME));
            if (atLeastOneRecipientIsExternal) {
                break;
            }

        }
        for (Record record : records) {
            record.replaceValues(IS_EXTERNAL_FIELD_NAME, atLeastOneRecipientIsExternal);
        }

    }


}
