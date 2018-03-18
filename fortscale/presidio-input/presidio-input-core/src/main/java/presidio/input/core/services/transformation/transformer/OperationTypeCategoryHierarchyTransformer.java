package presidio.input.core.services.transformation.transformer;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import presidio.sdk.api.domain.AbstractInputDocument;

import java.util.*;

public class OperationTypeCategoryHierarchyTransformer implements Transformer {

    final Map<String, List<String>> operationTypeCategoryHierarchyMapping;

    public OperationTypeCategoryHierarchyTransformer(Map<String, List<String>> operationTypeCategoryHierarchyMapping) {
        this.operationTypeCategoryHierarchyMapping = operationTypeCategoryHierarchyMapping;
    }

    @Override
    public List<AbstractInputDocument> transform(List<AbstractInputDocument> documents) {

        if (MapUtils.isNotEmpty(operationTypeCategoryHierarchyMapping)) {
            documents.forEach((AbstractInputDocument abstractInputDocument) -> {

                List<String> operationTypeCategories = abstractInputDocument.getOperationTypeCategories();
                if (CollectionUtils.isNotEmpty(operationTypeCategories)) {
                    Set<String> additionalCategories = new HashSet<>();
                    operationTypeCategories.forEach(s -> additionalCategories.addAll(getAdditionalCategories(s)));

                    if (CollectionUtils.isNotEmpty(additionalCategories)) {
                        additionalCategories.addAll(operationTypeCategories);
                        abstractInputDocument.setOperationTypeCategories(new ArrayList<>(additionalCategories));
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
