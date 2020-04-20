package com.rsa.netwitness.presidio.automation.mapping.operation_type;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class OperationTypeToCategories {

    private static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(OperationTypeToCategories.class.getName());

    private final String RESOURCE_NAME = "operation-type-category-mapping.json";
    private final JsonElement mapping;

    private OperationTypeToCategories() {
        LOGGER.debug("Going to load resource: " + RESOURCE_NAME);

        InputStream resourceAsStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(RESOURCE_NAME);

        assertThat(resourceAsStream).as("Unable to get resource " + RESOURCE_NAME).isNotNull();
        Reader targetReader = new InputStreamReader(resourceAsStream);
        JsonReader reader = new JsonReader(targetReader);
        Gson gson = new Gson();
        Map<String, String> root = gson.fromJson(Objects.requireNonNull(reader), HashMap.class);
        mapping = gson.toJsonTree(root.get("mapping"));

        try {
            targetReader.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class SingletonHelper {
        private static final OperationTypeToCategories INSTANCE = new OperationTypeToCategories();
    }

    public static OperationTypeToCategories getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public Map<String, List<String>> getForActiveDirectory(){
        return getForSchema("ACTIVE_DIRECTORY");
    }

    public Map<String, List<String>> getForFile(){
        return getForSchema("FILE");
    }

    private Map<String, List<String>> getForSchema(String schema) {
        JsonObject obj = mapping.getAsJsonObject().get(schema).getAsJsonObject();
        Map<String, List<String>> result = new Gson().fromJson(obj, HashMap.class);
        return result;
    }

}
