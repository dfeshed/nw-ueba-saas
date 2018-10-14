package presidio.input.core.services.transformation.transformer;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.utils.ReflectionUtils;

import java.util.*;

public class OperationTypeCategoryHierarchyTransformer implements Transformer {

    final Map<String, List<String>> operationTypeCategoryHierarchyMapping;
    private final String inputFieldName;
    private final String outputFieldName;

    public OperationTypeCategoryHierarchyTransformer(Map<String, List<String>> operationTypeCategoryHierarchyMapping, String inputFieldName, String outputFieldName) {
        this.operationTypeCategoryHierarchyMapping = operationTypeCategoryHierarchyMapping;
        this.inputFieldName = inputFieldName;
        this.outputFieldName = outputFieldName;
    }

    @Override
    public List<AbstractInputDocument> transform(List<AbstractInputDocument> documents) {

        if (MapUtils.isNotEmpty(operationTypeCategoryHierarchyMapping)) {
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
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        return documents;
    }

    private List<String> getAdditionalCategories(String operationType) {
        List<String> result = new ArrayList<>();
        List<String> additionalCategories = operationTypeCategoryHierarchyMapping.get(operationType);
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
