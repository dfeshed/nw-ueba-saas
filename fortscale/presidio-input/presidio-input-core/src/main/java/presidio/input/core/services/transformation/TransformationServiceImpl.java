package presidio.input.core.services.transformation;

import fortscale.common.general.Schema;
import fortscale.utils.logging.Logger;
import fortscale.utils.transform.AbstractJsonObjectTransformer;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import presidio.input.core.services.impl.SchemaFactory;
import presidio.input.core.services.transformation.managers.TransformationManager;
import presidio.monitoring.aspect.annotations.NumberOfFilteredEvents;
import presidio.sdk.api.domain.AbstractInputDocument;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TransformationServiceImpl implements TransformationService {
    private static final Logger logger = Logger.getLogger(TransformationServiceImpl.class);

    @Autowired
    private SchemaFactory schemaFactory;

    @NumberOfFilteredEvents
    @Override
    public List<AbstractInputDocument> run(List<AbstractInputDocument> events, Schema schema, Instant endDate, List<AbstractJsonObjectTransformer> transformers) {
        String transformationManagerName = String.format("%s.%s", schema.toString(), "transformer");

//        todo: remove after event will be jsonObject
        TransformationManager transformationManager = schemaFactory.getTransformationManager(transformationManagerName);

        transformers = transformers == null ? Collections.emptyList() : transformers;
        List<AbstractInputDocument> transformedEvents = new ArrayList<>(events.size());

        for (AbstractInputDocument event : events) {
            try {
                //todo: remove after event will be jsonObject
                AbstractInputDocument transformedEvent = transformationManager.getTransformedDocument(event);

                transformers.forEach(transformer -> {

                    //todo: temporary
                    JSONObject jsonObj = new JSONObject(transformedEvent);
                    transformer.transform(jsonObj);
                });
                transformedEvents.add(transformedEvent);

            } catch (Exception e) {
                logger.error(String.format("Exception caught while transforming event: %s.", event.toString()), e);
            }
        }

        return transformedEvents;
    }
}