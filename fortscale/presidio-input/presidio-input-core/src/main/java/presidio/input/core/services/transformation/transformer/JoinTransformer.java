package presidio.input.core.services.transformation.transformer;

import fortscale.utils.logging.Logger;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JoinTransformer implements Transformer {

    private static final Logger logger = Logger.getLogger(JoinTransformer.class);

    private List<String> inputFieldNames;
    private String outputFieldName;
    private String delimiter;


    public JoinTransformer(List<String> inputFieldNames, String outputFieldName, String delimiter) {
        this.inputFieldNames = inputFieldNames;
        this.outputFieldName = outputFieldName;
        this.delimiter = delimiter;
    }

    @Override
    public List<AbstractInputDocument> transform(List<AbstractInputDocument> documents) {

        documents.forEach(document -> {

            try {
                Map<String, Object> requiredFieldNameToValueMap = getInputFields(document);

                if (requiredFieldNameToValueMap.size() == inputFieldNames.size()) {
                    String outputFieldValue = inputFieldNames.stream().map(fieldName -> requiredFieldNameToValueMap.get(fieldName).toString()).collect(Collectors.joining(delimiter));
                    ReflectionUtils.setFieldValue(document, outputFieldName, outputFieldValue);
                }
            } catch (ReflectiveOperationException e) {
                logger.error("error setting the {} field value", outputFieldName, e);
            }

        });
        return documents;


    }

    private Map<String, Object> getInputFields(AbstractInputDocument document) throws ReflectiveOperationException {

        Map<String, Object> requiredFieldNameToValueMap = new HashMap<>();

        for (String requiredFieldName : inputFieldNames) {
            Field field = org.springframework.util.ReflectionUtils.findField(document.getClass(), requiredFieldName);
            if (field == null) throw new ReflectiveOperationException();
            requiredFieldNameToValueMap.put(requiredFieldName, ReflectionUtils.getFieldValue(document, requiredFieldName));
        }
        return requiredFieldNameToValueMap;
    }
}
