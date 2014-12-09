package fortscale.services.dataentity;

/**
* Created by Yossi on 10/11/2014.
*/
public class DataEntityField {
    private String id;
    private String name;
    private String scoreField;
    private Boolean isDefaultEnabled = true;
    private QueryValueType type;
    private Boolean logicalOnly = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScoreField() {
        return scoreField;
    }

    public void setScoreField(String scoreField) {
        this.scoreField = scoreField;
    }

    public Boolean getIsDefaultEnabled() {
        return isDefaultEnabled;
    }

    public void setIsDefaultEnabled(Boolean isDefaultEnabled) {
        this.isDefaultEnabled = isDefaultEnabled;
    }

    public QueryValueType getType() {
        return type;
    }

    public void setType(QueryValueType type) {
        this.type = type;
    }

    public Boolean isLogicalOnly() {
        return logicalOnly;
    }

    public void setLogicalOnly(Boolean logicalOnly) {
        this.logicalOnly = logicalOnly;
    }
}
