package presidio.input.core.services.transformation.transformer;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import presidio.sdk.api.domain.AbstractInputDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OperationTypeToCategoryTransformer implements Transformer {

    final Map<String, List<String>> operationTypeCategoryMapping;

    public OperationTypeToCategoryTransformer(Map<String, List<String>> operationTypeCategoryMapping) {
        this.operationTypeCategoryMapping = operationTypeCategoryMapping;
    }

    @Override
    public List<AbstractInputDocument> transform(List<AbstractInputDocument> documents) {

        if (MapUtils.isNotEmpty(operationTypeCategoryMapping)) {
            documents.forEach(abstractInputDocument -> {
                String operationType = abstractInputDocument.getOperationType();
                List<String> operationTypeCategories = operationTypeCategoryMapping.get(operationType);

                if (CollectionUtils.isNotEmpty(operationTypeCategories)) {
                    if (abstractInputDocument.getOperationTypeCategory() == null) {
                        abstractInputDocument.setOperationTypeCategory(new ArrayList<>());
                    }
                    abstractInputDocument.getOperationTypeCategory().addAll(operationTypeCategories);
                }
            });
        }

        return documents;
    }
}
