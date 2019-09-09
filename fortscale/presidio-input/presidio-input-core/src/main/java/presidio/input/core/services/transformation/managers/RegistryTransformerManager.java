package presidio.input.core.services.transformation.managers;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.transformedevents.RegistryTransformedEvent;

import java.io.IOException;

public class RegistryTransformerManager implements TransformationManager {

    @Override
    @SuppressWarnings("unchecked")
    public <U extends AbstractInputDocument> U getTransformedDocument(ObjectMapper objectMapper, JSONObject jsonObject) throws IOException {
        return (U) objectMapper.readValue(jsonObject.toString(), RegistryTransformedEvent.class);
    }
}