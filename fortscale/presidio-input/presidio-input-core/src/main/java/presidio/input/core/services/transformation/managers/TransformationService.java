package presidio.input.core.services.transformation.managers;

import fortscale.common.general.Schema;
import presidio.sdk.api.domain.AbstractPresidioDocument;

import java.util.List;

public interface TransformationService {
    List<AbstractPresidioDocument> run(List<AbstractPresidioDocument> events, Schema schema);
}
