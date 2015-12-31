package fortscale.collection.jobs.gds;

import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.ConfigurationService;
import fortscale.services.configuration.Impl.InitPartConfiguration;
import fortscale.utils.logging.Logger;

import java.util.Map;

/**
 * Configure the Init configuration (The part that support the schema (HDFS paths and impala tables)
 *
 * @author gils
 * 30/12/2015
 */
public class GDSInitConfigurator extends GDSBaseConfigurator {

    private static Logger logger = Logger.getLogger(GDSInitConfigurator.class);

    private ConfigurationService initConfigurationService;

    //TODO - Generate this auto from the entities  properties
    private static final String BASE_SCHEMA_FIELDS_AS_CSV = "date_time TIMESTAMP,date_time_unix BIGINT,username STRING,normalized_username STRING,status STRING,isUserAdministrator BOOLEAN, isUserExecutive BOOLEAN,isUserServiceAccount BOOLEAN";
    private static final String DATA_ACCESS_SCHEMA_FIELDS_AS_CSV = "date_time TIMESTAMP,date_time_unix BIGINT,username STRING,normalized_username STRING,source_ip STRING,hostname STRING,normalized_src_machine STRING,src_class STRING,country STRING,longtitude STRING,latitude STRING,countryIsoCode STRING,region STRING,city STRING,isp STRING,usageType STRING,status STRING,isUserAdministrator BOOLEAN, isUserExecutive BOOLEAN,isUserServiceAccount BOOLEAN";
    private static final String SCORE_DATA_ACCESS_SCHEMA_FIELDS_AS_CSV = "date_time_score DOUBLE,eventscore DOUBLE,source_machine_score DOUBLE,country_score DOUBLE";
    private static final String AUTH_SCHEMA_FIELDS_AS_CSV = "date_time TIMESTAMP,date_time_unix BIGINT,username STRING,normalized_username STRING,source_ip STRING,hostname STRING,normalized_src_machine STRING,src_class STRING,country STRING,longtitude STRING,latitude STRING,countryIsoCode STRING,region STRING,city STRING,isp STRING,usageType STRING,target_ip STRING,target_machine STRING,normalized_dst_machine STRING,dst_class STRING,dst_country STRING,dst_longtitude STRING,dst_latitude STRING,dst_countryIsoCode STRING,dst_region STRING,dst_city STRING,dst_isp STRING,dst_usageType STRING,status STRING,isUserAdministrator BOOLEAN, isUserExecutive BOOLEAN,isUserServiceAccount BOOLEAN,is_sensitive_machine BOOLEAN";
    private static final String SCORE_AUTH_SCHEMA_FIELDS_AS_CSV = "date_time_score DOUBLE,eventscore DOUBLE,source_machine_score DOUBLE,country_score DOUBLE,destination_machine_score DOUBLE";
    private static final String CUSTOMED_AUTH_SCHEMA_FIELDS_AS_CSV = "date_time TIMESTAMP,date_time_unix BIGINT,username STRING,normalized_username STRING,source_ip STRING,hostname STRING,src_class STRING,country STRING,longtitude STRING,latitude STRING,countryIsoCode STRING,region STRING,city STRING,isp STRING,usageType STRING,target_ip STRING,target_machine STRING,dst_class STRING,dst_country STRING,dst_longtitude STRING,dst_latitude STRING,dst_countryIsoCode STRING,dst_region STRING,dst_city STRING,dst_isp STRING,dst_usageType STRING,action_type STRING,status STRING,isUserAdministrator BOOLEAN, isUserExecutive BOOLEAN,isUserServiceAccount BOOLEAN,is_sensitive_machine BOOLEAN";
    private static final String SCORE_CUSTOMED_AUTH_SCHEMA_FIELDS_AS_CSV = "date_time_score DOUBLE,eventscore DOUBLE,source_machine_score DOUBLE,country_score DOUBLE,destination_machine_score DOUBLE,action_type_score DOUBLE";

    public GDSInitConfigurator(GDSConfigurationState gdsConfigurationState) {
        super(gdsConfigurationState);
        initConfigurationService = new InitPartConfiguration();
    }

    public void configure() throws Exception {
        super.configure();

        String additionalFieldsCSV="";
        String additionalScoreFieldsCSV="";

        GDSConfigurationState.SchemaDefinitionsState schemaDefinitionsState = gdsConfigurationState.getSchemaDefinitionsState();

        Map<String, ConfigurationParam> paramsMap = schemaDefinitionsState.getParamsMap();

        paramsMap.put("dataSourceName", new ConfigurationParam("dataSourceName", false, gdsConfigurationState.getDataSourceName()));
        paramsMap.put("dataSourceType", new ConfigurationParam("dataSourceType", false, gdsConfigurationState.getEntityType().name().toLowerCase()));
        paramsMap.put("dataSourceLists", new ConfigurationParam("dataSourceLists", false, gdsConfigurationState.getCurrentDataSources()));

        String dataSourceName = gdsConfigurationState.getDataSourceName();

        System.out.println(String.format("Does %s data source have additional fields (y/n)", gdsConfigurationState.getDataSourceName()));
        String result = gdsInputHandler.getInput("include_additional_fields");
        if(isYesAnswer(result))
        {
            additionalFieldsCSV=",";

            System.out.println(String.format("Please enter %s data source additional fields csv style (i.e url STRING,application STRING  etc): ", gdsConfigurationState.getDataSourceName()));

            additionalFieldsCSV += gdsInputHandler.getInput("additional_fields");;

            System.out.println(String.format("Does %s data source have additional score fields (y/n)", gdsConfigurationState.getDataSourceName()));
            result = gdsInputHandler.getInput("include_additional_score_fields");

            if(isYesAnswer(result)) {
                additionalScoreFieldsCSV=",";
                System.out.println(String.format("Please enter %s data source additional score fields csv style  (i.e url_score STRING,application_score STRING  etc): ", gdsConfigurationState.getDataSourceName()));
                additionalScoreFieldsCSV += gdsInputHandler.getInput("additional_score_fields");
            }
        }

        switch(gdsConfigurationState.getEntityType().name().toLowerCase())
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
                result = gdsInputHandler.getInput("is_source_ip_resolving");
                paramsMap.put("sourceIpResolvingFlag",new ConfigurationParam("ResolvingFlag", isYesAnswer(result),""));

                System.out.println(String.format("Does %s source ip should be geo located (y/n)?",dataSourceName));
                result = gdsInputHandler.getInput("is_source_ip_geolocated");
                paramsMap.put("sourceIpGeoLocationFlag",new ConfigurationParam("GeoLocationFlag", isYesAnswer(result),""));

                System.out.println(String.format("Does %s source machine name should be normalized (y/n)?",dataSourceName));
                result = gdsInputHandler.getInput("is_source_machine_normalization");
                paramsMap.put("sourceMachineNormalizationFlag",new ConfigurationParam("MachineNormalizationFlag", isYesAnswer(result),""));


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
                result = gdsInputHandler.getInput("is_source_ip_resolving");
                paramsMap.put("sourceIpResolvingFlag",new ConfigurationParam("ResolvingFlag", isYesAnswer(result),""));

                System.out.println(String.format("Does %s source ip should be geo located (y/n)?",dataSourceName));
                result = gdsInputHandler.getInput("is_source_ip_geo_located");
                paramsMap.put("sourceIpGeoLocationFlag",new ConfigurationParam("GeoLocationFlag", isYesAnswer(result),""));

                System.out.println(String.format("Does %s source machine name should be normalized (y/n)?",dataSourceName));
                result = gdsInputHandler.getInput("is_source_machine_normalization");
                paramsMap.put("sourceMachineNormalizationFlag",new ConfigurationParam("MachineNormalizationFlag", isYesAnswer(result),""));


                paramsMap.put("targetIpFlag",new ConfigurationParam("targetIpFlag",true,""));

                System.out.println(String.format("Does %s target ip should be resolved (y/n)?",dataSourceName));
                result = gdsInputHandler.getInput("is_target_ip_resolving");
                paramsMap.put("targetIpResolvingFlag",new ConfigurationParam("ResolvingFlag", isYesAnswer(result),""));

                System.out.println(String.format("Does %s target ip should be geo located (y/n)?",dataSourceName));
                result = gdsInputHandler.getInput("is_target_ip_geo_located");
                paramsMap.put("targetIpGeoLocationFlag",new ConfigurationParam("GeoLocationFlag", isYesAnswer(result),""));

                System.out.println(String.format("Does %s target machine name should be normalized (y/n)?",dataSourceName));
                result = gdsInputHandler.getInput("is_target_machine_normalization");
                paramsMap.put("targetMachineNormalizationFlag",new ConfigurationParam("MachineNormalizationFlag", isYesAnswer(result),""));
                break;
            }
            case "customized_auth_event" :
            {
                paramsMap.put("dataFields" ,new ConfigurationParam("dataFields",false,CUSTOMED_AUTH_SCHEMA_FIELDS_AS_CSV+additionalFieldsCSV));
                paramsMap.put("enrichFields",new ConfigurationParam("enrichFields",false,CUSTOMED_AUTH_SCHEMA_FIELDS_AS_CSV+additionalFieldsCSV));
                paramsMap.put("scoreFields",new ConfigurationParam("scoreFields",false,CUSTOMED_AUTH_SCHEMA_FIELDS_AS_CSV+","+SCORE_CUSTOMED_AUTH_SCHEMA_FIELDS_AS_CSV+additionalFieldsCSV+additionalScoreFieldsCSV));
                paramsMap.put("sourceIpFlag",new ConfigurationParam("sourceIpFlag",true,""));

                System.out.println(String.format("Does %s source ip should be resolved (y/n)?",dataSourceName));
                result = gdsInputHandler.getInput("is_source_ip_resolved");
                paramsMap.put("sourceIpResolvingFlag",new ConfigurationParam("ResolvingFlag", isYesAnswer(result),""));

                System.out.println(String.format("Does %s source ip should be geo located (y/n)?",dataSourceName));
                result = gdsInputHandler.getInput("is_source_ip_geo_located");
                paramsMap.put("sourceIpGeoLocationFlag",new ConfigurationParam("GeoLocationFlag", isYesAnswer(result),""));

                System.out.println(String.format("Does %s source machine name should be normalized (y/n)?",dataSourceName));
                result = gdsInputHandler.getInput("is_source_machine_normalization");
                paramsMap.put("sourceMachineNormalizationFlag",new ConfigurationParam("MachineNormalizationFlag", isYesAnswer(result),""));

                paramsMap.put("targetIpFlag",new ConfigurationParam("targetIpFlag",true,""));

                System.out.println(String.format("Does %s target ip should be resolved (y/n)?",dataSourceName));
                result = gdsInputHandler.getInput("is_target_ip_resolved");
                paramsMap.put("targetIpResolvingFlag",new ConfigurationParam("ResolvingFlag", isYesAnswer(result),""));

                System.out.println(String.format("Does %s target ip should be geo located (y/n)?",dataSourceName));
                result = gdsInputHandler.getInput("is_target_ip_geo_located");
                paramsMap.put("targetIpGeoLocationFlag",new ConfigurationParam("GeoLocationFlag", isYesAnswer(result),""));

                System.out.println(String.format("Does %s target machine name should be normalized (y/n)?",dataSourceName));
                result = gdsInputHandler.getInput("is_target_machine_normalized");
                paramsMap.put("targetMachineNormalizationFlag",new ConfigurationParam("MachineNormalizationFlag", isYesAnswer(result),""));
                break;

            }
        }

        //delimiter
        System.out.println(String.format("Please enter the %s data schema delimiter  (i.e | or , )",dataSourceName));
        String delimiter = gdsInputHandler.getInput("data_schema_delimiter");
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
        System.out.println(String.format("Dose %s Have top table schema (y/n) ?",dataSourceName));
        String brResult =gdsInputHandler.getInput("additional_score_fields").toLowerCase();
        paramsMap.put("topSchemaFlag", new ConfigurationParam("topSchemaFlaf",brResult.equals("y") || brResult.equals("yes"),""));

        initConfigurationService.setConfigurationParams(paramsMap);
        System.out.println("Finish to configure the Schema part");

        System.out.println("Do you want to apply changes? (y/n)");
        if (isYesAnswer(gdsInputHandler.getInput("is_apply_changes"))) {
            apply();
        }
    }

    private static boolean isYesAnswer(String input) {
        return input.toLowerCase().equals("y") || input.toLowerCase().equals("yes");
    }

    @Override
    public void apply() throws Exception {
        if (initConfigurationService.init()) {
            initConfigurationService.applyConfiguration();
        }

        initConfigurationService.done();
    }

    @Override
    public void revert() throws Exception {

    }
}
