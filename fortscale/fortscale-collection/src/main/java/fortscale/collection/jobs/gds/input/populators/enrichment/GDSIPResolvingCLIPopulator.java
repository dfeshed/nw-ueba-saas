package fortscale.collection.jobs.gds.input.populators.enrichment;

import fortscale.collection.jobs.gds.helper.GDSUserInputHelper;
import fortscale.collection.jobs.gds.input.GDSCLIInputHandler;
import fortscale.collection.jobs.gds.input.GDSInputHandler;
import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.gds.state.GDSCompositeConfigurationState;

import java.util.HashMap;
import java.util.Map;

/**
 * IP Resolving command line populator
 *
 * @author gils
 * 03/01/2016
 */
public class GDSIPResolvingCLIPopulator implements GDSConfigurationPopulator {

    private GDSInputHandler gdsInputHandler = new GDSCLIInputHandler();

    private static final String RESTRICT_TO_AD_PARAM = "restrictToAD";
    private static final String SHORT_NAME_USAGE_PARAM = "shortNameUsage";
    private static final String EMPTY_STR = "";
    private static final String REMOVE_LAST_DOT_USAGE_PARAM = "removeLastDotUsage";
    private static final String DROP_ON_FAIL_USAGE_PARAM = "dropOnFailUsage";
    private static final String OVERRIDE_IP_WITH_HOST_NAME_USAGE_PARAM = "overrideIpWithHostNameUsage";
    private static final String TASK_NAME_PARAM = "taskName";
    private static final String OUTPUT_TOPIC_PARAM = "outputTopic";
    private static final String IP_FIELD_PARAM = "ipField";
    private static final String HOST_PARAM = "host";
    private static final String LAST_STATE_PARAM = "lastState";

    @Override
    public Map<String, ConfigurationParam> populateConfigurationData(GDSCompositeConfigurationState currentConfigurationState) throws Exception {
        Map<String, ConfigurationParam> paramsMap = new HashMap<>();

        String dataSourceName = currentConfigurationState.getDataSourceName();

        if (currentConfigurationState.isSourceIpResolvingRequired()) {
            System.out.println(String.format("Does %s resolving is restricted to AD name (in case of true and the machine doesn't exist in the AD it will not return it as resolved value) (y/n) ?", dataSourceName));
            paramsMap.put(RESTRICT_TO_AD_PARAM, new ConfigurationParam(RESTRICT_TO_AD_PARAM, GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput()), EMPTY_STR));

            System.out.println(String.format("Does %s resolving use the machine short name (i.e SERV1@DOMAINBLABLA instead of SERV1@DOMAINBLABLA.com) (y/n) ?", dataSourceName));
            paramsMap.put(SHORT_NAME_USAGE_PARAM, new ConfigurationParam(SHORT_NAME_USAGE_PARAM, GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput()), EMPTY_STR));

            System.out.println(String.format("Does %s resolving need to remove last dot from the resolved server name  (i.e SERV1@DOMAINBLABLA instead of SERV1@DOMAINBLABLA.) (y/n) ?", dataSourceName));
            paramsMap.put(REMOVE_LAST_DOT_USAGE_PARAM, new ConfigurationParam(REMOVE_LAST_DOT_USAGE_PARAM, GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput()), EMPTY_STR));

            System.out.println(String.format("Does %s resolving need to drop in case of resolving fail (y/n) ?", dataSourceName));
            paramsMap.put(DROP_ON_FAIL_USAGE_PARAM, new ConfigurationParam(DROP_ON_FAIL_USAGE_PARAM, GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput()), EMPTY_STR));

            System.out.println(String.format("Does %s resolving need to override the source ip field with the resolving value (y/n) ?", dataSourceName));
            paramsMap.put(OVERRIDE_IP_WITH_HOST_NAME_USAGE_PARAM, new ConfigurationParam(OVERRIDE_IP_WITH_HOST_NAME_USAGE_PARAM, GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput()), EMPTY_STR));

            paramsMap.put(TASK_NAME_PARAM, new ConfigurationParam(TASK_NAME_PARAM, false, "IpResolvingStreamTask_sourceIp"));
            if (currentConfigurationState.isTargetIpResolvingRequired()) {
                paramsMap.put(OUTPUT_TOPIC_PARAM, new ConfigurationParam(OUTPUT_TOPIC_PARAM, false, "fortscale-generic-data-access-source-ip-resolved"));
            }
            else {
                paramsMap.put(OUTPUT_TOPIC_PARAM, new ConfigurationParam(OUTPUT_TOPIC_PARAM, false, "fortscale-generic-data-access-ip-resolved"));
            }

            paramsMap.put(IP_FIELD_PARAM, new ConfigurationParam(IP_FIELD_PARAM, false, String.format("${impala.data.%s.table.field.source}", dataSourceName)));
            paramsMap.put(HOST_PARAM, new ConfigurationParam(HOST_PARAM, false, String.format("${impala.data.%s.table.field.source_name}", dataSourceName)));

            paramsMap.put(LAST_STATE_PARAM, new ConfigurationParam(LAST_STATE_PARAM, false, "IpResolvingStreamTask"));
        }

        if (currentConfigurationState.isTargetIpResolvingRequired()) {

            paramsMap.put(TASK_NAME_PARAM, new ConfigurationParam(TASK_NAME_PARAM, false, "IpResolvingStreamTask_targetIp"));
            paramsMap.put(OUTPUT_TOPIC_PARAM, new ConfigurationParam(OUTPUT_TOPIC_PARAM, false, "fortscale-generic-data-access-ip-resolved"));
            paramsMap.put(IP_FIELD_PARAM, new ConfigurationParam(IP_FIELD_PARAM, false, String.format("${impala.data.%s.table.field.target}", dataSourceName)));
            paramsMap.put(HOST_PARAM, new ConfigurationParam(HOST_PARAM, false, String.format("${impala.data.%s.table.field.target_name}", dataSourceName)));

            paramsMap.put(LAST_STATE_PARAM, new ConfigurationParam(LAST_STATE_PARAM, false, "IpResolvingStreamTask"));
        }

        return paramsMap;
    }
}
