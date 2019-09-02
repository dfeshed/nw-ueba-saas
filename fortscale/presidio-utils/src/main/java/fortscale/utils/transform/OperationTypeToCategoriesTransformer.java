package fortscale.utils.transform;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.utils.json.JacksonUtils;
import fortscale.utils.logging.Logger;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.json.JSONArray;
import org.json.JSONObject;

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

    public OperationTypeToCategoriesTransformer(@JsonProperty("name") String name,
                                                @JsonProperty("inputOperationTypeFieldName") String inputOperationTypeFieldName,
                                                @JsonProperty("inputOperationTypeCategoriesFieldName") String inputOperationTypeCategoriesFieldName,
                                                @JsonProperty("outputOperationTypeCategoriesFieldName") String outputOperationTypeCategoriesFieldName,
                                                @JsonProperty("operationTypeCategoriesMapping") Map<String, List<String>> operationTypeCategoriesMapping) {
        super(name);
        this.inputOperationTypeFieldName = inputOperationTypeFieldName;
        this.inputOperationTypeCategoriesFieldName = inputOperationTypeCategoriesFieldName;
        this.outputOperationTypeCategoriesFieldName = outputOperationTypeCategoriesFieldName;
        this.operationTypeCategoriesMapping = operationTypeCategoriesMapping;
    }

    @Override
    public JSONObject transform(JSONObject document) {
        if (MapUtils.isNotEmpty(operationTypeCategoriesMapping)) {
            try {
                String operationType = (String) document.get(inputOperationTypeFieldName);
                List<String> operationTypeCategories = operationTypeCategoriesMapping.get(operationType);
                if (CollectionUtils.isNotEmpty(operationTypeCategories)) {
                    Set<String> additionalCategories = new HashSet<>(operationTypeCategories);
                    Object operationalTypeCategories = document.get(inputOperationTypeCategoriesFieldName);
                    if (operationalTypeCategories != JSONObject.NULL) {
                        List<String> existingOperationTypeCategories = JacksonUtils.jsonArrayToList((JSONArray) operationalTypeCategories);
                        if (CollectionUtils.isNotEmpty(existingOperationTypeCategories)) {
                            additionalCategories.addAll(existingOperationTypeCategories);
                        }
                    }
                    document.put(outputOperationTypeCategoriesFieldName, new ArrayList<>(additionalCategories));
                }
            } catch (Exception e) {
                logger.error("error setting the {} field value", outputOperationTypeCategoriesFieldName, e);
            }
        }
        return document;
    }
}
