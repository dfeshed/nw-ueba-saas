package presidio.input.core.services.transformation.managers;

import org.springframework.stereotype.Component;
import presidio.sdk.api.domain.AbstractPresidioDocument;

@Component("ACTIVE_DIRECTORY")
public class ActiveDirectoryTransformationManager extends TransformationManager {

    @Override
    protected <U extends AbstractPresidioDocument> U getTransformedDocument(AbstractPresidioDocument rawEvent) {
        return (U) rawEvent;
    }
}
