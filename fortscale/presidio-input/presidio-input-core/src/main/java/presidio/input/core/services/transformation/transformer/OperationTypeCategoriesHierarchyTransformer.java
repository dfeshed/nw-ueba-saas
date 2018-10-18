package presidio.input.core.services.transformation.transformer;

import fortscale.utils.logging.Logger;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.utils.ReflectionUtils;

import java.util.*;

public class OperationTypeCategoriesHierarchyTransformer implements Transformer {

    private static final Logger logger = Logger.getLogger(OperationTypeCategoriesHierarchyTransformer.class);

    private final Map<String, List<String>> operationTypeCategoriesHierarchyMapping;
    private final String inputFieldName;
    private final String outputFieldName;

    public OperationTypeCategoriesHierarchyTransformer(Map<String, List<String>> operationTypeCategoriesHierarchyMapping, String inputFieldName, String outputFieldName) {
        this.operationTypeCategoriesHierarchyMapping = operationTypeCategoriesHierarchyMapping;
        this.inputFieldName = inputFieldName;
        this.outputFieldName = outputFieldName;
    }

    @Override
    public List<AbstractInputDocument> transform(List<AbstractInputDocument> documents) {

        if (MapUtils.isNotEmpty(operationTypeCategoriesHierarchyMapping)) {
            documents.forEach((AbstractInputDocument inputDocument) -> {

                List<String> operationTypeCategories =  (List<String>) ReflectionUtils.getFieldValue(inputDocument, inputFieldName);

                if (CollectionUtils.isNotEmpty(operationTypeCategories)) {
                    Set<String> additionalCategories = new HashSet<>();
                    operationTypeCategories.forEach(s -> additionalCategories.addAll(getAdditionalCategories(s)));

                    if (CollectionUtils.isNotEmpty(additionalCategories)) {
                        additionalCategories.addAll(operationTypeCategories);
                        try {
                            ReflectionUtils.setFieldValue(inputDocument, outputFieldName, new ArrayList<>(additionalCategories));
                        } catch (IllegalAccessException e) {
                            logger.error("error setting the {} field value", outputFieldName, e);
                        }
                    }
                }
            });
        }

        return documents;
    }

    private List<String> getAdditionalCategories(String operationTypeCategory) {
        List<String> result = new ArrayList<>();
        List<String> additionalCategories = operationTypeCategoriesHierarchyMapping.get(operationTypeCategory);
        if (CollectionUtils.isNotEmpty(additionalCategories)) {
            result.addAll(additionalCategories);
            additionalCategories.forEach(s -> {
                List<String> categories = getAdditionalCategories(s);
                if (CollectionUtils.isNotEmpty(categories)) {
                    result.addAll(categories);
                }
            });
        }
        return result;
    }
}
