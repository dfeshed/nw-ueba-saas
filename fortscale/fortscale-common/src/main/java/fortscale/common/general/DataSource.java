package fortscale.common.general;


public enum DataSource {

    DLPFILE, DLPMAIL, PRNLOG;

    public static DataSource createDataSource(String dataSourceName) throws Exception {
        return DataSource.valueOf(dataSourceName.toUpperCase());
    }


}
