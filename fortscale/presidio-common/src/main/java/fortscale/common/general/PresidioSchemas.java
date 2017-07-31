package fortscale.common.general;


public enum PresidioSchemas {

    DLPFILE("dlpfile"), DLPMAIL("dlpmail"), PRNLOG("prnlog"), FILE("file"), ACTIVE_DIRECTORY("active_directory"),
    AUTHENTICATION("authentication");

    private String name;

    PresidioSchemas(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static PresidioSchemas createDataSource(String dataSourceName) throws IllegalArgumentException {
        return PresidioSchemas.valueOf(dataSourceName.toUpperCase());
    }
}
