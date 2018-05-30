package org.flume.source;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.base.Stopwatch;
import fortscale.common.general.Schema;
import fortscale.domain.core.AbstractDocument;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.FlumeException;
import org.apache.flume.event.JsonEventBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.apache.flume.CommonStrings.END_DATE;
import static org.apache.flume.CommonStrings.START_DATE;


public abstract class AbstractStreamablePresidioSource extends AbstractPresidioSource {

    private static Logger logger = LoggerFactory.getLogger(AbstractStreamablePresidioSource.class);

    private final Stopwatch initStopWatch = Stopwatch.createUnstarted();

    private final Stopwatch streamingStopWatch = Stopwatch.createUnstarted();

    private final Stopwatch processingStopWatch = Stopwatch.createUnstarted();

    private int totalEvents = 0;

    protected SourceFetcher sourceFetcher;

    protected Map<String, String> config;

    private static ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }


    @Override
    protected abstract void doPresidioConfigure(Context context);


    @Override
    protected void doStart() throws FlumeException {
        logger.info("{} is processing events for {}: {}, {}: {}.", getName(), START_DATE, END_DATE, startDate, endDate);

        try {
            initStopWatch.start();
            startStreaming(Schema.createSchema(schema), startDate, endDate, config);
            initStopWatch.stop();

            while (hasNext()) {

                streamingStopWatch.start();
                AbstractDocument event = next();
                streamingStopWatch.stop();

                if (event!=null) {
                    processingStopWatch.start();
                    processEvent(event);
                    processingStopWatch.stop();
                    totalEvents++;
                }
            }

            if (isBatch) {
                sendDoneControlMessage();
            }

        } catch (Exception e) {
            logger.error("{} Failed to process events for {}: {}, {}: {}.",
                    getName(), START_DATE, startDate, END_DATE, endDate, e);
            throw new RuntimeException(e);

        } finally {
            stopStreaming();
            this.stop();
        }
    }



    @Override
    protected void doStop() throws FlumeException {
        logger.info("\n" + "STREAMING SOURCE: total events: {}, init time: {} sec., streaming time: {} sec., processing time: {} sec.",
                    totalEvents,
                    initStopWatch.elapsed(TimeUnit.SECONDS),
                    streamingStopWatch.elapsed(TimeUnit.SECONDS),
                    processingStopWatch.elapsed(TimeUnit.SECONDS));

        try {
            flumePresidioExternalMonitoringService.manualExportMetrics();
        } catch (Exception e) {
            logger.error("Failed to send metrics during stop. Monitor Details: {} ", monitorDetails, e);
        }
        sendDoneControlMessage();
    }



    protected abstract void startStreaming(Schema schema, Instant startDate, Instant endDate, Map<String, String> config);

    protected abstract boolean hasNext();

    protected abstract AbstractDocument next();

    protected abstract void stopStreaming();


    private void processEvent(AbstractDocument event) throws JsonProcessingException {
        String failureReason = null;
        try {

            final String eventAsString;
            eventAsString = mapper.writeValueAsString(event);
            final Event flumeEvent = JsonEventBuilder.withBody(eventAsString, Charset.defaultCharset());
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

}