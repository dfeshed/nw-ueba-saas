package fortscale.streaming.model.tagging;

import fortscale.domain.core.ComputerUsageType;
import org.junit.Test;

import javax.xml.transform.Source;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.*;

public class AccountMachineAccessTest {

    @Test
    public void testAddTag() throws Exception {

        //case 1 -  adding new tag that docent exist
        AccountMachineAccess amc = new AccountMachineAccess("testAccount");

        String tag = "Desktops";

        amc.addTag(tag);

        assertTrue(amc.getTags().get(tag).booleanValue() && amc.getIsDirty());


        //case 2 - adding tag that already exist (ignore it )
        amc.setIsDirty(false);
        amc.addTag(tag);

        assertTrue(amc.getTags().get(tag).booleanValue() && !amc.getIsDirty());

        //case 3 - Remove tag
        amc.removeTag(tag);

        assertTrue(!amc.getTags().get(tag).booleanValue() && amc.getIsDirty());


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

    @Test
    public void testDilutionLists () throws Exception
    {
        AccountMachineAccess amc = new AccountMachineAccess("testAccount");
        long timestamp = new Date().getTime();

        amc.setLastEventTimeStamp(timestamp);

        //create 3 old source ( 3 days back )
        long threeDaysBack = timestamp - (3 * 24 * 60 * 60 * 1000);
        amc.addSource("sourceHost1",threeDaysBack,ComputerUsageType.Desktop);
        amc.addSource("sourceHost2",threeDaysBack,ComputerUsageType.Desktop);
        amc.addSource("sourceHost3",threeDaysBack,ComputerUsageType.Desktop);

        //create 3 new source
        amc.addSource("sourceHost1",timestamp,ComputerUsageType.Desktop);
        amc.addSource("sourceHost2",timestamp,ComputerUsageType.Desktop);
        amc.addSource("sourceHost3",timestamp,ComputerUsageType.Desktop);

        //create 6 old dest ( 3 days back )
        amc.addDestination("destHost1",threeDaysBack,ComputerUsageType.Desktop);
        amc.addDestination("destHost2",threeDaysBack,ComputerUsageType.Desktop);
        amc.addDestination("destHost3",threeDaysBack,ComputerUsageType.Desktop);
        amc.addDestination("destHost4",threeDaysBack,ComputerUsageType.Desktop);
        amc.addDestination("destHost5",threeDaysBack,ComputerUsageType.Desktop);
        amc.addDestination("destHost6",threeDaysBack,ComputerUsageType.Desktop);

        //create 3 new dest
        amc.addDestination("destHost1",timestamp,ComputerUsageType.Desktop);
        amc.addDestination("destHost2",timestamp,ComputerUsageType.Desktop);
        amc.addDestination("destHost3",timestamp,ComputerUsageType.Desktop);

        amc.dilutionLists(0l);

        Map<String,MachineState> source = amc.getSources();
        Collection<MachineState> SourceList = source.values();
        Map<String,MachineState> dest = amc.getDestinations();
        Collection<MachineState> destList = dest.values();

        long srcMaxTs = 0l;
        long destMaxTs = 0l;

        for (MachineState ms : SourceList)
        {
            srcMaxTs = ms.getLastEventTimeStamp() > srcMaxTs ? ms.getLastEventTimeStamp() : srcMaxTs;
        }

        for (MachineState ms : destList)
        {
            destMaxTs =  ms.getLastEventTimeStamp() > destMaxTs ? ms.getLastEventTimeStamp() : destMaxTs;
        }


        assertTrue(source.size() == 3 && srcMaxTs == timestamp && dest.size() == 3 &&  destMaxTs == timestamp);


    }
}