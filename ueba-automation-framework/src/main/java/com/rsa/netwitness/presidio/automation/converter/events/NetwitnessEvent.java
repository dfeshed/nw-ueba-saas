package com.rsa.netwitness.presidio.automation.converter.events;

import fortscale.common.general.Schema;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

public abstract class NetwitnessEvent implements ConverterEventBase {

    @Override
    public NetwitnessEvent getAsNetwitnessEvent() {
        return this;
    }

    protected DateTimeFormatter eventTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withLocale( Locale.getDefault() ).withZone( ZoneId.of("UTC"));
    protected Instant eventTimeEpoch;
    protected Instant mongo_source_event_time;

    protected Schema schema;

    protected String cefVendor;
    protected String cefProduct;
    protected String cefEventType;
    protected String cefEventDesc;
    protected String event_time;




























    public String getCefVendor() {
        return cefVendor;
    }

    public String getCefProduct() {
        return cefProduct;
    }

    public String getCefEventType() {
        return cefEventType;
    }

    public String getCefEventDesc() {
        return cefEventDesc;
    }

    @Override
    public Schema mongoSchema() {
        return schema;
    }

    public Instant getEventTimeEpoch() {
        return eventTimeEpoch;
    }

    public Map<String, String> getCefKeyValueMap() {
        Map<String, String> cefFields = new LinkedHashMap<>();

        cefOnlyFields
                .forEach(field -> cefFields.computeIfAbsent(field.getName(), v -> stringValueOf.apply(field)));

        return cefFields;
    }

    private Function<Field, String> stringValueOf = field -> {
        try {
            return (String) field.get(this);
        } catch (IllegalAccessException | ClassCastException e1) {
            System.err.println("Field name: " + field.getName());
            e1.printStackTrace();
        }
        return  null;
    };

    private static boolean cefFields(Field cefFeild) {
        String [] filterList = {"eventTimeFormatter", "schema", "eventTimeEpoch"};

        boolean result = Arrays.stream(filterList)
                .anyMatch(elementToFilter -> elementToFilter.equalsIgnoreCase(cefFeild.getName()));

        result |= cefFeild.getName().startsWith("cef");
        result |= ( !cefFeild.getType().isAssignableFrom(String.class) );
        return !result;
    }

    private static final Field[] classFields = NetwitnessEvent.class.getDeclaredFields();
    private static final List<Field> cefOnlyFields = Arrays.stream(classFields)
            .filter(NetwitnessEvent::cefFields)
            .collect(toList());
}
