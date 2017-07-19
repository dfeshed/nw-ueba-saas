package org.flume.sink.base;

import org.apache.flume.*;
import org.apache.flume.conf.Configurable;
import org.apache.flume.instrumentation.SinkCounter;
import org.apache.flume.sink.AbstractSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class AbstractPresidioSink<T> extends AbstractSink implements Configurable, Sink {

    private static Logger logger = LoggerFactory.getLogger(AbstractPresidioSink.class);

    protected final SinkCounter sinkCounter = new SinkCounter(getName() + "-counter");

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
    public abstract void configure(Context context);

    @Override
    public Status process() throws EventDeliveryException {
        logger.debug("{} is starting...", getName());
        sinkCounter.start();
        Status result = Status.READY;
        Channel channel = getChannel();
        Transaction transaction = channel.getTransaction();
        try {
            transaction.begin();
            final List<T> eventsToSave = parseEvents(channel);
            final int numOfEventsToSave = saveEvents(eventsToSave);
            logger.debug("{} has finished processing {} events {}.", getName(), numOfEventsToSave);
            transaction.commit();
        } catch (Exception ex) {
            transaction.rollback();
            throw new EventDeliveryException("Failed to save event: ", ex);
        } finally {
            transaction.close();
            this.stop();
        }

        return result;
    }

    protected abstract int saveEvents(List<T> eventsToSave);

    protected abstract List<T> parseEvents(Channel channel);
}
