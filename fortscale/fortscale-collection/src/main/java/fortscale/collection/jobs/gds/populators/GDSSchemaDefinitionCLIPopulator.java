package fortscale.collection.jobs.gds.populators;

import fortscale.collection.jobs.gds.GDSInputHandler;
import fortscale.collection.jobs.gds.GDSMenuPrinterHelper;
import fortscale.collection.jobs.gds.GDSStandardInputHandler;
import fortscale.collection.jobs.gds.helper.GDSUserInputHelper;
import fortscale.collection.jobs.gds.populators.enrichment.GDSConfigurationPopulator;
import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.state.GDSCompositeConfigurationState;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gils
 * 03/01/2016
 */
public class GDSSchemaDefinitionCLIPopulator implements GDSConfigurationPopulator {

    private GDSInputHandler gdsInputHandler = new GDSStandardInputHandler();

    @Value("${fortscale.data.source}")
    private String currentDataSources = "ssh,vpn"; // TODO fix

    //TODO - Generate this auto from the entities  properties
    private static final String BASE_SCHEMA_FIELDS_AS_CSV = "date_time TIMESTAMP,date_time_unix BIGINT,username STRING,normalized_username STRING,status STRING,isUserAdministrator BOOLEAN, isUserExecutive BOOLEAN,isUserServiceAccount BOOLEAN";
    private static final String DATA_ACCESS_SCHEMA_FIELDS_AS_CSV = "date_time TIMESTAMP,date_time_unix BIGINT,username STRING,normalized_username STRING,source_ip STRING,hostname STRING,normalized_src_machine STRING,src_class STRING,country STRING,longtitude STRING,latitude STRING,countryIsoCode STRING,region STRING,city STRING,isp STRING,usageType STRING,status STRING,isUserAdministrator BOOLEAN, isUserExecutive BOOLEAN,isUserServiceAccount BOOLEAN";
    private static final String SCORE_DATA_ACCESS_SCHEMA_FIELDS_AS_CSV = "date_time_score DOUBLE,eventscore DOUBLE,source_machine_score DOUBLE,country_score DOUBLE";
    private static final String AUTH_SCHEMA_FIELDS_AS_CSV = "date_time TIMESTAMP,date_time_unix BIGINT,username STRING,normalized_username STRING,source_ip STRING,hostname STRING,normalized_src_machine STRING,src_class STRING,country STRING,longtitude STRING,latitude STRING,countryIsoCode STRING,region STRING,city STRING,isp STRING,usageType STRING,target_ip STRING,target_machine STRING,normalized_dst_machine STRING,dst_class STRING,dst_country STRING,dst_longtitude STRING,dst_latitude STRING,dst_countryIsoCode STRING,dst_region STRING,dst_city STRING,dst_isp STRING,dst_usageType STRING,status STRING,isUserAdministrator BOOLEAN, isUserExecutive BOOLEAN,isUserServiceAccount BOOLEAN,is_sensitive_machine BOOLEAN";
    private static final String SCORE_AUTH_SCHEMA_FIELDS_AS_CSV = "date_time_score DOUBLE,eventscore DOUBLE,source_machine_score DOUBLE,country_score DOUBLE,destination_machine_score DOUBLE";
    private static final String CUSTOMED_AUTH_SCHEMA_FIELDS_AS_CSV = "date_time TIMESTAMP,date_time_unix BIGINT,username STRING,normalized_username STRING,source_ip STRING,hostname STRING,src_class STRING,country STRING,longtitude STRING,latitude STRING,countryIsoCode STRING,region STRING,city STRING,isp STRING,usageType STRING,target_ip STRING,target_machine STRING,dst_class STRING,dst_country STRING,dst_longtitude STRING,dst_latitude STRING,dst_countryIsoCode STRING,dst_region STRING,dst_city STRING,dst_isp STRING,dst_usageType STRING,action_type STRING,status STRING,isUserAdministrator BOOLEAN, isUserExecutive BOOLEAN,isUserServiceAccount BOOLEAN,is_sensitive_machine BOOLEAN";
    private static final String SCORE_CUSTOMED_AUTH_SCHEMA_FIELDS_AS_CSV = "date_time_score DOUBLE,eventscore DOUBLE,source_machine_score DOUBLE,country_score DOUBLE,destination_machine_score DOUBLE,action_type_score DOUBLE";


    @Override
    public Map<String, ConfigurationParam> populateConfigurationData(GDSCompositeConfigurationState currentConfigurationState) throws Exception {
        Map<String, ConfigurationParam> paramsMap = new HashMap<>();

        populateBaseDataSourceDefinitions(paramsMap);

        String additionalFieldsCSV="";
        String additionalScoreFieldsCSV="";

        String dataSourceName = paramsMap.get("dataSourceName").getParamValue();
        String dataSourceType = paramsMap.get("dataSourceType").getParamValue();

        System.out.println(String.format("Does %s data source have additional fields (y/n)", paramsMap.get("dataSourceName")));
        String result = gdsInputHandler.getInput();
        if(GDSUserInputHelper.isConfirmed(result))
        {
            additionalFieldsCSV=",";

            System.out.println(String.format("Please enter %s data source additional fields csv style (i.e url STRING,application STRING  etc): ", paramsMap.get("dataSourceName")));

            additionalFieldsCSV += gdsInputHandler.getInput();

            System.out.println(String.format("Does %s data source have additional score fields (y/n)", paramsMap.get("dataSourceName")));
            result = gdsInputHandler.getInput();

            if(GDSUserInputHelper.isConfirmed(result)) {
                additionalScoreFieldsCSV=",";
                System.out.println(String.format("Please enter %s data source additional score fields csv style  (i.e url_score STRING,application_score STRING  etc): ", paramsMap.get("dataSourceName")));
                additionalScoreFieldsCSV += gdsInputHandler.getInput();
            }
        }

        switch(dataSourceType)
        {
            case "base":
            {
                paramsMap.put("dataFields" ,new ConfigurationParam("dataFields",false,BASE_SCHEMA_FIELDS_AS_CSV+additionalFieldsCSV));
                paramsMap.put("enrichFields",new ConfigurationParam("enrichFields",false,BASE_SCHEMA_FIELDS_AS_CSV+additionalFieldsCSV));
                paramsMap.put("scoreFields",new ConfigurationParam("scoreFields",false,BASE_SCHEMA_FIELDS_AS_CSV+additionalFieldsCSV+additionalScoreFieldsCSV));
                paramsMap.put("sourceIpFlag",new ConfigurationParam("sourceIpFlag",false,""));
                paramsMap.put("targetIpFlag",new ConfigurationParam("targetIpFlag",false,""));
                break;
            }

            case "access_event":
            {
                paramsMap.put("dataFields" ,new ConfigurationParam("dataFields",false,DATA_ACCESS_SCHEMA_FIELDS_AS_CSV+additionalFieldsCSV));
                paramsMap.put("enrichFields",new ConfigurationParam("enrichFields",false,DATA_ACCESS_SCHEMA_FIELDS_AS_CSV+additionalFieldsCSV));
                paramsMap.put("scoreFields",new ConfigurationParam("scoreFields",false,DATA_ACCESS_SCHEMA_FIELDS_AS_CSV+","+SCORE_DATA_ACCESS_SCHEMA_FIELDS_AS_CSV+additionalFieldsCSV+additionalScoreFieldsCSV));
                paramsMap.put("sourceIpFlag",new ConfigurationParam("sourceIpFlag",true,""));

                System.out.println(String.format("Does %s source ip should be resolved (y/n)?",dataSourceName));
                result = gdsInputHandler.getInput();
                paramsMap.put("sourceIpResolvingFlag",new ConfigurationParam("ResolvingFlag", GDSUserInputHelper.isConfirmed(result),""));

                System.out.println(String.format("Does %s source ip should be geo located (y/n)?",dataSourceName));
                result = gdsInputHandler.getInput();
                paramsMap.put("sourceIpGeoLocationFlag",new ConfigurationParam("GeoLocationFlag", GDSUserInputHelper.isConfirmed(result),""));

                System.out.println(String.format("Does %s source machine name should be normalized (y/n)?",dataSourceName));
                result = gdsInputHandler.getInput();
                paramsMap.put("sourceMachineNormalizationFlag",new ConfigurationParam("MachineNormalizationFlag", GDSUserInputHelper.isConfirmed(result),""));


                paramsMap.put("targetIpFlag",new ConfigurationParam("targetIpFlag",false,""));
                break;


            }
            case "auth_event":
            {
                paramsMap.put("dataFields", new ConfigurationParam("dataFields",false,AUTH_SCHEMA_FIELDS_AS_CSV+additionalFieldsCSV));
                paramsMap.put("enrichFields",new ConfigurationParam("enrichFields",false,AUTH_SCHEMA_FIELDS_AS_CSV+additionalFieldsCSV));
                paramsMap.put("scoreFields",new ConfigurationParam("scoreFields",false,AUTH_SCHEMA_FIELDS_AS_CSV+","+SCORE_AUTH_SCHEMA_FIELDS_AS_CSV+additionalFieldsCSV+additionalScoreFieldsCSV));
                paramsMap.put("sourceIpFlag",new ConfigurationParam("sourceIpFlag",true,""));

                System.out.println(String.format("Does %s source ip should be resolved (y/n)?",dataSourceName));
                result = gdsInputHandler.getInput();
                paramsMap.put("sourceIpResolvingFlag",new ConfigurationParam("ResolvingFlag", GDSUserInputHelper.isConfirmed(result),""));

                System.out.println(String.format("Does %s source ip should be geo located (y/n)?",dataSourceName));
                result = gdsInputHandler.getInput();
                paramsMap.put("sourceIpGeoLocationFlag",new ConfigurationParam("GeoLocationFlag", GDSUserInputHelper.isConfirmed(result),""));

                System.out.println(String.format("Does %s source machine name should be normalized (y/n)?",dataSourceName));
                result = gdsInputHandler.getInput();
                paramsMap.put("sourceMachineNormalizationFlag",new ConfigurationParam("MachineNormalizationFlag", GDSUserInputHelper.isConfirmed(result),""));


                paramsMap.put("targetIpFlag",new ConfigurationParam("targetIpFlag",true,""));

                System.out.println(String.format("Does %s target ip should be resolved (y/n)?",dataSourceName));
                result = gdsInputHandler.getInput();
                paramsMap.put("targetIpResolvingFlag",new ConfigurationParam("ResolvingFlag", GDSUserInputHelper.isConfirmed(result),""));

                System.out.println(String.format("Does %s target ip should be geo located (y/n)?",dataSourceName));
                result = gdsInputHandler.getInput();
                paramsMap.put("targetIpGeoLocationFlag",new ConfigurationParam("GeoLocationFlag", GDSUserInputHelper.isConfirmed(result),""));

                System.out.println(String.format("Does %s target machine name should be normalized (y/n)?",dataSourceName));
                result = gdsInputHandler.getInput();
                paramsMap.put("targetMachineNormalizationFlag",new ConfigurationParam("MachineNormalizationFlag", GDSUserInputHelper.isConfirmed(result),""));
                break;
            }
            case "customized_auth_event" :
            {
                paramsMap.put("dataFields" ,new ConfigurationParam("dataFields",false,CUSTOMED_AUTH_SCHEMA_FIELDS_AS_CSV+additionalFieldsCSV));
                paramsMap.put("enrichFields",new ConfigurationParam("enrichFields",false,CUSTOMED_AUTH_SCHEMA_FIELDS_AS_CSV+additionalFieldsCSV));
                paramsMap.put("scoreFields",new ConfigurationParam("scoreFields",false,CUSTOMED_AUTH_SCHEMA_FIELDS_AS_CSV+","+SCORE_CUSTOMED_AUTH_SCHEMA_FIELDS_AS_CSV+additionalFieldsCSV+additionalScoreFieldsCSV));
                paramsMap.put("sourceIpFlag",new ConfigurationParam("sourceIpFlag",true,""));

                System.out.println(String.format("Does %s source ip should be resolved (y/n)?",dataSourceName));
                result = gdsInputHandler.getInput();
                paramsMap.put("sourceIpResolvingFlag",new ConfigurationParam("ResolvingFlag", GDSUserInputHelper.isConfirmed(result),""));

                System.out.println(String.format("Does %s source ip should be geo located (y/n)?",dataSourceName));
                result = gdsInputHandler.getInput();
                paramsMap.put("sourceIpGeoLocationFlag",new ConfigurationParam("GeoLocationFlag", GDSUserInputHelper.isConfirmed(result),""));

                System.out.println(String.format("Does %s source machine name should be normalized (y/n)?",dataSourceName));
                result = gdsInputHandler.getInput();
                paramsMap.put("sourceMachineNormalizationFlag",new ConfigurationParam("MachineNormalizationFlag", GDSUserInputHelper.isConfirmed(result),""));

                paramsMap.put("targetIpFlag",new ConfigurationParam("targetIpFlag",true,""));

                System.out.println(String.format("Does %s target ip should be resolved (y/n)?",dataSourceName));
                result = gdsInputHandler.getInput();
                paramsMap.put("targetIpResolvingFlag",new ConfigurationParam("ResolvingFlag", GDSUserInputHelper.isConfirmed(result),""));

                System.out.println(String.format("Does %s target ip should be geo located (y/n)?",dataSourceName));
                result = gdsInputHandler.getInput();
                paramsMap.put("targetIpGeoLocationFlag",new ConfigurationParam("GeoLocationFlag", GDSUserInputHelper.isConfirmed(result),""));

                System.out.println(String.format("Does %s target machine name should be normalized (y/n)?",dataSourceName));
                result = gdsInputHandler.getInput();
                paramsMap.put("targetMachineNormalizationFlag",new ConfigurationParam("MachineNormalizationFlag", GDSUserInputHelper.isConfirmed(result),""));
                break;

            }
        }

        //delimiter
        System.out.println(String.format("Please enter the %s data schema delimiter  (i.e | or , )",dataSourceName));
        String delimiter = gdsInputHandler.getInput();
        paramsMap.put("dataDelimiter", new ConfigurationParam("delimiter",false,delimiter));

        //table name
        String tableName = dataSourceName+"data";
        paramsMap.put("dataTableName", new ConfigurationParam("TableName",false,tableName));

        //sensitive_machine
        paramsMap.put("sensitive_machine", new ConfigurationParam("sensitive_machine",false,"is_sensitive_machine"));

        //delimiter
        paramsMap.put("enrichDelimiter", new ConfigurationParam("delimiter",false,delimiter));

        //table name
        tableName = dataSourceName+"enriched";
        paramsMap.put("enrichTableName", new ConfigurationParam("TableName",false,tableName));

        //delimiter
        paramsMap.put("scoreDelimiter", new ConfigurationParam("delimiter",false,","));

        //table name
        tableName = dataSourceName+"score";
        paramsMap.put("scoreTableName", new ConfigurationParam("TableName",false,tableName));

        //top score
        System.out.println(String.format("Does %s Have top table schema (y/n) ?",dataSourceName));
        String brResult =gdsInputHandler.getInput().toLowerCase();
        paramsMap.put("topSchemaFlag", new ConfigurationParam("topSchemaFlaf",brResult.equals("y") || brResult.equals("yes"),""));

        return paramsMap;
    }

    private void populateBaseDataSourceDefinitions(Map<String, ConfigurationParam> paramsMap) throws Exception {
        System.out.println("Please enter the data source name: ");
        String dataSourceName = gdsInputHandler.getInput();

        GDSMenuPrinterHelper.printDataSourceTypeMenuOptions(dataSourceName);
        String dataSourceType = gdsInputHandler.getInput();

        paramsMap.put("dataSourceName", new ConfigurationParam("dataSourceName", false, dataSourceName));
        paramsMap.put("dataSourceType", new ConfigurationParam("dataSourceType", false, dataSourceType.toLowerCase()));

        paramsMap.put("dataSourceLists", new ConfigurationParam("dataSourceLists", false, currentDataSources));
    }
}
