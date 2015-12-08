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

                System.out.println(String.format("Please enter the fields for %s data schema at csv style with data types  (i.e ussername STRING,target_score DOUBLE)",dataSourceName));
                String fields = br.readLine();

				line=String.format("impala.data.%s.table.fields=%s",dataSourceName,fields);
				writeLineToFile(line,fileWriter,true);

                System.out.println(String.format("Please enter the %s data schema delimiter  (i.e | or , )",dataSourceName));
                String delimiter = br.readLine();

                line = String.format("impala.data.%s.table.delimiter=%s",dataSourceName,delimiter);
				writeLineToFile(line,fileWriter,true);

                System.out.println(String.format("Please enter the %s data table name  (i.e sshdata )",dataSourceName));
                String dataTableName = br.readLine();

                line = String.format("impala.data.%s.table.name=%s",dataSourceName,dataTableName);
				writeLineToFile(line,fileWriter,true);


                line = String.format("hdfs.user.data.%s.path=${hdfs.user.data.path}/%s",dataSourceName,dataSourceName);
				writeLineToFile(line,fileWriter,true);
                line = String.format("impala.data.%s.table.partition.type=monthly",dataSourceName);
				writeLineToFile(line,fileWriter,true);


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

			//hadfs path
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
				fileWriter.write(line);
				streamingOverridingfileWriter.write(line);
				fileWriter.write("\r\n");
				streamingOverridingfileWriter.write("\r\n");


            }


            fileWriter.flush();

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



		File taskPropertiesFile = null;
		FileWriter taskPropertiesFileWriter=null;
		Boolean result = false;

		try {


			//Open the streaming overriding file
			File streamingOverridingFile = new File (configFilesPath + "fortscale-overriding-streaming.properties");
			FileWriter streamingOverridingFileWriter =  new FileWriter(streamingOverridingFile, true);




			//Normalized User Name and user Tagging task
			System.out.println(String.format("Going gto configure the Normalized Username and tagging task for %s",dataSourceName));

			//open the task properties file
			taskPropertiesFile = new File(configFilesPath + "username-normalization-tagging-task.properties");
			taskPropertiesFileWriter = new FileWriter(taskPropertiesFile, true);

			taskPropertiesFileWriter.write("\r\n");
			taskPropertiesFileWriter.write("\r\n");

			taskPropertiesFileWriter.write(String.format("# %s", this.dataSourceName));
			taskPropertiesFileWriter.write("\r\n");

			taskPropertiesFileWriter.write(String.format("fortscale.events.entry.name.%s_UsernameNormalizationAndTaggingTask=%s_UsernameNormalizationAndTaggingTask", this.dataSourceName, this.dataSourceName));
			taskPropertiesFileWriter.write(String.format("fortscale.events.entry.%s_UsernameNormalizationAndTaggingTask.data.source=%s", this.dataSourceName, this.dataSourceName));
			taskPropertiesFileWriter.write(String.format("fortscale.events.entry.%s_UsernameNormalizationAndTaggingTask.last.state=etl", this.dataSourceName));



			// configure new Topic to the data source or use the GDS general topic
			System.out.println(String.format("Dose %s use the general GDS streaming topology   (y/n) ?",dataSourceName));
			result = br.readLine().toLowerCase().equals("y") || br.readLine().toLowerCase().equals("yes");

			//GDS general topology
			if(result) {
				taskPropertiesFileWriter.write(String.format("fortscale.events.entry.%s_UsernameNormalizationAndTaggingTask.output.topic=kafka.genericDataAccess.struct.topic", this.dataSourceName));
				taskPropertiesFileWriter.write("\r\n");
			}
			else{

				System.out.println("Not supported yet via  this configuration tool ");
				//TODO - Need to add the topic configuration  also for task.inputs and fortscale.events.entry.<dataSource>_UsernameNormalizationAndTaggingTask.output.topic

			}

			//User name field
			System.out.println(String.format("Please enter the username field (i.e account_name for kerberos): "));
			String usernameField = br.readLine();
			taskPropertiesFileWriter.write(String.format("fortscale.events.entry.%s_UsernameNormalizationAndTaggingTask.username.field=%s", usernameField));
			taskPropertiesFileWriter.write("\r\n");

			//Domain Field
			System.out.println(String.format("Please enter the domain field (i.e account_domain for kerberos): "));
			String domainField = br.readLine();
			taskPropertiesFileWriter.write(String.format("fortscale.events.entry.%s_UsernameNormalizationAndTaggingTask.domain.field=%s", domainField));
			taskPropertiesFileWriter.write("\r\n");

			//Normalized_username field
			taskPropertiesFileWriter.write(String.format("fortscale.events.entry.%s_UsernameNormalizationAndTaggingTask.normalizedusername.field=${impala.table.fields.normalized.username}"));
			taskPropertiesFileWriter.write("\r\n");

			//partition field name  (today we use for all the username)
			taskPropertiesFileWriter.write(String.format("fortscale.events.entry.%s_UsernameNormalizationAndTaggingTask.partition.field=%s", usernameField));
			taskPropertiesFileWriter.write("\r\n");

			//partition field name  (today we use for all the username)
			taskPropertiesFileWriter.write(String.format("fortscale.events.entry.%s_UsernameNormalizationAndTaggingTask.normalization.service=%s", usernameField));
			taskPropertiesFileWriter.write("\r\n");


			//classifier value
			taskPropertiesFileWriter.write(String.format("fortscale.events.entry.%s_UsernameNormalizationAndTaggingTask.classifier=%s", this.dataSourceName.toLowerCase()));
			taskPropertiesFileWriter.write("\r\n");




			/*SecurityUsernameNormalizationService

			fortscale.events.entry.%s_UsernameNormalizationAndTaggingTask.updateOnly=true*/




			//Ip Resolving task


			//Computer tagging task


			//Geo location task


			//User Mongo update task

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
