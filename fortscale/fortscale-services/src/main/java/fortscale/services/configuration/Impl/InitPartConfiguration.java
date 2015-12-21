package fortscale.services.configuration.Impl;

import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.ConfigurationService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * Created by idanp on 12/20/2015.
 */
public class InitPartConfiguration extends ConfigurationService {

	@Value("${fortscale.data.source}")
	private String currentDataSources;

	private String secondFileToConfigurePath; //Will represent the streaming overriding file
	private File secoundFileToConfigure;
	private FileWriter secondFileWriterToConfigure;






	public InitPartConfiguration(Map<String,ConfigurationParam> params)
	{
		logger = LoggerFactory.getLogger(InitPartConfiguration.class);
		this.fileToConfigurePath = this.root+"/fortscale/fortscale-core/fortscale/fortscale-collection/target/resources/fortscale-collection-overriding.properties";
		this.secondFileToConfigurePath = root+"/fortscale/streaming/config/fortscale-overriding-streaming.properties";
		this.configurationParams = params;


	}


	@Override
	public Boolean Init() {
		Boolean result = false;
		try {
			this.fileToConfigure = new File(this.fileToConfigurePath);
			this.fileWriterToConfigure = new FileWriter(this.fileToConfigure, true);
			this.secoundFileToConfigure = new File(this.secondFileToConfigurePath);
			this.secondFileWriterToConfigure = new FileWriter(this.secoundFileToConfigure, true);
			result = true;
		} catch (Exception e) {
			logger.error("There was an exception during InitPartConfiguration init part execution - {} ", e.getMessage());
			System.out.println(String.format("There was an exception during execution please see more info at the log "));
			result = false;

		}
		return result;
	}

	@Override
	public  Boolean Configure() throws Exception{

		Boolean result = false;
		String line ="";
		String dataSourceName = configurationParams.get("dataSourceName").getParamValue();
		String dataSourceType = configurationParams.get("dataSourceType").getParamValue();

		System.out.println("Init Configuration - This part will responsible to the schema configuration (HDFS and Impala)");



		writeLineToFile("\n",fileWriterToConfigure,true);
		writeLineToFile("\n",fileWriterToConfigure,true);
		writeLineToFile("\n",secondFileWriterToConfigure,true);
		writeLineToFile("\n",secondFileWriterToConfigure,true);

		line = String.format("########################################### New Configuration For Generic Data Source  ########################################################");
		writeLineToFile(line,fileWriterToConfigure,true);
		writeLineToFile(line,secondFileWriterToConfigure,true);

		//Configure the data source list
		line = String.format("fortscale.data.source=%s,%s",currentDataSources, dataSourceName);
		writeLineToFile(line,fileWriterToConfigure,true);

		line = String.format("########################################### %s ########################################################",dataSourceName);
		writeLineToFile(line,fileWriterToConfigure,true);
		writeLineToFile(line,secondFileWriterToConfigure,true);

		line=String.format("impala.data.%s.table.field.username=username",dataSourceName);
		writeLineToFile(line,fileWriterToConfigure,true);
		writeLineToFile(line,secondFileWriterToConfigure,true);

		if (configurationParams.containsKey("sourceIpFlag") && configurationParams.get("sourceIpFlag").getParamFlag()) {
			line = String.format("impala.data.%s.table.field.source=source_ip", dataSourceName);
			writeLineToFile(line, fileWriterToConfigure, true);
			writeLineToFile(line, secondFileWriterToConfigure, true);

			line = String.format("impala.data.%s.table.field.source_name=hostname", dataSourceName);
			writeLineToFile(line, fileWriterToConfigure, true);
			writeLineToFile(line, secondFileWriterToConfigure, true);

			line = String.format("impala.data.%s.table.field.src_class=src_class", dataSourceName);
			writeLineToFile(line, fileWriterToConfigure, true);
			writeLineToFile(line, secondFileWriterToConfigure, true);

			line = String.format("impala.data.%s.table.field.normalized_src_machine=normalized_src_machine", dataSourceName);
			writeLineToFile(line, fileWriterToConfigure, true);
			writeLineToFile(line, secondFileWriterToConfigure, true);
		}

		if (configurationParams.containsKey("targetIpFlag") && configurationParams.get("targetIpFlag").getParamFlag()) {
			line = String.format("impala.data.%s.table.field.target=source_ip", dataSourceName);
			writeLineToFile(line, fileWriterToConfigure, true);
			writeLineToFile(line, secondFileWriterToConfigure, true);

			line = String.format("impala.data.%s.table.field.target_name=target", dataSourceName);
			writeLineToFile(line, fileWriterToConfigure, true);
			writeLineToFile(line, secondFileWriterToConfigure, true);

			line = String.format("impala.data.%s.table.field.dst_class=dst_class", dataSourceName);
			writeLineToFile(line, fileWriterToConfigure, true);
			writeLineToFile(line, secondFileWriterToConfigure, true);

			line = String.format("impala.data.%s.table.field.normalized_dst_machine=normalized_dst_machine", dataSourceName);
			writeLineToFile(line, fileWriterToConfigure, true);
			writeLineToFile(line, secondFileWriterToConfigure, true);
		}

		line=String.format("impala.data.%s.table.field.epochtime=date_time_unix",dataSourceName);
		writeLineToFile(line,fileWriterToConfigure,true);
		writeLineToFile(line,secondFileWriterToConfigure,true);

		line = String.format("########### Data Schema");
		writeLineToFile(line,fileWriterToConfigure,true);

		line=String.format("impala.%s.have.data=true",dataSourceName);
		writeLineToFile(line,fileWriterToConfigure,true);

		line = String.format("impala.data.%s.table.delimiter=%s",dataSourceName,configurationParams.get("dataDelimiter").getParamValue());
		writeLineToFile(line,fileWriterToConfigure,true);

		line = String.format("impala.data.%s.table.name=%s",dataSourceName,configurationParams.get("dataTableName").getParamValue());
		writeLineToFile(line,fileWriterToConfigure,true);


		//Static configuration
		//TODO - DOES WE NEED TO PUT IT OUT TO BE DYNAMIC??
		//hdfs paths
		line = String.format("hdfs.user.data.%s.path=${hdfs.user.data.path}/%s",dataSourceName,dataSourceName);
		writeLineToFile(line,fileWriterToConfigure,true);


		//hdfs retention
		line = String.format("hdfs.user.data.%s.retention=90",dataSourceName);
		writeLineToFile(line,fileWriterToConfigure,true);

		//is sensitive machien field
		line = String.format("impala.data.%s.table.field.is_sensitive_machine=%s",dataSourceName,configurationParams.get("sensitive_machine").getParamValue());
		writeLineToFile(line,fileWriterToConfigure,true);
		writeLineToFile(line,secondFileWriterToConfigure,true);

		//partition type
		line = String.format("impala.data.%s.table.partition.type=monthly",dataSourceName);
		writeLineToFile(line,fileWriterToConfigure,true);

		line=String.format("impala.data.%s.table.fields=%s",dataSourceName,configurationParams.get("dataFields").getParamValue());
		writeLineToFile(line,fileWriterToConfigure,true);


		writeLineToFile("\n",fileWriterToConfigure,true);
		writeLineToFile("\n",secondFileWriterToConfigure,true);


		//Enrich fields
		line = String.format("########### Enrich Schema");
		writeLineToFile(line,fileWriterToConfigure,true);
		writeLineToFile(line,secondFileWriterToConfigure,true);

		line=String.format("impala.%s.have.enrich=true",dataSourceName);
		writeLineToFile(line,fileWriterToConfigure,true);

		line = String.format("impala.enricheddata.%s.table.fields=%s",dataSourceName,configurationParams.get("enrichFields").getParamValue());
		writeLineToFile(line,fileWriterToConfigure,true);
		writeLineToFile(line,secondFileWriterToConfigure,true);

		line = String.format("impala.enricheddata.%s.table.delimiter=%s",dataSourceName,configurationParams.get("enrichDelimiter").getParamValue());
		writeLineToFile(line,fileWriterToConfigure,true);
		writeLineToFile(line,secondFileWriterToConfigure,true);

		line = String.format("impala.enricheddata.%s.table.name=%s",dataSourceName,configurationParams.get("enrichTableName").getParamValue());
		writeLineToFile(line,fileWriterToConfigure,true);
		writeLineToFile(line,secondFileWriterToConfigure,true);


		//TODO - DOES WE NEED TO PUT IT OUT TO BE DYNAMIC??
		//hdfs path
		line = String.format("hdfs.user.enricheddata.%s.path=${hdfs.user.enricheddata.path}/%s",dataSourceName,dataSourceName);
		writeLineToFile(line,fileWriterToConfigure,true);
		writeLineToFile(line,secondFileWriterToConfigure,true);

		//hdfs retention
		line = String.format("hdfs.user.enricheddata.%s.retention=90",dataSourceName);
		writeLineToFile(line,fileWriterToConfigure,true);
		writeLineToFile(line,secondFileWriterToConfigure,true);

		//hdfs file name
		line = String.format("hdfs.enricheddata.%s.file.name=${impala.enricheddata.%s.table.name}.csv",dataSourceName,dataSourceName);
		writeLineToFile(line,fileWriterToConfigure,true);
		writeLineToFile(line,secondFileWriterToConfigure,true);

		//partition strategy
		line = String.format("impala.enricheddata.%s.table.partition.type=daily",dataSourceName);
		writeLineToFile(line,fileWriterToConfigure,true);
		writeLineToFile(line,secondFileWriterToConfigure,true);

		//Score
		line = String.format("########### Score Schema");
		writeLineToFile(line,fileWriterToConfigure,true);
		writeLineToFile(line,secondFileWriterToConfigure,true);


		//fields
		line = String.format("impala.score.%s.table.fields=%s",dataSourceName,configurationParams.get("scoreFields").getParamValue());
		writeLineToFile(line,fileWriterToConfigure,true);
		writeLineToFile(line,secondFileWriterToConfigure,true);

		line = String.format("impala.score.%s.table.delimiter=%s",dataSourceName,configurationParams.get("scoreDelimiter").getParamValue());
		writeLineToFile(line,fileWriterToConfigure,true);
		writeLineToFile(line,secondFileWriterToConfigure,true);


		line = String.format("impala.score.%s.table.name=%s",dataSourceName,configurationParams.get("scoreTableName").getParamValue());
		writeLineToFile(line,fileWriterToConfigure,true);
		writeLineToFile(line,secondFileWriterToConfigure,true);



		//TODO - DOES WE NEED TO PUT IT OUT TO BE DYNAMIC??
		//hdfs path
		line = String.format("hdfs.user.processeddata.%s.path=${hdfs.user.processeddata.path}/%s",dataSourceName,dataSourceName);
		writeLineToFile(line,fileWriterToConfigure,true);
		writeLineToFile(line,secondFileWriterToConfigure,true);

		//hdfs retention
		line = String.format("hdfs.user.processeddata.%s.retention=90",dataSourceName);
		writeLineToFile(line,fileWriterToConfigure,true);
		writeLineToFile(line,secondFileWriterToConfigure,true);


		//partition strategy
		line=String.format("impala.score.%s.table.partition.type=daily",dataSourceName);
		writeLineToFile(line,fileWriterToConfigure,true);
		writeLineToFile(line,secondFileWriterToConfigure,true);

		//Top Score schema
		if(configurationParams.containsKey("topSchemaFlag") && configurationParams.get("topSchemaFlag").getParamFlag())
		{
			line = String.format("########### Top Score Schema");
			writeLineToFile(line,fileWriterToConfigure,true);
			writeLineToFile(line,secondFileWriterToConfigure,true);

			line=String.format("impala.%s.have.topScore=true",dataSourceName);
			writeLineToFile(line,fileWriterToConfigure,true);


			//fields
			line = String.format("impala.score.%s.top.table.fields=%s",dataSourceName,configurationParams.get("scoreFields").getParamValue());
			writeLineToFile(line,fileWriterToConfigure,true);
			writeLineToFile(line,secondFileWriterToConfigure,true);

			line = String.format("impala.score.%s.top.table.delimiter=%s",dataSourceName,configurationParams.get("scoreDelimiter").getParamValue());
			writeLineToFile(line,fileWriterToConfigure,true);
			writeLineToFile(line,secondFileWriterToConfigure,true);


			line = String.format("impala.score.%s.top.table.name=%s",dataSourceName,configurationParams.get("scoreTableName").getParamValue());
			writeLineToFile(line,fileWriterToConfigure,true);
			writeLineToFile(line,secondFileWriterToConfigure,true);



			//TODO - DOES WE NEED TO PUT IT OUT TO BE DYNAMIC??
			//hdfs path
			line = String.format("hdfs.user.processeddata.%s.top.path=${hdfs.user.processeddata.path}/%s",dataSourceName,dataSourceName);
			writeLineToFile(line,fileWriterToConfigure,true);
			writeLineToFile(line,secondFileWriterToConfigure,true);

			//hdfs retention
			line = String.format("hdfs.user.processeddata.%s.top.retention=90",dataSourceName);
			writeLineToFile(line,fileWriterToConfigure,true);
			writeLineToFile(line,secondFileWriterToConfigure,true);


			//partition strategy
			line=String.format("impala.score.%s.top.table.partition.type=daily",dataSourceName);
			writeLineToFile(line,fileWriterToConfigure,true);
			writeLineToFile(line,secondFileWriterToConfigure,true);

		}

		line=String.format("%s.EventsJoiner.ttl=86400",dataSourceName);
		writeLineToFile(line,fileWriterToConfigure,true);

		line=String.format("kafka.%s.message.record.field.data_source=data_source",dataSourceName);
		writeLineToFile(line,fileWriterToConfigure,true);

		line=String.format("kafka.%s.message.record.field.last_state=last_state",dataSourceName);
		writeLineToFile(line,fileWriterToConfigure,true);

		line=String.format("kafka.%s.message.record.fields = ${impala.data.%s.table.fields},${kafka.%s.message.record.field.data_source},${kafka.%s.message.record.field.last_state}",dataSourceName,dataSourceName,dataSourceName,dataSourceName);
		writeLineToFile(line,fileWriterToConfigure,true);

		line=String.format("kafka.%s.message.record.fields = ${impala.data.%s.table.fields},${kafka.%s.message.record.field.data_source},${kafka.%s.message.record.field.last_state}",dataSourceName,dataSourceName,dataSourceName,dataSourceName);
		writeLineToFile(line,fileWriterToConfigure,true);


		line="read"+dataSourceName.toUpperCase()+".morphline=file:resources/conf-files/processread"+dataSourceName.toUpperCase()+".conf";
		writeLineToFile(line,fileWriterToConfigure,true);

		secondFileWriterToConfigure.flush();
		fileWriterToConfigure.flush();


		result = true;


		return result;

	}

	@Override
	public Boolean Done() {


		if (secondFileWriterToConfigure != null) {
			try {
				secondFileWriterToConfigure.close();
			} catch (IOException exception) {
				logger.error("There was an exception during the file - {} closing  , cause - {} ", secoundFileToConfigure.getName(), exception.getMessage());
				System.out.println(String.format("There was an exception during execution please see more info at the log "));
				return false;

			}

		}

		return super.Done();
	}


}
