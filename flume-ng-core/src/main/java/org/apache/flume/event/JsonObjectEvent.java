package org.apache.flume.event;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.flume.Event;

import java.util.HashMap;
import java.util.Map;

public class JsonObjectEvent implements Event {

    private static JsonParser jsonParser;

    private Map<String, String> headers;
    private byte[] body;
    private JsonObject eventBodyAsJson;


    public JsonObjectEvent(){
        headers = new HashMap<String, String>();
        eventBodyAsJson = null;//new JsonObject();
        jsonParser = new JsonParser();
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
        if(eventBodyAsJson != null) {
            return eventBodyAsJson.toString().getBytes();
        } else {
            return body;
        }
    }

    @Override
    public void setBody(byte[] body) {
        this.body = body;
        this.eventBodyAsJson = null;
    }

    public JsonObject getEventBodyAsJson() {
        if(eventBodyAsJson == null) {
            final String eventBodyAsString = new String(body);
            setEventBodyAsJson(jsonParser.parse(eventBodyAsString).getAsJsonObject());
        }
        return eventBodyAsJson;
    }

    public void setEventBodyAsJson(JsonObject eventBodyAsJson) {
        this.eventBodyAsJson = eventBodyAsJson;
    }
}
