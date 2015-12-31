package fortscale.collection.jobs.gds;

/**
 * @author gils
 * 30/12/2015
 */
public class GDSMenuPrinterHelper {
    public static void printMainMenu(boolean includeMainMenuHeader) {
        if (includeMainMenuHeader) {
            printMainMenuHeader();
        }

        System.out.println("1.\tSchema definition (HDFS/Impala)\n" +
                "2.\tCollection definition\n" +
                "3.\tEnrichment definition\n" +
                "4.\tApply all changes\n" +
                "5.\tReset all changes\n" +
                "6.\tQuit\n");
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

    public static void printMainMenuAfterFailure() {
        GDSMenuPrinterHelper.printMainMenu(false);
        System.out.println("Illegal input. Please enter your choice [1-6]:");
    }

    public static void printNewMainMenuIteration() {
        System.out.println("");
        GDSMenuPrinterHelper.printMainMenu(false);
        System.out.println("Please enter your choice [1-6]:");
    }
}
