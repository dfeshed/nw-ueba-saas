package presidio.input.core.services.transformation;

import fortscale.common.general.Schema;
import presidio.sdk.api.domain.AbstractInputDocument;

import java.time.Instant;
import java.util.List;

public interface TransformationService {
    List<AbstractInputDocument> run(List<AbstractInputDocument> events, Schema schema, Instant endDate);
}
