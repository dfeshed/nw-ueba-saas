package fortscale.streaming.service.tagging;

import fortscale.domain.core.ComputerUsageType;
import fortscale.streaming.model.tagging.*;
import org.apache.samza.storage.kv.KeyValueStore;
import org.junit.Test;
import static org.mockito.BDDMockito.given;
import static  org.mockito.Mockito.*;

import java.util.Date;

import static org.junit.Assert.*;

public class TagServiceTest {

    @Test
    public void testSweeperTagAccount() throws Exception {




       long timestamp = new Date().getTime();


        //create the account instance for case 1
        AccountMachineAccess accountToTagg = new AccountMachineAccess("testAccountCase1");
        accountToTagg.setLastEventTimeStamp(timestamp);



        //fill the account with 4 destinationd for the case 1 test
        accountToTagg.addDestination("testHost1",new Date().getTime(), ComputerUsageType.Desktop,false);
        accountToTagg.addDestination("testHost2",new Date().getTime(), ComputerUsageType.Desktop,false);
        accountToTagg.addDestination("testHost3",new Date().getTime(), ComputerUsageType.Desktop,false);
        accountToTagg.addDestination("testHost4",new Date().getTime(), ComputerUsageType.Desktop,false);
        accountToTagg.addDestination("testHost5",new Date().getTime(), ComputerUsageType.Desktop,false);



        //create the account instance for case 2
        AccountMachineAccess accountToTagg2 = new AccountMachineAccess("testAccountCase2");
        accountToTagg2.setLastEventTimeStamp(timestamp);

        //fill the account with 2 destinations for the case 2  test
        accountToTagg2.addDestination("testHost1",new Date().getTime(), ComputerUsageType.Desktop,false);
        accountToTagg2.addDestination("testHost2",new Date().getTime(), ComputerUsageType.Desktop,false);
        accountToTagg2.addDestination("testHost3",new Date().getTime(), ComputerUsageType.Desktop,false);



        //Case 1 - account with 5 destinations and threshold 4 ( mark it as sweeper)

        //create sweeper impl instance
        SweeperTagImpl sti = new SweeperTagImpl();

        sti.setThreshold(4.0);
        sti.tag(accountToTagg);

        assertTrue(accountToTagg.getTags().get("Sweeper") && accountToTagg.getIsDirty());

        //Case 2 - account with 3 destinations and threshold 4 (un mark it as sweeper)
        sti.tag(accountToTagg2);

        assertTrue(!accountToTagg2.getTags().get("Sweeper")  && accountToTagg2.getIsDirty());

    }

    @Test
    public void testServerDesktopUsageTagAccount()
    {
        KeyValueStore<String,AccountMachineAccess> mockKeyValue = mock(KeyValueStore.class);
        long timestamp = new Date().getTime();


        //create the account instance for case 1
        AccountMachineAccess accountToTagg = new AccountMachineAccess("testAccountCase1");
        accountToTagg.setLastEventTimeStamp(timestamp);


        //threshold  - 90 %

        //fill the account with 8 destinations  for the case 1 test (7 server , 1 desktop )
        accountToTagg.addDestination("testHost1",new Date().getTime(), ComputerUsageType.Server,false);
        accountToTagg.addDestination("testHost2",new Date().getTime(), ComputerUsageType.Server,false);
        accountToTagg.addDestination("testHost3",new Date().getTime(), ComputerUsageType.Server,false);
        accountToTagg.addDestination("testHost4",new Date().getTime(), ComputerUsageType.Server,false);
        accountToTagg.addDestination("testHost5",new Date().getTime(), ComputerUsageType.Server,false);
        accountToTagg.addDestination("testHost6",new Date().getTime(), ComputerUsageType.Server,false);
        accountToTagg.addDestination("testHost7",new Date().getTime(), ComputerUsageType.Server,false);


        when(mockKeyValue.get("testAccountCase1")).thenReturn(accountToTagg);

        //create the service instance
        TagService ts = new TagService(mockKeyValue,30l);

        //Case 1
        ts.handleAccount("testAccountCase1",timestamp,"sourcetestHost8","testHost8",ComputerUsageType.Desktop,ComputerUsageType.Desktop,false);

        assertTrue(accountToTagg.getTags().get("Server") && accountToTagg.getIsDirty());


        //Case 2 - un tag the server tagging ( 6server,2 desktops )
        accountToTagg.getDestinations().get("testHost1").setType(ComputerUsageType.Desktop);




        //Case 2 - account with 3 destinations and threshold 4 (un mark it as sweeper)
        ts.handleAccount("testAccountCase1",timestamp,"sourcetestHost3","testHost9",ComputerUsageType.Desktop,ComputerUsageType.Desktop,false);

        assertTrue(!accountToTagg.getTags().get("Server")  && accountToTagg.getIsDirty());

        //Case 3 - account that have 50 % servers and 50 % desktop
        AccountMachineAccess accountToTagg2 = new AccountMachineAccess("testAccountCase3");
        accountToTagg2.setLastEventTimeStamp(timestamp);

        accountToTagg2.addDestination("testHost1",new Date().getTime(), ComputerUsageType.Desktop,false);


        when(mockKeyValue.get("testAccountCase2")).thenReturn(accountToTagg2);

        ts = new TagService(mockKeyValue,30l);

        ts.handleAccount("testAccountCase2",timestamp,"sourcetestHost3","testHost3",ComputerUsageType.Server,ComputerUsageType.Server,false);

        assertTrue(!accountToTagg2.getTags().get("Server")&& !accountToTagg2.getTags().get("Desktop")&& accountToTagg2.getIsDirty());

        //Case 4 - sweep account (move from server tag to desktop tag )
        accountToTagg.getDestinations().get("testHost2").setType(ComputerUsageType.Desktop);
        accountToTagg.getDestinations().get("testHost3").setType(ComputerUsageType.Desktop);
        accountToTagg.getDestinations().get("testHost4").setType(ComputerUsageType.Desktop);
        accountToTagg.getDestinations().get("testHost5").setType(ComputerUsageType.Desktop);
        accountToTagg.getDestinations().get("testHost6").setType(ComputerUsageType.Desktop);
        accountToTagg.getDestinations().get("testHost7").setType(ComputerUsageType.Desktop);

        ts.handleAccount("testAccountCase1",timestamp,"sourcetestHost3","testHost10",ComputerUsageType.Desktop,ComputerUsageType.Desktop,false);

        assertTrue(!accountToTagg.getTags().get("Server") && accountToTagg.getTags().get("Desktop")  && accountToTagg.getIsDirty());



    }

    @Test
    public void testSensitiveTagAccount()
    {
        KeyValueStore<String,AccountMachineAccess> mockKeyValue = mock(KeyValueStore.class);

        long timestamp = new Date().getTime();

        AccountMachineAccess accountToTagg = new AccountMachineAccess("testAccountCase1");
        accountToTagg.setLastEventTimeStamp(timestamp);

        when(mockKeyValue.get("testAccountCase1")).thenReturn(accountToTagg);

        //create the service instance
        TagService ts = new TagService(mockKeyValue,30l);

        //Case 1 - Mark account as sensitive
        ts.handleAccount("testAccountCase1",timestamp,"sourcetestHost1","testHost1",ComputerUsageType.Desktop,ComputerUsageType.Desktop,true);

        assertTrue(accountToTagg.getTags().get("Sensitive") && accountToTagg.getIsDirty());



        //Case 2 - Don't unremarked the sensitive field
        accountToTagg.getDestinations().remove("testHost1");

        ts.handleAccount("testAccountCase1",timestamp,"sourcetestHost1","testHost1",ComputerUsageType.Desktop,ComputerUsageType.Desktop,false);

        assertTrue(accountToTagg.getTags().get("Sensitive") && accountToTagg.getIsDirty());








    }

    @Test
    public void testIsFixedTagAccount()
    {

        long timestamp = new Date().getTime();


        //create the account instance for case 1
        AccountMachineAccess accountToTagg = new AccountMachineAccess("testAccountCase1");
        accountToTagg.setLastEventTimeStamp(timestamp);



        //fill the account with 4 destinations for the case 1 test
        accountToTagg.addDestination("testHost1",new Date().getTime(), ComputerUsageType.Desktop,false);
        accountToTagg.addDestination("testHost2",new Date().getTime(), ComputerUsageType.Desktop,false);
        accountToTagg.addDestination("testHost3",new Date().getTime(), ComputerUsageType.Desktop,false);
        accountToTagg.addDestination("testHost4",new Date().getTime(), ComputerUsageType.Desktop,false);
        accountToTagg.addDestination("testHost5",new Date().getTime(), ComputerUsageType.Desktop,false);


        //Case 1 - turn on the is fixed tag
        IsFixSourceTagImpl ifsi = new IsFixSourceTagImpl();

        ifsi.setThreshold(3.0);
        ifsi.setDayBack(0l);

        ifsi.tag(accountToTagg);

        assertTrue(accountToTagg.getTags().get("Fixed Source") && accountToTagg.getIsDirty());


        //Case 2 - turn off tag
        accountToTagg.getDestinations().remove("testHost1");
        accountToTagg.getDestinations().remove("testHost2");
        accountToTagg.getDestinations().remove("testHost3");

        ifsi.tag(accountToTagg);

        assertTrue(!accountToTagg.getTags().get("Fixed Source") && accountToTagg.getIsDirty());

        //Case 3 new tag with off flag
        AccountMachineAccess accountToTagg2 = new AccountMachineAccess("testAccountCase2");
        accountToTagg2.setLastEventTimeStamp(timestamp);

        accountToTagg2.addDestination("testHost1",new Date().getTime(), ComputerUsageType.Desktop,false);

        ifsi.tag(accountToTagg2);

        assertTrue(!accountToTagg2.getTags().get("Fixed Source") && accountToTagg.getIsDirty());



    }
}