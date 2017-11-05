package org.flume.sink.base;

import com.mongodb.MongoException;
import org.apache.commons.lang.BooleanUtils;
import org.apache.flume.*;
import org.apache.flume.conf.Configurable;
import org.apache.flume.sink.AbstractSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.apache.flume.CommonStrings.APPLICATION_NAME;
import static org.apache.flume.CommonStrings.IS_BATCH;
import static org.apache.flume.CommonStrings.MAX_BACK_OFF_SLEEP;

public abstract class AbstractPresidioSink<T> extends AbstractSink implements Configurable {

    private static Logger logger = LoggerFactory.getLogger(AbstractPresidioSink.class);

//    protected final SinkCounter sinkCounter = new SinkCounter(getName() + "-counter");

    protected boolean isBatch;
    protected String applicationName;
    protected boolean isDone;


    @Override
    public synchronized String getName() {
        return "presidio-sink";
    }

    @Override
    public void start() {
//        if (sinkCounter.getStartTime() == 0L) { //if wasn't started yet
//            sinkCounter.start();
//        }
        super.start();
    }

    @Override
    public void stop() {
//        sinkCounter.stop();
        super.stop();
    }

    @Override
    public void configure(Context context) {
        isBatch = context.getBoolean(IS_BATCH, false);
        applicationName = context.getString(APPLICATION_NAME, this.getName());
        int maxBackOffSleep = context.getInteger(MAX_BACK_OFF_SLEEP, 5000);
        if (maxBackOffSleep > 0) {
            SinkRunner.maxBackoffSleep = maxBackOffSleep;
        }
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
                final int numOfSavedEvents = saveEvents(eventsToSave);
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
}
