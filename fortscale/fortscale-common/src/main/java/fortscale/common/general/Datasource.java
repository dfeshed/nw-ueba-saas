package fortscale.common.general;


public enum Datasource {

    DLPFILE, DLPMAIL, PRNLOG;

    public static Datasource createDataSource(String dataSourceName) throws Exception {
        return Datasource.valueOf(dataSourceName.toUpperCase());
    }


}
