package fortscale.collection.jobs;

import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.ConfigurationService;
import fortscale.services.configuration.Impl.InitPartConfiguration;
import fortscale.utils.logging.Logger;
import org.apache.commons.lang.StringUtils;
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

	private ConfigurationParam dataSourceName;
	private ConfigurationParam dataSourceType;
	private ConfigurationParam dataFelds;
	private ConfigurationParam enrichFelds;
	private ConfigurationParam scoreFelds;
    private Map<String,ConfigurationParam> paramsMap = new LinkedHashMap<>();
	private Map<String,String> additionalFieldsMap;
	private Map<String,String> additionalScoreFieldsMap;

	private ConfigurationService initConfigurationService;




	//TODO - Generate this auto from the entities  properties
	private static final String BASE_SCHEMA_FIELDS_AS_CSV = "date_time TIMESTAMP,date_time_unix LONG,username STRING,normalized_username STRING,status STRING,${fortscale.tags.admin} BOOLEAN, ${fortscale.tags.executive} BOOLEAN,${fortscale.tags.service} BOOLEAN";
	private static final String DATA_ACCESS_SCHEMA_FIELDS_AS_CSV = "date_time TIMESTAMP,date_time_unix LONG,username STRING,normalized_username STRING,source_ip STRING,hostname STRING,src_class STRING,src_country STRING,src_longtitude STRING,src_latitude STRING,src_countryIsoCode STRING,src_region STRING,src_city STRING,src_isp STRING,src_usageType STRING,status STRING,${fortscale.tags.admin} BOOLEAN, ${fortscale.tags.executive} BOOLEAN,${fortscale.tags.service} BOOLEAN";
	private static final String SCORE_DATA_ACCESS_SCHEMA_FIELDS_AS_CSV = "date_time_score DOUBLE,event_score DOUBLE,source_machine_score DOUBLE,country_score DOUBLE";
	private static final String AUTH_SCHEMA_FIELDS_AS_CSV = "date_time TIMESTAMP,date_time_unix LONG,username STRING,normalized_username STRING,source_ip STRING,hostname STRING,src_class STRING,src_country STRING,src_longtitude STRING,src_latitude STRING,src_countryIsoCode STRING,src_region STRING,src_city STRING,src_isp STRING,src_usageType STRING,target_ip STRING,target_machine STRING,dst_class STRING,dst_country STRING,dst_longtitude STRING,dst_latitude STRING,dst_countryIsoCode STRING,dst_region STRING,dst_city STRING,dst_isp STRING,dst_usageType STRING,status STRING,${fortscale.tags.admin} BOOLEAN, ${fortscale.tags.executive} BOOLEAN,${fortscale.tags.service} BOOLEAN,${fortscale.tags.sensitive} BOOLEAN";
	private static final String SCORE_AUTH_SCHEMA_FIELDS_AS_CSV = "date_time_score DOUBLE,event_score DOUBLE,source_machine_score DOUBLE,country_score DOUBLE,destination_machine_score DOUBLE";
	private static final String CUSTOMED_AUTH_SCHEMA_FIELDS_AS_CSV = "date_time TIMESTAMP,date_time_unix LONG,username STRING,normalized_username STRING,source_ip STRING,hostname STRING,src_class STRING,src_country STRING,src_longtitude STRING,src_latitude STRING,src_countryIsoCode STRING,src_region STRING,src_city STRING,src_isp STRING,src_usageType STRING,target_ip STRING,target_machine STRING,dst_class STRING,dst_country STRING,dst_longtitude STRING,dst_latitude STRING,dst_countryIsoCode STRING,dst_region STRING,dst_city STRING,dst_isp STRING,dst_usageType STRING,action_type STRING,status STRING,${fortscale.tags.admin} BOOLEAN, ${fortscale.tags.executive} BOOLEAN,${fortscale.tags.service} BOOLEAN,${fortscale.tags.sensitive} BOOLEAN";
	private static final String SCORE_CUSTOMED_AUTH_SCHEMA_FIELDS_AS_CSV = "date_time_score DOUBLE,event_score DOUBLE,source_machine_score DOUBLE,country_score DOUBLE,destination_machine_score DOUBLE,action_type_score DOUBLE,data_bucket_score DOUBLE";








	@Override
    protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.debug("Initializing Configuration GDS Job");
		additionalFieldsMap = new LinkedHashMap<>();
		additionalScoreFieldsMap = new LinkedHashMap<>();


        logger.debug("Job Initialized");
    }

    @Override
    protected void runSteps() throws Exception {

        logger.debug("Running Configuration GDS Job");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Please enter the new data source name: ");
		String dataSourceNameString = br.readLine();
		this.dataSourceName = new ConfigurationParam("dataSourceName",false,dataSourceNameString);


		System.out.println("What is the %s data source type (base/access_event/auth_event/customized_auth_event): ");
		System.out.println("         base                    - user , time  ");
		System.out.println("         access_event            - user , time, source (resolving,geo location)  ");
		System.out.println("         auth_event              - user , time, source (resolving,geo location) , target (resolving,geo location)  ");
		System.out.println("         customized_auth_event   - user , time, source (resolving,geo location) , target (resolving,geo location), action , data usage ");
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

		Boolean executionResult=false;
		String additionalFieldsCSV="";
		String additionalScoreFieldsCSV="";
		String result = "";
		String line ="";

		paramsMap.put(this.dataSourceName.getParamName(),this.dataSourceName);
		paramsMap.put(this.dataSourceType.getParamName(),this.dataSourceType);


		//Additional Fields
		System.out.println("Does %s data source have additional fields (y/n)");
		result = br.readLine();
		if(result.toLowerCase().equals("y") || result.toLowerCase().equals("yes"))
		{
			additionalFieldsCSV=",";
			System.out.println("Please enter %s data source additional fields csv style  (i.e url STRING,application STRING  etc): ");
			additionalFieldsCSV += br.readLine();
			spilitCSVtoMap(additionalFieldsCSV,additionalFieldsMap);

			System.out.println("Does %s data source have additional score fields (y/n)");
			result = br.readLine();
			if(result.toLowerCase().equals("y") || result.toLowerCase().equals("yes")) {
				additionalScoreFieldsCSV=",";
				System.out.println("Please enter %s data source additional score fields csv style  (i.e url_score STRING,application_score STRING  etc): ");
				additionalScoreFieldsCSV += br.readLine();
				spilitCSVtoMap(additionalScoreFieldsCSV,additionalScoreFieldsMap);
			}
		}

		switch(dataSourceType.getParamValue())
		{
			case "base":
			{
				paramsMap.put("dataFields" ,new ConfigurationParam("dataFields",false,BASE_SCHEMA_FIELDS_AS_CSV+additionalFieldsCSV));
				paramsMap.put("enrichFelds",new ConfigurationParam("enrichFelds",false,BASE_SCHEMA_FIELDS_AS_CSV+additionalFieldsCSV));
				paramsMap.put("scoreFields",new ConfigurationParam("scoreFields",false,BASE_SCHEMA_FIELDS_AS_CSV+additionalFieldsCSV+additionalScoreFieldsCSV));
				paramsMap.put("sourceIpFlag",new ConfigurationParam("sourceIpFlag",false,""));
				paramsMap.put("targetIpFlag",new ConfigurationParam("targetIpFlag",false,""));
			}

			case "access_event":
			{
				paramsMap.put("dataFields" ,new ConfigurationParam("dataFields",false,DATA_ACCESS_SCHEMA_FIELDS_AS_CSV+additionalFieldsCSV));
				paramsMap.put("enrichFelds",new ConfigurationParam("enrichFelds",false,DATA_ACCESS_SCHEMA_FIELDS_AS_CSV+additionalFieldsCSV));
				paramsMap.put("scoreFields",new ConfigurationParam("scoreFields",false,SCORE_DATA_ACCESS_SCHEMA_FIELDS_AS_CSV+additionalFieldsCSV+additionalScoreFieldsCSV));
				paramsMap.put("sourceIpFlag",new ConfigurationParam("sourceIpFlag",true,""));

				System.out.println("Does %s source ip should be resolved (y/n)?");
				result = br.readLine();
				paramsMap.put("sourceIpResolvingFlag",new ConfigurationParam("ResolvingFlag",result.toLowerCase().equals("y") || result.toLowerCase().equals("yes"),""));

				System.out.println("Does %s source ip should be geo located (y/n)?");
				result = br.readLine();
				paramsMap.put("sourceIpGeoLocationFlag",new ConfigurationParam("GeoLocationFlag",result.toLowerCase().equals("y") || result.toLowerCase().equals("yes"),""));

				System.out.println("Does %s source machine name should be normalized (y/n)?");
				result = br.readLine();
				paramsMap.put("sourceMachineNormalizationFlag",new ConfigurationParam("MachineNormalizationFlag",result.toLowerCase().equals("y") || result.toLowerCase().equals("yes"),""));


				paramsMap.put("targetIpFlag",new ConfigurationParam("targetIpFlag",false,""));


			}
			case "auth_event":
			{
				paramsMap.put("dataFields", new ConfigurationParam("dataFields",false,AUTH_SCHEMA_FIELDS_AS_CSV+additionalFieldsCSV));
				paramsMap.put("enrichFelds",new ConfigurationParam("enrichFelds",false,AUTH_SCHEMA_FIELDS_AS_CSV+additionalFieldsCSV));
				paramsMap.put("scoreFields",new ConfigurationParam("scoreFields",false,SCORE_AUTH_SCHEMA_FIELDS_AS_CSV+additionalFieldsCSV+additionalScoreFieldsCSV));
				paramsMap.put("sourceIpFlag",new ConfigurationParam("sourceIpFlag",true,""));

				System.out.println("Does %s source ip should be resolved (y/n)?");
				result = br.readLine();
				paramsMap.put("sourceIpResolvingFlag",new ConfigurationParam("ResolvingFlag",result.toLowerCase().equals("y") || result.toLowerCase().equals("yes"),""));

				System.out.println("Does %s source ip should be geo located (y/n)?");
				result = br.readLine();
				paramsMap.put("sourceIpGeoLocationFlag",new ConfigurationParam("GeoLocationFlag",result.toLowerCase().equals("y") || result.toLowerCase().equals("yes"),""));

				System.out.println("Does %s source machine name should be normalized (y/n)?");
				result = br.readLine();
				paramsMap.put("sourceMachineNormalizationFlag",new ConfigurationParam("MachineNormalizationFlag",result.toLowerCase().equals("y") || result.toLowerCase().equals("yes"),""));


				paramsMap.put("targetIpFlag",new ConfigurationParam("targetIpFlag",true,""));

				System.out.println("Does %s target ip should be resolved (y/n)?");
				result = br.readLine();
				paramsMap.put("targetIpResolvingFlag",new ConfigurationParam("ResolvingFlag",result.toLowerCase().equals("y") || result.toLowerCase().equals("yes"),""));

				System.out.println("Does %s target ip should be geo located (y/n)?");
				result = br.readLine();
				paramsMap.put("targetIpGeoLocationFlag",new ConfigurationParam("GeoLocationFlag",result.toLowerCase().equals("y") || result.toLowerCase().equals("yes"),""));

				System.out.println("Does %s target machine name should be normalized (y/n)?");
				result = br.readLine();
				paramsMap.put("targetMachineNormalizationFlag",new ConfigurationParam("MachineNormalizationFlag",result.toLowerCase().equals("y") || result.toLowerCase().equals("yes"),""));



			}
			case "customized_auth_event" :
			{
				paramsMap.put("dataFields" ,new ConfigurationParam("dataFields",false,CUSTOMED_AUTH_SCHEMA_FIELDS_AS_CSV+additionalFieldsCSV));
				paramsMap.put("enrichFelds",new ConfigurationParam("enrichFelds",false,CUSTOMED_AUTH_SCHEMA_FIELDS_AS_CSV+additionalFieldsCSV));
				paramsMap.put("scoreFields",new ConfigurationParam("scoreFields",false,SCORE_CUSTOMED_AUTH_SCHEMA_FIELDS_AS_CSV+additionalFieldsCSV+additionalScoreFieldsCSV));
				paramsMap.put("sourceIpFlag",new ConfigurationParam("sourceIpFlag",true,""));

				System.out.println("Does %s source ip should be resolved (y/n)?");
				result = br.readLine();
				paramsMap.put("sourceIpResolvingFlag",new ConfigurationParam("ResolvingFlag",result.toLowerCase().equals("y") || result.toLowerCase().equals("yes"),""));

				System.out.println("Does %s source ip should be geo located (y/n)?");
				result = br.readLine();
				paramsMap.put("sourceIpGeoLocationFlag",new ConfigurationParam("GeoLocationFlag",result.toLowerCase().equals("y") || result.toLowerCase().equals("yes"),""));

				System.out.println("Does %s source machine name should be normalized (y/n)?");
				result = br.readLine();
				paramsMap.put("sourceMachineNormalizationFlag",new ConfigurationParam("MachineNormalizationFlag",result.toLowerCase().equals("y") || result.toLowerCase().equals("yes"),""));

				paramsMap.put("targetIpFlag",new ConfigurationParam("targetIpFlag",true,""));

				System.out.println("Does %s target ip should be resolved (y/n)?");
				result = br.readLine();
				paramsMap.put("targetIpResolvingFlag",new ConfigurationParam("ResolvingFlag",result.toLowerCase().equals("y") || result.toLowerCase().equals("yes"),""));

				System.out.println("Does %s target ip should be geo located (y/n)?");
				result = br.readLine();
				paramsMap.put("targetIpGeoLocationFlag",new ConfigurationParam("GeoLocationFlag",result.toLowerCase().equals("y") || result.toLowerCase().equals("yes"),""));

				System.out.println("Does %s target machine name should be normalized (y/n)?");
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
			initConfigurationService = new InitPartConfiguration(paramsMap);

			if (initConfigurationService.Init())
				executionResult = initConfigurationService.Configure();

			initConfigurationService.Done();

			return executionResult;


        }
        catch (Exception exception)
        {
            logger.error("There was an exception during execution - {} ",exception.getMessage());
			System.out.println(String.format("There was an exception during execution please see more info at the log "));
			return false;

        }

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


            //in case there is a target user to be normalize also
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

            paramsMap.put("lastState", new ConfigurationParam("lastState",false,"UsernameNormalizationAndTaggingTask"));

            System.out.println(String.format("End configure the Normalized Username and tagging task for %s", dataSourceName));






			//Ip Resolving task
			if (sourceIpResolvingFlag || targetIpResolvingFlag) {


				System.out.println(String.format("Going to configure the IP resolving task for %s", dataSourceName));

				//open the task properties file
				taskPropertiesFile = new File(configFilesPath + "ip-resolving-task.properties");
				taskPropertiesFileWriter = new FileWriter(taskPropertiesFile, true);

				taskPropertiesFileWriter.write("\n");
				taskPropertiesFileWriter.write("\n");

				configureIpResolving(taskPropertiesFileWriter,taskPropertiesFile,topolegyResult,br);

				System.out.println(String.format("End configure the IP resolving task for %s", dataSourceName));




			}


			//Computer tagging task
			if (sourceIpResolvingFlag || targetIpResolvingFlag) {

				System.out.println(String.format("Going to configure the Computer tagging and normalization task for %s", dataSourceName));

				//open the task properties file
				taskPropertiesFile = new File(configFilesPath + "computer-tagging-clustering-task.properties");
				taskPropertiesFileWriter = new FileWriter(taskPropertiesFile, true);

				taskPropertiesFileWriter.write("\n");
				taskPropertiesFileWriter.write("\n");

				configureComputerTaggingTask(taskPropertiesFileWriter,taskPropertiesFile,topolegyResult,br);

				System.out.println(String.format("End configure the Computer tagging and normalization task for %s", dataSourceName));



			}




			if(sourceGeoLocatedFlag || tartgetGeoLocatedFlag) {

				System.out.println(String.format("Going to configure the GeoLocation task for %s", dataSourceName));


				//open the task properties file
				taskPropertiesFile = new File(configFilesPath + "vpn-geolocation-session-update-task.properties");
				taskPropertiesFileWriter = new FileWriter(taskPropertiesFile, true);

				taskPropertiesFileWriter.write("\n");
				taskPropertiesFileWriter.write("\n");

				configureGeoLocationTask(taskPropertiesFileWriter,taskPropertiesFile, topolegyResult, br, sourceGeoLocatedFlag, tartgetGeoLocatedFlag);

				System.out.println(String.format("End configure the GeoLocation task for %s", dataSourceName));
			}


			//User Mongo update task
			System.out.println(String.format("Going to configure the UserMongoUpdate task for %s (i.e we use it for user last activity update) ", dataSourceName));


			//open the task properties file
			taskPropertiesFile = new File(configFilesPath + "user-mongo-update-task.properties");
			taskPropertiesFileWriter = new FileWriter(taskPropertiesFile, true);

			taskPropertiesFileWriter.write("\n");
			taskPropertiesFileWriter.write("\n");

			configureUserMongoUpdateTask(taskPropertiesFileWriter, taskPropertiesFile, topolegyResult, br);

			System.out.println(String.format("End configure the UserMongoUpdate task for %s", dataSourceName));



            //HDFS Write - for enrich
			System.out.println(String.format("Going to configure the HDFS write task for the enrich for %s (i.e we use it for user last activity update) ", dataSourceName));


			//open the task properties file
			taskPropertiesFile = new File(configFilesPath + "hdfs-events-writer-task.properties");
			taskPropertiesFileWriter = new FileWriter(taskPropertiesFile, true);

			taskPropertiesFileWriter.write("\n");
			taskPropertiesFileWriter.write("\n");

			configureHDFSWruteTask(taskPropertiesFileWriter, taskPropertiesFile, topolegyResult, br, "enrich");

			System.out.println(String.format("End configure the HDFS write task for %s", dataSourceName));


         
		}

		catch(Exception e)
		{
			logger.error("There was an exception during the execution - {}",e.getMessage());
			System.out.println(String.format("There was an exception during execution please see more info at the log "));
		}





	}


	private void writeLineToFile(String line, FileWriter writer, boolean withNewLine){
		try {
			writer.write(line);
			if (withNewLine)
				writer.write("\n");
		}

		catch (Exception e)
		{
			logger.error("There was an exception during the execution - {}",e.getMessage());
			System.out.println(String.format("There was an exception during execution please see more info at the log "));
		}
	}



	private void configureIpResolving(FileWriter taskPropertiesFileWriter,File taskPropertiesFile, Boolean topolegyResult,BufferedReader br){

		try {
			String line = "";
			line = String.format("# %s", this.dataSourceName);
			writeLineToFile(line, taskPropertiesFileWriter, true);

			// configure new configuration for the new dta source for source_ip



			System.out.println(String.format("Dose %s resolving is restricted to AD name (in case of true and the machine doesnt exist in the AD it will not return it as resolved value) (y/n) ?", dataSourceName));
			String brResult =br.readLine().toLowerCase();
			Boolean restrictToAD = brResult.equals("y") || brResult.equals("yes");

			System.out.println(String.format("Dose %s resolving use the machine short name (i.e SERV1@DOMAINBLABLA instead of SERV1@DOMAINBLABLA.com) (y/n) ?", dataSourceName));
			brResult =br.readLine().toLowerCase();
			Boolean shortNameUsage = brResult.equals("y") || brResult.equals("yes");

			System.out.println(String.format("Dose %s resolving need to remove last dot from the resolved server name  (i.e SERV1@DOMAINBLABLA instead of SERV1@DOMAINBLABLA.) (y/n) ?", dataSourceName));
			brResult =br.readLine().toLowerCase();
			Boolean removeLastDotUsage = brResult.equals("y") || brResult.equals("yes");

			System.out.println(String.format("Dose %s resolving need to drop in case of resolving fail (y/n) ?", dataSourceName));
			brResult =br.readLine().toLowerCase();
			Boolean dropOnFailUsage = brResult.equals("y") || brResult.equals("yes");

			System.out.println(String.format("Dose %s resolving need to override the source ip field with the resolving value (y/n) ?", dataSourceName));
			brResult =br.readLine().toLowerCase();
			Boolean overrideIpWithHostNameUsage = brResult.equals("y") || brResult.equals("yes");

			//source ip configuration for resolving the ip
			if (sourceIpResolvingFlag) {

				//in case of no target to resolve
				if (!targetIpResolvingFlag)
					configureTaskMandatoryConfiguration(taskPropertiesFileWriter,topolegyResult,"IpResolvingStreamTask_sourceIp",lastState,"fortscale-generic-data-access-ip-resolved");
				else
					configureTaskMandatoryConfiguration(taskPropertiesFileWriter,topolegyResult,"IpResolvingStreamTask_sourceIp",lastState,"fortscale-generic-data-access-source-ip-resolved");


				//partition field name  (today we use for all the username)
				line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_sourceIp.partition.field=${impala.data.%s.table.field.username}", this.dataSourceName, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//source ip field
				line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_sourceIp.ip.field=${impala.data.%s.table.field.source_ip}", this.dataSourceName, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//hostname
				line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_sourceIp.host.field=${impala.data.%s.table.field.source}", this.dataSourceName, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//time stamp
				line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_sourceIp.timestamp.field=${impala.data.%s.table.field.epochtime}", this.dataSourceName, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);


				//restric to AD

				line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_sourceIp.restrictToADName=%s", this.dataSourceName, restrictToAD.toString());
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//short name

				line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_sourceIp.shortName=%s", this.dataSourceName, shortNameUsage.toString());
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//Remove last Dot

				line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_sourceIp.isRemoveLastDot=%s", this.dataSourceName, removeLastDotUsage.toString());
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//Drop When Fail

				line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_sourceIp.dropWhenFail=%s", this.dataSourceName, dropOnFailUsage.toString());
				writeLineToFile(line, taskPropertiesFileWriter, true);


				//Override IP with Hostname

				line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_sourceIp.overrideIPWithHostname=%s", this.dataSourceName, overrideIpWithHostNameUsage.toString());
				writeLineToFile(line, taskPropertiesFileWriter, true);

				lastState="IpResolvingStreamTask_sourceIp";


			}

			//reslove also a target ip
			if (targetIpResolvingFlag) {

				writeLineToFile("", taskPropertiesFileWriter, true);

				configureTaskMandatoryConfiguration(taskPropertiesFileWriter,topolegyResult,"IpResolvingStreamTask_targetIp",lastState,"fortscale-generic-data-access-ip-resolved");


				//partition field name  (today we use for all the username)
				line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_targetIp.partition.field=${impala.data.%s.table.field.username}", this.dataSourceName, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//target ip field
				line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_targetIp.ip.field=${impala.data.%s.table.field.target_ip}", this.dataSourceName, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//target machine
				line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_targetIp.host.field=${impala.data.%s.table.field.target}", this.dataSourceName, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//time stamp
				line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_targetIp.timestamp.field=${impala.data.%s.table.field.epochtime}", this.dataSourceName, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);


				//restric to AD
				line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_targetIp.restrictToADName=%s", this.dataSourceName, restrictToAD.toString());
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//short name
				line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_targetIp.shortName=%s", this.dataSourceName, shortNameUsage.toString());
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//Remove last Dot
				line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_targetIp.isRemoveLastDot=%s", this.dataSourceName, removeLastDotUsage.toString());
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//Drop When Fail
				line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_targetIp.dropWhenFail=%s", this.dataSourceName, dropOnFailUsage.toString());
				writeLineToFile(line, taskPropertiesFileWriter, true);


				//Override IP with Hostname
				line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_targetIp.overrideIPWithHostname=%s", this.dataSourceName, overrideIpWithHostNameUsage.toString());
				writeLineToFile(line, taskPropertiesFileWriter, true);

				lastState="IpResolvingStreamTask";

			}

			//flush the writer for ip-resolving-task.properties
			taskPropertiesFileWriter.flush();
		}

		catch(Exception e)
		{
			logger.error("There was an exception during the execution - {}",e.getMessage());
			System.out.println(String.format("There was an exception during execution please see more info at the log "));
		}

		finally {
			try {
				taskPropertiesFileWriter.close();
			}
			catch (IOException exception)
			{

				logger.error("There was an exception during the file - {} closing  , cause - {} ",taskPropertiesFile.getName(),exception.getMessage());
				System.out.println(String.format("There was an exception during execution please see more info at the log "));

			}
		}

	}
	private void configureComputerTaggingTask(FileWriter taskPropertiesFileWriter,File taskPropertiesFile, Boolean topolegyResult,BufferedReader br){

		try {
			String line = "";
			line = String.format("# %s", this.dataSourceName);
			writeLineToFile(line, taskPropertiesFileWriter, true);

			if(sourceGeoLocatedFlag || tartgetGeoLocatedFlag)
				configureTaskMandatoryConfiguration(taskPropertiesFileWriter,topolegyResult,"ComputerTaggingClusteringTask",lastState,"fortscale-generic-data-access-computer-tagged-clustered_to_geo_location");
			else
				configureTaskMandatoryConfiguration(taskPropertiesFileWriter,topolegyResult,"ComputerTaggingClusteringTask",lastState,"fortscale-generic-data-access-computer-tagged-clustered");



			//partition field name  (today we use for all the username)
			line = String.format("fortscale.events.entry.%s_ComputerTaggingClusteringTask.partition.field=${impala.data.%s.table.field.username}", this.dataSourceName, this.dataSourceName);
			writeLineToFile(line, taskPropertiesFileWriter, true);


			//only in case there is a source ip that should be resolving
			if (sourceIpResolvingFlag) {
				//hostname
				line = String.format("fortscale.events.entry.%s_ComputerTaggingClusteringTask.source.hostname.field=${impala.data.%s.table.field.source}", this.dataSourceName, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//classification
				line = String.format("fortscale.events.entry.%s_ComputerTaggingClusteringTask.source.classification.field=${impala.data.%s.table.field.src_class}", this.dataSourceName, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//Normalized_src_machine (clustering)
				line = String.format("fortscale.events.entry.%s_ComputerTaggingClusteringTask.source.clustering.field=${impala.data.%s.table.field.normalized_src_machine}", this.dataSourceName, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);


				// configure new configuration for the new dta source for source_ip
				System.out.println(String.format("Dose %s source machine need to be added to the computer document in case he is missing (y/n) ?", dataSourceName));
				Boolean ensureComputerExist = br.readLine().toLowerCase().equals("y") || br.readLine().toLowerCase().equals("yes");
				line = String.format("fortscale.events.entry.%s_ComputerTaggingClusteringTask.source.create-new-computer-instances=%s", this.dataSourceName, ensureComputerExist.toString());
				writeLineToFile(line, taskPropertiesFileWriter, true);
			}

			if (targetIpResolvingFlag) {
				//hostname
				line = String.format("fortscale.events.entry.%s_ComputerTaggingClusteringTask.destination.hostname.field=${impala.data.%s.table.field.target}", this.dataSourceName, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//classification
				line = String.format("fortscale.events.entry.%s_ComputerTaggingClusteringTask.destination.classification.field=${impala.data.%s.table.field.dst_class}", this.dataSourceName, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//Normalized_src_machine (clustering)
				line = String.format("fortscale.events.entry.%s_ComputerTaggingClusteringTask.destination.clustering.field=${impala.data.%s.table.field.normalized_dst_machine}", this.dataSourceName, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);


				// configure new configuration for the new dta source for source_ip
				System.out.println(String.format("Dose %s target machine need to be added to the computer document in case he is missing (y/n) ?", dataSourceName));
				String brResult =br.readLine().toLowerCase();
				Boolean ensureComputerExist = brResult.equals("y") || brResult.equals("yes");

				line = String.format("fortscale.events.entry.%s_ComputerTaggingClusteringTask.destination.create-new-computer-instances=%s", this.dataSourceName, ensureComputerExist.toString());
				writeLineToFile(line, taskPropertiesFileWriter, true);


				// configure the is sensitive machine field
				line = String.format("fortscale.events.entry.%s_ComputerTaggingClusteringTask.destination.is-sensitive-machine.field=${impala.data.%s.table.field.is_sensitive_machine}", this.dataSourceName, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				lastState="ComputerTaggingClusteringTask";

			}

			//flush the writer for computer-tagging-clustering-task.properties
			taskPropertiesFileWriter.flush();
		}

		catch(Exception e){

			logger.error("There was an exception during the execution - {}",e.getMessage());
			System.out.println(String.format("There was an exception during execution please see more info at the log "));
		}

		finally {
			try {
				taskPropertiesFileWriter.close();
			}
			catch (IOException exception)
			{
				logger.error("There was an exception during the file - {} closing  , cause - {} ",taskPropertiesFile.getName(),exception.getMessage());
				System.out.println(String.format("There was an exception during execution please see more info at the log "));

			}
		}

	}
	private void configureGeoLocationTask(FileWriter taskPropertiesFileWriter,File taskPropertiesFile, Boolean topolegyResult,BufferedReader br,Boolean sourceGeoLocatedFlag,Boolean tartgetGeoLocatedFlag) {


		try {
			String line = "";
			line = String.format("# %s", this.dataSourceName);
			writeLineToFile(line, taskPropertiesFileWriter, true);




			if (sourceGeoLocatedFlag) {

				if (tartgetGeoLocatedFlag)
					configureTaskMandatoryConfiguration(taskPropertiesFileWriter,topolegyResult,"source_VpnEnrichTask",lastState,"fortscale-generic-data-access-source-ip-geolocated");
				else
					configureTaskMandatoryConfiguration(taskPropertiesFileWriter,topolegyResult,"source_VpnEnrichTask",lastState,"fortscale-generic-data-access-ip-geolocated");

				//source ip field
				line = String.format("%s.%s_source_VpnEnrichTask.ip.field=${impala.data.%s.table.field.source_ip}",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//country ip field
				line = String.format("%s.%s_source_VpnEnrichTask.country.field=src_country",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//longtitude  field
				line = String.format("%s.%s_source_VpnEnrichTask.longtitude.field=src_longtitude",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//latitude  field
				line = String.format("%s.%s_source_VpnEnrichTask.latitude.field=src_latitude",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//countryIsoCode field
				line = String.format("%s.%s_source_VpnEnrichTask.countryIsoCode.field=src_countryIsoCode",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//region  field
				line = String.format("%s.%s_source_VpnEnrichTask.region.field=src_region",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//city field
				line = String.format("%s.%s_source_VpnEnrichTask.city.field=src_city",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//isp field
				line = String.format("%s.%s_source_VpnEnrichTask.isp.field=isp",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//usageType field
				line = String.format("%s.%s_source_VpnEnrichTask.usageType.field=src_usageType",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//put session update configuration as false  field
				line = String.format("%s.%s_source_VpnEnrichTask.doSessionUpdate=false",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);


				//put data bucket as false field
				line = String.format("%s.%s_source_VpnEnrichTask.doDataBuckets=false",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//put geo location as true field
				line = String.format("%s.%s_source_VpnEnrichTask.doGeoLocation=true",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);


				//partition field name  (today we use for all the username)
				line = String.format("%s.%s_source_VpnEnrichTask.partition.field=${impala.data.%s.table.field.username}",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//username
				line = String.format("%s.%s_source_VpnEnrichTask.username.field=${impala.data.%s.table.field.username}",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);


				lastState="VpnEnrichTask";



			}

			if (tartgetGeoLocatedFlag) {

				configureTaskMandatoryConfiguration(taskPropertiesFileWriter,topolegyResult,"dest_VpnEnrichTask","VpnEnrichTask","fortscale-generic-data-access-target-ip-geolocated");

				//target ip field
				line = String.format("%s.%s_dest_VpnEnrichTask.ip.field=${impala.data.%s.table.field.target_ip}",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//country ip field
				line = String.format("%s.%s_dest_VpnEnrichTask.country.field=dest_country",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//longtitude  field
				line = String.format("%s.%s_dest_VpnEnrichTask.longtitude.field=dest_longtitude",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//latitude  field
				line = String.format("%s.%s_dest_VpnEnrichTask.latitude.field=dest_latitude",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//countryIsoCode field
				line = String.format("%s.%s_dest_VpnEnrichTask.countryIsoCode.field=dest_countryIsoCode",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//region  field
				line = String.format("%s.%s_dest_VpnEnrichTask.region.field=dest_region",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//city field
				line = String.format("%s.%s_dest_VpnEnrichTask.city.field=dest_city",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//isp field
				line = String.format("%s.%s_dest_VpnEnrichTask.isp.field=dest_isp",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//usageType field
				line = String.format("%s.%s_dest_VpnEnrichTask.usageType.field=dest_usageType",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//put session update configuration as false  field
				line = String.format("%s.%s_dest_VpnEnrichTask.doSessionUpdate=false",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);


				//put data bucket as false field
				line = String.format("%s.%s_dest_VpnEnrichTask.doDataBuckets=false",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//put geo location as true field
				line = String.format("%s.%s_dest_VpnEnrichTask.doGeoLocation=true",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);


				//partition field name  (today we use for all the username)
				line = String.format("%s.%s_dest_VpnEnrichTask.partition.field=${impala.data.%s.table.field.username}",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//username
				line = String.format("%s.%s_dest_VpnEnrichTask.username.field=${impala.data.%s.table.field.username}",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				lastState="VpnEnrichTask";




			}


			//flush the writer for vpn-geolocation-session-update-task.properties
			taskPropertiesFileWriter.flush();


		} catch (Exception e) {
			logger.error("There was an exception during the execution - {}",e.getMessage());
			System.out.println(String.format("There was an exception during execution please see more info at the log "));
		}

		finally {
			try {
				taskPropertiesFileWriter.close();
			}
			catch (IOException exception)
			{
				logger.error("There was an exception during the file - {} closing  , cause - {} ",taskPropertiesFile.getName(),exception.getMessage());

			}
		}
	}
	private void configureUserMongoUpdateTask(FileWriter taskPropertiesFileWriter,File taskPropertiesFile, Boolean topolegyResult,BufferedReader br) {

		try {
			String line = "";
			line = String.format("# %s", this.dataSourceName);
			writeLineToFile(line, taskPropertiesFileWriter, true);


			configureTaskMandatoryConfiguration(taskPropertiesFileWriter, topolegyResult, "UserMongoUpdateStreamTask", lastState, "");

			//classifier value
			line = String.format("%s.%s_UserMongoUpdateStreamTask.classifier=%s",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName, this.dataSourceName.toLowerCase());
			writeLineToFile(line, taskPropertiesFileWriter, true);

			//Status field value
			System.out.println(String.format("Do you want to update last activity for any raw that came and not only successed events (y/n)? "));
			String brResult =br.readLine().toLowerCase();
			Boolean anyRow = brResult.equals("y") || brResult.equals("yes");


			if (anyRow) {
				line = String.format("%s.%s_UserMongoUpdateStreamTask.success.field=#AnyRow#", FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);
				line = String.format("%s.%s_UserMongoUpdateStreamTask.success.value=#NotRelevant#",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);
			}
			else {
				//configure the field that represent the status
				System.out.println(String.format("Please enter the field that will hold the message status   (i.e status,failure_code):"));
				String statusFieldName =  br.readLine().toLowerCase();
				line = String.format("%s.%s_UserMongoUpdateStreamTask.success.field=%s", FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName,statusFieldName);
				writeLineToFile(line, taskPropertiesFileWriter, true);
				//SUCCESS  value
				System.out.println(String.format("Please enter value that mark event as successed (i.c Accepted for ssh or SUCCESS for vpn 0x0 for kerberos ) :"));
				String successValue = br.readLine();
				line = String.format("%s.%s_UserMongoUpdateStreamTask.success.value=%s",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName, successValue);
				writeLineToFile(line, taskPropertiesFileWriter, true);
			}


			//logusername
			line = String.format("%s.%s_UserMongoUpdateStreamTask.logusername.field=%s",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName, usernameFieldName);
			writeLineToFile(line, taskPropertiesFileWriter, true);

			//normalized_username
			line = String.format("%s.%s_UserMongoUpdateStreamTask.username.field=normalized_username",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName);
			writeLineToFile(line, taskPropertiesFileWriter, true);


			//TODO - NOT SURE THIS FILED IS NEEDED , NET TO VALIDATE AND IF NOT TO REMOVE IT
			line = String.format("%s.%s_UserMongoUpdateStreamTask.UserMongoUpdateStreamTask.updateOnly=false",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName);
			writeLineToFile(line, taskPropertiesFileWriter, true);

			//flush the writer for user-mongo-update-task.properties
			taskPropertiesFileWriter.flush();



		} catch (Exception e) {
			logger.error("There was an exception during the execution - {}",e.getMessage());
			System.out.println(String.format("There was an exception during execution please see more info at the log "));

		} finally {
			try {
				taskPropertiesFileWriter.close();
			} catch (IOException exception) {

				logger.error("There was an exception during the file - {} closing  , cause - {} ", taskPropertiesFile.getName(), exception.getMessage());

				System.out.println(String.format("There was an exception during execution please see more info at the log "));

			}
		}
	}
	private void configureHDFSWruteTask(FileWriter taskPropertiesFileWriter, File taskPropertiesFile, Boolean topolegyResult, BufferedReader br,String step){

		try {
			String line = "";
			line = String.format("# %s", this.dataSourceName);
			writeLineToFile(line, taskPropertiesFileWriter, true);


			configureTaskMandatoryConfiguration(taskPropertiesFileWriter, topolegyResult, "enriched_HDFSWriterStreamTask", lastState, "fortscale-generic-data-access-enriched-after-write");

			//bdp routing value
			line = String.format("%s.%s_enriched_HDFSWriterStreamTask.bdp.output.topics=",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName);
			writeLineToFile(line, taskPropertiesFileWriter, true);

			//time stamp field
			line = String.format("%s.%s_enriched_HDFSWriterStreamTask.timestamp.field=${impala.data.%s.table.field.epochtime}",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName,this.dataSourceName);
			writeLineToFile(line, taskPropertiesFileWriter, true);

			//normalized_username
			line = String.format("%s.%s_enriched_HDFSWriterStreamTask.username.field=normalized_username",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName);
			writeLineToFile(line, taskPropertiesFileWriter, true);

			//enrich fields
			line = String.format("%s.%s_enriched_HDFSWriterStreamTask.fields=${impala.enricheddata.%s.table.fields}",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName,this.dataSourceName);
			writeLineToFile(line, taskPropertiesFileWriter, true);

			//seperator fields
			line = String.format("%s.%s_enriched_HDFSWriterStreamTask.separator=${impala.enricheddata.%s.table.delimiter}",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName,this.dataSourceName);
			writeLineToFile(line, taskPropertiesFileWriter, true);

			//hdfs path
			line = String.format("%s.%s_enriched_HDFSWriterStreamTask.hdfs.root=${hdfs.user.enricheddata.%s.path}",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName,this.dataSourceName);
			writeLineToFile(line, taskPropertiesFileWriter, true);

			//file name
			line = String.format("%s.%s_enriched_HDFSWriterStreamTask.file.name=${hdfs.enricheddata.%s.file.name}",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName,this.dataSourceName);
			writeLineToFile(line, taskPropertiesFileWriter, true);

			//table name  fields
			line = String.format("%s.%s_enriched_HDFSWriterStreamTask.table.name=${impala.enricheddata.%s.table.name}",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName,this.dataSourceName);
			writeLineToFile(line, taskPropertiesFileWriter, true);

			//partition strategy fields
			line = String.format("%s.%s_enriched_HDFSWriterStreamTask.partition.strategy=${impala.enricheddata.%s.table.partition.type}",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName,this.dataSourceName);
			writeLineToFile(line, taskPropertiesFileWriter, true);

			//partition strategy fields
			line = String.format("%s.%s_enriched_HDFSWriterStreamTask.split.strategy=fortscale.utils.hdfs.split.DailyFileSplitStrategy",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName);
			writeLineToFile(line, taskPropertiesFileWriter, true);

			//discriminator
			System.out.println(String.format("Please enter the fields that be used as discriminators csv style (i.c resord_number,target_machine ) :"));
			String discriminatorsFields = br.readLine().toLowerCase();
			line = String.format("%s.%s_HDFSWriterStreamTask.discriminator.fields=%s",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName, discriminatorsFields);
			writeLineToFile(line, taskPropertiesFileWriter, true);

			//flush buffer size
			writeLineToFile("# Buffer no more than 10000 events before flushing to HDFS", taskPropertiesFileWriter, true);
			line = String.format("%s.%s_enriched_HDFSWriterStreamTask.events.flush.threshold=10000",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName);
			writeLineToFile(line, taskPropertiesFileWriter, true);



            //Key-Value store configuration

            line = String.format("stores.hdfs-write-%sEnrich.factory=org.apache.samza.storage.kv.KeyValueStorageEngineFactory", this.dataSourceName);
            writeLineToFile(line, taskPropertiesFileWriter, true);
            line = String.format("stores.hdfs-write-%sEnrich.changelog=kafka.hdfs-write-crmsfEnrich-changelog", this.dataSourceName);
            writeLineToFile(line, taskPropertiesFileWriter, true);
            line = String.format("stores.hdfs-write-%sEnrich.key.serde=string", this.dataSourceName);
            writeLineToFile(line, taskPropertiesFileWriter, true);
            line = String.format("stores.hdfs-write-%sEnrich.msg.serde=timebarrier", this.dataSourceName);
            writeLineToFile(line, taskPropertiesFileWriter, true);
            line = String.format("# This property is set to the number of key/value pairs that should be kept in this in-memory buffer, per task instance. The number cannot be greater than stores.*.object.cache.size.");
            writeLineToFile(line, taskPropertiesFileWriter, true);
            line = String.format("stores.hdfs-write-%sEnrich.write.batch.size=25", this.dataSourceName);
            writeLineToFile(line, taskPropertiesFileWriter, true);
            line = String.format("# This property determines the number of objects to keep in Samza's cache, per task instance. This same cache is also used for write buffering (see stores.*.write.batch.size). A value of 0 disables all caching and batching.");
            writeLineToFile(line, taskPropertiesFileWriter, true);
            line = String.format("stores.hdfs-write-%sEnrich.object.cache.size=100", this.dataSourceName);
            writeLineToFile(line, taskPropertiesFileWriter, true);
            line = String.format("# The size of LevelDB's block cache in bytes, per container. Note that this is an off-heap memory allocation, so the container's total memory use is the maximum JVM heap size plus the size of this cache.");
            writeLineToFile(line, taskPropertiesFileWriter, true);
            line = String.format("stores.hdfs-write-%sEnrich.container.cache.size.bytes=2000", this.dataSourceName);
            writeLineToFile(line, taskPropertiesFileWriter, true);
            line = String.format("# The amount of memory (in bytes) that LevelDB uses for buffering writes before they are written to disk.");
            writeLineToFile(line, taskPropertiesFileWriter, true);
            line = String.format("stores.hdfs-write-%sEnrich.container.write.buffer.size.bytes=1000", this.dataSourceName);
            writeLineToFile(line, taskPropertiesFileWriter, true);



			//flush the writer for hdfs write of enrich part
			taskPropertiesFileWriter.flush();

            lastState="enriched_HDFSWriterStreamTask";

		}
		catch (Exception e)
		{
			logger.error("There was an exception during the execution - {}",e.getMessage());
			System.out.println(String.format("There was an exception during execution please see more info at the log "));
		}

		finally {
			try {
				taskPropertiesFileWriter.close();
			} catch (IOException exception) {
				logger.error("There was an exception during the file - {} closing  , cause - {} ", taskPropertiesFile.getName(), exception.getMessage());

				System.out.println(String.format("There was an exception during execution please see more info at the log "));

			}
		}

	}

	private void configureTaskMandatoryConfiguration(FileWriter taskPropertiesFileWriter ,Boolean topolegyResult, String name,String lastState,String outputTopic){


	}

	private String validatedFieldExietInSchema(String fieldName,Map<String,String> fieldsSchema,String fields,String showMessage) throws Exception{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		boolean exist =  fieldsSchema.containsKey(fieldName);
		boolean result = false;

		while (!exist) {

			if (showMessage.equals("autoAddition")){
				System.out.println(String.format("The %s field does not exist on the data schema you inserted going to be added automatically  ", fieldName));
				fields += String.format(",%s STRING", fieldName);
				this.dataFelds.put(fieldName, "STRING");
				break;

			}
			else {
				System.out.println(String.format("The %s field does not exist on the data schema you inserted do you want to add it (y/n)?", fieldName));
				String brResult = br.readLine().toLowerCase();
				result = brResult.equals("y") || brResult.equals("yes");
				if (result) {
					this.dataFelds.put(fieldName, "STRING");
					break;
				}
				System.out.println(String.format(showMessage, fieldName));
				fieldName = br.readLine().toLowerCase();
				exist = fieldsSchema.containsKey(fieldName);
			}
		}

			return fieldName;



	}



	private String convertDataSchemaMapToCSVlist(Map<String, String> updatedDataSourceSchema){
		String result = "";

		for (Map.Entry<String,String> entry :updatedDataSourceSchema.entrySet())
		{
			result+=entry.getKey()+" "+entry.getValue()+",";

		}

		if (!StringUtils.isEmpty(result))
			result = result.substring(0,result.length()-1);

		return result;
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

*/


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
