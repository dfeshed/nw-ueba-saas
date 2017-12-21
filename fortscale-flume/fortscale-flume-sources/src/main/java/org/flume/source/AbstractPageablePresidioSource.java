package org.flume.source;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fortscale.domain.core.AbstractDocument;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.flume.CommonStrings;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.FlumeException;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.lifecycle.LifecycleState;
import org.apache.flume.source.AbstractEventDrivenSource;
import org.flume.utils.ConnectorSharedPresidioExternalMonitoringService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;

import java.nio.charset.Charset;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.flume.CommonStrings.END_DATE;
import static org.apache.flume.CommonStrings.START_DATE;


/**
 * This class adds support for 2 things:
 * 1) for running flume as a batch process (init, run, stop) and not as a stream process (which is the default behaviour). A Presidio sink/interceptors must also be used when using a Presidio source.
 * 2) for using a metric service (that needs an application name).
 */
public abstract class AbstractPageablePresidioSource extends AbstractPresidioSource {

    public static final String NUMBER_OF_PROCESSED_PAGES = "number.of.processed.pages";
    public static final String NUMBER_OF_PROCESSED_EVENTS = "number_of_processed_events";

    private static Logger logger = LoggerFactory.getLogger(AbstractPageablePresidioSource.class);
    private int totalEvents = 0;
    private int totalPages = 0;

    /* This field indicates whether the agent is supposed to shut-down after the source is done (or in other words - is this a batch run?) */


    protected SourceFetcher sourceFetcher;
    protected Instant startDate;
    protected Instant endDate;
    protected String schema;

    protected ConnectorSharedPresidioExternalMonitoringService connectorSharedPresidioExternalMonitoringService;
    private static ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }




    @Override
    public void start() {
        //TODO: How to pass the application name to enum-singelton
        connectorSharedPresidioExternalMonitoringService = ConnectorSharedPresidioExternalMonitoringService.COLLECTOR_INSTANCE;
        super.start();
        try {


            if (isBatch) {
                sendDoneControlMessage();
                setLifecycleState(LifecycleState.DONE);
                logger.info("Source {} is done. Starting source-is-done flow", getName());
            }
        } catch (Exception e) {

            logger.error("Failed to start " + this, e);
            setLifecycleState(LifecycleState.ERROR);
        }
    }





    protected void sendDoneControlMessage() {
        final Event isDoneControlMessage = EventBuilder.withBody(new byte[0]);
        isDoneControlMessage.getHeaders().put(CommonStrings.IS_DONE, Boolean.TRUE.toString());
        logger.debug("Sending control message DONE");

//        connectorSharedPresidioExternalMonitoringService.reportCustomMetric(
//                NUMBER_OF_DONE_MESSAGES_SENT, 1, new HashMap<>(), MetricEnums.MetricUnitType.NUMBER, null);
        this.getChannelProcessor().processEvent(isDoneControlMessage);
    }

    @Override
    protected void doPresidioConfigure(Context context){
        schema = context.getString(CommonStrings.SCHEMA, null);
    }


    @Override
    protected void doStart() throws FlumeException {
        logger.debug("{} is processing events for {}: {}, {}: {}.",
                getName(), START_DATE, END_DATE, startDate, endDate);
        try {
            int pageNum = 0;// first page
            List<AbstractDocument> currentPage = doFetch(pageNum);
            if (currentPage.size() == 0) {
                logger.warn("Failed to process events for {}: {}, {]: {}. There were no events to process",
                        START_DATE, startDate, END_DATE, endDate);
            } else {
                processPage(currentPage); //handle first event
                pageNum++;
                while (currentPage.size() == batchSize) { //kind of (maybe)hasNext()
                    currentPage = doFetch(pageNum);
                    processPage(currentPage);
                    pageNum++;
                }
            }
            if (currentPage.isEmpty()) {
                logger.trace("{} has finished processing events for {}: {}, {}: {}.",
                        getName(), START_DATE, startDate, END_DATE, endDate);
            } else {
                logger.debug("{} has finished processing events for {}: {}, {}: {}.",
                        getName(), START_DATE, startDate, END_DATE, endDate);
            }
            startDate = endDate; // advance the cursor

            if (isBatch) {
                sendDoneControlMessage();
            }
            this.stop();

        } catch (Exception e) {
            logger.error("{} has failed to process events for {}: {}, {}: {}.",
                    getName(), START_DATE, startDate, END_DATE, endDate);
            logger.error(e.getMessage());
            this.stop();
        }
    }

    @Override
    protected void doStop() throws FlumeException {
        connectorSharedPresidioExternalMonitoringService.destroy();
    }

    protected abstract List<AbstractDocument> doFetch(int pageNum);


    private void processEvent(AbstractDocument event) throws JsonProcessingException {
        String failureReason = null;
        try {

            final String eventAsString;
            eventAsString = mapper.writeValueAsString(event);
            final Event flumeEvent = EventBuilder.withBody(eventAsString, Charset.defaultCharset());
            logger.trace("{} has finished processing event {}. Sending event to channel", getName(), flumeEvent);
            getChannelProcessor().processEvent(flumeEvent); // Store the Event into this Source's associated Channel(s)
        }
        catch(JsonProcessingException e){
            failureReason = "CANNOT_SERIALIZE_EVENT";
        }

        if (failureReason==null){
            reportSuccessMetric(NUMBER_OF_PROCESSED_EVENTS,MetricEnums.MetricValues.SUCCESS_EVENTS,1);
        } else {
            reportFailedMetric(NUMBER_OF_PROCESSED_EVENTS,failureReason,MetricEnums.MetricValues.FAILED_EVENTS,1);
        }

    }

    private void processPage(List<AbstractDocument> pageEvents) throws Exception {
        reportSuccessMetric(NUMBER_OF_PROCESSED_PAGES, MetricEnums.MetricValues.TOTAL_PAGES, 1);
        totalPages++;
        if (CollectionUtils.isNotEmpty(pageEvents)) {
            reportSuccessMetric(NUMBER_OF_PROCESSED_EVENTS, MetricEnums.MetricValues.TOTAL_EVENTS, pageEvents.size());
            totalEvents+=pageEvents.size();
        }
        reportSuccessMetric(NUMBER_OF_PROCESSED_PAGES, MetricEnums.MetricValues.AVG_PAGE_SIZE, 1);

        if (!validateEvents(pageEvents)) { //todo
            final String errorMessage = "event validation failed!";
            logger.error(errorMessage);
            reportFailedMetric(NUMBER_OF_PROCESSED_EVENTS,  "INVALID_EVENTS", MetricEnums.MetricValues.FAILED_EVENTS, pageEvents.size());
            reportFailedMetric(NUMBER_OF_PROCESSED_PAGES,  "INVALID_EVENTS", MetricEnums.MetricValues.FAILED_PAGES, 1 );
            throw new Exception(errorMessage);
        } else {
            for (AbstractDocument pageEvent : pageEvents) {
                processEvent(pageEvent);
            }
            reportSuccessMetric(NUMBER_OF_PROCESSED_PAGES, MetricEnums.MetricValues.SUCCESS_PAGES, 1 );
        }
    }


    private boolean validateEvents(List<AbstractDocument> events) {
        return events != null; //todo
    }


    private void reportSuccessMetric(String metricName, MetricEnums.MetricValues value, int amount) {
        reportMetric(metricName,false,null,value,amount);
    }


    private void reportFailedMetric(String metricName, String errorKey, MetricEnums.MetricValues value, int amount) {
        if (StringUtils.isBlank(errorKey)){
            throw new RuntimeException("Metric error tag cannot be empty");
        }
        reportMetric(metricName,true,errorKey,value,amount);
    }
    private void reportMetric(String metricName, boolean isFailure, String statusTag, MetricEnums.MetricValues value, int amount) {
        if(amount<=0 || value == null){
            return;
        }


        Map<MetricEnums.MetricValues, Number> values = new HashMap<>();
        values.put(value,amount);

        Map<MetricEnums.MetricTagKeysEnum, String> tags= new HashMap<>();

        //Set Schema tag if possible
        if (StringUtils.isNotBlank(schema)){
            tags.put(MetricEnums.MetricTagKeysEnum.SCHEMA,schema);
        }
        //Set Failure Tag if relevant
        if (isFailure){
            tags.put(MetricEnums.MetricTagKeysEnum.FAILURE_REASON,statusTag);
        }

        connectorSharedPresidioExternalMonitoringService.reportCustomMetricMultipleValues(metricName,values,tags, MetricEnums.MetricUnitType.NUMBER,startDate);
    }
}
