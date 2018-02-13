package fortscale.common.general;

import com.google.common.base.CaseFormat;

public enum Schema {
    // The order is important for user update!!!!
    // We first want to look for user data in the authentication events,
    // then file and so on...
    AUTHENTICATION("authentication"),
    FILE("file"),
    PRINT("print"),
    ACTIVE_DIRECTORY("active_directory"),
    // Dlp file is used only in the ade tests
    DLPFILE("dlpfile");

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

    public String toCamelCase() {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name);
    }
}
