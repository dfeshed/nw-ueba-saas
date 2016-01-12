package fortscale.collection.jobs.gds.input;

import fortscale.services.configuration.ConfigurationParam;
import fortscale.utils.logging.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;

/**
 * Command line implementation of Generic data source Input handler
 *
 * @author gils
 * 30/12/2015
 */
public class GDSCLIInputHandler implements GDSInputHandler {
    private static Logger logger = Logger.getLogger(GDSCLIInputHandler.class);

    private BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

    @Override
    public String getInput() throws IOException {
        return bufferedReader.readLine();
    }

    public String getInput(String paramName) throws IOException {
        return getInput();
    }

    @Override
    public Map<String, String> getInput(Set<String> paramNames) {
        // TODO implement
        return null;
    }

    @Override
    public void close() {
        try {
            bufferedReader.close();
        } catch (IOException e) {
            logger.error("Could not close input stream reader");
        }
    }

	public ConfigurationParam getParamConfiguration (Map<String,ConfigurationParam> configurationParams, String key)
	{
		if (configurationParams.containsKey(key))
			return configurationParams.get(key);
		return null;
	}
}
