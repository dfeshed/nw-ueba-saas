package com.rsa.netwitness.presidio.automation.converter.formatters;


import com.rsa.netwitness.presidio.automation.converter.events.CefHeader;
import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import org.testng.collections.Maps;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.rsa.netwitness.presidio.automation.utils.common.LambdaUtils.not;
import static java.util.Map.Entry.comparingByKey;

public class BrokerCefFormatter implements EventFormatter<String> {

    public String format(NetwitnessEvent converted) {
        StringBuilder eventBuilder = new StringBuilder(headerToString(converted.cefHeader));

        Map<String, Object> event = converted.getEvent();

        Map<String, Object> filtered = event.entrySet()
                .parallelStream()
                .filter(not(e -> e.getKey().contains("-")))
                .sorted(comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        Map<String, Object> mapToFormat = Maps.newLinkedHashMap();
        mapToFormat.put("event_time", converted.brokerEventTime);
        mapToFormat.putAll(filtered);

        replaceByCustomField(mapToFormat, "netname");
        replaceByCustomField(mapToFormat, "org_dst");

        mapToFormat.entrySet().forEach(e -> eventBuilder.append(fieldToCefValue(e)));
        return eventBuilder.toString().trim();
    }

    private void replaceByCustomField(Map<String, Object> mapToFormat, String replace) {
        if (mapToFormat.containsKey(replace)) {
            mapToFormat.put("presidio_" + replace, mapToFormat.get(replace));
            mapToFormat.remove(replace);
        }
    }

    private String headerToString(CefHeader header) {
        return String.format("CEF:0|%s|%s|11.3|%s|%s|9|",
                header.cefVendor, header.cefProduct, header.cefEventType, header.cefEventDesc);
    }

    private String fieldToCefValue(Map.Entry<String, Object> entry) {
        String name = entry.getKey().replaceAll("_",".");
        Object value = entry.getValue();

        if (value instanceof String) {
            return name.concat("=").concat((String) value).concat(" ");
        }

        if (value instanceof Number || value instanceof Boolean) {
            return name.concat("=").concat(String.valueOf(value)).concat(" ");
        }

        if (value instanceof List<?>) {
            StringBuilder sb = new StringBuilder();

            for (Object object : (List<?>) value) {
                sb.append(name).append("=").append(object).append(" ");
            }
            return sb.toString();
        }

        throw new RuntimeException("Cast failed key=" + entry.getKey() + " value=" + entry.getValue());
    };
}