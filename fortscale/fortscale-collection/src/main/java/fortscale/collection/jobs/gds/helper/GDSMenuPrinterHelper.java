package fortscale.collection.jobs.gds.helper;

import fortscale.collection.jobs.gds.GDSConfigurationType;
import fortscale.collection.jobs.gds.configurators.GDSConfigurationResult;
import fortscale.utils.ConversionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Helper class for Generic data source menu printing
 *
 * @author gils
 * 30/12/2015
 */
public class GDSMenuPrinterHelper {

    private static final String END_LINE = "\n";

    public static Map<String, GDSConfigurationType> createMainMenuOptionToConfigurationType() {
        Map<String, GDSConfigurationType> mainMenuOptionToConfigurationType = new HashMap<>();

        mainMenuOptionToConfigurationType.put(GDSMenuOptions.MAIN_MENU_SCHEMA_DEFINITION_OPTION, GDSConfigurationType.SCHEMA);
        mainMenuOptionToConfigurationType.put(GDSMenuOptions.MAIN_MENU_ENTITIES_PROPERTIES_OPTION, GDSConfigurationType.ENTITIES_PROPERTIES);

        return mainMenuOptionToConfigurationType;
    }

    public static Map<String, GDSConfigurationType> createEnrichmentMenuOptionToConfigurationType() {

        Map<String, GDSConfigurationType> enrichmentMenuOptionToConfigurationType = new HashMap<>();

        enrichmentMenuOptionToConfigurationType.put(GDSMenuOptions.ENRICHMENT_MENU_USER_NORMALIZATION_OPTION, GDSConfigurationType.USER_NORMALIZATION);
        enrichmentMenuOptionToConfigurationType.put(GDSMenuOptions.ENRICHMENT_IP_RESOLVING_OPTION, GDSConfigurationType.IP_RESOLVING);
        enrichmentMenuOptionToConfigurationType.put(GDSMenuOptions.ENRICHMENT_COMPUTER_TAGGING_OPTION, GDSConfigurationType.COMPUTER_TAGGING);
        enrichmentMenuOptionToConfigurationType.put(GDSMenuOptions.ENRICHMENT_GEO_LOCATION_OPTION, GDSConfigurationType.GEO_LOCATION);
        enrichmentMenuOptionToConfigurationType.put(GDSMenuOptions.ENRICHMENT_MENU_USER_MONGO_UPDATE_OPTION, GDSConfigurationType.USER_MONGO_UPDATE);
        enrichmentMenuOptionToConfigurationType.put(GDSMenuOptions.ENRICHMENT_HDFS_WRITER_OPTION, GDSConfigurationType.HDFS_WRITER);


        return enrichmentMenuOptionToConfigurationType;
    }

    public static void printMainMenu(boolean includeMainMenuHeader) {
        if (includeMainMenuHeader) {
            printMainMenuHeader();
        }

        System.out.println("");

        System.out.println(GDSMenuOptions.MAIN_MENU_SCHEMA_DEFINITION_OPTION + ".\tSchema definition (HDFS/Impala)\n" +
                GDSMenuOptions.MAIN_MENU_ENRICHMENT_DEFINITION_OPTION + ".\tEnrichment definition\n" +
                GDSMenuOptions.MAIN_MENU_MODEL_AND_SCORING_DEFINITION_OPTION + ".\tModel&Scoring definition\n" +
                GDSMenuOptions.MAIN_MENU_ENTITIES_PROPERTIES_OPTION + ".\tEntities properties definition\n" +
                GDSMenuOptions.MAIN_MENU_APPLY_ALL_CHANGES_OPTION + ".\tApply all changes\n" +
                GDSMenuOptions.MAIN_MENU_RESET_ALL_CHANGES_OPTION + ".\tReset all changes\n" +
                GDSMenuOptions.MAIN_MENU_RESTORE_DEFAULTS_OPTION + ".\tRestore Defaults\n" +
                GDSMenuOptions.MAIN_MENU_QUIT_OPTION + ".\tQuit\n");
    }

    private static void printMainMenuHeader() {
        System.out.println("Generic Data Source Tool");
        System.out.println("========================");
    }

    public static void printMainMenuAfterFailure(String message) {
        System.out.println(message);
        GDSMenuPrinterHelper.printMainMenu(false);
        System.out.println(GDSUserMessages.USER_INPUT_REQUEST_MESSAGE);
    }

    public static void printNextMainMenu() {
        System.out.println("");
        GDSMenuPrinterHelper.printMainMenu(false);
        System.out.println(GDSUserMessages.USER_INPUT_REQUEST_MESSAGE);
    }

    public static void printEnrichmentMenu() {
        System.out.println("");
        System.out.println("Enrichment Tasks Settings");
        System.out.println("-------------------------");

        System.out.println(GDSMenuOptions.ENRICHMENT_MENU_USER_NORMALIZATION_OPTION + ".\tUser Normalization task\n" +
                GDSMenuOptions.ENRICHMENT_IP_RESOLVING_OPTION + ".\tIP Resolving task\n" +
                GDSMenuOptions.ENRICHMENT_COMPUTER_TAGGING_OPTION + ".\tComputer Tagging task\n" +
                GDSMenuOptions.ENRICHMENT_GEO_LOCATION_OPTION + ".\tGeo Location task\n" +
                GDSMenuOptions.ENRICHMENT_MENU_USER_MONGO_UPDATE_OPTION + ".\tUser Mongo Update task\n" +
                GDSMenuOptions.ENRICHMENT_HDFS_WRITER_OPTION + ".\tHDFS Writer task\n" +
                GDSMenuOptions.ENRICHMENT_APPLY_ALL_CHANGES_OPTION + ".\tApply all Enrichment changes\n" +
                GDSMenuOptions.ENRICHMENT_RESET_ALL_CHANGES_OPTION + ".\tReset all Enrichment changes\n" +
                GDSMenuOptions.ENRICHMENT_EXIT_TO_MAIN_MENU_OPTION + ".\tReturn to Main menu\n");
    }

    public static void printModelAndScoringMenu() {
        System.out.println("");
        System.out.println("Model & Scoring Settings");
        System.out.println("------------------------");
    }

    public static void printEnrichmentMenuAfterFailure(String message) {
        GDSMenuPrinterHelper.printEnrichmentMenu();
        System.out.println(message);
        System.out.println(GDSUserMessages.USER_INPUT_REQUEST_MESSAGE);
    }

    public static void printNextEnrichmentMenu() {
        System.out.println("");
        GDSMenuPrinterHelper.printEnrichmentMenu();
        System.out.println(GDSUserMessages.USER_INPUT_REQUEST_MESSAGE);
    }

    public static String formatCSVInMultiLines(String csvText, int numOfValuesInLine) {
        StringBuilder result = new StringBuilder();
        Set<String> csvValues = ConversionUtils.convertCSVToSet(csvText, true);

        int numberOfCSVValues = csvValues.size();

        String[] csvValuesArr = csvValues.toArray(new String[numberOfCSVValues]);

        int printedValuesCounter = 0;
        while (printedValuesCounter < numberOfCSVValues) {
            StringBuilder line = new StringBuilder();
            int startIndex = printedValuesCounter;
            int endIndex = printedValuesCounter + numOfValuesInLine;
            for (int i = startIndex; i <  endIndex && printedValuesCounter < numberOfCSVValues; ++i){
                line.append(csvValuesArr[i].trim()).append(", ");
                printedValuesCounter++;
            }

            // remove the comma+space combination at the end of each line
            line.delete(line.length() - 2, line.length());

            result.append(line).append(END_LINE);
        }

        result.deleteCharAt(result.length() - 1);

        return result.toString();
    }

    public static void printConfigurationResult(GDSConfigurationResult<String> configurationResult, String configuratorName) {
        boolean success = configurationResult.isSuccess();

        if (success) {
            Set<String> affectedFiles = configurationResult.getAffectedConfigDescriptors();

            System.out.println("Apply " + configuratorName + " configuration succeeded. Affected files:");
            affectedFiles.stream().forEach(System.out::println);
        }
        else {
            System.out.println("Apply configuration failed.");
        }
    }
}
