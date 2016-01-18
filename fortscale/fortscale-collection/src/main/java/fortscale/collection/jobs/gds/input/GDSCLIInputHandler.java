package fortscale.collection.jobs.gds.input;

import fortscale.services.configuration.ConfigurationParam;
import fortscale.utils.ConversionUtils;
import fortscale.utils.logging.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Command line implementation of Generic data source Input handler
 *
 * @author gils
 * 30/12/2015
 */
public class GDSCLIInputHandler implements GDSInputHandler {
    private static Logger logger = Logger.getLogger(GDSCLIInputHandler.class);

    private BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

    private static Set<String> legalYesNoInputVariations = new HashSet<>(Arrays.asList(new String[]{"yes", "y", "no", "n"}));

    @Override
    public boolean getYesNoInput() throws Exception {
        String input = bufferedReader.readLine();

        while (!legalYesNoInputVariations.contains(input.toLowerCase())) {
            System.out.println("Illegal input. Please enter (y)es / (n)o");
            input = bufferedReader.readLine();
        }

        return input.equals("yes") || input.equals("y");

    }

    @Override
    public String getInput() throws IOException {
        return bufferedReader.readLine();
    }

    @Override
    public String getInput(Set<String> allowedValues) throws IOException {
        String input = bufferedReader.readLine();

        while (!allowedValues.contains(input.toLowerCase()) && !allowedValues.contains(input.toUpperCase())) {
            System.out.println("Illegal input. Please enter one of the following values: " + allowedValues);
            input = bufferedReader.readLine();
        }

        return input;
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

	public Map<String,String> splitCSVtoMap (String CSVfield)
	{
		Map<String,String> result = new LinkedHashMap<>();
		List<String> csvAsList = ConversionUtils.convertCSVToList(CSVfield, ",");
		for (String keyValuePair : csvAsList)
		{
			Map<String,String> res;
			res  = ConversionUtils.convertCSVToMap(keyValuePair," ");
			result.putAll(res);
		}

		return result;

	}
}
