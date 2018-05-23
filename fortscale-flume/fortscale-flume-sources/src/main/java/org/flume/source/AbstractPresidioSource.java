package org.flume.source;


import org.apache.flume.*;

import org.apache.flume.conf.MonitorDetails;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.lifecycle.LifecycleState;
import org.apache.flume.marker.MonitorInitiator;
import org.apache.flume.source.AbstractEventDrivenSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import presidio.monitoring.sdk.api.services.PresidioExternalMonitoringService;
import presidio.monitoring.sdk.impl.factory.PresidioExternalMonitoringServiceFactory;

import java.time.Instant;


/**
 * This class adds support for 2 things:
 * 1) for running flume as a batch process (init, run, stop) and not as a stream process (which is the default behaviour). A Presidio sink/interceptors must also be used when using a Presidio source.
 * 2) for using a metric service (that needs an application name).
 */
public abstract class AbstractPresidioSource extends AbstractEventDrivenSource implements MonitorInitiator {


    protected static Logger logger = LoggerFactory.getLogger(AbstractPresidioSource.class);
    private static final String COLLECTOR_SOURCE_NAME = "CollectorSource";
    protected boolean isBatch;
    protected String applicationName;
    protected int batchSize;
    protected Instant startDate;
    protected Instant endDate;
    protected String schema;

    protected MonitorDetails monitorDetails;


    PresidioExternalMonitoringService presidioExternalMonitoringService;

    protected FlumePresidioExternalMonitoringService flumePresidioExternalMonitoringService;

    @Override
    protected void doConfigure(Context context) throws FlumeException {
        isBatch = context.getBoolean(CommonStrings.IS_BATCH, false);
        applicationName = context.getString(CommonStrings.APPLICATION_NAME, this.getName());
        schema = context.getString(CommonStrings.SCHEMA_NAME);
        doPresidioConfigure(context);
    }

    public String getApplicationName() {
        return applicationName;
    }

    /**
     * Method for configuring AbstractPresidioSource (couldn't call it doDoConfigure right?)
     *
     * @param context the context
     * @throws FlumeException
     */
    protected abstract void doPresidioConfigure(Context context) throws FlumeException;

    @Override
    protected void doStart() throws FlumeException {

    }

    @Override
    protected void doStop() throws FlumeException {

    }

    @Override
    public void start() {
        if (this.monitorDetails == null) {
            throw new RuntimeException("Monitor should be already initiated in this phase");
        }
        super.start();

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
            logger.error("Failed to stop {}", this, e);
            setLifecycleState(LifecycleState.ERROR);
        }
    }


    protected void sendDoneControlMessage() {
        final Event isDoneControlMessage = EventBuilder.withBody(new byte[0]);
        isDoneControlMessage.getHeaders().put(CommonStrings.IS_DONE, Boolean.TRUE.toString());
        logger.debug("Sending control message DONE");

        this.getChannelProcessor().processEvent(isDoneControlMessage);
    }

    @Override
    public MonitorDetails getMonitorDetails() {
        if (monitorDetails == null) {
            PresidioExternalMonitoringServiceFactory presidioExternalMonitoringServiceFactory = new PresidioExternalMonitoringServiceFactory();

            try {
                presidioExternalMonitoringService = presidioExternalMonitoringServiceFactory.createPresidioExternalMonitoringService(applicationName);
                logger.info("New Monitoring Service has initiated");
                monitorDetails = new MonitorDetails(this.startDate, presidioExternalMonitoringService, this.schema);
                this.flumePresidioExternalMonitoringService = new FlumePresidioExternalMonitoringService(monitorDetails, FlumePresidioExternalMonitoringService.FlumeComponentType.SOURCE, COLLECTOR_SOURCE_NAME);
            } catch (Exception e) {
                logger.error("Cannot load external monitoring service");
                throw new RuntimeException(e);
            }

        }
        return monitorDetails;
    }
}
