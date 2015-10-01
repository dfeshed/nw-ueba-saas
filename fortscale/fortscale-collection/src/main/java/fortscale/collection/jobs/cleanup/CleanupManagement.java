package fortscale.collection.jobs.cleanup;

import fortscale.utils.logging.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.util.Map;

/**
 * Created by Amir Keren on 30/09/15.
 */
public class CleanupManagement implements InitializingBean {

    private static Logger logger = Logger.getLogger(CleanupManagement.class);

    @Value("${cleanup.steps}")
    private String cleanupStepsFile;

    private Map<String, CleanupStep> cleanupSteps;

    @Override
    public void afterPropertiesSet() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        File jsonFile;
        try {
            jsonFile = new File(cleanupStepsFile);
            if (!jsonFile.exists()) {
                throw new Exception();
            }
        } catch (Exception ex) {
            logger.error("Error reading cleanupSteps.json file {}", ex);
            throw new Exception();
        }
        try {
            cleanupSteps = mapper.readValue(jsonFile, new TypeReference<Map<String, CleanupStep>>(){});
        } catch (Exception ex) {
            logger.error("Error mapping cleanupSteps.json file to class {}", ex);
            throw new Exception();
        }
    }

    public CleanupStep getCleanupStep(String stepId) {
        return cleanupSteps.get(stepId);
    }

}