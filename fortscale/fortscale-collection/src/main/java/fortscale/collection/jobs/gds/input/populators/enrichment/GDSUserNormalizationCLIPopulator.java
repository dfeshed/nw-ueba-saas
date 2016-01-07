package fortscale.collection.jobs.gds.input.populators.enrichment;

import fortscale.collection.jobs.gds.helper.GDSUserInputHelper;
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
    private static final String USER_NAME_FIELD_PARAM = "userNameField";
    private static final String DOMAIN_FIELD_NAME_PARAM = "domainFieldName";
    private static final String DOMAIN_VALUE_PARAM = "domainValue";
    private static final String NORMALIZE_SERVICE_NAME_PARAM = "normalizeServiceName";
    private static final String UPDATE_ONLY_PARAM = "updateOnlyFlag";
    private static final String NORMALIZED_USER_NAME_FIELD_PARAM = "normalizedUserNameField";
    private GDSInputHandler gdsInputHandler = new GDSCLIInputHandler();

    @Override
    public Map<String, ConfigurationParam> populateConfigurationData(GDSCompositeConfigurationState currentConfigurationState) throws Exception {
        Map<String, ConfigurationParam> paramsMap = new HashMap<>();

        String dataSourceName = currentConfigurationState.getDataSourceName();

        paramsMap.put(TOPOLOGY_FLAG_PARAM, new ConfigurationParam(TOPOLOGY_FLAG_PARAM, true, EMPTY_STR));
        paramsMap.put(LAST_STATE_PARAM, new ConfigurationParam(LAST_STATE_PARAM, false, "etl"));
        paramsMap.put(TASK_NAME_PARAM, new ConfigurationParam(TASK_NAME_PARAM, false, "UsernameNormalizationAndTaggingTask"));

        System.out.println(String.format("Does %s have target username to normalized (y/n) ?", dataSourceName));
        boolean targetUserNormalizationRequired = GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput());

        boolean isSourceIpResolvingRequired = currentConfigurationState.isSourceIpResolvingRequired();
        boolean isTargetIpResolvingRequired = currentConfigurationState.isTargetIpResolvingRequired();
        boolean isSourceMachineNormalizationRequired = currentConfigurationState.isSourceMachineNormalizationRequired();
        boolean isTargetMachineNormalizationRequired = currentConfigurationState.isTargetMachineNormalizationRequired();
        boolean isSourceIpGeoLocationRequired = currentConfigurationState.isSourceIpGeoLocationRequired();
        boolean isTargetIpGeoLocationRequired = currentConfigurationState.isTargetIpGeoLocationRequired();

        //in case there is a target user to be normalize also
        if (targetUserNormalizationRequired) {
            paramsMap.put(OUTPUT_TOPIC_PARAM, new ConfigurationParam(OUTPUT_TOPIC_PARAM, false, "fortscale-generic-data-access-normalized-tagged-event_to_normalized_target_user"));
        } else if (isSourceIpResolvingRequired || isTargetIpResolvingRequired) {
            paramsMap.put(OUTPUT_TOPIC_PARAM, new ConfigurationParam(OUTPUT_TOPIC_PARAM, false, "fortscale-generic-data-access-normalized-tagged-event_to_ip_resolving"));
        }
        //in case there is machine to normalized and tag
        else if (isSourceMachineNormalizationRequired || isTargetMachineNormalizationRequired) {
            paramsMap.put(OUTPUT_TOPIC_PARAM, new ConfigurationParam(OUTPUT_TOPIC_PARAM, false, "fortscale-generic-data-access-normalized-tagged-event_to_computer_tagging"));
        }
        //in case there is ip to geo locate
        else if (isSourceIpGeoLocationRequired || isTargetIpGeoLocationRequired) {
            paramsMap.put(OUTPUT_TOPIC_PARAM, new ConfigurationParam(OUTPUT_TOPIC_PARAM, false, "fortscale-generic-data-access-normalized-tagged-event_to_geo_location"));
        } else {
            paramsMap.put(OUTPUT_TOPIC_PARAM, new ConfigurationParam(OUTPUT_TOPIC_PARAM, false, "fortscale-generic-data-access-normalized-tagged-event"));
        }

        //User name field
        paramsMap.put(USER_NAME_FIELD_PARAM, new ConfigurationParam(USER_NAME_FIELD_PARAM, false, "username"));

        //Domain field  - for the enrich part
        System.out.println(String.format("Does %s have a field that contain the user domain  (y/n)?", dataSourceName));

        if (GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput())) {
            //Domain field  - for the enrich part
            System.out.println("please enter the field name that will contain the user Domain value:");
            paramsMap.put(DOMAIN_FIELD_NAME_PARAM, new ConfigurationParam(DOMAIN_FIELD_NAME_PARAM, false, gdsInputHandler.getInput()));
        } else {
            paramsMap.put(DOMAIN_FIELD_NAME_PARAM, new ConfigurationParam(DOMAIN_FIELD_NAME_PARAM, false, "fake"));

            //In case of fake domain - enter the actual domain value the PS want
            paramsMap.put(DOMAIN_VALUE_PARAM, new ConfigurationParam("domainValue", false, EMPTY_STR));
        }

        //TODO - When we develope a new normalize service need to think what to do here cause now we have only ~2 kinds
        //Normalizing service
        System.out.println(String.format("Does the %s data source should contain users on the AD and you want to drop event of users that are not appeare there (i.e what we do for kerberos) (y/n):", dataSourceName));

        if (GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput())) {
            //Service  name
            paramsMap.put(NORMALIZE_SERVICE_NAME_PARAM, new ConfigurationParam(NORMALIZE_SERVICE_NAME_PARAM, false, "SecurityUsernameNormalizationService"));
            paramsMap.put(UPDATE_ONLY_PARAM, new ConfigurationParam(UPDATE_ONLY_PARAM, true, "true"));

        } else {
            paramsMap.put(NORMALIZE_SERVICE_NAME_PARAM, new ConfigurationParam(NORMALIZE_SERVICE_NAME_PARAM, false, "genericUsernameNormalizationService"));
            paramsMap.put(UPDATE_ONLY_PARAM, new ConfigurationParam(UPDATE_ONLY_PARAM, false, "false"));
        }

        // TODO do we want to allow the user to flush/apply changes now?

        if (targetUserNormalizationRequired) {
            if (isSourceIpResolvingRequired || isTargetIpResolvingRequired) {
                paramsMap.put(OUTPUT_TOPIC_PARAM, new ConfigurationParam(OUTPUT_TOPIC_PARAM, false, "fortscale-generic-data-access-normalized-tagged-event_to_ip_resolving"));
            }
            else if (isSourceMachineNormalizationRequired || isTargetMachineNormalizationRequired) {
                paramsMap.put(OUTPUT_TOPIC_PARAM, new ConfigurationParam(OUTPUT_TOPIC_PARAM, false, "fortscale-generic-data-access-normalized-tagged-even_to_computer_tagging"));
            }
            else if (isSourceIpGeoLocationRequired || isTargetIpGeoLocationRequired) {
                paramsMap.put(OUTPUT_TOPIC_PARAM, new ConfigurationParam(OUTPUT_TOPIC_PARAM, false, "fortscale-generic-data-access-normalized-tagged-event_to_geo_location"));
            }
            else {
                paramsMap.put(OUTPUT_TOPIC_PARAM, new ConfigurationParam(OUTPUT_TOPIC_PARAM, false, "fortscale-generic-data-access-normalized-tagged-event"));
            }

            paramsMap.put(LAST_STATE_PARAM, new ConfigurationParam(LAST_STATE_PARAM, false, "UsernameNormalizationAndTaggingTask"));
            paramsMap.put(TASK_NAME_PARAM, new ConfigurationParam(TASK_NAME_PARAM, false, "UsernameNormalizationAndTaggingTask_target"));

            System.out.println("Please enter the second username field to normalize:");
            paramsMap.put(USER_NAME_FIELD_PARAM, new ConfigurationParam(USER_NAME_FIELD_PARAM, false, gdsInputHandler.getInput()));

            //Domain field  - for the enrich part
            System.out.println(String.format("Does %s have a field that contain the target user domain  (y/n)?", dataSourceName));

            if (GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput())) {
                //Domain field  - for the enrich part
                System.out.println("Please enter the field name that will contain the target user Domain value:");
                paramsMap.put("domainFieldName", new ConfigurationParam("domainFieldName", false, gdsInputHandler.getInput()));

                paramsMap.put("domainValue", new ConfigurationParam("domainValue", false, EMPTY_STR));

            } else {
                paramsMap.put("domainFieldName", new ConfigurationParam("domainFieldName", false, "fake"));
                paramsMap.put("domainValue", new ConfigurationParam("domainValue", false, dataSourceName + "Connect"));
            }

            System.out.println("Please enter the field name of the field that will contain the second normalized user name :");
            paramsMap.put(NORMALIZED_USER_NAME_FIELD_PARAM, new ConfigurationParam(NORMALIZED_USER_NAME_FIELD_PARAM, false, gdsInputHandler.getInput()));

            paramsMap.put(LAST_STATE_PARAM, new ConfigurationParam(LAST_STATE_PARAM, false, "UsernameNormalizationAndTaggingTask"));
        }

        paramsMap.put(LAST_STATE_PARAM, new ConfigurationParam(LAST_STATE_PARAM, false, "UsernameNormalizationAndTaggingTask"));

        return paramsMap;
    }
}