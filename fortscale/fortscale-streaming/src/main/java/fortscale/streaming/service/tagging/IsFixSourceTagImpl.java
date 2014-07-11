package fortscale.streaming.service.tagging;

import fortscale.streaming.model.tagging.AccountMachineAccess;
import fortscale.streaming.model.tagging.MachineState;
import org.springframework.beans.factory.annotation.Value;

/**
 * Created by idanp on 7/11/2014.
 */
public class IsFixSourceTagImpl implements ServiceAccountTagging {


    @Value("${FixSourceTagThreshold}") //get the value from fortscale-overriding-streaming.properties file
    private Double threshold;

    @Value("${FixSourceTagDaysBack}") //get the value from fortscale-overriding-streaming.properties file
    private long dayBack;


    public IsFixSourceTagImpl() {
    }



    public Double getThreshold() {
        return threshold;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    public long getDayBack() {
        return dayBack;
    }

    public void setDayBack(long dayBack) {
        this.dayBack = dayBack;
    }


    public void tag(AccountMachineAccess account)
    {

        //Calculate the date of start counting in millisec
        long theStartDateToCheckFrom = account.getLastEventTimeStamp() - this.dayBack*24*60*60*1000;

        int machineCounter = 0;


        //scan the destination list and count all the accesses from the theStartDateToCheckFrom
        for(MachineState ms : account.getDestinations().values())
        {
            if(ms.getLastEventTimeStamp() >= theStartDateToCheckFrom)
                machineCounter++;
        }

        account.addTag("Fixed Source",machineCounter >= this.threshold);



    }


}
