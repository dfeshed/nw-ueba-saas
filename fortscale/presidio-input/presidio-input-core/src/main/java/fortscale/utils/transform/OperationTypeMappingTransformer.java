package fortscale.utils.transform;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.common.general.Schema;
import fortscale.utils.logging.Logger;
import org.json.JSONArray;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class OperationTypeMappingTransformer extends AbstractJsonObjectTransformer implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private static final Logger logger = Logger.getLogger(OperationTypeMappingTransformer.class);
    private static Map<Schema, Map<String, List<String>>> categoryMapping;
    private static Map<Schema, Map<String, List<String>>> categoryHierarchyMapping;

    @Value("${operation.type.category.mapping.file.path}")
    private String operationTypeCategoryMappingFilePath;

    @Value("${operation.type.category.hierarchy.mapping.file.path}")
    private String operationTypeCategoryHierarchyMappingFilePath;

    public OperationTypeMappingTransformer(String name) {
        super(name);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    abstract boolean isCategoryHierarchy();

    List<String> jsonArrayToList(JSONArray jsonArray) {
        ArrayList<String> toReturn = new ArrayList<>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                toReturn.add(jsonArray.getString(i));
            }
        }
        return toReturn;
    }

    Map<String, List<String>> getMappingBySchema(Schema schema) {
        Map<Schema, Map<String, List<String>>> mappingToReturn;
        if (isCategoryHierarchy()) {
            if (categoryHierarchyMapping == null) {
                categoryHierarchyMapping = getAllMapping(operationTypeCategoryHierarchyMappingFilePath);
            }
            mappingToReturn = categoryHierarchyMapping;
        } else {
            if (categoryMapping == null) {
                categoryMapping = getAllMapping(operationTypeCategoryMappingFilePath);
            }
            mappingToReturn = categoryMapping;
        }
        return mappingToReturn.get(schema);
    }

    private Map<Schema, Map<String, List<String>>> getAllMapping(String filePath) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Map<Schema, Map<String, List<String>>>> mapping;

        try {
            Resource resource = applicationContext.getResources(filePath)[0];
            mapping = mapper.readValue(resource.getFile(), new TypeReference<Map<String, Map<Schema, Map<String, List<String>>>>>() {});
            return mapping.get("mapping");
        } catch (Exception e) {
            logger.error("error loading the {} mapping file", filePath, e);
            return Collections.emptyMap();
        }
    }
}
