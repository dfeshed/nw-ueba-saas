package com.rsa.netwitness.presidio.automation.converter.formatters;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import org.slf4j.LoggerFactory;

public class JsonLineFormatter<T> implements EventFormatter<NetwitnessEvent, String> {
    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(JsonLineFormatter.class);
    private final EventFormatter<NetwitnessEvent, T> formatter;

    public JsonLineFormatter(EventFormatter<NetwitnessEvent, T> formatter) {
        this.formatter = formatter;
    }

    @Override
    public String format(NetwitnessEvent event) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(formatter.format(event)).concat("\n");
        } catch (JsonProcessingException e) {
            LOGGER.error("Unable to convert event " + event);
            e.printStackTrace();
        }
        throw new RuntimeException("Unable to convert event to Json Line");
    }
}
