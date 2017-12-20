package org.apache.flume.interceptor.presidio;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fortscale.utils.logging.Logger;
import org.apache.flume.Event;

import java.util.List;
import java.util.function.BiPredicate;

public class JsonFieldFilterInterceptor extends AbstractPresidioJsonInterceptor {

    private static final Logger logger = Logger.getLogger(JsonFieldFilterInterceptor.class);

    private final List<String> fields;
    private final List<BiPredicate<JsonObject, String>> predicates;

    public JsonFieldFilterInterceptor(List<String> fields, List<BiPredicate<JsonObject, String>> predicates) {
        this.fields = fields;
        this.predicates = predicates;
    }

    @Override
    public Event doIntercept(Event event) {
        final String eventBodyAsString = new String(event.getBody());
        JsonObject eventBodyAsJson = new JsonParser().parse(eventBodyAsString).getAsJsonObject();
        for (int i = 0; i < fields.size(); i++) {
            String currField = fields.get(i);
            final JsonElement jsonElement = eventBodyAsJson.get(currField);
            String currFieldValue;
            if (jsonElement == null || jsonElement.isJsonNull()) {
                currFieldValue = "";
            } else {
                currFieldValue = jsonElement.getAsString();
            }
            final BiPredicate<JsonObject, String> currPredicate = predicates.get(i);
            if (currPredicate.test(eventBodyAsJson, currFieldValue)) {
                if (eventBodyAsJson.remove(currField) != null) {
                    logger.trace("Field {} was removed.", currField);
                }
            }
        }

        event.setBody(eventBodyAsJson.toString().getBytes());
        return event;
    }

    @Override
    public String toString() {
        return new org.apache.commons.lang3.builder.ToStringBuilder(this)
                .append("fields", fields)
                .append("predicates", predicates)
                .append("applicationName", applicationName)
                .toString();
    }
}
