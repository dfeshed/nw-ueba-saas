package presidio.input.core.services.transformation.managers;

import fortscale.common.general.Schema;
import org.springframework.context.ApplicationContext;
import presidio.input.core.services.transformation.transformer.Transformer;
import presidio.sdk.api.domain.AbstractInputDocument;

import java.time.Instant;
import java.util.List;

public interface TransformationManager {

    default void init(Instant endDate){}

    List<Transformer> getTransformers();

    <U extends AbstractInputDocument> U getTransformedDocument(AbstractInputDocument rawEvent);
}
