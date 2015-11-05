package fortscale.ml.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.logging.Logger;
import groovy.json.JsonException;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ModelConfService implements InitializingBean {
    private static final String MODEL_CONFS_JSON_FIELD_NAME = "ModelConfs";
    private static final Logger logger = Logger.getLogger(ModelConfService.class);

    @Value("${fortscale.model.configurations.json.file.path}")
    private String modelConfigurationsJSONFilePath;

    private List<ModelConf> modelConfs;

    @Override
    public void afterPropertiesSet() throws Exception {
        modelConfs = new ArrayList<>();
        loadModelDefinitions(modelConfigurationsJSONFilePath);
    }

    public List<ModelConf> getModelConfs() {
        return modelConfs;
    }

    private void loadModelDefinitions(String JSONFilePath) {
        JSONArray modelConfJSONs;
        String errorMsg;

        try {
            JSONObject json = (JSONObject) JSONValue.parseWithException(new FileReader(JSONFilePath));
            modelConfJSONs = (JSONArray) json.get(MODEL_CONFS_JSON_FIELD_NAME);
        } catch (Exception e) {
            errorMsg = String.format("Failed to parse JSON file %s", JSONFilePath);
            logger.error(errorMsg, e);
            throw new JsonException(errorMsg, e);
        }

        if (modelConfJSONs == null) {
            errorMsg = String.format("JSON file %s does not contain field %s", JSONFilePath, MODEL_CONFS_JSON_FIELD_NAME);
            logger.error(errorMsg);
            throw new JsonException(errorMsg);
        }

        for (Object modelConfJSON : modelConfJSONs) {
            String jsonString = ((JSONObject) modelConfJSON).toJSONString();
            try {
                ModelConf modelConf = (new ObjectMapper()).readValue(jsonString, ModelConf.class);
                modelConfs.add(modelConf);
            } catch (IOException e) {
                errorMsg = String.format("Failed to deserialize JSON %s", jsonString);
                logger.error(errorMsg, e);
                throw new RuntimeException(errorMsg, e);
            }
        }
    }
}
