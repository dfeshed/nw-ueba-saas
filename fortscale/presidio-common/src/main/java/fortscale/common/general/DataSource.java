package fortscale.common.general;


public enum DataSource {

    DLPFILE("dlpfile"), DLPMAIL("dlpmail"), PRNLOG("prnlog");

    private String name;

    DataSource(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static DataSource createDataSource(String dataSourceName) throws IllegalArgumentException {
        return DataSource.valueOf(dataSourceName.toUpperCase());
    }
}
