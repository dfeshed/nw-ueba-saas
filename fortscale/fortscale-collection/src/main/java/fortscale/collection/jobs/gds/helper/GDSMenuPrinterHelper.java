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

        System.out.println("1.\tSchema definition (HDFS/Impala)\n" +
                "2.\tEnrichment definition\n" +
                "3.\tApply all changes\n" +
                "4.\tReset all changes\n" +
                "5.\tQuit\n");
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

        System.out.println("1.\tUser Normalization task\n" +
                "2.\tIP Resolving task\n" +
                "3.\tComputer Tagging task\n" +
                "4.\tGeo Location task\n" +
                "5.\tUser Mongo Update task\n" +
                "6.\tHDFS Writer task\n");
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
