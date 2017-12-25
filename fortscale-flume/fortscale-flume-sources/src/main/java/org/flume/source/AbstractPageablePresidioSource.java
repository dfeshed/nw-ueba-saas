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
import org.apache.flume.conf.MonitorDetails;
import org.apache.flume.conf.MonitorableContext;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.lifecycle.LifecycleState;
import org.apache.flume.ConnectorSharedPresidioExternalMonitoringService;
import org.apache.flume.marker.MonitorInitiator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import presidio.monitoring.sdk.api.services.PresidioExternalMonitoringService;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;
import presidio.monitoring.sdk.impl.factory.PresidioExternalMonitoringServiceFactory;

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
public abstract class AbstractPageablePresidioSource extends AbstractPresidioSource implements MonitorInitiator {


    private static final String NUMBER_OF_PROCESSED_PAGES = "processed_pages";
    public static final String INVALID_EVENTS_ERROR_KEY = "INVALID_EVENTS";
    private static Logger logger = LoggerFactory.getLogger(AbstractPageablePresidioSource.class);
    private int totalEvents = 0;
    private int totalPages = 0;

    /* This field indicates whether the agent is supposed to shut-down after the source is done (or in other words - is this a batch run?) */


    protected SourceFetcher sourceFetcher;
    protected Instant startDate;
    protected Instant endDate;
    protected String schema;

    PresidioExternalMonitoringService presidioExternalMonitoringService;

    protected ConnectorSharedPresidioExternalMonitoringService connectorSharedPresidioExternalMonitoringService;
    private static ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    protected MonitorDetails monitorDetails;


    @Override
    public void start() {
        if (this.monitorDetails == null){
            throw new RuntimeException("Monitor should be already initiated in this phase");
        }
        super.start();

    }



    protected void sendDoneControlMessage() {
        final Event isDoneControlMessage = EventBuilder.withBody(new byte[0]);
        isDoneControlMessage.getHeaders().put(CommonStrings.IS_DONE, Boolean.TRUE.toString());
        logger.debug("Sending control message DONE");

        this.getChannelProcessor().processEvent(isDoneControlMessage);
    }

    @Override
    protected void doPresidioConfigure(Context context){
        schema = context.getString(CommonStrings.SCHEMA_NAME, null);
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

        } catch (Exception e) {
            logger.error("{} has failed to process events for {}: {}, {}: {}.",
                    getName(), START_DATE, startDate, END_DATE, endDate);
            logger.error(e.getMessage());

        }
        finally {
            this.stop();
        }
    }

    @Override
    public synchronized void stop() {

        logger.info("{} is stopping...", getName());
        try {

            if (isBatch) {
                doStop();
                setLifecycleState(LifecycleState.DONE);
                logger.info("Source {} is done. Starting source-is-done flow", getName());
            }
        } catch (Exception e) {

            logger.error("Failed to start " + this, e);
            setLifecycleState(LifecycleState.ERROR);
        }
    }

    @Override
    protected void doStop() throws FlumeException {
        presidioExternalMonitoringService.manualExportMetrics();
        sendDoneControlMessage();

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
            connectorSharedPresidioExternalMonitoringService.reportSuccessEventMetric(1);
        } else {

            connectorSharedPresidioExternalMonitoringService.reportFailedEventMetric(failureReason,1);
        }

    }

    private void processPage(List<AbstractDocument> pageEvents) throws Exception {
        connectorSharedPresidioExternalMonitoringService.reportSuccessAndTotalMetric(NUMBER_OF_PROCESSED_PAGES, MetricEnums.MetricValues.TOTAL_PAGES, 1);
        totalPages++;
        if (CollectionUtils.isNotEmpty(pageEvents)) {
            connectorSharedPresidioExternalMonitoringService.reportTotalEventMetric(pageEvents.size());

            totalEvents+=pageEvents.size();
        }
        connectorSharedPresidioExternalMonitoringService.reportSuccessAndTotalMetric(NUMBER_OF_PROCESSED_PAGES, MetricEnums.MetricValues.AVG_PAGE_SIZE, 1);

        if (!validateEvents(pageEvents)) { //todo
            final String errorMessage = "event validation failed!";
            logger.error(errorMessage);
            connectorSharedPresidioExternalMonitoringService.reportFailedEventMetric(INVALID_EVENTS_ERROR_KEY,pageEvents.size());
            connectorSharedPresidioExternalMonitoringService.reportFailedMetric(NUMBER_OF_PROCESSED_PAGES,  INVALID_EVENTS_ERROR_KEY, MetricEnums.MetricValues.FAILED_PAGES, 1 );
            throw new Exception(errorMessage);
        } else {
            for (AbstractDocument pageEvent : pageEvents) {
                processEvent(pageEvent);
            }
            connectorSharedPresidioExternalMonitoringService.reportSuccessAndTotalMetric(NUMBER_OF_PROCESSED_PAGES, MetricEnums.MetricValues.SUCCESS_PAGES, 1 );
        }
    }


    private boolean validateEvents(List<AbstractDocument> events) {
        return events != null; //todo
    }




    @Override
    public MonitorDetails getMonitorDetails() {
        if (monitorDetails == null) {
            PresidioExternalMonitoringServiceFactory presidioExternalMonitoringServiceFactory = new PresidioExternalMonitoringServiceFactory();

            try {
                PresidioExternalMonitoringService presidioExternalMonitoringService = presidioExternalMonitoringServiceFactory.createPresidioExternalMonitoringService(applicationName);
                logger.info("New Monitoring Service has initiated");
                monitorDetails = new MonitorDetails(null,presidioExternalMonitoringService , null);
            } catch (Exception e) {
                logger.error("Cannot load external monitoring service");
                throw new RuntimeException(e);
            }

        }
        return monitorDetails;
    }
}
