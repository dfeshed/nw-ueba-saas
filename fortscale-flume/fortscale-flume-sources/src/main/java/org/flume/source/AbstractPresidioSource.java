package org.flume.source;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fortscale.domain.core.AbstractDocument;
import org.apache.flume.CommonStrings;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.FlumeException;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.lifecycle.LifecycleState;
import org.apache.flume.source.AbstractEventDrivenSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import presidio.monitoring.sdk.api.services.PresidioExternalMonitoringService;
import presidio.monitoring.sdk.impl.factory.PresidioExternalMonitoringServiceFactory;

import java.nio.charset.Charset;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;

import static org.apache.flume.CommonStrings.END_DATE;
import static org.apache.flume.CommonStrings.START_DATE;


/**
 * This class adds support for 2 things:
 * 1) for running flume as a batch process (init, run, stop) and not as a stream process (which is the default behaviour). A batchable sink must also be used when using a batchable source.
 * 2) for using a metric service (that needs an application name).
 */
public abstract class AbstractPresidioSource extends AbstractEventDrivenSource {

    public static final String NUMBER_OF_EVENTS_IN_PAGES = "number.of.events.in.pages";
    public static final String NUMBER_OF_PROCESSED_PAGES = "number.of.processed.pages";
    public static final String NUMBER_OF_PROCESSED_EVENTS = "number.of.processed.events";
    public static final String AMOUNT = "amount";
    public static final String NUMBER_OF_DONE_MESSAGES_SENT = "number.of.done.messages.sent";
    public static final String NUMBER_OF_FILTERED_PAGES = "number.of.filtered.pages";

    private static Logger logger = LoggerFactory.getLogger(AbstractPresidioSource.class);


    /* This field indicates whether the agent is supposed to shut-down after the source is done (or in other words - is this a batch run?) */
    protected boolean isBatch;
    private String applicationName;
    protected int batchSize;
    protected SourceFetcher sourceFetcher;
    protected Instant startDate;
    protected Instant endDate;

    private static PresidioExternalMonitoringService presidioExternalMonitoringService;
    private PresidioExternalMonitoringServiceFactory presidioExternalMonitoringServiceFactory;
    private static ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public String getApplicationName() {
        return applicationName;
    }


    @Override
    public void start() {
        super.start();
        try {
            presidioExternalMonitoringService = presidioExternalMonitoringServiceFactory.
                    createPresidioExternalMonitoringService(getApplicationName());
            if (isBatch) {
                sendDoneControlMessage();
                setLifecycleState(LifecycleState.DONE);
                logger.info("Source {} is done. Starting source-is-done flow", getName());
            }
        } catch (Exception e) {
            final String errorMessage = "Failed to start " + this.getClass().getSimpleName();
            logger.error(errorMessage, e);
            setLifecycleState(LifecycleState.ERROR);
        }
    }

    @Override
    protected void doConfigure(Context context) throws FlumeException {
        isBatch = context.getBoolean(CommonStrings.IS_BATCH, false);
        applicationName = context.getString(CommonStrings.APPLICATION_NAME, this.getName());
        doPresidioConfigure(context);
    }

    /**
     * Method for configuring AbstractPresidioSource (couldn't call it doDoConfigure right?)
     *
     * @param context the context
     * @throws FlumeException
     */
    protected abstract void doPresidioConfigure(Context context) throws FlumeException;

    private void sendDoneControlMessage() {
        final Event isDoneControlMessage = EventBuilder.withBody(new byte[0]);
        isDoneControlMessage.getHeaders().put(CommonStrings.IS_DONE, Boolean.TRUE.toString());
        logger.debug("Sending control message DONE");

        presidioExternalMonitoringService.reportCustomMetricReportOnce(
                NUMBER_OF_DONE_MESSAGES_SENT, 1, new HashSet<>(), AMOUNT, null);
        this.getChannelProcessor().processEvent(isDoneControlMessage);
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
        presidioExternalMonitoringServiceFactory.close();
    }

    protected abstract List<AbstractDocument> doFetch(int pageNum);


    private void processEvent(AbstractDocument event) throws JsonProcessingException {
        presidioExternalMonitoringService.reportCustomMetricReportOnce(
                NUMBER_OF_PROCESSED_EVENTS, 1, new HashSet<>(), AMOUNT, null);

        final String eventAsString;
        eventAsString = mapper.writeValueAsString(event);
        final Event flumeEvent = EventBuilder.withBody(eventAsString, Charset.defaultCharset());
        logger.trace("{} has finished processing event {}. Sending event to channel", getName(), flumeEvent);
        getChannelProcessor().processEvent(flumeEvent); // Store the Event into this Source's associated Channel(s)
    }

    private void processPage(List<AbstractDocument> pageEvents) throws Exception {
        presidioExternalMonitoringService.reportCustomMetricReportOnce(
                NUMBER_OF_PROCESSED_PAGES, 1, new HashSet<>(), AMOUNT, null);
        presidioExternalMonitoringService.reportCustomMetricReportOnce(
                NUMBER_OF_EVENTS_IN_PAGES, pageEvents.size(), new HashSet<>(), AMOUNT, null);

        if (!validateEvents(pageEvents)) { //todo
            final String errorMessage = "event validation failed!";
            logger.error(errorMessage);

            presidioExternalMonitoringService.reportCustomMetricReportOnce(
                    NUMBER_OF_FILTERED_PAGES, 1, new HashSet<>(), AMOUNT, null);
            throw new Exception(errorMessage);
        } else {
            for (AbstractDocument pageEvent : pageEvents) {
                processEvent(pageEvent);
            }
        }
    }


    private boolean validateEvents(List<AbstractDocument> events) {
        return events != null; //todo
    }
}
