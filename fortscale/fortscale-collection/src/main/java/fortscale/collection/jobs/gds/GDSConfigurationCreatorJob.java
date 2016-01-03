package fortscale.collection.jobs.gds;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.collection.jobs.gds.configurators.GDSConfiguratorFactory;
import fortscale.collection.jobs.gds.configurators.GDSConfiguratorType;
import fortscale.collection.jobs.gds.helper.GDSUserInputHelper;
import fortscale.collection.jobs.gds.state.GDSConfigurationState;
import fortscale.utils.logging.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by idanp on 12/1/2015.
 */
public class GDSConfigurationCreatorJob extends FortscaleJob {

	private static Logger logger = Logger.getLogger(GDSConfigurationCreatorJob.class);

	private GDSInputHandler gdsInputHandler = new GDSStandardInputHandler();

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
		GDSMenuPrinterHelper.printMainMenu(true);

		System.out.println("Please enter your choice:");
		String stepInput = gdsInputHandler.getInput();

		handleConfiguration(stepInput);
	}

	private void handleConfiguration(String stepInput) throws Exception {

		GDSConfigurationState gdsConfigurationState = new GDSConfigurationState();

		GDSConfiguratorFactory gdsConfiguratorFactory = new GDSConfiguratorFactory();

		Map<String, GDSConfiguratorType> mainMenuOptionToConfigurationType = createMainMenuOptionToConfigurationType();

		boolean illegalInput = false;

		while (true) {
			String stepInputNormalized = stepInput.trim();
			switch (stepInputNormalized) {
				case "1":
				case "2":
					GDSConfigurator configurator = gdsConfiguratorFactory.getConfigurator(mainMenuOptionToConfigurationType.get(stepInputNormalized));
					configurator.init(gdsConfigurationState);
					configurator.configure();
					System.out.println("Finished to configure. Do you want to apply changes now? (y/n)");

					if (GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput())) {
						configurator.apply();
					}

					System.out.println("Do you want to reset changes? (y/n)");

					if (GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput())) {
						configurator.reset();
					}
					break;
				case "3":
					GDSMenuPrinterHelper.printEnrichmentMenu();
					handleEnrichmentConfiguration(gdsConfigurationState);
					break;
				case "4":
					applyConfigurations();
					break;
				case "5":
					resetConfigurations(gdsConfigurationState);
					break;
				case "6":
					System.exit(0);
					break;
				default: // illegal operation
					illegalInput = true;
					break;
			}

			if (!illegalInput) {
				GDSMenuPrinterHelper.printNextMainMenu();
			}
			else {
				GDSMenuPrinterHelper.printMainMenuAfterFailure();
			}
			stepInput = gdsInputHandler.getInput();
		}
	}

	private Map<String, GDSConfiguratorType> createMainMenuOptionToConfigurationType() {
		Map<String, GDSConfiguratorType> mainMenuOptionToConfigurationType = new HashMap<>();

		mainMenuOptionToConfigurationType.put("1", GDSConfiguratorType.SCHEMA);
		mainMenuOptionToConfigurationType.put("2", GDSConfiguratorType.COLLECTION);
		mainMenuOptionToConfigurationType.put("3", GDSConfiguratorType.ENRICHMENT);

		return mainMenuOptionToConfigurationType;
	}

	private Map<String, GDSConfiguratorType> createEnrichmentMenuOptionToConfigurationType() {
		Map<String, GDSConfiguratorType> enrichmentMenuOptionToConfigurationType = new HashMap<>();

		return enrichmentMenuOptionToConfigurationType;
	}

	private void handleEnrichmentConfiguration(GDSConfigurationState gdsConfigurationState) throws Exception {
		System.out.println("Please enter your choice:");
		String stepInput = gdsInputHandler.getInput();

		GDSConfiguratorFactory gdsConfiguratorFactory = new GDSConfiguratorFactory();
		Map<String, GDSConfiguratorType> enrichmentMenuOptionToConfigurationType = createEnrichmentMenuOptionToConfigurationType();

		boolean illegalInput = false;

		while (true) {
			String stepInputNormalized = stepInput.trim();
			switch (stepInputNormalized) {
				case "1":
				case "2":
				case "3":
				case "4":
				case "5":
				case "6":
					GDSConfigurator configurator = gdsConfiguratorFactory.getConfigurator(enrichmentMenuOptionToConfigurationType.get(stepInputNormalized));
					configurator.init(gdsConfigurationState);
					configurator.configure();
					break;
				default: // illegal operation
					illegalInput = true;
					break;
			}

			if (!illegalInput) {
				GDSMenuPrinterHelper.printNextEnrichmentMenu();
			}
			else {
				GDSMenuPrinterHelper.printEnrichmentMenuAfterFailure();
			}
			stepInput = gdsInputHandler.getInput();
		}
	}

	private void resetConfigurations(GDSConfigurationState gdsConfigurationState) {
		gdsConfigurationState.reset();

		System.out.println("Configuration was reset successfully.");
	}

	private void applyConfigurations() throws Exception {
		List<GDSConfigurator> dirtyConfigurators = findDirtyConfigurations();
		for (GDSConfigurator dirtyConfigurator : dirtyConfigurators) {
			dirtyConfigurator.apply();
		}
	}

	private List<GDSConfigurator> findDirtyConfigurations() {
		return null; // TODO implement
	}

	@Override
	protected int getTotalNumOfSteps() { return 1; }

	@Override
	protected boolean shouldReportDataReceived() { return false; }
}
