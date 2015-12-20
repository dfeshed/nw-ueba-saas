package fortscale.services.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * Created by idanp on 12/20/2015.
 */
public abstract class ConfigurationService {

	protected static Logger logger = LoggerFactory.getLogger(ConfigurationService.class);
	protected static final String root = System.getProperty("user.home");

	protected String fileToConfigurePath;
	protected File fileToConfigure;
	protected FileWriter fileWriterToConfigure;
	protected Map<String,ConfigurationParam> configurationParams;




    public abstract Boolean Configure() throws Exception;
	public abstract Boolean Init();

	public  Boolean Done(){
        Boolean result = true;
        if (fileWriterToConfigure != null) {
            try {
                fileWriterToConfigure.close();
            } catch (IOException exception) {
                logger.error("There was an exception during the file - {} closing  , cause - {} ", fileToConfigure.getName(), exception.getMessage());
                System.out.println(String.format("There was an exception during execution please see more info at the log "));
                result=false;

            }

        }
        return result;
    }


	/**
	 * Private method that will write line into a given file
	 * @param line -  the line to write
	 * @param writer - the write of the file
	 * @param withNewLine - flag that will sign if need to add new line
	 */
	protected void writeLineToFile(String line, FileWriter writer, boolean withNewLine) throws Exception{
		try {
			writer.write(line);
			if (withNewLine)
				writer.write("\n");
		}

		catch (Exception e)
		{
			logger.error("There was an exception during the execution - {}",e.getMessage());
			System.out.println(String.format("There was an exception during execution please see more info at the log "));
			throw new Exception(e.getMessage());
		}
	}
}
