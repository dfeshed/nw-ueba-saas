package presidio.input.core.services.transformation.transformer;

import presidio.sdk.api.domain.AbstractInputDocument;

import java.util.List;

public class FileToFolderPathTransformer implements Transformer {

    private final RegexTransformer regexTransformer;

    public FileToFolderPathTransformer(String inputPathFieldName, String folderPathFieldName) {
        this.regexTransformer = new RegexTransformer(inputPathFieldName, folderPathFieldName, ".*\\\\(?!.*\\\\)|.*/(?!.*/)");
    }

    @Override
    public List<AbstractInputDocument> transform(List<AbstractInputDocument> documents) {

        this.regexTransformer.transform(documents);
        return documents;
    }
}
