package fortscale.common.general;


import org.apache.commons.lang3.builder.ToStringBuilder;

public enum Schema {

    DLPFILE("dlpfile"), DLPMAIL("dlpmail"), PRNLOG("prnlog"), FILE("file"), ACTIVE_DIRECTORY("active_directory"),
    AUTHENTICATION("authentication");

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

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", getName())
                .toString();
    }
}
