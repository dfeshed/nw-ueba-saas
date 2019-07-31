package com.rsa.netwitness.presidio.automation.mapping.operation_type;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;
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

        Gson gson = new Gson();
        Map<String, String> root = gson.fromJson(Objects.requireNonNull(reader), HashMap.class);
        mapping = gson.toJsonTree(root.get("mapping"));
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
