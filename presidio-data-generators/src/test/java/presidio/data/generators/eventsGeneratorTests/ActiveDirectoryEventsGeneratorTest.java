package presidio.data.generators.eventsGeneratorTests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.domain.event.activedirectory.AD_OPERATION_TYPE;
import presidio.data.generators.domain.event.activedirectory.ActiveDirectoryEvent;
import presidio.data.generators.event.activedirectory.ActiveDirectoryEventsGenerator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ActiveDirectoryEventsGeneratorTest {

    List<ActiveDirectoryEvent> events;

    /** Default values:
     * time: 8:00 to 16:00, every 10 min, 30 to 1 days back
     * normalizedUsername:  "random" alphanumeric string, 10 chars length
     * operation type: all types from enum presidio.data.generators.domain.event.activedirectory.AD_OPERATION_TYPE
     * isSecuritySensitiveOperation: 1%
     * isUserAdministrator: 2%
     * objectName: "random" alphanumeric string, 20 chars length
     * result: 100% "Success"
     * dataSource: "Quest"
     *
     * event count: 1392 = 6 per hour * 8 work hours * 29 days
     */

    @Before
    public void prepare() throws GeneratorException {
        ActiveDirectoryEventsGenerator generator = new ActiveDirectoryEventsGenerator();
        events = generator.generate();
    }

    @Test
    public void EventsCountTest () {
        Assert.assertEquals(events.size(), 1392);
    }

    @Test
    public void DataSourceTest () {
        Assert.assertEquals(events.get(0).getDataSource(), "Quest");
    }

    @Test
    public void ResultsTest () {
        Set<String> resultsStrings = new HashSet<>();
        for (final ActiveDirectoryEvent ev : events) {
            resultsStrings.add(ev.getResult());
        }
        Assert.assertTrue(resultsStrings.contains("Success"));
        Assert.assertFalse(resultsStrings.contains("Failure"));
    }

    @Test
    public void ObjectNameTest () {
        Assert.assertEquals(events.get(0).getObjectName().length(), 20);
        Assert.assertEquals(events.get(1000).getObjectName().length(), 20);
        Assert.assertEquals(events.get(1391).getObjectName().length(), 20);

    }

    @Test
    public void IsAdministratorPctTest () {
        // isUserAdministrator 2% of 1392 = 28
        int admins = 0;
        for (final ActiveDirectoryEvent ev : events) {
            if (ev.getUserAdministrator()) admins++;
        }
        Assert.assertEquals(admins, 28);
    }

    @Test
    public void IsSecuritySensitiveOpPctTest () {
        // isSecuritySensitiveOperation 1% of 1392 = 13
        int sensitiveOps = 0;
        for (final ActiveDirectoryEvent ev : events) {
            if (ev.getSecuritySensitiveOperation()) sensitiveOps++;
        }
        Assert.assertEquals(sensitiveOps, 14);
    }

    @Test
    public void OperatioTypeTest () {
        // Operation types - see that all included, in the same order as enum
        Assert.assertEquals(events.get(0).getOperationType(), AD_OPERATION_TYPE.ACCOUNT_MANAGEMENT.value);
        Assert.assertEquals(events.get(1).getOperationType(), AD_OPERATION_TYPE.PASSWORD_CHANGED.value);
        Assert.assertEquals(events.get(2).getOperationType(), AD_OPERATION_TYPE.PASSWORD_CHANGED_BY_NON_OWNER.value);
        Assert.assertEquals(events.get(3).getOperationType(), AD_OPERATION_TYPE.GROUP_MEMBERSHIP.value);
        Assert.assertEquals(events.get(4).getOperationType(), AD_OPERATION_TYPE.USER_ACCOUNT_ENABLED.value);
        Assert.assertEquals(events.get(5).getOperationType(), AD_OPERATION_TYPE.USER_ACCOUNT_DISABLED.value);
        Assert.assertEquals(events.get(6).getOperationType(), AD_OPERATION_TYPE.USER_ACCOUNT_UNLOCKED.value);
        Assert.assertEquals(events.get(7).getOperationType(), AD_OPERATION_TYPE.USER_ACCOUNT_TYPE_CHANGED.value);
        Assert.assertEquals(events.get(8).getOperationType(), AD_OPERATION_TYPE.USER_ACCOUNT_RE_ENABLED.value);
        Assert.assertEquals(events.get(9).getOperationType(), AD_OPERATION_TYPE.USER_ACCOUNT_LOCKED.value);
        Assert.assertEquals(events.get(10).getOperationType(), AD_OPERATION_TYPE.USER_PASSWORD_NEVER_EXPIRES_OPTION_CHANGED.value);
        Assert.assertEquals(events.get(11).getOperationType(), AD_OPERATION_TYPE.NESTED_MEMBER_ADDED_TO_CRITICAL_ENTERPRISE_GROUP.value);
        Assert.assertEquals(events.get(12).getOperationType(), AD_OPERATION_TYPE.MEMBER_ADDED_TO_CRITICAL_ENTERPRISE_GROUP.value);
    }

    @Test
    public void NormalizedUserNameTest () {
        Assert.assertEquals(events.get(0).getNormalizedUsername().length(), 10);
        Assert.assertEquals(events.get(1000).getNormalizedUsername().length(), 10);
        Assert.assertEquals(events.get(1391).getNormalizedUsername().length(), 10);
    }
}
