package fortscale.streaming.service.tagging;

import fortscale.domain.core.ComputerUsageType;
import fortscale.streaming.model.tagging.AccountMachineAccess;
import fortscale.streaming.model.tagging.MachineState;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by idanp on 7/10/2014.
 */
public class UseServerDesktopTagImpl implements ServiceAccountTagging {

    @Value("${ServerDesktopThreshold}") //get the value from fortscale-overriding-streaming.properties file
    private Double threshold;

    @Value("${ServersRegExp}") //get the value from fortscale-overriding-streaming.properties file
    private String ServerRegExpMachines;

    @Value("${DesktopsRegExp}") //get the value from fortscale-overriding-streaming.properties file
    private String DesktopsRegExpMachines;

    @Value("${ServerDesktopMinimumDaysBack}") //get the value from fortscale-overriding-streaming.properties file
    private Long daysBack;

    private Pattern ServerRegExpPattern;
    private Pattern DesktopsRegExpPattern;



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

        if(account == null ||account.getDestinations() == null)
            return;

        //Calculate the date of start counting in millisec
        long theStartDateToCheckFrom = account.getLastEventTimeStamp() - this.daysBack*24*60*60*1000;


        //Validate that you have enough time of data (the first event for that account occur at least "dayBack" from the current one)
        if(account.getFirstEventTimestamp() <=  theStartDateToCheckFrom) {

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


            tagPerType(account, ComputerUsageType.Desktop);
            tagPerType(account, ComputerUsageType.Server);
        }


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
