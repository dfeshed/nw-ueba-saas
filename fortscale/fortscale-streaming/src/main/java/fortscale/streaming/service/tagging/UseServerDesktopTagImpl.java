package fortscale.streaming.service.tagging;

import fortscale.domain.core.ComputerUsageType;
import fortscale.streaming.model.tagging.AccountMachineAccess;
import fortscale.streaming.model.tagging.MachineState;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

/**
 * Created by idanp on 7/10/2014.
 */
public class UseServerDesktopTagImpl implements ServiceAccountTagging {

    @Value("${ServerDesktopThreshold}") //get the value from fortscale-overriding-streaming.properties file
    private Double threshold;

    public UseServerDesktopTagImpl() {
    }


    public void tag(AccountMachineAccess account)
    {

        if(account == null ||account.getDestinations() == null)
            return;

        tagPerType(account,ComputerUsageType.Desktop);
        tagPerType(account, ComputerUsageType.Server);


    }

    private void tagPerType(AccountMachineAccess account,ComputerUsageType type)
    {

        int numOfDistinctDestinations = account.getDestinations().size();

        //calculate the number that represent the thershold precent round down (for example 0.9 from 8 destination will be 7 cause the true value is 7.2)
        int breakPointValue  =  (numOfDistinctDestinations == 1 ? 1 : (numOfDistinctDestinations  == 2 ? 2 : ((int) Math.floor(threshold*numOfDistinctDestinations))));

        int numOfTagType=0;

        for(Map.Entry<String,MachineState> entry : account.getDestinations().entrySet())
        {
            if(entry.getValue().getType() == type)
                numOfTagType++;

        }


        account.addTag(type.toString(),(numOfTagType >= breakPointValue));


    }
}
