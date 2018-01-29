package presidio.input.core.services.transformation.transformer;

import fortscale.utils.logging.Logger;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.utils.ReflectionUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang.StringUtils.isNotBlank;

public class FileToFolderPathTransformer implements Transformer {

    private static final Logger logger = Logger.getLogger(FileToFolderPathTransformer.class);
    private final String inputPathFieldName;
    private final String folderPathFieldName;
    private final Pattern pattern;

    public FileToFolderPathTransformer(String inputPathFieldName, String folderPathFieldName) {
        this.inputPathFieldName = inputPathFieldName;
        this.folderPathFieldName = folderPathFieldName;
        pattern = Pattern.compile(".*\\\\(?!.*\\\\)|.*/(?!.*/)");
    }

    @Override
    public List<AbstractInputDocument> transform(List<AbstractInputDocument> documents) {

        documents.forEach((AbstractInputDocument document) -> {

                    String filePathValue = (String) ReflectionUtils.getFieldValue(document, inputPathFieldName);

                    if (isNotBlank(filePathValue)) {
                        String outputFolderPath = null;
                        Matcher matcher = pattern.matcher(filePathValue);
                        if (matcher.find()) {
                            outputFolderPath = matcher.group();
                        }
                        try {
                            ReflectionUtils.setFieldValue(document, folderPathFieldName, outputFolderPath);
                        } catch (IllegalAccessException e) {
                            logger.error("error setting {} field value", folderPathFieldName, e);
                        }
                    }
                }
        );

        return documents;
    }
}
