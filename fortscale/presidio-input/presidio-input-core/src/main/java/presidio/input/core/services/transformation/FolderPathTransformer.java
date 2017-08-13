package presidio.input.core.services.transformation;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;
import presidio.sdk.api.domain.AbstractPresidioDocument;
import presidio.sdk.api.utils.ReflectionUtils;

import java.io.File;
import java.util.List;
import java.util.Set;

public class FolderPathTransformer implements Transformer {

    private final String filePathFieldName;
    private final String inputPathFieldName;
    private final String operationTypeFieldName;
    private final String folderPathFieldName;
    private final Set<String> folderOperations;

    public FolderPathTransformer(String inputPathFieldName, String filePathFieldName, String folderPathFieldName, String operationTypeFieldName, Set<String> folderOperations) {
        this.inputPathFieldName = inputPathFieldName;
        this.filePathFieldName = filePathFieldName;
        this.folderPathFieldName = folderPathFieldName;
        this.operationTypeFieldName = operationTypeFieldName;
        this.folderOperations = folderOperations;
    }

    @Override
    public List<AbstractPresidioDocument> transform(List<AbstractPresidioDocument> documents) {

        documents.forEach((AbstractPresidioDocument document) -> {

                    String filePathValue = (String) ReflectionUtils.getFieldValue(document, inputPathFieldName);
                    String outputFilePath;
                    String outputFolderPath;
                    if (isFolderOperation(document)) {
                        outputFilePath = null;
                        outputFolderPath = filePathValue;
                    } else {

                        String[] splitFilePath = filePathValue.split(File.separator);
                        outputFolderPath = StringUtils.join(ArrayUtils.remove(splitFilePath, splitFilePath.length - 1), File.separator);
                        outputFilePath = filePathValue;
                    }
                    try {
                        ReflectionUtils.setFieldValue(document, filePathFieldName, outputFilePath);
                        ReflectionUtils.setFieldValue(document, folderPathFieldName, outputFolderPath);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
        );


        return documents;
    }

    private boolean isFolderOperation(AbstractPresidioDocument document) {
        String operationType = (String) ReflectionUtils.getFieldValue(document, operationTypeFieldName);
        return CollectionUtils.contains(folderOperations.iterator(), operationType);
    }
}
