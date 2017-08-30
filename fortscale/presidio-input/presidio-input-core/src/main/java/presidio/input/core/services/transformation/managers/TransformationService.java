package presidio.input.core.services.transformation.managers;

import fortscale.common.general.Schema;
import presidio.sdk.api.domain.AbstractInputDocument;

import java.util.List;

public interface TransformationService {
    List<AbstractInputDocument> run(List<AbstractInputDocument> events, Schema schema);
}
