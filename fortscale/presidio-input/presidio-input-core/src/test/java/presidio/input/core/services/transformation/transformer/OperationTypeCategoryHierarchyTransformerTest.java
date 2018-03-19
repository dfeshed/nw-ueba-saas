package presidio.input.core.services.transformation.transformer;

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
public class OperationTypeCategoryHierarchyTransformerTest {

    @Test
    public void testOneLevelHierarchy() {
        List<String> operationTypeCategory = new ArrayList<>();
        operationTypeCategory.add("A");
        ActiveDirectoryRawEvent event = createEvent(operationTypeCategory);

        Map<String, List<String>> operationTypeCategoryHierarchyMapping = new HashMap<>();
        List<String> operationCategories = new ArrayList<>();
        operationCategories.add("B");
        operationTypeCategoryHierarchyMapping.put("A", operationCategories);

        OperationTypeCategoryHierarchyTransformer operationTypeCategoryHierarchyTransformer = new OperationTypeCategoryHierarchyTransformer(operationTypeCategoryHierarchyMapping);
        List<AbstractInputDocument> transformed = operationTypeCategoryHierarchyTransformer.transform(Arrays.asList(new ActiveDirectoryTransformedEvent(event)));

        Assert.assertEquals(1, transformed.size());
        Assert.assertEquals(2, transformed.get(0).getOperationTypeCategories().size());
    }

    @Test
    public void testTwoLevelHierarchy() {
        List<String> operationTypeCategory = new ArrayList<>();
        operationTypeCategory.add("A");
        ActiveDirectoryRawEvent event = createEvent(operationTypeCategory);

        Map<String, List<String>> operationTypeCategoryHierarchyMapping = new HashMap<>();
        operationTypeCategoryHierarchyMapping.put("A", Arrays.asList("B", "C"));
        operationTypeCategoryHierarchyMapping.put("B", Arrays.asList("C", "D"));

        OperationTypeCategoryHierarchyTransformer operationTypeCategoryHierarchyTransformer = new OperationTypeCategoryHierarchyTransformer(operationTypeCategoryHierarchyMapping);
        List<AbstractInputDocument> transformed = operationTypeCategoryHierarchyTransformer.transform(Arrays.asList(new ActiveDirectoryTransformedEvent(event)));

        Assert.assertEquals(1, transformed.size());
        Assert.assertEquals(4, transformed.get(0).getOperationTypeCategories().size());
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

        OperationTypeCategoryHierarchyTransformer operationTypeCategoryHierarchyTransformer = new OperationTypeCategoryHierarchyTransformer(operationTypeCategoryHierarchyMapping);
        List<AbstractInputDocument> transformed = operationTypeCategoryHierarchyTransformer.transform(Arrays.asList(new ActiveDirectoryTransformedEvent(event)));

        Assert.assertEquals(1, transformed.size());
        Assert.assertEquals(1, transformed.get(0).getOperationTypeCategories().size());
    }

    @Test
    public void testEmptyMapping() {
        List<String> operationTypeCategory = new ArrayList<>();
        operationTypeCategory.add("C");
        ActiveDirectoryRawEvent event = createEvent(operationTypeCategory);

        Map<String, List<String>> operationTypeCategoryHierarchyMapping = new HashMap<>();

        OperationTypeCategoryHierarchyTransformer operationTypeCategoryHierarchyTransformer = new OperationTypeCategoryHierarchyTransformer(operationTypeCategoryHierarchyMapping);
        List<AbstractInputDocument> transformed = operationTypeCategoryHierarchyTransformer.transform(Arrays.asList(new ActiveDirectoryTransformedEvent(event)));

        Assert.assertEquals(1, transformed.size());
        Assert.assertEquals(1, transformed.get(0).getOperationTypeCategories().size());
    }

    @Test
    public void testMappingNull() {
        List<String> operationTypeCategory = new ArrayList<>();
        operationTypeCategory.add("C");
        ActiveDirectoryRawEvent event = createEvent(operationTypeCategory);

        OperationTypeCategoryHierarchyTransformer operationTypeCategoryHierarchyTransformer = new OperationTypeCategoryHierarchyTransformer(null);
        List<AbstractInputDocument> transformed = operationTypeCategoryHierarchyTransformer.transform(Arrays.asList(new ActiveDirectoryTransformedEvent(event)));

        Assert.assertEquals(1, transformed.size());
        Assert.assertEquals(1, transformed.get(0).getOperationTypeCategories().size());
    }

    public ActiveDirectoryRawEvent createEvent(List<String> operationTypeCategory) {
        ActiveDirectoryRawEvent activeDirectoryRawEvent = new ActiveDirectoryRawEvent(Instant.now(), "eventId",
                "dataSource", "userId", "operationType", operationTypeCategory,
                EventResult.SUCCESS, "userName", "userDisplayName", null,
                false, "objectId", "resultCode");
        return activeDirectoryRawEvent;
    }
}
