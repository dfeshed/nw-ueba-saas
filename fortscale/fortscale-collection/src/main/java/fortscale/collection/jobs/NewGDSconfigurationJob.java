package fortscale.collection.jobs;

import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.ConfigurationService;
import fortscale.services.configuration.Impl.*;
import fortscale.utils.logging.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;


import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by idanp on 12/1/2015.
 */
public class NewGDSconfigurationJob extends FortscaleJob {

    private static Logger logger = Logger.getLogger(NewGDSconfigurationJob.class);

    @Value("${fortscale.data.source}")
    private String currentDataSources;

	private ConfigurationParam dataSourceNameParam;
	private ConfigurationParam dataSourceType;
	private ConfigurationParam dataFelds;
	private ConfigurationParam enrichFelds;
	private ConfigurationParam scoreFelds;
    private Map<String,ConfigurationParam> paramsMap = new LinkedHashMap<>();
	private Map<String,String> additionalFieldsMap;
	private Map<String,String> additionalScoreFieldsMap;
	private Boolean executionResult = true;
	private ConfigurationService initConfigurationService;
	private ConfigurationService userNormalizationTaskService;
	private ConfigurationService ipResolvingTaskService;
	private ConfigurationService computerTaggingTaskService;
	private ConfigurationService geoLocationTaskService;
	private ConfigurationService userMongoUpdateTaskService;
    private ConfigurationService hdfsTaskService;
	private String dataSourceName;


	//TODO - Generate this auto from the entities  properties
	private static final String BASE_SCHEMA_FIELDS_AS_CSV = "date_time TIMESTAMP,date_time_unix LONG,username STRING,normalized_username STRING,status STRING,isUserAdministrator BOOLEAN, isUserExecutive BOOLEAN,isUserServiceAccount BOOLEAN";
	private static final String DATA_ACCESS_SCHEMA_FIELDS_AS_CSV = "date_time TIMESTAMP,date_time_unix LONG,username STRING,normalized_username STRING,source_ip STRING,hostname STRING,src_class STRING,src_country STRING,src_longtitude STRING,src_latitude STRING,src_countryIsoCode STRING,src_region STRING,src_city STRING,src_isp STRING,src_usageType STRING,status STRING,isUserAdministrator BOOLEAN, isUserExecutive BOOLEAN,isUserServiceAccount BOOLEAN";
	private static final String SCORE_DATA_ACCESS_SCHEMA_FIELDS_AS_CSV = "date_time_score DOUBLE,event_score DOUBLE,source_machine_score DOUBLE,country_score DOUBLE";
	private static final String AUTH_SCHEMA_FIELDS_AS_CSV = "date_time TIMESTAMP,date_time_unix LONG,username STRING,normalized_username STRING,source_ip STRING,hostname STRING,src_class STRING,src_country STRING,src_longtitude STRING,src_latitude STRING,src_countryIsoCode STRING,src_region STRING,src_city STRING,src_isp STRING,src_usageType STRING,target_ip STRING,target_machine STRING,dst_class STRING,dst_country STRING,dst_longtitude STRING,dst_latitude STRING,dst_countryIsoCode STRING,dst_region STRING,dst_city STRING,dst_isp STRING,dst_usageType STRING,status STRING,isUserAdministrator BOOLEAN, isUserExecutive BOOLEAN,isUserServiceAccount BOOLEAN,is_sensitive_machine BOOLEAN";
	private static final String SCORE_AUTH_SCHEMA_FIELDS_AS_CSV = "date_time_score DOUBLE,event_score DOUBLE,source_machine_score DOUBLE,country_score DOUBLE,destination_machine_score DOUBLE";
	private static final String CUSTOMED_AUTH_SCHEMA_FIELDS_AS_CSV = "date_time TIMESTAMP,date_time_unix LONG,username STRING,normalized_username STRING,source_ip STRING,hostname STRING,src_class STRING,src_country STRING,src_longtitude STRING,src_latitude STRING,src_countryIsoCode STRING,src_region STRING,src_city STRING,src_isp STRING,src_usageType STRING,target_ip STRING,target_machine STRING,dst_class STRING,dst_country STRING,dst_longtitude STRING,dst_latitude STRING,dst_countryIsoCode STRING,dst_region STRING,dst_city STRING,dst_isp STRING,dst_usageType STRING,action_type STRING,status STRING,isUserAdministrator BOOLEAN, isUserExecutive BOOLEAN,isUserServiceAccount BOOLEAN,is_sensitive_machine BOOLEAN";
	private static final String SCORE_CUSTOMED_AUTH_SCHEMA_FIELDS_AS_CSV = "date_time_score DOUBLE,event_score DOUBLE,source_machine_score DOUBLE,country_score DOUBLE,destination_machine_score DOUBLE,action_type_score DOUBLE,data_bucket_score DOUBLE";








	@Override
    protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.debug("Initializing Configuration GDS Job");
		additionalFieldsMap = new LinkedHashMap<>();
		additionalScoreFieldsMap = new LinkedHashMap<>();
        ipResolvingTaskService = new IpResolvingTaskConfiguration();
        initConfigurationService = new InitPartConfiguration();
        userNormalizationTaskService = new UserNormalizationTaskConfiguration();
        computerTaggingTaskService = new ComputerTaggingClassConfiguration();
        geoLocationTaskService = new GeoLocationConfiguration();
        userMongoUpdateTaskService = new UserMongoUpdateConfiguration();
        hdfsTaskService = new HDFSWriteTaskConfiguration();


        logger.debug("Job Initialized");
    }

    @Override
    protected void runSteps() throws Exception {

        logger.debug("Running Configuration GDS Job");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Please enter the new data source name: ");
		String dataSourceNameString = br.readLine();
		this.dataSourceNameParam = new ConfigurationParam("dataSourceName",false,dataSourceNameString);
		dataSourceName = this.dataSourceNameParam.getParamValue();


		System.out.println(String.format("What is the %s data source type (base/access_event/auth_event/customized_auth_event): ",dataSourceName));
        System.out.println(String.format("* - meaning mandatory field ? -meaning optioinal field: ",dataSourceName));
		System.out.println("         base                    - user* , time*  ");
		System.out.println("         access_event            - user* , time*, source? (resolving,geo location)?  ");
		System.out.println("         auth_event              - user* , time*, source? (resolving,geo location)? , target? (resolving,geo location)?  ");
		System.out.println("         customized_auth_event   - user* , time*, source? (resolving,geo location)? , target? (resolving,geo location)?, action? , data usage? ");
		String dataSourceTypeString = br.readLine();
		this.dataSourceType = new ConfigurationParam("dataSourceType",false,dataSourceTypeString);


		System.out.println("Do you want to configure the schema part (y/n)?");
		String result = br.readLine();
		if (result.toLowerCase().equals("y") || result.toLowerCase().equals("yes"))
        	initPartConfiguration(br);

		System.out.println("Do you want to configure the collection jobs  (y/n)?");
		result = br.readLine();
		if (result.toLowerCase().equals("y") || result.toLowerCase().equals("yes"))
			collectionJobConfiguration(br);

		System.out.println("Do you want to configure the streaming part (y/n)?");
		result = br.readLine();
		if (result.toLowerCase().equals("y") || result.toLowerCase().equals("yes"))
			streamingConfiguration(br);




    }

    /**
     * This section will configure the Init configuration (The part that support the schema (HDFS paths and impala tables)
     * @param br - Will hold the scanner for tracing the user input
     */
    public Boolean initPartConfiguration(BufferedReader br) throws Exception {

		String additionalFieldsCSV="";
		String additionalScoreFieldsCSV="";
		String result = "";
		String line ="";

		paramsMap.put(this.dataSourceNameParam.getParamName(),this.dataSourceNameParam);
		paramsMap.put(this.dataSourceType.getParamName(),this.dataSourceType);
        paramsMap.put("dataSourceLists", new ConfigurationParam("dataSourceLists",false,currentDataSources));


		//Additional Fields
		System.out.println(String.format("Does %s data source have additional fields (y/n)",dataSourceName));
		result = br.readLine();
		if(result.toLowerCase().equals("y") || result.toLowerCase().equals("yes"))
		{
			additionalFieldsCSV=",";
			System.out.println(String.format("Please enter %s data source additional fields csv style  (i.e url STRING,application STRING  etc): ",dataSourceName));
			additionalFieldsCSV += br.readLine();
			//spilitCSVtoMap(additionalFieldsCSV,additionalFieldsMap);

			System.out.println(String.format("Does %s data source have additional score fields (y/n)",dataSourceName));
			result = br.readLine();
			if(result.toLowerCase().equals("y") || result.toLowerCase().equals("yes")) {
				additionalScoreFieldsCSV=",";
				System.out.println(String.format("Please enter %s data source additional score fields csv style  (i.e url_score STRING,application_score STRING  etc): ",dataSourceName));
				additionalScoreFieldsCSV += br.readLine();
				//spilitCSVtoMap(additionalScoreFieldsCSV,additionalScoreFieldsMap);
			}
		}

		switch(dataSourceType.getParamValue())
		{
			case "base":
			{
				paramsMap.put("dataFields" ,new ConfigurationParam("dataFields",false,BASE_SCHEMA_FIELDS_AS_CSV+additionalFieldsCSV));
				paramsMap.put("enrichFields",new ConfigurationParam("enrichFields",false,BASE_SCHEMA_FIELDS_AS_CSV+additionalFieldsCSV));
				paramsMap.put("scoreFields",new ConfigurationParam("scoreFields",false,BASE_SCHEMA_FIELDS_AS_CSV+additionalFieldsCSV+additionalScoreFieldsCSV));
				paramsMap.put("sourceIpFlag",new ConfigurationParam("sourceIpFlag",false,""));
				paramsMap.put("targetIpFlag",new ConfigurationParam("targetIpFlag",false,""));
			}

			case "access_event":
			{
				paramsMap.put("dataFields" ,new ConfigurationParam("dataFields",false,DATA_ACCESS_SCHEMA_FIELDS_AS_CSV+additionalFieldsCSV));
				paramsMap.put("enrichFields",new ConfigurationParam("enrichFields",false,DATA_ACCESS_SCHEMA_FIELDS_AS_CSV+additionalFieldsCSV));
				paramsMap.put("scoreFields",new ConfigurationParam("scoreFields",false,DATA_ACCESS_SCHEMA_FIELDS_AS_CSV+","+SCORE_DATA_ACCESS_SCHEMA_FIELDS_AS_CSV+additionalFieldsCSV+additionalScoreFieldsCSV));
				paramsMap.put("sourceIpFlag",new ConfigurationParam("sourceIpFlag",true,""));

				System.out.println(String.format("Does %s source ip should be resolved (y/n)?",dataSourceName));
				result = br.readLine();
				paramsMap.put("sourceIpResolvingFlag",new ConfigurationParam("ResolvingFlag",result.toLowerCase().equals("y") || result.toLowerCase().equals("yes"),""));

				System.out.println(String.format("Does %s source ip should be geo located (y/n)?",dataSourceName));
				result = br.readLine();
				paramsMap.put("sourceIpGeoLocationFlag",new ConfigurationParam("GeoLocationFlag",result.toLowerCase().equals("y") || result.toLowerCase().equals("yes"),""));

				System.out.println(String.format("Does %s source machine name should be normalized (y/n)?",dataSourceName));
				result = br.readLine();
				paramsMap.put("sourceMachineNormalizationFlag",new ConfigurationParam("MachineNormalizationFlag",result.toLowerCase().equals("y") || result.toLowerCase().equals("yes"),""));


				paramsMap.put("targetIpFlag",new ConfigurationParam("targetIpFlag",false,""));


			}
			case "auth_event":
			{
				paramsMap.put("dataFields", new ConfigurationParam("dataFields",false,AUTH_SCHEMA_FIELDS_AS_CSV+additionalFieldsCSV));
				paramsMap.put("enrichFields",new ConfigurationParam("enrichFields",false,AUTH_SCHEMA_FIELDS_AS_CSV+additionalFieldsCSV));
				paramsMap.put("scoreFields",new ConfigurationParam("scoreFields",false,AUTH_SCHEMA_FIELDS_AS_CSV+","+SCORE_AUTH_SCHEMA_FIELDS_AS_CSV+additionalFieldsCSV+additionalScoreFieldsCSV));
				paramsMap.put("sourceIpFlag",new ConfigurationParam("sourceIpFlag",true,""));

				System.out.println(String.format("Does %s source ip should be resolved (y/n)?",dataSourceName));
				result = br.readLine();
				paramsMap.put("sourceIpResolvingFlag",new ConfigurationParam("ResolvingFlag",result.toLowerCase().equals("y") || result.toLowerCase().equals("yes"),""));

				System.out.println(String.format("Does %s source ip should be geo located (y/n)?",dataSourceName));
				result = br.readLine();
				paramsMap.put("sourceIpGeoLocationFlag",new ConfigurationParam("GeoLocationFlag",result.toLowerCase().equals("y") || result.toLowerCase().equals("yes"),""));

				System.out.println(String.format("Does %s source machine name should be normalized (y/n)?",dataSourceName));
				result = br.readLine();
				paramsMap.put("sourceMachineNormalizationFlag",new ConfigurationParam("MachineNormalizationFlag",result.toLowerCase().equals("y") || result.toLowerCase().equals("yes"),""));


				paramsMap.put("targetIpFlag",new ConfigurationParam("targetIpFlag",true,""));

				System.out.println(String.format("Does %s target ip should be resolved (y/n)?",dataSourceName));
				result = br.readLine();
				paramsMap.put("targetIpResolvingFlag",new ConfigurationParam("ResolvingFlag",result.toLowerCase().equals("y") || result.toLowerCase().equals("yes"),""));

				System.out.println(String.format("Does %s target ip should be geo located (y/n)?",dataSourceName));
				result = br.readLine();
				paramsMap.put("targetIpGeoLocationFlag",new ConfigurationParam("GeoLocationFlag",result.toLowerCase().equals("y") || result.toLowerCase().equals("yes"),""));

				System.out.println(String.format("Does %s target machine name should be normalized (y/n)?",dataSourceName));
				result = br.readLine();
				paramsMap.put("targetMachineNormalizationFlag",new ConfigurationParam("MachineNormalizationFlag",result.toLowerCase().equals("y") || result.toLowerCase().equals("yes"),""));



			}
			case "customized_auth_event" :
			{
				paramsMap.put("dataFields" ,new ConfigurationParam("dataFields",false,CUSTOMED_AUTH_SCHEMA_FIELDS_AS_CSV+additionalFieldsCSV));
				paramsMap.put("enrichFields",new ConfigurationParam("enrichFields",false,CUSTOMED_AUTH_SCHEMA_FIELDS_AS_CSV+additionalFieldsCSV));
				paramsMap.put("scoreFields",new ConfigurationParam("scoreFields",false,CUSTOMED_AUTH_SCHEMA_FIELDS_AS_CSV+","+SCORE_CUSTOMED_AUTH_SCHEMA_FIELDS_AS_CSV+additionalFieldsCSV+additionalScoreFieldsCSV));
				paramsMap.put("sourceIpFlag",new ConfigurationParam("sourceIpFlag",true,""));

				System.out.println(String.format("Does %s source ip should be resolved (y/n)?",dataSourceName));
				result = br.readLine();
				paramsMap.put("sourceIpResolvingFlag",new ConfigurationParam("ResolvingFlag",result.toLowerCase().equals("y") || result.toLowerCase().equals("yes"),""));

				System.out.println(String.format("Does %s source ip should be geo located (y/n)?",dataSourceName));
				result = br.readLine();
				paramsMap.put("sourceIpGeoLocationFlag",new ConfigurationParam("GeoLocationFlag",result.toLowerCase().equals("y") || result.toLowerCase().equals("yes"),""));

				System.out.println(String.format("Does %s source machine name should be normalized (y/n)?",dataSourceName));
				result = br.readLine();
				paramsMap.put("sourceMachineNormalizationFlag",new ConfigurationParam("MachineNormalizationFlag",result.toLowerCase().equals("y") || result.toLowerCase().equals("yes"),""));

				paramsMap.put("targetIpFlag",new ConfigurationParam("targetIpFlag",true,""));

				System.out.println(String.format("Does %s target ip should be resolved (y/n)?",dataSourceName));
				result = br.readLine();
				paramsMap.put("targetIpResolvingFlag",new ConfigurationParam("ResolvingFlag",result.toLowerCase().equals("y") || result.toLowerCase().equals("yes"),""));

				System.out.println(String.format("Does %s target ip should be geo located (y/n)?",dataSourceName));
				result = br.readLine();
				paramsMap.put("targetIpGeoLocationFlag",new ConfigurationParam("GeoLocationFlag",result.toLowerCase().equals("y") || result.toLowerCase().equals("yes"),""));

				System.out.println(String.format("Does %s target machine name should be normalized (y/n)?",dataSourceName));
				result = br.readLine();
				paramsMap.put("targetMachineNormalizationFlag",new ConfigurationParam("MachineNormalizationFlag",result.toLowerCase().equals("y") || result.toLowerCase().equals("yes"),""));

			}
		}

        try{

			//delimiter
			System.out.println(String.format("Please enter the %s data schema delimiter  (i.e | or , )",dataSourceName));
			String delimiter = br.readLine();
			paramsMap.put("dataDelimiter", new ConfigurationParam("delimiter",false,delimiter));



			//table name
			String tableName = dataSourceName+"data";
			paramsMap.put("dataTableName", new ConfigurationParam("TableName",false,tableName));



			//sensitive_machine
			paramsMap.put("sensitive_machine", new ConfigurationParam("sensitive_machine",false,"${fortscale.tags.sensitive}"));


			//Enrich

			//delimiter
			paramsMap.put("enrichDelimiter", new ConfigurationParam("delimiter",false,delimiter));


			//table name
			tableName = dataSourceName+"enriched";
			paramsMap.put("enrichTableName", new ConfigurationParam("TableName",false,tableName));



			//Score

			//delimiter
			paramsMap.put("scoreDelimiter", new ConfigurationParam("delimiter",false,","));

			//table name
			tableName = dataSourceName+"score";
			paramsMap.put("scoreTableName", new ConfigurationParam("TableName",false,tableName));


			//top score
			System.out.println(String.format("Dose %s Have top table schema (y/n) ?",dataSourceName));
			String brResult =br.readLine().toLowerCase();
			paramsMap.put("topSchemaFlag", new ConfigurationParam("topSchemaFlaf",brResult.equals("y") || brResult.equals("yes"),""));



            //Service configuration


            initConfigurationService.setConfigurationParams(paramsMap);
			if (initConfigurationService.Init())
				executionResult = initConfigurationService.Configure();

			initConfigurationService.Done();
        }
        catch (Exception exception)
        {
            logger.error("There was an exception during execution - {} ",exception.getMessage());
			System.out.println(String.format("There was an exception during execution please see more info at the log "));
			executionResult = false;

        }

		return executionResult;

    }


	/**
	 * This method will configure the entire streaming configuration - Enrich , Single model/score, Aggregation
	 * @param br - Will hold the scanner for tracing the user input
	 */
	public void streamingConfiguration(BufferedReader br){

		Boolean result;
		try {


			System.out.println(String.format("Dose %s need to pass through enrich steps at the Streaming (y/n) ?", dataSourceName));
			result = br.readLine().toLowerCase().equals("y");

			if (result)
				//Enrich part
				enrichStereamingConfiguration(br);
		}

		catch (Exception e)
		{
			logger.error("There was an exception during the execution - {}",e.getMessage());
			System.out.println(String.format("There was an exception during execution please see more info at the log "));
		}



	}

	private void collectionJobConfiguration(BufferedReader br){

		String tempalteDirectoryPath;



	}

	/**
	 * This method will configure the entire enrich parts at the streaming
	 * @param br
	 * @param
	 */

	private void enrichStereamingConfiguration(BufferedReader br){

		String line="";


		try {

			// configure new Topic to the data source or use the GDS general topic
			System.out.println(String.format("Dose %s use the general GDS streaming topology   (y/n) ?",dataSourceName));
			String brResult =br.readLine().toLowerCase();
			Boolean topolegyResult = brResult.equals("y") || brResult.equals("yes");
			paramsMap.put("topologyFlag",new ConfigurationParam("topologyFlag",true,""));
			paramsMap.put("lastState", new ConfigurationParam("lastState",false,"etl"));
            paramsMap.put("taskName",new ConfigurationParam("taskName",false,"UsernameNormalizationAndTaggingTask"));

            System.out.println(String.format("Dose %s have target username to normalized (y/n) ?",dataSourceName));
            brResult =br.readLine().toLowerCase();
            Boolean targetNormalizationFlag = brResult.equals("y") || brResult.equals("yes");


            //in case there is a target user to be normalize also
            if(targetNormalizationFlag)
                paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-normalized-tagged-event_to_normalized_target_user"));
            else if (paramsMap.get("sourceIpResolvingFlag").getParamFlag() || paramsMap.get("targetIpResolvingFlag").getParamFlag())
                paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-normalized-tagged-event_to_ip_resolving"));
                //in case there is machine to normalized and tag
            else if (paramsMap.get("sourceMachineNormalizationFlag").getParamFlag() || paramsMap.get("targetMachineNormalizationFlag").getParamFlag())
                paramsMap.put("outPutTopic",new ConfigurationParam("outPutTopic",false,"fortscale-generic-data-access-normalized-tagged-even_to_computer_tagging"));
                //in case there is ip to geo locate
            else if (paramsMap.get("sourceIpGeoLocationFlag").getParamFlag() || paramsMap.get("targetIpGeoLocationFlag").getParamFlag())
                paramsMap.put("outPutTopic",new ConfigurationParam("outPutTopic",false,"fortscale-generic-data-access-normalized-tagged-event_to_geo_location"));
            else
                paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-normalized-tagged-event"));


            //User name field
            paramsMap.put("userNameField", new ConfigurationParam("userNameField",false,"username"));

            //Domain field  - for the enrich part
            paramsMap.put("domainFieldName", new ConfigurationParam("domainFieldName",false,"fake"));

            //In case of fake domain - enter the actual domain value the PS want
            paramsMap.put("domainValue", new ConfigurationParam("domainValue",false,""));

            //Normalized_username field
            paramsMap.put("normalizedUserNameField", new ConfigurationParam("normalizedUserNameField",false,"${impala.table.fields.normalized.username}"));



            //TODO - When we develope a new normalize service need to think what to do here cause now we have only ~2 kinds
            //Normalizing service
            System.out.println(String.format("Does the %s data source should contain users on the AD and you want to drop event of users that are not appeare there (i.e what we do for kerberos) (y/n):",this.dataSourceName));
            String updateOnlyResult = br.readLine().toLowerCase();
            Boolean updateOnly = updateOnlyResult.equals("y") ||updateOnlyResult.toLowerCase().equals("yes");

            if (updateOnly) {
                //Service  name
                paramsMap.put("normalizeSservieName", new ConfigurationParam("normalizeSservieName",false,"SecurityUsernameNormalizationService"));
                paramsMap.put("updateOnlyFlag", new ConfigurationParam("updateOnlyFlag",true,"true"));

            } else {
                paramsMap.put("normalizeSservieName", new ConfigurationParam("normalizeSservieName",false,"genericUsernameNormalizationService"));
                paramsMap.put("updateOnlyFlag", new ConfigurationParam("updateOnlyFlag",false,"false"));
            }

			//Service configuration

            userNormalizationTaskService.setConfigurationParams(paramsMap);
			executionResult = userNormalizationTaskService.Init();
			if (executionResult)
				executionResult = userNormalizationTaskService.Configure();


			//******bug configuration **********//
            //Configure the taarget user name normalization
            if(executionResult && targetNormalizationFlag)
            {

                if (paramsMap.get("sourceIpResolvingFlag").getParamFlag() || paramsMap.get("targetIpResolvingFlag").getParamFlag())
                    paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-normalized-tagged-event_to_ip_resolving"));
                    //in case there is machine to normalized and tag
                else if (paramsMap.get("sourceMachineNormalizationFlag").getParamFlag() || paramsMap.get("targetMachineNormalizationFlag").getParamFlag())
                    paramsMap.put("outPutTopic",new ConfigurationParam("outPutTopic",false,"fortscale-generic-data-access-normalized-tagged-even_to_computer_tagging"));
                    //in case there is ip to geo locate
                else if (paramsMap.get("sourceIpGeoLocationFlag").getParamFlag() || paramsMap.get("targetIpGeoLocationFlag").getParamFlag())
                    paramsMap.put("outPutTopic",new ConfigurationParam("outPutTopic",false,"fortscale-generic-data-access-normalized-tagged-event_to_geo_location"));
                else
                    paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-normalized-tagged-event"));

                paramsMap.put("lastState", new ConfigurationParam("lastState",false,"UsernameNormalizationAndTaggingTask"));
                paramsMap.put("taskName",new ConfigurationParam("taskName",false,"UsernameNormalizationAndTaggingTask_target"));


                System.out.println(String.format("Please enter the second username field to normalize :"));
                brResult =br.readLine().toLowerCase();

                paramsMap.put("userNameField", new ConfigurationParam("userNameField",false,brResult));

                //Domain field  - for the enrich part
                paramsMap.put("domainFieldName", new ConfigurationParam("domainFieldName",false,"fake"));

                //In case of fake domain - enter the actual domain value the PS want
                paramsMap.put("domainValue", new ConfigurationParam("domainValue",false,""));


                System.out.println(String.format("Please enter the field name of the field that will contain the second normalized user name :"));
                brResult =br.readLine().toLowerCase();
                //Normalized_username field
                paramsMap.put("normalizedUserNameField", new ConfigurationParam("normalizedUserNameField",false,brResult));

                userNormalizationTaskService.setConfigurationParams(paramsMap);
                executionResult = userNormalizationTaskService.Configure();


            }

            userNormalizationTaskService.Done();


			if (executionResult) {
				System.out.println(String.format("End configure the Normalized Username and tagging task for %s", dataSourceName));
				paramsMap.put("lastState", new ConfigurationParam("lastState", false, "UsernameNormalizationAndTaggingTask"));
			}



			//source Ip Resolving task
			if (paramsMap.get("sourceIpResolvingFlag").getParamFlag() && executionResult) {

				System.out.println(String.format("Going to configure the IP resolving task for %s", dataSourceName));

				System.out.println(String.format("Dose %s resolving is restricted to AD name (in case of true and the machine doesnt exist in the AD it will not return it as resolved value) (y/n) ?", dataSourceName));
				brResult = br.readLine().toLowerCase();
				Boolean restrictToAD = brResult.equals("y") || brResult.equals("yes");

				paramsMap.put("restrictToAD", new ConfigurationParam("restrictToAD", restrictToAD, ""));

				System.out.println(String.format("Dose %s resolving use the machine short name (i.e SERV1@DOMAINBLABLA instead of SERV1@DOMAINBLABLA.com) (y/n) ?", dataSourceName));
				brResult = br.readLine().toLowerCase();
				Boolean shortNameUsage = brResult.equals("y") || brResult.equals("yes");

				paramsMap.put("shortNameUsage", new ConfigurationParam("shortNameUsage", shortNameUsage, ""));

				System.out.println(String.format("Dose %s resolving need to remove last dot from the resolved server name  (i.e SERV1@DOMAINBLABLA instead of SERV1@DOMAINBLABLA.) (y/n) ?", dataSourceName));
				brResult = br.readLine().toLowerCase();
				Boolean removeLastDotUsage = brResult.equals("y") || brResult.equals("yes");

				paramsMap.put("removeLastDotUsage", new ConfigurationParam("removeLastDotUsage", removeLastDotUsage, ""));

				System.out.println(String.format("Dose %s resolving need to drop in case of resolving fail (y/n) ?", dataSourceName));
				brResult = br.readLine().toLowerCase();
				Boolean dropOnFailUsage = brResult.equals("y") || brResult.equals("yes");

				paramsMap.put("dropOnFailUsage", new ConfigurationParam("dropOnFailUsage", dropOnFailUsage, ""));

				System.out.println(String.format("Dose %s resolving need to override the source ip field with the resolving value (y/n) ?", dataSourceName));
				brResult = br.readLine().toLowerCase();
				Boolean overrideIpWithHostNameUsage = brResult.equals("y") || brResult.equals("yes");

				paramsMap.put("overrideIpWithHostNameUsage", new ConfigurationParam("overrideIpWithHostNameUsage", overrideIpWithHostNameUsage, ""));

				paramsMap.put("taskName", new ConfigurationParam("taskName", false, "IpResolvingStreamTask_sourceIp"));
				if (paramsMap.get("targetIpResolvingFlag").getParamFlag())
					paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-source-ip-resolved"));

				else
					paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-ip-resolved"));

				paramsMap.put("ipField", new ConfigurationParam("ipField", false, String.format("${impala.data.%s.table.field.source}",dataSourceName)));
				paramsMap.put("host", new ConfigurationParam("host", false, String.format("${impala.data.%s.table.field.source_name}",dataSourceName)));


				//Service configuration
				ipResolvingTaskService.setConfigurationParams(paramsMap);
				executionResult = ipResolvingTaskService.Init();
				if (executionResult)
					executionResult = ipResolvingTaskService.Configure();


				if (executionResult)
					paramsMap.put("lastState", new ConfigurationParam("lastState",false,"IpResolvingStreamTask"));


			}

			//target ip resolving
			if (paramsMap.get("targetIpResolvingFlag").getParamFlag() && executionResult) {

				paramsMap.put("taskName", new ConfigurationParam("taskName", false, "IpResolvingStreamTask_targetIp"));
				paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-ip-resolved"));
				paramsMap.put("ipField", new ConfigurationParam("ipField", false,  String.format("${impala.data.%s.table.field.target}",dataSourceName)));
				paramsMap.put("host", new ConfigurationParam("host", false,  String.format("${impala.data.%s.table.field.target_name}",dataSourceName)));

				if (ipResolvingTaskService.Init())
                {
                    ipResolvingTaskService.setConfigurationParams(paramsMap);
                    executionResult = ipResolvingTaskService.Configure();
                }



				paramsMap.put("lastState", new ConfigurationParam("lastState",false,"IpResolvingStreamTask"));


			}

            ipResolvingTaskService.Done();
			System.out.println(String.format("End configure the IP resolving task for %s", dataSourceName));


			//Computer tagging task
			if ((paramsMap.get("sourceMachineNormalizationFlag").getParamFlag() || paramsMap.get("targetMachineNormalizationFlag").getParamFlag()) && executionResult)
			{
				System.out.println(String.format("Going to configure the Computer tagging and normalization task for %s", dataSourceName));
				paramsMap.put("taskName", new ConfigurationParam("taskName", false, "ComputerTaggingClusteringTask"));

				if(paramsMap.get("sourceIpGeoLocationFlag").getParamFlag() || paramsMap.get("targetIpGeoLocationFlag").getParamFlag())
					paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-computer-tagged-clustered_to_geo_location"));
				else
					paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-computer-tagged-clustered"));

				// configure new configuration for the new dta source for source_ip
				System.out.println(String.format("Dose %s source machine need to be added to the computer document in case he is missing (y/n) ?", dataSourceName));
				Boolean ensureComputerExist = br.readLine().toLowerCase().equals("y") || br.readLine().toLowerCase().equals("yes");
				paramsMap.put("createNewComputerFlag",new ConfigurationParam("createNewComputerFlag",ensureComputerExist,""));
				paramsMap.put("srcMachineClassifier", new ConfigurationParam("srcMachineClassifier",false, String.format("${impala.data.%s.table.field.src_class}",dataSourceName)));
				paramsMap.put("srcHost", new ConfigurationParam("srcHost", false,  String.format("${impala.data.%s.table.field.source_name}",dataSourceName)));
				paramsMap.put("srcClusteringField", new ConfigurationParam("srcClusteringField",false, String.format("${impala.data.%s.table.field.normalized_src_machine}",dataSourceName)));
				paramsMap.put("dstMachineClassifier", new ConfigurationParam("dstMachineClassifier",false, String.format("${impala.data.%s.table.field.dst_class}",dataSourceName)));
				paramsMap.put("dstClusteringField", new ConfigurationParam("dstClusteringField",false, String.format("${impala.data.%s.table.field.normalized_dst_machine}",dataSourceName)));
				paramsMap.put("dstHost", new ConfigurationParam("dstHost", false,  String.format("${impala.data.%s.table.field.target_name}",dataSourceName)));

				//Service configuration
				computerTaggingTaskService.setConfigurationParams(paramsMap);
				executionResult = computerTaggingTaskService.Init();
				if (executionResult)
					executionResult = computerTaggingTaskService.Configure();


				if(executionResult) {
					paramsMap.put("lastState", new ConfigurationParam("lastState", false, "ComputerTaggingClusteringTask"));
					System.out.println(String.format("End configure the Computer Tagging task for %s", dataSourceName));
				}


                computerTaggingTaskService.Done();

			}

			//Source Geo Location
			if(paramsMap.get("sourceIpGeoLocationFlag").getParamFlag()  && executionResult) {

				System.out.println(String.format("Going to configure the source ip at GeoLocation task for %s", dataSourceName));
				paramsMap.put("taskName", new ConfigurationParam("taskName", false, "source_VpnEnrichTask"));

				if(paramsMap.get("targetIpGeoLocationFlag").getParamFlag())
					paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-source-ip-geolocated"));
				else
					paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-ip-geolocated"));

				paramsMap.put("ipField", new ConfigurationParam("ipField",false,"${impala.data.%s.table.field.source_ip}"));
				paramsMap.put("countryField", new ConfigurationParam("ipField",false,"src_country"));
				paramsMap.put("longtitudeField", new ConfigurationParam("ipField",false,"src_longtitudeField"));
				paramsMap.put("latitudeField", new ConfigurationParam("ipField",false,"src_latitudeField"));
				paramsMap.put("countryIsoCodeField", new ConfigurationParam("ipField",false,"src_countryIsoCodeField"));
				paramsMap.put("regionField", new ConfigurationParam("ipField",false,"src_regionField"));
				paramsMap.put("cityField", new ConfigurationParam("ipField",false,"src_cityField"));
				paramsMap.put("ispField", new ConfigurationParam("ipField",false,"src_ispField"));
				paramsMap.put("usageTypeField", new ConfigurationParam("ipField",false,"src_usageTypeField"));
				paramsMap.put("doSessionUpdateFlag", new ConfigurationParam("ipField",false,""));
				paramsMap.put("doDataBuckets", new ConfigurationParam("ipField",false,""));
				paramsMap.put("doGeoLocation", new ConfigurationParam("ipField",true,""));


				//Service configuration
				geoLocationTaskService.setConfigurationParams(paramsMap);
				executionResult = geoLocationTaskService.Init();
				if (executionResult)
					executionResult = geoLocationTaskService.Configure();


				if (executionResult)
				{
					paramsMap.put("lastState", new ConfigurationParam("lastState", false, "VpnEnrichTask"));
				}

			}

			//Target Geo Location
			if(paramsMap.get("targetIpGeoLocationFlag").getParamFlag()  && executionResult) {

				System.out.println(String.format("Going to configure the target ip at  GeoLocation task for %s", dataSourceName));
				paramsMap.put("taskName", new ConfigurationParam("taskName", false, "target_VpnEnrichTask"));


				paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-ip-geolocated"));

				paramsMap.put("ipField", new ConfigurationParam("ipField",false,"${impala.data.%s.table.field.target}"));
				paramsMap.put("countryField", new ConfigurationParam("countryField",false,"dst_country"));
				paramsMap.put("longtitudeField", new ConfigurationParam("longtitudeField",false,"dst_longtitudeField"));
				paramsMap.put("latitudeField", new ConfigurationParam("latitudeField",false,"dst_latitudeField"));
				paramsMap.put("countryIsoCodeField", new ConfigurationParam("countryIsoCodeField",false,"dst_countryIsoCodeField"));
				paramsMap.put("regionField", new ConfigurationParam("regionField",false,"dst_regionField"));
				paramsMap.put("cityField", new ConfigurationParam("cityField",false,"dst_cityField"));
				paramsMap.put("ispField", new ConfigurationParam("ispField",false,"dst_ispField"));
				paramsMap.put("usageTypeField", new ConfigurationParam("usageTypeField",false,"dst_usageTypeField"));
				paramsMap.put("doSessionUpdateFlag", new ConfigurationParam("doSessionUpdateFlag",false,""));
				paramsMap.put("doDataBuckets", new ConfigurationParam("doDataBuckets",false,""));
				paramsMap.put("doGeoLocation", new ConfigurationParam("doGeoLocation",true,""));

                geoLocationTaskService.setConfigurationParams(paramsMap);
				executionResult = geoLocationTaskService.Init();
				if (executionResult)
					executionResult = geoLocationTaskService.Configure();


				if (executionResult)
				{
					paramsMap.put("lastState", new ConfigurationParam("lastState", false, "VpnEnrichTask"));
				}

                geoLocationTaskService.Done();

			}
			System.out.println(String.format("End configure the GeoLocation task for %s", dataSourceName));


			//USER MONGO UPDATE
			if (executionResult) {


				//User Mongo update task
				System.out.println(String.format("Going to configure the UserMongoUpdate task for %s (i.e we use it for user last activity update) ", dataSourceName));

				paramsMap.put("taskName", new ConfigurationParam("taskName", false, String.format("UserMongoUpdateStreamTask",dataSourceName)));
                paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, ""));


				//Status field value
				System.out.println(String.format("Do you want to update last activity for any raw that came and not only successed events (y/n)? "));
				brResult =br.readLine().toLowerCase();
				Boolean anyRow = brResult.equals("y") || brResult.equals("yes");
				paramsMap.put("anyRow", new ConfigurationParam("anyRow", anyRow, ""));


				if (!anyRow) {

					//configure the field that represent the status
					//System.out.println(String.format("Please enter the field that will hold the message status   (i.e status,failure_code):"));
					//String statusFieldName =  br.readLine().toLowerCase();
					paramsMap.put("statusFieldName", new ConfigurationParam("statusFieldName", false, "status"));

					//SUCCESS  value
					System.out.println(String.format("Please enter value that mark event as successed (i.c Accepted for ssh or SUCCESS for vpn 0x0 for kerberos ) :"));
					String successValue = br.readLine();
					paramsMap.put("successValue", new ConfigurationParam("successValue", false, successValue));

				}


				userMongoUpdateTaskService.setConfigurationParams(paramsMap);
				executionResult = userMongoUpdateTaskService.Init();
				if (executionResult)
					executionResult = userMongoUpdateTaskService.Configure();
				userMongoUpdateTaskService.Done();
				System.out.println(String.format("End configure the UserMongoUpdate task for %s", dataSourceName));
			}

			//HDFS - WRITE
			if (executionResult) {


				//HDFS Write - for enrich
				System.out.println(String.format("Going to configure the HDFS write task for the enrich for %s  ", dataSourceName));

                paramsMap.put("taskName", new ConfigurationParam("taskName", false, String.format("enriched_HDFSWriterStreamTask")));


				paramsMap.put("outPutTopic", new ConfigurationParam("outPutTopic", false, "fortscale-generic-data-access-enriched-after-write"));
				paramsMap.put("fieldList", new ConfigurationParam("fieldList",false,String.format("${impala.enricheddata.%s.table.fields}",dataSourceName)));
				paramsMap.put("delimiter", new ConfigurationParam("delimiter",false,String.format("${impala.enricheddata.%s.table.delimiter}",dataSourceName)));
				paramsMap.put("tableName", new ConfigurationParam("tableName",false,String.format("${impala.enricheddata.%s.table.name}",dataSourceName)));
				paramsMap.put("hdfsPath", new ConfigurationParam("hdfsPath",false,String.format("${hdfs.user.enricheddata.%s.path}",dataSourceName)));
				paramsMap.put("fileName", new ConfigurationParam("fileName",false,String.format("${hdfs.enricheddata.%s.file.name}",dataSourceName)));
				paramsMap.put("partitionStrategy", new ConfigurationParam("partitionStrategy",false,String.format("${impala.enricheddata.%s.table.partition.type}",dataSourceName)));


				//todo -  add the anility to configure this param
				paramsMap.put("discriminatorsFields", new ConfigurationParam("discriminatorsFields",false,""));


				if (executionResult)
				{
					paramsMap.put("lastState", new ConfigurationParam("lastState", false, "enriched_HDFSWriterStreamTask"));

				}

               hdfsTaskService.setConfigurationParams(paramsMap);
                executionResult = hdfsTaskService.Init();
                if (executionResult)
                    executionResult = hdfsTaskService.Configure();
                hdfsTaskService.Done();

				System.out.println(String.format("End configure the HDFS write task for %s", dataSourceName));

			}


         
		}

		catch(Exception e)
		{
			logger.error("There was an exception during the execution - {}",e.getMessage()!= null ? e.getMessage() : e.getCause().getMessage());
			System.out.println(String.format("There was an exception during execution please see more info at the log "));
		}
	}

	private void spilitCSVtoMap(String fieldsCsv,Map<String,String> feldSchema) {

		String[] fieldsArray = fieldsCsv.split(",");
		for (String fieldDef : fieldsArray) {
			String[] fieldDefSep = fieldDef.split(" ");
			feldSchema.put(fieldDefSep[0], fieldDefSep[1]);
			//this.enrichFelds.put(fieldDefSep[0],fieldDefSep[1]);
			//this.scoreFelds.put(fieldDefSep[0],fieldDefSep[1]);
		}
	}

    @Override
    protected int getTotalNumOfSteps() { return 1; }

    @Override
    protected boolean shouldReportDataReceived() { return false; }



    public static void main(String[] args)  throws IOException {


        // loading spring application context, we do not close this context as the application continue to
        // run in background threads
        //ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:META-INF/spring/fortscale-global-config-context.xml");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        NewGDSconfigurationJob simulator = new NewGDSconfigurationJob();
       try {
		   simulator.runSteps();
	   }
	   catch(Exception e)
	   {
		   logger.error("There was an exception during the execution - {}",e.getMessage());
		   System.out.println(String.format("There was an exception during execution please see more info at the log "));
	   }


    }

}
