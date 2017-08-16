package presidio.input.core.services.transformation.managers;

import presidio.input.core.services.transformation.PatternReplacementTransformer;
import presidio.input.core.services.transformation.Transformer;
import presidio.sdk.api.domain.AbstractPresidioDocument;
import presidio.sdk.api.domain.rawevents.AuthenticationRawEvent;
import presidio.sdk.api.domain.transformedevents.AuthenticationTransformedEvent;

import java.util.ArrayList;
import java.util.List;

public class AuthenticationTransformerManager implements TransformationManager {

    public static final String CLUSTER_REPLACEMENT_PATTERN = "[0-9]";
    public static final String CLUSTER_POST_REPLACEMENT_CONDITION = "(.*[a-zA-Z]){5}.*";
    private List<Transformer> transformers;

    @Override
    public List<Transformer> getTransformers() {
        if (transformers == null) {
            transformers = new ArrayList<>();
            transformers.add(new PatternReplacementTransformer(AuthenticationRawEvent.SRC_MACHINE_NAME_FIELD_NAME,
                    AuthenticationTransformedEvent.SRC_MACHINE_CLUSTER_FIELD_NAME, CLUSTER_REPLACEMENT_PATTERN, "", null, CLUSTER_POST_REPLACEMENT_CONDITION));

            transformers.add(new PatternReplacementTransformer(AuthenticationRawEvent.DST_MACHINE_NAME_FIELD_NAME,
                    AuthenticationTransformedEvent.DST_MACHINE_CLUSTER_FIELD_NAME, CLUSTER_REPLACEMENT_PATTERN, "", null, CLUSTER_POST_REPLACEMENT_CONDITION));
        }
        return transformers;
    }

    @Override
    public <U extends AbstractPresidioDocument> U getTransformedDocument(AbstractPresidioDocument rawEvent) {
        return (U) new AuthenticationTransformedEvent((AuthenticationRawEvent) rawEvent);
    }
}
