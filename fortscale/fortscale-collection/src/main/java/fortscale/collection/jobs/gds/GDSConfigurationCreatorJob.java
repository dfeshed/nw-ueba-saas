package fortscale.collection.jobs.gds;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.utils.logging.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by idanp on 12/1/2015.
 */
public class GDSConfigurationCreatorJob extends FortscaleJob {

	private static Logger logger = Logger.getLogger(GDSConfigurationCreatorJob.class);

	private static final int MAX_NUM_OF_INPUT_RETRIES = 5;

	private GDSInputHandler gdsInputHandler = new GDSStandardInputHandler();

	Map<String, GDSConfigurator> configuratorsMap = new HashMap<>();

	private boolean isBaseDataSourceConfigurationDefined;

	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		logger.debug("Initializing Configuration GDS Job");
		logger.debug("Job Initialized");
	}

	@Override
	protected void runSteps() throws Exception {
		logger.debug("Running Configuration Generic Data Source Tool");

		handleMainMenuChoice();

		gdsInputHandler.close();
	}

	private void handleMainMenuChoice() throws Exception {
		GDSMenuPrintHelper.printMainMenu(true);

		System.out.println("Please enter your choice:");
		String stepInput = gdsInputHandler.getInput();

		dispatchConfiguratorByInput(stepInput);
	}

	private void dispatchConfiguratorByInput(String stepInput) throws Exception {

		GDSConfigurator gdsConfigurator = getGdsConfigurator(stepInput);

		if (gdsConfigurator != null) {
			gdsConfigurator.configure();
		}
		else {
			System.exit(1);
		}
	}

	private GDSConfigurator getGdsConfigurator(String stepInput) throws Exception {
		int numOfInputRetries = MAX_NUM_OF_INPUT_RETRIES;

		GDSConfigurationState gdsConfigurationState = new GDSConfigurationState();

		while (numOfInputRetries > 0) {
			String stepInputNormalized = stepInput.trim();
			switch (stepInputNormalized) {
				case "1":
					configuratorsMap.putIfAbsent(stepInputNormalized, new GDSInitConfigurator(gdsConfigurationState));
				case "2":
					configuratorsMap.putIfAbsent(stepInputNormalized, new GDSCollectionConfigurator(gdsConfigurationState));
					configuratorsMap.get(stepInputNormalized).configure();
				case "3":
					configuratorsMap.putIfAbsent(stepInputNormalized, new GDSStreamingConfigurator(gdsConfigurationState));
					configuratorsMap.get(stepInputNormalized).configure();
				case "4":
					throw new UnsupportedOperationException("Operation not supported yet");
				case "5":
					throw new UnsupportedOperationException("Operation is not support yet");
				case "6":
					System.exit(0);
					break;
				default: // illegal operation
					GDSMenuPrintHelper.printMainMenu(false);

					System.out.println("Illegal input. Please enter your choice [1-6]:");
					stepInput = gdsInputHandler.getInput();
					numOfInputRetries--;
					break;
			}
		}

		return null;
	}

	@Override
	protected int getTotalNumOfSteps() { return 1; }

	@Override
	protected boolean shouldReportDataReceived() { return false; }
}
