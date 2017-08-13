package presidio.input.core.services.transformation.managers;

import org.springframework.stereotype.Component;
import presidio.input.core.services.transformation.PatternReplacementTransformer;
import presidio.sdk.api.domain.AbstractPresidioDocument;
import presidio.sdk.api.domain.rawevents.AuthenticationRawEvent;
import presidio.sdk.api.domain.transformedevents.AuthenticationTransformedEvent;

@Component("AUTHENTICATION")
public class AuthenticationTransformerManager extends TransformationManager {

    public AuthenticationTransformerManager() {
        super();
        transformers.add(new PatternReplacementTransformer(AuthenticationRawEvent.SRC_MACHINE_NAME_FIELD_NAME,
                AuthenticationTransformedEvent.SRC_MACHINE_CLUSTER_FIELD_NAME, "[0-9]", "", null, "(.*[a-zA-Z]){5}.*"));

        transformers.add(new PatternReplacementTransformer(AuthenticationRawEvent.DST_MACHINE_NAME_FIELD_NAME,
                AuthenticationTransformedEvent.DST_MACHINE_CLUSTER_FIELD_NAME, "[0-9]", "", null, "(.*[a-zA-Z]){5}.*"));
    }

    @Override
    protected <U extends AbstractPresidioDocument> U getTransformedDocument(AbstractPresidioDocument rawEvent) {
        return (U) new AuthenticationTransformedEvent((AuthenticationRawEvent) rawEvent);
    }
}
