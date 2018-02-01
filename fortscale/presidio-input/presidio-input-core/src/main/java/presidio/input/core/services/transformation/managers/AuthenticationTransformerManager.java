package presidio.input.core.services.transformation.managers;

import fortscale.common.general.Schema;
import presidio.input.core.services.transformation.transformer.MachineNameTransformer;
import presidio.input.core.services.transformation.transformer.OperationTypeCategoryTransformer;
import presidio.input.core.services.transformation.transformer.PatternReplacementTransformer;
import presidio.input.core.services.transformation.transformer.Transformer;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.rawevents.AuthenticationRawEvent;
import presidio.sdk.api.domain.transformedevents.AuthenticationTransformedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AuthenticationTransformerManager implements TransformationManager {

    public static final String CLUSTER_REPLACEMENT_PATTERN = "[0-9]";
    public static final String CLUSTER_POST_REPLACEMENT_CONDITION = "(.*[a-zA-Z]){5}.*";
    private List<Transformer> transformers;
    private Map<Schema, Map<String, List<String>>> operationTypeToCategoryMapping;

    public AuthenticationTransformerManager(Map<Schema, Map<String, List<String>>> operationTypeToCategoryMapping) {
        this.operationTypeToCategoryMapping = operationTypeToCategoryMapping;
    }

    @Override
    public List<Transformer> getTransformers() {
        if (transformers == null) {
            transformers = new ArrayList<>();

            //src\dst machine name transformer:
            //machine name containing ip address will be transformed to empty string,
            //and value of resolved machine name will be transformed according to the cluster regex
            transformers.add(new MachineNameTransformer(AuthenticationRawEvent.SRC_MACHINE_NAME_FIELD_NAME,
                    AuthenticationTransformedEvent.SRC_MACHINE_CLUSTER_FIELD_NAME, CLUSTER_REPLACEMENT_PATTERN, "", null, CLUSTER_POST_REPLACEMENT_CONDITION));
            transformers.add(new MachineNameTransformer(AuthenticationRawEvent.DST_MACHINE_NAME_FIELD_NAME,
                    AuthenticationTransformedEvent.DST_MACHINE_CLUSTER_FIELD_NAME, CLUSTER_REPLACEMENT_PATTERN, "", null, CLUSTER_POST_REPLACEMENT_CONDITION));

            //src\dst machine id transformers:
            //machine id containing ip address will be transformed to empty string
            //and value of resolved machine id will kept as is
            transformers.add(new PatternReplacementTransformer(AuthenticationRawEvent.SRC_MACHINE_ID_FIELD_NAME,
                    AuthenticationTransformedEvent.SRC_MACHINE_ID_FIELD_NAME, MachineNameTransformer.ipPattern.pattern(), "", null, null));
            transformers.add(new PatternReplacementTransformer(AuthenticationRawEvent.DST_MACHINE_ID_FIELD_NAME,
                    AuthenticationTransformedEvent.DST_MACHINE_ID_FIELD_NAME, MachineNameTransformer.ipPattern.pattern(), "", null, null));

            transformers.add(new OperationTypeCategoryTransformer(operationTypeToCategoryMapping.get(Schema.AUTHENTICATION.toString())));
        }
        return transformers;
    }

    @Override
    public <U extends AbstractInputDocument> U getTransformedDocument(AbstractInputDocument rawEvent) {
        return (U) new AuthenticationTransformedEvent((AuthenticationRawEvent) rawEvent);
    }
}
