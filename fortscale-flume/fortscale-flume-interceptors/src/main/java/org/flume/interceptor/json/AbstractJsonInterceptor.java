package org.flume.interceptor.json;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.flume.Event;
import org.flume.interceptor.base.AbstractInterceptor;

public abstract class AbstractJsonInterceptor extends AbstractInterceptor {

    @Override
    public abstract Event intercept(Event event);

    protected JsonObject getEventBodyAsJson(Event event) {
        final String eventBodyAsString = new String(event.getBody());
        return new JsonParser().parse(eventBodyAsString).getAsJsonObject();
    }
}
