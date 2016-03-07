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

    private static final String TASK_NAME_PARAM = "taskName";
    private static final String OUTPUT_TOPIC_PARAM = "outputTopic";
    private static final String LAST_STATE_PARAM = "lastState";
    private static final String CREATE_NEW_COMPUTER_FLAG_PARAM = "createNewComputerFlag";
    private static final String SRC_MACHINE_CLASSIFIER_PARAM = "srcMachineClassifier";
    private static final String SRC_HOST_PARAM = "srcHost";
    private static final String SRC_CLUSTERING_FIELD_PARAM = "srcClusteringField";
    private static final String DST_MACHINE_CLASSIFIER_PARAM = "dstMachineClassifier";
    private static final String DST_CLUSTERING_FIELD = "dstClusteringField";
    private static final String DST_HOST = "dstHost";

    private static final String GDS_CONFIG_ENTRY = "gds.config.entry.";

    private GDSInputHandler gdsInputHandler = new GDSCLIInputHandler();

    @Override
    public Map<String, Map<String, ConfigurationParam>> populateConfigurationData(GDSCompositeConfigurationState currentConfigurationState) throws Exception {
        Map<String, Map<String, ConfigurationParam>> configurationsMap = new HashMap<>();

        String dataSourceName = currentConfigurationState.getDataSourceName();

        if (currentConfigurationState.getStreamingTopologyDefinitionState().isSourceMachineNormalizationRequired() || currentConfigurationState.getStreamingTopologyDefinitionState().isTargetMachineNormalizationRequired()) {
            HashMap<String, ConfigurationParam> paramsMap = new HashMap<>();

            configurationsMap.put(GDS_CONFIG_ENTRY, paramsMap);

            System.out.println(String.format("Going to configure the Computer tagging and normalization task for %s", dataSourceName));
            paramsMap.put(TASK_NAME_PARAM, new ConfigurationParam(TASK_NAME_PARAM, false, "ComputerTaggingNormalizationTask"));

            if(currentConfigurationState.getStreamingTopologyDefinitionState().isSourceIpGeoLocationRequired() || currentConfigurationState.getStreamingTopologyDefinitionState().isTargetIpGeoLocationRequired())
            {
                paramsMap.put(OUTPUT_TOPIC_PARAM, new ConfigurationParam(OUTPUT_TOPIC_PARAM, false, "fortscale-generic-data-access-computer-tagged-clustered_to_geo_location"));
            }
            else
                paramsMap.put(OUTPUT_TOPIC_PARAM, new ConfigurationParam(OUTPUT_TOPIC_PARAM, false, "fortscale-generic-data-access-computer-tagged-clustered"));

            // configure new configuration for the new dta source for source_ip
            System.out.println(String.format("Does %s source machine need to be added to the computer document in case he is missing (y/n) ?", dataSourceName));
            paramsMap.put(CREATE_NEW_COMPUTER_FLAG_PARAM,new ConfigurationParam(CREATE_NEW_COMPUTER_FLAG_PARAM, gdsInputHandler.getYesNoInput(),""));
            paramsMap.put(SRC_MACHINE_CLASSIFIER_PARAM, new ConfigurationParam(SRC_MACHINE_CLASSIFIER_PARAM,false, String.format("${impala.data.%s.table.field.src_class}",dataSourceName)));
            paramsMap.put(SRC_HOST_PARAM, new ConfigurationParam(SRC_HOST_PARAM, false,  String.format("${impala.data.%s.table.field.source_name}",dataSourceName)));
            paramsMap.put(SRC_CLUSTERING_FIELD_PARAM, new ConfigurationParam(SRC_CLUSTERING_FIELD_PARAM,false, String.format("${impala.data.%s.table.field.normalized_src_machine}",dataSourceName)));
            paramsMap.put(DST_MACHINE_CLASSIFIER_PARAM, new ConfigurationParam(DST_MACHINE_CLASSIFIER_PARAM,false, String.format("${impala.data.%s.table.field.dst_class}",dataSourceName)));
            paramsMap.put(DST_CLUSTERING_FIELD, new ConfigurationParam(DST_CLUSTERING_FIELD,false, String.format("${impala.data.%s.table.field.normalized_dst_machine}",dataSourceName)));
            paramsMap.put(DST_HOST, new ConfigurationParam(DST_HOST, false,  String.format("${impala.data.%s.table.field.target_name}",dataSourceName)));

            paramsMap.put(LAST_STATE_PARAM, new ConfigurationParam(LAST_STATE_PARAM, false, "ComputerTaggingNormalizationTask"));

            System.out.println(String.format("End configure the Computer Tagging task for %s", dataSourceName));
        }

        return configurationsMap;
    }
}
