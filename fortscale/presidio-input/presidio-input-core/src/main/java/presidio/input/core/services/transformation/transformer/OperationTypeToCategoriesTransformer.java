package presidio.input.core.services.transformation.transformer;

import fortscale.utils.logging.Logger;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.utils.ReflectionUtils;

import java.util.*;

public class OperationTypeToCategoriesTransformer implements Transformer {

    private static final Logger logger = Logger.getLogger(OperationTypeToCategoriesTransformer.class);

    private final Map<String, List<String>> operationTypeCategoriesMapping;
    private final String inputOperationTypeFieldName;
    private final String inputOperationTypeCategoriesFieldName;
    private final String outputOperationTypeCategoriesFieldName;

    public OperationTypeToCategoriesTransformer(Map<String, List<String>> operationTypeCategoriesMapping, String inputOperationTypeFieldName, String inputOperationTypeCategoriesFieldName, String outputOperationTypeCategoriesFieldName) {
        this.operationTypeCategoriesMapping = operationTypeCategoriesMapping;
        this.inputOperationTypeFieldName = inputOperationTypeFieldName;
        this.inputOperationTypeCategoriesFieldName = inputOperationTypeCategoriesFieldName;
        this.outputOperationTypeCategoriesFieldName = outputOperationTypeCategoriesFieldName;
    }

    @Override
    public List<AbstractInputDocument> transform(List<AbstractInputDocument> documents) {

        if (MapUtils.isNotEmpty(operationTypeCategoriesMapping)) {
            documents.forEach(inputDocument -> {
                String operationType =  (String) ReflectionUtils.getFieldValue(inputDocument, inputOperationTypeFieldName);

                List<String> operationTypeCategories = operationTypeCategoriesMapping.get(operationType);

                if (CollectionUtils.isNotEmpty(operationTypeCategories)) {
                    Set<String> additionalCategories = new HashSet<>(operationTypeCategories);

                    List<String> existingOperationTypeCategories = (List<String>) ReflectionUtils.getFieldValue(inputDocument, inputOperationTypeCategoriesFieldName);
                    if (existingOperationTypeCategories != null) {
                        additionalCategories.addAll(existingOperationTypeCategories);
                    }
                    try {
                        ReflectionUtils.setFieldValue(inputDocument, outputOperationTypeCategoriesFieldName, new ArrayList<>(additionalCategories));
                    } catch (IllegalAccessException e) {
                        logger.error("error setting the {} field value", outputOperationTypeCategoriesFieldName, e);
                    }
                }
            });
        }

        return documents;
    }
}
