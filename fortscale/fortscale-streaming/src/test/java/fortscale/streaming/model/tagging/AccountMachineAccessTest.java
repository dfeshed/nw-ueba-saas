package fortscale.streaming.model.tagging;

import fortscale.domain.core.ComputerUsageType;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class AccountMachineAccessTest {

    @Test
    public void testAddTag() throws Exception {

        //case 1 -  adding new tag that docent exist
        AccountMachineAccess amc = new AccountMachineAccess("testAccount");

        String tag = "Desktops";

        amc.addTag(tag);

        assertTrue(amc.getTags().contains(tag) && amc.getIsDirty());


        //case 2 - adding tag that already exist (ignore it )
        amc.setIsDirty(false);
        amc.addTag(tag);

        assertTrue(amc.getTags().contains(tag) && amc.getTags().size() == 1 && !amc.getIsDirty());

        //case 3 - Remove tag
        amc.removeTag(tag);

        assertTrue(amc.getTags().size() == 0 && amc.getIsDirty());


    }

    @Test
    public void testAddSource() throws Exception {

        AccountMachineAccess amc = new AccountMachineAccess("testAccount");

        String hostName="testHost";
        long currentTime = new Date().getTime();
        ComputerUsageType type = ComputerUsageType.Desktop;



        //case 1 - add new source to sources list
        amc.addSource(hostName,currentTime,type);
        assertTrue(amc.getSources().get(hostName).getHostName() == hostName);
        assertTrue(amc.getSources().get(hostName).getLastEventTimeStamp() == currentTime);
        assertTrue(amc.getSources().get(hostName).getType() == type);


        //case 2 - add source that already existing with newer time stamp (update time stamp)
        amc.setIsDirty(false);
        long newTime = currentTime + 60000;
        amc.addSource(hostName,newTime,type);

        assertTrue(amc.getSources().get(hostName).getHostName() == hostName);
        assertTrue(amc.getSources().get(hostName).getLastEventTimeStamp() == newTime);
        assertTrue(amc.getSources().get(hostName).getType() == type);





        //case 3 - add source that already exist with oldest time stamp (ignore)
        long oldTime = currentTime - 120000;
        amc.addSource(hostName,oldTime,type);

        assertTrue(amc.getSources().get(hostName).getHostName() == hostName);
        assertTrue(amc.getSources().get(hostName).getLastEventTimeStamp() == newTime);
        assertTrue(amc.getSources().get(hostName).getType() == type);


    }

    @Test
    public void testAddDestination() throws Exception {

        AccountMachineAccess amc = new AccountMachineAccess("testAccount");

        String hostName="testHost";
        long currentTime = new Date().getTime();
        ComputerUsageType type = ComputerUsageType.Desktop;



        //case 1 - add new destination to sources list
        amc.addDestination(hostName,currentTime,type);
        assertTrue(amc.getDestinations().get(hostName).getHostName() == hostName);
        assertTrue(amc.getDestinations().get(hostName).getLastEventTimeStamp() == currentTime);
        assertTrue(amc.getDestinations().get(hostName).getType() == type);


        //case 2 - add destination that already existing with newer time stamp (update time stamp)
        amc.setIsDirty(false);
        long newTime = currentTime + 60000;
        amc.addDestination(hostName,newTime,type);

        assertTrue(amc.getDestinations().get(hostName).getHostName() == hostName);
        assertTrue(amc.getDestinations().get(hostName).getLastEventTimeStamp() == newTime);
        assertTrue(amc.getDestinations().get(hostName).getType() == type);





        //case 3 - add destination that already exist with oldest time stamp (ignore)
        long oldTime = currentTime - 120000;
        amc.addDestination(hostName,oldTime,type);

        assertTrue(amc.getDestinations().get(hostName).getHostName() == hostName);
        assertTrue(amc.getDestinations().get(hostName).getLastEventTimeStamp() == newTime);
        assertTrue(amc.getDestinations().get(hostName).getType() == type);


    }
}