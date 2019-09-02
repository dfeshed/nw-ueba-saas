package presidio.input.core.services.transformation.transformer;

import fortscale.domain.core.EventResult;
import fortscale.utils.transform.OperationTypeCategoriesHierarchyTransformer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.sdk.api.domain.rawevents.ActiveDirectoryRawEvent;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

@RunWith(SpringRunner.class)
public class OperationTypeCategoriesHierarchyTransformerTest extends TransformerJsonTest {

    @Test
    public void testOneLevelHierarchy() throws IOException {
        List<String> operationTypeCategory = new ArrayList<>();
        operationTypeCategory.add("A");
        ActiveDirectoryRawEvent event = createEvent(operationTypeCategory);

        Map<String, List<String>> operationTypeCategoryHierarchyMapping = new HashMap<>();
        List<String> operationCategories = new ArrayList<>();
        operationCategories.add("B");
        operationTypeCategoryHierarchyMapping.put("A", operationCategories);

        OperationTypeCategoriesHierarchyTransformer operationTypeCategoriesHierarchyTransformer = new OperationTypeCategoriesHierarchyTransformer("name", ActiveDirectoryRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME, ActiveDirectoryRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME, operationTypeCategoryHierarchyMapping);
        ActiveDirectoryRawEvent activeDirectoryRawEvent = (ActiveDirectoryRawEvent) transformEvent(event, operationTypeCategoriesHierarchyTransformer, ActiveDirectoryRawEvent.class);
        Assert.assertEquals(2, activeDirectoryRawEvent.getOperationTypeCategories().size());
    }

    @Test
    public void testTwoLevelHierarchy() throws IOException {
        List<String> operationTypeCategory = new ArrayList<>();
        operationTypeCategory.add("A");
        ActiveDirectoryRawEvent event = createEvent(operationTypeCategory);

        Map<String, List<String>> operationTypeCategoryHierarchyMapping = new HashMap<>();
        operationTypeCategoryHierarchyMapping.put("A", Arrays.asList("B", "C"));
        operationTypeCategoryHierarchyMapping.put("B", Arrays.asList("C", "D"));

        OperationTypeCategoriesHierarchyTransformer operationTypeCategoriesHierarchyTransformer = new OperationTypeCategoriesHierarchyTransformer("name", ActiveDirectoryRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME, ActiveDirectoryRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME, operationTypeCategoryHierarchyMapping);
        ActiveDirectoryRawEvent activeDirectoryRawEvent = (ActiveDirectoryRawEvent) transformEvent(event, operationTypeCategoriesHierarchyTransformer, ActiveDirectoryRawEvent.class);
        Assert.assertEquals(4, activeDirectoryRawEvent.getOperationTypeCategories().size());
    }

    @Test
    public void testNoHierarchy() throws IOException {
        List<String> operationTypeCategory = new ArrayList<>();
        operationTypeCategory.add("C");
        ActiveDirectoryRawEvent event = createEvent(operationTypeCategory);

        Map<String, List<String>> operationTypeCategoryHierarchyMapping = new HashMap<>();
        List<String> operationCategories = new ArrayList<>();
        operationCategories.add("B");
        operationTypeCategoryHierarchyMapping.put("A", operationCategories);

        OperationTypeCategoriesHierarchyTransformer operationTypeCategoriesHierarchyTransformer = new OperationTypeCategoriesHierarchyTransformer("name", ActiveDirectoryRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME, ActiveDirectoryRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME, operationTypeCategoryHierarchyMapping);
        ActiveDirectoryRawEvent activeDirectoryRawEvent = (ActiveDirectoryRawEvent) transformEvent(event, operationTypeCategoriesHierarchyTransformer, ActiveDirectoryRawEvent.class);
        Assert.assertEquals(1, activeDirectoryRawEvent.getOperationTypeCategories().size());
    }

    @Test
    public void testEmptyMapping() throws IOException {
        List<String> operationTypeCategory = new ArrayList<>();
        operationTypeCategory.add("C");
        ActiveDirectoryRawEvent event = createEvent(operationTypeCategory);
        OperationTypeCategoriesHierarchyTransformer operationTypeCategoriesHierarchyTransformer = new OperationTypeCategoriesHierarchyTransformer("name", ActiveDirectoryRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME, ActiveDirectoryRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME, new HashMap<>());
        ActiveDirectoryRawEvent activeDirectoryRawEvent = (ActiveDirectoryRawEvent) transformEvent(event, operationTypeCategoriesHierarchyTransformer, ActiveDirectoryRawEvent.class);
        Assert.assertEquals(1, activeDirectoryRawEvent.getOperationTypeCategories().size());
    }

    @Test
    public void testMappingNull() throws IOException {
        List<String> operationTypeCategory = new ArrayList<>();
        operationTypeCategory.add("C");
        ActiveDirectoryRawEvent event = createEvent(operationTypeCategory);
        OperationTypeCategoriesHierarchyTransformer operationTypeCategoriesHierarchyTransformer = new OperationTypeCategoriesHierarchyTransformer("name", ActiveDirectoryRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME, ActiveDirectoryRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME, null);
        ActiveDirectoryRawEvent activeDirectoryRawEvent = (ActiveDirectoryRawEvent) transformEvent(event, operationTypeCategoriesHierarchyTransformer, ActiveDirectoryRawEvent.class);
        Assert.assertEquals(1, activeDirectoryRawEvent.getOperationTypeCategories().size());
    }

    public ActiveDirectoryRawEvent createEvent(List<String> operationTypeCategory) {
        return new ActiveDirectoryRawEvent(Instant.now(), "eventId",
                "dataSource", "userId", "operationType", operationTypeCategory,
                EventResult.SUCCESS, "userName", "userDisplayName", null,
                 "objectId", "resultCode");
    }

    @Override
    String getResourceFilePath() {
        return "OperationTypeCategoriesHierarchyTransformer.json";
    }

    @Override
    Class getTransformerClass() {
        return OperationTypeCategoriesHierarchyTransformer.class;
    }
}
