package presidio.input.core.services.transformation.managers;

import presidio.input.core.services.transformation.transformer.JoinTransformer;
import presidio.input.core.services.transformation.transformer.Transformer;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.rawevents.ProcessRawEvent;
import presidio.sdk.api.domain.transformedevents.ProcessTransformedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProcessTransformerManager implements TransformationManager {

    private List<Transformer> transformers;

    @Override
    public List<Transformer> getTransformers() {
        if (transformers == null) {
            transformers = new ArrayList<>();
            transformers.add(new JoinTransformer(Arrays.asList(ProcessRawEvent.SRC_PROCESS_DIRECTORY_FIELD_NAME, ProcessRawEvent.SRC_PROCESS_FILE_NAME_FIELD_NAME),ProcessTransformedEvent.SRC_PROCESS_FILE_PATH_FIELD_NAME,"\\"));
            transformers.add(new JoinTransformer(Arrays.asList(ProcessRawEvent.DST_PROCESS_DIRECTORY_FIELD_NAME, ProcessRawEvent.DST_PROCESS_FILE_NAME_FIELD_NAME),ProcessTransformedEvent.DST_PROCESS_FILE_PATH_FIELD_NAME,"\\"));
        }
        return transformers;
    }

    @Override
    public <U extends AbstractInputDocument> U getTransformedDocument(AbstractInputDocument rawEvent) {
        return (U) new ProcessTransformedEvent((ProcessRawEvent) rawEvent);
    }
}
