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
public class CustomDateDeserializer extends JsonDeserializer<DateTime> {

    @Override
    public DateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        DateTime result;
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        if (node.has("millis")) {
            result = new DateTime(node.get("millis").asLong());
        } else if (node.has("$date")) {
            result = new DateTime(node.get("$date").asText());
        } else {
            //default - return today's date
            result = new DateTime();
        }
        return result;
    }

}