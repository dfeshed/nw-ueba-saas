package fortscale.collection.jobs.gds.input.populators.enrichment;

import fortscale.collection.jobs.gds.helper.GDSUserInputHelper;
import fortscale.collection.jobs.gds.input.GDSCLIInputHandler;
import fortscale.collection.jobs.gds.input.GDSInputHandler;
import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.gds.state.GDSCompositeConfigurationState;

import java.util.HashMap;
import java.util.Map;

/**
 * User Mongo update command line populator
 *
 * @author gils
 * 03/01/2016
 */
public class GDSUserMongoUpdateCLIPopulator implements GDSConfigurationPopulator {
    private GDSInputHandler gdsInputHandler = new GDSCLIInputHandler();

    @Override
    public Map<String, ConfigurationParam> populateConfigurationData(GDSCompositeConfigurationState currentConfigurationState) throws Exception {
        Map<String, ConfigurationParam> paramsMap = new HashMap<>();

        String dataSourceName = currentConfigurationState.getDataSourceName();

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

        System.out.println(String.format("End configure the UserMongoUpdate task for %s", dataSourceName));

        return paramsMap;
    }
}
