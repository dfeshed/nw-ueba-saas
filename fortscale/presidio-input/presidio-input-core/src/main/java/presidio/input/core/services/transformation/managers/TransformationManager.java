package presidio.input.core.services.transformation.managers;

import presidio.input.core.services.transformation.transformer.Transformer;
import presidio.sdk.api.domain.AbstractInputDocument;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public interface TransformationManager {
    default void init(Instant workflowStartDate, Instant intervalEndDate, Duration transformationWaitingDuration) {}

    List<Transformer> getTransformers();

    <U extends AbstractInputDocument> U getTransformedDocument(AbstractInputDocument rawEvent);
}
