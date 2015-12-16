package fortscale.collection.jobs;

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

	private String dataSourceName;
	private Boolean sourceIpFlag;
	private Boolean targetIpFlag;
	private String dataFieldsCsv;
	private String enrichFieldsCsv;
	private String scoreFieldsCsv;
	private Boolean sourceIpResolvingFlag;
	private Boolean targetIpResolvingFlag;
	private Boolean sourceMachineNameFlag;
	private Boolean targetMachineNameFlag;
	private String usernameFieldName;
	private String lastState;
	private Boolean sourceGeoLocatedFlag;
	private Boolean tartgetGeoLocatedFlag;
	//private String root;

	private Map<String,String> dataFelds;
	private Map<String,String> enrichFelds;
	private Map<String,String> scoreFelds;

	private static final String FORTSCALE_CONFIGURATION_PREFIX  = "fortscale.events.entry.name";


    @Value("${fortscale.data.source}")
    private String currentDataSources;


    @Override
    protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.debug("Initializing Configuration GDS Job");

        logger.debug("Job Initialized");
    }

    @Override
    protected void runSteps() throws Exception {

        logger.debug("Running Configuration GDS Job");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Please enter the new data source name: ");
		this.dataSourceName = br.readLine();

		this.dataFelds = new LinkedHashMap<>();
		this.enrichFelds = new LinkedHashMap<>();
		this.scoreFelds = new LinkedHashMap<>();
		sourceGeoLocatedFlag=false;
		tartgetGeoLocatedFlag=false;
		sourceIpResolvingFlag=false;
		targetIpResolvingFlag=false;
		sourceMachineNameFlag=false;
		targetMachineNameFlag=false;
		sourceIpFlag=false;
		targetIpFlag=false;
		//root = System.getProperty("user.home");

        initPartConfiguration(br);
		streamingConfiguration(br);


    }

    /**
     * This section will configure the Init configuration (The part that support the schema (HDFS paths and impala tables)
     * @param br - Will hold the scanner for tracing the user input
     */
    public void initPartConfiguration(BufferedReader br){

        File file = new File("resources/fortscale-collection-overriding.properties");
        FileWriter fileWriter=null;
		File streamingOverridingFile = new File ("../../fortscale-streaming/config/fortscale-overriding-streaming.properties");
		FileWriter streamingOverridingfileWriter=null;
		String brResult="";
		String showMessage="";

        try{
            fileWriter = new FileWriter(file,true);
			streamingOverridingfileWriter=new FileWriter(streamingOverridingFile,true);

            Boolean result = false;
            System.out.println("Init Configuration - This part will responsible to the schema configuration (HDFS and Impala)");


            fileWriter.write("\n");
            fileWriter.write("\n");
			streamingOverridingfileWriter.write("\n");
			streamingOverridingfileWriter.write("\n");


			String line = String.format("########################################### New Configuration For Generic Data Source  ########################################################");
			writeLineToFile(line,fileWriter,true);
			writeLineToFile(line,streamingOverridingfileWriter,true);



			//Configure the data source list
			writeLineToFile(String.format("fortscale.data.source=%s,%s",currentDataSources,dataSourceName),fileWriter,true);



			line = String.format("########################################### %s ########################################################",dataSourceName);
			writeLineToFile(line,fileWriter,true);
			writeLineToFile(line,streamingOverridingfileWriter,true);





            System.out.println(String.format("Dose %s Have data schema (for ETL) (y/n) ?",dataSourceName));
			brResult =br.readLine().toLowerCase();
            result = brResult.equals("y") || brResult.equals("yes");

            //Data part
            if(result)
            {



				//fields
                System.out.println(String.format("Please enter the fields for %s data schema at csv style with data types  (i.e ussername STRING,target_score DOUBLE)",dataSourceName));
                dataFieldsCsv = br.readLine();

				//keep the field definition at map of <Field name,Data type> for validation

				spilitCSVtoMap(dataFieldsCsv,this.dataFelds);



                //write the username configuration at the streaming overriding for the enrich part
				showMessage = String.format("Please enter the \"username\" field name (i.e account_name or user_id ):");
                System.out.println(showMessage);
                usernameFieldName =  br.readLine().toLowerCase();

				//validation of the username field
				usernameFieldName = validatedFieldExietInSchema(usernameFieldName,this.dataFelds,dataFieldsCsv,showMessage);

                line=String.format("impala.data.%s.table.field.username=%s",dataSourceName,usernameFieldName);
                writeLineToFile(line,streamingOverridingfileWriter,true);
				writeLineToFile(line,fileWriter,true);



				//write the source_ip field configuration at the streaming overriding for the enrich part
				System.out.println(String.format("Does %s will have source ip field  (i.e spurce_ip or client_address ) (y/n)?",this.dataSourceName));
				brResult =br.readLine().toLowerCase();
				sourceIpFlag = brResult.equals("y") || brResult.equals("yes");





				if (sourceIpFlag) {
					showMessage = String.format("Please enter the \"source ip\" field name (i.e source_ip or client_address ):");
					System.out.println(showMessage);
					String sourceIpFieldName = br.readLine().toLowerCase();

					//validation of the SOURCE IP  field
					sourceIpFieldName = validatedFieldExietInSchema(sourceIpFieldName,this.dataFelds,dataFieldsCsv,showMessage);


					line = String.format("impala.data.%s.table.field.source_ip=%s", dataSourceName, sourceIpFieldName);
					writeLineToFile(line, streamingOverridingfileWriter, true);
					writeLineToFile(line,fileWriter,true);

					//Geo location task
					System.out.println(String.format("Does %s supposed to geo locate the source ip (y/n)?", dataSourceName));
					brResult =br.readLine().toLowerCase();
					sourceGeoLocatedFlag = brResult.equals("y") || brResult.equals("yes");


					System.out.println(String.format("Does %s will have source ip resolving (y/n)?",this.dataSourceName));
					brResult =br.readLine().toLowerCase();
					sourceIpResolvingFlag = brResult.equals("y") || brResult.equals("yes");


					if (!sourceIpResolvingFlag)
					{
						System.out.println(String.format("Does %s will have machine name from raw data (y/n)?",this.dataSourceName));
						brResult =br.readLine().toLowerCase();
						sourceMachineNameFlag = brResult.equals("y") || brResult.equals("yes");

					}


					if (sourceIpResolvingFlag || sourceMachineNameFlag) {

						//write the source_ip field configuration at the streaming overriding for the enrich part
						showMessage=String.format("Please enter the \"source machine\" field name that will contain the source ip resolving result or the machine name (i.e hostname ):");
						System.out.println(showMessage);
						String sourceMachineFieldName = br.readLine().toLowerCase();

						//validation of the SOURCE machine  field
						sourceMachineFieldName = validatedFieldExietInSchema(sourceMachineFieldName,this.dataFelds,dataFieldsCsv,showMessage);

						line = String.format("impala.data.%s.table.field.hostname=%s", dataSourceName, sourceMachineFieldName);
						writeLineToFile(line, streamingOverridingfileWriter, true);
						writeLineToFile(line,fileWriter,true);

						//write the source machien classification field configuration at the streaming overriding for the enrich part
						showMessage=String.format("Please enter the \"source machine\" class field name, this field will contain if this is a Desktop or Server   (i.e src_class ):");
						System.out.println(showMessage);
						String srClassFieldName = br.readLine().toLowerCase();

						//validation of the source machine class  field
						srClassFieldName = validatedFieldExietInSchema(srClassFieldName,this.dataFelds,dataFieldsCsv,showMessage);

						line = String.format("impala.data.%s.table.field.src_class=%s", dataSourceName, srClassFieldName);
						writeLineToFile(line, streamingOverridingfileWriter, true);
						writeLineToFile(line,fileWriter,true);

						//configure the normalized_src_machine
						//validation of the normalized_src_machine  field
						String normalizedSrcMachine = "normalized_src_machine";
						normalizedSrcMachine =validatedFieldExietInSchema(normalizedSrcMachine,this.dataFelds,dataFieldsCsv,"autoAddition");

						line = String.format("impala.data.%s.table.field.normalized_src_machine=%s", dataSourceName, normalizedSrcMachine);
						writeLineToFile(line, streamingOverridingfileWriter, true);
						writeLineToFile(line,fileWriter,true);
					}

				}



				//write the target_ip field configuration at the streaming overriding for the enrich part
				System.out.println(String.format("Does %s will have target ip field  (i.e target_ip  )(y/n)?",this.dataSourceName));
				brResult =br.readLine().toLowerCase();
				targetIpFlag = brResult.equals("y") || brResult.equals("yes");


				if (targetIpFlag) {
					showMessage=String.format("Please enter the \"target ip\" field name (i.e target_ip ):");
					System.out.println(showMessage);
					String targetIpFieldName = br.readLine().toLowerCase();

					//validation of the target ip  field
					targetIpFieldName = validatedFieldExietInSchema(targetIpFieldName,this.dataFelds,dataFieldsCsv,showMessage);

					line = String.format("impala.data.%s.table.field.target_ip=%s", dataSourceName, targetIpFieldName);
					writeLineToFile(line, streamingOverridingfileWriter, true);
					writeLineToFile(line,fileWriter,true);


					System.out.println(String.format("Does %s supposed to geo locate the target ip (y/n)?", dataSourceName));
					brResult =br.readLine().toLowerCase();
					tartgetGeoLocatedFlag = brResult.equals("y") || brResult.equals("yes");


					System.out.println(String.format("Does %s will have target ip resolving (y/n)?",this.dataSourceName));
					brResult =br.readLine().toLowerCase();
					targetIpResolvingFlag = brResult.equals("y") || brResult.equals("yes");


					if (!targetIpResolvingFlag)
					{
						System.out.println(String.format("Does %s will have machine name from raw data (y/n)?",this.dataSourceName));
						brResult =br.readLine().toLowerCase();
						targetMachineNameFlag = brResult.equals("y") || brResult.equals("yes");

					}


					if(targetIpResolvingFlag || targetMachineNameFlag) {

						//write the target_ip field configuration at the streaming overriding for the enrich part
						showMessage=String.format("Please enter the \"target machine\" field name that will contain the target ip resolving result in case that %s doesnt contain target ip keep this empty  (i.e target_machine ):", this.dataSourceName);
						System.out.println(showMessage);
						String targetMachineFieldName = br.readLine().toLowerCase();

						//validation of the target machine  field
						targetMachineFieldName = validatedFieldExietInSchema(targetMachineFieldName,this.dataFelds,dataFieldsCsv,showMessage);

						line = String.format("impala.data.%s.table.field.target_machine=%s", dataSourceName, targetMachineFieldName);
						writeLineToFile(line, streamingOverridingfileWriter, true);
						writeLineToFile(line,fileWriter,true);

						//write the dest machine classification field configuration at the streaming overriding for the enrich part
						showMessage=String.format("Please enter the \"target machine\" class field name, this field will contain if this is a Desktop or Server (i.e dst_class ):");
						System.out.println(showMessage);
						String dstClassFieldName = br.readLine().toLowerCase();

						//validation of the target machine class  field
						dstClassFieldName = validatedFieldExietInSchema(dstClassFieldName,this.dataFelds,dataFieldsCsv,showMessage);


						line = String.format("impala.data.%s.table.field.dst_class=%s", dataSourceName, dstClassFieldName);
						writeLineToFile(line, streamingOverridingfileWriter, true);
						writeLineToFile(line,fileWriter,true);


						//configure the normalized_dst_machine
						//validation of the normalized_dst_machine  field
						String normalizedDstMachine = "normalized_dst_machine";
						normalizedDstMachine =validatedFieldExietInSchema(normalizedDstMachine,this.dataFelds,dataFieldsCsv,"autoAddition");

						//configure the normalized_dst_machine
						line = String.format("impala.data.%s.table.field.normalized_dst_machine=%s", dataSourceName,normalizedDstMachine);
						writeLineToFile(line, streamingOverridingfileWriter, true);
						writeLineToFile(line,fileWriter,true);
					}
				}


				//write the time stamp field name
				showMessage=String.format("Please enter the \"time stamp field name \"  (i.e date_time_unix):");
				System.out.println(showMessage);
				String timestampFieldName =  br.readLine().toLowerCase();

				//validation of the target machine class  field
				timestampFieldName = validatedFieldExietInSchema(timestampFieldName,this.dataFelds,dataFieldsCsv,showMessage);

				line=String.format("impala.data.%s.table.field.epochtime=%s",dataSourceName,timestampFieldName);
				writeLineToFile(line,streamingOverridingfileWriter,true);
				writeLineToFile(line,fileWriter,true);


				line = String.format("########### Data Schema");
				writeLineToFile(line,fileWriter,true);

                //delimiter
                System.out.println(String.format("Please enter the %s data schema delimiter  (i.e | or , )",dataSourceName));
                String delimiter = br.readLine();
                line = String.format("impala.data.%s.table.delimiter=%s",dataSourceName,delimiter);
				writeLineToFile(line,fileWriter,true);
                writeLineToFile(line,streamingOverridingfileWriter,true);

                //table name
                System.out.println(String.format("Please enter the %s data table name  (i.e sshdata )",dataSourceName));
                String dataTableName = br.readLine();
                line = String.format("impala.data.%s.table.name=%s",dataSourceName,dataTableName);
				writeLineToFile(line,fileWriter,true);
                writeLineToFile(line,streamingOverridingfileWriter,true);


                //hdfs paths
                line = String.format("hdfs.user.data.%s.path=${hdfs.user.data.path}/%s",dataSourceName,dataSourceName);
				writeLineToFile(line,fileWriter,true);
                writeLineToFile(line,streamingOverridingfileWriter,true);


				//is sensitive machien field
				line = String.format("impala.data.%s.table.field.is_sensitive_machine=is_sensitive_machine",dataSourceName);

				//validation of the target machine class  field
				validatedFieldExietInSchema("is_sensitive_machine",this.dataFelds,dataFieldsCsv,"autoAddition");

				writeLineToFile(line,fileWriter,true);
				writeLineToFile(line,streamingOverridingfileWriter,true);


                //partition type
                line = String.format("impala.data.%s.table.partition.type=monthly",dataSourceName);
				writeLineToFile(line,fileWriter,true);
                writeLineToFile(line,streamingOverridingfileWriter,true);

				//align the field list csv
				dataFieldsCsv = alignTheFieldList(this.dataFelds);

				line=String.format("impala.data.%s.table.fields=%s",dataSourceName,dataFieldsCsv);
				writeLineToFile(line,fileWriter,true);
				writeLineToFile(line,streamingOverridingfileWriter,true);
            }

            System.out.println(String.format("Dose %s Have enrich schema (y/n)?",dataSourceName));
			brResult =br.readLine().toLowerCase();
			result = brResult.equals("y") || brResult.equals("yes");


            //Enrich  part
            if(result)
            {
				writeLineToFile("\n",fileWriter,true);
				writeLineToFile("\n",streamingOverridingfileWriter,true);

				line = String.format("########### Enrich Schema");
				writeLineToFile(line,fileWriter,true);
				writeLineToFile(line,streamingOverridingfileWriter,true);



				//fields
				this.enrichFieldsCsv = this.dataFieldsCsv;



                //System.out.println(String.format("Please enter addition fields for %s enrich schema csv style with data types no need to enter the %s data schema again   (i.e )",dataSourceName,dataSourceName));
                //String enrichAdditionFields = ""+br.readLine();

				//if (enrichAdditionFields.length()>0)
				//	this.enrichFieldsCsv+=String.format(",%s",enrichAdditionFields);

				spilitCSVtoMap(this.enrichFieldsCsv,this.enrichFelds);


				//Validate geo location fields if needed for source ip
				if(sourceGeoLocatedFlag)
				{
					validatedFieldExietInSchema("src_longtitude",this.enrichFelds,enrichFieldsCsv,"autoAddition");
					validatedFieldExietInSchema("src_latitude",this.enrichFelds,enrichFieldsCsv,"autoAddition");
					validatedFieldExietInSchema("src_countryIsoCode",this.enrichFelds,enrichFieldsCsv,"autoAddition");
					validatedFieldExietInSchema("src_region",this.enrichFelds,enrichFieldsCsv,"autoAddition");
					validatedFieldExietInSchema("src_city",this.enrichFelds,enrichFieldsCsv,"autoAddition");
					validatedFieldExietInSchema("src_isp",this.enrichFelds,enrichFieldsCsv,"autoAddition");
					validatedFieldExietInSchema("src_usageType",this.enrichFelds,enrichFieldsCsv,"autoAddition");


				}

				//Validate geo location fields if needed for target ip
				if(tartgetGeoLocatedFlag)
				{
					validatedFieldExietInSchema("dst_longtitude",this.enrichFelds,enrichFieldsCsv,"autoAddition");
					validatedFieldExietInSchema("dst_latitude",this.enrichFelds,enrichFieldsCsv,"autoAddition");
					validatedFieldExietInSchema("dst_countryIsoCode",this.enrichFelds,enrichFieldsCsv,"autoAddition");
					validatedFieldExietInSchema("dst_region",this.enrichFelds,enrichFieldsCsv,"autoAddition");
					validatedFieldExietInSchema("dst_city",this.enrichFelds,enrichFieldsCsv,"autoAddition");
					validatedFieldExietInSchema("dst_isp",this.enrichFelds,enrichFieldsCsv,"autoAddition");
					validatedFieldExietInSchema("dst_usageType",this.enrichFelds,enrichFieldsCsv,"autoAddition");


				}


				//align the field list csv
				this.enrichFieldsCsv = alignTheFieldList(this.enrichFelds);


				line = String.format("impala.enricheddata.%s.table.fields=%s",dataSourceName,this.enrichFieldsCsv);
				writeLineToFile(line,fileWriter,true);
				writeLineToFile(line,streamingOverridingfileWriter,true);

				//delimiter
                System.out.println(String.format("Please enter the %s enrich schema delimiter  (i.e | or , )",dataSourceName));
                String delimiter = br.readLine();
				line = String.format("impala.enricheddata.%s.table.delimiter=%s",dataSourceName,delimiter);
				writeLineToFile(line,fileWriter,true);
				writeLineToFile(line,streamingOverridingfileWriter,true);


				//table name
                System.out.println(String.format("Please enter the %s enrich table name  (i.e sshenriched )",dataSourceName));
                String tableName = br.readLine();
				line = String.format("impala.enricheddata.%s.table.name=%s",dataSourceName,tableName);
				writeLineToFile(line,fileWriter,true);
				writeLineToFile(line,streamingOverridingfileWriter,true);

				//hdfs path
				line = String.format("hdfs.user.enricheddata.%s.path=${hdfs.user.enricheddata.path}/%s",dataSourceName,dataSourceName);
				writeLineToFile(line,fileWriter,true);
				writeLineToFile(line,streamingOverridingfileWriter,true);

				//hdfs file name
				line = String.format("hdfs.enricheddata.%s.file.name=${impala.enricheddata.%s.table.name}.csv",dataSourceName,dataSourceName);
				writeLineToFile(line,fileWriter,true);
				writeLineToFile(line,streamingOverridingfileWriter,true);



				//partition strategy
				line = String.format("impala.enricheddata.%s.table.partition.type=daily",dataSourceName);
				writeLineToFile(line,fileWriter,true);
				writeLineToFile(line,streamingOverridingfileWriter,true);

            }

			line = String.format("########### Score Schema");
			writeLineToFile(line,fileWriter,true);
			writeLineToFile(line,streamingOverridingfileWriter,true);


			this.scoreFieldsCsv = this.enrichFieldsCsv;

            //Score part
            System.out.println(String.format("Please enter the score fields for %s the score schema in csv style with data types  (i.e date_time_score DOUBLE,target_score DOUBLE)",dataSourceName));
            String fields = br.readLine();

			if (fields.length()>0)
				this.scoreFieldsCsv+=String.format(",%s",fields);



			//fields
			line = String.format("impala.score.%s.table.fields=%s",dataSourceName,this.scoreFieldsCsv);
			writeLineToFile(line,fileWriter,true);
			writeLineToFile(line,streamingOverridingfileWriter,true);


			//delimiter
            System.out.println(String.format("Please enter the %s score schema delimiter  (i.e | or , )",dataSourceName));
            String delimiter = br.readLine();
			line = String.format("impala.score.%s.table.delimiter=%s",dataSourceName,delimiter);
			writeLineToFile(line,fileWriter,true);
			writeLineToFile(line,streamingOverridingfileWriter,true);

			//table name
            System.out.println(String.format("Please enter the %s score table name  (i.e sshscores )",dataSourceName));
            String tableName = br.readLine();
			line = String.format("impala.score.%s.table.name=%s",dataSourceName,tableName);
			writeLineToFile(line,fileWriter,true);
			writeLineToFile(line,streamingOverridingfileWriter,true);

			//hdfs path
			line = String.format("hdfs.user.processeddata.%s.path=${hdfs.user.processeddata.path}/%s",dataSourceName,dataSourceName);
			writeLineToFile(line,fileWriter,true);
			writeLineToFile(line,streamingOverridingfileWriter,true);


			//partition strategy
			line=String.format("impala.score.%s.table.partition.type=daily",dataSourceName);
			writeLineToFile(line,fileWriter,true);
			writeLineToFile(line,streamingOverridingfileWriter,true);



            System.out.println(String.format("Dose %s Have top table schema (y/n) ?",dataSourceName));
			brResult =br.readLine().toLowerCase();
			result = brResult.equals("y") || brResult.equals("yes");


            if(result) {
                //Top score part

				line = String.format("########### Top Score Schema",dataSourceName);
				writeLineToFile(line,fileWriter,true);
				writeLineToFile(line,streamingOverridingfileWriter,true);


				//fields
				line = String.format("impala.score.%s.top.table.fields=%s",dataSourceName,this.scoreFieldsCsv);
				writeLineToFile(line,fileWriter,true);
				writeLineToFile(line,streamingOverridingfileWriter,true);


				//delimiter
				line = String.format("impala.score.%s.top.table.delimiter=%s",dataSourceName,delimiter);
				writeLineToFile(line,fileWriter,true);
				writeLineToFile(line,streamingOverridingfileWriter,true);


				//table name
				line = String.format("impala.score.%s.top.table.name=%s",dataSourceName,tableName);
				writeLineToFile(line,fileWriter,true);
				writeLineToFile(line,streamingOverridingfileWriter,true);


				//hdfs path
				line=String.format("hdfs.user.processeddata.%s.top.path=${hdfs.user.processeddata.path}/%s",dataSourceName,dataSourceName);
				writeLineToFile(line,fileWriter,true);
				writeLineToFile(line,streamingOverridingfileWriter,true);
				
				//partition startegy
				line=String.format("impala.score.%s.top.table.partition.type=daily",dataSourceName);
				writeLineToFile(line,fileWriter,true);
				writeLineToFile(line,streamingOverridingfileWriter,true);


            }





            fileWriter.flush();
			streamingOverridingfileWriter.flush();

        }
        catch (Exception exception)
        {
            logger.error("There was an exception during execution - {} ",exception.getCause().getMessage());
			System.out.println(String.format("There was an exception during execution please see more info at the log "));

        }

        finally {
            if (fileWriter != null)
            {
               try {
                   fileWriter.close();
               }
               catch (IOException exception)
               {
                   logger.error("There was an exception during the file - {} closing  , cause - {} ",file.getName(),exception.getCause().getMessage());
				   System.out.println(String.format("There was an exception during execution please see more info at the log "));

               }

            }

			if (streamingOverridingfileWriter != null)
			{
				try {
					streamingOverridingfileWriter.close();
				}
				catch (IOException exception)
				{
					logger.error("There was an exception during the file - {} closing  , cause - {} ",streamingOverridingFile.getName(),exception.getCause().getMessage());
					System.out.println(String.format("There was an exception during execution please see more info at the log "));

				}

			}
        }



    }


	/**
	 * This method will configure the entire streaming configuration - Enrich , Single model/score, Aggregation
	 * @param br - Will hold the scanner for tracing the user input
	 */
	public void streamingConfiguration(BufferedReader br)
	{

		String configFilesPath = "../../fortscale-streaming/config/";
		Boolean result = false;
		lastState="etl";

		try {


			System.out.println(String.format("Dose %s need to pass through enrich steps at the Streaming (y/n) ?", dataSourceName));
			result = br.readLine().toLowerCase().equals("y");

			if (result)
				//Enrich part
				enrichStereamingConfiguration(br, configFilesPath);
		}

		catch (Exception e)
		{
			logger.error("There was an exception during the execution - {}",e.getCause().getMessage());
			System.out.println(String.format("There was an exception during execution please see more info at the log "));
		}



	}


	private void enrichStereamingConfiguration(BufferedReader br,String configFilesPath)
	{


		String line="";
		//Open the streaming overriding file
		File streamingOverridingFile = new File (configFilesPath + "fortscale-overriding-streaming.properties");
		FileWriter streamingOverridingFileWriter = null;


		File taskPropertiesFile = null;
		FileWriter taskPropertiesFileWriter=null;
		Boolean result = false;


		try {

			// configure new Topic to the data source or use the GDS general topic
			System.out.println(String.format("Dose %s use the general GDS streaming topology   (y/n) ?",dataSourceName));
			String brResult =br.readLine().toLowerCase();
			Boolean topolegyResult = brResult.equals("y") || brResult.equals("yes");


			streamingOverridingFileWriter =  new FileWriter(streamingOverridingFile, true);


			//Normalized User Name and user Tagging task

			System.out.println(String.format("Going to configure the Normalized Username and tagging task for %s",dataSourceName));

			//open the task properties file
			taskPropertiesFile = new File(configFilesPath + "username-normalization-tagging-task.properties");
			taskPropertiesFileWriter = new FileWriter(taskPropertiesFile, true);

			taskPropertiesFileWriter.write("\n");
			taskPropertiesFileWriter.write("\n");

			configureNormalizeUserNameTask(taskPropertiesFileWriter,taskPropertiesFile,topolegyResult,br);

			System.out.println(String.format("End configure the Normalized Username and tagging task for %s", dataSourceName));

			lastState="UsernameNormalizationAndTaggingTask";




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
			logger.error("There was an exception during the execution - {}",e.getCause().getMessage());
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
			logger.error("There was an exception during the execution - {}",e.getCause().getMessage());
			System.out.println(String.format("There was an exception during execution please see more info at the log "));
		}
	}

	private void configureNormalizeUserNameTask(FileWriter taskPropertiesFileWriter,File taskPropertiesFile, Boolean topolegyResult,BufferedReader br){

		try {
			String line = "";
			line = String.format("# %s", this.dataSourceName);
			writeLineToFile(line, taskPropertiesFileWriter, true);


			if (sourceIpResolvingFlag || targetIpResolvingFlag)
				configureTaskMandatoryConfiguration(taskPropertiesFileWriter,topolegyResult,"UsernameNormalizationAndTaggingTask",lastState,"fortscale-generic-data-access-normalized-tagged-event_to_ip_resolving");
			else if (sourceMachineNameFlag || targetMachineNameFlag)
				configureTaskMandatoryConfiguration(taskPropertiesFileWriter,topolegyResult,"UsernameNormalizationAndTaggingTask",lastState,"fortscale-generic-data-access-normalized-tagged-even_to_computer_tagging");
			else if (sourceGeoLocatedFlag || tartgetGeoLocatedFlag)
				configureTaskMandatoryConfiguration(taskPropertiesFileWriter,topolegyResult,"UsernameNormalizationAndTaggingTask",lastState,"fortscale-generic-data-access-normalized-tagged-event_to_geo_location");
			else
				configureTaskMandatoryConfiguration(taskPropertiesFileWriter,topolegyResult,"UsernameNormalizationAndTaggingTask",lastState,"fortscale-generic-data-access-normalized-tagged-event");




			//User name field
			line = String.format("fortscale.events.entry.%s_UsernameNormalizationAndTaggingTask.username.field=%s", this.dataSourceName, usernameFieldName);
			writeLineToFile(line, taskPropertiesFileWriter, true);

			//Domain field  - for the enrich part
			System.out.println(String.format("Please enter the Domain field name (i.e account_domain) in case %s data source doesn't have domain field please enter \"fake\":", dataSourceName));
			String domainFieldName = br.readLine().toLowerCase();
			line = String.format("fortscale.events.entry.%s_UsernameNormalizationAndTaggingTask.domain.field=%s", dataSourceName, domainFieldName);
			writeLineToFile(line, taskPropertiesFileWriter, true);

			//In case of fake domain - enter the actual domain value the PS want
			System.out.println(String.format("If you chose a \"fake\" domain please enter the fix domain value for using (i.e vpnConnect,sshConnect or empty valuefor keeping the name without domain): "));
			String domainValue = br.readLine();
			line = String.format("fortscale.events.entry.%s_UsernameNormalizationAndTaggingTask.domain.fake=%s", dataSourceName, domainValue);
			writeLineToFile(line, taskPropertiesFileWriter, true);

			//Normalized_username field
			line = String.format("fortscale.events.entry.%s_UsernameNormalizationAndTaggingTask.normalizedusername.field=${impala.table.fields.normalized.username}");
			writeLineToFile(line, taskPropertiesFileWriter, true);

			//partition field name  (today we use for all the username)
			line = String.format("fortscale.events.entry.%s_UsernameNormalizationAndTaggingTask.partition.field=%s", this.dataSourceName, usernameFieldName);
			writeLineToFile(line, taskPropertiesFileWriter, true);


			//TODO - When we develope a new normalize service need to think what to do here cause now we have only ~2 kinds
			//Normalizing service
			System.out.println(String.format("Does the %s data source should contain users on the AD and you want to drop event of users that are not appeare there (i.e what we do for kerberos) (y/n):"));
			Boolean updateOnly = br.readLine().toLowerCase().equals("y") || br.readLine().toLowerCase().equals("yes");

			if (updateOnly) {
				line = String.format("fortscale.events.entry.%S_UsernameNormalizationAndTaggingTask.normalization.service=SecurityUsernameNormalizationService", this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);
				line = String.format("fortscale.events.entry.%s_UsernameNormalizationAndTaggingTask.updateOnly=true", this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);
			} else {

				line = String.format("fortscale.events.entry.%S_UsernameNormalizationAndTaggingTask.normalization.service=genericUsernameNormalizationService", this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);
				line = String.format("fortscale.events.entry.%s_UsernameNormalizationAndTaggingTask.updateOnly=false", this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);
			}


			//classifier value
			line = String.format("fortscale.events.entry.%s_UsernameNormalizationAndTaggingTask.classifier=%s", this.dataSourceName, this.dataSourceName);
			writeLineToFile(line, taskPropertiesFileWriter, true);

			writeLineToFile("\n", taskPropertiesFileWriter, true);
			writeLineToFile("#############", taskPropertiesFileWriter, true);


			//flush the writer for username-normalization-tagging-task.properties
			taskPropertiesFileWriter.flush();
		}
		catch (Exception e)
		{
			logger.error("There was an exception during the execution - {}",e.getCause().getMessage());
			System.out.println(String.format("There was an exception during execution please see more info at the log "));
		}

		finally {
			try {
				taskPropertiesFileWriter.close();
			}
			catch (IOException exception)
			{
				logger.error("There was an exception during the file - {} closing  , cause - {} ",taskPropertiesFile.getName(),exception.getCause().getMessage());
				System.out.println(String.format("There was an exception during execution please see more info at the log "));

			}
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
				line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_sourceIp.host.field=${impala.data.%s.table.field.hostname}", this.dataSourceName, this.dataSourceName);
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
				line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_targetIp.host.field=${impala.data.%s.table.field.target_machine}", this.dataSourceName, this.dataSourceName);
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
			logger.error("There was an exception during the execution - {}",e.getCause().getMessage());
			System.out.println(String.format("There was an exception during execution please see more info at the log "));
		}

		finally {
			try {
				taskPropertiesFileWriter.close();
			}
			catch (IOException exception)
			{

				logger.error("There was an exception during the file - {} closing  , cause - {} ",taskPropertiesFile.getName(),exception.getCause().getMessage());
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
				line = String.format("fortscale.events.entry.%s_ComputerTaggingClusteringTask.source.hostname.field=${impala.data.%s.table.field.hostname}", this.dataSourceName, this.dataSourceName);
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
				line = String.format("fortscale.events.entry.%s_ComputerTaggingClusteringTask.destination.hostname.field=${impala.data.%s.table.field.target_machine}", this.dataSourceName, this.dataSourceName);
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

			logger.error("There was an exception during the execution - {}",e.getCause().getMessage());
			System.out.println(String.format("There was an exception during execution please see more info at the log "));
		}

		finally {
			try {
				taskPropertiesFileWriter.close();
			}
			catch (IOException exception)
			{
				logger.error("There was an exception during the file - {} closing  , cause - {} ",taskPropertiesFile.getName(),exception.getCause().getMessage());
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
				line = String.format("%s.%s_dest_VpnEnrichTask.country.field=dest_country",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//longtitude  field
				line = String.format("%s.%s_dest_VpnEnrichTask.longtitude.field=dest_longtitude",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//latitude  field
				line = String.format("%s.%s_dest_VpnEnrichTask.latitude.field=dest_latitude",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//countryIsoCode field
				line = String.format("%s.%s_dest_VpnEnrichTask.countryIsoCode.field=dest_countryIsoCode",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//region  field
				line = String.format("%s.%s_dest_VpnEnrichTask.region.field=dest_region",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//city field
				line = String.format("%s.%s_dest_VpnEnrichTask.city.field=dest_city",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//isp field
				line = String.format("%s.%s_dest_VpnEnrichTask.isp.field=dest_isp",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//usageType field
				line = String.format("%s.%s_dest_VpnEnrichTask.usageType.field=dest_usageType",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//put session update configuration as false  field
				line = String.format("%s.%s_dest_VpnEnrichTask.doSessionUpdate=false",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);


				//put data bucket as false field
				line = String.format("%s.%s_dest_VpnEnrichTask.doDataBuckets=false",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//put geo location as true field
				line = String.format("%s.%s_dest_VpnEnrichTask.doGeoLocation=true",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName, this.dataSourceName);
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
			logger.error("There was an exception during the execution - {}",e.getCause().getMessage());
			System.out.println(String.format("There was an exception during execution please see more info at the log "));
		}

		finally {
			try {
				taskPropertiesFileWriter.close();
			}
			catch (IOException exception)
			{
				logger.error("There was an exception during the file - {} closing  , cause - {} ",taskPropertiesFile.getName(),exception.getCause().getMessage());

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
				String successValue = br.readLine().toLowerCase();
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
			line = String.format("%s.%s_UserMongoUpdateStreamTask.UserMongoUpdateStreamTask.updateOnly=true",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName);
			writeLineToFile(line, taskPropertiesFileWriter, true);

			//flush the writer for user-mongo-update-task.properties
			taskPropertiesFileWriter.flush();



		} catch (Exception e) {
			logger.error("There was an exception during the execution - {}",e.getCause().getMessage());
			System.out.println(String.format("There was an exception during execution please see more info at the log "));

		} finally {
			try {
				taskPropertiesFileWriter.close();
			} catch (IOException exception) {

				logger.error("There was an exception during the file - {} closing  , cause - {} ", taskPropertiesFile.getName(), exception.getCause().getMessage());

				System.out.println(String.format("There was an exception during execution please see more info at the log "));

			}
		}
	}
	private void configureHDFSWruteTask(FileWriter taskPropertiesFileWriter, File taskPropertiesFile, Boolean topolegyResult, BufferedReader br,String step){

		try {
			String line = "";
			line = String.format("# %s", this.dataSourceName);
			writeLineToFile(line, taskPropertiesFileWriter, true);


			configureTaskMandatoryConfiguration(taskPropertiesFileWriter, topolegyResult, "enriched_HDFSWriterStreamTask", lastState, String.format("fortscale-%s-enriched-after-write",this.dataSourceName));

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
			line = String.format("%s.%s_enriched_HDFSWriterStreamTask.hdfs.root=${hdfs.user.enricheddata.%s.pat}",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName,this.dataSourceName);
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


			//flush the writer for hdfs write of enrich part
			taskPropertiesFileWriter.flush();

		}
		catch (Exception e)
		{
			logger.error("There was an exception during the execution - {}",e.getCause().getMessage());
			System.out.println(String.format("There was an exception during execution please see more info at the log "));
		}

		finally {
			try {
				taskPropertiesFileWriter.close();
			} catch (IOException exception) {
				logger.error("There was an exception during the file - {} closing  , cause - {} ", taskPropertiesFile.getName(), exception.getCause().getMessage());

				System.out.println(String.format("There was an exception during execution please see more info at the log "));

			}
		}

	}
	private void configureTaskMandatoryConfiguration(FileWriter taskPropertiesFileWriter ,Boolean topolegyResult, String name,String lastState,String outputTopic){
		String line ="";
		//name
		line = String.format("%s.%s_%s=%s_%s",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName, name,this.dataSourceName,name);
		writeLineToFile(line, taskPropertiesFileWriter, true);

		//data source
		line = String.format("%s.%s_%s.data.source=%s",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName,name, this.dataSourceName.toLowerCase());
		writeLineToFile(line, taskPropertiesFileWriter, true);

		//last state
		line = String.format("%s.%s_%s.last.state=%s",FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName,name,lastState);
		writeLineToFile(line, taskPropertiesFileWriter, true);


		if(!StringUtils.isBlank(outputTopic)) {
			//GDS general topology
			if (topolegyResult) {
				line = String.format("%s.%s_%s.output.topic=%s", FORTSCALE_CONFIGURATION_PREFIX, this.dataSourceName, name, outputTopic);
				writeLineToFile(line, taskPropertiesFileWriter, true);
			} else {

				System.out.println("Not supported yet via  this configuration tool ");
				//TODO - Need to add the topic configuration  also for task.inputs and fortscale.events.entry.<dataSource>_UsernameNormalizationAndTaggingTask.output.topic

			}
		}

	}

	private String validatedFieldExietInSchema(String fieldName,Map<String,String> fieldsSchema,String fields,String showMessage) throws Exception
	{
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

	private String alignTheFieldList(Map<String,String> updatedDataSourceSchema)
	{
		String result = "";

		for (Map.Entry<String,String> entry :updatedDataSourceSchema.entrySet())
		{
			result+=entry.getKey()+" "+entry.getValue()+",";

		}

		return result.substring(0,result.length()-1);
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
		   logger.error("There was an exception during the execution - {}",e.getCause().getMessage());
		   System.out.println(String.format("There was an exception during execution please see more info at the log "));
	   }


    }

}
