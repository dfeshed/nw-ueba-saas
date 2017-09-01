package fortscale.common.general;


public enum Schema {

    FILE("file"), ACTIVE_DIRECTORY("active_directory"), AUTHENTICATION("authentication");

    private String name;

    Schema(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Schema createSchema(String schemaName) throws IllegalArgumentException {
        return Schema.valueOf(schemaName.toUpperCase().replace(" ", "_"));
    }
}
