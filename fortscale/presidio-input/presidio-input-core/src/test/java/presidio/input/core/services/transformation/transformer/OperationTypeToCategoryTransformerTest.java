package presidio.input.core.services.transformation.transformer;

import com.google.common.collect.Lists;
import fortscale.domain.core.EventResult;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.rawevents.AuthenticationRawEvent;
import presidio.sdk.api.domain.rawevents.FileRawEvent;
import presidio.sdk.api.domain.transformedevents.AuthenticationTransformedEvent;
import presidio.sdk.api.domain.transformedevents.FileTransformedEvent;

import java.time.Instant;
import java.util.*;

@RunWith(SpringRunner.class)
public class OperationTypeToCategoryTransformerTest {
    @Test
    public void test() {
        FileRawEvent fileRawEvent = new FileRawEvent(Instant.now(), "id", "dataSource", "userId",
                "operationType", null, EventResult.SUCCESS, "userName",
                "displayName", null, "", false,
                "", false, 0l, "resultCode");

        Map<String, List<String>> operationTypeMap = new HashMap<>();
        List<String> operationCategories = new ArrayList<>();
        operationCategories.add("category");
        operationTypeMap.put("operationType", operationCategories);

        OperationTypeToCategoryTransformer operationTypeToCategoryTransformer = new OperationTypeToCategoryTransformer(operationTypeMap);
        List<AbstractInputDocument> transformed = operationTypeToCategoryTransformer.transform(Arrays.asList(new FileTransformedEvent(fileRawEvent)));

        Assert.assertEquals(1, transformed.size());
        Assert.assertEquals(operationCategories.size(), transformed.get(0).getOperationTypeCategory().size());
    }

    @Test
    public void testNoMatch() {
        FileRawEvent fileRawEvent = new FileRawEvent(Instant.now(), "id", "dataSource", "userId",
                "operationType2", null, EventResult.SUCCESS, "userName",
                "displayName", null, "", false,
                "", false, 0l, "resultCode");

        Map<String, List<String>> operationTypeMap = new HashMap<>();
        List<String> operationCategories = new ArrayList<>();
        operationCategories.add("category");
        operationTypeMap.put("operationType", operationCategories);

        OperationTypeToCategoryTransformer operationTypeToCategoryTransformer = new OperationTypeToCategoryTransformer(operationTypeMap);
        List<AbstractInputDocument> transformed = operationTypeToCategoryTransformer.transform(Arrays.asList(new FileTransformedEvent(fileRawEvent)));

        Assert.assertEquals(1, transformed.size());
        Assert.assertNull(transformed.get(0).getOperationTypeCategory());
    }

    @Test
    public void testOperationTypeCategoryReceived() {
        FileRawEvent fileRawEvent = new FileRawEvent(Instant.now(), "id", "dataSource", "userId",
                "operationType", Lists.newArrayList("existingCategory"), EventResult.SUCCESS, "userName",
                "displayName", null, "", false,
                "", false, 0l, "resultCode");

        Map<String, List<String>> operationTypeMap = new HashMap<>();
        List<String> operationCategories = new ArrayList<>();
        operationCategories.add("category");
        operationTypeMap.put("operationType", operationCategories);

        OperationTypeToCategoryTransformer operationTypeToCategoryTransformer = new OperationTypeToCategoryTransformer(operationTypeMap);
        List<AbstractInputDocument> transformed = operationTypeToCategoryTransformer.transform(Arrays.asList(new FileTransformedEvent(fileRawEvent)));

        Assert.assertEquals(1, transformed.size());
        Assert.assertEquals(2, transformed.get(0).getOperationTypeCategory().size());
    }

    @Test
    public void testNoMapping() {
        AuthenticationRawEvent authenticationRawEvent = new AuthenticationRawEvent(Instant.now(), "eventId",
                "dataSource", "userId", "operationType", null,
                EventResult.SUCCESS, "userName", "userDisplayName", null,
                "srcMachineId", "srcMachineName", "dstMachineId",
                "dstMachineName", "dstMachineDomain", "resultCode", "site");

        OperationTypeToCategoryTransformer operationTypeToCategoryTransformer = new OperationTypeToCategoryTransformer(null);
        List<AbstractInputDocument> transformed = operationTypeToCategoryTransformer.transform(Arrays.asList(new AuthenticationTransformedEvent(authenticationRawEvent)));

        Assert.assertEquals(1, transformed.size());
        Assert.assertNull(transformed.get(0).getOperationTypeCategory());
    }

    @Test
    public void testNoOperationType() {
        AuthenticationRawEvent authenticationRawEvent = new AuthenticationRawEvent(Instant.now(), "eventId",
                "dataSource", "userId", null, null,
                EventResult.SUCCESS, "userName", "userDisplayName", null,
                "srcMachineId", "srcMachineName", "dstMachineId",
                "dstMachineName", "dstMachineDomain", "resultCode", "site");

        OperationTypeToCategoryTransformer operationTypeToCategoryTransformer = new OperationTypeToCategoryTransformer(null);
        List<AbstractInputDocument> transformed = operationTypeToCategoryTransformer.transform(Arrays.asList(new AuthenticationTransformedEvent(authenticationRawEvent)));

        Assert.assertEquals(1, transformed.size());
        Assert.assertNull(transformed.get(0).getOperationTypeCategory());
    }
}
