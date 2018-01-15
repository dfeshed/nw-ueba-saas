package presidio.data.generators.event.activedirectory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import presidio.data.domain.event.OperationType;
import presidio.data.domain.event.activedirectory.ACTIVEDIRECTORY_OP_TYPE_CATEGORIES;
import presidio.data.domain.event.activedirectory.AD_OPERATION_TYPE;
import presidio.data.domain.event.activedirectory.ActiveDirectoryEvent;
import presidio.data.generators.activedirectoryop.*;
import presidio.data.generators.common.GeneratorException;
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
        Assert.assertEquals(AD_OPERATION_TYPE.OWNER_CHANGED_ON_COMPUTER_OBJECT.value, events.get(i++).getOperation().getOperationType().getName());
        Assert.assertEquals(AD_OPERATION_TYPE.DACL_CHANGED_ON_COMPUTER_OBJECT.value, events.get(i++).getOperation().getOperationType().getName());
        Assert.assertEquals(AD_OPERATION_TYPE.COMPUTER_RENAMED.value, events.get(i++).getOperation().getOperationType().getName());
        Assert.assertEquals(AD_OPERATION_TYPE.COMPUTER_REMOVED.value, events.get(i++).getOperation().getOperationType().getName());
        Assert.assertEquals(AD_OPERATION_TYPE.COMPUTER_MOVED.value, events.get(i++).getOperation().getOperationType().getName());
        Assert.assertEquals(AD_OPERATION_TYPE.COMPUTER_ADDED.value, events.get(i++).getOperation().getOperationType().getName());
        Assert.assertEquals(AD_OPERATION_TYPE.COMPUTER_ACCOUNT_ENABLED.value, events.get(i++).getOperation().getOperationType().getName());
        Assert.assertEquals(AD_OPERATION_TYPE.COMPUTER_ACCOUNT_DISABLED.value, events.get(i++).getOperation().getOperationType().getName());
        Assert.assertEquals(AD_OPERATION_TYPE.OWNER_CHANGED_ON_GROUP_OBJECT.value, events.get(i++).getOperation().getOperationType().getName());
        Assert.assertEquals(AD_OPERATION_TYPE.NESTED_MEMBER_REMOVED_FROM_GROUP.value, events.get(i++).getOperation().getOperationType().getName());
        Assert.assertEquals(AD_OPERATION_TYPE.NESTED_MEMBER_ADDED_TO_GROUP.value, events.get(i++).getOperation().getOperationType().getName());
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
        IActiveDirectoryOperationGenerator opGenerator = new ActiveDirectoryOpGeneratorTemplateFactory().getActiveDirectoryOperationsGenerator(
                AD_OPERATION_TYPE.NESTED_MEMBER_ADDED_TO_CRITICAL_ENTERPRISE_GROUP.value);

        generator.setActiveDirOperationGenerator(opGenerator);
        events = generator.generate();

        Assert.assertTrue(events.get(0).getOperation().getOperationType().getCategories().contains(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.SECURITY_SENSITIVE_OPERATION.value));
    }

    @Test
    public void SensitiveGroupMembershipOperationTest () throws GeneratorException {

        ActiveDirectoryEventsGenerator generator = new ActiveDirectoryEventsGenerator();
        IActiveDirectoryOperationGenerator opGenerator = new ActiveDirectoryOpGeneratorTemplateFactory().getActiveDirectoryOperationsGenerator(
                AD_OPERATION_TYPE.NESTED_MEMBER_ADDED_TO_CRITICAL_ENTERPRISE_GROUP.value
        );

        generator.setActiveDirOperationGenerator(opGenerator);
        events = generator.generate();

        List<String> opCategories = events.get(0).getOperation().getOperationType().getCategories();
        Assert.assertTrue(opCategories.contains(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.SECURITY_SENSITIVE_OPERATION.value));
        Assert.assertTrue(opCategories.contains(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.GROUP_MEMBERSHIP_OPERATION.value));

    }

    @Test
    public void CustomOperationtypeTest () throws GeneratorException {

        ActiveDirectoryEventsGenerator generator = new ActiveDirectoryEventsGenerator();
        ActiveDirectoryOperationGenerator opGenerator = new ActiveDirectoryOperationGenerator();
        List<OperationType> operationTypes = Arrays.asList(new OperationType("custom_type_x"), new OperationType("custom_type_y"));

        ActiveDirOperationTypeCyclicGenerator opTypeGenerator = new ActiveDirOperationTypeCyclicGenerator((OperationType[]) operationTypes.toArray());
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
