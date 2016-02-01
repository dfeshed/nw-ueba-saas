package fortscale.collection.jobs.gds.input.populators.enrichment;

import fortscale.collection.jobs.gds.input.GDSCLIInputHandler;
import fortscale.collection.jobs.gds.input.GDSInputHandler;
import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.gds.state.GDSCompositeConfigurationState;

import java.util.HashMap;
import java.util.Map;

/**
 * User normalization command line populator
 *
 * @author gils
 * 03/01/2016
 */
public class GDSUserNormalizationCLIPopulator implements GDSConfigurationPopulator {

    private static final String EMPTY_STR = "";
    private static final String TOPOLOGY_FLAG_PARAM = "topologyFlag";
    private static final String LAST_STATE_PARAM = "lastState";
    private static final String TASK_NAME_PARAM = "taskName";
    private static final String OUTPUT_TOPIC_PARAM = "outputTopic";
    private static final String NORMALIZATION_BASED_FIELD_PARAM = "normalizationBasedField";
    private static final String DOMAIN_FIELD_NAME_PARAM = "domainFieldName";
    private static final String DOMAIN_VALUE_PARAM = "domainValue";
    private static final String NORMALIZE_SERVICE_NAME_PARAM = "normalizeServiceName";
    private static final String UPDATE_ONLY_PARAM = "updateOnlyFlag";
    private static final String NORMALIZED_USER_NAME_FIELD_PARAM = "normalizedUserNameField";

    private static final String GDS_CONFIG_ENTRY = "gds.config.entry.";
    private static final String SOURCE_USERNAME_CONFIG_ENTRY = "source.";
    private static final String TARGET_USERNAME_CONFIG_ENTRY = "target.";

    private GDSInputHandler gdsInputHandler = new GDSCLIInputHandler();

    @Override
    public Map<String, Map<String, ConfigurationParam>> populateConfigurationData(GDSCompositeConfigurationState currentConfigurationState) throws Exception {
        Map<String, Map<String, ConfigurationParam>> configurationsMap = new HashMap<>();
        HashMap<String, ConfigurationParam> sourceUserParamsMap = new HashMap<>();

        configurationsMap.put(GDS_CONFIG_ENTRY + SOURCE_USERNAME_CONFIG_ENTRY, sourceUserParamsMap);

        String dataSourceName = currentConfigurationState.getDataSourceName();

        sourceUserParamsMap.put(TOPOLOGY_FLAG_PARAM, new ConfigurationParam(TOPOLOGY_FLAG_PARAM, true, EMPTY_STR));
        sourceUserParamsMap.put(LAST_STATE_PARAM, new ConfigurationParam(LAST_STATE_PARAM, false, "etl"));
        sourceUserParamsMap.put(TASK_NAME_PARAM, new ConfigurationParam(TASK_NAME_PARAM, false, "UsernameNormalizationAndTaggingTask"));

        System.out.println(String.format("Does %s have target username to normalized (y/n) ?", dataSourceName));
        boolean targetUserNormalizationRequired = gdsInputHandler.getYesNoInput();

        boolean isSourceIpResolvingRequired = currentConfigurationState.getStreamingTopologyDefinitionState().isSourceIpResolvingRequired();
        boolean isTargetIpResolvingRequired = currentConfigurationState.getStreamingTopologyDefinitionState().isTargetIpResolvingRequired();
        boolean isSourceMachineNormalizationRequired = currentConfigurationState.getStreamingTopologyDefinitionState().isSourceMachineNormalizationRequired();
        boolean isTargetMachineNormalizationRequired = currentConfigurationState.getStreamingTopologyDefinitionState().isTargetMachineNormalizationRequired();
        boolean isSourceIpGeoLocationRequired = currentConfigurationState.getStreamingTopologyDefinitionState().isSourceIpGeoLocationRequired();
        boolean isTargetIpGeoLocationRequired = currentConfigurationState.getStreamingTopologyDefinitionState().isTargetIpGeoLocationRequired();

        //in case there is a target user to be normalize also
        if (targetUserNormalizationRequired) {
            sourceUserParamsMap.put(OUTPUT_TOPIC_PARAM, new ConfigurationParam(OUTPUT_TOPIC_PARAM, false, "fortscale-generic-data-access-normalized-tagged-event_to_normalized_target_user"));
        } else if (isSourceIpResolvingRequired || isTargetIpResolvingRequired) {
            sourceUserParamsMap.put(OUTPUT_TOPIC_PARAM, new ConfigurationParam(OUTPUT_TOPIC_PARAM, false, "fortscale-generic-data-access-normalized-tagged-event_to_ip_resolving"));
        }
        //in case there is machine to normalized and tag
        else if (isSourceMachineNormalizationRequired || isTargetMachineNormalizationRequired) {
            sourceUserParamsMap.put(OUTPUT_TOPIC_PARAM, new ConfigurationParam(OUTPUT_TOPIC_PARAM, false, "fortscale-generic-data-access-normalized-tagged-event_to_computer_tagging"));
        }
        //in case there is ip to geo locate
        else if (isSourceIpGeoLocationRequired || isTargetIpGeoLocationRequired) {
            sourceUserParamsMap.put(OUTPUT_TOPIC_PARAM, new ConfigurationParam(OUTPUT_TOPIC_PARAM, false, "fortscale-generic-data-access-normalized-tagged-event_to_geo_location"));
        } else {
            sourceUserParamsMap.put(OUTPUT_TOPIC_PARAM, new ConfigurationParam(OUTPUT_TOPIC_PARAM, false, "fortscale-generic-data-access-normalized-tagged-event"));
        }

        //User name field
        sourceUserParamsMap.put(NORMALIZATION_BASED_FIELD_PARAM, new ConfigurationParam(NORMALIZATION_BASED_FIELD_PARAM, false, "username"));

        //Domain field  - for the enrich part
        System.out.println(String.format("Does %s have a field that contain the user domain  (y/n)?", dataSourceName));

        if (gdsInputHandler.getYesNoInput()) {
            //Domain field  - for the enrich part
            System.out.println("please enter the field name that will contain the user Domain value:");
            sourceUserParamsMap.put(DOMAIN_FIELD_NAME_PARAM, new ConfigurationParam(DOMAIN_FIELD_NAME_PARAM, false, gdsInputHandler.getInput()));
			sourceUserParamsMap.put(DOMAIN_VALUE_PARAM, new ConfigurationParam("domainValue", false, EMPTY_STR));
        } else {
            sourceUserParamsMap.put(DOMAIN_FIELD_NAME_PARAM, new ConfigurationParam(DOMAIN_FIELD_NAME_PARAM, false, "fake"));

            //In case of fake domain - enter the actual domain value the PS want
            sourceUserParamsMap.put(DOMAIN_VALUE_PARAM, new ConfigurationParam("domainValue", false, dataSourceName + "Connect"));
        }

        //TODO - When we develope a new normalize service need to think what to do here cause now we have only ~2 kinds
        //Normalizing service
        System.out.println(String.format("Does the %s data source should contain users on the AD and you want to drop event of users that are not appear there (i.e what we do for kerberos) (y/n):", dataSourceName));

        ConfigurationParam serviceNameParam;
        ConfigurationParam updateOnlyParam;
        if (gdsInputHandler.getYesNoInput()) {
            //Service  name
            serviceNameParam = new ConfigurationParam(NORMALIZE_SERVICE_NAME_PARAM, false, "SecurityUsernameNormalizationService");
            sourceUserParamsMap.put(NORMALIZE_SERVICE_NAME_PARAM, serviceNameParam);

            updateOnlyParam = new ConfigurationParam(UPDATE_ONLY_PARAM, true, "true");
            sourceUserParamsMap.put(UPDATE_ONLY_PARAM, updateOnlyParam);

        } else {
            serviceNameParam = new ConfigurationParam(NORMALIZE_SERVICE_NAME_PARAM, false, "genericUsernameNormalizationService");
            sourceUserParamsMap.put(NORMALIZE_SERVICE_NAME_PARAM, serviceNameParam);

            updateOnlyParam = new ConfigurationParam(UPDATE_ONLY_PARAM, false, "false");
            sourceUserParamsMap.put(UPDATE_ONLY_PARAM, updateOnlyParam);
        }

        sourceUserParamsMap.put(NORMALIZED_USER_NAME_FIELD_PARAM, new ConfigurationParam(NORMALIZED_USER_NAME_FIELD_PARAM, false, "${impala.table.fields.normalized.username}"));

        if (targetUserNormalizationRequired) {
            HashMap<String, ConfigurationParam> targetUserParamsMap = new HashMap<>();

            configurationsMap.put(GDS_CONFIG_ENTRY + TARGET_USERNAME_CONFIG_ENTRY, targetUserParamsMap);

            if (isSourceIpResolvingRequired || isTargetIpResolvingRequired) {
                targetUserParamsMap.put(OUTPUT_TOPIC_PARAM, new ConfigurationParam(OUTPUT_TOPIC_PARAM, false, "fortscale-generic-data-access-normalized-tagged-event_to_ip_resolving"));
            }
            else if (isSourceMachineNormalizationRequired || isTargetMachineNormalizationRequired) {
                targetUserParamsMap.put(OUTPUT_TOPIC_PARAM, new ConfigurationParam(OUTPUT_TOPIC_PARAM, false, "fortscale-generic-data-access-normalized-tagged-event_to_computer_tagging"));
            }
            else if (isSourceIpGeoLocationRequired || isTargetIpGeoLocationRequired) {
                targetUserParamsMap.put(OUTPUT_TOPIC_PARAM, new ConfigurationParam(OUTPUT_TOPIC_PARAM, false, "fortscale-generic-data-access-normalized-tagged-event_to_geo_location"));
            }
            else {
                targetUserParamsMap.put(OUTPUT_TOPIC_PARAM, new ConfigurationParam(OUTPUT_TOPIC_PARAM, false, "fortscale-generic-data-access-normalized-tagged-event"));
            }

            targetUserParamsMap.put(LAST_STATE_PARAM, new ConfigurationParam(LAST_STATE_PARAM, false, "UsernameNormalizationAndTaggingTask"));
            targetUserParamsMap.put(TASK_NAME_PARAM, new ConfigurationParam(TASK_NAME_PARAM, false, "UsernameNormalizationAndTaggingTask_target"));

            System.out.println("Please enter the target username field to normalize:");
            targetUserParamsMap.put(NORMALIZATION_BASED_FIELD_PARAM, new ConfigurationParam(NORMALIZATION_BASED_FIELD_PARAM, false, gdsInputHandler.getInput()));

            //Domain field  - for the enrich part
            System.out.println(String.format("Does %s have a field that contain the target user domain  (y/n)?", dataSourceName));

            if (gdsInputHandler.getYesNoInput()) {
                //Domain field  - for the enrich part
                System.out.println("Please enter the field name that will contain the target user Domain value:");
                targetUserParamsMap.put("domainFieldName", new ConfigurationParam("domainFieldName", false, gdsInputHandler.getInput()));

                targetUserParamsMap.put("domainValue", new ConfigurationParam("domainValue", false, EMPTY_STR));

            } else {
                targetUserParamsMap.put("domainFieldName", new ConfigurationParam("domainFieldName", false, "fake"));
                targetUserParamsMap.put("domainValue", new ConfigurationParam("domainValue", false, dataSourceName + "Connect"));
            }

            System.out.println("Please enter the field name of the field that will contain the target normalized user name:");
            targetUserParamsMap.put(NORMALIZED_USER_NAME_FIELD_PARAM, new ConfigurationParam(NORMALIZED_USER_NAME_FIELD_PARAM, false, gdsInputHandler.getInput()));

            targetUserParamsMap.put(LAST_STATE_PARAM, new ConfigurationParam(LAST_STATE_PARAM, false, "UsernameNormalizationAndTaggingTask"));

            targetUserParamsMap.put(NORMALIZE_SERVICE_NAME_PARAM, serviceNameParam);
            targetUserParamsMap.put(UPDATE_ONLY_PARAM, updateOnlyParam);
        }

        return configurationsMap;
    }
}
