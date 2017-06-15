package fortscale.utils.shell.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.logging.Logger;
import org.springframework.shell.core.CommandMarker;

/**
 * Shell commands
 * Created by barak_schuster on 7/18/16.
 */
public abstract class FortscaleCommandMarker implements CommandMarker {

    private static final Logger logger = Logger.getLogger(FortscaleCommandMarker.class);


    /**
     * converts object to json
     *
     * @param obj - object to be converted
     * @return json with date iso-formatted
     * @throws JsonProcessingException
     */
    public static String objectToDateFormattedJsonString(Object obj) throws JsonProcessingException {
        String result;
//        ObjectMapper mapper = JacksonCreators.newStandardObjectMapper();
        ObjectMapper mapper = new ObjectMapper();
        try {
            result = mapper.writeValueAsString(obj);
            return result;
        } catch (Exception e) {
            logger.error("failed to parse and log command output as json", e);
            throw e;
        }
    }
}
