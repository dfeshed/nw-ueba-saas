package presidio.input.core.services.transformation.managers;

import presidio.input.core.services.transformation.transformer.JoinTransformer;
import presidio.input.core.services.transformation.transformer.Transformer;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.rawevents.RegistryRawEvent;
import presidio.sdk.api.domain.transformedevents.RegistryTransformedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegistryTransformerManager implements TransformationManager {

    private List<Transformer> transformers;

    @Override
    public List<Transformer> getTransformers() {
        if (transformers == null) {
            transformers = new ArrayList<>();
            transformers.add(new JoinTransformer(Arrays.asList(RegistryRawEvent.PROCESS_DIRECTORY_FIELD_NAME, RegistryRawEvent.PROCESS_FILE_NAME_FIELD_NAME),RegistryTransformedEvent.PROCESS_FILE_PATH_FIELD_NAME,"\\"));
        }
        return transformers;
    }

    @Override
    public <U extends AbstractInputDocument> U getTransformedDocument(AbstractInputDocument rawEvent) {
        return (U) new RegistryTransformedEvent((RegistryRawEvent) rawEvent);
    }
}