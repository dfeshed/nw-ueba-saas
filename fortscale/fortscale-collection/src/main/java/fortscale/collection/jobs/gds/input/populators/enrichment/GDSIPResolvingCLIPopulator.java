package fortscale.collection.jobs.gds.input.populators.enrichment;

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
    private static final String HOST_PARAM = "hostField";
    private static final String LAST_STATE_PARAM = "lastState";

    private static final String GDS_CONFIG_ENTRY = "gds.config.entry.";
    private static final String SOURCE_IP_CONFIG_ENTRY = "source.";
    private static final String TARGET_IP_CONFIG_ENTRY = "target.";

    @Override
    public Map<String, Map<String, ConfigurationParam>> populateConfigurationData(GDSCompositeConfigurationState currentConfigurationState) throws Exception {
        Map<String, Map<String, ConfigurationParam>> configurationsMap = new HashMap<>();

        String dataSourceName = currentConfigurationState.getDataSourceName();

        if (currentConfigurationState.getStreamingTopologyDefinitionState().isSourceIpResolvingRequired()) {
            HashMap<String, ConfigurationParam> sourceIPParamsMap = new HashMap<>();

            configurationsMap.put(GDS_CONFIG_ENTRY + SOURCE_IP_CONFIG_ENTRY, sourceIPParamsMap);

            System.out.println(String.format("Does %s resolving is restricted to AD name (in case of true and the machine doesn't exist in the AD it will not return it as resolved value) (y/n) ?", dataSourceName));
            sourceIPParamsMap.put(RESTRICT_TO_AD_PARAM, new ConfigurationParam(RESTRICT_TO_AD_PARAM, gdsInputHandler.getYesNoInput(), EMPTY_STR));

            System.out.println(String.format("Does %s resolving use the machine short name (i.e SERV1@DOMAINBLABLA instead of SERV1@DOMAINBLABLA.com) (y/n) ?", dataSourceName));
            sourceIPParamsMap.put(SHORT_NAME_USAGE_PARAM, new ConfigurationParam(SHORT_NAME_USAGE_PARAM, gdsInputHandler.getYesNoInput(), EMPTY_STR));

            System.out.println(String.format("Does %s resolving need to remove last dot from the resolved server name  (i.e SERV1@DOMAINBLABLA instead of SERV1@DOMAINBLABLA.) (y/n) ?", dataSourceName));
            sourceIPParamsMap.put(REMOVE_LAST_DOT_USAGE_PARAM, new ConfigurationParam(REMOVE_LAST_DOT_USAGE_PARAM, gdsInputHandler.getYesNoInput(), EMPTY_STR));

            System.out.println(String.format("Does %s resolving need to drop in case of resolving fail (y/n) ?", dataSourceName));
            sourceIPParamsMap.put(DROP_ON_FAIL_USAGE_PARAM, new ConfigurationParam(DROP_ON_FAIL_USAGE_PARAM, gdsInputHandler.getYesNoInput(), EMPTY_STR));

            System.out.println(String.format("Does %s resolving need to override the source ip field with the resolving value (y/n) ?", dataSourceName));
            sourceIPParamsMap.put(OVERRIDE_IP_WITH_HOST_NAME_USAGE_PARAM, new ConfigurationParam(OVERRIDE_IP_WITH_HOST_NAME_USAGE_PARAM, gdsInputHandler.getYesNoInput(), EMPTY_STR));

            sourceIPParamsMap.put(TASK_NAME_PARAM, new ConfigurationParam(TASK_NAME_PARAM, false, "IpResolvingStreamTask_sourceIp"));
            if (currentConfigurationState.getStreamingTopologyDefinitionState().isTargetIpResolvingRequired()) {
                sourceIPParamsMap.put(OUTPUT_TOPIC_PARAM, new ConfigurationParam(OUTPUT_TOPIC_PARAM, false, "fortscale-generic-data-access-source-ip-resolved"));
            }
            else {
                sourceIPParamsMap.put(OUTPUT_TOPIC_PARAM, new ConfigurationParam(OUTPUT_TOPIC_PARAM, false, "fortscale-generic-data-access-ip-resolved"));
            }

            sourceIPParamsMap.put(IP_FIELD_PARAM, new ConfigurationParam(IP_FIELD_PARAM, false, String.format("${impala.data.%s.table.field.source}", dataSourceName)));
            sourceIPParamsMap.put(HOST_PARAM, new ConfigurationParam(HOST_PARAM, false, String.format("${impala.data.%s.table.field.source_name}", dataSourceName)));

            sourceIPParamsMap.put(LAST_STATE_PARAM, new ConfigurationParam(LAST_STATE_PARAM, false, "IpResolvingStreamTask"));
        }

        if (currentConfigurationState.getStreamingTopologyDefinitionState().isTargetIpResolvingRequired()) {
            HashMap<String, ConfigurationParam> targetUserParamsMap = new HashMap<>();

            configurationsMap.put(GDS_CONFIG_ENTRY + TARGET_IP_CONFIG_ENTRY, targetUserParamsMap);

            targetUserParamsMap.put(TASK_NAME_PARAM, new ConfigurationParam(TASK_NAME_PARAM, false, "IpResolvingStreamTask_targetIp"));
            targetUserParamsMap.put(OUTPUT_TOPIC_PARAM, new ConfigurationParam(OUTPUT_TOPIC_PARAM, false, "fortscale-generic-data-access-ip-resolved"));
            targetUserParamsMap.put(IP_FIELD_PARAM, new ConfigurationParam(IP_FIELD_PARAM, false, String.format("${impala.data.%s.table.field.target}", dataSourceName)));
            targetUserParamsMap.put(HOST_PARAM, new ConfigurationParam(HOST_PARAM, false, String.format("${impala.data.%s.table.field.target_name}", dataSourceName)));

            targetUserParamsMap.put(LAST_STATE_PARAM, new ConfigurationParam(LAST_STATE_PARAM, false, "IpResolvingStreamTask"));
        }

        return configurationsMap;
    }
}
