package presidio.input.core.services.transformation.transformer;

import com.google.common.collect.Lists;
import fortscale.common.general.Schema;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
public class OperationTypeToCategoriesTransformerTest extends TransformerJsonTest {
    @Test
    public void test() {
        FileRawEvent fileRawEvent = new FileRawEvent(Instant.now(), "id", "dataSource", "userId",
                "operationType", null, EventResult.SUCCESS, "userName",
                "displayName", null, "", false,
                "", false, 0L, "resultCode");

        Map<String, List<String>> operationTypeMap = new HashMap<>();
        List<String> operationCategories = new ArrayList<>();
        operationCategories.add("category");
        operationTypeMap.put("operationType", operationCategories);

        OperationTypeToCategoriesTransformer operationTypeToCategoriesTransformer = new OperationTypeToCategoriesTransformer(Schema.FILE,  FileRawEvent.OPERATION_TYPE_FIELD_NAME, FileRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME,  FileRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME);
        operationTypeToCategoriesTransformer.setOperationTypeCategoriesMapping(operationTypeMap);
        operationTypeToCategoriesTransformer.transform(new FileTransformedEvent(fileRawEvent));
    }

    @Test
    public void testNoMatch() {
        FileRawEvent fileRawEvent = new FileRawEvent(Instant.now(), "id", "dataSource", "userId",
                "operationType2", null, EventResult.SUCCESS, "userName",
                "displayName", null, "", false,
                "", false, 0L, "resultCode");

        Map<String, List<String>> operationTypeMap = new HashMap<>();
        List<String> operationCategories = new ArrayList<>();
        operationCategories.add("category");
        operationTypeMap.put("operationType", operationCategories);

        OperationTypeToCategoriesTransformer operationTypeToCategoriesTransformer = new OperationTypeToCategoriesTransformer(Schema.FILE,  FileRawEvent.OPERATION_TYPE_FIELD_NAME, FileRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME,  FileRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME);
        operationTypeToCategoriesTransformer.setOperationTypeCategoriesMapping(operationTypeMap);
        AbstractInputDocument transformed = operationTypeToCategoriesTransformer.transform(new FileTransformedEvent(fileRawEvent));
        Assert.assertNull(((FileRawEvent)transformed).getOperationTypeCategories());
    }

    @Test
    public void testOperationTypeCategoryReceived() {
        FileRawEvent fileRawEvent = new FileRawEvent(Instant.now(), "id", "dataSource", "userId",
                "operationType", Lists.newArrayList("existingCategory"), EventResult.SUCCESS, "userName",
                "displayName", null, "", false,
                "", false, 0L, "resultCode");

        Map<String, List<String>> operationTypeMap = new HashMap<>();
        List<String> operationCategories = new ArrayList<>();
        operationCategories.add("category");
        operationTypeMap.put("operationType", operationCategories);

        OperationTypeToCategoriesTransformer operationTypeToCategoriesTransformer = new OperationTypeToCategoriesTransformer(Schema.FILE,  FileRawEvent.OPERATION_TYPE_FIELD_NAME, FileRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME,  FileRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME);
        operationTypeToCategoriesTransformer.setOperationTypeCategoriesMapping(operationTypeMap);
        AbstractInputDocument transformed = operationTypeToCategoriesTransformer.transform(new FileTransformedEvent(fileRawEvent));
        Assert.assertEquals(2, ((FileRawEvent)transformed).getOperationTypeCategories().size());
    }

    @Test
    public void testNoMapping() {
        AuthenticationRawEvent authenticationRawEvent = new AuthenticationRawEvent(Instant.now(), "eventId",
                "dataSource", "userId", "operationType", null,
                EventResult.SUCCESS, "userName", "userDisplayName", null,
                "srcMachineId", "srcMachineName", "dstMachineId",
                "dstMachineName", "dstMachineDomain", "resultCode", "site",
                "country", "city");

        OperationTypeToCategoriesTransformer operationTypeToCategoriesTransformer = new OperationTypeToCategoriesTransformer(Schema.AUTHENTICATION,  AuthenticationRawEvent.OPERATION_TYPE_FIELD_NAME, AuthenticationRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME,  AuthenticationRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME);
        AbstractInputDocument transformed = operationTypeToCategoriesTransformer.transform(new AuthenticationTransformedEvent(authenticationRawEvent));
        Assert.assertNull(((AuthenticationRawEvent)transformed).getOperationTypeCategories());
    }

    @Test
    public void testNoOperationType() {
        AuthenticationRawEvent authenticationRawEvent = new AuthenticationRawEvent(Instant.now(), "eventId",
                "dataSource", "userId", null, null,
                EventResult.SUCCESS, "userName", "userDisplayName", null,
                "srcMachineId", "srcMachineName", "dstMachineId",
                "dstMachineName", "dstMachineDomain", "resultCode", "site",
                "country", "city");

        OperationTypeToCategoriesTransformer operationTypeToCategoriesTransformer = new OperationTypeToCategoriesTransformer(Schema.AUTHENTICATION,  AuthenticationRawEvent.OPERATION_TYPE_FIELD_NAME, AuthenticationRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME,  AuthenticationRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME);
        AbstractInputDocument transformed = operationTypeToCategoriesTransformer.transform(new AuthenticationTransformedEvent(authenticationRawEvent));
        Assert.assertNull(((AuthenticationRawEvent)transformed).getOperationTypeCategories());
    }

    @Override
    String getResourceFilePath() {
        return "OperationTypeToCategoriesTransformer.json";
    }

    @Override
    Class getTransformerClass() {
        return OperationTypeMappingTransformer.class;
    }
}
