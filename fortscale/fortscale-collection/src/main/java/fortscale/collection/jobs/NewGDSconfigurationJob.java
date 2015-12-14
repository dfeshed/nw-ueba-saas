package fortscale.collection.jobs;

import fortscale.utils.logging.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;

/**
 * Created by idanp on 12/1/2015.
 */
public class NewGDSconfigurationJob extends FortscaleJob {

    private static Logger logger = Logger.getLogger(NewGDSconfigurationJob.class);

	private  String dataSourceName;
	private  Boolean sourceIpFlag;
	private  Boolean targetIpFlag;

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


        startNewStep("Init Configuration");
        initPartConfiguration(br);
        finishStep();

		startNewStep("Init Configuration");
		streamingConfiguration(br);
		finishStep();


    }

    /**
     * This section will configure the Init configuration (The part that support the schema (HDFS paths and impala tables)
     * @param br - Will hold the scanner for tracing the user input
     */
    public void initPartConfiguration(BufferedReader br)
    {

        // Open the fortscale-collection-overriding.properties in append mode for adding the new configuration
        //TODO - CHANGE IT TO REALTIVE PATH
        File file = new File("fortscale-collection/resources/fortscale-collection-overriding.properties");
        FileWriter fileWriter=null;
		File streamingOverridingFile = new File ("fortscale-streaming/config/fortscale-overriding-streaming.properties");
		FileWriter streamingOverridingfileWriter=null;

        try{
            fileWriter = new FileWriter(file,true);
			streamingOverridingfileWriter=new FileWriter(streamingOverridingFile,true);

            Boolean result = false;
            System.out.println("Init Configuration - This part will responsible to the schema configuration (HDFS and Impala)");


            fileWriter.write("\r\n");
            fileWriter.write("\r\n");
			streamingOverridingfileWriter.write("\r\n");
			streamingOverridingfileWriter.write("\r\n");


			String line = String.format("########################################### New Configuration For Generic Data Source  ########################################################",dataSourceName);
			writeLineToFile(line,fileWriter,true);
			writeLineToFile(line,streamingOverridingfileWriter,true);


			//Configure the data source list
            fileWriter.write(String.format("fortscale.data.source=%s,%s",currentDataSources,dataSourceName));


			line = String.format("########################################### %s ########################################################",dataSourceName);
			writeLineToFile(line,fileWriter,true);
			writeLineToFile(line,streamingOverridingfileWriter,true);





            System.out.println(String.format("Dose %s Have data schema (for ETL) (y/n) ?",dataSourceName));
            result = br.readLine().toLowerCase().equals("y") || br.readLine().toLowerCase().equals("yes");

            //Data part
            if(result)
            {
                line = String.format("########### Data Schema",dataSourceName);
				writeLineToFile(line,fileWriter,true);


				//fields
                System.out.println(String.format("Please enter the fields for %s data schema at csv style with data types  (i.e ussername STRING,target_score DOUBLE)",dataSourceName));
                String fields = br.readLine();
				line=String.format("impala.data.%s.table.fields=%s",dataSourceName,fields);
				writeLineToFile(line,fileWriter,true);
                writeLineToFile(line,streamingOverridingfileWriter,true);


                //write the username configuration at the streaming overriding for the enrich part
                System.out.println(String.format("Please enter the \"username\" field name (i.e account_name or user_id ):"));
                String usernameFieldName =  br.readLine().toLowerCase();
                line=String.format("impala.data.%s.table.field.username=%s",dataSourceName,usernameFieldName);
                writeLineToFile(line,streamingOverridingfileWriter,true);



				//write the source_ip field configuration at the streaming overriding for the enrich part
				System.out.println(String.format("Does %s will have source ip field  (i.e spurce_ip or client_address ):",this.dataSourceName));
				sourceIpFlag =  br.readLine().toLowerCase().equals("y") || br.readLine().toLowerCase().equals("yes");

				if (sourceIpFlag) {
					System.out.println(String.format("Please enter the \"source ip\" field name (i.e source_ip or client_address ):"));
					String sourceIpFieldName = br.readLine().toLowerCase();
					line = String.format("impala.data.%s.table.field.source_ip=%s", dataSourceName, sourceIpFieldName);
					writeLineToFile(line, streamingOverridingfileWriter, true);

					//write the source_ip field configuration at the streaming overriding for the enrich part
					System.out.println(String.format("Please enter the \"source machine field name that will contain the source ip resolving result\"  (i.e hostname ):"));
					String sourceMachineFieldName =  br.readLine().toLowerCase();
					line=String.format("impala.data.%s.table.field.hostname=%s",dataSourceName,sourceMachineFieldName);
					writeLineToFile(line,streamingOverridingfileWriter,true);

					//write the source machien classification field configuration at the streaming overriding for the enrich part
					System.out.println(String.format("Please enter the \"source machine\" class field name, this field will contain if this is a Desktop or Server   (i.e src_class ):",this.dataSourceName));
					String srClassFieldName =  br.readLine().toLowerCase();
					line=String.format("impala.data.%s.table.field.src_class=%s",dataSourceName,srClassFieldName);
					writeLineToFile(line,streamingOverridingfileWriter,true);

					//configure the normalized_src_machine
					line=String.format("impala.data.%s.table.field.normalized_src_machine=normalized_src_machine",dataSourceName,srClassFieldName);
					writeLineToFile(line,streamingOverridingfileWriter,true);

				}



				//write the target_ip field configuration at the streaming overriding for the enrich part
				System.out.println(String.format("Does %s will have target ip field  (i.e target_ip  ):",this.dataSourceName));
				targetIpFlag =  br.readLine().toLowerCase().equals("y") || br.readLine().toLowerCase().equals("yes");

				if (targetIpFlag) {
					System.out.println(String.format("Please enter the \"target ip\" field name (i.e target_ip ):"));
					String targetIpFieldName = br.readLine().toLowerCase();
					line = String.format("impala.data.%s.table.field.target_ip=%s", dataSourceName, targetIpFieldName);
					writeLineToFile(line, streamingOverridingfileWriter, true);


					//write the target_ip field configuration at the streaming overriding for the enrich part
					System.out.println(String.format("Please enter the \"target machine\" field name that will contain the target ip resolving result in case that %s doesnt contain target ip keep this empty  (i.e target_machine ):", this.dataSourceName));
					String targetMachineFieldName = br.readLine().toLowerCase();
					line = String.format("impala.data.%s.table.field.target_machine=%s", dataSourceName, targetMachineFieldName);
					writeLineToFile(line, streamingOverridingfileWriter, true);
				}


				//write the time stamp field name
				System.out.println(String.format("Please enter the \"time stamp field name \"  (i.e date_time_unix):"));
				String timestampFieldName =  br.readLine().toLowerCase();
				line=String.format("impala.data.%s.table.field.epochtime=%s",dataSourceName,timestampFieldName);
				writeLineToFile(line,streamingOverridingfileWriter,true);




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

                //partition type
                line = String.format("impala.data.%s.table.partition.type=monthly",dataSourceName);
				writeLineToFile(line,fileWriter,true);
                writeLineToFile(line,streamingOverridingfileWriter,true);








            }

            System.out.println(String.format("Dose %s Have enrich schema (y/n)?",dataSourceName));
            result = br.readLine().toLowerCase().equals("y") || br.readLine().toLowerCase().equals("yes");

            //Enrich  part
            if(result)
            {
				writeLineToFile("\r\n",fileWriter,true);
				writeLineToFile("\r\n",streamingOverridingfileWriter,true);

				line = String.format("########### Enrich Schema",dataSourceName);
				writeLineToFile(line,fileWriter,true);
				writeLineToFile(line,streamingOverridingfileWriter,true);



				//fields
                System.out.println(String.format("Please enter the fields for %s enrich schema csv style with data types  (i.e ussername STRING,target_score DOUBLE)",dataSourceName));
                String fields = br.readLine();
				line = String.format("impala.enricheddata.%s.table.fields=%s",dataSourceName,fields);
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


				//partition strategy
				line = String.format("impala.enricheddata.%s.table.partition.type=daily",dataSourceName);
				writeLineToFile(line,fileWriter,true);
				writeLineToFile(line,streamingOverridingfileWriter,true);

            }

			line = String.format("########### Score Schema",dataSourceName);
			writeLineToFile(line,fileWriter,true);
			writeLineToFile(line,streamingOverridingfileWriter,true);

            //Score part
            System.out.println(String.format("Please enter the fields for %s score schema csv style with data types  (i.e ussername STRING,target_score DOUBLE)",dataSourceName));
            String fields = br.readLine();

			//fields
			line = String.format("impala.score.%s.table.fields=%s",dataSourceName,fields);
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
            result = br.readLine().toLowerCase().equals("y") ||br.readLine().toLowerCase().equals("yes") ;

            if(result) {
                //Top score part

				line = String.format("########### Top Score Schema",dataSourceName);
				writeLineToFile(line,fileWriter,true);
				writeLineToFile(line,streamingOverridingfileWriter,true);


				//fields
				line = String.format("impala.score.%s.top.table.fields=%s",dataSourceName,fields);
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
            logger.error("There was an exception during the file - {} processing , cause - {} ",file.getName(),exception.getMessage());

        }

        finally {
            if (fileWriter != null)
            {
               try {
                   fileWriter.close();
               }
               catch (IOException exception)
               {
                   logger.error("There was an exception during the file - {} closing  , cause - {} ",file.getName(),exception.getMessage());

               }

            }

			if (streamingOverridingfileWriter != null)
			{
				try {
					streamingOverridingfileWriter.close();
				}
				catch (IOException exception)
				{
					logger.error("There was an exception during the file - {} closing  , cause - {} ",streamingOverridingFile.getName(),exception.getMessage());

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

		// Open the fortscale-collection-overriding.properties in append mode for adding the new configuration
		//TODO - CHANGE IT TO REALTIVE PATH
		String configFilesPath = "fortscale-streaming/config/";
		Boolean result = false;

		try {


			System.out.println(String.format("Dose %s need to pass through enrich steps at the Streaming (y/n) ?", dataSourceName));
			result = br.readLine().toLowerCase().equals("y");

			if (result)
				//Enrich part
				enrichStereamingConfiguration(br, configFilesPath);
		}

		catch (Exception e)
		{

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
			Boolean topolegyResult = br.readLine().toLowerCase().equals("y") || br.readLine().toLowerCase().equals("yes");

			streamingOverridingFileWriter =  new FileWriter(streamingOverridingFile, true);



			//Normalized User Name and user Tagging task

			System.out.println(String.format("Going to configure the Normalized Username and tagging task for %s",dataSourceName));

			//open the task properties file
			taskPropertiesFile = new File(configFilesPath + "username-normalization-tagging-task.properties");
			taskPropertiesFileWriter = new FileWriter(taskPropertiesFile, true);

			taskPropertiesFileWriter.write("\r\n");
			taskPropertiesFileWriter.write("\r\n");

			line = String.format("# %s", this.dataSourceName);

			writeLineToFile(line,taskPropertiesFileWriter,true);


            //name
			line = String.format("fortscale.events.entry.name.%s_UsernameNormalizationAndTaggingTask=%s_UsernameNormalizationAndTaggingTask", this.dataSourceName, this.dataSourceName);
			writeLineToFile(line,taskPropertiesFileWriter,true);

            //data source
			line = String.format("fortscale.events.entry.%s_UsernameNormalizationAndTaggingTask.data.source=%s", this.dataSourceName, this.dataSourceName);
			writeLineToFile(line,taskPropertiesFileWriter,true);

            //last state
			line = String.format("fortscale.events.entry.%s_UsernameNormalizationAndTaggingTask.last.state=etl", this.dataSourceName);
			writeLineToFile(line,taskPropertiesFileWriter,true);



			//GDS general topology
			if(topolegyResult) {
				line = String.format("fortscale.events.entry.%s_UsernameNormalizationAndTaggingTask.output.topic=fortscale-generic-data-access-normalized-tagged-event", this.dataSourceName);
				writeLineToFile(line,taskPropertiesFileWriter,true);
			}
			else{

				System.out.println("Not supported yet via  this configuration tool ");
				//TODO - Need to add the topic configuration  also for task.inputs and fortscale.events.entry.<dataSource>_UsernameNormalizationAndTaggingTask.output.topic

			}

			//User name field
            System.out.println(String.format("Please enter the \"username\" field name (i.e account_name or user_id ):"));
            String usernameFieldName =  br.readLine().toLowerCase();
            line = String.format("fortscale.events.entry.%s_UsernameNormalizationAndTaggingTask.username.field=%s", this.dataSourceName,usernameFieldName);
            writeLineToFile(line, taskPropertiesFileWriter, true);

            //Domain field  - for the enrich part
            System.out.println(String.format("Please enter the Domain field name (i.e account_domain) in case %s data source doesn't have domain field please enter \"fake\":",dataSourceName));
            String domainFieldName =  br.readLine().toLowerCase();
            line=String.format("fortscale.events.entry.%s_UsernameNormalizationAndTaggingTask.domain.field=%s",dataSourceName,domainFieldName);
            writeLineToFile(line,taskPropertiesFileWriter,true);

            //In case of fake domain - enter the actual domain value the PS want
            System.out.println(String.format("If you chose a \"fake\" domain please enter the fix domain value for using (i.e vpnConnect,sshConnect or empty valuefor keeping the name without domain): "));
            String domainValue =  br.readLine().toLowerCase();
            line=String.format("fortscale.events.entry.%s_UsernameNormalizationAndTaggingTask.domain.fake=%s",dataSourceName,domainValue);
            writeLineToFile(line, taskPropertiesFileWriter, true);

			//Normalized_username field
            line = String.format("fortscale.events.entry.%s_UsernameNormalizationAndTaggingTask.normalizedusername.field=${impala.table.fields.normalized.username}");
            writeLineToFile(line, taskPropertiesFileWriter, true);

			//partition field name  (today we use for all the username)
            line = String.format("fortscale.events.entry.%s_UsernameNormalizationAndTaggingTask.partition.field=%s",this.dataSourceName, usernameFieldName);
            writeLineToFile(line, taskPropertiesFileWriter, true);


            //TODO - When we develope a new normalize service need to think what to do here cause now we have only ~2 kinds
			//Normalizing service
            System.out.println(String.format("Does the %s data source should contain users on the AD and you want to drop event of users that are not appeare there (i.e what we do for kerberos) (y/n):"));
            Boolean updateOnly =  br.readLine().toLowerCase().equals("y") || br.readLine().toLowerCase().equals("yes");

            if(updateOnly)
            {
                line = String.format("fortscale.events.entry.%S_UsernameNormalizationAndTaggingTask.normalization.service=SecurityUsernameNormalizationService",this.dataSourceName);
                writeLineToFile(line, taskPropertiesFileWriter, true);
                line = String.format("fortscale.events.entry.%s_UsernameNormalizationAndTaggingTask.updateOnly=true",this.dataSourceName);
                writeLineToFile(line, taskPropertiesFileWriter, true);
            }

            else {

                line = String.format("fortscale.events.entry.%S_UsernameNormalizationAndTaggingTask.normalization.service=genericUsernameNormalizationService",this.dataSourceName);
                writeLineToFile(line, taskPropertiesFileWriter, true);
                line = String.format("fortscale.events.entry.%s_UsernameNormalizationAndTaggingTask.updateOnly=false",this.dataSourceName);
                writeLineToFile(line, taskPropertiesFileWriter, true);
            }


			//classifier value
			line = String.format("fortscale.events.entry.%s_UsernameNormalizationAndTaggingTask.classifier=%s", this.dataSourceName,this.dataSourceName);
            writeLineToFile(line, taskPropertiesFileWriter, true);

            writeLineToFile("\r\n", taskPropertiesFileWriter, true);
            writeLineToFile("#############", taskPropertiesFileWriter, true);


			//flush the writer for username-normalization-tagging-task.properties
			taskPropertiesFileWriter.flush();







			//Ip Resolving task

			System.out.println(String.format("Going to configure the IP resolving task for %s",dataSourceName));

			//open the task properties file
			taskPropertiesFile = new File(configFilesPath + "ip-resolving-task.properties");
			taskPropertiesFileWriter = new FileWriter(taskPropertiesFile, true);

			taskPropertiesFileWriter.write("\r\n");
			taskPropertiesFileWriter.write("\r\n");

			line = String.format("# %s", this.dataSourceName);
			writeLineToFile(line,taskPropertiesFileWriter,true);

			// configure new configuration for the new dta source for source_ip
			System.out.println(String.format("Dose %s have source ip for ip resolving (y/n) ?",dataSourceName));
			Boolean sourceIpResult = br.readLine().toLowerCase().equals("y") || br.readLine().toLowerCase().equals("yes");

			System.out.println(String.format("Dose %s have target ip for ip resolving (y/n) ?",dataSourceName));
			Boolean targetIpResult = br.readLine().toLowerCase().equals("y") || br.readLine().toLowerCase().equals("yes");

			Boolean restrictToAD = br.readLine().toLowerCase().equals("y") || br.readLine().toLowerCase().equals("yes");
			Boolean shortNameUsage = br.readLine().toLowerCase().equals("y") || br.readLine().toLowerCase().equals("yes");
			Boolean removeLastDotUsage = br.readLine().toLowerCase().equals("y") || br.readLine().toLowerCase().equals("yes");
			Boolean dropOnFailUsage = br.readLine().toLowerCase().equals("y") || br.readLine().toLowerCase().equals("yes");
			Boolean overrideIpWithHostNameUsage = br.readLine().toLowerCase().equals("y") || br.readLine().toLowerCase().equals("yes");

			//source ip configuration for resolving the ip
			if(sourceIpResult)
			{

				//name
				line = String.format("fortscale.events.entry.name.%s_IpResolvingStreamTask_sourceIp=%s_IpResolvingStreamTask_sourceIp", this.dataSourceName, this.dataSourceName);
				writeLineToFile(line,taskPropertiesFileWriter,true);

				//data source
				line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_sourceIp.data.source=%s", this.dataSourceName, this.dataSourceName);
				writeLineToFile(line,taskPropertiesFileWriter,true);

				//last state
				line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_sourceIp.last.state=etl", this.dataSourceName);
				writeLineToFile(line,taskPropertiesFileWriter,true);



				//GDS general topology
				if(topolegyResult) {
					line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_sourceIp.output.topic=fortscale-generic-data-access-source-ip-resolved", this.dataSourceName);
					writeLineToFile(line,taskPropertiesFileWriter,true);
				}
				else{

					System.out.println("Not supported yet via  this configuration tool ");
					//TODO - Need to add the topic configuration  also for task.inputs and fortscale.events.entry.<dataSource>_UsernameNormalizationAndTaggingTask.output.topic

				}

				//partition field name  (today we use for all the username)
				line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_sourceIp.partition.field=${impala.data.%s.table.field.username}",this.dataSourceName, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//source ip field
				line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_sourceIp.ip.field=${impala.data.%s.table.field.source_ip}",this.dataSourceName, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//hostname
				line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_sourceIp.host.field=${impala.data.%s.table.field.hostname}",this.dataSourceName, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//time stamp
				line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_sourceIp.timestamp.field=${impala.data.%s.table.field.epochtime}",this.dataSourceName, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);


				//restric to AD
				System.out.println(String.format("Dose %s resolving is restricted to AD name (in case of true and the machine doesnt exist in the AD it will not return it as resolved value) (y/n) ?",dataSourceName));
				line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_sourceIp.restrictToADName=%s",this.dataSourceName, restrictToAD.toString());
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//short name
				System.out.println(String.format("Dose %s resolving use the machine short name (i.e SERV1@DOMAINBLABLA instead of SERV1@DOMAINBLABLA.com) (y/n) ?",dataSourceName));
				line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_sourceIp.shortName=%s",this.dataSourceName, shortNameUsage.toString());
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//Remove last Dot
				System.out.println(String.format("Dose %s resolving need to remove last dot from the resolved server name  (i.e SERV1@DOMAINBLABLA instead of SERV1@DOMAINBLABLA.) (y/n) ?",dataSourceName));
				line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_sourceIp.isRemoveLastDot=%s",this.dataSourceName, removeLastDotUsage.toString());
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//Drop When Fail
				System.out.println(String.format("Dose %s resolving need to drop in case of resolving fail (y/n) ?",dataSourceName));
				line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_sourceIp.dropWhenFail=%s",this.dataSourceName, dropOnFailUsage.toString());
				writeLineToFile(line, taskPropertiesFileWriter, true);


				//Override IP with Hostname
				System.out.println(String.format("Dose %s resolving need to override the source ip field with the resolving value (y/n) ?",dataSourceName));
				line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_sourceIp.overrideIPWithHostname=%s",this.dataSourceName, overrideIpWithHostNameUsage.toString());
				writeLineToFile(line, taskPropertiesFileWriter, true);



			}

			//reslove also a target ip
			if(targetIpResult)
			{

				//name
				line = String.format("fortscale.events.entry.name.%s_IpResolvingStreamTask_targetIp=%s_IpResolvingStreamTask_targetIp", this.dataSourceName, this.dataSourceName);
				writeLineToFile(line,taskPropertiesFileWriter,true);

				//data source
				line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_targetIp.data.source=%s", this.dataSourceName, this.dataSourceName);
				writeLineToFile(line,taskPropertiesFileWriter,true);

				//last state
				line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_targetIp.last.state=IpResolvingStreamTask", this.dataSourceName);
				writeLineToFile(line,taskPropertiesFileWriter,true);



				//GDS general topology
				if(topolegyResult) {
					line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_targetIp.output.topic=fortscale-generic-data-access-target-ip-resolved", this.dataSourceName);
					writeLineToFile(line,taskPropertiesFileWriter,true);
				}
				else{

					System.out.println("Not supported yet via  this configuration tool ");
					//TODO - Need to add the topic configuration  also for task.inputs and fortscale.events.entry.<dataSource>_UsernameNormalizationAndTaggingTask.output.topic

				}

				//partition field name  (today we use for all the username)
				line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_targetIp.partition.field=${impala.data.%s.table.field.username}",this.dataSourceName, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//target ip field
				line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_targetIp.ip.field=${impala.data.%s.table.field.target_ip}",this.dataSourceName, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//target machine
				line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_targetIp.host.field=${impala.data.%s.table.field.target_machine}",this.dataSourceName, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//time stamp
				line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_targetIp.timestamp.field=${impala.data.%s.table.field.epochtime}",this.dataSourceName, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);


				//restric to AD

				line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_targetIp.restrictToADName=%s",this.dataSourceName, restrictToAD.toString());
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//short name
				System.out.println(String.format("Dose %s resolving use the machine short name (i.e SERV1@DOMAINBLABLA instead of SERV1@DOMAINBLABLA.com) (y/n) ?",dataSourceName));
				line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_sourceIp.shortName=%s",this.dataSourceName, shortNameUsage.toString());
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//Remove last Dot
				System.out.println(String.format("Dose %s resolving need to remove last dot from the resolved server name  (i.e SERV1@DOMAINBLABLA instead of SERV1@DOMAINBLABLA.) (y/n) ?",dataSourceName));
				line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_sourceIp.isRemoveLastDot=%s",this.dataSourceName, removeLastDotUsage.toString());
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//Drop When Fail
				System.out.println(String.format("Dose %s resolving need to drop in case of resolving fail (y/n) ?",dataSourceName));
				line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_sourceIp.dropWhenFail=%s",this.dataSourceName, dropOnFailUsage.toString());
				writeLineToFile(line, taskPropertiesFileWriter, true);


				//Override IP with Hostname
				System.out.println(String.format("Dose %s resolving need to override the source ip field with the resolving value (y/n) ?",dataSourceName));
				line = String.format("fortscale.events.entry.%s_IpResolvingStreamTask_sourceIp.overrideIPWithHostname=%s",this.dataSourceName, overrideIpWithHostNameUsage.toString());
				writeLineToFile(line, taskPropertiesFileWriter, true);

			}

			//flush the writer for ip-resolving-task.properties
			taskPropertiesFileWriter.flush();


			//Computer tagging task

			if (sourceIpFlag || targetIpFlag) {
				System.out.println(String.format("Going to configure the Computer tagging and normalization task for %s", dataSourceName));

				//open the task properties file
				taskPropertiesFile = new File(configFilesPath + "computer-tagging-clustering-task.properties");
				taskPropertiesFileWriter = new FileWriter(taskPropertiesFile, true);

				taskPropertiesFileWriter.write("\r\n");
				taskPropertiesFileWriter.write("\r\n");

				line = String.format("# %s", this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//name
				line = String.format("fortscale.events.entry.name.%s_ComputerTaggingClusteringTask=%s_ComputerTaggingClusteringTask", this.dataSourceName, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//data source
				line = String.format("fortscale.events.entry.%s_ComputerTaggingClusteringTask.data.source=%s", this.dataSourceName, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//last state
				line = String.format("fortscale.events.entry.%s_ComputerTaggingClusteringTask.last.state=IpResolvingStreamTask", this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//GDS general topology
				if (topolegyResult) {
					line = String.format("fortscale.events.entry.%s_ComputerTaggingClusteringTask.output.topic=fortscale-generic-data-access-computer-tagged-clustered", this.dataSourceName);
					writeLineToFile(line, taskPropertiesFileWriter, true);
				} else {

					System.out.println("Not supported yet via  this configuration tool ");
					//TODO - Need to add the topic configuration  also for task.inputs and fortscale.events.entry.<dataSource>_UsernameNormalizationAndTaggingTask.output.topic

				}

				//partition field name  (today we use for all the username)
				line = String.format("fortscale.events.entry.%s_ComputerTaggingClusteringTask.partition.field=${impala.data.%s.table.field.username}", this.dataSourceName, this.dataSourceName);
				writeLineToFile(line, taskPropertiesFileWriter, true);

				//only in case there is a source ip that should be resolving
				if(sourceIpFlag) {
					//hostname
					line = String.format("fortscale.events.entry.%s_ComputerTaggingClusteringTask.source.hostname.field=${impala.data.%s.table.field.hostname}", this.dataSourceName, this.dataSourceName);
					writeLineToFile(line, taskPropertiesFileWriter, true);

					//classification
					line = String.format("fortscale.events.entry.%s_ComputerTaggingClusteringTask.source.classification.field=${impala.data.%s.table.field.src_class}", this.dataSourceName, this.dataSourceName);
					writeLineToFile(line, taskPropertiesFileWriter, true);

					//Normalized_src_machine
					line = String.format("fortscale.events.entry.%s_ComputerTaggingClusteringTask.source.clustering.field=${impala.data.%s.table.field.normalized_src_machine}", this.dataSourceName, this.dataSourceName);
					writeLineToFile(line, taskPropertiesFileWriter, true);

					// configure new configuration for the new dta source for source_ip
					System.out.println(String.format("Dose %s source machine need to be added to the computer document in case he is missing (y/n) ?", dataSourceName));
					Boolean ensureComputerExist = br.readLine().toLowerCase().equals("y") || br.readLine().toLowerCase().equals("yes");
					line = String.format("fortscale.events.entry.%s_ComputerTaggingClusteringTask.source.create-new-computer-instances=%s", this.dataSourceName, ensureComputerExist.toString());
					writeLineToFile(line, taskPropertiesFileWriter, true);
				}

/*
				fortscale.events.entry.ssh_ComputerTaggingClusteringTask.destination.hostname.field = target_machine_temp
				fortscale.events.entry.ssh_ComputerTaggingClusteringTask.destination.classification.field = $ {
					impala.score.ssh.table.field.dst_class
				}
				fortscale.events.entry.ssh_ComputerTaggingClusteringTask.destination.clustering.field = $ {
					impala.data.ssh.table.field.normalized_dst_machine
				}
				fortscale.events.entry.ssh_ComputerTaggingClusteringTask.destination.is - sensitive - machine.field = $
				{
					impala.data.ssh.table.field.is_sensitive_machine
				}
				fortscale.events.entry.ssh_ComputerTaggingClusteringTask.destination.create - new - computer - instances = true
				*/
			}


			//Geo location task


			//User Mongo update task


            //HDFS Write

         
		}

		catch(Exception e)
		{

		}





	}





	private void writeLineToFile(String line, FileWriter writer, boolean withNewLine) throws Exception
	{
		writer.write(line);
		if(withNewLine)
			writer.write("\r\n");
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
        simulator.initPartConfiguration(br);


    }

}
