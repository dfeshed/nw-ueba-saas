package fortscale.streaming.service.tagging;

import fortscale.domain.core.ComputerUsageType;
import fortscale.streaming.model.tagging.AccountMachineAccess;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class SweeperTagImplTest {

    @Test
    public void testTag() throws Exception {


        long timestamp = new Date().getTime();

        //create the account instance for case 1
        AccountMachineAccess accountToTagg = new AccountMachineAccess("testAccountCase1");
        accountToTagg.setLastEventTimeStamp(timestamp);
        accountToTagg.setFirstEventTimestamp(timestamp);

        //fill the account with 4 destinationd for the case 1 test
        accountToTagg.addDestination("testHost1",new Date().getTime(), ComputerUsageType.Desktop,false);
        accountToTagg.addDestination("testHost2",new Date().getTime(), ComputerUsageType.Desktop,false);
        accountToTagg.addDestination("testHost3",new Date().getTime(), ComputerUsageType.Desktop,false);
        accountToTagg.addDestination("testHost4",new Date().getTime(), ComputerUsageType.Desktop,false);
        accountToTagg.addDestination("testHost5",new Date().getTime(), ComputerUsageType.Desktop,false);


        //create the account instance for case 2
        AccountMachineAccess accountToTagg2 = new AccountMachineAccess("testAccountCase2");
        accountToTagg2.setLastEventTimeStamp(timestamp);
        accountToTagg2.setFirstEventTimestamp(timestamp);

        //fill the account with 2 destinations for the case 2  test
        accountToTagg2.addDestination("testHost1",new Date().getTime(), ComputerUsageType.Desktop,false);
        accountToTagg2.addDestination("testHost2",new Date().getTime(), ComputerUsageType.Desktop,false);
        accountToTagg2.addDestination("testHost3",new Date().getTime(), ComputerUsageType.Desktop,false);



        //Case 1 - account with 5 destinations and threshold 4 ( mark it as sweeper)

        //create sweeper impl instance
        SweeperTagImpl sti = new SweeperTagImpl();

        sti.setThreshold(4.0);
        sti.setDaysBack(0l);
        sti.setRegExpMachines("");

        sti.tag(accountToTagg);

        assertTrue(accountToTagg.getTags().get("Sweeper") && accountToTagg.getIsDirty());

        //Case 2 - account with 3 destinations and threshold 4 (un mark it as sweeper)
        sti.tag(accountToTagg2);

        assertTrue(!accountToTagg2.getTags().get("Sweeper")  && accountToTagg2.getIsDirty());


        //Case 3 - test reg ecpretion account
        SweeperTagImpl sti2 = new SweeperTagImpl();
        sti2.setThreshold(4.0);
        sti2.setDaysBack(0l);
        sti2.setRegExpMachines("(idanp|g)@fortscale.com");

        AccountMachineAccess accountToTagg3 = new AccountMachineAccess("idanp@fortscale.com");
        accountToTagg3.setLastEventTimeStamp(timestamp);
        accountToTagg3.setFirstEventTimestamp(timestamp);

        //fill the account with 2 destinations for the case 2  test
        accountToTagg3.addDestination("testHost1",new Date().getTime(), ComputerUsageType.Desktop,false);

        sti2.tag(accountToTagg3);

        assertTrue(accountToTagg3.getTags().get("Sweeper")  && accountToTagg3.getIsDirty());


    }
}