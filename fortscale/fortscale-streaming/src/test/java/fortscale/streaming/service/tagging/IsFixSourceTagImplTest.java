package fortscale.streaming.service.tagging;

import fortscale.domain.core.ComputerUsageType;
import fortscale.streaming.model.tagging.AccountMachineAccess;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class IsFixSourceTagImplTest {

    @Test
    public void testTag() throws Exception {
        long timestamp = new Date().getTime();


        //create the account instance for case 1
        AccountMachineAccess accountToTagg = new AccountMachineAccess("testAccountCase1");
        accountToTagg.setLastEventTimeStamp(timestamp);
        accountToTagg.setFirstEventTimestamp(timestamp);



        //fill the account with 4 destinations for the case 1 test
        accountToTagg.addSource("testHost1",new Date().getTime(), ComputerUsageType.Desktop);
        accountToTagg.addSource("testHost2", new Date().getTime(), ComputerUsageType.Desktop);
        accountToTagg.addSource("testHost3", new Date().getTime(), ComputerUsageType.Desktop);
        accountToTagg.addSource("testHost4", new Date().getTime(), ComputerUsageType.Desktop);
        accountToTagg.addSource("testHost5", new Date().getTime(), ComputerUsageType.Desktop);


        //Case 1 - turn on the is fixed tag
        IsFixSourceTagImpl ifsi = new IsFixSourceTagImpl();

        ifsi.setThreshold(3.0);
        ifsi.setDayBack(0l);
        ifsi.setIsFixSourceRegExpMachines("");

        ifsi.tag(accountToTagg);

        assertTrue(accountToTagg.getTags().get("Fixed Source") && accountToTagg.getIsDirty());


        //Case 2 - turn off tag
        accountToTagg.getSources().remove("testHost1");
        accountToTagg.getSources().remove("testHost2");
        accountToTagg.getSources().remove("testHost3");

        ifsi.tag(accountToTagg);

        assertTrue(!accountToTagg.getTags().get("Fixed Source") && accountToTagg.getIsDirty());

        //Case 3 new tag with off flag
        AccountMachineAccess accountToTagg2 = new AccountMachineAccess("testAccountCase2");
        accountToTagg2.setLastEventTimeStamp(timestamp);
        accountToTagg2.setFirstEventTimestamp(timestamp);

        accountToTagg2.addSource("testHost1", new Date().getTime(), ComputerUsageType.Desktop);

        ifsi.tag(accountToTagg2);

        assertTrue(!accountToTagg2.getTags().get("Fixed Source") && accountToTagg.getIsDirty());


        //Case 4 - reg exp test
        AccountMachineAccess regExpAccount = new AccountMachineAccess("idanp@fortscale.com");
        regExpAccount.setFirstEventTimestamp(timestamp);
        regExpAccount.setLastEventTimeStamp(timestamp);

        IsFixSourceTagImpl ifsti2 = new IsFixSourceTagImpl();
        ifsti2.setDayBack(0l);
        ifsti2.setThreshold(4.0);
        ifsti2.setIsFixSourceRegExpMachines("(idan|idanp|do)@fortscale.com");

        ifsti2.tag(regExpAccount);

        assertTrue(regExpAccount.getTags().get("Fixed Source") && regExpAccount.getIsDirty());


        //Case 5 - dont mark account if he dosent have at least daysBack of data
        AccountMachineAccess accountToTagg3 = new AccountMachineAccess("testAccountCase5");
        accountToTagg3.setLastEventTimeStamp(timestamp);
        accountToTagg3.setFirstEventTimestamp(timestamp);
        accountToTagg3.addSource("testHost1",new Date().getTime(), ComputerUsageType.Desktop);
        accountToTagg3.addSource("testHost2", new Date().getTime(), ComputerUsageType.Desktop);
        accountToTagg3.addSource("testHost3", new Date().getTime(), ComputerUsageType.Desktop);
        accountToTagg3.addSource("testHost4", new Date().getTime(), ComputerUsageType.Desktop);
        accountToTagg3.addSource("testHost5", new Date().getTime(), ComputerUsageType.Desktop);

        ifsti2 = new IsFixSourceTagImpl();
        ifsti2.setDayBack(5l);
        ifsti2.setThreshold(4.0);
        ifsti2.setIsFixSourceRegExpMachines("");



        ifsti2.tag(accountToTagg3);

        assertTrue(accountToTagg3.getTags().size()==0 && !accountToTagg3.getIsDirty());










    }
}