package presidio.input.core.services.transformation;

import fortscale.utils.logging.Logger;
import org.springframework.util.CollectionUtils;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.utils.ReflectionUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FolderPathTransformer implements Transformer {

    private static final Logger logger = Logger.getLogger(FolderPathTransformer.class);
    private final String filePathFieldName;
    private final String inputPathFieldName;
    private final String operationTypeFieldName;
    private final String folderPathFieldName;
    private final List<String> folderOperations;
    private final Pattern pattern;

    public FolderPathTransformer(String inputPathFieldName, String filePathFieldName, String folderPathFieldName, String operationTypeFieldName, List<String> folderOperations) {
        this.inputPathFieldName = inputPathFieldName;
        this.filePathFieldName = filePathFieldName;
        this.folderPathFieldName = folderPathFieldName;
        this.operationTypeFieldName = operationTypeFieldName;
        this.folderOperations = folderOperations;
        pattern = Pattern.compile(".*\\\\(?!.*\\\\)|.*\\/(?!.*\\/)");
    }

    @Override
    public List<AbstractInputDocument> transform(List<AbstractInputDocument> documents) {

        documents.forEach((AbstractInputDocument document) -> {

                    String filePathValue = (String) ReflectionUtils.getFieldValue(document, inputPathFieldName);
                    String outputFilePath = null;
                    String outputFolderPath = null;
                    if (isFolderOperation(document)) {
                        outputFolderPath = filePathValue;
                    } else {
                        Matcher matcher = pattern.matcher(filePathValue);
                        if (matcher.find()) {
                            outputFolderPath = matcher.group();
                            outputFilePath = filePathValue;
                        }
                    }
                    try {
                        ReflectionUtils.setFieldValue(document, filePathFieldName, outputFilePath);
                        ReflectionUtils.setFieldValue(document, folderPathFieldName, outputFolderPath);
                    } catch (IllegalAccessException e) {
                        logger.error("error setting one of {} {} field values", filePathFieldName, folderPathFieldName, e);
                    }
                }
        );


        return documents;
    }

    private boolean isFolderOperation(AbstractInputDocument document) {
        String operationType = (String) ReflectionUtils.getFieldValue(document, operationTypeFieldName);
        return CollectionUtils.contains(folderOperations.iterator(), operationType);
    }
}
