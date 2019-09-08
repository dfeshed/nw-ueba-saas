package presidio.input.core.services.transformation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fortscale.common.general.Schema;
import fortscale.utils.logging.Logger;
import fortscale.utils.transform.IJsonObjectTransformer;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import presidio.input.core.services.impl.SchemaFactory;
import presidio.input.core.services.transformation.managers.TransformationManager;
import presidio.monitoring.aspect.annotations.NumberOfFilteredEvents;
import presidio.sdk.api.domain.AbstractInputDocument;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TransformationServiceImpl implements TransformationService {
    private static final Logger logger = Logger.getLogger(TransformationServiceImpl.class);

    private ObjectMapper mapper;

    @Autowired
    private SchemaFactory schemaFactory;

    public TransformationServiceImpl() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @NumberOfFilteredEvents
    @Override
    public List<AbstractInputDocument> run(List<AbstractInputDocument> events, Schema schema, Instant endDate, List<IJsonObjectTransformer> transformers) {
        String transformationManagerName = String.format("%s.%s", schema.toString(), "transformer");
        TransformationManager transformationManager = schemaFactory.getTransformationManager(transformationManagerName);

        transformers = transformers == null ? Collections.emptyList() : transformers;
        List<AbstractInputDocument> transformedEvents = new ArrayList<>(events.size());

        for (AbstractInputDocument event : events) {
            try {
                transformers.forEach(transformer -> {
                    try {
                        JSONObject jsonObj = new JSONObject(mapper.writeValueAsString(event));
                        transformer.transform(jsonObj);
                        AbstractInputDocument rawEvent = mapper.readValue(jsonObj.toString(), event.getClass());
                        AbstractInputDocument transformedEvent = transformationManager.getTransformedDocument(rawEvent);
                        transformedEvents.add(transformedEvent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                logger.error(String.format("Exception caught while transforming event: %s.", event.toString()), e);
            }
        }
        return transformedEvents;
    }
}