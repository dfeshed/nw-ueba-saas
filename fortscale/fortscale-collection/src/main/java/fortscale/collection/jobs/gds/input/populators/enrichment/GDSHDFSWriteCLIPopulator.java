package fortscale.collection.jobs.gds.input.populators.enrichment;

import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.gds.state.GDSCompositeConfigurationState;

import java.util.HashMap;
import java.util.Map;

/**
 * HDFS writer command line populator
 *
 * @author gils
 * 03/01/2016
 */
public class GDSHDFSWriteCLIPopulator implements GDSConfigurationPopulator{

    private static final String TASK_NAME_PARAM = "taskName";
    private static final String OUTPUT_TOPIC_PARAM = "outputTopic";
    private static final String FIELD_LIST_PARAM = "fieldList";
    private static final String DELIMITER_PARAM = "delimiter";
    private static final String TABLE_NAME_PARAM = "tableName";
    private static final String HDFS_PATH_PARAM = "hdfsPath";
    private static final String FILE_NAME_PARAM = "fileName";
    private static final String PARTITION_STRATEGY_PARAM = "partitionStrategy";
    private static final String DISCRIMINATORS_FIELDS_PARAM = "discriminatorsFields";
    private static final String LAST_STATE_PARAM = "lastState";

    @Override
    public Map<String, ConfigurationParam> populateConfigurationData(GDSCompositeConfigurationState currentConfigurationState) throws Exception {

        Map<String, ConfigurationParam> paramsMap = new HashMap<>();

        String dataSourceName = currentConfigurationState.getDataSourceName();

        System.out.println(String.format("Going to configure the HDFS write task for the enrich for %s  ", dataSourceName));

        paramsMap.put(TASK_NAME_PARAM, new ConfigurationParam(TASK_NAME_PARAM, false, "enriched_HDFSWriterStreamTask"));
        paramsMap.put(OUTPUT_TOPIC_PARAM, new ConfigurationParam(OUTPUT_TOPIC_PARAM, false, "fortscale-generic-data-access-enriched-after-write"));
        paramsMap.put(FIELD_LIST_PARAM, new ConfigurationParam(FIELD_LIST_PARAM,false,String.format("${impala.enricheddata.%s.table.fields}",dataSourceName)));
        paramsMap.put(DELIMITER_PARAM, new ConfigurationParam(DELIMITER_PARAM,false,String.format("${impala.enricheddata.%s.table.delimiter}",dataSourceName)));
        paramsMap.put(TABLE_NAME_PARAM, new ConfigurationParam(TABLE_NAME_PARAM,false,String.format("${impala.enricheddata.%s.table.name}",dataSourceName)));
        paramsMap.put(HDFS_PATH_PARAM, new ConfigurationParam(HDFS_PATH_PARAM,false,String.format("${hdfs.user.enricheddata.%s.path}",dataSourceName)));
        paramsMap.put(FILE_NAME_PARAM, new ConfigurationParam(FILE_NAME_PARAM,false,String.format("${hdfs.enricheddata.%s.file.name}",dataSourceName)));
        paramsMap.put(PARTITION_STRATEGY_PARAM, new ConfigurationParam(PARTITION_STRATEGY_PARAM,false,String.format("${impala.enricheddata.%s.table.partition.type}",dataSourceName)));

        //TODO add the ability to configure this param
        paramsMap.put(DISCRIMINATORS_FIELDS_PARAM, new ConfigurationParam(DISCRIMINATORS_FIELDS_PARAM,false,""));

        paramsMap.put(LAST_STATE_PARAM, new ConfigurationParam(LAST_STATE_PARAM, false, "enriched_HDFSWriterStreamTask"));

        System.out.println(String.format("End configure the HDFS write task for %s", dataSourceName));

        return paramsMap;
    }
}
