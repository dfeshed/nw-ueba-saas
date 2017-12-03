package org.apache.flume.interceptor.presidio;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fortscale.utils.logging.Logger;
import org.apache.flume.Event;

import java.util.List;
import java.util.function.Predicate;

public class JsonFilterInterceptor extends AbstractPresidioJsonInterceptor {

    private static final Logger logger = Logger.getLogger(JsonFilterInterceptor.class);

    private final List<String> fields;
    private final List<Predicate<String>> predicates;

    public JsonFilterInterceptor(List<String> fields, List<Predicate<String>> predicates) {
        this.fields = fields;
        this.predicates = predicates;
    }

    @Override
    public Event doIntercept(Event event) {
        final String eventBodyAsString = new String(event.getBody());
        JsonObject eventBodyAsJson = new JsonParser().parse(eventBodyAsString).getAsJsonObject();
        for (int i = 0; i < fields.size(); i++) {
            String currField = fields.get(i);
            String currFieldValue = eventBodyAsJson.get(currField).toString();
            final Predicate<String> currPredicate = predicates.get(i);
            if (currPredicate.test(currFieldValue)) {
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
