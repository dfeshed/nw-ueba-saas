package presidio.input.core.services.transformation.transformer;

import presidio.sdk.api.domain.AbstractInputDocument;

import java.util.List;

public class FileExtensionTransformer implements Transformer {
    private final RegexTransformer regexTransformer;

    public FileExtensionTransformer(String inputPathFieldName, String folderPathFieldName) {
        this.regexTransformer = new RegexTransformer(inputPathFieldName, folderPathFieldName, "\\.[0-9a-z]+$");
    }

    @Override
    public List<AbstractInputDocument> transform(List<AbstractInputDocument> documents) {
        this.regexTransformer.transform(documents);
        return documents;
    }
}
