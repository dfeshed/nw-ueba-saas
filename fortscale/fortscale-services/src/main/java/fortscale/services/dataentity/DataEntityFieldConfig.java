package fortscale.services.dataentity;

/**
 * Created by Yossi on 10/11/2014.
 */
public class DataEntityFieldConfig {
    private String name;
    private String column;
    private String score;
    private QueryValueType type;
    private Boolean isLogicalOnly;

    public Boolean getDefaultEnabled() {
        return defaultEnabled;
    }

    public void setDefaultEnabled(Boolean defaultEnabled) {
        this.defaultEnabled = defaultEnabled;
    }

    private Boolean defaultEnabled;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public QueryValueType getType() {
        return type;
    }

    public void setType(QueryValueType type) {
        this.type = type;
    }

    public Boolean getIsLogicalOnly() {
        return isLogicalOnly;
    }

    public void setIsLogicalOnly(Boolean isLogicalOnly) {
        this.isLogicalOnly = isLogicalOnly;
    }
}
