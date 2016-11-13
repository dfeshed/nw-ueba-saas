package fortscale.streaming.service.tagging;

import fortscale.domain.core.Tag;
import fortscale.services.TagService;
import fortscale.streaming.model.tagging.AccountMachineAccess;
import fortscale.streaming.model.tagging.MachineState;
import org.springframework.beans.factory.annotation.Value;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by idanp on 7/7/2014.
 */
public class SweeperTagImpl implements ServiceAccountTagging {

    @Value("${sweeper.min.destinations.count.threshold}") //get the value from fortscale-overriding-streaming.properties file
    private Double threshold;

    @Value("${sweeper.daysBack.toWatch}") //get the value from fortscale-overriding-streaming.properties file
    private long daysBack;

    @Value("${sweeper.RegExp:}") //get the value from fortscale-overriding-streaming.properties file
    private String regExpMachines;

    private Pattern regExpPattern;

    private static final String SweeperTag =  "Sweeper";


    public SweeperTagImpl() {

        if(this.regExpMachines != null)
            regExpPattern =  Pattern.compile(this.regExpMachines); // generate the reg excretion pattern

    }

    @Override
    public void addTagToMongo(TagService tagService) {
        tagService.addTag(new Tag(SweeperTag, SweeperTag, false, false));
    }



    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    public void setDaysBack(long daysBack) {
        this.daysBack = daysBack;
    }

    public void setRegExpMachines(String regExpMachines) {
        this.regExpMachines = regExpMachines;
        regExpPattern =  Pattern.compile(this.regExpMachines); // generate the reg excretion pattern
    }

    public void tag(AccountMachineAccess account)
    {

        if(account == null ||account.getDestinations() == null)
            return;

        if(regExpPattern == null && regExpMachines!= null )
            regExpPattern =  Pattern.compile(this.regExpMachines); // generate the reg excretion pattern

        Matcher match= regExpPattern.matcher(account.getUserName());


        //check first if the account is configure at the regex list
        if(match.matches())
        {
            account.addTag(SweeperTag,true);
            return;

        }


        long theStartDateToCheckFrom = account.getLastEventTimeStamp() - this.daysBack*24*60*60*1000;

        int machineCounter= 0;

        //Get the number of destination for that account (the distinct forced at the insertion )
        //scan the destination list and count all the accesses from the theStartDateToCheckFrom
        for(MachineState ms : account.getDestinations().values())
        {
            if(ms.getLastEventTimeStamp() >= theStartDateToCheckFrom)
                machineCounter++;
        }

        account.addTag(SweeperTag,(machineCounter >= threshold));



    }
}
