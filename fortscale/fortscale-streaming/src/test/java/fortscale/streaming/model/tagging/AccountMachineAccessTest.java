package fortscale.streaming.model.tagging;

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

        //case 3 - adding server tag when Desktops tag already exist (switch between them)
        amc.setIsDirty(false);
        amc.addTag("Servers");

        assertTrue(amc.getTags().contains("Servers") && !amc.getTags().contains(tag) && amc.getIsDirty());

        //case 4 - adding desktops tag when server tag already exist (switch between them)
        amc.setIsDirty(false);
        amc.addTag(tag);

        assertTrue(amc.getTags().contains(tag) && !amc.getTags().contains("Servers") && amc.getIsDirty());

    }

    @Test
    public void testAddSource() throws Exception {

        AccountMachineAccess amc = new AccountMachineAccess("testAccount");

        MachineState ms = new MachineState("testHost");
        long currentTime = new Date().getTime();
        ms.setLastEventTimeStamp(currentTime);


        //case 1 - add new source to sources list
        amc.addSource(ms);
        assertTrue(amc.getSources().contains(ms) && amc.getIsDirty());

        //case 2 - add source that already existing with newer time stamp (update time stamp)
        amc.setIsDirty(false);
        ms  =  new MachineState("testHost");
        ms.setLastEventTimeStamp(currentTime+60000);
        amc.addSource(ms);

        assertTrue(amc.getSources().size() == 1 && amc.getSources().get(0).getLastEventTimeStamp() == currentTime + 60000 && amc.getIsDirty());


        //case 3 - add source that already exist with oldest time stamp (ignore)
        amc.setIsDirty(false);
        ms  =  new MachineState("testHost");
        ms.setLastEventTimeStamp(currentTime - 120000);
        amc.addSource(ms);

        assertTrue(amc.getSources().size() == 1 && amc.getSources().get(0).getLastEventTimeStamp() == currentTime + 60000 && !amc.getIsDirty());

    }

    @Test
    public void testAddDestination() throws Exception {

        AccountMachineAccess amc = new AccountMachineAccess("testAccount");

        MachineState ms = new MachineState("testHost");
        long currentTime = new Date().getTime();
        ms.setLastEventTimeStamp(currentTime);


        //case 1 - add new source to sources list
        amc.addDestination(ms);
        assertTrue(amc.getDestinations().contains(ms) && amc.getIsDirty());

        //case 2 - add source that already existing with newer time stamp (update time stamp)
        amc.setIsDirty(false);
        ms  =  new MachineState("testHost");
        ms.setLastEventTimeStamp(currentTime+60000);
        amc.addDestination(ms);

        assertTrue(amc.getDestinations().size() == 1 && amc.getDestinations().get(0).getLastEventTimeStamp() == currentTime + 60000 && amc.getIsDirty());


        //case 3 - add source that already exist with oldest time stamp (ignore)
        amc.setIsDirty(false);
        ms  =  new MachineState("testHost");
        ms.setLastEventTimeStamp(currentTime - 120000);
        amc.addDestination(ms);

        assertTrue(amc.getDestinations().size() == 1 && amc.getDestinations().get(0).getLastEventTimeStamp() == currentTime + 60000 && !amc.getIsDirty());

    }
}