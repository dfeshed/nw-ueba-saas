package presidio.input.core.services.transformation.transformer;

import com.google.common.collect.Lists;
import fortscale.domain.core.EventResult;
import fortscale.utils.transform.OperationTypeToCategoriesTransformer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.sdk.api.domain.rawevents.AuthenticationRawEvent;
import presidio.sdk.api.domain.rawevents.FileRawEvent;
import presidio.sdk.api.domain.transformedevents.FileTransformedEvent;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
public class OperationTypeToCategoriesTransformerTest extends TransformerJsonTest {
    @Test
    public void test() throws IOException {
        FileRawEvent fileRawEvent = new FileRawEvent(Instant.now(), "id", "dataSource", "userId",
                "operationType", null, EventResult.SUCCESS, "userName",
                "displayName", null, "", false,
                "", false, 0L, "resultCode");

        Map<String, List<String>> operationTypeMap = new HashMap<>();
        List<String> operationCategories = new ArrayList<>();
        operationCategories.add("category");
        operationTypeMap.put("operationType", operationCategories);
        OperationTypeToCategoriesTransformer operationTypeToCategoriesTransformer = new OperationTypeToCategoriesTransformer("name",  FileRawEvent.OPERATION_TYPE_FIELD_NAME, FileRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME,  FileRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME, operationTypeMap);
        transformEvent(fileRawEvent, operationTypeToCategoriesTransformer, FileTransformedEvent.class);
    }

    @Test
    public void testNoMatch() throws IOException {
        FileRawEvent fileRawEvent = new FileRawEvent(Instant.now(), "id", "dataSource", "userId",
                "operationType2", null, EventResult.SUCCESS, "userName",
                "displayName", null, "", false,
                "", false, 0L, "resultCode");

        Map<String, List<String>> operationTypeMap = new HashMap<>();
        List<String> operationCategories = new ArrayList<>();
        operationCategories.add("category");
        operationTypeMap.put("operationType", operationCategories);

        OperationTypeToCategoriesTransformer operationTypeToCategoriesTransformer = new OperationTypeToCategoriesTransformer("name",  FileRawEvent.OPERATION_TYPE_FIELD_NAME, FileRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME,  FileRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME, operationTypeMap);
        FileRawEvent transformed = (FileRawEvent) transformEvent(fileRawEvent, operationTypeToCategoriesTransformer, FileRawEvent.class);
        Assert.assertNull(transformed.getOperationTypeCategories());
    }

    @Test
    public void testOperationTypeCategoryReceived() throws IOException {
        FileRawEvent fileRawEvent = new FileRawEvent(Instant.now(), "id", "dataSource", "userId",
                "operationType", Lists.newArrayList("existingCategory"), EventResult.SUCCESS, "userName",
                "displayName", null, "", false,
                "", false, 0L, "resultCode");

        Map<String, List<String>> operationTypeMap = new HashMap<>();
        List<String> operationCategories = new ArrayList<>();
        operationCategories.add("category");
        operationTypeMap.put("operationType", operationCategories);

        OperationTypeToCategoriesTransformer operationTypeToCategoriesTransformer = new OperationTypeToCategoriesTransformer("name",  FileRawEvent.OPERATION_TYPE_FIELD_NAME, FileRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME,  FileRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME, operationTypeMap);
        FileRawEvent transformed = (FileRawEvent) transformEvent(fileRawEvent, operationTypeToCategoriesTransformer, FileRawEvent.class);
        Assert.assertEquals(2, transformed.getOperationTypeCategories().size());
    }

    @Test
    public void testNoMapping() throws IOException {
        AuthenticationRawEvent authenticationRawEvent = new AuthenticationRawEvent(Instant.now(), "eventId",
                "dataSource", "userId", "operationType", null,
                EventResult.SUCCESS, "userName", "userDisplayName", null,
                "srcMachineId", "srcMachineName", "dstMachineId",
                "dstMachineName", "dstMachineDomain", "resultCode", "site",
                "country", "city");

        OperationTypeToCategoriesTransformer operationTypeToCategoriesTransformer = new OperationTypeToCategoriesTransformer("name", AuthenticationRawEvent.OPERATION_TYPE_FIELD_NAME, AuthenticationRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME,  AuthenticationRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME, null);
        AuthenticationRawEvent transformed = (AuthenticationRawEvent) transformEvent(authenticationRawEvent, operationTypeToCategoriesTransformer, AuthenticationRawEvent.class);
        Assert.assertNull(transformed.getOperationTypeCategories());
    }

    @Test
    public void testNoOperationType() throws IOException {
        AuthenticationRawEvent authenticationRawEvent = new AuthenticationRawEvent(Instant.now(), "eventId",
                "dataSource", "userId", null, null,
                EventResult.SUCCESS, "userName", "userDisplayName", null,
                "srcMachineId", "srcMachineName", "dstMachineId",
                "dstMachineName", "dstMachineDomain", "resultCode", "site",
                "country", "city");

        OperationTypeToCategoriesTransformer operationTypeToCategoriesTransformer = new OperationTypeToCategoriesTransformer("name",  AuthenticationRawEvent.OPERATION_TYPE_FIELD_NAME, AuthenticationRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME,  AuthenticationRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME, null);
        AuthenticationRawEvent transformed = (AuthenticationRawEvent) transformEvent(authenticationRawEvent, operationTypeToCategoriesTransformer, AuthenticationRawEvent.class);
        Assert.assertNull(transformed.getOperationTypeCategories());
    }

    @Override
    String getResourceFilePath() {
        return "OperationTypeToCategoriesTransformer.json";
    }

    @Override
    Class getTransformerClass() {
        return OperationTypeToCategoriesTransformer.class;
    }
}
