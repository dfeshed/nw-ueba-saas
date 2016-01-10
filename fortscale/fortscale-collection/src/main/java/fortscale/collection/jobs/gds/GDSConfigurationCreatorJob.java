package fortscale.collection.jobs.gds;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.collection.jobs.gds.configurators.GDSConfigurator;
import fortscale.collection.jobs.gds.configurators.GDSConfiguratorFactory;
import fortscale.collection.jobs.gds.helper.GDSMenuOptions;
import fortscale.collection.jobs.gds.helper.GDSMenuPrinterHelper;
import fortscale.collection.jobs.gds.helper.GDSUserInputHelper;
import fortscale.collection.jobs.gds.helper.GDSUserMessages;
import fortscale.collection.jobs.gds.input.GDSCLIInputHandler;
import fortscale.collection.jobs.gds.input.GDSInputHandler;
import fortscale.collection.jobs.gds.input.populators.GDSConfigurationPopulatorFactory;
import fortscale.collection.jobs.gds.input.populators.enrichment.GDSConfigurationPopulator;
import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.gds.state.GDSCompositeConfigurationState;
import fortscale.utils.logging.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

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

	private Map<String, GDSConfigurationType> mainMenuOptionToConfigurationType = GDSMenuPrinterHelper.createMainMenuOptionToConfigurationType();

	private Map<String, GDSConfigurationType> enrichmentMenuOptionToConfigurationType = GDSMenuPrinterHelper.createEnrichmentMenuOptionToConfigurationType();

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
	}

	private void handleMainMenu() throws Exception {
		GDSMenuPrinterHelper.printMainMenu(true);

		System.out.println(GDSUserMessages.USER_INPUT_REQUEST_MESSAGE);
		String optionInput = gdsInputHandler.getInput();

		handleMainMenuUserInput(optionInput);
	}

	private void handleMainMenuUserInput(String optionInput) throws Exception {

		while (true) {
			String inputErrorMessage = null;

			String userInputNormalized = optionInput.trim();
			switch (userInputNormalized) {
				case GDSMenuOptions.MAIN_MENU_SCHEMA_DEFINITION_OPTION:
					GDSConfigurationType gdsConfigurationType = mainMenuOptionToConfigurationType.get(userInputNormalized);

					GDSConfigurationPopulator configurationPopulator = gdsConfigurationPopulatorFactory.getConfigurationPopulator(gdsConfigurationType);

					Map<String, Map<String, ConfigurationParam>> configurationParams = configurationPopulator.populateConfigurationData(currConfigurationState);// TODO need to send immutable state so that populators cannot change the state

					if (configurationParams.isEmpty()) {
						System.out.println(GDSUserMessages.NO_CONFIGURATION_CHANGES_DETECTED_MESSAGE);
					}
					else {
						GDSConfigurator configurator = gdsConfiguratorFactory.getConfigurator(gdsConfigurationType);
						configurator.setConfigurationState(currConfigurationState);
						configurator.configure(configurationParams);

						System.out.println(GDSUserMessages.APPLY_CONFIRMATION_MESSAGE);

						if (GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput())) {
							configurator.apply();

							break;
						}

						System.out.println(GDSUserMessages.RESET_CONFIRMATION_MESSAGE);

						if (GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput())) {
							configurator.reset();
						}
						else {
							dirtyConfiguratorsQueue.add(configurator);
						}
					}
					break;
				case GDSMenuOptions.MAIN_MENU_ENRICHMENT_DEFINITION_OPTION:
					if (canEnterEnrichmentStep(currConfigurationState)) {
						GDSMenuPrinterHelper.printEnrichmentMenu();
						handleEnrichmentConfiguration();
					}
					else {
						inputErrorMessage = GDSUserMessages.SCHEMA_IS_MANDATORY_MESSAGE;
					}
					break;
				case GDSMenuOptions.MAIN_MENU_MODEL_AND_SCORING_DEFINITION_OPTION:
					if (canEnterModelAndScoringPhase(currConfigurationState)) {
						GDSMenuPrinterHelper.printModelAndScoringMenu();
						handleModelAndScoringConfiguration();
					}
					else {
						inputErrorMessage = GDSUserMessages.SCHEMA_IS_MANDATORY_MESSAGE;
					}
					break;
				case GDSMenuOptions.MAIN_MENU_APPLY_ALL_CHANGES_OPTION:
					if (!dirtyConfiguratorsQueue.isEmpty()) {
						System.out.println(GDSUserMessages.APPLY_IN_PROGRESS_MESSAGE);
						applyAllDirtyConfigurators();
						System.out.println(GDSUserMessages.APPLY_COMPLETED_SUCCESSFULLY_MESSAGE);
					}
					else {
						System.out.println(GDSUserMessages.NOTHING_TO_APPLY_MESSAGE);
					}

					break;
				case GDSMenuOptions.MAIN_MENU_RESET_ALL_CHANGES_OPTION:
					if (!dirtyConfiguratorsQueue.isEmpty()) {
						System.out.println(GDSUserMessages.RESET_IN_PROGRESS_MESSAGE);
						resetConfigurators(currConfigurationState);
						System.out.println(GDSUserMessages.RESET_COMPLETED_SUCCESSFULLY_MESSAGE);
					}
					else {
						System.out.println(GDSUserMessages.IGNORING_RESET_OPERATION_MESSAGE);
					}
					break;
				case GDSMenuOptions.MAIN_MENU_QUIT_OPTION:
					System.exit(0);
					break;
				default: // illegal operation
					inputErrorMessage = GDSUserMessages.ILLEGAL_OPERATION_MESSAGE;
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

	private boolean canEnterEnrichmentStep(GDSCompositeConfigurationState currConfigurationState) {
		return currConfigurationState.isDataSourceAlreadyDefined();
	}

	private boolean canEnterModelAndScoringPhase(GDSCompositeConfigurationState currConfigurationState) {
		return currConfigurationState.isDataSourceAlreadyDefined();
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

	private void handleModelAndScoringConfiguration() throws Exception {
		// TBD
	}

	private void handleEnrichmentConfiguration() throws Exception {
		System.out.println(GDSUserMessages.USER_INPUT_REQUEST_MESSAGE);
		String stepInput = gdsInputHandler.getInput();

		while (true) {
			String inputErrorMessage = null;

			String stepInputNormalized = stepInput.trim();
			switch (stepInputNormalized) {
				case GDSMenuOptions.ENRICHMENT_MENU_USER_NORMALIZATION_OPTION:
				case GDSMenuOptions.ENRICHMENT_IP_RESOLVING_OPTION:
				case GDSMenuOptions.ENRICHMENT_COMPUTER_TAGGING_OPTION:
				case GDSMenuOptions.ENRICHMENT_GEO_LOCATION_OPTION:
				case GDSMenuOptions.ENRICHMENT_MENU_USER_MONGO_UPDATE_OPTION:
				case GDSMenuOptions.ENRICHMENT_HDFS_WRITER_OPTION:
					GDSConfigurationType gdsConfiguratorType = enrichmentMenuOptionToConfigurationType.get(stepInputNormalized);

					GDSConfigurationPopulatorFactory gdsConfigurationPopulatorFactory = new GDSConfigurationPopulatorFactory();
					GDSConfigurationPopulator configurationPopulator = gdsConfigurationPopulatorFactory.getConfigurationPopulator(gdsConfiguratorType);

					Map<String, Map<String, ConfigurationParam>> configurationParams = configurationPopulator.populateConfigurationData(currConfigurationState);

					if (configurationParams.isEmpty()) {
						System.out.println(GDSUserMessages.NO_CONFIGURATION_CHANGES_DETECTED_MESSAGE);
					}
					else {

						GDSConfigurator configurator = gdsConfiguratorFactory.getConfigurator(gdsConfiguratorType);
						configurator.setConfigurationState(currConfigurationState);
						configurator.configure(configurationParams);

						System.out.println(GDSUserMessages.APPLY_CONFIRMATION_MESSAGE);

						if (GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput())) {
							configurator.apply();
						}

						System.out.println(GDSUserMessages.RESET_CONFIRMATION_MESSAGE);

						if (GDSUserInputHelper.isConfirmed(gdsInputHandler.getInput())) {
							configurator.reset();
						}
					}

					break;
				case GDSMenuOptions.ENRICHMENT_APPLY_ALL_CHANGES_OPTION:
					applyAllDirtyConfigurators();
					System.out.println(GDSUserMessages.APPLY_ALL_ENDED_SECCESSFULLY_MESSAGE);
					break;
				case GDSMenuOptions.ENRICHMENT_RESET_ALL_CHANGES_OPTION:
					resetConfigurators(currConfigurationState);
					System.out.println(GDSUserMessages.SUCCESSFULLY_RESET_MESSAGE);
					break;
				case GDSMenuOptions.ENRICHMENT_EXIT_TO_MAIN_MENU_OPTION:
					System.exit(0);
					break;
				default: // illegal operation
					inputErrorMessage = GDSUserMessages.ILLEGAL_OPERATION_MESSAGE;
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
