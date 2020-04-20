package presidio.input.core.services.transformation.managers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import presidio.sdk.api.domain.AbstractInputDocument;

import java.io.IOException;

public interface TransformationManager {
    <U extends AbstractInputDocument> U getTransformedDocument(ObjectMapper objectMapper, JSONObject jsonObject) throws IOException;
}
