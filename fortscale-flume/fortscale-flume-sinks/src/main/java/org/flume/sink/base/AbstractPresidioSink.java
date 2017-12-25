package org.flume.sink.base;

import com.mongodb.MongoException;
import org.apache.commons.lang.BooleanUtils;
import org.apache.flume.*;
import org.apache.flume.conf.Configurable;
import org.apache.flume.lifecycle.LifecycleSupervisor;
import org.apache.flume.sink.AbstractSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.apache.flume.CommonStrings.APPLICATION_NAME;
import static org.apache.flume.CommonStrings.IS_BATCH;

/**
 * This class adds support for 3 things:
 * 1) for running flume as a batch process (init, run, stop) and not as a stream process (which is the default behaviour). A Presidio source\interceptors must also be used when using a Presidio source.
 * 2) for using a metric service (that needs an application name).
 * 3) using the backoff mechanism with Presidio's default configurations
 */
public abstract class AbstractPresidioSink<T> extends AbstractSink implements Configurable {

    private static Logger logger = LoggerFactory.getLogger(AbstractPresidioSink.class);

    private static final String MIN_BACKOFF_SLEEP = "minBackoffSleep";
    private static final String MAX_BACKOFF_SLEEP = "maxBackoffSleep";
    private static final String BACKOFF_SLEEP_INCREMENT = "backoffSleepIncrement";

    protected boolean isBatch;
    protected String applicationName;
    protected boolean isDone;
    protected long minBackoffSleep;
    protected long maxBackoffSleep;
    protected long backoffSleepIncrement;


    @Override
    public synchronized String getName() {
        return "presidio-sink";
    }

    public String getApplicationName() {
        return applicationName;
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public void configure(Context context) {
        isBatch = context.getBoolean(IS_BATCH, false);
        applicationName = context.getString(APPLICATION_NAME, this.getName());
        initBackoff(context);
        doPresidioConfigure(context);
    }

    @Override
    public Status process() throws EventDeliveryException {
        logger.trace("{} is starting...", getName());
        Status result = Status.READY;
        Channel channel = getChannel();
        Transaction transaction = channel.getTransaction();
        try {
            transaction.begin();
            final List<T> eventsToSave = getEvents();

            if (eventsToSave.isEmpty()) {
                logger.debug("{} has finished processing 0 events.", getName());
                result = Status.BACKOFF;
            } else {
                SinkRunner.consecutiveBackoffCounter = 0;
                final int numOfSavedEvents = saveEvents(eventsToSave);
                LifecycleSupervisor.addToTotalSinkedEvents(numOfSavedEvents);
                logger.trace("{} has finished processing {} events.", getName(), numOfSavedEvents);
            }
            transaction.commit();
        } catch (Exception ex) {
            if (!ex.getClass().isAssignableFrom(MongoException.class)) {
                logger.warn("Exception is probably not recoverable. Not performing rollback.", ex);
                transaction.commit();
            } else {
                logger.warn("Performing rollback.");
                transaction.rollback();
            }
        } finally {
            if (LifecycleSupervisor.getTotalSinkedEvents() != 0) {
                logger.info("Presidio sink have sinked {} events", LifecycleSupervisor.getTotalSinkedEvents());
            }
            transaction.close();
            this.stop();
        }

        if (isBatch && isDone) {
            result = Status.DONE;
        }
        return result;
    }


    protected abstract void doPresidioConfigure(Context context);

    protected abstract int saveEvents(List<T> eventsToSave) throws Exception;

    protected abstract List<T> getEvents() throws Exception;

    protected boolean isControlDoneMessage(Event flumeEvent) {
        final boolean isControlDoneMessage = BooleanUtils.toBoolean(flumeEvent.getHeaders().get(CommonStrings.IS_DONE));
        if (isControlDoneMessage) {
            logger.debug("Sink {} got a control DONE message.", getName());
        }

        return isControlDoneMessage;
    }

    /**
     * this method overrides the backoff properties for ALL sinks
     *
     * @param context
     */
    private void initBackoff(Context context) {
        minBackoffSleep = context.getLong(MIN_BACKOFF_SLEEP, SinkRunner.DEFAULT_MIN_BACKOFF_SLEEP);
        maxBackoffSleep = context.getLong(MAX_BACKOFF_SLEEP, SinkRunner.DEFAULT_MAX_BACKOFF_SLEEP);
        backoffSleepIncrement = context.getLong(BACKOFF_SLEEP_INCREMENT, SinkRunner.DEFAULT_BACKOFF_SLEEP_INCREMENT);


        logger.info("Setting backoff properties. minBackoffSleep:{}, maxBackoffSleep: {}, backoffSleepIncrement: {}", minBackoffSleep, maxBackoffSleep, backoffSleepIncrement);
        SinkRunner.setMinBackoffSleep(minBackoffSleep);
        SinkRunner.setMaxBackoffSleep(maxBackoffSleep);
        SinkRunner.setBackoffSleepIncrement(backoffSleepIncrement);
    }
}
