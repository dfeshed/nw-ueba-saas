package fortscale.streaming.service.tagging;

import fortscale.streaming.model.tagging.AccountMachineAccess;
import fortscale.streaming.model.tagging.MachineState;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by idanp on 7/11/2014.
 */
public class IsSensetiveTagImpl implements ServiceAccountTagging {

    @Value("${IsSensitiveRegEx}") //get the value from fortscale-overriding-streaming.properties file
    private String IsSensitiveRegExMachines;

    private Pattern regExpPattern;



    public IsSensetiveTagImpl() {

        if(IsSensitiveRegExMachines!=null)
            regExpPattern = Pattern.compile(IsSensitiveRegExMachines);

    }

    public void setIsSensitiveRegExMachines(String isSensitiveRegExMachines) {
        IsSensitiveRegExMachines = isSensitiveRegExMachines;
        regExpPattern = Pattern.compile(IsSensitiveRegExMachines);
    }

    public void tag (AccountMachineAccess account)
    {
        if(account == null ||account.getDestinations() == null)
            return;

        if(account.getTags().size() > 0 &&  account.getTags().get("Sensitive") != null &&  account.getTags().get("Sensitive"))
            return;

        Matcher match= regExpPattern.matcher(account.getUserName());

        //check first if the account is configure at the regex list
        if(match.matches())
        {
            account.addTag("Sensitive",true);
            return;

        }



        for(Map.Entry<String,MachineState> entry : account.getDestinations().entrySet())
        {
            if(entry.getValue().isSensitiveMachine())
                account.addTag("Sensitive",true);
        }
    }
}
