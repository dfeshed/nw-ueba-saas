package presidio.input.core.services.transformation;

import presidio.sdk.api.domain.AbstractInputDocument;

import java.util.List;
import java.util.Map;

public class OperationTypeCategoryTransformer implements Transformer {

    final Map<String, List<String>> operationTypeCategoryMapping;

    public OperationTypeCategoryTransformer(Map<String, List<String>> operationTypeCategoryMapping) {
        this.operationTypeCategoryMapping = operationTypeCategoryMapping;
    }

    @Override
    public List<AbstractInputDocument> transform(List<AbstractInputDocument> documents) {

        documents.forEach(abstractInputDocument -> {
            String operationType = abstractInputDocument.getOperationType();
            List<String> operationTypeCategories = operationTypeCategoryMapping.get(operationType);
            abstractInputDocument.getOperationTypeCategory().addAll(operationTypeCategories);
        });

        return documents;
    }
}
