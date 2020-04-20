package com.rsa.netwitness.presidio.automation.mapping.operation_type;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.stream.JsonReader;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class ActiveDirectoryOperationTypeMapping {

    private static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(ActiveDirectoryOperationTypeMapping.class.getName());

    private final String RESOURCE_NAME = "active_directory.json";
    private static Map<String, String> root;
    private Gson gson = new Gson();

    private Map<String, List<String>> operationTypeToCategoryMap;
    private Map<Integer, String> eventCodeMapToOperationTypeMap;

    private ActiveDirectoryOperationTypeMapping() {
        LOGGER.debug("Going to load resource: " + RESOURCE_NAME);
        InputStream resourceAsStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(RESOURCE_NAME);

        assertThat(resourceAsStream).as("Unable to get resource " + RESOURCE_NAME).isNotNull();
        Reader targetReader = new InputStreamReader(resourceAsStream);
        JsonReader reader = new JsonReader(targetReader);

        root = gson.fromJson(Objects.requireNonNull(reader), HashMap.class);
        assertThat(root).isNotNull().isNotEmpty();
        setOperationTypeToCategoryMap();
        setOperationTypeToEventCodeMap();

        try {
            targetReader.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class SingletonHelper {
        private static final ActiveDirectoryOperationTypeMapping INSTANCE = new ActiveDirectoryOperationTypeMapping();
    }

    public static ActiveDirectoryOperationTypeMapping getInstance() {
        return ActiveDirectoryOperationTypeMapping.SingletonHelper.INSTANCE;
    }


    public Map<String, List<String>> getOperationTypeToCategoryMap() {
        return operationTypeToCategoryMap;
    }

    public Map<Integer, String> getEventCodeMapToOperationTypeMap() {
        return eventCodeMapToOperationTypeMap;
    }

    public Optional<Integer> getEventCode(String operationType) {
        return eventCodeMapToOperationTypeMap.entrySet().parallelStream()
                .filter(map -> map.getValue().equalsIgnoreCase(operationType))
                .map(Map.Entry::getKey)
                .findAny();
    }


    private void setOperationTypeToCategoryMap() {
        JsonArray transformerList = gson.toJsonTree(root).getAsJsonObject().get("transformerList").getAsJsonArray();

        List<Map<String, Object>> transformerListObj = gson.fromJson(transformerList, ArrayList.class);

        List<Map<String, Object>> name = transformerListObj.stream()
                .filter(e -> e.get("name").toString().equals("operation_type-categories-according-to-operation-type"))
                .collect(Collectors.toList());

        JsonArray cases = gson.toJsonTree(name.get(0)).getAsJsonObject().get("cases").getAsJsonArray();

        List<Map<String, Object>> result = gson.fromJson(cases, ArrayList.class);

        Function<Object, JsonArray> objToJsonArray = o -> gson.toJsonTree(o).getAsJsonArray();
        Function<JsonArray, List<String>> jArrToList = jarr -> gson.fromJson(jarr, ArrayList.class);

        operationTypeToCategoryMap = result.parallelStream()
                .collect(Collectors.toMap(e -> e.get("caseKey").toString(), e -> objToJsonArray.andThen(jArrToList).apply(e.get("caseValue"))));

        assertThat(operationTypeToCategoryMap).hasSizeGreaterThan(2);
    }

    private void setOperationTypeToEventCodeMap() {
        JsonArray transformerList = gson.toJsonTree(root).getAsJsonObject().get("transformerList").getAsJsonArray();

        List<Map<String, Object>> transformerListObj = gson.fromJson(transformerList, ArrayList.class);

        List<Map<String, Object>> name = transformerListObj.stream()
                .filter(e -> e.get("name").toString().equals("operation_type-according-to-event-code"))
                .collect(Collectors.toList());

        JsonArray cases = gson.toJsonTree(name.get(0)).getAsJsonObject().get("cases").getAsJsonArray();

        List<Map<String, Object>> result = gson.fromJson(cases, ArrayList.class);

        eventCodeMapToOperationTypeMap = result.parallelStream()
                .collect(Collectors.toMap(e -> Integer.parseInt(e.get("caseKey").toString()), e -> e.get("caseValue").toString()));
        assertThat(eventCodeMapToOperationTypeMap).hasSizeGreaterThan(3);
    }


}
