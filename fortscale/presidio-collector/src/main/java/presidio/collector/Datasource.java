package presidio.collector;


public enum Datasource {

    DLPFILE, DLPMAIL, PRNLOG;

    public static Datasource createDataSource(String dataSourceName) throws Exception {
        return Datasource.valueOf(dataSourceName.toUpperCase());
    }


}
