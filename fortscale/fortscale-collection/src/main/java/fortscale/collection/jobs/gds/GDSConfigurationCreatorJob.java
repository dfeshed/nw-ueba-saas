package fortscale.collection.jobs.gds;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.utils.logging.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by idanp on 12/1/2015.
 */
public class GDSConfigurationCreatorJob extends FortscaleJob {

	private static Logger logger = Logger.getLogger(GDSConfigurationCreatorJob.class);

	private static final int MAX_NUM_OF_INPUT_RETRIES = 5;

	private GDSInputHandler gdsInputHandler = new GDSStandardInputHandler();

	Map<String, GDSConfigurator> configuratorsMap = new HashMap<>();

	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		logger.debug("Initializing Configuration GDS Job");
		logger.debug("Job Initialized");
	}

	@Override
	protected void runSteps() throws Exception {
		logger.debug("Running Configuration Generic Data Source Tool");

		handleMainMenu();

		gdsInputHandler.close();
	}

	private void handleMainMenu() throws Exception {
		GDSMenuPrintHelper.printMainMenu(true);

		System.out.println("Please enter your choice:");
		String stepInput = gdsInputHandler.getInput();

		handleConfiguration(stepInput);
	}

	private void handleConfiguration(String stepInput) throws Exception {

		GDSConfigurationState gdsConfigurationState = new GDSConfigurationState();

		while (true) {
			String stepInputNormalized = stepInput.trim();
			switch (stepInputNormalized) {
				case "1":
					configuratorsMap.putIfAbsent(stepInputNormalized, new GDSInitConfigurator(gdsConfigurationState));
					configuratorsMap.get(stepInputNormalized).configure();
					break;
				case "2":
					configuratorsMap.putIfAbsent(stepInputNormalized, new GDSCollectionConfigurator(gdsConfigurationState));
					configuratorsMap.get(stepInputNormalized).configure();
					break;
				case "3":
					configuratorsMap.putIfAbsent(stepInputNormalized, new GDSStreamingConfigurator(gdsConfigurationState));
					configuratorsMap.get(stepInputNormalized).configure();
					break;
				case "4":
					applyDirtyConfigurations();
					break;
				case "5":
					revertDirtyConfigurations(gdsConfigurationState);
					break;
				case "6":
					System.exit(0);
					break;
				default: // illegal operation
					GDSMenuPrintHelper.printMainMenu(false);

					System.out.println("Illegal input. Please enter your choice [1-6]:");
					stepInput = gdsInputHandler.getInput();

					break;
			}
		}
	}

	private void revertDirtyConfigurations(GDSConfigurationState gdsConfigurationState) {
		gdsConfigurationState.clear();
	}

	private void applyDirtyConfigurations() throws Exception {
		List<GDSConfigurator> dirtyConfigurators = findDirtyConfigurations();
		for (GDSConfigurator dirtyConfigurator : dirtyConfigurators) {
			dirtyConfigurator.apply();
        }
	}

	private List<GDSConfigurator> findDirtyConfigurations() {
		return new ArrayList<>(configuratorsMap.values());
	}

	@Override
	protected int getTotalNumOfSteps() { return 1; }

	@Override
	protected boolean shouldReportDataReceived() { return false; }
}
