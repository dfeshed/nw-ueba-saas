package fortscale.streaming.service.tagging;

import fortscale.domain.core.ComputerUsageType;
import fortscale.streaming.model.tagging.AccountMachineAccess;
import org.apache.samza.storage.kv.KeyValueStore;
import org.junit.Test;

import static org.mockito.BDDMockito.given;
import static  org.mockito.Mockito.*;

import java.util.Date;

import static org.junit.Assert.*;

public class TagServiceTest {

    @Test
    public void testTagAccount() throws Exception {


       KeyValueStore<String,AccountMachineAccess> mockKeyValue = mock(KeyValueStore.class);

        mockKeyValue.put("noiseTestAccount1",new AccountMachineAccess("noiseTestAccount1"));
        mockKeyValue.put("noiseTestAccount2",new AccountMachineAccess("noiseTestAccount2"));
        mockKeyValue.put("noiseTestAccount3",new AccountMachineAccess("noiseTestAccount3"));
        mockKeyValue.put("noiseTestAccount4",new AccountMachineAccess("noiseTestAccount4"));



        long timestamp = new Date().getTime();


        //create the account instance for case 1
        AccountMachineAccess accountToTagg = new AccountMachineAccess("testAccountCase1");
        accountToTagg.setLastEventTimeStamp(timestamp);



        //fill the account with 4 destinationd for the case 1 test
        accountToTagg.addDestination("testHost1",new Date().getTime(), ComputerUsageType.Desktop);
        accountToTagg.addDestination("testHost2",new Date().getTime(), ComputerUsageType.Desktop);
        accountToTagg.addDestination("testHost3",new Date().getTime(), ComputerUsageType.Desktop);
        accountToTagg.addDestination("testHost4",new Date().getTime(), ComputerUsageType.Desktop);

        mockKeyValue.put("testAccountCase1",accountToTagg);

        //create the account instance for case 2
        AccountMachineAccess accountToTagg2 = new AccountMachineAccess("testAccountCase2");
        accountToTagg2.setLastEventTimeStamp(timestamp);

        //fill the account with 2 destinations for the case 2  test
        accountToTagg2.addDestination("testHost1",new Date().getTime(), ComputerUsageType.Desktop);
        accountToTagg2.addDestination("testHost2",new Date().getTime(), ComputerUsageType.Desktop);

        mockKeyValue.put("testAccountCase2",accountToTagg2);


        when(mockKeyValue.get("testAccountCase1")).thenReturn(accountToTagg);
        when(mockKeyValue.get("testAccountCase2")).thenReturn(accountToTagg2);

        AccountMachineAccess test =  mockKeyValue.get("testAccountCase2");




        //create the service instance
        TagService ts = new TagService(mockKeyValue,30l);


        //Case 1 - account with 5 destinations and threshold 4 ( mark it as sweeper)
        ts.handleAccount("testAccountCase1",timestamp,"sourcetestHost5","testHost5",ComputerUsageType.Desktop,ComputerUsageType.Desktop);

        assertTrue(accountToTagg.getTags().get("Sweeper") && accountToTagg.getIsDirty());

        //Case 2 - account with 3 destinations and threshold 4 (un mark it as sweeper)
        ts.handleAccount("testAccountCase2",timestamp,"sourcetestHost3","testHost3",ComputerUsageType.Desktop,ComputerUsageType.Desktop);

        assertTrue(accountToTagg2.getTags().get("Sweeper") == null  && !accountToTagg2.getIsDirty());



    }
}