package org.flume.source;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fortscale.domain.core.AbstractDocument;
import org.apache.commons.collections.CollectionUtils;
import org.apache.flume.*;
import org.apache.flume.FlumePresidioExternalMonitoringService.FlumeComponentType;
import org.apache.flume.conf.MonitorDetails;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.lifecycle.LifecycleState;
import org.apache.flume.marker.MonitorInitiator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import presidio.monitoring.sdk.api.services.PresidioExternalMonitoringService;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;
import presidio.monitoring.sdk.impl.factory.PresidioExternalMonitoringServiceFactory;

import java.nio.charset.Charset;
import java.time.Instant;
import java.util.List;

import static org.apache.flume.CommonStrings.END_DATE;
import static org.apache.flume.CommonStrings.START_DATE;


/**
 * This class adds support for 2 things:
 * 1) for running flume as a batch process (init, run, stop) and not as a stream process (which is the default behaviour). A Presidio sink/interceptors must also be used when using a Presidio source.
 * 2) for using a metric service (that needs an application name).
 */
public abstract class AbstractPageablePresidioSource extends AbstractPresidioSource  {


    private static final String NUMBER_OF_PROCESSED_PAGES = "processed_pages";
    public static final String INVALID_EVENTS_ERROR_KEY = "INVALID_EVENTS";

    private static Logger logger = LoggerFactory.getLogger(AbstractPageablePresidioSource.class);
    private int totalEvents = 0;
    private int totalPages = 0;

    /* This field indicates whether the agent is supposed to shut-down after the source is done (or in other words - is this a batch run?) */


    protected SourceFetcher sourceFetcher;


    private static ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }


    @Override
    protected void doStart() throws FlumeException {
        logger.info("{} is processing events for {}: {}, {}: {}.",
                getName(), START_DATE, END_DATE, startDate, endDate);
        try {
            int pageNum = 0;// first page
            List<AbstractDocument> currentPage = doFetch(pageNum);
            if (currentPage.size() == 0) {
                logger.warn("Failed to process events for {}: {}, {}: {}. There were no events to process",
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
            logger.error("{} Failed to process events for {}: {}, {}: {}. There were no events to process",
                    getName(), START_DATE, startDate, END_DATE, endDate);
            logger.error(e.getMessage());

        } finally {
            this.stop();
        }
    }


    @Override
    protected void doStop() throws FlumeException {
        try {
            flumePresidioExternalMonitoringService.manualExportMetrics();
        } catch (Exception e) {
            logger.error("Failed to send metrics during stop. Monitor Details: {} ", monitorDetails, e);
        }
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
        } catch (JsonProcessingException e) {
            failureReason = "CANNOT_SERIALIZE_EVENT";
        }

        if (failureReason == null) {
            flumePresidioExternalMonitoringService.reportSuccessEventMetric(1);
        } else {

            flumePresidioExternalMonitoringService.reportFailedEventMetric(failureReason, 1);
        }

    }

    private void processPage(List<AbstractDocument> pageEvents) throws Exception {
        flumePresidioExternalMonitoringService.reportSuccessAndTotalMetric(NUMBER_OF_PROCESSED_PAGES, MetricEnums.MetricValues.TOTAL_PAGES, 1);
        totalPages++;
        if (CollectionUtils.isNotEmpty(pageEvents)) {
            flumePresidioExternalMonitoringService.reportTotalEventMetric(pageEvents.size());

            totalEvents += pageEvents.size();
        }
        flumePresidioExternalMonitoringService.reportSuccessAndTotalMetric(NUMBER_OF_PROCESSED_PAGES, MetricEnums.MetricValues.AVG_PAGE_SIZE, 1);

        if (!validateEvents(pageEvents)) { //todo
            final String errorMessage = "event validation failed!";
            logger.error(errorMessage);
            flumePresidioExternalMonitoringService.reportFailedEventMetric(INVALID_EVENTS_ERROR_KEY, pageEvents.size());
            flumePresidioExternalMonitoringService.reportFailedMetric(NUMBER_OF_PROCESSED_PAGES, INVALID_EVENTS_ERROR_KEY, MetricEnums.MetricValues.FAILED_PAGES, 1);
            throw new Exception(errorMessage);
        } else {
            for (AbstractDocument pageEvent : pageEvents) {
                processEvent(pageEvent);
            }
            flumePresidioExternalMonitoringService.reportSuccessAndTotalMetric(NUMBER_OF_PROCESSED_PAGES, MetricEnums.MetricValues.SUCCESS_PAGES, 1);
        }
    }


    private boolean validateEvents(List<AbstractDocument> events) {
        return events != null; //todo
    }



}
