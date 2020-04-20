package org.flume.source.sdk;

import com.google.common.base.Preconditions;
import fortscale.common.general.Schema;
import fortscale.domain.core.AbstractDocument;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.flume.Context;
import org.apache.flume.FlumeException;
import org.apache.flume.conf.Configurable;
import org.flume.source.AbstractStreamablePresidioSource;
import org.flume.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.apache.flume.CommonStrings.*;

/**
 * an AbstractPresidioSource that runs a STREAMING SDK code to get the events
 */
public class PresidioStreamingSDKSource extends AbstractStreamablePresidioSource implements Configurable {

    private static Logger logger = LoggerFactory.getLogger(PresidioStreamingSDKSource.class);

    public static final String STREAM_IMPL_CLASS = "streamImplClassName";

    private static String[] mandatoryParams = {STREAM_IMPL_CLASS, START_DATE, END_DATE};
    private String streamImplClassName;

    @Override
    protected void doPresidioConfigure(Context context) throws FlumeException {
        try {
            for (String mandatoryParam : mandatoryParams) {
                if (!context.containsKey(mandatoryParam)) {
                    throw new Exception(String.format("Missing mandatory param %s for %s. Mandatory params are: %s",
                            getName(), mandatoryParam, Arrays.toString(mandatoryParams)));
                }
            }
            streamImplClassName = context.getString(STREAM_IMPL_CLASS);
            Preconditions.checkState(isStreamingSDK(streamImplClassName), "The specified " + STREAM_IMPL_CLASS + " is invalid");
            sourceFetcher = createStreamingSDKImpl(streamImplClassName);
            final String dateFormat = context.getString(DATE_FORMAT, DEFAULT_DATE_FORMAT);
            final String endDateAsString = context.getString(END_DATE);
            final String startDateAsString = context.getString(START_DATE);
            startDate = DateUtils.getDateFromText(startDateAsString, dateFormat);
            endDate = DateUtils.getDateFromText(endDateAsString, dateFormat);
            config = new HashMap<>(context.getParameters());

            setName("presidio-streaming-sdk-source:" + this.toString());
        } catch (Exception e) {
            logger.error("Error configuring " + PresidioStreamingSDKSource.class.getName(), e);
        }
    }

    @Override
    protected void startStreaming(Schema schema, Instant startDate, Instant endDate, Map<String, String> config) {
        ((EventsStream) sourceFetcher).startStreaming(schema, startDate, endDate, config);
    }

    @Override
    protected boolean hasNext() {
        return ((EventsStream) sourceFetcher).hasNext();
    }

    @Override
    protected AbstractDocument next() {
        return ((EventsStream) sourceFetcher).next();
    }

    @Override
    protected void stopStreaming() {
        ((EventsStream) sourceFetcher).stopStreaming();
    }


    private EventsStream createStreamingSDKImpl(String streamingSDKImplClassName) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException {
        Class<?> cl = Class.forName(streamingSDKImplClassName);
        Constructor<?> cons = cl.getConstructor();
        return (EventsStream) cons.newInstance();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException(PresidioStreamingSDKSource.class.getName() + " is a singleton and can not be cloned");
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("streamImplClassName", streamImplClassName)
                .append("startDate", startDate)
                .append("endDate", endDate)
                .toString();
    }

    private boolean isStreamingSDK(String streamingSDKImplClass) {
        Class<?> implClass;
        try {
            implClass = ClassLoader.getSystemClassLoader().loadClass(streamingSDKImplClass);
        } catch (ClassNotFoundException e) {
            logger.error("{} doesn't exist.", STREAM_IMPL_CLASS, e);
            return false;
        }
        if (!EventsStream.class.isAssignableFrom(implClass)) {
            logger.error("{} is not assignable from {}.", STREAM_IMPL_CLASS, EventsStream.class.getName());
            return false;
        }
        return true;
    }

}