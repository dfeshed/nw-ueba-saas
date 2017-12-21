package org.flume.source;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fortscale.domain.core.AbstractDocument;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.flume.CommonStrings;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.FlumeException;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.lifecycle.LifecycleState;
import org.apache.flume.source.AbstractEventDrivenSource;
import org.flume.utils.ConnectorSharedPresidioExternalMonitoringService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;

import java.nio.charset.Charset;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.flume.CommonStrings.END_DATE;
import static org.apache.flume.CommonStrings.START_DATE;


/**
 * This class adds support for 2 things:
 * 1) for running flume as a batch process (init, run, stop) and not as a stream process (which is the default behaviour). A Presidio sink/interceptors must also be used when using a Presidio source.
 * 2) for using a metric service (that needs an application name).
 */
public abstract class AbstractPresidioSource extends AbstractEventDrivenSource {


    private static Logger logger = LoggerFactory.getLogger(AbstractPresidioSource.class);
    protected boolean isBatch;
    protected String applicationName;
    protected int batchSize;

    @Override
    protected void doConfigure(Context context) throws FlumeException {
        isBatch = context.getBoolean(CommonStrings.IS_BATCH, false);
        applicationName = context.getString(CommonStrings.APPLICATION_NAME, this.getName());
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
}
