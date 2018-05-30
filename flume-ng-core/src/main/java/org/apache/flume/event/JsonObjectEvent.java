package org.apache.flume.event;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.flume.Event;

import java.util.HashMap;
import java.util.Map;

public class JsonObjectEvent implements Event {

    private Map<String, String> headers;
    private JsonObject eventBodyAsJson;

    public JsonObjectEvent(){
        headers = new HashMap<String, String>();
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public byte[] getBody() {
        return eventBodyAsJson.toString().getBytes();
    }

    @Override
    public void setBody(byte[] body) {
        final String eventBodyAsString = new String(body);
        this.eventBodyAsJson = new JsonParser().parse(eventBodyAsString).getAsJsonObject();
    }

    public JsonObject getEventBodyAsJson() {
        return eventBodyAsJson;
    }
}
