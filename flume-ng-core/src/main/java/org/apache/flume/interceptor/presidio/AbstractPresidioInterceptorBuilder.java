package org.apache.flume.interceptor.presidio;

import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;
import org.apache.flume.CommonStrings;
import org.apache.flume.Context;
import org.apache.flume.interceptor.Interceptor;

public abstract class AbstractPresidioInterceptorBuilder implements Interceptor.Builder {

    protected String monitoringApplicationName;

    @Override
    public void configure(Context context) {
        monitoringApplicationName = context.getString(CommonStrings.MONITORING_APPLICATION_NAME, this.getClass().getSimpleName());
        doConfigure(context);
    }

    protected abstract void doConfigure(Context context);

    protected String[] getStringArrayFromConfiguration(Context context, String key, String delimiter) {
        String arrayAsString = context.getString(key, "");
        Preconditions.checkArgument(StringUtils.isNotEmpty(arrayAsString),
                key + " can not be empty.");

        return arrayAsString.split(delimiter);
    }
}
