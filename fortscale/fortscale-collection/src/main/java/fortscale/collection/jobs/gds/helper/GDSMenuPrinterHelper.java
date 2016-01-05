package fortscale.collection.jobs.gds.helper;

/**
 * Helper class for Generic data source menu printing
 *
 * @author gils
 * 30/12/2015
 */
public class GDSMenuPrinterHelper {
    public static void printMainMenu(boolean includeMainMenuHeader) {
        if (includeMainMenuHeader) {
            printMainMenuHeader();
        }

        System.out.println("");

        System.out.println(GDSMenuOptionsConsts.MAIN_MENU_SCHEMA_DEFINITION_OPTION + ".\tSchema definition (HDFS/Impala)\n" +
                GDSMenuOptionsConsts.MAIN_MENU_ENRICHMENT_DEFINITION_OPTION + ".\tEnrichment definition\n" +
                GDSMenuOptionsConsts.MAIN_MENU_APPLY_ALL_CHANGES_OPTION + ".\tApply all changes\n" +
                GDSMenuOptionsConsts.MAIN_MENU_RESET_ALL_CHANGES_OPTION + ".\tReset all changes\n" +
                GDSMenuOptionsConsts.MAIN_MENU_QUIT_OPTION + ".\tQuit\n");
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
        GDSMenuPrinterHelper.printMainMenu(false);
        System.out.println(message);
        System.out.println("Please enter your choice [1-6]:");
    }

    public static void printNextMainMenu() {
        System.out.println("");
        GDSMenuPrinterHelper.printMainMenu(false);
        System.out.println("Please enter your choice [1-6]:");
    }

    public static void printEnrichmentMenu() {
        System.out.println("");
        System.out.println("Enrichment Tasks Settings");
        System.out.println("----------------");

        System.out.println(GDSMenuOptionsConsts.ENRICHMENT_MENU_USER_NORMALIZATION_OPTION + ".\tUser Normalization task\n" +
                GDSMenuOptionsConsts.ENRICHMENT_IP_RESOLVING_OPTION + ".\tIP Resolving task\n" +
                GDSMenuOptionsConsts.ENRICHMENT_COMPUTER_TAGGING_OPTION + ".\tComputer Tagging task\n" +
                GDSMenuOptionsConsts.ENRICHMENT_GEO_LOCATION_OPTION + ".\tGeo Location task\n" +
                GDSMenuOptionsConsts.ENRICHMENT_MENU_USER_MONGO_UPDATE_OPTION + ".\tUser Mongo Update task\n" +
                GDSMenuOptionsConsts.ENRICHMENT_HDFS_WRITER_OPTION + ".\tHDFS Writer task\n" +
                GDSMenuOptionsConsts.ENRICHMENT_APPLY_ALL_CHANGES_OPTION + ".\tApply all changes\n" +
                GDSMenuOptionsConsts.ENRICHMENT_RESET_ALL_CHANGES_OPTION + ".\tReset all changes\n" +
                GDSMenuOptionsConsts.ENRICHMENT_EXIT_TO_MAIN_MENU_OPTION + ".\tExit to Main menu\n");
    }

    public static void printEnrichmentMenuAfterFailure(String message) {
        GDSMenuPrinterHelper.printEnrichmentMenu();
        System.out.println(message);
        System.out.println("Please enter your choice [1-6]:");
    }

    public static void printNextEnrichmentMenu() {
        System.out.println("");
        GDSMenuPrinterHelper.printEnrichmentMenu();
        System.out.println("Please enter your choice [1-6]:");
    }
}
