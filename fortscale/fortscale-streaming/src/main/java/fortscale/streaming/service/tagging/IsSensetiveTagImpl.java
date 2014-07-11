package fortscale.streaming.service.tagging;

import fortscale.streaming.model.tagging.AccountMachineAccess;
import fortscale.streaming.model.tagging.MachineState;

import java.util.Map;

/**
 * Created by idanp on 7/11/2014.
 */
public class IsSensetiveTagImpl implements ServiceAccountTagging {


    public IsSensetiveTagImpl() {
    }

    public void tag (AccountMachineAccess account)
    {
        if(account == null ||account.getDestinations() == null)
            return;

        if(account.getTags().size() > 0 &&  account.getTags().get("Sensitive") != null &&  account.getTags().get("Sensitive"))
            return;

        for(Map.Entry<String,MachineState> entry : account.getDestinations().entrySet())
        {
            if(entry.getValue().isSensitiveMachine())
                account.addTag("Sensitive",true);
        }
    }
}
