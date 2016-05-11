package fortscale.collection.jobs.gds;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.collection.jobs.gds.configurators.GDSConfigurationResult;
import fortscale.collection.jobs.gds.configurators.GDSConfigurator;
import fortscale.collection.jobs.gds.configurators.GDSConfiguratorFactory;
import fortscale.collection.jobs.gds.helper.GDSMenuOptions;
import fortscale.collection.jobs.gds.helper.GDSMenuPrinterHelper;
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

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * This is the main entry point of the GDS configuration job.
 * The configuration job receives GDS (Generic Data Source) requirements and generates the appropriate configurations
 * in the various config files.
 * The job currently supports only command line interface.
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

	// used to restore the default settings
	private Queue<String> modifiedConfigurationFiles = new LinkedList<>();

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

						if (gdsInputHandler.getYesNoInput()) {
							GDSConfigurationResult<String> configurationResult = configurator.apply();

							modifiedConfigurationFiles.addAll(configurationResult.getAffectedConfigDescriptors());

							GDSMenuPrinterHelper.printConfigurationResult(configurationResult, configurator.getType().getLabel());

							break;
						}

						System.out.println(GDSUserMessages.RESET_CONFIRMATION_MESSAGE);

						if (gdsInputHandler.getYesNoInput()) {
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
					if (canEnterModelAndScoringStep(currConfigurationState)) {
						GDSMenuPrinterHelper.printModelAndScoringMenu();
						handleModelAndScoringConfiguration();
					}
					else {
						inputErrorMessage = GDSUserMessages.SCHEMA_IS_MANDATORY_MESSAGE;
					}
					break;
			case GDSMenuOptions.MAIN_MENU_ENTITIES_PROPERTIES_OPTION:
				if(canEnterEntitiesPropertiesStep(currConfigurationState)){
					handleEntitiesPropertiesConfiguration();
				}
				else {
					inputErrorMessage = GDSUserMessages.SCHEMA_IS_MANDATORY_MESSAGE;
				}
				break;

				case GDSMenuOptions.MAIN_MENU_APPLY_ALL_CHANGES_OPTION:
					if (!dirtyConfiguratorsQueue.isEmpty()) {
						System.out.println(GDSUserMessages.APPLY_IN_PROGRESS_MESSAGE);
						applyDirtyConfigurators();
						System.out.println(GDSUserMessages.APPLY_COMPLETED_SUCCESSFULLY_MESSAGE);
					}
					else {
						System.out.println(GDSUserMessages.NOTHING_TO_APPLY_MESSAGE);
					}

					break;
				case GDSMenuOptions.MAIN_MENU_RESET_ALL_CHANGES_OPTION:
					if (!dirtyConfiguratorsQueue.isEmpty()) {
						System.out.println(GDSUserMessages.RESET_IN_PROGRESS_MESSAGE);
						resetConfigurators();
						System.out.println(GDSUserMessages.RESET_COMPLETED_SUCCESSFULLY_MESSAGE);
					}
					else {
						System.out.println(GDSUserMessages.IGNORING_RESET_OPERATION_MESSAGE);
					}
					break;
				case GDSMenuOptions.MAIN_MENU_RESTORE_DEFAULTS_OPTION:
					System.out.println(GDSUserMessages.RESTORE_IN_PROGRESS_MESSAGE);
					restoreDefaults();
					System.out.println(GDSUserMessages.RESTORE_COMPLETED_SUCCESSFULLY_MESSAGE);
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

	private void restoreDefaults() {
		// TODO go through modifiedConfigurationFiles and override with the default
		modifiedConfigurationFiles.stream().forEach(modifiedFile -> System.out.print("Restoring " + modifiedFile));
		dirtyConfiguratorsQueue.clear();
		currConfigurationState.reset();
		modifiedConfigurationFiles.clear();
	}

	private boolean canEnterEnrichmentStep(GDSCompositeConfigurationState currConfigurationState) {
		return currConfigurationState.isDataSourceAlreadyDefined();
	}

	private boolean canEnterModelAndScoringStep(GDSCompositeConfigurationState currConfigurationState) {
		return currConfigurationState.isDataSourceAlreadyDefined();
	}

	private boolean canEnterEntitiesPropertiesStep(GDSCompositeConfigurationState currConfigurationState) {
		return currConfigurationState.isDataSourceAlreadyDefined();
	}

	private void resetConfigurators() {
		currConfigurationState.reset();
		dirtyConfiguratorsQueue.clear();
	}

	private void resetEnrichmentConfigurators(Collection<GDSConfigurationType> configurationTypesToReset) {
		currConfigurationState.getEnrichmentDefinitionState().reset();
		dirtyConfiguratorsQueue.removeIf(isInConfigurationTypes(configurationTypesToReset));
	}

	private Predicate<GDSConfigurator> isInConfigurationTypes(Collection<GDSConfigurationType> configuratorTypes) {
		return configurator -> configuratorTypes.contains(configurator.getType());
	}

	private void applyDirtyConfigurators() throws Exception {
		applyDirtyConfigurators(Arrays.asList(GDSConfigurationType.values()));
	}

	private void applyDirtyConfigurators(Collection<GDSConfigurationType> configurationTypesToApply) throws Exception {
		Predicate<GDSConfigurator> configurationTypesPredicate = isInConfigurationTypes(configurationTypesToApply);

		List<GDSConfigurator> dirtyConfiguratorsToApply = dirtyConfiguratorsQueue.stream().filter(configurationTypesPredicate).collect(Collectors.toList());

		for (GDSConfigurator dirtyConfigurator : dirtyConfiguratorsToApply) {
			GDSConfigurationResult<String> configurationResult = dirtyConfigurator.apply();

			modifiedConfigurationFiles.addAll(configurationResult.getAffectedConfigDescriptors());
			GDSMenuPrinterHelper.printConfigurationResult(configurationResult, dirtyConfigurator.getType().getLabel());
		}

		dirtyConfiguratorsQueue.removeIf(configurationTypesPredicate);
	}

	private void handleModelAndScoringConfiguration() throws Exception {
		System.out.println(GDSUserMessages.USER_INPUT_REQUEST_MESSAGE);
		String stepInput = gdsInputHandler.getInput();
		String stepInputNormalized = stepInput.trim();

		switch (stepInputNormalized) {
			default: {
				throw new GDSConfigurationException("Operation not supported");
			}
		}
	}

	private void handleEntitiesPropertiesConfiguration() throws Exception {
		handleConfiguration(mainMenuOptionToConfigurationType, GDSMenuOptions.MAIN_MENU_ENTITIES_PROPERTIES_OPTION);
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

					handleConfiguration(enrichmentMenuOptionToConfigurationType,stepInputNormalized);

					break;
				case GDSMenuOptions.ENRICHMENT_APPLY_ALL_CHANGES_OPTION:
					applyDirtyConfigurators(enrichmentMenuOptionToConfigurationType.values());
					System.out.println(GDSUserMessages.APPLY_ALL_ENDED_SECCESSFULLY_MESSAGE);
					break;
				case GDSMenuOptions.ENRICHMENT_RESET_ALL_CHANGES_OPTION:
					resetEnrichmentConfigurators(enrichmentMenuOptionToConfigurationType.values());
					System.out.println(GDSUserMessages.SUCCESSFULLY_RESET_MESSAGE);
					break;
				case GDSMenuOptions.ENRICHMENT_EXIT_TO_MAIN_MENU_OPTION:
					return;
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

	/**
	 * handle configuration: gets the relevant populator, populate, save the output to the state and asks the user if she wants to apply or reset.
	 * @param stepInputNormalized used to determine the populator/configurator from the configTypeMap
	 * @param configTypeMap maps between user input and configuration
	 * @throws Exception
	 */
	private void handleConfiguration(Map<String, GDSConfigurationType> configTypeMap ,String stepInputNormalized) throws Exception {
		GDSConfigurationType gdsConfiguratorType = configTypeMap.get(stepInputNormalized);

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

			if (gdsInputHandler.getYesNoInput()) {
				GDSConfigurationResult<String> configurationResult = configurator.apply();

				modifiedConfigurationFiles.addAll(configurationResult.getAffectedConfigDescriptors());

				GDSMenuPrinterHelper.printConfigurationResult(configurationResult, configurator.getType().getLabel());

			}

			System.out.println(GDSUserMessages.RESET_CONFIRMATION_MESSAGE);

			if (gdsInputHandler.getYesNoInput()) {
				configurator.reset();
			}
			else {
				dirtyConfiguratorsQueue.add(configurator);
			}
		}
	}

	@Override
	protected int getTotalNumOfSteps() { return 1; }

	@Override
	protected boolean shouldReportDataReceived() { return false; }
}
