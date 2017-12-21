package org.flume.source.rest;

import com.google.common.base.Preconditions;
import fortscale.domain.core.AbstractDocument;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.flume.Context;
import org.apache.flume.FlumeException;
import org.apache.flume.conf.Configurable;
import org.flume.source.AbstractPageablePresidioSource;
import org.flume.source.AbstractPresidioSource;
import org.flume.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.apache.flume.CommonStrings.DATE_FORMAT;
import static org.apache.flume.CommonStrings.DEFAULT_DATE_FORMAT;
import static org.apache.flume.CommonStrings.END_DATE;
import static org.apache.flume.CommonStrings.START_DATE;

/**
 * an AbstractPresidioSource that runs a REST query to get the events
 */
public class PresidioRestSource extends AbstractPageablePresidioSource implements Configurable {

    private static Logger logger = LoggerFactory.getLogger(PresidioRestSource.class);

    public static final String REST_API_IMPL_CLASS = "restApiImplClassName";

    private static String[] mandatoryParams = {REST_API_IMPL_CLASS, START_DATE, END_DATE};
    private String restApiImplClassName;
    private Instant startDate;
    private Instant endDate;


    @Override
    protected void doPresidioConfigure(Context context) throws FlumeException {
        try {
            for (String mandatoryParam : mandatoryParams) {
                if (!context.containsKey(mandatoryParam)) {
                    throw new Exception(String.format("Missing mandatory param %s for %s. Mandatory params are: %s",
                            getName(), mandatoryParam, Arrays.toString(mandatoryParams)));
                }
            }
            restApiImplClassName = context.getString(REST_API_IMPL_CLASS);
            Preconditions.checkState(isRestApi(restApiImplClassName), "The specified " + REST_API_IMPL_CLASS + " is invalid");
            sourceFetcher = createRestApiImpl(restApiImplClassName);
            final String dateFormat = context.getString(DATE_FORMAT, DEFAULT_DATE_FORMAT);
            final String endDateAsString = context.getString(END_DATE);
            final String startDateAsString = context.getString(START_DATE);
            startDate = DateUtils.getDateFromText(startDateAsString, dateFormat);
            endDate = DateUtils.getDateFromText(endDateAsString, dateFormat);


            setName("presidio-rest-source:" + this.toString());
        } catch (Exception e) {
            logger.error("Error configuring " + PresidioRestSource.class.getName(), e);
        }
    }

    @Override
    protected List<AbstractDocument> doFetch(int pageNum) {
        return ((RestApi) sourceFetcher).findByDateTimeBetween(startDate, endDate, pageNum, batchSize);
    }

    private RestApi createRestApiImpl(String restApiImplClassName) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException {
        Class<?> cl = Class.forName(restApiImplClassName);
        Constructor<?> cons = cl.getConstructor();
        return (RestApi) cons.newInstance();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException(PresidioRestSource.class.getName() + " is a singleton and can not be cloned");
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("restApiImplClassName", restApiImplClassName)
                .append("startDate", startDate)
                .append("endDate", endDate)
                .toString();
    }

    private boolean isRestApi(String restApiImplClass) {
        Class<?> implClass;
        try {
            implClass = ClassLoader.getSystemClassLoader().loadClass(restApiImplClass);
        } catch (ClassNotFoundException e) {
            logger.error("{} doesn't exist.", REST_API_IMPL_CLASS, e);
            return false;
        }
        if (!RestApi.class.isAssignableFrom(implClass)) {
            logger.error("{} is not assignable from {}.", REST_API_IMPL_CLASS, RestApi.class.getName());
            return false;
        }
        return true;
    }


}
