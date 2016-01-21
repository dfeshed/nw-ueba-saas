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
    public Map<String, Map<String, ConfigurationParam>> populateConfigurationData(GDSCompositeConfigurationState currentConfigurationState) throws Exception {
        Map<String, Map<String, ConfigurationParam>> configurationsMap = new HashMap<>();

        String dataSourceName = currentConfigurationState.getDataSourceName();

        for(GDSHDFSWriterTableNamesEnum tableName: GDSHDFSWriterTableNamesEnum.values()){
            HashMap<String, ConfigurationParam> paramsMap = new HashMap<>();
            configurationsMap.put(tableName.name(), paramsMap);

            System.out.println(String.format("Going to configure the HDFS write task for the %s for %s  ", tableName, dataSourceName));

            paramsMap.put(TASK_NAME_PARAM, new ConfigurationParam(TASK_NAME_PARAM, false, String.format("%s_HDFSWriterStreamTask", tableName.getTaskName())));
            paramsMap.put(DELIMITER_PARAM, new ConfigurationParam(DELIMITER_PARAM,false,String.format("${impala.%s.%s.%s.table.delimiter}",tableName.getHdfsTableName(),dataSourceName,tableName.getSuffixHdfsTableName())));
            paramsMap.put(TABLE_NAME_PARAM, new ConfigurationParam(TABLE_NAME_PARAM,false,String.format("${impala.%s.%s.%s.table.name}",tableName.getHdfsTableName(),dataSourceName)));
            paramsMap.put(FILE_NAME_PARAM, new ConfigurationParam(FILE_NAME_PARAM,false,String.format("${hdfs.%s.%s.%s.table.name}.csv",tableName.getHdfsTableName(),dataSourceName,tableName.getSuffixHdfsTableName())));
            paramsMap.put(PARTITION_STRATEGY_PARAM, new ConfigurationParam(PARTITION_STRATEGY_PARAM, false, String.format("${impala.%s.%s.%s.table.partition.type}",tableName.getHdfsTableName(), dataSourceName,tableName.getSuffixHdfsTableName())));
            paramsMap.put(FIELD_LIST_PARAM, new ConfigurationParam(FIELD_LIST_PARAM,false,String.format("${impala.%s.%s.table.fields}",tableName.getHdfsTableName(),dataSourceName)));
            //TODO add the ability to configure this param
            paramsMap.put(DISCRIMINATORS_FIELDS_PARAM, new ConfigurationParam(DISCRIMINATORS_FIELDS_PARAM,false,""));

            switch (tableName){
            case ENRICH:
                paramsMap.put(OUTPUT_TOPIC_PARAM, new ConfigurationParam(OUTPUT_TOPIC_PARAM, false, "fortscale-generic-data-access-enriched-after-write"));
                paramsMap.put(LAST_STATE_PARAM, new ConfigurationParam(LAST_STATE_PARAM, false, "enriched_HDFSWriterStreamTask"));
                paramsMap.put(HDFS_PATH_PARAM, new ConfigurationParam(HDFS_PATH_PARAM,false,String.format("${hdfs.user.%s%.path}",tableName.getHdfsTableName(),dataSourceName)));
                break;
            case SCORE:
                paramsMap.put(OUTPUT_TOPIC_PARAM, new ConfigurationParam(OUTPUT_TOPIC_PARAM, false, "fortscale-generic-data-access-score-after-write,fortscale-generic-data-access-event-score-from-hdfs"));
                paramsMap.put(LAST_STATE_PARAM, new ConfigurationParam(LAST_STATE_PARAM, false, "MultipleEventsPrevalenceModelStreamTask"));
                paramsMap.put(HDFS_PATH_PARAM, new ConfigurationParam(HDFS_PATH_PARAM,false,String.format("${hdfs.user.processeddata.%s.path}",dataSourceName)));
                break;
            case TOP_SCORE:
                paramsMap.put(LAST_STATE_PARAM, new ConfigurationParam(LAST_STATE_PARAM, false, "MultipleEventsPrevalenceModelStreamTask"));
                paramsMap.put(HDFS_PATH_PARAM, new ConfigurationParam(HDFS_PATH_PARAM,false,String.format("${hdfs.user.processeddata.%s.%s.path}", dataSourceName, tableName.getSuffixHdfsTableName())));
                break;
            default:
                break;
            }
        }

        System.out.println(String.format("End configure the HDFS write task for %s", dataSourceName));

        return configurationsMap;
    }
}
