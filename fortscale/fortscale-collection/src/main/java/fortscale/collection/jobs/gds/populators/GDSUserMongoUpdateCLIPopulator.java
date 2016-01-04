package fortscale.collection.jobs.gds.populators;

import fortscale.collection.jobs.gds.helper.GDSUserInputHelper;
import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.state.GDSConfigurationStateImpl;

import java.util.Map;

/**
 * @author gils
 *         03/01/2016
 */
public class GDSUserMongoUpdateCLIPopulator implements GDSConfigurationPopulator {
    @Override
    public Map<String, ConfigurationParam> populateConfigurationData(GDSConfigurationStateImpl currentConfigurationState) throws Exception {
        //USER MONGO UPDATE
        System.out.println(String.format("Going to configure the UserMongoUpdate task for %s (i.e we use it for user last activity update) ", dataSourceName));

        paramsMap.put("taskName", new ConfigurationParam("taskName", false, String.format("UserMongoUpdateStreamTask",dataSourceName)));
        paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, ""));


        //Status field value
        System.out.println("Do you want to update last activity for any raw that came and not only successed events (y/n)? ");
        boolean isAnyRow = GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput());
        paramsMap.put("anyRow", new ConfigurationParam("anyRow", isAnyRow, ""));


        if (!isAnyRow) {
            //configure the field that represent the status
            //System.out.println(String.format("Please enter the field that will hold the message status   (i.e status,failure_code):"));
            //String statusFieldName =  gdsInputHandler.getInput();
            paramsMap.put("statusFieldName", new ConfigurationParam("statusFieldName", false, "status"));

            //SUCCESS  value
            System.out.println("Please enter value that mark event as successed (i.c Accepted for ssh or SUCCESS for vpn 0x0 for kerberos ) :");
            paramsMap.put("successValue", new ConfigurationParam("successValue", false, gdsInputHandler.getInput()));
        }


//        userMongoUpdateTaskService.setConfigurationParams(paramsMap);
//        System.out.println("Finished to configure user mongo update streaming task. Do you want to apply changes now? (y/n)");
//
//        if (GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput())) {
//            if (userMongoUpdateTaskService.init()) {
//                userMongoUpdateTaskService.applyConfiguration();
//            }
//        }
//
//        System.out.println("Do you want to reset user mongo update streaming task changes? (y/n)");
//
//        if (GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput())) {
//            reset();
//        }
//
//        userMongoUpdateTaskService.done();
        System.out.println(String.format("End configure the UserMongoUpdate task for %s", dataSourceName));

    }
}
