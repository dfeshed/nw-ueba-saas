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

    /**
     * A logicalOnly field is one that has no matching physical representation, but is instead a function, case/if, etc.
     */
    private Boolean logicalOnly = false;

    /**
     * Flag that indicates that this field can be searched by the front-end, to be used by autocomplete or other such components.
     */
    private Boolean searchable = false;

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

    public Boolean isSearchable() {
        return searchable;
    }

    public void setSearchable(Boolean searchable) {
        this.searchable = searchable;
    }
}
