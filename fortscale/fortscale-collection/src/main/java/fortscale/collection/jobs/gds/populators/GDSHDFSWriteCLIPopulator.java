package fortscale.collection.jobs.gds.populators;

import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.state.GDSConfigurationStateImpl;

import java.util.Map;

/**
 * @author gils
 * 03/01/2016
 */
public class GDSHDFSWriteCLIPopulator implements GDSConfigurationPopulator{
    @Override
    public Map<String, ConfigurationParam> populateConfigurationData(GDSConfigurationStateImpl currentConfigurationState) throws Exception {
        //HDFS - WRITE
        System.out.println(String.format("Going to configure the HDFS write task for the enrich for %s  ", dataSourceName));

        paramsMap.put("taskName", new ConfigurationParam("taskName", false, "enriched_HDFSWriterStreamTask"));
        paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-enriched-after-write"));
        paramsMap.put("fieldList", new ConfigurationParam("fieldList",false,String.format("${impala.enricheddata.%s.table.fields}",dataSourceName)));
        paramsMap.put("delimiter", new ConfigurationParam("delimiter",false,String.format("${impala.enricheddata.%s.table.delimiter}",dataSourceName)));
        paramsMap.put("tableName", new ConfigurationParam("tableName",false,String.format("${impala.enricheddata.%s.table.name}",dataSourceName)));
        paramsMap.put("hdfsPath", new ConfigurationParam("hdfsPath",false,String.format("${hdfs.user.enricheddata.%s.path}",dataSourceName)));
        paramsMap.put("fileName", new ConfigurationParam("fileName",false,String.format("${hdfs.enricheddata.%s.file.name}",dataSourceName)));
        paramsMap.put("partitionStrategy", new ConfigurationParam("partitionStrategy",false,String.format("${impala.enricheddata.%s.table.partition.type}",dataSourceName)));


        //todo -  add the anility to configure this param
        paramsMap.put("discriminatorsFields", new ConfigurationParam("discriminatorsFields",false,""));

//        hdfsTaskService.setConfigurationParams(paramsMap);
//        System.out.println("Finished to configure hdfs write streaming task. Do you want to apply changes now? (y/n)");
//
//        if (GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput())) {
//            if (hdfsTaskService.init()) {
//                hdfsTaskService.applyConfiguration();
//            }
//        }
//
//        System.out.println("Do you want to reset hdfs write streaming task changes? (y/n)");
//
//        if (GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput())) {
//            reset();
//        }
//        hdfsTaskService.done();

        paramsMap.put("lastState", new ConfigurationParam("lastState", false, "enriched_HDFSWriterStreamTask"));

        System.out.println(String.format("End configure the HDFS write task for %s", dataSourceName));

        return paramsMap;
    }
}
