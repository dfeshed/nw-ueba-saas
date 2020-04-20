package presidio.input.core.services.transformation.managers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.transformedevents.ProcessTransformedEvent;

import java.io.IOException;

public class ProcessTransformerManager implements TransformationManager {

    @Override
    @SuppressWarnings("unchecked")
    public <U extends AbstractInputDocument> U getTransformedDocument(ObjectMapper objectMapper, JSONObject jsonObject) throws IOException {
        return (U) objectMapper.readValue(jsonObject.toString(), ProcessTransformedEvent.class);
    }
}