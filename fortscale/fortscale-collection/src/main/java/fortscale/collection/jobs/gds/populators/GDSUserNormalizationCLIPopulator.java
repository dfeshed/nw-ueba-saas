package fortscale.collection.jobs.gds.populators;

import fortscale.collection.jobs.gds.GDSInputHandler;
import fortscale.collection.jobs.gds.GDSStandardInputHandler;
import fortscale.collection.jobs.gds.helper.GDSUserInputHelper;
import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.state.GDSConfigurationStateImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gils
 * 03/01/2016
 */
public class GDSUserNormalizationCLIPopulator implements GDSConfigurationPopulator{

    private GDSInputHandler gdsInputHandler = new GDSStandardInputHandler();

    @Override
    public Map<String, ConfigurationParam> populateConfigurationData(GDSConfigurationStateImpl currentConfigurationState) throws Exception {
        Map<String, ConfigurationParam> paramsMap = new HashMap<>();

        String dataSourceName = currentConfigurationState.getDataSourceName();

        // configure new Topic to the data source or use the GDS general topic
        System.out.println(String.format("Does %s use the general GDS streaming topology (y/n) ?", dataSourceName));

        paramsMap.put("topologyFlag",new ConfigurationParam("topologyFlag",true,""));
        paramsMap.put("lastState", new ConfigurationParam("lastState",false,"etl"));
        paramsMap.put("taskName",new ConfigurationParam("taskName",false,"UsernameNormalizationAndTaggingTask"));

        System.out.println(String.format("Does %s have target username to normalized (y/n) ?",dataSourceName));

        boolean targetNormalizationFlag = false;
        //in case there is a target user to be normalize also
        if(GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput())) {
            targetNormalizationFlag = true;
            paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-normalized-tagged-event_to_normalized_target_user"));
        }
        else if ((paramsMap.containsKey("sourceIpResolvingFlag") && paramsMap.get("sourceIpResolvingFlag").getParamFlag()) || (paramsMap.containsKey("targetIpResolvingFlag") && paramsMap.get("targetIpResolvingFlag").getParamFlag())) {
            paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-normalized-tagged-event_to_ip_resolving"));
        }
        //in case there is machine to normalized and tag
        else if ((paramsMap.containsKey("sourceMachineNormalizationFlag") && paramsMap.get("sourceMachineNormalizationFlag").getParamFlag()) || (paramsMap.containsKey("targetMachineNormalizationFlag") && paramsMap.get("targetMachineNormalizationFlag").getParamFlag())) {
            paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-normalized-tagged-even_to_computer_tagging"));
        }
        //in case there is ip to geo locate
        else if ((paramsMap.containsKey("sourceIpGeoLocationFlag") && paramsMap.get("sourceIpGeoLocationFlag").getParamFlag()) || (paramsMap.containsKey("targetIpGeoLocationFlag") && paramsMap.get("targetIpGeoLocationFlag").getParamFlag())) {
            paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-normalized-tagged-event_to_geo_location"));
        }
        else {
            paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-normalized-tagged-event"));
        }

        //User name field
        paramsMap.put("userNameField", new ConfigurationParam("userNameField",false,"username"));

        //Domain field  - for the enrich part
        System.out.println(String.format("Does %s have a field that contain the user domain  (y/n) ?",dataSourceName));

        if (GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput()))
        {
            //Domain field  - for the enrich part
            System.out.println(String.format("pleaase enter the field name that will contain the user Domain value :"));

            paramsMap.put("domainFieldName", new ConfigurationParam("domainFieldName", false, gdsInputHandler.getInput()));
        }
        else {
            paramsMap.put("domainFieldName", new ConfigurationParam("domainFieldName", false, "fake"));

            //In case of fake domain - enter the actual domain value the PS want
            paramsMap.put("domainValue", new ConfigurationParam("domainValue", false, ""));
        }

        //Normalized_username field
        paramsMap.put("normalizedUserNameField", new ConfigurationParam("normalizedUserNameField",false,"${impala.table.fields.normalized.username}"));

        //TODO - When we develope a new normalize service need to think what to do here cause now we have only ~2 kinds
        //Normalizing service
        System.out.println(String.format("Does the %s data source should contain users on the AD and you want to drop event of users that are not appeare there (i.e what we do for kerberos) (y/n):",dataSourceName));

        if (GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput())) {
            //Service  name
            paramsMap.put("normalizeSservieName", new ConfigurationParam("normalizeSservieName",false,"SecurityUsernameNormalizationService"));
            paramsMap.put("updateOnlyFlag", new ConfigurationParam("updateOnlyFlag",true,"true"));

        } else {
            paramsMap.put("normalizeSservieName", new ConfigurationParam("normalizeSservieName",false,"genericUsernameNormalizationService"));
            paramsMap.put("updateOnlyFlag", new ConfigurationParam("updateOnlyFlag",false,"false"));
        }



        if (targetNormalizationFlag) {
            if ((paramsMap.containsKey("sourceIpResolvingFlag") && paramsMap.get("sourceIpResolvingFlag").getParamFlag()) || (paramsMap.containsKey("targetIpResolvingFlag") && paramsMap.get("targetIpResolvingFlag").getParamFlag()))
                paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-normalized-tagged-event_to_ip_resolving"));
                //in case there is machine to normalized and tag
            else if ((paramsMap.containsKey("sourceMachineNormalizationFlag") && paramsMap.get("sourceMachineNormalizationFlag").getParamFlag()) || (paramsMap.containsKey("targetMachineNormalizationFlag") && paramsMap.get("targetMachineNormalizationFlag").getParamFlag()))
                paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-normalized-tagged-even_to_computer_tagging"));
                //in case there is ip to geo locate
            else if ((paramsMap.containsKey("sourceIpGeoLocationFlag") && paramsMap.get("sourceIpGeoLocationFlag").getParamFlag()) || (paramsMap.containsKey("targetIpGeoLocationFlag") && paramsMap.get("targetIpGeoLocationFlag").getParamFlag()))
                paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-normalized-tagged-event_to_geo_location"));
            else
                paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-normalized-tagged-event"));

            paramsMap.put("lastState", new ConfigurationParam("lastState", false, "UsernameNormalizationAndTaggingTask"));
            paramsMap.put("taskName", new ConfigurationParam("taskName", false, "UsernameNormalizationAndTaggingTask_target"));

            paramsMap.put("userNameField", new ConfigurationParam("userNameField", false, gdsInputHandler.getInput()));

            //Domain field  - for the enrich part
            System.out.println(String.format("Does %s have a field that contain the target user domain  (y/n) ?", dataSourceName));

            if (GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput())) {
                //Domain field  - for the enrich part
                System.out.println("Please enter the field name that will contain the target user Domain value :");

                paramsMap.put("domainFieldName", new ConfigurationParam("domainFieldName", false, gdsInputHandler.getInput()));

                paramsMap.put("domainValue", new ConfigurationParam("domainValue", false, ""));

            } else {
                paramsMap.put("domainFieldName", new ConfigurationParam("domainFieldName", false, "fake"));
                paramsMap.put("domainValue", new ConfigurationParam("domainValue", false, dataSourceName + "Connect"));


            }
            System.out.println("Please enter the field name of the field that will contain the second normalized user name :");

            paramsMap.put("normalizedUserNameField", new ConfigurationParam("normalizedUserNameField", false, gdsInputHandler.getInput()));

            System.out.println(String.format("End configure the Normalized Username and tagging task for %s", dataSourceName));
            paramsMap.put("lastState", new ConfigurationParam("lastState", false, "UsernameNormalizationAndTaggingTask"));

        }
    }
