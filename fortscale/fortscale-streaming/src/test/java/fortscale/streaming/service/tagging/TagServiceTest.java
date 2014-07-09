package fortscale.streaming.service.tagging;

import fortscale.domain.core.ComputerUsageType;
import fortscale.streaming.model.tagging.AccountMachineAccess;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class TagServiceTest {

    @Test
    public void testTagAccount() throws Exception {

        //create the service instance
        TagService ts = new TagService();


        //Case 1 - account with 5 destinations and threshold 4 (mark it as sweeper)

        //create the account instance
        AccountMachineAccess accountToTagg = new AccountMachineAccess("testAccountCase1");

        //fill the account with 5 destinationd for the test
        accountToTagg.addDestination("testHost1",new Date().getTime(), ComputerUsageType.Desktop);
        accountToTagg.addDestination("testHost2",new Date().getTime(), ComputerUsageType.Desktop);
        accountToTagg.addDestination("testHost3",new Date().getTime(), ComputerUsageType.Desktop);
        accountToTagg.addDestination("testHost4",new Date().getTime(), ComputerUsageType.Desktop);
        accountToTagg.addDestination("testHost5",new Date().getTime(), ComputerUsageType.Desktop);

        ts.tagAccount(accountToTagg);

        assertTrue(accountToTagg.getTags().get("Sweeper") && accountToTagg.getIsDirty());

        //Case 2 - account with 3 destinations and threshold 4 (un mark it as sweeper)
        //create the account instance
        AccountMachineAccess accountToTagg2 = new AccountMachineAccess("testAccountCase2");

        //fill the account with 5 destinationd for the test
        accountToTagg2.addDestination("testHost1",new Date().getTime(), ComputerUsageType.Desktop);
        accountToTagg2.addDestination("testHost2",new Date().getTime(), ComputerUsageType.Desktop);
        accountToTagg2.addDestination("testHost3",new Date().getTime(), ComputerUsageType.Desktop);

        ts.tagAccount(accountToTagg2);

        assertTrue(accountToTagg2.getTags().get("Sweeper") == null  && !accountToTagg2.getIsDirty());






    }
}