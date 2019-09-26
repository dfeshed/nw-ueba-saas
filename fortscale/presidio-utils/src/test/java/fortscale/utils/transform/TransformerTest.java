package fortscale.utils.transform;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.Before;

import java.util.Optional;

public class TransformerTest extends TransformerSubtypeRegisterer {

    protected ObjectMapper mapper;

    @Before
    public void init(){
        mapper = new ObjectMapper();
        registerSubtypes(mapper);
    }

    protected JSONObject transform(IJsonObjectTransformer transformer, JSONObject jsonObject) throws JsonProcessingException {
        return TransformerUtil.transform(mapper, transformer, jsonObject);
    }

    protected JSONObject transform(IJsonObjectTransformer transformer, JSONObject jsonObject, boolean isFilteredOut) throws JsonProcessingException {
        return TransformerUtil.transform(mapper, transformer, jsonObject, isFilteredOut);
    }

    protected JSONObject transform(String transformerJsonAsString, JSONObject jsonObject) {
        return TransformerUtil.transform(mapper, transformerJsonAsString, jsonObject);
    }

    @Override
    public Optional<String> additionalPackageLocation() {
        return Optional.empty();
    }
}
