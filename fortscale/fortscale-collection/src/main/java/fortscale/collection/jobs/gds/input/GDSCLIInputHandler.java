package fortscale.collection.jobs.gds.input;

import fortscale.utils.logging.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
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
}
