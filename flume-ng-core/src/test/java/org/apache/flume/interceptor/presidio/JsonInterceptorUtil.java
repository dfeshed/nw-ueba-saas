package org.apache.flume.interceptor.presidio;

import com.google.common.base.Charsets;
import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;

import java.util.List;

public class JsonInterceptorUtil {
    public static Event buildEvent(List<String> fields){
        String eventBody = String.format("{%s}", String.join(",", fields));

        return EventBuilder.withBody(eventBody, Charsets.UTF_8);
    }

    public static String buildKeyValue(String key, String value){
        return String.format("\"%s\":\"%s\"", key, value);
    }

    public static String buildKeyNullValue(String key){
        return String.format("\"%s\":null", key);
    }
}
