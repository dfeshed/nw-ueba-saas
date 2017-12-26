package fortscale.common.general;


import com.google.common.base.CaseFormat;

import java.util.ArrayList;
import java.util.List;

public enum Schema {

    DLPFILE("dlpfile"), DLPMAIL("dlpmail"), PRNLOG("prnlog"), FILE("file"), ACTIVE_DIRECTORY("active_directory"), AUTHENTICATION("authentication");

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

    public static List<Schema> createListOfSchema() {
        List<Schema> schemaList = new ArrayList<>();
        schemaList.add(FILE);
        schemaList.add(ACTIVE_DIRECTORY);
        schemaList.add(AUTHENTICATION);
        return schemaList;
    }

    public String toCamelCase() {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name);
    }
}
