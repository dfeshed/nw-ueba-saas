package fortscale.collection.jobs.gds.input.populators;

import fortscale.collection.jobs.gds.helper.GDSMenuOptions;
import fortscale.collection.jobs.gds.input.GDSCLIInputHandler;
import fortscale.collection.jobs.gds.input.GDSInputHandler;
import fortscale.collection.jobs.gds.input.populators.enrichment.GDSConfigurationPopulator;
import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.gds.state.GDSCompositeConfigurationState;
import fortscale.services.configuration.gds.state.field.FieldType;
import fortscale.utils.ConversionUtils;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Schema definition command-line interface populator
 *
 * @author gils
 * 03/01/2016
 */
public class GDSSchemaDefinitionCLIPopulator implements GDSConfigurationPopulator {

    private static final String DATA_SOURCE_LISTS = "dataSourceLists";

    private static final String ADDITIONAL_FIELDS_USER_END_MARK = "done";
    private static final String ADDITIONAL_FIELD_SCORE_FIELD_DOUBLE_DATA_TYPE = "DOUBLE";

    private static final String BASE_DATA_SOURCE_TYPE = "base";
    private static final String ACCESS_EVENT_DATA_SOURCE_TYPE = "access_event";
    private static final String AUTH_EVENT_DATA_SOURCE_TYPE = "auth_event";
    private static final String CUSTOMIZED_AUTH_EVENT_DATA_SOURCE_TYPE = "customized_auth_event";

    private static final String DATA_SOURCE_NAME_PARAM = "dataSourceName";
    private static final String DATA_SOURCE_TYPE_PARAM = "dataSourceType";
    private static final String DATA_DELIMITER_PARAM = "dataDelimiter";
    private static final String DATA_TABLE_NAME_PARAM = "dataTableName";
    private static final String SENSITIVE_MACHINE_PARAM = "sensitive_machine";
    private static final String ENRICH_DELIMITER_PARAM = "enrichDelimiter";
    private static final String ENRICH_TABLE_NAME_PARAM = "enrichTableName";
    private static final String SCORE_DELIMITER_PARAM = "scoreDelimiter";
    private static final String SCORE_TABLE_NAME_PARAM = "scoreTableName";
    private static final String TOP_SCHEMA_FLAG_PARAM = "topSchemaFlag";

    private static final String SPACE = " ";
    private static final String COMMA = ",";
    private static final String EMPTY_STR = "";
    private static final String NORMALIZED_USER_NAME_FIELD_PARAM = "normalizedUserNameField";
    private static final String DATA_FIELDS_PARAM = "dataFields";
    private static final String ENRICH_FIELDS_PARAM = "enrichFields";
    private static final String SCORE_FIELDS_PARAM = "scoreFields";
    private static final String SOURCE_IP_FLAG_PARAM = "sourceIpFlag";
    private static final String TARGET_IP_FLAG_PARAM = "targetIpFlag";
    private static final String SCORE_FIELDS_CSV_PARAM = "scoreFieldsCSV";
    private static final String POPULATED_SCORE_FIELDS_CSV_PARAM = "populatedScoreFieldsCSV";
    private static final String POPULATED_ADDITIONAL_SCORE_FIELDS_CSV_PARAM = "additionalPopulatedScoreFieldsCSV";
    private static final String SCORE_FIELD_TO_FIELD_NAME_PARAM = "scoreFieldToFieldNameCSV";
    private static final String ADDITIONAL_SCORE_FIELDS_CSV_PARAM = "additionalScoreFieldsCSV";
	private static final String ADDITIONAL_FIELDS_CSV_PARAM = "additionalFieldsCSV";
	private static final String ADDITIONAL_FIELD_TO_ADDITIONAL_SCORE_FIELD_MAP = "additionalFiledToScoreFieldMapCSV";

    private static final String GDS_CONFIG_ENTRY = "gds.config.entry.";

    private GDSInputHandler gdsInputHandler = new GDSCLIInputHandler();

    @Value("${fortscale.data.source:xxxx}")
    private String currentDataSources = "ssh,vpn,kerberos_logins,login4768,vpn_session,crmsf"; // TODO only for windows workaround

    //TODO - Generate this auto from the entities  properties
    private static final String BASE_SCHEMA_FIELDS_AS_CSV = "date_time TIMESTAMP,date_time_unix BIGINT,username STRING,normalized_username STRING,status STRING,isUserAdministrator BOOLEAN, isUserExecutive BOOLEAN,isUserServiceAccount BOOLEAN";
    private static final String DATA_ACCESS_SCHEMA_FIELDS_AS_CSV = "date_time TIMESTAMP,date_time_unix BIGINT,username STRING,normalized_username STRING,source_ip STRING,hostname STRING,normalized_src_machine STRING,src_class STRING,country STRING,longtitude STRING,latitude STRING,countryIsoCode STRING,region STRING,city STRING,isp STRING,usageType STRING,status STRING,isUserAdministrator BOOLEAN, isUserExecutive BOOLEAN,isUserServiceAccount BOOLEAN,is_sensitive_machine BOOLEAN";
    private static final String SCORE_DATA_ACCESS_SCHEMA_FIELDS_AS_CSV = "date_time_score DOUBLE,eventscore DOUBLE,source_machine_score DOUBLE,country_score DOUBLE";
    private static final String AUTH_SCHEMA_FIELDS_AS_CSV = "date_time TIMESTAMP,date_time_unix BIGINT,username STRING,normalized_username STRING,source_ip STRING,hostname STRING,normalized_src_machine STRING,src_class STRING,country STRING,longtitude STRING,latitude STRING,countryIsoCode STRING,region STRING,city STRING,isp STRING,usageType STRING,target_ip STRING,target_machine STRING,normalized_dst_machine STRING,dst_class STRING,dst_country STRING,dst_longtitude STRING,dst_latitude STRING,dst_countryIsoCode STRING,dst_region STRING,dst_city STRING,dst_isp STRING,dst_usageType STRING,status STRING,isUserAdministrator BOOLEAN, isUserExecutive BOOLEAN,isUserServiceAccount BOOLEAN,is_sensitive_machine BOOLEAN";
    private static final String SCORE_AUTH_SCHEMA_FIELDS_AS_CSV = "date_time_score DOUBLE,eventscore DOUBLE,source_machine_score DOUBLE,country_score DOUBLE,destination_machine_score DOUBLE";
    private static final String CUSTOMED_AUTH_SCHEMA_FIELDS_AS_CSV = "date_time TIMESTAMP,date_time_unix BIGINT,username STRING,normalized_username STRING,source_ip STRING,hostname STRING,src_class STRING,country STRING,longtitude STRING,latitude STRING,countryIsoCode STRING,region STRING,city STRING,isp STRING,usageType STRING,target_ip STRING,target_machine STRING,dst_class STRING,dst_country STRING,dst_longtitude STRING,dst_latitude STRING,dst_countryIsoCode STRING,dst_region STRING,dst_city STRING,dst_isp STRING,dst_usageType STRING,action_type STRING,status STRING,isUserAdministrator BOOLEAN, isUserExecutive BOOLEAN,isUserServiceAccount BOOLEAN,is_sensitive_machine BOOLEAN";
    private static final String SCORE_CUSTOMED_AUTH_SCHEMA_FIELDS_AS_CSV = "date_time_score DOUBLE,eventscore DOUBLE,source_machine_score DOUBLE,country_score DOUBLE,destination_machine_score DOUBLE,action_type_score DOUBLE";
    private static final String SCORE_FIELD_TO_FIELD_NAME_CSV = "date_time_score date_time, source_machine_score normalized_src_machine, country_score country, destination_machine_score normalized_dst_machine";

    @Override
    public Map<String, Map<String, ConfigurationParam>> populateConfigurationData(GDSCompositeConfigurationState currentConfigurationState) throws Exception {
        Map<String, Map<String, ConfigurationParam>> configurationsMap = new HashMap<>();
        HashMap<String, ConfigurationParam> paramsMap = new HashMap<>();

        configurationsMap.put(GDS_CONFIG_ENTRY, paramsMap);

        populateBaseDataSourceDefinitions(paramsMap);

        String dataSourceName = paramsMap.get(DATA_SOURCE_NAME_PARAM).getParamValue();
        String dataSourceType = paramsMap.get(DATA_SOURCE_TYPE_PARAM).getParamValue();

        AdditionalFieldsWrapper additionalFieldsWrapper = populateAdditionalFields(dataSourceName);

        populateDataSourceTypeFields(paramsMap, dataSourceName, additionalFieldsWrapper, dataSourceType);

        String[] allowedDelimitersArr = {"|", ","};
        Set<String> allowedDelimiters = new HashSet<>(Arrays.asList(allowedDelimitersArr));
        System.out.println(String.format("Please enter the %s data schema delimiter  (i.e | or , )",dataSourceName));
        String delimiter = gdsInputHandler.getInput(allowedDelimiters);
        paramsMap.put(DATA_DELIMITER_PARAM, new ConfigurationParam(DATA_DELIMITER_PARAM,false,delimiter));

        paramsMap.put(SCORE_FIELD_TO_FIELD_NAME_PARAM, new ConfigurationParam(SCORE_FIELD_TO_FIELD_NAME_PARAM,false,SCORE_FIELD_TO_FIELD_NAME_CSV));

        //table name
        String tableName = dataSourceName+"data";
        paramsMap.put(DATA_TABLE_NAME_PARAM, new ConfigurationParam(DATA_TABLE_NAME_PARAM,false,tableName));

        //sensitive_machine
        paramsMap.put(SENSITIVE_MACHINE_PARAM, new ConfigurationParam(SENSITIVE_MACHINE_PARAM,false,"is_sensitive_machine"));

        //delimiter
        paramsMap.put(ENRICH_DELIMITER_PARAM, new ConfigurationParam(ENRICH_DELIMITER_PARAM,false,delimiter));

        //table name
        tableName = dataSourceName+"enriched";
        paramsMap.put(ENRICH_TABLE_NAME_PARAM, new ConfigurationParam(ENRICH_TABLE_NAME_PARAM,false,tableName));

        //delimiter
        paramsMap.put(SCORE_DELIMITER_PARAM, new ConfigurationParam(SCORE_DELIMITER_PARAM,false, GDSSchemaDefinitionCLIPopulator.COMMA));

        //table name
        tableName = dataSourceName+"score";
        paramsMap.put(SCORE_TABLE_NAME_PARAM, new ConfigurationParam(SCORE_TABLE_NAME_PARAM,false,tableName));

        //top score
        System.out.println(String.format("Does %s has top table schema (y/n) ?",dataSourceName));
        paramsMap.put(TOP_SCHEMA_FLAG_PARAM, new ConfigurationParam(TOP_SCHEMA_FLAG_PARAM, gdsInputHandler.getYesNoInput(), EMPTY_STR));

        paramsMap.put(NORMALIZED_USER_NAME_FIELD_PARAM, new ConfigurationParam(NORMALIZED_USER_NAME_FIELD_PARAM, false, "${impala.table.fields.normalized.username}"));

        return configurationsMap;
    }

    private void populateDataSourceTypeFields(Map<String, ConfigurationParam> paramsMap, String dataSourceName, AdditionalFieldsWrapper additionalFieldsWrapper, String dataSourceType) throws Exception {
        String scoreFieldsCSV = null;

        switch(dataSourceType)
        {
            case BASE_DATA_SOURCE_TYPE:
            {
                paramsMap.put(DATA_FIELDS_PARAM,new ConfigurationParam(DATA_FIELDS_PARAM,false,BASE_SCHEMA_FIELDS_AS_CSV + additionalFieldsWrapper.getAdditionalFieldsCSV()));
                paramsMap.put(ENRICH_FIELDS_PARAM,new ConfigurationParam(ENRICH_FIELDS_PARAM,false,BASE_SCHEMA_FIELDS_AS_CSV + additionalFieldsWrapper.getAdditionalFieldsCSV()));
                paramsMap.put(SCORE_FIELDS_PARAM,new ConfigurationParam(SCORE_FIELDS_PARAM,false,BASE_SCHEMA_FIELDS_AS_CSV + additionalFieldsWrapper.getAdditionalFieldsCSV()+additionalFieldsWrapper.getAdditionalScoreFieldsCSV()));
                paramsMap.put(SOURCE_IP_FLAG_PARAM,new ConfigurationParam(SOURCE_IP_FLAG_PARAM,false, EMPTY_STR));
                paramsMap.put(TARGET_IP_FLAG_PARAM,new ConfigurationParam(TARGET_IP_FLAG_PARAM,false, EMPTY_STR));
                scoreFieldsCSV = EMPTY_STR;
                break;
            }

            case ACCESS_EVENT_DATA_SOURCE_TYPE:
            {
                paramsMap.put(DATA_FIELDS_PARAM,new ConfigurationParam(DATA_FIELDS_PARAM,false,DATA_ACCESS_SCHEMA_FIELDS_AS_CSV + additionalFieldsWrapper.getAdditionalFieldsCSV()));
                paramsMap.put(ENRICH_FIELDS_PARAM,new ConfigurationParam(ENRICH_FIELDS_PARAM,false,DATA_ACCESS_SCHEMA_FIELDS_AS_CSV + additionalFieldsWrapper.getAdditionalFieldsCSV()));
                paramsMap.put(SCORE_FIELDS_PARAM,new ConfigurationParam(SCORE_FIELDS_PARAM,false,DATA_ACCESS_SCHEMA_FIELDS_AS_CSV + COMMA + SCORE_DATA_ACCESS_SCHEMA_FIELDS_AS_CSV + additionalFieldsWrapper.getAdditionalFieldsCSV() + additionalFieldsWrapper.getAdditionalScoreFieldsCSV()));
                paramsMap.put(SOURCE_IP_FLAG_PARAM,new ConfigurationParam(SOURCE_IP_FLAG_PARAM,true, EMPTY_STR));
                scoreFieldsCSV = SCORE_DATA_ACCESS_SCHEMA_FIELDS_AS_CSV;

                System.out.println(String.format("Does %s source ip should be resolved (y/n)?",dataSourceName));
                paramsMap.put("sourceIpResolvingFlag",new ConfigurationParam("ResolvingFlag", gdsInputHandler.getYesNoInput(), EMPTY_STR));

                System.out.println(String.format("Does %s source ip should be geo located (y/n)?",dataSourceName));
                paramsMap.put("sourceIpGeoLocationFlag",new ConfigurationParam("GeoLocationFlag",gdsInputHandler.getYesNoInput(), EMPTY_STR));

                System.out.println(String.format("Does %s source machine name should be normalized (y/n)?",dataSourceName));
                paramsMap.put("sourceMachineNormalizationFlag",new ConfigurationParam("MachineNormalizationFlag",gdsInputHandler.getYesNoInput(), EMPTY_STR));

                paramsMap.put(TARGET_IP_FLAG_PARAM,new ConfigurationParam(TARGET_IP_FLAG_PARAM,false, EMPTY_STR));
                break;
            }
            case AUTH_EVENT_DATA_SOURCE_TYPE:
            {
                paramsMap.put(DATA_FIELDS_PARAM, new ConfigurationParam(DATA_FIELDS_PARAM,false,AUTH_SCHEMA_FIELDS_AS_CSV+additionalFieldsWrapper.getAdditionalFieldsCSV()));
                paramsMap.put(ENRICH_FIELDS_PARAM,new ConfigurationParam(ENRICH_FIELDS_PARAM,false,AUTH_SCHEMA_FIELDS_AS_CSV+additionalFieldsWrapper.getAdditionalFieldsCSV()));
                paramsMap.put(SCORE_FIELDS_PARAM,new ConfigurationParam(SCORE_FIELDS_PARAM,false,AUTH_SCHEMA_FIELDS_AS_CSV+","+SCORE_AUTH_SCHEMA_FIELDS_AS_CSV+additionalFieldsWrapper.getAdditionalFieldsCSV()+additionalFieldsWrapper.getAdditionalScoreFieldsCSV()));
                paramsMap.put(SOURCE_IP_FLAG_PARAM,new ConfigurationParam(SOURCE_IP_FLAG_PARAM,true, EMPTY_STR));

                scoreFieldsCSV = SCORE_AUTH_SCHEMA_FIELDS_AS_CSV;

                System.out.println(String.format("Does %s source ip should be resolved (y/n)?",dataSourceName));
                paramsMap.put("sourceIpResolvingFlag",new ConfigurationParam("ResolvingFlag",gdsInputHandler.getYesNoInput(), EMPTY_STR));

                System.out.println(String.format("Does %s source ip should be geo located (y/n)?",dataSourceName));
                paramsMap.put("sourceIpGeoLocationFlag",new ConfigurationParam("GeoLocationFlag",gdsInputHandler.getYesNoInput(), EMPTY_STR));

                System.out.println(String.format("Does %s source machine name should be normalized (y/n)?",dataSourceName));
                paramsMap.put("sourceMachineNormalizationFlag",new ConfigurationParam("MachineNormalizationFlag",gdsInputHandler.getYesNoInput(), EMPTY_STR));


                paramsMap.put(TARGET_IP_FLAG_PARAM,new ConfigurationParam(TARGET_IP_FLAG_PARAM,true, EMPTY_STR));

                System.out.println(String.format("Does %s target ip should be resolved (y/n)?",dataSourceName));
                paramsMap.put("targetIpResolvingFlag",new ConfigurationParam("ResolvingFlag",gdsInputHandler.getYesNoInput(), EMPTY_STR));

                System.out.println(String.format("Does %s target ip should be geo located (y/n)?",dataSourceName));
                paramsMap.put("targetIpGeoLocationFlag",new ConfigurationParam("GeoLocationFlag",gdsInputHandler.getYesNoInput(), EMPTY_STR));

                System.out.println(String.format("Does %s target machine name should be normalized (y/n)?",dataSourceName));
                paramsMap.put("targetMachineNormalizationFlag",new ConfigurationParam("MachineNormalizationFlag",gdsInputHandler.getYesNoInput(), EMPTY_STR));
                break;
            }
            case CUSTOMIZED_AUTH_EVENT_DATA_SOURCE_TYPE:
            {
                paramsMap.put(DATA_FIELDS_PARAM ,new ConfigurationParam(DATA_FIELDS_PARAM,false,CUSTOMED_AUTH_SCHEMA_FIELDS_AS_CSV+additionalFieldsWrapper.getAdditionalFieldsCSV()));
                paramsMap.put(ENRICH_FIELDS_PARAM,new ConfigurationParam(ENRICH_FIELDS_PARAM,false,CUSTOMED_AUTH_SCHEMA_FIELDS_AS_CSV+additionalFieldsWrapper.getAdditionalFieldsCSV()));
                paramsMap.put(SCORE_FIELDS_PARAM,new ConfigurationParam(SCORE_FIELDS_PARAM,false,CUSTOMED_AUTH_SCHEMA_FIELDS_AS_CSV+","+SCORE_CUSTOMED_AUTH_SCHEMA_FIELDS_AS_CSV+additionalFieldsWrapper.getAdditionalFieldsCSV()+additionalFieldsWrapper.getAdditionalScoreFieldsCSV()));
                paramsMap.put(SOURCE_IP_FLAG_PARAM,new ConfigurationParam(SOURCE_IP_FLAG_PARAM,true, EMPTY_STR));

                scoreFieldsCSV = SCORE_CUSTOMED_AUTH_SCHEMA_FIELDS_AS_CSV;

                System.out.println(String.format("Does %s source ip should be resolved (y/n)?",dataSourceName));
                paramsMap.put("sourceIpResolvingFlag",new ConfigurationParam("ResolvingFlag",gdsInputHandler.getYesNoInput(), EMPTY_STR));

                System.out.println(String.format("Does %s source ip should be geo located (y/n)?",dataSourceName));
                paramsMap.put("sourceIpGeoLocationFlag",new ConfigurationParam("GeoLocationFlag",gdsInputHandler.getYesNoInput(), EMPTY_STR));

                System.out.println(String.format("Does %s source machine name should be normalized (y/n)?",dataSourceName));
                paramsMap.put("sourceMachineNormalizationFlag",new ConfigurationParam("MachineNormalizationFlag",gdsInputHandler.getYesNoInput(), EMPTY_STR));

                paramsMap.put(TARGET_IP_FLAG_PARAM,new ConfigurationParam(TARGET_IP_FLAG_PARAM,true, EMPTY_STR));

                System.out.println(String.format("Does %s target ip should be resolved (y/n)?",dataSourceName));
                paramsMap.put("targetIpResolvingFlag",new ConfigurationParam("ResolvingFlag",gdsInputHandler.getYesNoInput(), EMPTY_STR));

                System.out.println(String.format("Does %s target ip should be geo located (y/n)?",dataSourceName));
                paramsMap.put("targetIpGeoLocationFlag",new ConfigurationParam("GeoLocationFlag",gdsInputHandler.getYesNoInput(), EMPTY_STR));

                System.out.println(String.format("Does %s target machine name should be normalized (y/n)?",dataSourceName));
                paramsMap.put("targetMachineNormalizationFlag",new ConfigurationParam("MachineNormalizationFlag",gdsInputHandler.getYesNoInput(), EMPTY_STR));
                break;
            }
        }

        handleIsScoreFieldInUseIndication(paramsMap, scoreFieldsCSV);

        paramsMap.put(SCORE_FIELDS_CSV_PARAM,new ConfigurationParam(SCORE_FIELDS_CSV_PARAM,false, scoreFieldsCSV));
        paramsMap.put(ADDITIONAL_SCORE_FIELDS_CSV_PARAM,new ConfigurationParam(ADDITIONAL_SCORE_FIELDS_CSV_PARAM,false,additionalFieldsWrapper.getAdditionalScoreFieldsCSV()));
		paramsMap.put(ADDITIONAL_FIELDS_CSV_PARAM , new ConfigurationParam(ADDITIONAL_FIELDS_CSV_PARAM,false,additionalFieldsWrapper.getAdditionalFieldsCSV()));
        paramsMap.put(POPULATED_ADDITIONAL_SCORE_FIELDS_CSV_PARAM , new ConfigurationParam(POPULATED_ADDITIONAL_SCORE_FIELDS_CSV_PARAM,false,additionalFieldsWrapper.getAdditionalPopulatedScoreFields()));
		paramsMap.put(ADDITIONAL_FIELD_TO_ADDITIONAL_SCORE_FIELD_MAP , new ConfigurationParam(ADDITIONAL_FIELD_TO_ADDITIONAL_SCORE_FIELD_MAP,false,additionalFieldsWrapper.getAdditionalFiledToScoreFieldMapCSV()));

    }

    private void handleIsScoreFieldInUseIndication(Map<String, ConfigurationParam> paramsMap, String scoreFieldsCSV) throws Exception {
        if (scoreFieldsCSV == null || EMPTY_STR.equals(scoreFieldsCSV)) {
            paramsMap.put(POPULATED_SCORE_FIELDS_CSV_PARAM,new ConfigurationParam(POPULATED_SCORE_FIELDS_CSV_PARAM,false, EMPTY_STR));
        }

        Map<String,String> potentialScoresFieldMap = ConversionUtils.convertCSVToMap(scoreFieldsCSV);

        StringBuilder populatedScoreFieldsCSV = new StringBuilder();

        //For each potential basic score field ask if we want to populate it
        for (Map.Entry<String,String> entry : potentialScoresFieldMap.entrySet())
        {
            String scoreField = entry.getKey();

            System.out.println(String.format("Is %s field should be populated (y/n)?", scoreField));

            if (gdsInputHandler.getYesNoInput()) {
                populatedScoreFieldsCSV.append(scoreField).append(",");
            }
        }

        paramsMap.put(POPULATED_SCORE_FIELDS_CSV_PARAM,new ConfigurationParam(POPULATED_SCORE_FIELDS_CSV_PARAM,false, populatedScoreFieldsCSV.toString()));
    }

    private void populateBaseDataSourceDefinitions(Map<String, ConfigurationParam> paramsMap) throws Exception {

        System.out.println("Please enter the new data source name:");
        String dataSourceName = gdsInputHandler.getInput();

        printEntityTypeOptions(dataSourceName);
        String dataSourceType = handleEntityTypeSelection();

        paramsMap.put(DATA_SOURCE_NAME_PARAM, new ConfigurationParam(DATA_SOURCE_NAME_PARAM, false, dataSourceName));
        paramsMap.put(DATA_SOURCE_TYPE_PARAM, new ConfigurationParam(DATA_SOURCE_TYPE_PARAM, false, dataSourceType.toLowerCase()));

        paramsMap.put(DATA_SOURCE_LISTS, new ConfigurationParam(DATA_SOURCE_LISTS, false, currentDataSources));
    }

    private String handleEntityTypeSelection() throws Exception {
        String selection = gdsInputHandler.getInput().trim();

        while (true) {
            switch (selection) {
                case GDSMenuOptions.GDS_SCHEMA_TYPE_BASE: {
                    return "base";
                }
                case GDSMenuOptions.GDS_SCHEMA_TYPE_ACCESS_EVENT: {
                    return "access_event";
                }
                case GDSMenuOptions.GDS_SCHEMA_TYPE_AUTH_EVENT: {
                    return "auth_event";
                }
                case GDSMenuOptions.GDS_SCHEMA_TYPE_CUSTOMIZED_AUTH_EVENT: {
                    return "customized_auth_event";
                }
                default: {
                    System.out.println("Illegal input. Please enter [1-4]");
                    selection = gdsInputHandler.getInput().trim();
                    break;
                }
            }
        }
    }

    private AdditionalFieldsWrapper populateAdditionalFields(String dataSourceName) throws Exception {

        System.out.println(String.format("Does %s data source have additional fields (y/n)", dataSourceName));
        boolean shouldHandleAdditionalFields = gdsInputHandler.getYesNoInput();

        if(shouldHandleAdditionalFields) {
            String additionalFieldsCSV = COMMA;
            String additionalScoreFieldsCSV = COMMA;
            String additionalFiledToScoreFieldMapCSV = EMPTY_STR;
            String additionalScoreFieldNames = "";

            System.out.println(String.format("Please enter %s data source additional fields. When you are done please type \"Done\"", dataSourceName));

            while (true) {
                //get the additional field name
                System.out.println("Field name:");
                String additionalFieldName = gdsInputHandler.getInput();

                //in case the user want to stop the insertion
                if (additionalFieldName.toLowerCase().equals(ADDITIONAL_FIELDS_USER_END_MARK)) {
                    break;
                }

                Set<String> allowedValues = Arrays.asList(FieldType.values()).stream().map(Enum::name).collect(Collectors.toSet());
                System.out.println("Field data type name: " + allowedValues);
                String additionalFieldDataType = gdsInputHandler.getInput(allowedValues);

                //in case the user want to stop the insertion
                if (additionalFieldDataType.toLowerCase().equals(ADDITIONAL_FIELDS_USER_END_MARK)) {
                    break;
                }

                additionalFieldDataType = additionalFieldDataType.toUpperCase();

                //add the additional field to the csv list of the additional fields
                additionalFieldsCSV = additionalFieldsCSV + additionalFieldName + SPACE + additionalFieldDataType + GDSSchemaDefinitionCLIPopulator.COMMA;

                System.out.println(String.format("Is the field %s should be scored (y/n)? ", additionalFieldName));
                if (gdsInputHandler.getYesNoInput()) {
                    //get the additional score field name
                    System.out.println("Score field name:");
                    String additionalScoreFieldName = gdsInputHandler.getInput();

                    //in case the user want to stop the insertion
                    if (additionalScoreFieldName.toLowerCase().equals(ADDITIONAL_FIELDS_USER_END_MARK)) {
                        break;
                    }

                    String additionalScoreFieldDataType = ADDITIONAL_FIELD_SCORE_FIELD_DOUBLE_DATA_TYPE;

                    //add the additional field to the csv list of the additional fields
                    additionalScoreFieldsCSV = additionalScoreFieldsCSV + additionalScoreFieldName + SPACE + additionalScoreFieldDataType + COMMA;
                    additionalFiledToScoreFieldMapCSV = additionalFiledToScoreFieldMapCSV + additionalScoreFieldName + SPACE + additionalFieldName + GDSSchemaDefinitionCLIPopulator.COMMA;
                    additionalScoreFieldNames += additionalScoreFieldNames + additionalScoreFieldName + COMMA;
                }
            }

            //remove the last comma from the CSVs
            additionalScoreFieldsCSV = additionalScoreFieldsCSV.substring(0, additionalScoreFieldsCSV.length()>0 ? additionalScoreFieldsCSV.length()  - 1 : 0);
            additionalFieldsCSV = additionalFieldsCSV.substring(0, additionalFieldsCSV.length()>0 ? additionalFieldsCSV.length() - 1 : 0);
            additionalFiledToScoreFieldMapCSV = additionalFiledToScoreFieldMapCSV.substring(0, additionalFiledToScoreFieldMapCSV.length()>0 ? additionalFiledToScoreFieldMapCSV.length() - 1: 0);
            additionalScoreFieldNames = additionalScoreFieldNames.substring(0, additionalScoreFieldNames.length()>0 ? additionalScoreFieldNames.length() - 1: 0);

            return new AdditionalFieldsWrapper(additionalFieldsCSV, additionalScoreFieldsCSV, additionalFiledToScoreFieldMapCSV, additionalScoreFieldNames);
        }
        else {
            return new AdditionalFieldsWrapper(EMPTY_STR, EMPTY_STR, EMPTY_STR, EMPTY_STR);
        }
    }

    private static void printEntityTypeOptions(String dataSourceName) {
        System.out.println(String.format("Please chose the %s data source type : ", dataSourceName));
        System.out.println("[* - meaning mandatory field ? -meaning optional field] ");
        System.out.println("1. base                    - user* , time*  ");
        System.out.println("2. access_event            - user* , time*, source? (resolving,geo location)?  ");
        System.out.println("3. auth_event              - user* , time*, source? (resolving,geo location)? , target? (resolving,geo location)?  ");
        System.out.println("4. customized_auth_event   - user* , time*, source? (resolving,geo location)? , target? (resolving,geo location)?, action? , data usage? ");
    }

    private static class AdditionalFieldsWrapper {
        private String additionalFieldsCSV;
        private String additionalScoreFieldsCSV;
        private String additionalFiledToScoreFieldMapCSV;
        private String additionalPopulatedScoreFields;

        public AdditionalFieldsWrapper(String additionalFieldsCSV, String additionalScoreFieldsCSV, String additionalFiledToScoreFieldMapCSV, String additionalPopulatedScoreFields) {
            this.additionalScoreFieldsCSV = additionalScoreFieldsCSV;
            this.additionalFieldsCSV = additionalFieldsCSV;
            this.additionalFiledToScoreFieldMapCSV = additionalFiledToScoreFieldMapCSV;
            this.additionalPopulatedScoreFields = additionalPopulatedScoreFields;
        }

        public String getAdditionalScoreFieldsCSV() {
            return additionalScoreFieldsCSV;
        }

        public String getAdditionalFieldsCSV() {
            return additionalFieldsCSV;
        }

        public String getAdditionalFiledToScoreFieldMapCSV() {
            return additionalFiledToScoreFieldMapCSV;
        }

        public String getAdditionalPopulatedScoreFields() {
            return additionalPopulatedScoreFields;
        }
    }
}
