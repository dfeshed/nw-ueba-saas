package presidio.collector;


import java.util.Arrays;

public enum Datasource {

    DLPFILE, DLPMAIL, PRNLOG;

    public static Datasource createDataSource(String dataSourceName) throws Exception {
        switch (dataSourceName.toLowerCase()) {
            case ("dlpfile"):
                return DLPFILE;
            case ("dlpmail"):
                return DLPMAIL;
            case ("prnlog"):
                return PRNLOG;
            default:
                throw new Exception(String.format("invalid datasource name %s. Supported datasources are: %s", dataSourceName, Arrays.toString(Datasource.values())));
        }
    }


}
