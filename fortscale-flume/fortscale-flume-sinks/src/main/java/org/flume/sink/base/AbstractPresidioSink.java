package org.flume.sink.base;

import org.apache.commons.lang.BooleanUtils;
import org.apache.flume.*;
import org.apache.flume.conf.Configurable;
import org.apache.flume.instrumentation.SinkCounter;
import org.apache.flume.sink.AbstractSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.apache.flume.CommonStrings.IS_BATCH;

public abstract class AbstractPresidioSink<T> extends AbstractSink implements Configurable {

    private static Logger logger = LoggerFactory.getLogger(AbstractPresidioSink.class);

    protected final SinkCounter sinkCounter = new SinkCounter(getName() + "-counter");

    protected boolean isBatch;
    protected boolean isDone;


    @Override
    public synchronized String getName() {
        return "presidio-sink";
    }

    @Override
    public void start() {
        sinkCounter.start();
        super.start();
    }

    @Override
    public void stop() {
        sinkCounter.stop();
        super.stop();
    }

    @Override
    public void configure(Context context) {
        isBatch = context.getBoolean(IS_BATCH, false);
    }


    @Override
    public Status process() throws EventDeliveryException {
        logger.debug("{} is starting...", getName());
        Status result = Status.READY;
        Channel channel = getChannel();
        Transaction transaction = channel.getTransaction();
        try {
            transaction.begin();
            final List<T> eventsToSave = getEvents();
            saveEvents(eventsToSave);
            logger.debug("{} has finished processing {} events {}.", getName(), eventsToSave.size());
            transaction.commit();
        } catch (Exception ex) {
            transaction.rollback();
            throw new EventDeliveryException("Failed to save event: ", ex);
        } finally {
            transaction.close();
            this.stop();
        }

        if (isBatch && isDone) {
            result = Status.DONE;
        }
        return result;
    }

    protected abstract void saveEvents(List<T> eventsToSave);

    protected abstract List<T> getEvents() throws Exception;

    protected boolean isGotControlDoneMessage(Event flumeEvent) {
        final boolean isControlDoneMessage = BooleanUtils.toBoolean(flumeEvent.getHeaders().get(CommonStrings.IS_DONE));
        if (isControlDoneMessage) {
            logger.debug("Sink {} got a control DONE message.", getName());
            if (isBatch) {
                isDone = true;
            }
            if (this.getChannel().take() != null) {
                logger.error("Got a control message DONE while there are still more records to process. This is not a valid state!");
            }
        }

        return isControlDoneMessage;
    }
}
