package fortscale.collection.jobs.gds.input.populators.enrichment;

import fortscale.collection.jobs.gds.input.GDSCLIInputHandler;
import fortscale.collection.jobs.gds.input.GDSInputHandler;
import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.gds.state.GDSCompositeConfigurationState;

import java.util.HashMap;
import java.util.Map;

/**
 * Computer tagging command line populator
 *
 * @author gils
 * 03/01/2016
 */
public class GDSComputerTaggingCLIPopulator implements GDSConfigurationPopulator{

    private GDSInputHandler gdsInputHandler = new GDSCLIInputHandler();

    @Override
    public Map<String, ConfigurationParam> populateConfigurationData(GDSCompositeConfigurationState currentConfigurationState) throws Exception {

        Map<String, ConfigurationParam> paramsMap = new HashMap<>();

        String dataSourceName = currentConfigurationState.getDataSourceName();

        if (((paramsMap.containsKey("sourceMachineNormalizationFlag") && paramsMap.get("sourceMachineNormalizationFlag").getParamFlag()) || (paramsMap.containsKey("targetMachineNormalizationFlag") && paramsMap.get("targetMachineNormalizationFlag").getParamFlag())))
        {
            System.out.println(String.format("Going to configure the Computer tagging and normalization task for %s", dataSourceName));
            paramsMap.put("taskName", new ConfigurationParam("taskName", false, "ComputerTaggingClusteringTask"));

            if((paramsMap.containsKey("sourceIpGeoLocationFlag") && paramsMap.get("sourceIpGeoLocationFlag").getParamFlag()) || (paramsMap.containsKey("targetIpGeoLocationFlag") && paramsMap.get("targetIpGeoLocationFlag").getParamFlag()))
                paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-computer-tagged-clustered_to_geo_location"));
            else
                paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-computer-tagged-clustered"));

            // configure new configuration for the new dta source for source_ip
            System.out.println(String.format("Does %s source machine need to be added to the computer document in case he is missing (y/n) ?", dataSourceName));
            Boolean ensureComputerExist = gdsInputHandler.getInput().equals("y") || gdsInputHandler.getInput().equals("yes");
            paramsMap.put("createNewComputerFlag",new ConfigurationParam("createNewComputerFlag",ensureComputerExist,""));
            paramsMap.put("srcMachineClassifier", new ConfigurationParam("srcMachineClassifier",false, String.format("${impala.data.%s.table.field.src_class}",dataSourceName)));
            paramsMap.put("srcHost", new ConfigurationParam("srcHost", false,  String.format("${impala.data.%s.table.field.source_name}",dataSourceName)));
            paramsMap.put("srcClusteringField", new ConfigurationParam("srcClusteringField",false, String.format("${impala.data.%s.table.field.normalized_src_machine}",dataSourceName)));
            paramsMap.put("dstMachineClassifier", new ConfigurationParam("dstMachineClassifier",false, String.format("${impala.data.%s.table.field.dst_class}",dataSourceName)));
            paramsMap.put("dstClusteringField", new ConfigurationParam("dstClusteringField",false, String.format("${impala.data.%s.table.field.normalized_dst_machine}",dataSourceName)));
            paramsMap.put("dstHost", new ConfigurationParam("dstHost", false,  String.format("${impala.data.%s.table.field.target_name}",dataSourceName)));

            paramsMap.put("lastState", new ConfigurationParam("lastState", false, "ComputerTaggingClusteringTask"));
            System.out.println(String.format("End configure the Computer Tagging task for %s", dataSourceName));

        }

        return paramsMap;
    }
}
