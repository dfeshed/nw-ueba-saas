package org.flume.sink.base;

import com.mongodb.MongoException;
import org.apache.commons.lang.BooleanUtils;
import org.apache.flume.*;
import org.apache.flume.conf.Configurable;
import org.apache.flume.sink.AbstractSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
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

    /**
     * Stopping single processing cycle
     */
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
        List<T> eventsToSave=null;
        Instant logicalTime=null;
        try {
            transaction.begin();
            eventsToSave = getEvents();

            if (eventsToSave.isEmpty()) {
                logger.debug("{} has finished processing 0 events.", getName());
                result = Status.BACKOFF;
            } else {
                logicalTime = getLogicalHour(eventsToSave.get(0));
                monitorNumberOfReadEvents(eventsToSave.size(),logicalTime);

                SinkRunner.consecutiveBackoffCounter = 0;

                final int numOfSavedEvents = saveEvents(eventsToSave);
                monitorNumberOfSavedEvents(numOfSavedEvents,logicalTime);
                logger.trace("{} has finished processing {} events.", getName(), numOfSavedEvents);
            }
            transaction.commit();
        } catch (Exception ex) {
            if (!ex.getClass().isAssignableFrom(MongoException.class)) {
                logger.warn("Exception is probably not recoverable. Not performing rollback.", ex);
                transaction.commit();
                monitorUnknownError(eventsToSave.size(),logicalTime);

            } else {
                logger.warn("Performing rollback.");

                 monitorNumberOfUnassignableEvents(eventsToSave.size() ,channel.getName(),logicalTime);

                transaction.rollback();
            }

        } finally {
            transaction.close();
            this.stop();
        }

        if (isBatch && isDone) {
            //Batchable work finished, system if moved to done and going to be closed, monitor stopped
            result = Status.DONE;
        }
        return result;
    }


    protected abstract void doPresidioConfigure(Context context);

    protected abstract int saveEvents(List<T> eventsToSave) throws Exception;

    protected abstract List<T> getEvents() throws Exception;

    /**
     * Monitor how many events have been retrieved in the sink and need to be saved to the DB
     * @param number - number of retried events
     * @param logicalHour - the logical hour - optional.
     */
    protected void monitorNumberOfReadEvents(int number, Instant logicalHour) {
        logger.warn(this.getClass().getName()+" does not support monitoring");
    }


    /**
     * Monitor how many events have been saved successfully into the DB
     * @param number - number of saved events
     * @param logicalHour - the logical hour - optional.
     */
    protected void monitorNumberOfSavedEvents(int number, Instant logicalHour){
        logger.warn(this.getClass().getName()+" does not support monitoring");
    }

    /**
     * Monitor how many events have been failed because of not existing schema
     * @param number - number of failed events
     * @param logicalHour - the logical hour - optional.
     */
    protected void monitorNumberOfUnassignableEvents(int number, String schema, Instant logicalHour){
        logger.warn(this.getClass().getName()+" does not support monitoring");
    }
    /**
     * Monitor how many events have been failed because any other reason
     * @param number - number of failed vevents
     * @param logicalHour - the logical hour - optional.
     */
    protected void monitorUnknownError(int number, Instant logicalHour){
        logger.warn(this.getClass().getName()+" does not support monitoring");
    }

    /**
     * Get logical hour from the event,
     * @param event
     * @return - the time from the event is possible, null if not possible
     */
    protected Instant getLogicalHour(T event){
        logger.warn(this.getClass().getName()+" does not support monitoring");
        return null;
    }


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
