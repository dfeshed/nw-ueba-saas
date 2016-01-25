package fortscale.services.configuration;

/**
 * Enum of logical base entity types
 *
 * @author gils
 * 30/12/2015
 */
public enum EntityType {
    BASE("base"),
    ACCESS_EVENT("access_event"),
    AUTH_EVENT("auth_event"),
    CUSTOMIZED_AUTH_EVENT("customized_auth_event");


    private String entityName;

    private EntityType(String name) {
        this.entityName = name;
    }

    public String getEntityName() {
        return entityName;
    }
}
