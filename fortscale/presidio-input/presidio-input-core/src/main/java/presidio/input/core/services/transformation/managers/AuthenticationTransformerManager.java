package presidio.input.core.services.transformation.managers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.transformedevents.AuthenticationTransformedEvent;

import java.io.IOException;

public class AuthenticationTransformerManager implements TransformationManager {

    @Override
    @SuppressWarnings("unchecked")
    public <U extends AbstractInputDocument> U getTransformedDocument(ObjectMapper objectMapper, JSONObject jsonObject) throws IOException {
        return (U) objectMapper.readValue(jsonObject.toString(), AuthenticationTransformedEvent.class);
    }
}
