package fortscale.streaming.service.tagging;

import fortscale.domain.core.ComputerUsageType;
import fortscale.streaming.model.tagging.AccountMachineAccess;
import fortscale.streaming.model.tagging.MachineState;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class UseServerDesktopTagImplTest {

    @Test
    public void testTag() throws Exception {


        long timestamp = new Date().getTime();


        //create the account instance for case 1
        AccountMachineAccess accountToTagg = new AccountMachineAccess("testAccountCase1");
        accountToTagg.setLastEventTimeStamp(timestamp);
        accountToTagg.setFirstEventTimestamp(timestamp);


        //threshold  - 90 %

        //fill the account with 8 destinations  for the case 1 test (7 server , 1 desktop )
        accountToTagg.addDestination("testHost1",new Date().getTime(), ComputerUsageType.Server,false);
        accountToTagg.addDestination("testHost2",new Date().getTime(), ComputerUsageType.Server,false);
        accountToTagg.addDestination("testHost3",new Date().getTime(), ComputerUsageType.Server,false);
        accountToTagg.addDestination("testHost4",new Date().getTime(), ComputerUsageType.Server,false);
        accountToTagg.addDestination("testHost5",new Date().getTime(), ComputerUsageType.Server,false);
        accountToTagg.addDestination("testHost6",new Date().getTime(), ComputerUsageType.Server,false);
        accountToTagg.addDestination("testHost7",new Date().getTime(), ComputerUsageType.Server,false);
        accountToTagg.addDestination("testHost8",new Date().getTime(), ComputerUsageType.Desktop,false);

        UseServerDesktopTagImpl usdti = new UseServerDesktopTagImpl();
        usdti.setThreshold(0.9);
        usdti.setDaysBack(0l);
        usdti.setServerRegExpMachines("");

        usdti.tag(accountToTagg);

        assertTrue(accountToTagg.getTags().get("Server") && accountToTagg.getIsDirty());


        //Case 2 - un tag the server tagging ( 6server,2 desktops )
        accountToTagg.getDestinations().get("testHost1").setType(ComputerUsageType.Desktop);


        //Case 2 - account with 3 destinations and threshold 4 (un mark it as sweeper)
        accountToTagg.addDestination("testHost9",new Date().getTime(), ComputerUsageType.Desktop,false);
        usdti.tag(accountToTagg);

        assertTrue(!accountToTagg.getTags().get("Server")  && accountToTagg.getIsDirty());



        //Case 3 - account that have 50 % servers and 50 % desktop
        AccountMachineAccess accountToTagg2 = new AccountMachineAccess("testAccountCase3");
        accountToTagg2.setLastEventTimeStamp(timestamp);
        accountToTagg2.setFirstEventTimestamp(timestamp);

        accountToTagg2.addDestination("testHost1",new Date().getTime(), ComputerUsageType.Desktop,false);
        accountToTagg2.addDestination("testHost3",new Date().getTime(), ComputerUsageType.Server,false);
        usdti.tag(accountToTagg2);

        assertTrue(!accountToTagg2.getTags().get("Server")&& !accountToTagg2.getTags().get("Desktop")&& accountToTagg2.getIsDirty());



        //Case 4 - sweep account (move from server tag to desktop tag )
        accountToTagg.getDestinations().get("testHost2").setType(ComputerUsageType.Desktop);
        accountToTagg.getDestinations().get("testHost3").setType(ComputerUsageType.Desktop);
        accountToTagg.getDestinations().get("testHost4").setType(ComputerUsageType.Desktop);
        accountToTagg.getDestinations().get("testHost5").setType(ComputerUsageType.Desktop);
        accountToTagg.getDestinations().get("testHost6").setType(ComputerUsageType.Desktop);
        accountToTagg.getDestinations().get("testHost7").setType(ComputerUsageType.Desktop);
        accountToTagg.getDestinations().get("testHost9").setType(ComputerUsageType.Desktop);

        usdti.tag(accountToTagg);

        assertTrue(!accountToTagg.getTags().get("Server") && accountToTagg.getTags().get("Desktop")  && accountToTagg.getIsDirty());


        //Case 5 - tag per reg exp
        AccountMachineAccess regexpAccount = new AccountMachineAccess("idanp@fortscale.com");
        regexpAccount.setFirstEventTimestamp(timestamp);
        regexpAccount.setLastEventTimeStamp(timestamp);

        UseServerDesktopTagImpl usdti2  = new UseServerDesktopTagImpl();
        usdti2.setDesktopsRegExpMachines("(idanp|d|idan)@fortscale.com");
        usdti2.setThreshold(0.9);
        usdti2.setDaysBack(0l);

        usdti2.tag(regexpAccount);

        assertTrue(regexpAccount.getTags().get("Desktop") && regexpAccount.getIsDirty());


        //Case 6 - un tag account with less days data then the daysBack threshold
        AccountMachineAccess accountToTagg3 = new AccountMachineAccess("TestAccountCase6");
        accountToTagg3.setFirstEventTimestamp(timestamp);
        accountToTagg3.setLastEventTimeStamp(timestamp);

        usdti2  = new UseServerDesktopTagImpl();
        usdti2.setDesktopsRegExpMachines("");
        usdti2.setThreshold(0.9);
        usdti2.setDaysBack(4l);

        usdti2.tag(accountToTagg3);

        assertTrue(accountToTagg3.getTags().size() == 0 && !accountToTagg3.getIsDirty() );

        //all destinations are unknowen
        accountToTagg3 = new AccountMachineAccess("TestUnkown");
        accountToTagg3.setFirstEventTimestamp(timestamp);
        accountToTagg3.setLastEventTimeStamp(timestamp);
        MachineState machineState1 =  new MachineState("Unkown1");
        machineState1.setType(ComputerUsageType.Unknown);
        MachineState machineState2 =  new MachineState("Unkown2");
        machineState2.setType(ComputerUsageType.Unknown);
        MachineState machineState3 =  new MachineState("Unkown3");
        machineState3.setType(ComputerUsageType.Unknown);

        accountToTagg3.getServerDesktopDestination().put("Unkown1",machineState1);
        accountToTagg3.getServerDesktopDestination().put("Unkown1",machineState2);
        accountToTagg3.getServerDesktopDestination().put("Unkown1",machineState3);

        usdti2  = new UseServerDesktopTagImpl();
        usdti2.setDesktopsRegExpMachines("");
        usdti2.setThreshold(0.9);
        usdti2.setDaysBack(0l);

        usdti2.tag(accountToTagg3);

        assertTrue(!accountToTagg3.getTags().get("Desktop") && !accountToTagg3.getTags().get("Server") && accountToTagg3.getIsDirty() );





    }
}
