package fortscale.ml.scorer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.ml.model.ModelConf;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

import java.util.*;

public class ScorerConfService implements ApplicationContextAware, InitializingBean {

    private static final Logger logger = Logger.getLogger(ScorerConfService.class);
    private static final String DATA_SOURCE_SCORERS_NODE_NAME = "data-source-scorers";

    @Value("${fortscale.scorer.configurations.location.path}")
    private String scorerConfigurationsLocationPath;

    private ApplicationContext applicationContext;
    private Map<String, DataSourceScorerConfs> dataSourceToDataSourceScorerConfs;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        dataSourceToDataSourceScorerConfs = new HashMap<>();
        loadDataSourceScorerConfs();
    }

    public DataSourceScorerConfs getDataSourceScorerConfs(String dataSource) {
        return dataSourceToDataSourceScorerConfs.get(dataSource);
    }

    public Map<String, DataSourceScorerConfs> getAllDataSourceScorerConfs() {
        return dataSourceToDataSourceScorerConfs;
    }


    private void loadDataSourceScorerConfs() {
        String errorMsg;
        List<Object> listOfDataSourceConfJSONsLists = getDataSourceScorerConfsFromAllResources();
        ObjectMapper objectMapper = new ObjectMapper();

        for (Object dataSourceScorerConfsJSON : listOfDataSourceConfJSONsLists) {
            String jsonString = ((JSONObject)dataSourceScorerConfsJSON).toJSONString();

            try {
                DataSourceScorerConfs dataSourceScorerConfs = objectMapper.readValue(jsonString, DataSourceScorerConfs.class);
                String dataSource = dataSourceScorerConfs.getDataSource();

                if (dataSourceToDataSourceScorerConfs.containsKey(dataSource)) {
                    errorMsg = String.format(
                            "All scorers configuration for specific data-source must be in the same file. %s scorer configurations appear multiple times.",
                            dataSource);
                    logger.error(errorMsg);
                    throw new IllegalArgumentException(errorMsg);
                }

                dataSourceToDataSourceScorerConfs.put(dataSource, dataSourceScorerConfs);
            } catch (Exception e) {
                errorMsg = String.format("Failed to deserialize data-source scorer confs JSON %s.", jsonString);
                logger.error(errorMsg, e);
                throw new IllegalArgumentException(errorMsg, e);
            }
        }
    }

    private List<Object> getDataSourceScorerConfsFromAllResources() {
        String errorMsg;
        List<Resource> resources;
        List<Object> listOfdataSourceScorerConfsLists = new ArrayList<>();

        try {
            resources = Arrays.asList(applicationContext
                    .getResources(scorerConfigurationsLocationPath.concat("/*.json")));
        } catch (Exception e) {
            errorMsg = String.format(
                    "Failed to get scorer confs resources from location path %s.",
                    scorerConfigurationsLocationPath);
            logger.error(errorMsg, e);
            throw new IllegalArgumentException(errorMsg, e);
        }

        for (Resource resource : resources) {
            listOfdataSourceScorerConfsLists.addAll(getDataSourceScorerConfsFromResource(resource));
        }

        return listOfdataSourceScorerConfsLists;
    }

    private static JSONArray getDataSourceScorerConfsFromResource(Resource resource) {
        String errorMsg;
        JSONArray dataSourceScorerConfs;

        try {
            JSONObject json = (JSONObject) JSONValue.parseWithException(resource.getInputStream());
            dataSourceScorerConfs = (JSONArray)json.get(DATA_SOURCE_SCORERS_NODE_NAME);
        } catch (Exception e) {
            errorMsg = String.format(
                    "Failed to parse scorer confs JSON file %s.",
                    resource.getFilename());
            logger.error(errorMsg, e);
            throw new IllegalArgumentException(errorMsg, e);
        }

        if (dataSourceScorerConfs == null) {
            errorMsg = String.format(
                    "Scorer confs JSON file %s does not contain field %s.",
                    resource.getFilename(), DATA_SOURCE_SCORERS_NODE_NAME);
            logger.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }

        return dataSourceScorerConfs;
    }
}
