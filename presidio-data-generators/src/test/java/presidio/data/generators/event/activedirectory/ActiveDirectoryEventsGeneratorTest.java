package presidio.data.generators.event.activedirectory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import presidio.data.domain.event.activedirectory.ACTIVEDIRECTORY_OP_TYPE_CATEGORIES;
import presidio.data.domain.event.activedirectory.AD_OPERATION_TYPE;
import presidio.data.domain.event.activedirectory.ActiveDirectoryEvent;
import presidio.data.generators.activedirectoryop.*;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.user.RandomAdminUserPercentageGenerator;

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
        Assert.assertEquals(AD_OPERATION_TYPE.ACCOUNT_MANAGEMENT.value, events.get(0).getOperation().getOperationType());
        Assert.assertEquals(AD_OPERATION_TYPE.PASSWORD_CHANGED.value, events.get(1).getOperation().getOperationType());
        Assert.assertEquals(AD_OPERATION_TYPE.PASSWORD_CHANGED_BY_NON_OWNER.value, events.get(2).getOperation().getOperationType());
        Assert.assertEquals(AD_OPERATION_TYPE.GROUP_MEMBERSHIP.value, events.get(3).getOperation().getOperationType());
        Assert.assertEquals(AD_OPERATION_TYPE.USER_ACCOUNT_ENABLED.value, events.get(4).getOperation().getOperationType());
        Assert.assertEquals(AD_OPERATION_TYPE.USER_ACCOUNT_DISABLED.value, events.get(5).getOperation().getOperationType());
        Assert.assertEquals(AD_OPERATION_TYPE.USER_ACCOUNT_UNLOCKED.value, events.get(6).getOperation().getOperationType());
        Assert.assertEquals(AD_OPERATION_TYPE.USER_ACCOUNT_TYPE_CHANGED.value, events.get(7).getOperation().getOperationType());
        Assert.assertEquals(AD_OPERATION_TYPE.USER_ACCOUNT_RE_ENABLED.value, events.get(8).getOperation().getOperationType());
        Assert.assertEquals(AD_OPERATION_TYPE.USER_ACCOUNT_LOCKED.value, events.get(9).getOperation().getOperationType());
        Assert.assertEquals(AD_OPERATION_TYPE.USER_PASSWORD_NEVER_EXPIRES_OPTION_CHANGED.value, events.get(10).getOperation().getOperationType());
        Assert.assertEquals(AD_OPERATION_TYPE.NESTED_MEMBER_ADDED_TO_CRITICAL_ENTERPRISE_GROUP.value, events.get(11).getOperation().getOperationType());
        Assert.assertEquals(AD_OPERATION_TYPE.MEMBER_ADDED_TO_CRITICAL_ENTERPRISE_GROUP.value, events.get(12).getOperation().getOperationType());
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

        Assert.assertTrue(events.get(0).getOperation().getOperationTypeCategories().contains(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.SECURITY_SENSITIVE_OPERATION.value));
    }

    @Test
    public void SensitiveGroupMembershipOperationTest () throws GeneratorException {

        ActiveDirectoryEventsGenerator generator = new ActiveDirectoryEventsGenerator();
        ActiveDirectoryOperationGenerator adOperationsGenerator = new ActiveDirectoryOperationGenerator();

        ActiveDirectoryOpTypeCategoriesGenerator opTypeCategoriesGenerator = new ActiveDirectoryOpTypeCategoriesGenerator(
                new String[] {ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.SECURITY_SENSITIVE_OPERATION.value,
                        ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.GROUP_MEMBERSHIP.value}
        );
        adOperationsGenerator.setOperationTypeCategoriesGenerator(opTypeCategoriesGenerator);
        generator.setActiveDirOperationGenerator(adOperationsGenerator);
        events = generator.generate();

        List<String> opCategories = events.get(0).getOperation().getOperationTypeCategories();
        Assert.assertTrue(opCategories.contains(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.SECURITY_SENSITIVE_OPERATION.value));
        Assert.assertTrue(opCategories.contains(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.GROUP_MEMBERSHIP.value));

    }

    @Test
    public void CustomOperationtypeTest () throws GeneratorException {

        ActiveDirectoryEventsGenerator generator = new ActiveDirectoryEventsGenerator();
        ActiveDirectoryOperationGenerator opGenerator = new ActiveDirectoryOperationGenerator();

        ActiveDirOperationTypeCyclicGenerator opTypeGenerator = new ActiveDirOperationTypeCyclicGenerator(new String[] {"custom_type_x", "custom_type_y"});
        opGenerator.setOperationTypeGenerator(opTypeGenerator);

        generator.setActiveDirOperationGenerator(opGenerator);
        events = generator.generate();

        Assert.assertTrue(events.get(0).getOperation().getOperationType().contains("custom_type_x"));
        Assert.assertTrue(events.get(1).getOperation().getOperationType().contains("custom_type_y"));
    }

}
