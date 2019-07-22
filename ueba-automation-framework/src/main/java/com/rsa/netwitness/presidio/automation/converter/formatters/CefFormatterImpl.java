package com.rsa.netwitness.presidio.automation.converter.formatters;


import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import com.rsa.netwitness.presidio.automation.converter.events.ConverterEventBase;

public class CefFormatterImpl implements NetwitnessEventFormatter<String> {

    public String format(ConverterEventBase event) {
        StringBuilder eventBuilder = new StringBuilder(cefHeader(event.getAsNetwitnessEvent()));
        event.getAsNetwitnessEvent()
                .getCefKeyValueMap()
                .forEach((k, v) ->
                        eventBuilder.append(formatCefKey(k)).append("=").append(v).append(" "));
        return eventBuilder.toString().trim();
    }

    private String cefHeader(NetwitnessEvent event) {
        return String.format("CEF:0|%s|%s|11.3|%s|%s|9|",
                event.getCefVendor(), event.getCefProduct(), event.getCefEventType(), event.getCefEventDesc());
    }

    private String formatCefKey(String fieldName) {
        String newField = fieldName.replaceAll("_",".");
        int idx = newField.indexOf("$");
        if (idx > 0)
            return newField.substring(0, idx);
        else return newField;
    }
}