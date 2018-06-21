package fortscale.utils.transform;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.Validate;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class JsonObjectChainTransformer extends AbstractJsonObjectTransformer {
    private static final Logger logger = LoggerFactory
            .getLogger(JsonObjectChainTransformer.class);

    public static final String TYPE = "chain";

    private List<IJsonObjectTransformer> transformerList;

    @JsonCreator
    public JsonObjectChainTransformer(@JsonProperty("name") String name,
                                      @JsonProperty("transformerList") List<IJsonObjectTransformer> transformerList){
        super(name);

        this.transformerList = Validate.notEmpty(transformerList, "transformerList cannot be empty or null.");
    }

    @Override
    public JSONObject transform(JSONObject jsonObject) {
        JSONObject ret = jsonObject;
        for(IJsonObjectTransformer transformer: transformerList){
            JSONObject input = ret; //This line is only for log purposes.
            ret = transformer.transform(input);
            if(ret == null){
                logger.debug("The transformer {} filtered the event: {}. The event after all the transformation: {}",
                        transformer.getName(), jsonObject, input);
                return null;
            }
        }

        return ret;
    }
}
