package presidio.input.core.services.transformation.managers;

import presidio.input.core.services.transformation.transformer.FileToFolderPathTransformer;
import presidio.input.core.services.transformation.transformer.MachineNameTransformer;
import presidio.input.core.services.transformation.transformer.Transformer;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.rawevents.PrintRawEvent;
import presidio.sdk.api.domain.transformedevents.PrintTransformedEvent;

import java.util.ArrayList;
import java.util.List;

public class PrintTransformerManager implements TransformationManager {

    public static final String CLUSTER_REPLACEMENT_PATTERN = "[0-9]";
    public static final String CLUSTER_POST_REPLACEMENT_CONDITION = "(.*[a-zA-Z]){5}.*";
    private List<Transformer> transformers;

    @Override
    public List<Transformer> getTransformers() {
        if (transformers == null) {
            transformers = new ArrayList<>();
            //src\dst machine name transformer:
            //machine name containing ip address will be transformed to empty string,
            //and value of resolved machine name will be transformed according to the cluster regex
            transformers.add(new MachineNameTransformer(PrintRawEvent.SRC_MACHINE_NAME_FIELD_NAME,
                    PrintTransformedEvent.SRC_MACHINE_CLUSTER_FIELD_NAME, CLUSTER_REPLACEMENT_PATTERN, "", null, CLUSTER_POST_REPLACEMENT_CONDITION));
            transformers.add(new MachineNameTransformer(PrintRawEvent.PRINTER_NAME_FIELD_NAME,
                    PrintTransformedEvent.PRINTER_CLUSTER_FIELD_NAME, CLUSTER_REPLACEMENT_PATTERN, "", null, CLUSTER_POST_REPLACEMENT_CONDITION));
            transformers.add(new FileToFolderPathTransformer(PrintTransformedEvent.SRC_FILE_PATH_FIELD_NAME, PrintTransformedEvent.SRC_FOLDER_PATH_FIELD_NAME));
        }
        return transformers;
    }

    @Override
    public <U extends AbstractInputDocument> U getTransformedDocument(AbstractInputDocument rawEvent) {
        return (U) new PrintTransformedEvent((PrintRawEvent) rawEvent);
    }
}
