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

        try{
            fileWriter = new FileWriter(file,true);

            Boolean result = false;
            System.out.println("Init Configuration - This part will responsible to the schema configuration (HDFS and Impala)");


            fileWriter.write("\r\n");
            fileWriter.write("\r\n");



            fileWriter.write(String.format("########################################### New Configuration For Generic Data Source  ########################################################",dataSourceName));
            fileWriter.write("\r\n");
            fileWriter.write("\r\n");

            fileWriter.write(String.format("fortscale.data.source=%s,%s",currentDataSources,dataSourceName));

            fileWriter.write("\r\n");
            fileWriter.write("\r\n");

            fileWriter.write(String.format("########################################### %s ########################################################",dataSourceName));
            fileWriter.write("\r\n");





            System.out.println(String.format("Dose %s Have data schema (for ETL) (y/n) ?",dataSourceName));
            result = br.readLine().toLowerCase().equals("y");

            //Data part
            if(result)
            {
                fileWriter.write("\r\n");
                fileWriter.write(String.format("########### Data Schema",dataSourceName));
                fileWriter.write("\r\n");
                System.out.println(String.format("Please enter the fields for %s data schema at csv style with data types  (i.e ussername STRING,target_score DOUBLE)",dataSourceName));
                String fields = br.readLine();

                fileWriter.write(String.format("impala.data.%s.table.fields=%s",dataSourceName,fields));
                fileWriter.write("\r\n");

                System.out.println(String.format("Please enter the %s data schema delimiter  (i.e | or , )",dataSourceName));
                String delimiter = br.readLine();

                fileWriter.write(String.format("impala.data.%s.table.delimiter=%s",dataSourceName,delimiter));
                fileWriter.write("\r\n");

                System.out.println(String.format("Please enter the %s data table name  (i.e sshdata )",dataSourceName));
                String dataTableName = br.readLine();

                fileWriter.write(String.format("impala.data.%s.table.name=%s",dataSourceName,dataTableName));
                fileWriter.write("\r\n");


                fileWriter.write(String.format("hdfs.user.data.%s.path=${hdfs.user.data.path}/%s",dataSourceName,dataSourceName));
                fileWriter.write("\r\n");
                fileWriter.write(String.format("impala.data.%s.table.partition.type=monthly",dataSourceName));
                fileWriter.write("\r\n");


            }

            System.out.println(String.format("Dose %s Have enrich schema (y/n)?",dataSourceName));
            result = br.readLine().toLowerCase().equals("y");;

            //Enrich  part
            if(result)
            {
                fileWriter.write("\r\n");
                fileWriter.write(String.format("########### Enrich Schema",dataSourceName));
                fileWriter.write("\r\n");
                System.out.println(String.format("Please enter the fields for %s enrich schema csv style with data types  (i.e ussername STRING,target_score DOUBLE)",dataSourceName));
                String fields = br.readLine();

                fileWriter.write(String.format("impala.enricheddata.%s.table.fields=%s",dataSourceName,fields));
                fileWriter.write("\r\n");

                System.out.println(String.format("Please enter the %s enrich schema delimiter  (i.e | or , )",dataSourceName));
                String delimiter = br.readLine();

                fileWriter.write(String.format("impala.enricheddata.%s.table.delimiter=%s",dataSourceName,delimiter));
                fileWriter.write("\r\n");

                System.out.println(String.format("Please enter the %s enrich table name  (i.e sshenriched )",dataSourceName));
                String tableName = br.readLine();


                fileWriter.write(String.format("impala.enricheddata.%s.table.name=%s",dataSourceName,tableName));
                fileWriter.write("\r\n");

                fileWriter.write(String.format("hdfs.user.enricheddata.%s.path=${hdfs.user.enricheddata.path}/%s",dataSourceName,dataSourceName));
                fileWriter.write("\r\n");

                fileWriter.write(String.format("impala.enricheddata.%s.table.partition.type=daily",dataSourceName));
                fileWriter.write("\r\n");


            }

            fileWriter.write(String.format("########### Score Schema",dataSourceName));
            fileWriter.write("\r\n");
            //Score part
            System.out.println(String.format("Please enter the fields for %s score schema csv style with data types  (i.e ussername STRING,target_score DOUBLE)",dataSourceName));
            String fields = br.readLine();

            fileWriter.write(String.format("impala.score.%s.table.fields=%s",dataSourceName,fields));
            fileWriter.write("\r\n");

            System.out.println(String.format("Please enter the %s score schema delimiter  (i.e | or , )",dataSourceName));
            String delimiter = br.readLine();

            fileWriter.write(String.format("impala.score.%s.table.delimiter=%s",dataSourceName,delimiter));
            fileWriter.write("\r\n");

            System.out.println(String.format("Please enter the %s score table name  (i.e sshscores )",dataSourceName));
            String tableName = br.readLine();


            fileWriter.write(String.format("impala.score.%s.table.name=%s",dataSourceName,tableName));
            fileWriter.write("\r\n");

            fileWriter.write(String.format("hdfs.user.processeddata.%s.path=${hdfs.user.processeddata.path}/%s",dataSourceName,dataSourceName));
            fileWriter.write("\r\n");

            fileWriter.write(String.format("impala.score.%s.table.partition.type=daily",dataSourceName));
            fileWriter.write("\r\n");

            System.out.println(String.format("Dose %s Have top table schema (y/n) ?",dataSourceName));
            result = br.readLine().toLowerCase().equals("y");

            if(result) {
                //Top score part

                fileWriter.write(String.format("########### Top Score Schema",dataSourceName));
                fileWriter.write("\r\n");

                fileWriter.write(String.format("impala.score.%s.top.table.fields=%s",dataSourceName,fields));
                fileWriter.write("\r\n");


                fileWriter.write(String.format("impala.score.%s.top.table.delimiter=%s",dataSourceName,delimiter));
                fileWriter.write("\r\n");

                fileWriter.write(String.format("impala.score.%s.top.table.name=%s",dataSourceName,tableName));
                fileWriter.write("\r\n");

                fileWriter.write(String.format("hdfs.user.processeddata.%s.top.path=${hdfs.user.processeddata.path}/%s",dataSourceName,dataSourceName));
                fileWriter.write("\r\n");

                fileWriter.write(String.format("impala.score.%s.top.table.partition.type=daily",dataSourceName));
                fileWriter.write("\r\n");


            }






            fileWriter.flush();

        }
        catch (IOException exception)
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


		File file = null;
		FileWriter fileWriter=null;
		Boolean result = false;

		try {



			//Normalized User Name and user Tagging task
			System.out.println(String.format("Going gto configure the Normalized Username and tagging task for %s",dataSourceName));

			//open the task properties file
			file = new File(configFilesPath + "username-normalization-tagging-task.properties");
			fileWriter = new FileWriter(file, true);

			fileWriter.write("\r\n");
			fileWriter.write("\r\n");

			fileWriter.write(String.format("# %s",this.dataSourceName));
			fileWriter.write("\r\n");

			fileWriter.write(String.format("fortscale.events.entry.name.%s_UsernameNormalizationAndTaggingTask=%s_UsernameNormalizationAndTaggingTask",this.dataSourceName,this.dataSourceName));
			fileWriter.write(String.format("fortscale.events.entry.%s_UsernameNormalizationAndTaggingTask.data.source=%s",this.dataSourceName,this.dataSourceName));
			fileWriter.write(String.format("fortscale.events.entry.%s_UsernameNormalizationAndTaggingTask.last.state=etl",this.dataSourceName));



			// configure new Topic to the data source or use the GDS general topic
			System.out.println(String.format("Dose %s use the general GDS streaming topology   (y/n) ?",dataSourceName));
			result = br.readLine().toLowerCase().equals("y");

			//GDS general topology
			if(result) {
				fileWriter.write(String.format("fortscale.events.entry.%s_UsernameNormalizationAndTaggingTask.output.topic=kafka.genericDataAccess.struct.topic", this.dataSourceName));
				fileWriter.write("\r\n");
			}
			else{

				System.out.println("Not supported yet via  this configuration tool ");
				//TODO - Need to add the topic configuration  also for task.inputs and fortscale.events.entry.<dataSource>_UsernameNormalizationAndTaggingTask.output.topic

			}

			//User name field
			System.out.println(String.format("Please enter the username field (i.e account_name for kerberos): "));
			String usernameField = br.readLine();
			fileWriter.write(String.format("fortscale.events.entry.%s_UsernameNormalizationAndTaggingTask.username.field=%s", usernameField));
			fileWriter.write("\r\n");

			//Domain Field
			System.out.println(String.format("Please enter the domain field (i.e account_domain for kerberos): "));
			String domainField = br.readLine();
			fileWriter.write(String.format("fortscale.events.entry.%s_UsernameNormalizationAndTaggingTask.domain.field=%s", domainField));
			fileWriter.write("\r\n");

			//Normalized_username field
			fileWriter.write(String.format("fortscale.events.entry.%s_UsernameNormalizationAndTaggingTask.normalizedusername.field=${impala.table.fields.normalized.username}"));
			fileWriter.write("\r\n");

			//partition field name  (today we use for all the username)
			fileWriter.write(String.format("fortscale.events.entry.%s_UsernameNormalizationAndTaggingTask.partition.field=%s",usernameField));
			fileWriter.write("\r\n");

			//partition field name  (today we use for all the username)
			fileWriter.write(String.format("fortscale.events.entry.%s_UsernameNormalizationAndTaggingTask.normalization.service=%s",usernameField));
			fileWriter.write("\r\n");


			//classifier value
			fileWriter.write(String.format("fortscale.events.entry.%s_UsernameNormalizationAndTaggingTask.classifier=%s",this.dataSourceName.toLowerCase()));
			fileWriter.write("\r\n");




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
