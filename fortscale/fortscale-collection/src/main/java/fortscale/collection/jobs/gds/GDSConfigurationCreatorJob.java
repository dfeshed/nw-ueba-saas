package fortscale.collection.jobs.gds;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.collection.jobs.gds.configurators.GDSConfiguratorFactory;
import fortscale.collection.jobs.gds.configurators.GDSConfiguratorType;
import fortscale.collection.jobs.gds.helper.GDSMenuPrinterHelper;
import fortscale.collection.jobs.gds.helper.GDSUserInputHelper;
import fortscale.collection.jobs.gds.populators.enrichment.GDSConfigurationPopulator;
import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.state.GDSCompositeConfigurationState;
import fortscale.utils.logging.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generic Data Source configuration creator - job implementation
 *
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
		String optionInput = gdsInputHandler.getInput();

		handleMainMenuUserInput(optionInput);
	}

	private void handleMainMenuUserInput(String optionInput) throws Exception {

		GDSCompositeConfigurationState currConfigurationState = new GDSCompositeConfigurationState();

		GDSConfiguratorFactory gdsConfiguratorFactory = new GDSConfiguratorFactory();

		Map<String, GDSConfiguratorType> mainMenuOptionToConfigurationType = createMainMenuOptionToConfigurationType();

		while (true) {
			String inputErrorMessage = null;

			String stepInputNormalized = optionInput.trim();
			switch (stepInputNormalized) {
				case "1":
				case "2":
					GDSConfiguratorType gdsConfiguratorType = mainMenuOptionToConfigurationType.get(stepInputNormalized);

					GDSConfigurationPopulatorFactory gdsConfigurationPopulatorFactory = new GDSConfigurationPopulatorFactory();
					GDSConfigurationPopulator configurationPopulator = gdsConfigurationPopulatorFactory.getConfigurationPopulator(gdsConfiguratorType);

					Map<String, ConfigurationParam> configurationParams = configurationPopulator.populateConfigurationData(currConfigurationState);

					if (configurationParams.isEmpty()) {
						System.out.println("Configuration does not contain any changes to be applied.");
					}
					else {
						GDSConfigurator configurator = gdsConfiguratorFactory.getConfigurator(gdsConfiguratorType);
						currConfigurationState = configurator.configure(configurationParams);

						System.out.println("Finished to configure. Do you want to apply changes now? (y/n)");

						if (GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput())) {
							configurator.apply();
						}

						System.out.println("Do you want to reset changes? (y/n)");

						if (GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput())) {
							configurator.reset();
						}
					}
					break;
				case "3":
					if (canEnterEnrichmentPhase(currConfigurationState)) {
						GDSMenuPrinterHelper.printEnrichmentMenu();
						handleEnrichmentConfiguration(currConfigurationState);
					}
					else {
						inputErrorMessage = "Could not enter enrichment phase. Schema must be defined first.";
					}
					break;
				case "4":
					applyConfigurations();
					break;
				case "5":
					resetConfigurations(currConfigurationState);
					break;
				case "6":
					System.exit(0);
					break;
				default: // illegal operation
					inputErrorMessage = "Illegal operation.";
					break;
			}

			if (inputErrorMessage == null) {
				GDSMenuPrinterHelper.printNextMainMenu();
			}
			else {
				GDSMenuPrinterHelper.printMainMenuAfterFailure(inputErrorMessage);
			}

			optionInput = gdsInputHandler.getInput();
		}
	}

	private boolean canEnterEnrichmentPhase(GDSCompositeConfigurationState currConfigurationState) {
		return currConfigurationState.isDataSourceAlreadyDefined();

	}

	private Map<String, GDSConfiguratorType> createMainMenuOptionToConfigurationType() {
		Map<String, GDSConfiguratorType> mainMenuOptionToConfigurationType = new HashMap<>();

		mainMenuOptionToConfigurationType.put("1", GDSConfiguratorType.SCHEMA);
		mainMenuOptionToConfigurationType.put("2", GDSConfiguratorType.COLLECTION);

		return mainMenuOptionToConfigurationType;
	}

	private void resetConfigurations(GDSCompositeConfigurationState gdsConfigurationState) {
		gdsConfigurationState.reset();

		System.out.println("Configuration was reset successfully.");
	}

	private void applyConfigurations() throws Exception {
		List<GDSConfigurator> dirtyConfigurators = findDirtyConfigurations();
		for (GDSConfigurator dirtyConfigurator : dirtyConfigurators) {
			dirtyConfigurator.apply();
		}
	}

	private Map<String, GDSConfiguratorType> createEnrichmentMenuOptionToConfigurationType() {
		Map<String, GDSConfiguratorType> enrichmentMenuOptionToConfigurationType = new HashMap<>();

		enrichmentMenuOptionToConfigurationType.put("1", GDSConfiguratorType.USER_NORMALIZATION);
		enrichmentMenuOptionToConfigurationType.put("2", GDSConfiguratorType.IP_RESOLVING);
		enrichmentMenuOptionToConfigurationType.put("3", GDSConfiguratorType.COMPUTER_TAGGING);
		enrichmentMenuOptionToConfigurationType.put("4", GDSConfiguratorType.GEO_LOCATION);
		enrichmentMenuOptionToConfigurationType.put("5", GDSConfiguratorType.USER_MONGO_UPDATE);
		enrichmentMenuOptionToConfigurationType.put("6", GDSConfiguratorType.HDFS_WRITE);

		return enrichmentMenuOptionToConfigurationType;
	}

	private void handleEnrichmentConfiguration(GDSCompositeConfigurationState currConfigurationState) throws Exception {
		System.out.println("Please enter your choice:");
		String stepInput = gdsInputHandler.getInput();

		GDSConfiguratorFactory gdsConfiguratorFactory = new GDSConfiguratorFactory();
		Map<String, GDSConfiguratorType> enrichmentMenuOptionToConfigurationType = createEnrichmentMenuOptionToConfigurationType();

		while (true) {
			String inputErrorMessage = null;

			String stepInputNormalized = stepInput.trim();
			switch (stepInputNormalized) {
				case "1":
				case "2":
				case "3":
				case "4":
				case "5":
				case "6":
					GDSConfiguratorType gdsConfiguratorType = enrichmentMenuOptionToConfigurationType.get(stepInputNormalized);

					GDSConfigurationPopulatorFactory gdsConfigurationPopulatorFactory = new GDSConfigurationPopulatorFactory();
					GDSConfigurationPopulator configurationPopulator = gdsConfigurationPopulatorFactory.getConfigurationPopulator(gdsConfiguratorType);

					Map<String, ConfigurationParam> configurationParams = configurationPopulator.populateConfigurationData(currConfigurationState);

					if (configurationParams.isEmpty()) {
						System.out.println("Configuration does not contain any changes to be applied.");
					}
					else {

						GDSConfigurator configurator = gdsConfiguratorFactory.getConfigurator(gdsConfiguratorType);
						currConfigurationState = configurator.configure(configurationParams);

						System.out.println("Finished to configure. Do you want to apply changes now? (y/n)");

						if (GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput())) {
							configurator.apply();
						}

						System.out.println("Do you want to reset changes? (y/n)");

						if (GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput())) {
							configurator.reset();
						}
					}

					break;
				case "7":
					applyConfigurations();
					break;
				case "8":
					resetConfigurations(currConfigurationState);
					break;
				case "9":
					System.exit(0);
					break;
				default: // illegal operation
					inputErrorMessage = "Illegal operation.";
					break;
			}

			if (inputErrorMessage == null) {
				GDSMenuPrinterHelper.printNextEnrichmentMenu();
			}
			else {
				GDSMenuPrinterHelper.printEnrichmentMenuAfterFailure(inputErrorMessage);
			}
			stepInput = gdsInputHandler.getInput();
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
