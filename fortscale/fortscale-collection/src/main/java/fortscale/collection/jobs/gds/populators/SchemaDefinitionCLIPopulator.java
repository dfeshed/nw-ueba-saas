package fortscale.collection.jobs.gds.populators;

import fortscale.collection.jobs.gds.helper.GDSUserInputHelper;
import fortscale.collection.jobs.gds.state.GDSConfigurationState;
import fortscale.collection.jobs.gds.state.SchemaDefinitionState;
import fortscale.services.configuration.ConfigurationParam;

import java.util.Map;

/**
 * @author gils
 * 03/01/2016
 */
public class SchemaDefinitionCLIPopulator extends GDSBaseCLIPopulator{

    public SchemaDefinitionCLIPopulator(GDSConfigurationState gdsConfigurationState) {
        super(gdsConfigurationState);
    }

    @Override
    public void populateConfigurationData() throws Exception {
        super.populateConfigurationData();

        String additionalFieldsCSV="";
        String additionalScoreFieldsCSV="";

        SchemaDefinitionState schemaDefinitionState = gdsConfigurationState.getSchemaDefinitionState();

        Map<String, ConfigurationParam> paramsMap = schemaDefinitionState.getParamsMap();

        paramsMap.put("dataSourceName", new ConfigurationParam("dataSourceName", false, gdsConfigurationState.getDataSourceName()));
        paramsMap.put("dataSourceType", new ConfigurationParam("dataSourceType", false, gdsConfigurationState.getEntityType().name().toLowerCase()));
        paramsMap.put("dataSourceLists", new ConfigurationParam("dataSourceLists", false, gdsConfigurationState.getCurrentDataSources()));

        String dataSourceName = gdsConfigurationState.getDataSourceName();

        System.out.println(String.format("Does %s data source have additional fields (y/n)", gdsConfigurationState.getDataSourceName()));
        String result = gdsInputHandler.getInput();
        if(GDSUserInputHelper.isConfirmed(result))
        {
            additionalFieldsCSV=",";

            System.out.println(String.format("Please enter %s data source additional fields csv style (i.e url STRING,application STRING  etc): ", gdsConfigurationState.getDataSourceName()));

            additionalFieldsCSV += gdsInputHandler.getInput();

            System.out.println(String.format("Does %s data source have additional score fields (y/n)", gdsConfigurationState.getDataSourceName()));
            result = gdsInputHandler.getInput();

            if(GDSUserInputHelper.isConfirmed(result)) {
                additionalScoreFieldsCSV=",";
                System.out.println(String.format("Please enter %s data source additional score fields csv style  (i.e url_score STRING,application_score STRING  etc): ", gdsConfigurationState.getDataSourceName()));
                additionalScoreFieldsCSV += gdsInputHandler.getInput();
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
        System.out.println(String.format("Dose %s Have top table schema (y/n) ?",dataSourceName));
        String brResult =gdsInputHandler.getInput().toLowerCase();
        paramsMap.put("topSchemaFlag", new ConfigurationParam("topSchemaFlaf",brResult.equals("y") || brResult.equals("yes"),""));

    }
}
