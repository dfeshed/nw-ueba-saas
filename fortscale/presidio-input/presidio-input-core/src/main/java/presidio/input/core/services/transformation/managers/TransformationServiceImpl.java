package presidio.input.core.services.transformation.managers;

import fortscale.common.general.Schema;
import fortscale.utils.logging.Logger;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import presidio.input.core.services.impl.SchemaFactory;
import presidio.monitoring.aspect.annotations.NumberOfFilteredEvents;
import presidio.sdk.api.domain.AbstractInputDocument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class TransformationServiceImpl implements TransformationService {
    private static final Logger logger = Logger.getLogger(TransformationServiceImpl.class);

    @Autowired
    private SchemaFactory schemaFactory;

    //@NumberOfFilteredEvents
    @Override
    public List<AbstractInputDocument> run(List<AbstractInputDocument> events, Schema schema) {
        TransformationManager transformationManager = schemaFactory.getTransformationManager(String.format("%s.%s", schema.toString(), "transformer"));

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
