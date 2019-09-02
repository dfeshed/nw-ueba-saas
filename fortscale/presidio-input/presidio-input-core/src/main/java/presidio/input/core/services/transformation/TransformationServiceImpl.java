package presidio.input.core.services.transformation;

import fortscale.common.general.Schema;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import presidio.input.core.services.impl.SchemaFactory;
import presidio.input.core.services.transformation.managers.TransformationManager;
import presidio.input.core.services.transformation.transformer.Transformer;
import presidio.monitoring.aspect.annotations.NumberOfFilteredEvents;
import presidio.sdk.api.domain.AbstractInputDocument;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TransformationServiceImpl implements TransformationService {
    private static final Logger logger = Logger.getLogger(TransformationServiceImpl.class);

    @Autowired
    private SchemaFactory schemaFactory;

    @Value("#{T(java.time.Instant).parse('${dataPipeline.startTime}')}")
    private Instant workflowStartDate;

    @Value("#{T(java.time.Duration).parse('${presidio.input.core.transformation.waiting.duration:P10D}')}")
    private Duration transformationWaitingDuration;

    @NumberOfFilteredEvents
    @Override
    public List<AbstractInputDocument> run(List<AbstractInputDocument> events, Schema schema, Instant endDate) {
        String transformationManagerName = String.format("%s.%s", schema.toString(), "transformer");
        TransformationManager transformationManager = schemaFactory.getTransformationManager(transformationManagerName);
        transformationManager.init(workflowStartDate, endDate, transformationWaitingDuration);
        List<Transformer> transformers = transformationManager.getTransformers();
        transformers = transformers == null ? Collections.emptyList() : transformers;
        List<AbstractInputDocument> transformedEvents = new ArrayList<>(events.size());

        for (AbstractInputDocument event : events) {
            try {
                AbstractInputDocument transformedEvent = transformationManager.getTransformedDocument(event);
                List<AbstractInputDocument> singletonTransformedEvent = Collections.singletonList(transformedEvent);
                transformers.forEach(transformer -> transformer.transform(singletonTransformedEvent));
                transformedEvents.addAll(singletonTransformedEvent);
            } catch (Exception e) {
                logger.error(String.format("Exception caught while transforming event: %s.", event.toString()), e);
            }
        }

        return transformedEvents;
    }
}
