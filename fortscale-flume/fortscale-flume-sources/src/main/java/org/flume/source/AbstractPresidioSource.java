package org.flume.source;


import com.google.common.base.Preconditions;
import fortscale.common.general.Schema;
import org.apache.commons.lang.StringUtils;
import org.apache.flume.CommonStrings;
import org.apache.flume.Context;
import org.apache.flume.FlumeException;
import org.apache.flume.source.AbstractEventDrivenSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class adds support for 2 things:
 * 1) for running flume as a batch process (init, run, stop) and not as a stream process (which is the default behaviour). A Presidio sink/interceptors must also be used when using a Presidio source.
 * 2) for using a metric service (that needs an application name).
 */
public abstract class AbstractPresidioSource extends AbstractEventDrivenSource {


    protected static Logger logger = LoggerFactory.getLogger(AbstractPresidioSource.class);
    protected boolean isBatch;
    protected String applicationName;
    protected int batchSize;
    protected Schema schema;

    @Override
    protected void doConfigure(Context context) throws FlumeException {
        isBatch = context.getBoolean(CommonStrings.IS_BATCH, false);
        applicationName = context.getString(CommonStrings.APPLICATION_NAME, this.getName());
        final String schemaName = context.getString(CommonStrings.SCHEMA_NAME, null);
        Preconditions.checkArgument(StringUtils.isNotEmpty(schemaName), CommonStrings.SCHEMA_NAME + " can not be empty.");
        schema = Schema.createSchema(schemaName);

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
}
