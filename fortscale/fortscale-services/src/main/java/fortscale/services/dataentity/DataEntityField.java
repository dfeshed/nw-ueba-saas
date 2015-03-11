package fortscale.services.dataentity;

import java.util.List;

/**
* Created by Yossi on 10/11/2014.
*/
public class DataEntityField implements Comparable<DataEntityField> {
    private String id;
    private String name;
    private String scoreField;
    private Boolean isDefaultEnabled = true;
    private QueryValueType type;

    /**
     * A number to be used for sorting fields in an entity, relevant to how the front-end displays fields if the order isn't explicitly specified in the front-end.
     */
    private int rank = 100;

    /**
     * A logicalOnly field is one that has no matching physical representation, but is instead a function, case/if, etc.
     */
    private Boolean logicalOnly = false;

    /**
     * Flag that indicates that this field can be searched by the front-end, to be used by autocomplete or other such components.
     */
    private Boolean searchable = false;

    /**
     * Attributes are strings that are used by the front-end, for example in render conditions or to decide whether to use a menu
     */
    private List<String> attributes;

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

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    @Override
    public int compareTo(DataEntityField o) {
        return this.rank - o.rank;
    }
}
