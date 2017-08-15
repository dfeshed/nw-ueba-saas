package org.apache.flume.source;

import org.apache.flume.CommonStrings;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.FlumeException;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.lifecycle.LifecycleState;
import org.apache.flume.lifecycle.LifecycleSupervisor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

public abstract class AbstractBatchableEventDrivenSource extends AbstractEventDrivenSource {

    private static Logger logger = LoggerFactory.getLogger(AbstractBatchableEventDrivenSource.class);

    /* This field indicates whether the agent is supposed to shut-down after the source is done (or in other words - is this a batch run?) */
    private boolean isBatch;

    @Override
    public void start() {
        super.start();
        if (isBatch) {
            sendDoneControlMessage();
            setLifecycleState(LifecycleState.DONE);
            logger.info("Source {} is done. Starting source-is-done flow", getName());
        }
    }

    @Override
    protected void doConfigure(Context context) throws FlumeException {
        isBatch = context.getBoolean(CommonStrings.IS_BATCH, false);
        doBatchConfigure(context);
    }

    /**
     * Method for configuring AbstractBatchableEventDrivenSources (couldn't call it doDoConfigure right?)
     * @param context the context
     * @throws FlumeException
     */
    protected abstract void doBatchConfigure(Context context) throws FlumeException;

    protected void sendDoneControlMessage() {
        final Event isDoneControlMessage = EventBuilder.withBody(new byte[0]);
        isDoneControlMessage.getHeaders().put(CommonStrings.IS_DONE, Boolean.TRUE.toString());
        logger.debug("Sending control message DONE");
        this.getChannelProcessor().processEvent(isDoneControlMessage);
    }
}
