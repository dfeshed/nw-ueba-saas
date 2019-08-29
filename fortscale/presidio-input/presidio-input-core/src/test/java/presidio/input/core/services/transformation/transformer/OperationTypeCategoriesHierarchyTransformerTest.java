package presidio.input.core.services.transformation.transformer;

import fortscale.common.general.Schema;
import fortscale.domain.core.EventResult;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.rawevents.ActiveDirectoryRawEvent;
import presidio.sdk.api.domain.transformedevents.ActiveDirectoryTransformedEvent;

import java.time.Instant;
import java.util.*;

@RunWith(SpringRunner.class)
public class OperationTypeCategoriesHierarchyTransformerTest extends TransformerJsonTest {

    @Test
    public void testOneLevelHierarchy() {
        List<String> operationTypeCategory = new ArrayList<>();
        operationTypeCategory.add("A");
        ActiveDirectoryRawEvent event = createEvent(operationTypeCategory);

        Map<String, List<String>> operationTypeCategoryHierarchyMapping = new HashMap<>();
        List<String> operationCategories = new ArrayList<>();
        operationCategories.add("B");
        operationTypeCategoryHierarchyMapping.put("A", operationCategories);

        OperationTypeCategoriesHierarchyTransformer operationTypeCategoriesHierarchyTransformer = new OperationTypeCategoriesHierarchyTransformer(Schema.ACTIVE_DIRECTORY, ActiveDirectoryRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME, ActiveDirectoryRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME);
        operationTypeCategoriesHierarchyTransformer.setOperationTypeCategoriesHierarchyMapping(operationTypeCategoryHierarchyMapping);
        AbstractInputDocument transformed = operationTypeCategoriesHierarchyTransformer.transform(new ActiveDirectoryTransformedEvent(event));

        Assert.assertEquals(2, ((ActiveDirectoryRawEvent)transformed).getOperationTypeCategories().size());
    }

    @Test
    public void testTwoLevelHierarchy() {
        List<String> operationTypeCategory = new ArrayList<>();
        operationTypeCategory.add("A");
        ActiveDirectoryRawEvent event = createEvent(operationTypeCategory);

        Map<String, List<String>> operationTypeCategoryHierarchyMapping = new HashMap<>();
        operationTypeCategoryHierarchyMapping.put("A", Arrays.asList("B", "C"));
        operationTypeCategoryHierarchyMapping.put("B", Arrays.asList("C", "D"));

        OperationTypeCategoriesHierarchyTransformer operationTypeCategoriesHierarchyTransformer = new OperationTypeCategoriesHierarchyTransformer(Schema.ACTIVE_DIRECTORY, ActiveDirectoryRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME, ActiveDirectoryRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME);
        operationTypeCategoriesHierarchyTransformer.setOperationTypeCategoriesHierarchyMapping(operationTypeCategoryHierarchyMapping);
        AbstractInputDocument transformed = operationTypeCategoriesHierarchyTransformer.transform(new ActiveDirectoryTransformedEvent(event));

        Assert.assertEquals(4, ((ActiveDirectoryRawEvent) transformed).getOperationTypeCategories().size());
    }

    @Test
    public void testNoHierarchy() {
        List<String> operationTypeCategory = new ArrayList<>();
        operationTypeCategory.add("C");
        ActiveDirectoryRawEvent event = createEvent(operationTypeCategory);

        Map<String, List<String>> operationTypeCategoryHierarchyMapping = new HashMap<>();
        List<String> operationCategories = new ArrayList<>();
        operationCategories.add("B");
        operationTypeCategoryHierarchyMapping.put("A", operationCategories);

        OperationTypeCategoriesHierarchyTransformer operationTypeCategoriesHierarchyTransformer = new OperationTypeCategoriesHierarchyTransformer(Schema.ACTIVE_DIRECTORY, ActiveDirectoryRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME, ActiveDirectoryRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME);
        operationTypeCategoriesHierarchyTransformer.setOperationTypeCategoriesHierarchyMapping(operationTypeCategoryHierarchyMapping);
        AbstractInputDocument transformed = operationTypeCategoriesHierarchyTransformer.transform(new ActiveDirectoryTransformedEvent(event));

        Assert.assertEquals(1, ((ActiveDirectoryRawEvent)transformed).getOperationTypeCategories().size());
    }

    @Test
    public void testEmptyMapping() {
        List<String> operationTypeCategory = new ArrayList<>();
        operationTypeCategory.add("C");
        ActiveDirectoryRawEvent event = createEvent(operationTypeCategory);

        Map<String, List<String>> operationTypeCategoryHierarchyMapping = new HashMap<>();

        OperationTypeCategoriesHierarchyTransformer operationTypeCategoriesHierarchyTransformer = new OperationTypeCategoriesHierarchyTransformer(Schema.ACTIVE_DIRECTORY, ActiveDirectoryRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME, ActiveDirectoryRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME);
        AbstractInputDocument transformed = operationTypeCategoriesHierarchyTransformer.transform(new ActiveDirectoryTransformedEvent(event));

        Assert.assertEquals(1, ((ActiveDirectoryRawEvent)transformed).getOperationTypeCategories().size());
    }

    @Test
    public void testMappingNull() {
        List<String> operationTypeCategory = new ArrayList<>();
        operationTypeCategory.add("C");
        ActiveDirectoryRawEvent event = createEvent(operationTypeCategory);

        OperationTypeCategoriesHierarchyTransformer operationTypeCategoriesHierarchyTransformer = new OperationTypeCategoriesHierarchyTransformer(Schema.ACTIVE_DIRECTORY, ActiveDirectoryRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME, ActiveDirectoryRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME);
        AbstractInputDocument transformed = operationTypeCategoriesHierarchyTransformer.transform(new ActiveDirectoryTransformedEvent(event));

        Assert.assertEquals(1, ((ActiveDirectoryRawEvent)transformed).getOperationTypeCategories().size());
    }

    public ActiveDirectoryRawEvent createEvent(List<String> operationTypeCategory) {
        ActiveDirectoryRawEvent activeDirectoryRawEvent = new ActiveDirectoryRawEvent(Instant.now(), "eventId",
                "dataSource", "userId", "operationType", operationTypeCategory,
                EventResult.SUCCESS, "userName", "userDisplayName", null,
                 "objectId", "resultCode");
        return activeDirectoryRawEvent;
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
