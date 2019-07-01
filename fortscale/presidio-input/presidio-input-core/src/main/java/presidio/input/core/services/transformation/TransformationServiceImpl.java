package presidio.input.core.services.transformation;

import fortscale.common.general.Schema;
import fortscale.utils.factory.FactoryService;
import fortscale.utils.logging.Logger;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import presidio.input.core.services.impl.SchemaFactory;
import presidio.input.core.services.transformation.managers.TransformationManager;
import presidio.input.core.services.transformation.transformer.Transformer;
import presidio.monitoring.aspect.annotations.NumberOfFilteredEvents;
import presidio.sdk.api.domain.AbstractInputDocument;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class TransformationServiceImpl implements TransformationService {
    private static final Logger logger = Logger.getLogger(TransformationServiceImpl.class);

    @Autowired
    private SchemaFactory schemaFactory;

    @NumberOfFilteredEvents
    @Override
    public List<AbstractInputDocument> run(List<AbstractInputDocument> events, Schema schema, Instant endDate) {
        TransformationManager transformationManager = schemaFactory.getTransformationManager(String.format("%s.%s", schema.toString(), "transformer"));

        transformationManager.init(endDate);
        List<AbstractInputDocument> result = new ArrayList<>();

        events.forEach(event -> {
            try {
                AbstractInputDocument transformedDocument = transformationManager.getTransformedDocument(event);
                List<AbstractInputDocument> transformedDocuments = Arrays.asList(transformedDocument);
                if (CollectionUtils.isEmpty(transformationManager.getTransformers())) {
                    result.add(transformedDocument);
                } else {
                    transformationManager.getTransformers().forEach(transformer -> {
                        transformer.transform(transformedDocuments);
                    });
                    result.addAll(transformedDocuments);
                }
            } catch (Exception e) {
                logger.error(String.format("Error transforming event - %s", event.toString()), e);
            }
        });

        return result;
    }
}
