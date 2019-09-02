package fortscale.utils.transform;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.common.general.Schema;
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
@JsonTypeName("operation-type-categories-hierarchy-transformer")
public class OperationTypeCategoriesHierarchyTransformer extends OperationTypeMappingTransformer {

    private static final Logger logger = Logger.getLogger(OperationTypeCategoriesHierarchyTransformer.class);

    private Map<String, List<String>> operationTypeCategoriesHierarchyMapping;
    private final String inputFieldName;
    private final String outputFieldName;

    @JsonCreator
    public OperationTypeCategoriesHierarchyTransformer(@JsonProperty("name") String name,
                                                       @JsonProperty("schema") Schema schema,
                                                       @JsonProperty("inputFieldName") String inputFieldName,
                                                       @JsonProperty("outputFieldName") String outputFieldName) {
        super(name);
        this.inputFieldName = inputFieldName;
        this.outputFieldName = outputFieldName;
        this.operationTypeCategoriesHierarchyMapping = getMappingBySchema(schema);
    }

    public void setOperationTypeCategoriesHierarchyMapping(Map<String, List<String>> operationTypeCategoriesHierarchyMapping) {
        this.operationTypeCategoriesHierarchyMapping = operationTypeCategoriesHierarchyMapping;
    }

    @Override
    public JSONObject transform(JSONObject document) {
        if (MapUtils.isNotEmpty(operationTypeCategoriesHierarchyMapping)) {
            try {
                List<String> operationTypeCategories = jsonArrayToList((JSONArray) document.get(inputFieldName));
                if (CollectionUtils.isNotEmpty(operationTypeCategories)) {
                    Set<String> additionalCategories = new HashSet<>();
                    operationTypeCategories.forEach(s -> additionalCategories.addAll(getAdditionalCategories(s)));
                    if (CollectionUtils.isNotEmpty(additionalCategories)) {
                        additionalCategories.addAll(operationTypeCategories);
                            document.put(outputFieldName, new ArrayList<>(additionalCategories));
                    }
                }
            } catch (Exception e) {
                logger.error("error setting the {} field value", outputFieldName, e);
            }
        }
        return document;
    }

    @Override
    boolean isCategoryHierarchy() {
        return true;
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
