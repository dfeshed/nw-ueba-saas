package presidio.input.core.services.transformation.transformer;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.utils.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OperationTypeToCategoryTransformer implements Transformer {

    final Map<String, List<String>> operationTypeCategoryMapping;
    private final String inputFieldName1;
    private final String inputFieldName2;
    private final String outputFieldName1;

    public OperationTypeToCategoryTransformer(Map<String, List<String>> operationTypeCategoryMapping,  String inputFieldName1, String inputFieldName2, String outputFieldName1) {
        this.operationTypeCategoryMapping = operationTypeCategoryMapping;
        this.inputFieldName1 = inputFieldName1;
        this.inputFieldName2 = inputFieldName2;
        this.outputFieldName1 = outputFieldName1;
    }

    @Override
    public List<AbstractInputDocument> transform(List<AbstractInputDocument> documents) {

        if (MapUtils.isNotEmpty(operationTypeCategoryMapping)) {
            documents.forEach(inputDocument -> {
                String operationType =  (String) ReflectionUtils.getFieldValue(inputDocument, inputFieldName1);

                List<String> operationTypeCategories = operationTypeCategoryMapping.get(operationType);

                if (CollectionUtils.isNotEmpty(operationTypeCategories)) {
                    List<String> operationTypeCategoriesValue = (List<String>) ReflectionUtils.getFieldValue(inputDocument, inputFieldName2);
                    if (operationTypeCategoriesValue == null) {
                        operationTypeCategoriesValue = new ArrayList<>();
                    }
                    operationTypeCategoriesValue.addAll(operationTypeCategories);
                    try {
                        ReflectionUtils.setFieldValue(inputDocument, outputFieldName1, operationTypeCategoriesValue);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        return documents;
    }
}
