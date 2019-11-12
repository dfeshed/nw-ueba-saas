package presidio.output.processor.services.alert.indicator.enricher;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationContext;
import presidio.output.processor.services.alert.indicator.enricher.IndicatorEnricher.Implementable;

import java.io.IOException;

public class IndicatorEnricherJsonDeserializer extends JsonDeserializer<IndicatorEnricher> {
    private final ObjectMapper objectMapper;
    private final ApplicationContext applicationContext;

    public IndicatorEnricherJsonDeserializer(ObjectMapper objectMapper, ApplicationContext applicationContext) {
        this.objectMapper = objectMapper;
        this.applicationContext = applicationContext;
    }

    @Override
    public IndicatorEnricher deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {

        Implementable implementable = objectMapper.readValue(jsonParser, Implementable.class);
        implementable.setApplicationContext(applicationContext);
        return implementable;
    }
}
