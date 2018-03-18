package org.apache.flume.interceptor.presidio;

import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;
import org.apache.flume.CommonStrings;
import org.apache.flume.Context;
import org.apache.flume.interceptor.Interceptor;


/**
 * This class adds support for 2 things:
 * 2) for using a metric service (that needs an application name).
 */
public abstract class AbstractPresidioInterceptorBuilder implements Interceptor.Builder {

    @Override
    public void configure(Context context) {
        doConfigure(context);
    }

    @Override
    public Interceptor build() {
        return doBuild();
    }

    protected abstract AbstractPresidioJsonInterceptor doBuild();

    protected abstract void doConfigure(Context context);

    protected String[] getStringArrayFromConfiguration(Context context, String key, String delimiter) {
        String arrayAsString = context.getString(key, "");
        Preconditions.checkArgument(StringUtils.isNotEmpty(arrayAsString),
                key + " can not be empty.");

        return arrayAsString.split(delimiter);
    }
}
