package presidio.input.core.services.transformation.transformer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.common.general.Schema;
import fortscale.utils.logging.Logger;
import fortscale.utils.reflection.PresidioReflectionUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import presidio.sdk.api.domain.AbstractInputDocument;

import java.util.*;

@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonTypeName("operation-type-categories-transformer")
public class OperationTypeToCategoriesTransformer extends OperationTypeMappingTransformer {

    private static final Logger logger = Logger.getLogger(OperationTypeToCategoriesTransformer.class);

    private Map<String, List<String>> operationTypeCategoriesMapping;
    private final String inputOperationTypeFieldName;
    private final String inputOperationTypeCategoriesFieldName;
    private final String outputOperationTypeCategoriesFieldName;

    public OperationTypeToCategoriesTransformer(@JsonProperty("schema") Schema schema,
                                                @JsonProperty("inputOperationTypeFieldName") String inputOperationTypeFieldName,
                                                @JsonProperty("inputOperationTypeCategoriesFieldName") String inputOperationTypeCategoriesFieldName,
                                                @JsonProperty("outputOperationTypeCategoriesFieldName") String outputOperationTypeCategoriesFieldName) {
        this.inputOperationTypeFieldName = inputOperationTypeFieldName;
        this.inputOperationTypeCategoriesFieldName = inputOperationTypeCategoriesFieldName;
        this.outputOperationTypeCategoriesFieldName = outputOperationTypeCategoriesFieldName;
        this.operationTypeCategoriesMapping = getMappingBySchema(schema);
    }

    public void setOperationTypeCategoriesMapping(Map<String, List<String>> operationTypeCategoriesMapping) {
        this.operationTypeCategoriesMapping = operationTypeCategoriesMapping;
    }

    @Override
    public AbstractInputDocument transform(AbstractInputDocument document) {
        if (MapUtils.isNotEmpty(operationTypeCategoriesMapping)) {
            String operationType =  (String) PresidioReflectionUtils.getFieldValue(document, inputOperationTypeFieldName);

            List<String> operationTypeCategories = operationTypeCategoriesMapping.get(operationType);

            if (CollectionUtils.isNotEmpty(operationTypeCategories)) {
                Set<String> additionalCategories = new HashSet<>(operationTypeCategories);

                List<String> existingOperationTypeCategories = (List<String>) PresidioReflectionUtils.getFieldValue(document, inputOperationTypeCategoriesFieldName);
                if (existingOperationTypeCategories != null) {
                    additionalCategories.addAll(existingOperationTypeCategories);
                }
                try {
                    PresidioReflectionUtils.setFieldValue(document, outputOperationTypeCategoriesFieldName, new ArrayList<>(additionalCategories));
                } catch (IllegalAccessException e) {
                    logger.error("error setting the {} field value", outputOperationTypeCategoriesFieldName, e);
                }
            }
        }
        return document;
    }

    @Override
    boolean isCategoryHierarchy() {
        return false;
    }
}
