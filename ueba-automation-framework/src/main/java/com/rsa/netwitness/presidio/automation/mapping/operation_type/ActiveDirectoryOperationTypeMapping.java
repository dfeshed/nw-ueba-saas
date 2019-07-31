package com.rsa.netwitness.presidio.automation.mapping.operation_type;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.stream.JsonReader;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;
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
    private Map<String, Integer> operationTypeToEventCodeMap;

    private ActiveDirectoryOperationTypeMapping() {
        LOGGER.debug("Going to load resource: " + RESOURCE_NAME);
        URL resource = this.getClass()
                .getClassLoader()
                .getResource(RESOURCE_NAME);

        assertThat(resource).isNotNull();

        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader(resource.getFile()));
        } catch (FileNotFoundException e) {
            LOGGER.error(e.getMessage());
        }

        root = gson.fromJson(Objects.requireNonNull(reader), HashMap.class);
        assertThat(root).isNotNull().isNotEmpty();
        setOperationTypeToCategoryMap();
        setOperationTypeToEventCodeMap();
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

    public Map<String, Integer> getOperationTypeToEventCodeMap() {
        return operationTypeToEventCodeMap;
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

        operationTypeToEventCodeMap = result.parallelStream()
                .collect(Collectors.toMap(e -> e.get("caseValue").toString(), e -> Integer.parseInt(e.get("caseKey").toString())));
        assertThat(operationTypeToEventCodeMap).hasSizeGreaterThan(3);
    }


}
