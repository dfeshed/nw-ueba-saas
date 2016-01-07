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
    private static final String TASK_NAME_PARAM = "taskName";
    private static final String OUTPUT_TOPIC_PARAM = "outputTopic";
    private static final String EMPTY_STR = "";
    private static final String ANY_ROW_PARAM = "anyRow";
    private static final String STATUS_FIELD_NAME_PARAM = "statusFieldName";
    private static final String SUCCESS_VALUE_PARAM = "successValue";
    private GDSInputHandler gdsInputHandler = new GDSCLIInputHandler();

    @Override
    public Map<String, ConfigurationParam> populateConfigurationData(GDSCompositeConfigurationState currentConfigurationState) throws Exception {
        Map<String, ConfigurationParam> paramsMap = new HashMap<>();

        String dataSourceName = currentConfigurationState.getDataSourceName();

        System.out.println(String.format("Going to configure the UserMongoUpdate task for %s (i.e we use it for user last activity update) ", dataSourceName));

        paramsMap.put(TASK_NAME_PARAM, new ConfigurationParam(TASK_NAME_PARAM, false, "UserMongoUpdateStreamTask"));
        paramsMap.put(OUTPUT_TOPIC_PARAM, new ConfigurationParam(OUTPUT_TOPIC_PARAM, false, EMPTY_STR));

        //Status field value
        System.out.println("Do you want to update last activity for any raw that came and not only successed events (y/n)? ");
        boolean isAnyRow = GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput());
        paramsMap.put(ANY_ROW_PARAM, new ConfigurationParam(ANY_ROW_PARAM, isAnyRow, ""));

        if (!isAnyRow) {
            paramsMap.put(STATUS_FIELD_NAME_PARAM, new ConfigurationParam(STATUS_FIELD_NAME_PARAM, false, "status"));

            System.out.println("Please enter value that mark event as succeeded (e.g. Accepted for ssh or SUCCESS for vpn 0x0 for kerberos) :");
            paramsMap.put(SUCCESS_VALUE_PARAM, new ConfigurationParam(SUCCESS_VALUE_PARAM, false, gdsInputHandler.getInput()));
        }

        System.out.println(String.format("End configure the UserMongoUpdate task for %s", dataSourceName));

        return paramsMap;
    }
}
