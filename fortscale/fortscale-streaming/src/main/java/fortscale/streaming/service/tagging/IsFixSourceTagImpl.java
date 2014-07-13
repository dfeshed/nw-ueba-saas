package fortscale.streaming.service.tagging;

import fortscale.streaming.model.tagging.AccountMachineAccess;
import fortscale.streaming.model.tagging.MachineState;
import org.springframework.beans.factory.annotation.Value;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by idanp on 7/11/2014.
 */
public class IsFixSourceTagImpl implements ServiceAccountTagging {


    @Value("${FixSourceTagThreshold}") //get the value from fortscale-overriding-streaming.properties file
    private Double threshold;

    @Value("${FixSourceTagDaysBack}") //get the value from fortscale-overriding-streaming.properties file
    private long dayBack;

    @Value("${FixSourceRegExp}") //get the value from fortscale-overriding-streaming.properties file
    private String isFixSourceRegExpMachines;

    private Pattern regExpPattern;


    public IsFixSourceTagImpl() {

        if(isFixSourceRegExpMachines!=null)
             regExpPattern = Pattern.compile(this.isFixSourceRegExpMachines);

    }


    public IsFixSourceTagImpl(String isFixSourceRegExpMachines) {

        regExpPattern = Pattern.compile(this.isFixSourceRegExpMachines);

    }





    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    public void setDayBack(long dayBack) {
        this.dayBack = dayBack;
    }

    public void setIsFixSourceRegExpMachines(String isFixSourceRegExpMachines) {
        this.isFixSourceRegExpMachines = isFixSourceRegExpMachines;
        regExpPattern = Pattern.compile(this.isFixSourceRegExpMachines);
    }

    public void tag(AccountMachineAccess account)
    {

        if(account == null ||account.getSources() == null)
            return;

        Matcher match= regExpPattern.matcher(account.getUserName());

        //check first if the account is configure at the regex list
        if(match.matches())
        {
            account.addTag("Fixed Source",true);
            return;

        }


        //Calculate the date of start counting in millisec
        long theStartDateToCheckFrom = account.getLastEventTimeStamp() - this.dayBack*24*60*60*1000;


        //Validate that you have enough time of data (the first event for that account occur at least "dayBack" from the current one)
        if(account.getFirstEventTimestamp() <=  theStartDateToCheckFrom) {

            int machineCounter = 0;


            //scan the destination list and count all the accesses from the theStartDateToCheckFrom
            for (MachineState ms : account.getSources().values()) {
                if (ms.getLastEventTimeStamp() >= theStartDateToCheckFrom)
                    machineCounter++;
            }

            account.addTag("Fixed Source", machineCounter >= this.threshold);
        }



    }


}
