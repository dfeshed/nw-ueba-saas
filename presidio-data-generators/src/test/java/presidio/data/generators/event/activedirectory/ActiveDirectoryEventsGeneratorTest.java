package presidio.data.generators.event.activedirectory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import presidio.data.domain.event.OperationType;
import presidio.data.domain.event.activedirectory.ACTIVEDIRECTORY_OP_TYPE_CATEGORIES;
import presidio.data.domain.event.activedirectory.AD_OPERATION_TYPE;
import presidio.data.domain.event.activedirectory.AD_OPERATION_TYPE_2_CATEGORIES_MAP;
import presidio.data.domain.event.activedirectory.ActiveDirectoryEvent;
import presidio.data.generators.activedirectoryop.ActiveDirOperationTypeCyclicGenerator;
import presidio.data.generators.activedirectoryop.ActiveDirectoryOperationGenerator;
import presidio.data.generators.common.FixedOperationTypeGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IOperationTypeGenerator;
import presidio.data.generators.user.RandomAdminUserPercentageGenerator;

import java.util.Arrays;
import java.util.List;

public class ActiveDirectoryEventsGeneratorTest {

    private List<ActiveDirectoryEvent> events;

    /** Default values:
     * time: 8:00 to 16:00, every 10 min, 30 to 1 days back
     * userId (normalizedUsername):  "random" alphanumeric string, 10 chars length
     * operation type: all types from enum presidio.data.domain.activedirectoryop.AD_OPERATION_TYPE
     * isUserAdministrator: 10% (altering default 2% generator)
     * objectName: "random" alphanumeric string, 20 chars length
     * result: 100% "Success"
     * dataSource: "DefaultDS"
     *
     * event count: 1392 = 6 per hour * 8 work hours * 29 days
     */

    @Before
    public void prepare() throws GeneratorException {
        ActiveDirectoryEventsGenerator generator = new ActiveDirectoryEventsGenerator();
        RandomAdminUserPercentageGenerator adminUsersGenerator = new RandomAdminUserPercentageGenerator(10);
        generator.setUserGenerator(adminUsersGenerator);
        events = generator.generate();
    }

    @Test
    public void EventsCountTest () {
        Assert.assertEquals(1392, events.size());
    }

    @Test
    public void DataSourceTest () {
        Assert.assertEquals("Active Directory", events.get(0).getDataSource());
    }

    @Test
    public void ResultsTest () {
        // All should succeed
        boolean anySuccess = true; // expect to remain "true"
        for (ActiveDirectoryEvent ev : events) {
            anySuccess = anySuccess && ev.getOperation().getOperationResult().equalsIgnoreCase("SUCCESS");
        }
        Assert.assertTrue(anySuccess);
    }

    @Test
    public void ObjectNameTest () {
        Assert.assertEquals(20, events.get(0).getOperation().getObjectName().length());
        Assert.assertEquals(20, events.get(1000).getOperation().getObjectName().length());
        Assert.assertEquals(20, events.get(1391).getOperation().getObjectName().length());

    }

    @Test
    public void IsAdministratorPctTest () {
        // isUserAdministrator 10% of 1392 = 140
        int admins = 0;
        for (final ActiveDirectoryEvent ev : events) {
            if (ev.getUser().isAdministrator()) admins++;
        }
        Assert.assertEquals(140, admins);
    }

    @Test
    public void OperationTypeTest () {
        // Operation types - see that all included, in the same order as enum
        int i = 0;
        Assert.assertEquals(AD_OPERATION_TYPE.PERMISSIONS_ON_OBJECT_CHANGED.value, events.get(i++).getOperation().getOperationType().getName());
        Assert.assertEquals(AD_OPERATION_TYPE.SYSTEM_SECURITY_ACCESS_GRANTED_TO_ACCOUNT.value, events.get(i++).getOperation().getOperationType().getName());
        Assert.assertEquals(AD_OPERATION_TYPE.USER_ACCOUNT_CREATED.value, events.get(i++).getOperation().getOperationType().getName());
        Assert.assertEquals(AD_OPERATION_TYPE.USER_ACCOUNT_ENABLED.value, events.get(i++).getOperation().getOperationType().getName());
    }

    @Test
    public void NormalizedUserNameTest () {
        Assert.assertEquals(10, events.get(0).getUser().getUserId().length());
        Assert.assertEquals(10, events.get(1000).getUser().getUserId().length());
        Assert.assertEquals(10, events.get(1391).getUser().getUserId().length());
    }


    @Test
    public void SensitiveOperationTest () throws GeneratorException {

        ActiveDirectoryEventsGenerator generator = new ActiveDirectoryEventsGenerator();
        String operationType = AD_OPERATION_TYPE.NESTED_MEMBER_ADDED_TO_CRITICAL_ENTERPRISE_GROUP.value;

        ActiveDirectoryOperationGenerator opGenerator = new ActiveDirectoryOperationGenerator();
        IOperationTypeGenerator opTypeGenerator = new FixedOperationTypeGenerator(new OperationType(operationType, AD_OPERATION_TYPE_2_CATEGORIES_MAP.INSTANCE.getOperation2CategoryMap().get(operationType)));
        opGenerator.setOperationTypeGenerator(opTypeGenerator);


        generator.setActiveDirOperationGenerator(opGenerator);
        events = generator.generate();

        Assert.assertTrue(events.get(0).getOperation().getOperationType().getCategories().contains(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.SECURITY_SENSITIVE_OPERATION.value));
    }

    @Test
    public void AllOperationsTest () throws GeneratorException {

        ActiveDirectoryEventsGenerator generator = new ActiveDirectoryEventsGenerator();
        ActiveDirectoryOperationGenerator operationsGenerator = new ActiveDirectoryOperationGenerator();
        IOperationTypeGenerator opTypeGenerator = new ActiveDirOperationTypeCyclicGenerator();
        operationsGenerator.setOperationTypeGenerator(opTypeGenerator);
        generator.setActiveDirOperationGenerator(operationsGenerator);
        events = generator.generate();

        Assert.assertTrue(events.get(0).getOperation().getOperationType().getName().contains(AD_OPERATION_TYPE.PERMISSIONS_ON_OBJECT_CHANGED.value));
    }

    @Test
    public void CustomOperationtypeTest () throws GeneratorException {

        ActiveDirectoryEventsGenerator generator = new ActiveDirectoryEventsGenerator();
        ActiveDirectoryOperationGenerator opGenerator = new ActiveDirectoryOperationGenerator();
       
        
        List<OperationType> operationTypes = Arrays.asList(new OperationType("custom_type_x"), new OperationType("custom_type_y"));
        int test = 0;

        OperationType[] customList = operationTypes.toArray(new OperationType[0]);
        ActiveDirOperationTypeCyclicGenerator opTypeGenerator = new ActiveDirOperationTypeCyclicGenerator(customList);
        opGenerator.setOperationTypeGenerator(opTypeGenerator);

        generator.setActiveDirOperationGenerator(opGenerator);
        events = generator.generate();

        Assert.assertTrue(events.get(0).getOperation().getOperationType().getName().contains("custom_type_x"));
        Assert.assertTrue(events.get(1).getOperation().getOperationType().getName().contains("custom_type_y"));
    }

    @Test
    public void ActiveDirectoryAllEventsGenerator() throws GeneratorException {
        List<ActiveDirectoryEvent> events = null;
        ActiveDirectoryEventsGenerator generator = new ActiveDirectoryEventsGenerator();

        events = generator.generate();
        Assert.assertEquals(1392, events.size()); // all events for default time generator

        events = generator.generate(); // no more events
        Assert.assertEquals(0, events.size());
    }

    @Test
    public void ActiveDirectoryBulkEventsGenerator() throws GeneratorException {
        final int BULK_SIZE = 10;
        List<ActiveDirectoryEvent> events = null;
        ActiveDirectoryEventsGenerator generator = new ActiveDirectoryEventsGenerator();

        events = generator.generate(BULK_SIZE);
        Assert.assertEquals(BULK_SIZE, events.size());

        events = generator.generate(0);
        Assert.assertEquals(0, events.size());

        events = generator.generate(1);
        Assert.assertEquals(1, events.size());

        events = generator.generate(100000);
        Assert.assertTrue(events.size() < 100000); // default time generator generates less than 100K events

        events = generator.generate(1); // no more events

        Assert.assertEquals(0, events.size());

    }
}
