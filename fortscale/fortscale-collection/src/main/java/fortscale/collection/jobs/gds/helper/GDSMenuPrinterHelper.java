package fortscale.collection.jobs.gds.helper;

import fortscale.collection.jobs.gds.GDSConfigurationType;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for Generic data source menu printing
 *
 * @author gils
 * 30/12/2015
 */
public class GDSMenuPrinterHelper {

    public static Map<String, GDSConfigurationType> createMainMenuOptionToConfigurationType() {
        Map<String, GDSConfigurationType> mainMenuOptionToConfigurationType = new HashMap<>();

        mainMenuOptionToConfigurationType.put(GDSMenuOptions.MAIN_MENU_SCHEMA_DEFINITION_OPTION, GDSConfigurationType.SCHEMA);

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
                GDSMenuOptions.MAIN_MENU_APPLY_ALL_CHANGES_OPTION + ".\tApply all changes\n" +
                GDSMenuOptions.MAIN_MENU_RESET_ALL_CHANGES_OPTION + ".\tReset all changes\n" +
                GDSMenuOptions.MAIN_MENU_QUIT_OPTION + ".\tQuit\n");
    }

    private static void printMainMenuHeader() {
        System.out.println("Generic Data Source Tool");
        System.out.println("========================");
    }

    public static void printDataSourceTypeMenuOptions(String dataSourceName) {
        System.out.println(String.format("What is the %s data source type (base/access_event/auth_event/customized_auth_event): ", dataSourceName));
        System.out.println("* - meaning mandatory field ? -meaning optional field: ");
        System.out.println("         base                    - user* , time*  ");
        System.out.println("         access_event            - user* , time*, source? (resolving,geo location)?  ");
        System.out.println("         auth_event              - user* , time*, source? (resolving,geo location)? , target? (resolving,geo location)?  ");
        System.out.println("         customized_auth_event   - user* , time*, source? (resolving,geo location)? , target? (resolving,geo location)?, action? , data usage? ");
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
        System.out.println("----------------");

        System.out.println(GDSMenuOptions.ENRICHMENT_MENU_USER_NORMALIZATION_OPTION + ".\tUser Normalization task\n" +
                GDSMenuOptions.ENRICHMENT_IP_RESOLVING_OPTION + ".\tIP Resolving task\n" +
                GDSMenuOptions.ENRICHMENT_COMPUTER_TAGGING_OPTION + ".\tComputer Tagging task\n" +
                GDSMenuOptions.ENRICHMENT_GEO_LOCATION_OPTION + ".\tGeo Location task\n" +
                GDSMenuOptions.ENRICHMENT_MENU_USER_MONGO_UPDATE_OPTION + ".\tUser Mongo Update task\n" +
                GDSMenuOptions.ENRICHMENT_HDFS_WRITER_OPTION + ".\tHDFS Writer task\n" +
                GDSMenuOptions.ENRICHMENT_APPLY_ALL_CHANGES_OPTION + ".\tApply all changes\n" +
                GDSMenuOptions.ENRICHMENT_RESET_ALL_CHANGES_OPTION + ".\tReset all changes\n" +
                GDSMenuOptions.ENRICHMENT_EXIT_TO_MAIN_MENU_OPTION + ".\tExit to Main menu\n");
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
}
