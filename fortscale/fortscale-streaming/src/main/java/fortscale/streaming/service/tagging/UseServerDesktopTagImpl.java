package fortscale.streaming.service.tagging;

import fortscale.domain.core.ComputerUsageType;
import fortscale.domain.core.Tag;
import fortscale.services.*;
import fortscale.services.TagService;
import fortscale.streaming.model.tagging.AccountMachineAccess;
import fortscale.streaming.model.tagging.MachineState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by idanp on 7/10/2014.
 */
public class UseServerDesktopTagImpl implements ServiceAccountTagging {

    @Value("${ServerDesktop.rate.threshold}") //get the value from fortscale-overriding-streaming.properties file
    private Double threshold;

    @Value("${Servers.RegExp:}") //get the value from fortscale-overriding-streaming.properties file
    private String ServerRegExpMachines;

    @Value("${Desktops.RegExp:}") //get the value from fortscale-overriding-streaming.properties file
    private String DesktopsRegExpMachines;

    @Value("${ServerDesktop.min.daysBack.toWatch}") //get the value from fortscale-overriding-streaming.properties file
    private Long daysBack;

    private Pattern ServerRegExpPattern;
    private Pattern DesktopsRegExpPattern;

    private static final String endpointTag = "Endpoint";

    public UseServerDesktopTagImpl() {

        if(ServerRegExpMachines != null)
            ServerRegExpPattern = Pattern.compile(ServerRegExpMachines);
        if(DesktopsRegExpMachines != null)
            DesktopsRegExpPattern = Pattern.compile(DesktopsRegExpMachines);

    }

    public Long getDaysBack() {
        return daysBack;
    }

    public void setDaysBack(Long daysBack) {
        this.daysBack = daysBack;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    public void setServerRegExpMachines(String serverRegExpMachines) {

        ServerRegExpMachines = serverRegExpMachines;
        ServerRegExpPattern = Pattern.compile(ServerRegExpMachines);
    }

    public void setDesktopsRegExpMachines(String desktopsRegExpMachines) {
        DesktopsRegExpMachines = desktopsRegExpMachines;
        DesktopsRegExpPattern = Pattern.compile(DesktopsRegExpMachines);
    }

    public void tag(AccountMachineAccess account)
    {
        Matcher ServerMatcher;
        Matcher DesktopsMatcher;

        if(ServerRegExpPattern == null && ServerRegExpMachines!= null )
            ServerRegExpPattern =  Pattern.compile(this.ServerRegExpMachines); // generate the reg excretion pattern

        if(DesktopsRegExpPattern == null && DesktopsRegExpMachines!= null )
            DesktopsRegExpPattern =  Pattern.compile(this.DesktopsRegExpMachines); // generate the reg excretion pattern

        if(account == null ||account.getDestinations() == null)
            return;

        //Calculate the date of start counting in millisec
        long theStartDateToCheckFrom = account.getLastEventTimeStamp() - this.daysBack*24*60*60*1000;

        //Validate if the current account configure as reg exp
        if (this.ServerRegExpPattern != null) {
            ServerMatcher = this.ServerRegExpPattern.matcher(account.getUserName());
            if (ServerMatcher.matches()) {
                account.addTag(ComputerUsageType.Server.toString(), true);
                return;
            }

        }

        if (this.DesktopsRegExpPattern != null) {
            DesktopsMatcher = this.DesktopsRegExpPattern.matcher(account.getUserName());
            if (DesktopsMatcher.matches()) {
                account.addTag(ComputerUsageType.Desktop.toString(), true);
                return;
            }
        }


        //Validate that you have enough time of data (the first event for that account occur at least "dayBack" from the current one)
        if(account.getFirstEventTimestamp() <=  theStartDateToCheckFrom) {
            tagPerType(account);

        }


    }

    @Override public void addTagToMongo(TagService tagService) {
        tagService.addTag(new Tag(ComputerUsageType.Server.name(),
                ComputerUsageType.Server.toString(), false, false));
        tagService.addTag(new Tag(endpointTag, endpointTag, false, false));
    }

    private void tagPerType(AccountMachineAccess account)
    {

        int numOfDistinctDestinations = account.getServerDesktopDestination().size();

        //calculate the number that represent the thershold precent round down (for example 0.9 from 8 destination will be 7 cause the true value is 7.2)
        int breakPointValue  =  (numOfDistinctDestinations == 1 ? 1 : (numOfDistinctDestinations  == 2 ? 2 : ((int) Math.floor(threshold*numOfDistinctDestinations))));

        int numOfServersMachines=0;
        int numOfDesdktopsMachines = 0;

        for(Map.Entry<String,MachineState> entry : account.getDestinations().entrySet())
        {
            if(entry.getValue().getType() == ComputerUsageType.Server)
                numOfServersMachines++;
            else if (entry.getValue().getType() == ComputerUsageType.Desktop)
                numOfDesdktopsMachines++;

        }



        account.addTag(ComputerUsageType.Desktop.toString(),(breakPointValue > 0 && numOfDesdktopsMachines >= breakPointValue));
        account.addTag(ComputerUsageType.Server.toString(),( breakPointValue > 0 &&  numOfServersMachines >= breakPointValue));


    }
}
