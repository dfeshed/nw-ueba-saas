package fortscale.domain;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.joda.time.DateTime;

import java.io.IOException;

/**
 * Created by Amir Keren on 24/11/2015.
 */
public class CustomDateSerializer extends JsonDeserializer<DateTime> {

    @Override
    public DateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        return new DateTime();
    }

}