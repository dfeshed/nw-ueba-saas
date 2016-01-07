package fortscale.aggregation.configuration;

import fortscale.utils.logging.Logger;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by YaronDL on 1/6/2016.
 */
public abstract class AslConfigurationService implements InitializingBean, ApplicationContextAware {
    private static final Logger logger = Logger.getLogger(AslConfigurationService.class);

    private ApplicationContext applicationContext;

    protected abstract String getBaseConfJsonFilePath();
    protected abstract String getBaseOverridingConfJsonFolderPath();
    protected abstract String getAdditionalConfJsonFolderPath();
    protected abstract String getConfNodeName();
    protected abstract void loadConfJson(JSONObject confJsonObject);

    @Override
    public void afterPropertiesSet() throws Exception {
        loadConfs();
    }

    private void loadConfs() throws IllegalArgumentException  {
        loadBaseConfs();
        loadAdditionalConfs();
    }

    private void loadBaseConfs() throws IllegalArgumentException  {
        List<InputStream> confJsonInputStreams = new ArrayList<>();

        Resource[] confJsonResources = null;
        if(getBaseOverridingConfJsonFolderPath() != null) {
            try {
                confJsonResources = applicationContext.getResources(getBaseOverridingConfJsonFolderPath());
            } catch (Exception e) {
            }
        }

        boolean isBaseOverridingExists = !(confJsonResources == null || confJsonResources.length == 0);

        //load overriding base configuration if exist
        if(isBaseOverridingExists){
            for(Resource confJsonResource: confJsonResources) {
                try {
                    confJsonInputStreams.add(confJsonResource.getInputStream());

                } catch (Exception e) {
                    String errorMsg = String.format("Failed to open json file %s", confJsonResource.getFilename());
                    logger.error(errorMsg, e);
                    throw new IllegalArgumentException(errorMsg, e);
                }
            }
        } else{
            //if base overriding configuration does not exist
            try {
                Resource confJsonResource = applicationContext.getResource(getBaseConfJsonFilePath());
                confJsonInputStreams.add(confJsonResource.getInputStream());
            } catch (Exception e) {
                String errorMsg = String.format("Failed to open json file %s", getBaseConfJsonFilePath());
                logger.error(errorMsg, e);
                throw new IllegalArgumentException(errorMsg, e);
            }
        }

        try{
            for(InputStream inputStream: confJsonInputStreams) {
                loadConfInputStream(inputStream);
            }
        } catch (Exception e) {
            String errorMsg = String.format("Failed to load Configuration from json path %s", isBaseOverridingExists ? getBaseOverridingConfJsonFolderPath() : getBaseConfJsonFilePath());
            logger.error(errorMsg, e);
            throw new IllegalArgumentException(errorMsg, e);
        }
    }

    private void loadAdditionalConfs() throws IllegalArgumentException  {
        if(getAdditionalConfJsonFolderPath() == null){
            return;
        }

        List<InputStream> confJsonInputStreams = new ArrayList<>();

        Resource[] confJsonResources = null;
        try {
            confJsonResources = applicationContext.getResources(getAdditionalConfJsonFolderPath());
        } catch (Exception e) {}

        if(confJsonResources != null && confJsonResources.length > 0){
            for(Resource confJsonResource: confJsonResources) {
                try {
                    confJsonInputStreams.add(confJsonResource.getInputStream());

                } catch (Exception e) {
                    String errorMsg = String.format("Failed to open json file %s", confJsonResource.getFilename());
                    logger.error(errorMsg, e);
                    throw new IllegalArgumentException(errorMsg, e);
                }
            }
        }

        try {
            for(InputStream inputStream: confJsonInputStreams) {
                loadConfInputStream(inputStream);
            }
        } catch (Exception e) {
            String errorMsg = String.format("Failed to load Configuration from json folder path %s", getAdditionalConfJsonFolderPath());
            logger.error(errorMsg, e);
            throw new IllegalArgumentException(errorMsg, e);
        }
    }

    private void loadConfInputStream(InputStream inputStream) throws IOException, ParseException {
        JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(inputStream);

        if(jsonObj.get(getConfNodeName()) instanceof JSONArray) {
            JSONArray confsJson = (JSONArray) jsonObj.get(getConfNodeName());

            for (Object obj : confsJson) {
                loadConfJson((JSONObject) obj);
            }
        } else{
            loadConfJson((JSONObject) jsonObj.get(getConfNodeName()));
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
