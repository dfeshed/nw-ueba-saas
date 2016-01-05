package fortscale.collection.jobs.gds;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.collection.jobs.gds.configurators.GDSConfigurator;
import fortscale.collection.jobs.gds.configurators.GDSConfiguratorFactory;
import fortscale.collection.jobs.gds.helper.GDSMenuOptionsConsts;
import fortscale.collection.jobs.gds.helper.GDSMenuPrinterHelper;
import fortscale.collection.jobs.gds.helper.GDSUserInputHelper;
import fortscale.collection.jobs.gds.input.GDSCLIInputHandler;
import fortscale.collection.jobs.gds.input.GDSInputHandler;
import fortscale.collection.jobs.gds.input.populators.GDSConfigurationPopulatorFactory;
import fortscale.collection.jobs.gds.input.populators.enrichment.GDSConfigurationPopulator;
import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.gds.state.GDSCompositeConfigurationState;
import fortscale.utils.logging.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.*;

/**
 * Generic Data Source configuration creator job implementation
 *
 * Created by idanp on 12/1/2015.
 */
public class GDSConfigurationCreatorJob extends FortscaleJob {

	private static Logger logger = Logger.getLogger(GDSConfigurationCreatorJob.class);

	private GDSInputHandler gdsInputHandler = new GDSCLIInputHandler();

	private GDSCompositeConfigurationState currConfigurationState = new GDSCompositeConfigurationState();

	private GDSConfigurationPopulatorFactory gdsConfigurationPopulatorFactory = new GDSConfigurationPopulatorFactory();

	private GDSConfiguratorFactory gdsConfiguratorFactory = new GDSConfiguratorFactory();

	private Map<String, GDSConfigurationType> mainMenuOptionToConfigurationType = createMainMenuOptionToConfigurationType();

	private Map<String, GDSConfigurationType> enrichmentMenuOptionToConfigurationType = createEnrichmentMenuOptionToConfigurationType();

	private Queue<GDSConfigurator> dirtyConfiguratorsQueue = new LinkedList<>();

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

		while (true) {
			String inputErrorMessage = null;

			String userInputNormalized = optionInput.trim();
			switch (userInputNormalized) {
				case GDSMenuOptionsConsts.MAIN_MENU_SCHEMA_DEFINITION_OPTION:
					GDSConfigurationType gdsConfigurationType = mainMenuOptionToConfigurationType.get(userInputNormalized);

					GDSConfigurationPopulator configurationPopulator = gdsConfigurationPopulatorFactory.getConfigurationPopulator(gdsConfigurationType);

					Map<String, ConfigurationParam> configurationParams = configurationPopulator.populateConfigurationData(currConfigurationState); // TODO need to send immutable state

					if (configurationParams.isEmpty()) {
						System.out.println("Configuration does not contain any changes to be applied.");
					}
					else {
						GDSConfigurator configurator = gdsConfiguratorFactory.getConfigurator(gdsConfigurationType);
						configurator.setConfigurationState(currConfigurationState);
						currConfigurationState = configurator.configure(configurationParams);

						System.out.println("Finished to configure. Do you want to apply changes now? (y/n)");

						if (GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput())) {
							configurator.apply();
						}

						System.out.println("Do you want to reset changes? (y/n)");

						if (GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput())) {
							configurator.reset();
						}
						else {
							dirtyConfiguratorsQueue.add(configurator);
						}
					}
					break;
				case GDSMenuOptionsConsts.MAIN_MENU_ENRICHMENT_DEFINITION_OPTION:
					if (canEnterEnrichmentPhase(currConfigurationState)) {
						GDSMenuPrinterHelper.printEnrichmentMenu();
						handleEnrichmentConfiguration();
					}
					else {
						inputErrorMessage = "Could not enter enrichment phase. Schema must be defined first.";
					}
					break;
				case GDSMenuOptionsConsts.MAIN_MENU_APPLY_ALL_CHANGES_OPTION:
					applyAllDirtyConfigurators();
					break;
				case GDSMenuOptionsConsts.MAIN_MENU_RESET_ALL_CHANGES_OPTION:
					resetConfigurators(currConfigurationState);
					break;
				case GDSMenuOptionsConsts.MAIN_MENU_QUIT_OPTION:
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

	private Map<String, GDSConfigurationType> createMainMenuOptionToConfigurationType() {
		Map<String, GDSConfigurationType> mainMenuOptionToConfigurationType = new HashMap<>();

		mainMenuOptionToConfigurationType.put("1", GDSConfigurationType.SCHEMA);

		return mainMenuOptionToConfigurationType;
	}

	private void resetConfigurators(GDSCompositeConfigurationState gdsConfigurationState) {
		gdsConfigurationState.reset();
	}

	private void applyAllDirtyConfigurators() throws Exception {
		Iterator<GDSConfigurator> gdsConfiguratorIterator = dirtyConfiguratorsQueue.iterator();
		while (gdsConfiguratorIterator.hasNext()) {
			GDSConfigurator gdsDirtyConfigurator = gdsConfiguratorIterator.next();

			gdsDirtyConfigurator.apply();

			gdsConfiguratorIterator.remove();
		}
	}

	private Map<String, GDSConfigurationType> createEnrichmentMenuOptionToConfigurationType() {
		Map<String, GDSConfigurationType> enrichmentMenuOptionToConfigurationType = new HashMap<>();

		enrichmentMenuOptionToConfigurationType.put("1", GDSConfigurationType.USER_NORMALIZATION);
		enrichmentMenuOptionToConfigurationType.put("2", GDSConfigurationType.IP_RESOLVING);
		enrichmentMenuOptionToConfigurationType.put("3", GDSConfigurationType.COMPUTER_TAGGING);
		enrichmentMenuOptionToConfigurationType.put("4", GDSConfigurationType.GEO_LOCATION);
		enrichmentMenuOptionToConfigurationType.put("5", GDSConfigurationType.USER_MONGO_UPDATE);
		enrichmentMenuOptionToConfigurationType.put("6", GDSConfigurationType.HDFS_WRITER);

		return enrichmentMenuOptionToConfigurationType;
	}

	private void handleEnrichmentConfiguration() throws Exception {
		System.out.println("Please enter your choice:");
		String stepInput = gdsInputHandler.getInput();

		while (true) {
			String inputErrorMessage = null;

			String stepInputNormalized = stepInput.trim();
			switch (stepInputNormalized) {
				case GDSMenuOptionsConsts.ENRICHMENT_MENU_USER_NORMALIZATION_OPTION:
				case GDSMenuOptionsConsts.ENRICHMENT_IP_RESOLVING_OPTION:
				case GDSMenuOptionsConsts.ENRICHMENT_COMPUTER_TAGGING_OPTION:
				case GDSMenuOptionsConsts.ENRICHMENT_GEO_LOCATION_OPTION:
				case GDSMenuOptionsConsts.ENRICHMENT_MENU_USER_MONGO_UPDATE_OPTION:
				case GDSMenuOptionsConsts.ENRICHMENT_HDFS_WRITER_OPTION:
					GDSConfigurationType gdsConfiguratorType = enrichmentMenuOptionToConfigurationType.get(stepInputNormalized);

					GDSConfigurationPopulatorFactory gdsConfigurationPopulatorFactory = new GDSConfigurationPopulatorFactory();
					GDSConfigurationPopulator configurationPopulator = gdsConfigurationPopulatorFactory.getConfigurationPopulator(gdsConfiguratorType);

					Map<String, ConfigurationParam> configurationParams = configurationPopulator.populateConfigurationData(currConfigurationState);

					if (configurationParams.isEmpty()) {
						System.out.println("Configuration does not contain any changes to be applied.");
					}
					else {

						GDSConfigurator configurator = gdsConfiguratorFactory.getConfigurator(gdsConfiguratorType);
						configurator.setConfigurationState(currConfigurationState);
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
				case GDSMenuOptionsConsts.ENRICHMENT_APPLY_ALL_CHANGES_OPTION:
					applyAllDirtyConfigurators();
					System.out.println("All configuration changes applied successfully.");
					break;
				case GDSMenuOptionsConsts.ENRICHMENT_RESET_ALL_CHANGES_OPTION:
					resetConfigurators(currConfigurationState);
					System.out.println("Configuration was reset successfully.");
					break;
				case GDSMenuOptionsConsts.ENRICHMENT_EXIT_TO_MAIN_MENU_OPTION:
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

	@Override
	protected int getTotalNumOfSteps() { return 1; }

	@Override
	protected boolean shouldReportDataReceived() { return false; }
}
