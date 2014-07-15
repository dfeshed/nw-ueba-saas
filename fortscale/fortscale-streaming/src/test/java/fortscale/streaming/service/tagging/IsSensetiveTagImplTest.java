package fortscale.streaming.service.tagging;

import fortscale.domain.core.ComputerUsageType;
import fortscale.streaming.model.tagging.AccountMachineAccess;
import org.apache.samza.storage.kv.KeyValueStore;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IsSensetiveTagImplTest {

    @Test
    public void testTag() throws Exception {



        long timestamp = new Date().getTime();

        AccountMachineAccess accountToTagg = new AccountMachineAccess("testAccountCase1");
        accountToTagg.setLastEventTimeStamp(timestamp);
        accountToTagg.setFirstEventTimestamp(timestamp);

        accountToTagg.addDestination("testHost1",new Date().getTime(), ComputerUsageType.Desktop,true);

        IsSensetiveTagImpl isti = new IsSensetiveTagImpl();
        isti.setIsSensitiveRegExMachines("");

        isti.tag(accountToTagg);

        assertTrue(accountToTagg.getTags().get("Sensitive") && accountToTagg.getIsDirty());



        //Case 2 - Don't unremarked the sensitive field
        accountToTagg.getDestinations().remove("testHost1");

        accountToTagg.addDestination("testHost1",new Date().getTime(), ComputerUsageType.Desktop,false);

        isti.tag(accountToTagg);


        assertTrue(accountToTagg.getTags().get("Sensitive") && accountToTagg.getIsDirty());


    }
}