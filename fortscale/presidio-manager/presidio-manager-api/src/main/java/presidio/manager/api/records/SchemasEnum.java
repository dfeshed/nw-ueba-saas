package presidio.manager.api.records;


public enum SchemasEnum {
    FILE("file"),

    ACTIVE_DIRECTORY("active directory"),

    AUTHENTICATION("authentication");

    private String value;

    SchemasEnum(String value) {
        this.value = value;
    }

    public String toString() {
        return String.valueOf(value);
    }

    public static SchemasEnum fromValue(String text) {
        for (SchemasEnum b : SchemasEnum.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }

    public static SchemasEnum[] fromValue(String[] listOfSchemas) {
        SchemasEnum[] schemas = new SchemasEnum[listOfSchemas.length];
        for (int i = 0; i < listOfSchemas.length; i++) {
            schemas[i] = fromValue(listOfSchemas[i]);
        }
        return schemas;
    }
}